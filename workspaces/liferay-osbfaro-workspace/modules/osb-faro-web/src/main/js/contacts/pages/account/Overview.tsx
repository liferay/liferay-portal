import * as API from 'shared/api';
import AccountInfoBar from './components/AccountInfoBar';
import AccountMetricsCard from './components/AccountMetricsCard';
import BasePage from 'shared/components/base-page';
import ClayLayout from '@clayui/layout';
import React from 'react';
import TopAssets from './components/TopAssets';
import TopCategoriesAndTags from './components/TopCategoriesAndTags';
import TopPagesCard from './components/TopPagesCard';
import {AccountOverviewMetricType, IAccountOverviewMetric} from './utils/types';
import {IAccount} from './components/AccountInfo';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {useQueryParams} from 'shared/hooks/useQueryParams';

interface IOverviewProps {
	account?: IAccount;
}

const Overview: React.FC<IOverviewProps> = ({account}) => {
	const {channelId, groupId, id} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();

	const query = useQueryParams();

	const {data, loading} = useRequest({
		dataSourceFn: API.accounts.fetchOverviewMetrics,
		variables: {channelId, groupId},
	});

	const metrics = data as IAccountOverviewMetric[] | undefined;

	const getCount = (metricType: AccountOverviewMetricType) =>
		metrics?.find((metric) => metric.metricType === metricType)?.value;

	return (
		<BasePage.Context.Provider
			value={{
				accountId: id,
				accountName: account?.accountName,
				filters: {},
				router: {
					params: {channelId, groupId, id},
					query,
				},
			}}
		>
			<SectionHeader
				icon="box-container"
				title={Liferay.Language.get('account-info')}
			/>

			<AccountInfoBar
				accountName={account?.accountName}
				accountType={account?.accountType}
				annualRevenue={account?.annualRevenue}
				country={account?.country}
				industry={account?.industry}
				lifecycleStage={account?.lifecycleStage}
			/>

			<ClayLayout.Row>
				<ClayLayout.Col lg={3} md={6}>
					<AccountMetricsCard
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-individuals'),
								value: getCount(
									AccountOverviewMetricType.TotalIndividuals
								),
							},
						]}
						title={Liferay.Language.get('total-individuals')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col lg={3} md={6}>
					<AccountMetricsCard
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-known'),
								value: getCount(
									AccountOverviewMetricType.KnownIndividuals
								),
							},
							{
								label: Liferay.Language.get('x-anonymous'),
								value: getCount(
									AccountOverviewMetricType.AnonymousIndividuals
								),
							},
						]}
						title={Liferay.Language.get('identity-breakdown')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col lg={3} md={6}>
					<AccountMetricsCard
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-returning'),
								value: getCount(
									AccountOverviewMetricType.ReturningIndividuals
								),
							},
							{
								label: Liferay.Language.get('x-first-time'),
								value: getCount(
									AccountOverviewMetricType.FirstTimeIndividuals
								),
							},
						]}
						title={Liferay.Language.get('engagement-status')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col lg={3} md={6}>
					<AccountMetricsCard
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-no-activity'),
								value: getCount(
									AccountOverviewMetricType.InactiveIndividuals
								),
							},
						]}
						title={Liferay.Language.get('inactive-users')}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<SectionHeader
				className="mb-3 mt-4"
				icon="display-content"
				title={Liferay.Language.get('engagement-summary')}
			/>

			<ClayLayout.Row>
				<ClayLayout.Col size={12}>
					<TopPagesCard className="top-pages-card-root" />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col className="d-flex flex-column" size={12} xl={6}>
					<TopAssets account={account} className="flex-grow-1" />
				</ClayLayout.Col>

				<ClayLayout.Col className="d-flex flex-column" size={12} xl={6}>
					<TopCategoriesAndTags
						account={account}
						className="flex-grow-1"
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</BasePage.Context.Provider>
	);
};

export default Overview;
