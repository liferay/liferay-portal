/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {useMemo} from 'react';
import {Link} from 'react-router-dom';

import ErrorBoundary from '../../../components/ErrorBoundary';
import Page from '../../../components/Page';
import {useMarketplaceContext} from '../../../context/MarketplaceContext';
import i18n from '../../../i18n';
import {formatCurrency} from '../../../utils/currencies';
import InfoCard from '../components/InfoCard';
import DonutKPIChart from '../components/charts/DonutKPIChart';
import useAccountsMetrics from '../hooks/useAccountsMetrics';
import useAnalyticsViewsMetrics from '../hooks/useAnalyticsViewsMetrics';
import useKPI from '../hooks/useKPI';
import useOrderMetrics from '../hooks/useOrderMetrics';
import AdministratorAppsListView from './Apps/AdministratorAppsListView';
import {AdministratorOrdersListView} from './Orders';

export default function AdministratorSummary() {
	const {data: {kpis = [], projectsKPI} = {}} = useKPI();
	const {data: accounts} = useAccountsMetrics('week');
	const {data: orderMetrics} = useOrderMetrics('week');
	const {marketplaceUserAccount} = useMarketplaceContext();
	const {visitorsMetric} = useAnalyticsViewsMetrics();

	const infoCards = useMemo(
		() => [
			{
				growth: accounts?.growth ?? 0,
				growthContext: `+${accounts?.lastPeriod ?? 0} this week `,
				symbol: 'users',
				title: i18n.translate('accounts'),
				value: accounts?.totalCount ?? 0,
			},
			{
				symbol: 'dollar-symbol',
				title: (
					<span>
						{i18n.translate('income')}{' '}
						&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;
					</span>
				),
				value: formatCurrency(projectsKPI?.totalAmount?.USD || 0),
			},
			{
				growth: orderMetrics?.growth ?? 0,
				growthContext: `+${orderMetrics?.lastPeriod ?? 0} this week `,
				symbol: 'shopping-cart',
				title: i18n.translate('orders'),
				value: orderMetrics?.totalCount ?? 0,
			},
			{
				symbol: 'analytics',
				title: 'Site Visitors',
				value: visitorsMetric ?? 0,
			},
		],
		[
			accounts?.growth,
			accounts?.lastPeriod,
			accounts?.totalCount,
			orderMetrics?.growth,
			orderMetrics?.lastPeriod,
			orderMetrics?.totalCount,
			projectsKPI?.totalAmount?.USD,
			visitorsMetric,
		]
	);

	return (
		<Page
			description={i18n.translate(
				'a-sleek-and-intuitive-admin-dashboard-for-monitoring-key-metrics'
			)}
			title={i18n.translate('admin-dashboard')}
		>
			<div className="d-flex flex-column">
				<div className="d-flex flex-wrap mb-4" style={{gap: '20px'}}>
					<ErrorBoundary className="ml-5">
						{kpis.map((chart, index) => (
							<DonutKPIChart {...chart} key={index} />
						))}
					</ErrorBoundary>
				</div>

				<div className="d-flex flex-wrap info-container mb-8">
					{infoCards.map((infoCard, index) => (
						<InfoCard {...infoCard} key={index} />
					))}
				</div>

				<Page
					pageRendererProps={{
						className: 'border py-2 rounded-lg mb-8',
					}}
					rightButton={
						marketplaceUserAccount.isAdmin && (
							<Link className="font-weight-bold" to="/orders">
								{i18n.translate('view-all')}
								<ClayIcon symbol="order-arrow-right" />
							</Link>
						)
					}
					title={i18n.translate('recent-orders')}
				>
					<AdministratorOrdersListView
						listViewProps={{
							id: 'summary-orders',
							initialContext: {pageSize: 5},
							paginationOptions: {displayType: false},
						}}
						managementToolbarProps={{
							visible: false,
						}}
					/>
				</Page>

				<Page
					pageRendererProps={{className: 'border py-2 rounded-lg'}}
					rightButton={
						<Link className="font-weight-bold" to="/apps">
							{i18n.translate('view-all')}
							<ClayIcon symbol="order-arrow-right" />
						</Link>
					}
					title={i18n.translate('published-apps')}
				>
					<AdministratorAppsListView
						isSortable
						listViewProps={{
							id: 'summary-apps',
							initialContext: {pageSize: 5},
							paginationOptions: {displayType: false},
						}}
						managementToolbarProps={{
							visible: false,
						}}
					/>
				</Page>
			</div>
		</Page>
	);
}
