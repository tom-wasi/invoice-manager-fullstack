import {
    Box,
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    FormLabel,
    Input,
    Stack,
    useDisclosure
} from "@chakra-ui/react";
import React from "react";
import {AddIcon} from "@chakra-ui/icons";
import {createCompany} from "../services/client.jsx"
import {useParams} from "react-router-dom";
import {errorNotification, successNotification} from "../services/notification.jsx";

export const CreateCompanyDrawer = () => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    const firstField = React.useRef()
    const secondField = React.useRef()
    const { userId } = useParams();

    function submitCompany() {
        const companyName = firstField.current.value;
        const accountantEmail = secondField.current.value;

        const companyNameAndAccountantEmail = { companyName, accountantEmail };
        createCompany(userId, companyNameAndAccountantEmail).then(res => {
            successNotification(res.data);
            window.location.reload();
        }).catch(err => {
            errorNotification(err.message)
        });
    }

    return (
        <>
            <Button leftIcon={<AddIcon />} mr={"15px"} colorScheme='purple' onClick={onOpen}>
                Add
            </Button>
            <Drawer
                isOpen={isOpen}
                placement='left'
                initialFocusRef={firstField}
                onClose={onClose}
            >
                <DrawerOverlay />
                <DrawerContent>
                    <DrawerCloseButton />
                    <DrawerHeader borderBottomWidth='1px'>
                        Create a new company
                    </DrawerHeader>

                    <DrawerBody>
                        <Stack spacing='24px'>
                            <Box>
                                <FormLabel htmlFor='text'>Company name:</FormLabel>
                                <Input
                                    ref={firstField}
                                    id='companyName'
                                    placeholder='Enter your company name here'
                                    required={true}
                                />
                            </Box>

                            <Box>
                                <FormLabel htmlFor='email'>Accountant e-mail address:</FormLabel>
                                <Input
                                    ref={secondField}
                                    id='accountantEmail'
                                    placeholder='Accountant e-mail address here'
                                />
                            </Box>
                        </Stack>
                    </DrawerBody>

                    <DrawerFooter borderTopWidth='1px'>
                        <Button variant='outline' mr={3} onClick={onClose}>
                            Cancel
                        </Button>
                        <Button onClick={submitCompany} colorScheme='purple'>Submit</Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}