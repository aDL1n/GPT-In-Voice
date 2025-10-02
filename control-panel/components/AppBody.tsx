import React, { type ReactNode } from 'react';
import './AppBody.css'

interface props {
    children?: ReactNode;
}

export const AppBody: React.FC<props> = ({children}) => {
    return (
        <div className='customBody'>
            {children}
        </div>
    );
}