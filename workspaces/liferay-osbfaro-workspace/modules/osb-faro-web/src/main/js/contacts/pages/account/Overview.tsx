import * as API from 'shared/api';
import AccountInfoBar from './components/AccountInfoBar';
import AccountMetricsCard from './components/AccountMetricsCard';
import BasePage from 'shared/components/base-page';
import ClayLayout from '@clayui/layout';
import MostEngagedIndividuals from './components/MostEngagedIndividuals';
import React from 'react';
import TopAssets from './components/TopAssets';
import TopCategoriesAndTags from './components/TopCategoriesAndTags';
import TopPagesCard from './components/TopPagesCard';
import {
	AccountIndividualMetricType,
	IAccountIndividualMetric,
} from './utils/types';
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

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.accounts.fetchAccountIndividualMetrics,
		skipRequest: !id,
		variables: {accountId: id, channelId, groupId},
	});

	const accountMetrics = data as IAccountIndividualMetric[] | undefined;

	const getCount = (metricType: AccountIndividualMetricType) =>
		accountMetrics?.find(
			(accountMetric) => accountMetric.metricType === metricType
		)?.value;

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
				<ClayLayout.Col className="d-flex" lg={3} md={6}>
					<AccountMetricsCard
						error={error}
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-individuals'),
								singularLabel:
									Liferay.Language.get('x-individual'),
								value: getCount(
									AccountIndividualMetricType.TotalIndividuals
								),
							},
						]}
						refetch={refetch}
						title={Liferay.Language.get('total-individuals')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="d-flex" lg={3} md={6}>
					<AccountMetricsCard
						error={error}
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-known'),
								value: getCount(
									AccountIndividualMetricType.KnownIndividuals
								),
							},
							{
								label: Liferay.Language.get('x-anonymous'),
								value: getCount(
									AccountIndividualMetricType.AnonymousIndividuals
								),
							},
						]}
						refetch={refetch}
						title={Liferay.Language.get('identity-breakdown')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="d-flex" lg={3} md={6}>
					<AccountMetricsCard
						error={error}
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-returning'),
								value: getCount(
									AccountIndividualMetricType.ReturningIndividuals
								),
							},
							{
								label: Liferay.Language.get('x-first-time'),
								value: getCount(
									AccountIndividualMetricType.FirstTimeIndividuals
								),
							},
						]}
						refetch={refetch}
						title={Liferay.Language.get('engagement-status')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="d-flex" lg={3} md={6}>
					<AccountMetricsCard
						error={error}
						loading={loading}
						metrics={[
							{
								label: Liferay.Language.get('x-no-activity'),
								value: getCount(
									AccountIndividualMetricType.InactiveIndividuals
								),
							},
						]}
						refetch={refetch}
						title={Liferay.Language.get('inactive-users')}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col size={12}>
					<MostEngagedIndividuals />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<SectionHeader
				className="mb-3 mt-2"
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
