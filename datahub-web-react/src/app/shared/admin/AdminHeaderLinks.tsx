import styled from 'styled-components';
import * as React from 'react';
import { BankOutlined, BarChartOutlined, UsergroupAddOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { Button } from 'antd';

import { useAppConfig } from '../../useAppConfig';
import { useGetAuthenticatedUser } from '../../useGetAuthenticatedUser';
import { ANTD_GRAY } from '../../entity/shared/constants';

const AdminLink = styled.span`
    margin-right: 4px;
    color: ${ANTD_GRAY[1]};
`;

type Props = {
    isHome?: boolean;
};

export function AdminHeaderLinks({ isHome }: Props) {
    const me = useGetAuthenticatedUser();
    const { config } = useAppConfig();

    const isAnalyticsEnabled = config?.analyticsConfig.enabled;
    const isPoliciesEnabled = config?.policiesConfig.enabled;
    const isIdentityManagementEnabled = config?.identityManagementConfig.enabled;

    const showAnalytics = (isAnalyticsEnabled && me && me.platformPrivileges.viewAnalytics) || false;
    const showPolicyBuilder = (isPoliciesEnabled && me && me.platformPrivileges.managePolicies) || false;
    const showIdentityManagement =
        (isIdentityManagementEnabled && me && me.platformPrivileges.manageIdentities) || false;

    return (
        <>
            {showAnalytics && (
                <AdminLink>
                    <Link to="/analytics">
                        <Button type="text" style={{ color: isHome ? '#eee' : '' }}>
                            <BarChartOutlined /> Analytics
                        </Button>
                    </Link>
                </AdminLink>
            )}
            {showPolicyBuilder && (
                <AdminLink>
                    <Link to="/policies">
                        <Button type="text" style={{ color: isHome ? '#eee' : '' }}>
                            <BankOutlined /> Policies
                        </Button>
                    </Link>
                </AdminLink>
            )}
            {showIdentityManagement && (
                <AdminLink>
                    <Link to="/identities">
                        <Button type="text" style={{ color: isHome ? '#eee' : '' }}>
                            <UsergroupAddOutlined /> Users & Groups
                        </Button>
                    </Link>
                </AdminLink>
            )}
        </>
    );
}
