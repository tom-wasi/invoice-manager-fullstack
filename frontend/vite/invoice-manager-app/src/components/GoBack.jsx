import {Button, HStack} from "@chakra-ui/react";
import {ArrowBackIcon} from "@chakra-ui/icons";
import {useNavigate} from "react-router-dom";

export const GoBack = () => {
    const navigate = useNavigate();

    function goBack() {
        navigate(-1);
    }

    return (
        <Button color onClick={goBack} ml={"11px"} minW={"10%"}>
            <HStack>
            <ArrowBackIcon/>
            </HStack>
        </Button>
    )
}