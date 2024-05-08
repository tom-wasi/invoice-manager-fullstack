import {
    Alert,
    AlertIcon,
    Box,
    Button,
    Flex,
    FormLabel,
    Heading,
    Input,
    Stack,
} from '@chakra-ui/react';
import {Formik, Form, useField} from "formik";
import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification, successNotification} from "../services/notification.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {register as clientRegister} from "../services/client.jsx";

const MyTextInput = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const RegisterForm = () => {
    const { register } = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string()
                        .min(2, "Username must be at least 2 characters long")
                        .required("Username is required"),
                    email: Yup.string()
                        .email("Must be valid email")
                        .required("Email is required"),
                    password: Yup.string()
                        .max(20, "Password cannot be more than 20 characters")
                        .required("Password is required")
                })
            }
            initialValues={{username: '', email: '', password: ''}}
            onSubmit={(values, {setSubmitting}) => {
                setSubmitting(true);
                clientRegister(values).then(res => {
                    successNotification(res.data, "Please click the link provided in the e-mail message")
                }).catch(err => {
                    errorNotification(
                        '',
                        err.response.data
                    )
                }).finally(() => {
                    setSubmitting(false);
                })
            }}>

            {({isValid, isSubmitting}) => (
                <Form>
                    <Stack mt={15} spacing={15}>
                        <MyTextInput
                            label={"Username"}
                            name={"username"}
                            type={"text"}
                            placeholder={"Your username"}
                        />
                        <MyTextInput
                            label={"E-mail"}
                            name={"email"}
                            type={"email"}
                            placeholder={"email@example.com"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"*********"}
                        />
                        <Button
                            type={"submit"}
                            disabled={!isValid || isSubmitting}>
                            Register
                        </Button>
                    </Stack>
                </Form>
            )}

        </Formik>
    )
}
export const Register = () => {

    const { user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            const userId = user.id;
            navigate(`/user/${userId}`);
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Sign up</Heading>
                    <RegisterForm/>
                </Stack>
            </Flex>
        </Stack>
    );
}