import BaseCard from 'shared/components/base-card';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import CardTabs from 'shared/components/CardTabs';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import React, {useContext, useState} from 'react';
import SitesTopPagesQuery, {
	SitesTopPagesQueryData,
	SitesTopPagesQueryVariables,
} from 'shared/queries/SitesTopPagesQuery';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import Table from 'shared/components/table';
import URLConstants from 'shared/util/url-constants';
import {ApolloError, useQuery} from '@apollo/client';

import {ENTRANCES_METRIC, EXIT_RATE_METRIC} from 'shared/util/pagination';
import {getSafeRangeSelectors} from 'shared/util/util';
import {metricsListColumns} from 'shared/util/table-columns';
import {NameCell} from 'shared/components/table/cell-components';
import {OrderByDirections} from 'shared/util/constants';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {setUriQueryValues} from 'shared/util/router';

const ROW_IDENTIFIER = ['assetId', 'assetTitle'];

const ASSET_TITLE_COLUMN = {
	cellRenderer: NameCell,
	cellRendererProps: {
		nameKey: 'assetTitle',
		renderSecondaryInfo: ({assetId}: {assetId: string}) => assetId,
	},
	className: 'table-cell-expand',
	label: `${Liferay.Language.get('page-title')}
			|
			${Liferay.Language.get('canonical-url')}`,
	sortable: false,
};

const DEFAULT_METRIC_COLUMN = {
	sortable: false,
	title: true,
};

const tabs = [
	{
		getColumns: () => [
			ASSET_TITLE_COLUMN,
			{
				...DEFAULT_METRIC_COLUMN,
				...metricsListColumns.visitorsMetric,
				accessor: 'visitorsMetric.value',
			},
		],
		rowIdentifier: ROW_IDENTIFIER,
		tabId: 'visitorsMetric',
		title: Liferay.Language.get('visited-pages'),
	},
	{
		getColumns: () => [
			ASSET_TITLE_COLUMN,
			{
				...DEFAULT_METRIC_COLUMN,
				...metricsListColumns.entrancesMetric,
				accessor: 'entrancesMetric.value',
			},
		],
		rowIdentifier: ROW_IDENTIFIER,
		tabId: ENTRANCES_METRIC,
		title: Liferay.Language.get('entrance-pages'),
	},
	{
		getColumns: () => [
			ASSET_TITLE_COLUMN,
			{
				...DEFAULT_METRIC_COLUMN,
				...metricsListColumns.exitRateMetric,
				accessor: 'exitRateMetric.value',
			},
		],
		rowIdentifier: ROW_IDENTIFIER,
		tabId: EXIT_RATE_METRIC,
		title: Liferay.Language.get('exit-pages'),
	},
];

interface ITopPagesCardProps extends React.HTMLAttributes<HTMLElement> {
	footer: {
		label: string;
		href: string;
	};
	label: string;
	legacyDropdownRangeKey?: boolean;
}

const TopPagesCard: React.FC<ITopPagesCardProps> = ({
	className,
	footer,
	label,
	legacyDropdownRangeKey,
}) => (
	<BaseCard
		className={className}
		label={label}
		legacyDropdownRangeKey={legacyDropdownRangeKey ?? true}
		reportContainer={ReportContainer.TopPagesCard}
	>
		{({rangeSelectors}) => (
			<TopPagesCardWithData
				footer={footer}
				rangeSelectors={rangeSelectors}
			/>
		)}
	</BaseCard>
);

interface ITopPageCardWithData extends Partial<ITopPagesCardProps> {
	rangeSelectors: RangeSelectors;
}

const TopPagesCardWithData: React.FC<ITopPageCardWithData> = ({
	footer,
	rangeSelectors,
}) => {
	const [activeTabId, setActiveTabId] = useState(tabs[0].tabId);
	const {
		accountId,
		router: {
			params: {channelId},
		},
	} = useContext(BasePage.Context);
	const {
		data,
		error,
		loading = false,
	} = useQuery<SitesTopPagesQueryData, SitesTopPagesQueryVariables>(
		SitesTopPagesQuery,
		{
			variables: {
				...getSafeRangeSelectors(rangeSelectors),
				accountId,
				channelId,
				size: 5,
				sort: {
					column: activeTabId,
					type: OrderByDirections.Descending,
				},
				start: 0,
			},
		}
	);

	const activeTab = tabs.find(({tabId}) => tabId === activeTabId) ?? tabs[0];
	const {getColumns, rowIdentifier} = activeTab;

	return (
		<>
			<Card.Body
				className="w-100 d-flex flex-column flex-grow-1"
				noPadding
			>
				<CardTabs
					activeTabId={activeTabId}
					onChange={(tabId) => setActiveTabId(tabId)}
					tabs={tabs.map(({tabId, title}) => ({tabId, title}))}
				/>

				<TopPagesCardWithStatesRenderer
					empty={!data?.pages.total}
					error={error}
					loading={loading}
				>
					<Table
						className="flex-grow-1 table-hover"
						columns={getColumns() as any}
						items={data?.pages.assetMetrics ?? []}
						rowIdentifier={rowIdentifier}
					/>
				</TopPagesCardWithStatesRenderer>
			</Card.Body>

			{footer && !!Object.keys(footer).length && (
				<Card.Footer>
					<ClayLink
						borderless
						button
						className="button-root"
						displayType="secondary"
						href={setUriQueryValues(
							pickBy({
								...rangeSelectors,
								field: activeTabId,
								sortOrder: OrderByDirections.Descending,
							}),
							footer.href
						)}
						small
					>
						{footer.label}

						<ClayIcon
							className="icon-root ml-2"
							symbol="angle-right-small"
						/>
					</ClayLink>
				</Card.Footer>
			)}
		</>
	);
};

interface ITopPagesCardWithStatesRendererProps
	extends React.HTMLAttributes<HTMLElement> {
	empty?: boolean;
	error?: ApolloError;
	loading?: boolean;
}

const TopPagesCardWithStatesRenderer: React.FC<
	ITopPagesCardWithStatesRendererProps
> = ({children, empty, error, loading}) => (
	<StatesRenderer empty={empty} error={!!error} loading={loading}>
		<StatesRenderer.Loading />
		<StatesRenderer.Empty
			description={
				<>
					<span className="mr-1">
						{Liferay.Language.get(
							'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
						)}
					</span>

					<ClayLink
						href={URLConstants.SitesDashboardTopPages}
						key="DOCUMENTATION"
						target="_blank"
					>
						{Liferay.Language.get('learn-more-about-pages')}
					</ClayLink>
				</>
			}
			showIcon={false}
			title={Liferay.Language.get(
				'there-are-no-visitors-on-the-selected-period'
			)}
		/>
		<StatesRenderer.Error apolloError={error}>
			<ErrorDisplay />
		</StatesRenderer.Error>
		<StatesRenderer.Success>{children}</StatesRenderer.Success>
	</StatesRenderer>
);

export default TopPagesCard;
