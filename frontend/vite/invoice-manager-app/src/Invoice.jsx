import {useEffect, useState} from "react";
import {errorNotification} from "./services/notification.jsx";
import React from "react";
import {
    Badge,
    Box,
    Card,
    CardBody,
    CardHeader,
    Container,
    Divider,
    Flex,
    Heading,
    Popover,
    PopoverArrow,
    PopoverBody,
    PopoverCloseButton,
    PopoverContent,
    PopoverTrigger,
    Spinner,
    Stack,
    StackDivider,
    Text,
    useDisclosure,
    VStack
} from "@chakra-ui/react";
import {Image as ChakraImage} from "@chakra-ui/react";
import {getInvoices} from "./services/client.jsx"
import {useParams} from "react-router-dom";
import {getInvoiceFile} from "./services/client.jsx"
import dayjs from "dayjs";

export const InvoiceCard = ({invoice, setSelectedInvoices}) => {
    const [invoiceFile, setInvoiceFile] = useState(null);
    const {isOpen, onOpen, onClose} = useDisclosure();
    const date = new Date(invoice.uploaded);
    const formattedDate = dayjs(date).format("DD/MM/YYYY");
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (!invoiceFile && !isLoading) {
            setIsLoading(true);
            getInvoiceFile(invoice.id).then(res => {
                const file = new Blob([res.data], {type: 'image/jpeg'});
                const fileURL = URL.createObjectURL(file);
                setInvoiceFile(fileURL);
                setIsLoading(false);
            }).catch(err => {
                errorNotification(
                    err.code,
                    err.response.data.message
                )
                setIsLoading(false);
            })
        }
    }, [invoice, isLoading, invoiceFile]);

    function handleInvoiceSelect(e, invoiceId) {
        if (e.target.checked) {
            setSelectedInvoices(prev => [...prev, invoiceId]);
        } else {
            setSelectedInvoices(prev => prev.filter(id => id !== invoiceId));
        }
    }

    return (
        <>
            <input type="checkbox" onChange={(e) => handleInvoiceSelect(e, invoice.id)}/>
            <Flex direction={"column"} flex={'1'}>
                <Popover isOpen={isOpen} onOpen={onOpen} onClose={onClose}>
                    <PopoverTrigger>
                        <Card
                            key={invoice.id}
                            p={"10px"}
                            mb={"10px"}
                            mt={"10px"}
                            _hover={{bg: "gray.500"}}
                            onClick={onOpen}
                        >
                            <VStack>
                                <CardHeader>
                                    <Heading size='md'>{invoice.description}</Heading>
                                </CardHeader>
                                <Divider/>
                                <CardBody>
                                    <Stack divider={<StackDivider/>} spacing='4'>
                                        <Box>
                                            <Heading textTransform='uppercase'>
                                                {invoice.isPending === false ? (
                                                    <Badge colorScheme='green'>settled</Badge>
                                                ) : (
                                                    <Badge colorScheme='yellow'>pending</Badge>
                                                )}
                                            </Heading>
                                        </Box>
                                    </Stack>
                                    <Box>
                                        <Text>
                                            {formattedDate}
                                        </Text>
                                    </Box>
                                </CardBody>
                            </VStack>
                        </Card>
                    </PopoverTrigger>
                    <PopoverContent>
                        <PopoverArrow/>
                        <PopoverCloseButton/>
                        <PopoverBody>
                            <ChakraImage src={invoiceFile} alt="Invoice"/>
                        </PopoverBody>
                    </PopoverContent>
                </Popover>
            </Flex>
        </>
    );
};


export const Invoice = ({setSelectedInvoices}) => {
    const { companyId } = useParams();
    const [ invoices, setInvoices ] = useState([]);
    const [ loading, setLoading ] = useState(false);

    const fetchInvoices = () => {
        setLoading(true);
        getInvoices(companyId).then(res => {
            setInvoices(res.data)
        }).catch(res => {
            errorNotification(res.data.message)
        }).finally(() => {
            setLoading(false);
        })
    }

    useEffect(() => {
        fetchInvoices();
    }, [])

    if (loading) {
        return (
            <Container mt={"20%"}>
                <VStack>
                    <Spinner
                        thickness='4px'
                        speed='0.65s'
                        emptyColor='gray.200'
                        color='blue.500'
                        size='xl'
                    />
                </VStack>
            </Container>
        )
    }

    if (invoices.length <= 0) {
        return (
            <Container mt={"20%"}>
                <VStack spacing={'5'} mb={'5'}>
                    <Text fontSize={"x-large"}>No invoices yet.</Text>
                </VStack>
            </Container>
        )
    }

    if (invoices) {
        return (
            <>
                {invoices.map(invoice => (
                    <React.Fragment key={invoice.id}>
                        <Flex>
                            <InvoiceCard invoice={invoice} setSelectedInvoices={setSelectedInvoices}
                                         setInvoices={setInvoices}/>
                        </Flex>
                    </React.Fragment>
                ))}
            </>
        )
    }
}