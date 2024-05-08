import {
    Menu,
    MenuButton,
    MenuList,
    MenuGroup,
    MenuItem,
    MenuDivider,
    Button,
    Flex,
    Text,
    Heading,
    Spacer,
    HStack,
    Avatar,
    useColorMode,
    Link,
    Box,
    FormControl, FormLabel, Switch
} from "@chakra-ui/react";
import {useEffect, useState} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";
import {decodeToken, getUser} from "../services/client.jsx";
import {MoonIcon, SunIcon} from "@chakra-ui/icons";

export default function Navbar() {
    const navigate = useNavigate();
    const { user, logout, isUserAuthenticated } = useAuth();
    const [ isAuthenticated, setIsAuthenticated ] = useState(isUserAuthenticated());
    const { colorMode, toggleColorMode } = useColorMode();
    const [ username, setUsername ] = useState("");


    useEffect(() => {
        setIsAuthenticated(isUserAuthenticated());
        if(user) {
        setUsername(user.username);
        } else {
            setUsername("johndoe")}
    }, [user]);

    function handleLoginClick() {
        navigate('login');
    }

    function handleMyAccountClick() {
        navigate(`user/${user.id}/my-account`);
    }

    function handleRegisterClick() {
        navigate('register');
    }

    function handleLogoutClick() {
        setIsAuthenticated(false);
        logout();
        navigate('/')
    }

    function handleNavigate() {
        navigate(`/user/${user.id}`)
    }

    return (
        <Flex
            bg={"gray.800"}
            as='nav'
            alignItems={'center'}
            gap={'10px'}
            position="sticky"
            top="0"
            zIndex="1"
        >
            <Heading color={"white"} as="h1" ml={"10px"}>
                <NavLink to={'/'}>
                    Invoice Manager
                </NavLink>
            </Heading>
            <FormControl display={"flex"} alignItems={"center"}>
                <MoonIcon color={"white"}/>
                <Switch onChange={toggleColorMode} id={"color-mode"} />
                <SunIcon color={"white"}/>
            </FormControl>
            <Spacer/>
            {isAuthenticated ? (
                <HStack color={"white"} spacing={"20px"} mr={"10px"}>
                    <Menu>
                      <MenuButton as={Avatar} cursor='pointer' colorScheme='pink'>
                      </MenuButton>
                      <MenuList>
                        <MenuGroup title='Profile'>
                          <MenuItem onClick={handleMyAccountClick}>My Account</MenuItem>
                          <MenuItem onClick={handleLogoutClick}>Logout </MenuItem>
                        </MenuGroup>
                      </MenuList>
                    </Menu>
                    <Text>Welcome, {username}!</Text>

                </HStack>
            ) : (
                <HStack mr={"10px"}>
                    <Button onClick={handleLoginClick} colorScheme={"purple"}>Login</Button>
                    <Button onClick={handleRegisterClick} colorScheme={"purple"}>Register</Button>
                </HStack>
            )}
        </Flex>
    )
}