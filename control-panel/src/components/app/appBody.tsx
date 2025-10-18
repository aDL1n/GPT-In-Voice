import type {FC, ReactNode} from 'react';
import { Box } from '@chakra-ui/react';
import { useColorModeValue } from '../ui/color-mode';

interface AppBodyProps {
    children?: ReactNode;
}

export const AppBody: FC<AppBodyProps> = ({ children }) => {
    const backgroundColor = useColorModeValue("white", "#5C5470")
    
    return (
        <Box    
            height="100"
            display="flex"
            overflow="hidden"
            margin="1rem 1rem 1rem 1rem"
            borderRadius="25px"
            backgroundColor={backgroundColor}
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            boxShadow="2px 7px 10px rgba(0, 0, 0, 0.79)"
        >
            {children}
        </Box>
    );
};
