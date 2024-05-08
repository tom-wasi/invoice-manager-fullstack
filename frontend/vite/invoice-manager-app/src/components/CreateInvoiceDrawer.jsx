import {
    Box,
    Button, Divider,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    Input,
    Text,
    useDisclosure
} from "@chakra-ui/react";
import React, {useCallback, useState} from "react";
import {AddIcon} from "@chakra-ui/icons";
import {createInvoice} from "../services/client.jsx"
import {useParams} from "react-router-dom";
import {errorNotification, successNotification} from "../services/notification.jsx";
import {useDropzone} from 'react-dropzone';

export const CreateInvoiceDrawer = () => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    const [description, setDescription] = useState('');
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const { companyId } = useParams();

    const onDrop = useCallback((acceptedFiles) => {
        setFile(acceptedFiles[0]);
        setPreview(URL.createObjectURL(acceptedFiles[0]));
    }, []);

    const uploadFile = () => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('description', description);
        formData.append('preview', preview);
        createInvoice(companyId, formData)
            .then(response => {
                successNotification(response.data);
                window.location.reload();
            }).catch(err => {
            errorNotification(err.data)
        });
    };

    const {getRootProps, getInputProps, isDragActive} = useDropzone({
        onDrop,
        accept: 'image/jpeg, image/png, application/pdf'
    });

    return (
        <>
            <Button leftIcon={<AddIcon />} colorScheme='purple' onClick={onOpen}>
                Add
            </Button>
            <Drawer
                isOpen={isOpen}
                placement='right'
                onClose={onClose}
            >
                <DrawerOverlay />
                <DrawerContent>
                    <DrawerCloseButton />
                    <DrawerHeader borderBottomWidth='1px'>
                        Upload Invoice
                    </DrawerHeader>
                    <DrawerBody>
                        <Box {...getRootProps()}
                             w={'100%'}
                             textAlign={'center'}
                             border={'dashed'}
                             borderColor={'gray.200'}
                             borderRadius={'3xl'}
                             p={6}
                             rounded={'md'}>
                            <Input {...getInputProps()} />
                            {preview ? <img src={preview} alt="Preview" style={{maxWidth: '100%', maxHeight: '100%'}}/> :
                                isDragActive ?
                                    <Text>Drop the file here ...</Text> :
                                    <Text>Drag 'n' drop file here, or click to select file</Text>
                            }
                        </Box>
                        <Input mt={"10px"} type="text" placeholder="Description..."
                               onChange={e => setDescription(e.target.value)}/>
                    </DrawerBody>

                    <DrawerFooter borderTopWidth='1px'>
                        <Button variant='outline' mr={3} onClick={onClose}>
                            Cancel
                        </Button>
                        <Button onClick={uploadFile} colorScheme='purple'>Upload Invoice</Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    );
};