import './memory.css'
import { useEffect, useState } from "react";
import { MemoryClient, type MemoryData } from '../../utils/memoryClient';
import { Box, Container, Flex, Heading, ScrollArea } from '@chakra-ui/react';


function Memory() {
    const [memories, setMemories] = useState<MemoryData[]>([]);

    const handleMemoryUpdate = (data: MemoryData[]) => {
        setMemories(data);
    };

    useEffect(() => {
        const memoryClient: MemoryClient = new MemoryClient(handleMemoryUpdate);
        memoryClient.init();
    }, []);

    return (
        <>
            <Flex flexDirection="column" height="100%">
                <Container minH="0" minW="0" padding="1rem">
                    <ScrollArea.Root>
                        <ScrollArea.Viewport borderRadius="15px">
                            <ScrollArea.Content display="flex" flexDirection="column" gap="6px">
                                {memories.map((memory) => (
                                    <Box backgroundColor="gray.800" padding="10px" borderRadius="15px">
                                        <Heading size="lg">{memory.messageType}</Heading>
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
        </Flex >
        </>
    )
}

export default Memory;
