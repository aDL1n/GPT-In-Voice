import { type FC, useEffect, useState } from 'react';
import {
    Box,
    Flex,
    Heading,
    HStack,
    Button,
    IconButton,
    Group,
    Status,
    Spinner,
} from '@chakra-ui/react';
import { FaSun, FaMoon } from 'react-icons/fa';
import { useColorMode, useColorModeValue } from "@/components/ui/color-mode.tsx";
import { ApiStatus } from '@/utils/apiStatus';

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
    const [statusColor, setStatusColor] = useState<string>('gray');

    useEffect(() => {
        const apiStatus = new ApiStatus();
        apiStatus.getColor().then(setStatusColor);
    }, []);

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
            userSelect="none"
        >
            <Flex justify="space-between" align="center">

                <Group display="flex" gap="20px">
                    <Heading size="xl" letterSpacing="tight">
                        Control Panel
                    </Heading>
                    <Status.Root size="lg">
                        Status:
                        {statusColor === 'gray' ? (
                            <Spinner size="sm" ml="2" />
                        ) : (
                            <Status.Indicator colorPalette={statusColor} />
                        )}
                    </Status.Root>
                </Group>

                <HStack>
                    {navItems.map((item, index) => (
                        <Button
                            key={index}
                            as="a"
                            href={item.href}
                            variant="ghost"
                            _hover={{
                                bg: useColorModeValue('gray.300', 'gray.800'),
                            }}
                            borderRadius="40px"
                        >
                            {item.label}
                        </Button>
                    ))}
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
