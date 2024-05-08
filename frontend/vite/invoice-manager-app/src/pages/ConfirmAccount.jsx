import {Box, Button, Container, Text} from "@chakra-ui/react";
import {useParams} from "react-router-dom";
import {confirmAccount as clientConfirm} from "../services/client.jsx";
import {successNotification} from "../services/notification.jsx";

export const ConfirmAccount = () => {
    const { confirmationToken } = useParams();

    const confirm = async () => {
        clientConfirm(confirmationToken).then(res => {
                successNotification(res.data);
            }
        );
    }

    return (
        <Container>
            <Box boxSizing={"border-box"} borderColor={"black"}>
                <Text>
                    To confirm the registration please click the button below.
                </Text>
                <Button colorScheme={"purple"} onClick={confirm}>
                    Confirm
                </Button>
            </Box>
        </Container>
    )
}