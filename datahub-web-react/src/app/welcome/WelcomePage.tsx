import React from 'react';
import { WelcomePageHeader } from './WelcomePageHeader';
import { WelcomePageBody } from './WelcomePageBody';
import { WelcomePageFooter } from './WelcomePageFooter';

export const WelcomePage = () => {
    return (
        <>
            <WelcomePageHeader />
            <WelcomePageBody />
            <WelcomePageFooter />
        </>
    );
};
