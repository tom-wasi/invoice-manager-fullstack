import React from 'react';
import {useAuth} from '../context/AuthContext.jsx';
import {VStack} from "@chakra-ui/react";

export const MyAccount = () => {
    const {user} = useAuth();

    return (
        <>
            <VStack>Under construction :) </VStack>
        </>
        )
    }