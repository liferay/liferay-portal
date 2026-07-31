import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import ClayTabs from '@clayui/tabs';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import React, {useContext} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import Table from 'shared/components/table';
import URLConstants from 'shared/util/url-constants';
import {ApolloError} from '@apollo/client';
import {ENTRANCES_METRIC, EXIT_RATE_METRIC} from 'shared/util/pagination';
import {toRounded} from 'shared/util/numbers';
import {metricsListColumns} from 'shared/util/table-columns';
import {NameCell} from 'shared/components/table/cell-components';
import {OrderByDirections} from 'shared/util/constants';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';

const ROW_IDENTIFIER = ['assetId', 'assetTitle'];

const getAssetTitleColumn = ({
	channelId,
	groupId,
	routeQueries,
}: {
	channelId?: string;
	groupId?: string;
	routeQueries: {[key: string]: any};
}) => ({
	cellRenderer: NameCell,
	cellRendererProps: {
		nameKey: 'assetTitle',
		renderSecondaryInfo: ({assetId}: {assetId: string}) => assetId,
		routeFn: ({data: {assetId, assetTitle}}: {data: ITopPagesCardItem}) => {
			if (!assetId) {
				return undefined;
			}

			const params = {
				channelId,
				groupId,
				touchpoint: encodeURIComponent(assetId),
			};

			return setUriQueryValues(
				routeQueries,
				toRoute(
					Routes.SITES_TOUCHPOINTS_OVERVIEW,
					assetTitle
						? {...params, title: encodeURIComponent(assetTitle)}
						: params
				)
			);
		},
	},
	className: 'table-cell-expand',
	label: `${Liferay.Language.get('page-title')}
			|
			${Liferay.Language.get('canonical-url')}`,
	sortable: false,
});

const DEFAULT_METRIC_COLUMN = {
	sortable: false,
	title: true,
};

export const TOP_PAGES_TABS = [
	{
		metricColumn: metricsListColumns.visitorsMetric,
		rowIdentifier: ROW_IDENTIFIER,
		tabId: 'visitorsMetric',
		title: Liferay.Language.get('visited-pages'),
	},
	{
		metricColumn: metricsListColumns.entrancesMetric,
		rowIdentifier: ROW_IDENTIFIER,
		tabId: ENTRANCES_METRIC,
		title: Liferay.Language.get('entrance-pages'),
	},
	{
		metricColumn: {
			...metricsListColumns.exitRateMetric,
			dataFormatter: (data: number) => {
				const percent = data * 100;

				return isFinite(percent) ? `${toRounded(percent)}%` : '-';
			},
		},
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

	/**
	 * Replaces the default empty state, which points at the Site Dashboard
	 * documentation and only makes sense there.
	 */
	emptyState?: React.ReactElement;
	error?: ApolloError;
	footer?: {
		label: string;
		href: string;
	};
	items: ITopPagesCardItem[];
	loading?: boolean;
	onActiveTabIdChange: (tabId: string) => void;
	rangeSelectors: RangeSelectors;
	routeQueries?: {[key: string]: any};
}

const TopPagesCardContent: React.FC<ITopPagesCardContentProps> = ({
	activeTabId,
	empty,
	emptyState,
	error,
	footer,
	items,
	loading,
	onActiveTabIdChange,
	rangeSelectors,
	routeQueries = {},
}) => {
	const {
		router: {
			params: {channelId, groupId},
		},
	} = useContext(BasePage.Context);

	const activeIndex = Math.max(
		TOP_PAGES_TABS.findIndex(({tabId}) => tabId === activeTabId),
		0
	);

	const {metricColumn, rowIdentifier, tabId} = TOP_PAGES_TABS[activeIndex];

	const columns = [
		getAssetTitleColumn({channelId, groupId, routeQueries}),
		{
			...DEFAULT_METRIC_COLUMN,
			...metricColumn,
			accessor: `${tabId}.value`,
		},
	];

	const footerHref =
		footer &&
		setUriQueryValues(
			pickBy({
				...rangeSelectors,
				field: activeTabId,
				sortOrder: OrderByDirections.Descending,
			}),
			footer.href
		);

	return (
		<>
			<Card.Body
				className="w-100 d-flex flex-column flex-grow-1"
				noPadding
			>
				<ClayTabs
					active={activeIndex}
					className="mb-3"
					onActiveChange={(index) =>
						onActiveTabIdChange(TOP_PAGES_TABS[Number(index)].tabId)
					}
				>
					{TOP_PAGES_TABS.map(({tabId, title}) => (
						<ClayTabs.Item key={tabId}>{title}</ClayTabs.Item>
					))}
				</ClayTabs>

				<TopPagesCardWithStatesRenderer
					empty={empty}
					emptyState={emptyState}
					error={error}
					loading={loading}
				>
					<Table
						className="flex-grow-1 table-hover"
						columns={columns as any}
						items={items}
						rowIdentifier={rowIdentifier}
					/>
				</TopPagesCardWithStatesRenderer>
			</Card.Body>

			{footer && !!Object.keys(footer).length && (
				<Card.Footer className="d-flex">
					<ClayLink
						aria-label={Liferay.Language.get('view-all-pages')}
						borderless
						button
						className="ml-auto rounded-lg"
						displayType="primary"
						href={footerHref}
						small
					>
						{footer.label}
					</ClayLink>
				</Card.Footer>
			)}
		</>
	);
};

interface ITopPagesCardWithStatesRendererProps
	extends React.HTMLAttributes<HTMLElement> {
	empty?: boolean;
	emptyState?: React.ReactElement;
	error?: ApolloError;
	loading?: boolean;
}

const TopPagesCardWithStatesRenderer: React.FC<
	ITopPagesCardWithStatesRendererProps
> = ({children, empty, emptyState, error, loading}) => (
	<StatesRenderer empty={empty} error={!!error} loading={loading}>
		<StatesRenderer.Loading spacer />
		{emptyState ? (
			<StatesRenderer.Empty>{emptyState}</StatesRenderer.Empty>
		) : (
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
		)}
		<StatesRenderer.Error apolloError={error}>
			<ErrorDisplay />
		</StatesRenderer.Error>
		<StatesRenderer.Success>{children}</StatesRenderer.Success>
	</StatesRenderer>
);

export default TopPagesCardContent;
