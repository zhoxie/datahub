import React from 'react';
import { Switch, Route } from 'react-router-dom';
import { Layout } from 'antd';
import { BrowseResultsPage } from './browse/BrowseResultsPage';
import { EntityPage } from './entity/EntityPage';
import { PageRoutes } from '../conf/Global';
import { useEntityRegistry } from './useEntityRegistry';
import { HomePage } from './home/HomePage';
import { SearchPage } from './search/SearchPage';
import { AnalyticsPage } from './analyticsDashboard/components/AnalyticsPage';
import { PoliciesPage } from './policy/PoliciesPage';
import AppConfigProvider from '../AppConfigProvider';

/**
 * Container for all views behind an authentication wall.
 */
export const ProtectedRoutes = (): JSX.Element => {
    const entityRegistry = useEntityRegistry();
    return (
        <AppConfigProvider>
            <Layout style={{ height: '100%', width: '100%' }}>
                <Layout>
                    <Switch>
                        <Route exact path="/index" render={() => <HomePage />} />
                        {entityRegistry.getEntities().map((entity) => (
                            <Route
                                key={entity.getPathName()}
                                path={`/${entity.getPathName()}/:urn`}
                                render={() => <EntityPage entityType={entity.type} />}
                            />
                        ))}
                        <Route path={PageRoutes.SEARCH_RESULTS} render={() => <SearchPage />} />
                        <Route path={PageRoutes.BROWSE_RESULTS} render={() => <BrowseResultsPage />} />
                        <Route path={PageRoutes.ANALYTICS} render={() => <AnalyticsPage />} />
                        <Route path={PageRoutes.POLICIES} render={() => <PoliciesPage />} />
                    </Switch>
                </Layout>
            </Layout>
        </AppConfigProvider>
    );
};
