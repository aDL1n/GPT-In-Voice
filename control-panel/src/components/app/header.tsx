import type { FC } from 'react';
import {
    Box,
    Flex,
    Heading,
    HStack,
    Button,
    IconButton, For,
} from '@chakra-ui/react';
import { FaSun, FaMoon } from 'react-icons/fa';
import {useColorMode, useColorModeValue} from "@/components/ui/color-mode.tsx";

interface NavItem {
    label: string;
    href: string;
}

interface HeaderProps {
    navItems: NavItem[];
}

export const Header: FC<HeaderProps> = ({ navItems }) => {
    const { colorMode, toggleColorMode } = useColorMode();
    const bg = useColorModeValue('gray.100', '#352F44');
    const color = useColorModeValue('gray.800', 'white');

    // @ts-ignore
    return (
        <Box
            bg={bg}
            color={color}
            px={6}
            py={3}
            boxShadow="md"
            borderBottomRadius="25px"
            ml="1rem"
            mr="1rem"
        >
            <Flex justify="space-between" align="center">

                <Heading size="xl" letterSpacing="tight">
                    Control Panel
                </Heading>

                <HStack>
                    <For each={navItems}>
                        {(item, index) => (
                            <Button
                                key={index}
                                as="a"
                                href={item.href}
                                variant="ghost"
                                _hover={{
                                    bg: useColorModeValue('gray.200', 'gray.700'),
                                }}
                                borderRadius="40px"
                                
                            >
                                {item.label}
                            </Button>
                        )}
                    </For>
                    <IconButton
                        aria-label="Toggle color mode"
                        onClick={toggleColorMode}
                        variant="ghost"
                    >
                        {colorMode === 'light' ? <FaMoon /> : <FaSun />}
                    </IconButton>
                </HStack>
            </Flex>
        </Box>
    );
};
