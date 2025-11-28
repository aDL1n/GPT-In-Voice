import { useEffect, useState } from "react";
import { MemoryClient, type MemoryData } from '../../utils/memoryClient';
import { Box, Container, Heading, IconButton, ScrollArea } from '@chakra-ui/react';
import { FaTrash } from "react-icons/fa";
import { useColorModeValue } from "@/components/ui/color-mode";


function Memory() {
    const [memories, setMemories] = useState<MemoryData[]>([]);

    const handleMemoryUpdate = (data: MemoryData[]) => {
        // setMemories(data);
    };

    // const deleteButtonBackground = useColorModeValue("white", "gray.900");

    const memoryClient: MemoryClient = new MemoryClient(handleMemoryUpdate);

    useEffect(() => {
        memoryClient.init();
        setMemories([
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            },
            {
                text: "string",
                metadata: {
                    name: "",
                    data: ""
                },
                messageType: "USER",
                media: ""
            }
        ])
    }, []);

    return (
        <>
            <Container minH="0" minW="0" padding="1rem">
                <ScrollArea.Root>
                    <ScrollArea.Viewport borderRadius="15px">
                        <ScrollArea.Content display="flex" flexDirection="column" gap="6px">
                            {memories.map((memory, id) => (
                                <Box backgroundColor="gray.800" padding="10px" borderRadius="15px">
                                    <Box display="flex" alignItems="center" justifyContent="space-between">
                                        <Heading size="lg">{memory.messageType}</Heading>
                                        <IconButton
                                            borderRadius="10px"
                                            height="32px"
                                            width="32px"
                                            minWidth="16px"
                                            onClick={() => {
                                                memoryClient.delete(id)
                                            }}
                                            color="gray.900"
                                            backgroundColor="white"
                                        >
                                            <FaTrash />
                                        </IconButton>
                                    </Box>
                                    <p style={{
                                        wordBreak: "break-word",
                                    }}>{memory.text}</p>
                                </Box>
                            ))}
                        </ScrollArea.Content>
                    </ScrollArea.Viewport>
                    <ScrollArea.Scrollbar orientation="vertical">
                        <ScrollArea.Thumb />
                    </ScrollArea.Scrollbar>
                    <ScrollArea.Corner />
                </ScrollArea.Root>
            </Container>
        </>
    )
}

export default Memory;
