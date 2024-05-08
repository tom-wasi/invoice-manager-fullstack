import React, {useEffect, useState} from "react";
import {errorNotification, successNotification} from "./services/notification.jsx";
import {
    AccordionIcon,
    Box,
    Button,
    Card,
    CardBody,
    CardHeader,
    Container,
    Divider,
    Flex,
    Heading,
    Spacer,
    Spinner,
    Stack,
    StackDivider,
    Text,
    VStack
} from "@chakra-ui/react";
import {getCompanies} from "./services/client.jsx"
import {NavLink, useParams} from "react-router-dom";
import {deleteCompany} from "./services/client.jsx"
import {DeleteIcon} from "@chakra-ui/icons";
import {ModifyCompanyDrawer} from "./components/ModifyCompanyDrawer.jsx";
import {useAuth} from "./context/AuthContext.jsx"

export const CompanyCard = ({company, setCompanies}) => {

    function handleDeleteCompany() {
        deleteCompany(company.id).then(res => {
            successNotification(res.data);
            window.location.reload();
        }).catch(err => {
            errorNotification(err.response.data);
        })
    }

    return (
        <>
            <Card
                key={company.id}
                p={"10px"}
                mb={"10px"}
                mt={"10px"}
                _hover={{bg: "gray.500"}}
            >
                <NavLink to={`company/${company.id}`}>
                    <VStack>
                        <CardHeader>
                            <Heading size='md'>{company.companyName}</Heading>
                        </CardHeader>
                        <Divider/>
                        {company.accountantEmail.length <= 0 ?
                            <CardBody>
                                <Stack divider={<StackDivider/>} spacing='5'>
                                    <Box>
                                        <Heading size='s' textTransform='uppercase'>
                                            accountant e-mail address:
                                        </Heading>
                                        <Text pt='2' fontSize='sm'>
                                            NONE
                                        </Text>
                                    </Box>
                                </Stack>
                            </CardBody>
                            :
                            <CardBody>
                                <Box>
                                    <Heading size='xs' textTransform='uppercase'>
                                        accountant e-mail address:
                                    </Heading>
                                    <Text pt='2' fontSize='sm'>
                                        {company.accountantEmail}
                                    </Text>
                                </Box>
                            </CardBody>
                        }
                    </VStack>
                </NavLink>
                <Flex mt={"10px"} justifyContent={"center"} gap={"10px"}>
                    <ModifyCompanyDrawer companyId={company.id} currentCompanyName={company.companyName} currentAccountantEmail={company.accountantEmail} setCompanies={setCompanies}/>
                    <Button bg={"red"} onClick={() => handleDeleteCompany(company.id)} >
                        <DeleteIcon/>
                    </Button>
                </Flex>
            </Card>
        </>
    )
}

export const Company = () => {
    const { user } = useAuth();
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");

    const fetchCompanies = () => {
        setLoading(true);
        getCompanies(user.id).then(res => {
            setCompanies(res.data)
        }).catch(err => {
            setError(err.response)
            errorNotification(
                err.code,
                err.response.message
            )
        }).finally(() => {
            setLoading(false);
        })
    }

    useEffect(() => {
        if (user) {
            fetchCompanies();
        }
    }, [user])

    if (loading) {
        return (
            <Container>
                <VStack spacing={'5'} mb={'5'}>
                    <Spinner
                        thickness='8px'
                        speed='0.65s'
                        emptyColor='gray.200'
                        color='purple.500'
                        size='xl'
                    />
                </VStack>
            </Container>
        )
    }

    if (companies.length === 0) {
        return (
            <Container mt={"20%"}>
                <VStack spacing={'5'} mb={'5'}>
                    <Text fontSize={"x-large"}>No companies yet.</Text>
                </VStack>
            </Container>
        )
    }

    if (err) {
        return (
            <Container minW={"100%"}>
                <VStack spacing={'5'} mb={'5'}>
                    <Text>${err}</Text>
                </VStack>
            </Container>
        )
    }

    if (companies) {
        return (
            <>
                {companies.map((company) => (
                    <React.Fragment key={company.id}>
                        <Container justifySelf={"stretch"}>
                            <CompanyCard company={company} setCompanies={setCompanies}/>
                        </Container>
                    </React.Fragment>
                ))}
            </>
        )
    }

}