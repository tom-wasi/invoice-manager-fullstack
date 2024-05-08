import {Container, Heading, Text, Box, VStack} from "@chakra-ui/react";

export const Home = () => {

    return (
        <>
            <Container maxW={"60%"}>
                <VStack>
                    <Heading>Welcome to Invoice Manager!</Heading>
                    <Text>This is an app that allows you to keep all of your companies invoices in one place and send them to your company's accountant with just one click!
                        You can mess around with it on your own machine by downloading the source code and following
                        the instructions on this repo.
                    </Text>
                </VStack>
            </Container>
        </>
    )
}