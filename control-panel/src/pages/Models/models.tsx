import {Button, Center, Container, Flex, Heading, NativeSelect, Switch, Text, Textarea} from "@chakra-ui/react";
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
                    backgroundColor="gray.600" 
                    borderRadius="15px"
                    p="0"
                >   
                    <Flex gap="20px" flexDirection="column">

                        <Heading textAlign="center" size="2xl" padding="10px">
                            TTS & STT
                        </Heading>
                        <Flex flexDirection="column" gap="6px">
                            <Center>
                                <Flex width="100%" height="100%">
                                    <Container>
                                        <Switch.Root defaultChecked flexDirection="column">
                                            <Switch.HiddenInput/>
                                                <Switch.Label>Enable TTS</Switch.Label>
                                                <Switch.Control>
                                                    <Switch.Thumb />
                                                </Switch.Control>
                                            <Switch.Label />
                                        </Switch.Root>

                                        <Text>
                                            TTS models
                                            <NativeSelect.Root variant="subtle" size="md">
                                                <NativeSelect.Field>
                                                    <option value="1">Option 1</option>
                                                    <option value="2">Option 2</option>
                                                </NativeSelect.Field>
                                                <NativeSelect.Indicator />
                                            </NativeSelect.Root>
                                        </Text>
                                    </Container>

                                    <Container width="100%" height="100%">
                                        <Switch.Root defaultChecked flexDirection="column">
                                            <Switch.HiddenInput/>
                                                <Switch.Label>Enable STT</Switch.Label>
                                                <Switch.Control>
                                                    <Switch.Thumb />
                                                </Switch.Control>
                                            <Switch.Label />
                                        </Switch.Root>

                                        <Text>
                                            TTS models
                                            <Center>
                                                <NativeSelect.Root variant="subtle" size="md">
                                                    <NativeSelect.Field>
                                                        <option value="1">Option 1</option>
                                                        <option value="2">Option 2</option>
                                                    </NativeSelect.Field>
                                                    <NativeSelect.Indicator />
                                                </NativeSelect.Root>
                                            </Center>
                                        </Text>
                                    </Container>
                                </Flex>
                            </Center>
                        </Flex>
                    </Flex>
                </Container>
                    
                <Container 
                    backgroundColor="gray.800" 
                    borderRadius="15px"
                    p="0"
                >
                    <Heading size="2xl" padding="10px" textAlign="center">
                        LLM
                    </Heading>
                    <Center display="flex" flexDirection="column" gap="10px">
                        <Heading size="md" textAlign="center">
                            Start prompt
                            <Textarea autoresize placeholder="prompt..." backgroundColor="gray.700" />
                            <Button type="submit">Submit</Button>
                        </Heading>
                        <Center>
                            <Text fontWeight="semibold">
                                Model name:&nbsp;
                            </Text>
                            <Text>Llama</Text>
                        </Center>
                    </Center>
                </Container>
            </Flex>
        </>    
    );
}

export default Models;