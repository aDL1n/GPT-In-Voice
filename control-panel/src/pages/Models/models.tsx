import { Center, Container, Flex, Heading } from "@chakra-ui/react";
import type { JSX } from "react";

function Models(): JSX.Element {
    return (
        <>
            <Flex 
                height="100%" 
                width="100%" 
                padding="1rem"
                gap="20px"
            >
                    
                <Container 
                    backgroundColor="gray.800" 
                    borderRadius="15px"
                    p="0"
                >
                    <Center>
                        <Heading size="2xl" padding="10px">
                            TTS & STT
                        </Heading>
                    </Center>
                </Container>
                    
                <Container 
                    backgroundColor="gray.800" 
                    borderRadius="15px"
                    p="0"
                >
                    <Center>
                        <Heading size="2xl" padding="10px">
                            LLM
                        </Heading>
                    </Center>
                </Container>

                <Container 
                    backgroundColor="gray.800" 
                    borderRadius="15px"
                    p="0"
                >
                    <Center>
                        <Heading size="2xl" padding="10px">
                            Global settings
                        </Heading>
                    </Center>
                </Container>
        
            </Flex>
        </>    
    );
}

export default Models;