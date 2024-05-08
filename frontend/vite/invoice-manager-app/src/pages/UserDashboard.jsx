import {Box, Container, Flex, Spacer} from "@chakra-ui/react";
import {Company} from "../Company.jsx";
import {CreateCompanyDrawer} from "../components/CreateCompanyDrawer.jsx";
import {GoBack} from "../components/GoBack.jsx";
import {useEffect} from "react";

export const UserDashboard = () => {

    return (
        <Container>
            <Flex mt={"10px"}>
                <Spacer/>
                <CreateCompanyDrawer/>
            </Flex>
            <Company/>
        </Container>
    )
}