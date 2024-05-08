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
import React, {useState} from "react";
import {modifyCompany as updateCompany} from "../services/client.jsx";
import {errorNotification, successNotification} from "../services/notification.jsx";
import {EditIcon} from "@chakra-ui/icons";

export const ModifyCompanyDrawer = ({companyId, currentCompanyName, currentAccountantEmail, fetchCompanies}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    const [companyName, setCompanyName] = useState(currentCompanyName);
    const [accountantEmail, setAccountantEmail] = useState(currentAccountantEmail);

    function modifyCompany() {
        const companyNameAndAccountantEmail = { companyName, accountantEmail };
        updateCompany(companyId, companyNameAndAccountantEmail).then(res => {
            successNotification('Company updated successfully!');
            fetchCompanies();
        }).catch(err => {
            errorNotification(err.data)
        });
    }


    return (
        <>
            <Button mr={"15px"} onClick={onOpen}>
                <EditIcon/>
            </Button>
            <Drawer
                isOpen={isOpen}
                placement='bottom'
                onClose={onClose}
            >
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader borderBottomWidth='1px'>
                        Modify company
                    </DrawerHeader>

                    <DrawerBody>
                        <Stack spacing='24px'>
                            <Box>
                                <FormLabel htmlFor='text'>Company name:</FormLabel>
                                <Input
                                    id='company-name'
                                    placeholder='Please enter company name'
                                    required={true}
                                    value={companyName}
                                    onChange={e => setCompanyName(e.target.value)}
                                />
                            </Box>

                            <Box>
                                <FormLabel htmlFor='email'>Accountant e-mail address:</FormLabel>
                                <Input
                                    id='accountant-email'
                                    placeholder='Accountant e-mail address here'
                                    value={accountantEmail}
                                    onChange={e => setAccountantEmail(e.target.value)}
                                />
                            </Box>
                        </Stack>
                    </DrawerBody>

                    <DrawerFooter>
                        <Button variant='outline' mr={3} onClick={onClose}>
                            Cancel
                        </Button>
                        <Button onClick={modifyCompany} colorScheme='purple'>Submit</Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}