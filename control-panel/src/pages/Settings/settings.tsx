import { SettingsClient } from "@/utils/settingsClient";
import { Button, Container, Field, Flex, Heading, Portal, Select, Separator, Switch, Textarea } from "@chakra-ui/react";
import { useEffect, useState } from "react";

function Models() {
    const settingsClient = new SettingsClient();

    const [recognitionModels, setRecognitionModels] = useState<string[]>([]);
    const [synthsisModels, setSynthesisModels] = useState<string[]>([]);

    useEffect(() => {
        setRecognitionModels(settingsClient.getRecognitionModels());
        
        settingsClient.getSynthesisModels().then((data) => {
            setSynthesisModels(data);
        });
    }, []) 

    return (
        <>
            <Flex
                height="100%"
                width="100%"
                padding="1rem"
                gap="20px"
            >

                <Container
                    borderColor="gray.700"
                    borderWidth="1px"
                    borderRadius="15px"
                    shadow="md"
                    p="0"
                >
                    <Flex gap="4" width="100%" height="100%" flexDirection="column">
                        <Container
                            display="flex"
                            flexDirection="column"
                            gap="1"
                            width="100%"
                        >
                            <Heading textAlign="center" size="2xl" padding="10px">
                                TTS
                            </Heading>
                            <Switch.Root
                                defaultChecked
                            >
                                <Switch.HiddenInput />
                                <Switch.Label>Enable TTS</Switch.Label>
                                <Switch.Control>
                                    <Switch.Thumb />
                                </Switch.Control>
                                <Switch.Label />
                            </Switch.Root>

                            <Select.Root
                                size="sm"
                                width="220px"
                            >
                                <Select.HiddenSelect />
                                <Select.Label>Select TTS model</Select.Label>
                                <Select.Control>
                                    <Select.Trigger>
                                        <Select.ValueText />
                                    </Select.Trigger>
                                    <Select.IndicatorGroup>
                                        <Select.Indicator />
                                    </Select.IndicatorGroup>
                                </Select.Control>
                                <Portal>
                                    <Select.Positioner>
                                        <Select.Content>

                                        </Select.Content>
                                    </Select.Positioner>
                                </Portal>
                            </Select.Root>

                        </Container>

                        <Separator
                            borderColor="gray.700"
                            marginInline="5"
                        />

                        <Container
                            width="100%"
                            display="flex"
                            flexDirection="column"
                            gap="1"
                        >
                            <Heading textAlign="center" size="2xl">
                                STT
                            </Heading>
                            <Switch.Root defaultChecked >
                                <Switch.HiddenInput />
                                <Switch.Label>Enable STT</Switch.Label>
                                <Switch.Control>
                                    <Switch.Thumb />
                                </Switch.Control>
                                <Switch.Label />
                            </Switch.Root>

                            <Select.Root
                                size="sm"
                                width="220px"
                            >
                                <Select.HiddenSelect />
                                <Select.Label>Select STT model</Select.Label>
                                <Select.Control>
                                    <Select.Trigger>
                                        <Select.ValueText />
                                    </Select.Trigger>
                                    <Select.IndicatorGroup>
                                        <Select.Indicator />
                                    </Select.IndicatorGroup>
                                </Select.Control>
                                <Portal>
                                    <Select.Positioner>
                                        <Select.Content>

                                        </Select.Content>
                                    </Select.Positioner>
                                </Portal>
                            </Select.Root>
                        </Container>
                    </Flex>
                </Container>

                <Container
                    borderColor="gray.700"
                    borderWidth="1px"
                    borderRadius="15px"
                    shadow="md"
                    p="0"
                >
                    <Heading size="2xl" padding="10px" textAlign="center">
                        LLM
                    </Heading>
                    <Flex>
                        <Container>
                            <Field.Root gap="2">
                                <Field.Label>
                                    Start system prompt
                                </Field.Label>
                                <Textarea 
                                    autoresize 
                                    placeholder="prompt..." 
                                    variant="outline" 
                                    borderColor="gray.700"
                                />
                                <Button type="submit">Submit</Button>
                            </Field.Root>
                        </Container>
                    </Flex>

                </Container>
            </Flex>
        </>
    );
}

export default Models;