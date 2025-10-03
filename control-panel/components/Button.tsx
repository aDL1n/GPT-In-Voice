import React, { type ReactNode } from 'react';
import './Button.css'

interface props {
    children?: ReactNode;
}

export const Button: React.FC<props> = ({children}) => {
    return (
        <>
            <button className='customButton'>
                {children}
            </button>
        </>
    );
}  
