import { SettingsClient } from "@/utils/settingsClient";
import {
    Container,
    Field,
    Flex,
    Heading,
    type ListCollection,
    Portal,
    Select,
    Separator,
    Switch,
    Textarea,
} from "@chakra-ui/react";
import {useEffect,useState} from "react";
import {ModelClient} from "@/utils/modelClient.tsx";
import { useColorModeValue } from "@/components/ui/color-mode";

function Models() {
    const settingsClient = new SettingsClient();
    const modelClient = new ModelClient();

    const textColor = useColorModeValue("gray.800", "white")

    const [recognitionModels, setRecognitionModels] = useState<ListCollection<{ label: string; value: string }>>();
    const [synthesisModels, setSynthesisModels] = useState<ListCollection<{ label: string; value: string }>>();
    const [systemPrompt, setSystemPrompt] = useState<string>();

    useEffect(() => {
        settingsClient.getRecognitionModels().then(data => {
            setRecognitionModels(data);
        });

        settingsClient.getSynthesisModels().then(data => {
            setSynthesisModels(data);
        });

        modelClient.getSystemPrompt().then((data) => {
           setSystemPrompt(data);
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
                            <Heading textAlign="center" size="2xl" padding="10px" color={textColor}>
                                TTS
                            </Heading>
                            <Switch.Root
                                defaultChecked
                                onCheckedChange={(e) => settingsClient.enableSynthesisModel(e.checked)}
                                color={textColor}
                            >
                                <Switch.HiddenInput/>
                                <Switch.Label>Enable TTS</Switch.Label>
                                <Switch.Control>
                                    <Switch.Thumb />
                                </Switch.Control>
                                <Switch.Label />
                            </Switch.Root>
                            {synthesisModels && (
                                <Select.Root
                                    collection={synthesisModels?.copy()}
                                    size="sm"
                                    width="220px"
                                    defaultValue={["piper"]}
                                    onValueChange={(e) => console.log(e.value)}
                                    color={textColor}
                                >
                                    <Select.HiddenSelect />
                                    <Select.Label>Selected TTS model</Select.Label>
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
                                                {synthesisModels.items.map((model) => (
                                                    <Select.Item item={model} key={model.value}>
                                                        {model.label}
                                                    </Select.Item>
                                                ))}
                                            </Select.Content>
                                        </Select.Positioner>
                                    </Portal>
                                </Select.Root>
                            )}
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
                            <Heading textAlign="center" size="2xl" color={textColor}>
                                STT
                            </Heading>
                            <Switch.Root 
                                defaultChecked 
                                onCheckedChange={(e) => settingsClient.enableRecognitionModel(e.checked)} 
                                color={textColor}
                            >
                                <Switch.HiddenInput />
                                <Switch.Label>Enable STT</Switch.Label>
                                <Switch.Control>
                                    <Switch.Thumb />
                                </Switch.Control>
                                <Switch.Label />
                            </Switch.Root>

                            {recognitionModels && (
                                <Select.Root
                                    collection={recognitionModels?.copy()}
                                    size="sm"
                                    width="220px"
                                    defaultValue={["whisper"]}
                                    onValueChange={(e) => console.log(e.value)}
                                    color={textColor}
                                >
                                    <Select.HiddenSelect />
                                    <Select.Label>Selected STT model</Select.Label>
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
                                                {recognitionModels.items.map((model) => (
                                                    <Select.Item item={model} key={model.value}>
                                                        {model.label}
                                                    </Select.Item>
                                                ))}
                                            </Select.Content>
                                        </Select.Positioner>
                                    </Portal>
                                </Select.Root>
                            )}
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
                    <Heading 
                        size="2xl" 
                        padding="10px" 
                        textAlign="center"
                        color={textColor}
                    >
                        LLM
                    </Heading>
                    <Flex>
                        <Container>
                            <Field.Root gap="2" color={textColor}>
                                <Field.Label>
                                    Start system prompt
                                </Field.Label>
                                <Textarea
                                    autoresize
                                    placeholder="prompt..."
                                    variant="outline"
                                    borderColor="gray.700"
                                    value={systemPrompt}
                                    disabled
                                />
                                {/*<Button type="submit">Submit</Button>*/}
                            </Field.Root>
                        </Container>
                    </Flex>

                </Container>
            </Flex>
        </>
    );
}

export default Models;