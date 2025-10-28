/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {Suspense} from 'react';

import Loading from './components/Loading';
import {MarketplaceProperties} from './utils/attributes';

const lazyRoutes = {
	'administrator-dashboard': React.lazy(
		() =>
			import(
				'./pages/AdministratorDashboard/AdministratorDashboardRouter'
			)
	),
	'finance-dashboard': React.lazy(
		() => import('./pages/FinanceDashboard/FinanceDashboardRouter')
	),
	'get-app': React.lazy(() => import('./pages/GetApp/GetAppRouter')),
	'license-agreement': React.lazy(
		() => import('./pages/LicenseAgreementPage')
	),
	'new-account-trigger': React.lazy(
		() => import('./pages/NewAccount/NewAccountButton')
	),
	'next-steps': React.lazy(() => import('./pages/NextSteps')),
	'oauth2-authorize': React.lazy(
		() => import('./pages/OAuth2Authorize/OAuth2AuthorizeRouter')
	),
	'product-purchase': React.lazy(
		() => import('./pages/ProductPurchase/ProductPurchaseRouter')
	),
	'published-apps': React.lazy(
		() => import('./pages/PublisherDashboard/PublisherDashboardRouter')
	),
	'publisher-gate': React.lazy(
		() => import('./pages/PublisherGate/PublisherGateRouter')
	),
	'purchased-apps': React.lazy(
		() => import('./pages/CustomerDashboard/CustomerDashboardRouter')
	),
	'ssa-dashboard': React.lazy(
		() => import('./pages/SSADashboard/SSADashboardRouter')
	),
} as const;

export type RouteType = keyof typeof lazyRoutes;

type AppRoutesProps = {
	path: RouteType;
	properties: MarketplaceProperties;
};

export default function Routes({path, properties}: AppRoutesProps) {
	const Route = lazyRoutes[path] as React.FC<{
		properties: MarketplaceProperties;
	}>;

	if (!Route) {
		return <h1>Page not found</h1>;
	}

	return (
		<Suspense
			fallback={
				<Loading
					className="mt-4"
					displayType="secondary"
					shape="squares"
				/>
			}
		>
			<Route properties={properties} />
		</Suspense>
	);
}
