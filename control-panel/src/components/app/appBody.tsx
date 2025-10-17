import type {FC, ReactNode} from 'react';
import { Box } from '@chakra-ui/react';

interface AppBodyProps {
    children?: ReactNode;
}

export const AppBody: FC<AppBodyProps> = ({ children }) => {
    return (
        <Box
            display="flex"
            flex="1"
            margin="1rem 1rem 1rem 1rem"
            borderRadius="25px"
            backgroundColor="#5C5470"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            boxShadow="3px 6px 8px rgba(0, 0, 0, 0.3)"
        >
            {children}
        </Box>
    );
};
