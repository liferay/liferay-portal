import Card from 'shared/components/Card';
import CardTabs from 'shared/components/CardTabs';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import Table from 'shared/components/table';
import URLConstants from 'shared/util/url-constants';
import {ApolloError} from '@apollo/client';
import {ENTRANCES_METRIC, EXIT_RATE_METRIC} from 'shared/util/pagination';
import {metricsListColumns} from 'shared/util/table-columns';
import {NameCell} from 'shared/components/table/cell-components';
import {OrderByDirections} from 'shared/util/constants';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
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

export const TOP_PAGES_TABS = [
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

export interface ITopPagesCardItem {
	assetId: string;
	assetTitle: string;
	entrancesMetric: {
		value: number;
	};
	exitRateMetric: {
		value: number;
	};
	visitorsMetric: {
		value: number;
	};
}

interface ITopPagesCardContentProps {
	activeTabId: string;
	empty?: boolean;
	error?: ApolloError;
	footer?: {
		label: string;
		href: string;
	};
	items: ITopPagesCardItem[];
	loading?: boolean;
	onActiveTabIdChange: (tabId: string) => void;
	rangeSelectors: RangeSelectors;
}

const TopPagesCardContent: React.FC<ITopPagesCardContentProps> = ({
	activeTabId,
	empty,
	error,
	footer,
	items,
	loading,
	onActiveTabIdChange,
	rangeSelectors,
}) => {
	const activeTab =
		TOP_PAGES_TABS.find(({tabId}) => tabId === activeTabId) ??
		TOP_PAGES_TABS[0];

	const {getColumns, rowIdentifier} = activeTab;

	return (
		<>
			<Card.Body
				className="w-100 d-flex flex-column flex-grow-1"
				noPadding
			>
				<CardTabs
					activeTabId={activeTabId}
					onChange={(tabId) => onActiveTabIdChange(tabId)}
					tabs={TOP_PAGES_TABS.map(({tabId, title}) => ({
						tabId,
						title,
					}))}
				/>

				<TopPagesCardWithStatesRenderer
					empty={empty}
					error={error}
					loading={loading}
				>
					<Table
						className="flex-grow-1 table-hover"
						columns={getColumns() as any}
						items={items}
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

export default TopPagesCardContent;
