import {Button, Container, Flex, Spacer} from "@chakra-ui/react";
import {Invoice} from "../Invoice.jsx";
import {GoBack} from "../components/GoBack.jsx";
import {deleteInvoices, sendInvoices} from "../services/client.jsx";
import {CreateInvoiceDrawer} from "../components/CreateInvoiceDrawer.jsx";
import {useState} from "react";
import {useParams} from "react-router-dom";
import {errorNotification, successNotification} from "../services/notification.jsx";

export const CompanyDashboard = () => {
    const [selectedInvoices, setSelectedInvoices] = useState([]);
    const {companyId} = useParams();

    const handleSendInvoices = async () => {
        if(selectedInvoices.length === 0) {
                    errorNotification("Please mark invoices to send them.");
                    return;
                }
        try {
            await sendInvoices(companyId, selectedInvoices).then(res => {
                successNotification(res.data);
                setSelectedInvoices([])
            })
        } catch (e) {
            throw e;
        }
    }

    const handleDeleteInvoices = async () => {
        if(selectedInvoices.length === 0) {
            errorNotification("Please mark invoices to delete them.");
            return;
        }
        try {
            await deleteInvoices(companyId, selectedInvoices).then(res => {
                successNotification(res.data);
                setSelectedInvoices([]);
                window.location.reload();
            })
        } catch (e) {
            throw e;
        }
    }

    return (
        <Container justifyContent={"stretch"}>
            <Flex mt={"10px"}>
                <GoBack/>
                <Spacer/>
                <Button colorScheme={"green"} onClick={handleSendInvoices}>Send</Button>
                <Spacer/>
                <Button colorScheme={"red"} onClick={handleDeleteInvoices}>Delete</Button>
                <Spacer/>
                <CreateInvoiceDrawer/>
            </Flex>
            <Invoice setSelectedInvoices={setSelectedInvoices}/>
        </Container>
    )
}