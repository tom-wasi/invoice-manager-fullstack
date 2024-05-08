import {login, register} from './services/client.jsx'
import {useState} from "react";
import {Form, useNavigate} from "react-router-dom";
import {Container, Heading, Input, Text} from "@chakra-ui/react";

export const Login = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    return (
        <Container maxW="4xl">
            <Form>
                <Input></Input>
            </Form>
        </Container>
    )
}