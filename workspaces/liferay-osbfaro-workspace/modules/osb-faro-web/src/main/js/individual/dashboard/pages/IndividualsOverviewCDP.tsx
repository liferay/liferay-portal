import * as API from 'shared/api';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import IndividualMetricsQuery from 'shared/queries/IndividualMetricsQuery';
import IndividualsList from './IndividualsList';
import Loading from 'shared/components/Loading';
import MetricCard from 'shared/components/MetricCard';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {Text as ClayText} from '@clayui/core';
import {CSVType} from 'shared/components/download-report/utils';
import {DownloadStaticCSVReport} from 'shared/components/download-report/DownloadStaticCSVReport';
import {INTERVAL_KEY_MAP} from 'shared/util/time';
import {isNil} from 'lodash';
import {Routes, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {toThousands} from 'shared/util/numbers';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';
import {useRequest} from 'shared/hooks/useRequest';

interface IIndividualsOverviewEmptyStateProps {
	authorized: boolean;
	data: {total?: number} | null;
	groupId: string;
	loading: boolean;
}

const renderIndividualsValue = (value?: number) => {
	const rawValue = value || 0;

	return sub(
		rawValue === 1
			? Liferay.Language.get('x-individual')
			: Liferay.Language.get('x-individuals'),
		[toThousands(rawValue)]
	) as string;
};

const renderTrendLabel = (percentageNode: React.ReactNode) =>
	sub(Liferay.Language.get('x-vs-last-30-days'), [percentageNode], false);

const IndividualsOverviewEmptyState: React.FC<
	IIndividualsOverviewEmptyStateProps
> = ({authorized, data, groupId, loading}) => {
	if (loading) {
		return <Loading key="LOADING" />;
	}

	if (isNil(data?.total) || data?.total === 0) {
		return (
			<Card pageDisplay>
				<NoResultsDisplay
					description={
						<>
							{authorized
								? Liferay.Language.get(
										'connect-a-data-source-to-get-started'
									)
								: Liferay.Language.get(
										'contact-an-administrator-to-connect-a-data-source-to-get-started'
									)}

							<ClayLink
								className="d-block"
								decoration="underline"
								href={URLConstants.HelpConnectDxp}
								target="_blank"
							>
								<ClayText size={4}>
									{Liferay.Language.get(
										'learn-more-about-data-sources'
									)}
								</ClayText>

								<ClayIcon
									aria-label={Liferay.Language.get(
										'learn-more-about-data-sources'
									)}
									className="ml-1"
									fontSize={12}
									symbol="shortcut"
								/>
							</ClayLink>
						</>
					}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite',
					}}
					primary
					title={Liferay.Language.get('no-data-source-synced')}
				>
					{authorized ? (
						<ClayLink
							button
							className="button-root"
							displayType="primary"
							href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
								groupId,
							})}
						>
							{Liferay.Language.get('connect-data-source')}
						</ClayLink>
					) : undefined}
				</NoResultsDisplay>
			</Card>
		);
	}

	return null;
};

const IndividualsOverviewCDP = () => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const currentUser = useCurrentUser();

	const dataSourceStates = useDataSources();

	const authorized = currentUser.isAdmin();

	const {data: dataSourceData, loading: dataSourceLoading} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta: 1,
			groupId,
		},
	});
	const {data, loading} = useQuery(IndividualMetricsQuery, {
		fetchPolicy: 'network-only',
		variables: {
			channelId,
			interval: INTERVAL_KEY_MAP.week,
			rangeKey: 30,
		},
	});

	return (
		<>
			<BasePage.SubHeader>
				<div className="d-flex justify-content-end w-100">
					<DownloadStaticCSVReport
						disabled={!!dataSourceStates.empty}
						type={CSVType.Individual}
						typeLang={Liferay.Language.get('individuals')}
					/>
				</div>
			</BasePage.SubHeader>

			<BasePage.Body pageContainer>
				<IndividualsOverviewEmptyState
					authorized={authorized}
					data={dataSourceData}
					groupId={groupId}
					loading={dataSourceLoading}
				/>

				{dataSourceData?.total > 0 && (
					<>
						<ClayLayout.Row>
							<ClayLayout.Col lg={4} md={12}>
								<MetricCard
									description={Liferay.Language.get(
										'this-is-the-total-number-of-individuals,-including-both-known-individuals-and-anonymous-individuals'
									)}
									loading={loading}
									minHeight={198}
									title={Liferay.Language.get(
										'total-individuals'
									)}
									value={renderIndividualsValue(
										data?.individualMetric
											?.totalIndividualsMetric?.value
									)}
								/>
							</ClayLayout.Col>

							<ClayLayout.Col lg={4} sm={12}>
								<MetricCard
									description={Liferay.Language.get(
										'this-is-the-total-number-of-known-individuals.-an-individual-is-considered-known-if-we-have-any-identifiable-information-about-the-individual'
									)}
									loading={loading}
									minHeight={198}
									renderTrendLabel={renderTrendLabel}
									title={Liferay.Language.get(
										'known-individuals'
									)}
									trend={
										data?.individualMetric
											?.knownIndividualsMetric?.trend
									}
									value={renderIndividualsValue(
										data?.individualMetric
											?.knownIndividualsMetric?.value
									)}
								/>
							</ClayLayout.Col>

							<ClayLayout.Col lg={4} sm={12}>
								<MetricCard
									description={Liferay.Language.get(
										'this-is-the-total-number-of-anonymous-individuals.-anonymous-individuals-are-removed-after-30-days-of-inactivity'
									)}
									loading={loading}
									minHeight={198}
									renderTrendLabel={renderTrendLabel}
									title={Liferay.Language.get(
										'anonymous-individuals'
									)}
									trend={
										data?.individualMetric
											?.anonymousIndividualsMetric?.trend
									}
									value={renderIndividualsValue(
										data?.individualMetric
											?.anonymousIndividualsMetric?.value
									)}
								/>
							</ClayLayout.Col>
						</ClayLayout.Row>

						<SectionHeader
							icon="box-container"
							title={Liferay.Language.get('individuals')}
						/>

						<IndividualsList />
					</>
				)}
			</BasePage.Body>
		</>
	);
};

export default IndividualsOverviewCDP;
