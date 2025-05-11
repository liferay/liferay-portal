/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {useMemo} from 'react';
import {Link} from 'react-router-dom';

import Page from '../../../components/Page';
import i18n from '../../../i18n';
import InfoCard from '../components/InfoCard';
import useAccountsMetrics from '../hooks/useAccountsMetrics';
import useAnalyticsViewsMetrics from '../hooks/useAnalyticsViewsMetrics';
import useOrderMetrics from '../hooks/useOrderMetrics';
import {AdministratorAppsListView} from './Apps';
import {AdministratorOrdersListView} from './Orders';

const getTotalAmountCurrency = (amount = 0) =>
	new Intl.NumberFormat('en-US', {
		currency: 'USD',
		style: 'currency',
	}).format(amount);

const Container = ({
	children,
	path,
	title,
}: {
	children: React.ReactNode;
	path: string;
	title: string;
}) => (
	<>
		<div className="d-flex justify-content-between">
			<h3>{title}</h3>

			<Link to={path}>
				<span className="font-weight-bold">
					{i18n.translate('view-all')}
				</span>

				<ClayIcon
					className="ml-2"
					symbol="order-arrow-right
"
				/>
			</Link>
		</div>

		{children}
	</>
);

export default function AdministratorSummary() {
	const {data: accounts} = useAccountsMetrics('week');
	const {visitorsMetric} = useAnalyticsViewsMetrics();
	const {data: orderMetrics} = useOrderMetrics('week');

	const infoCard = useMemo(
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
				value: getTotalAmountCurrency(orderMetrics?.paidAmount),
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
			orderMetrics?.paidAmount,
			orderMetrics?.totalCount,
			visitorsMetric,
		]
	);

	return (
		<Page
			description="A sleek and intuitive admin dashboard for monitoring key metrics"
			title="Admin Dashboard"
		>
			<div className="d-flex flex-column">
				<div className="d-flex flex-wrap info-container mb-4">
					{infoCard.map((infoItem, index) => (
						<InfoCard
							growth={infoItem.growth}
							growthContext={infoItem.growthContext}
							key={index}
							symbol={infoItem.symbol}
							title={infoItem.title as string}
							value={infoItem.value as string}
						/>
					))}
				</div>

				<Container
					path="/orders"
					title={i18n.translate('recent-orders')}
				>
					<AdministratorOrdersListView
						listViewProps={{
							id: 'summary-orders',
							initialContext: {pageSize: 5},
							paginationOptions: {displayType: 'never'},
						}}
					/>
				</Container>

				<Container
					path="/apps"
					title={i18n.translate('published-apps')}
				>
					<AdministratorAppsListView
						listViewProps={{
							id: 'summary-apps',
							initialContext: {pageSize: 5},
							paginationOptions: {displayType: 'never'},
						}}
					/>
				</Container>
			</div>
		</Page>
	);
}
