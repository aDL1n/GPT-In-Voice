import type { FC, ReactNode } from "react";
import { Box } from "@chakra-ui/react";

interface Props {
    isUser: boolean,
    children?: ReactNode
}

export const Message: FC<Props> = ({ isUser, children }) => {
    return (
        <>
            {isUser ? <Box
                    alignSelf="flex-end"
                    backgroundColor="black"
                    padding="10px"
                    borderRadius="15px"
                >
                    {children}
                </Box> : <Box
                alignSelf="flex-start"
                backgroundColor="gray.700"
                padding="10px"
                borderRadius="15px"
                >
                    {children}
                </Box>}
        </>
    );
};