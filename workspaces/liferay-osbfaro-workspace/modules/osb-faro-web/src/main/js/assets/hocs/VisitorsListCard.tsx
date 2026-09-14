import Card from 'shared/components/Card';
import {AccountNames} from 'shared/components/table/cell-components';
import CardTabs, {CardTabSizes} from 'shared/components/CardTabs';
import ClayLink from '@clayui/link';
import getMetricsMapper from 'shared/hoc/mappers/metrics';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import React, {useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	accountsListColumns,
	metricsListColumns,
} from 'shared/util/table-columns';
import {
	compose,
	withBaseResults,
	withQueryPagination,
	withQueryRangeSelectors,
} from 'shared/hoc';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {getAssetListQueries} from './assetListQueries';
import {graphql} from '@apollo/client/react/hoc';
import {memoize} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {Routes} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

const TABS = [
	{
		tabId: 'accounts',
		title: Liferay.Language.get('accounts'),
	},
	{
		tabId: 'individuals',
		title: Liferay.Language.get('known-individuals'),
	},
];

/**
 * Built once per asset type and cached: the Apollo and pagination HOCs below
 * have to keep a stable component identity across renders.
 */
export const getVisitorsListCard = memoize((graphQLType: string) => {
	const {accountsMetric, accountsQuery, individualsMetric, individualsQuery} =
		getAssetListQueries(graphQLType);

	const withAccountsData = () =>
		graphql(
			accountsQuery,
			getMetricsMapper((result: any) => ({
				items: result[graphQLType][accountsMetric].accounts
					.accountNames,
				total: result[graphQLType][accountsMetric].accounts.total,
			}))
		);

	const withIndividualsData = () =>
		graphql(
			individualsQuery,
			getMetricsMapper((result: any) => ({
				items: result[graphQLType][individualsMetric].individuals
					.individuals,
				total: result[graphQLType][individualsMetric].individuals.total,
			}))
		);

	const AccountsTableWithData = withBaseResults(withAccountsData, {
		emptyIcon: {
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_satellite',
		},
		emptyTitle: Liferay.Language.get('no-accounts-were-found'),
		getColumns: ({
			router: {
				params: {channelId, groupId},
			},
		}: {
			router: {params: {channelId: string; groupId: string}};
		}) => [
			{
				...accountsListColumns.getName({channelId, groupId}),
				sortable: false,
			},
		],
		legacyDropdownRangeKey: false,
		rowIdentifier: 'id',
		showDropdownRangeKey: false,
	});

	const IndividualsTableWithData = withBaseResults(withIndividualsData, {
		emptyDescription: (
			<>
				<span className="mr-1">
					{Liferay.Language.get(
						'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
					)}
				</span>

				<ClayLink
					href={URLConstants.IndividualsDashboardDocumentation}
					key="DOCUMENTATION"
					target="_blank"
				>
					{Liferay.Language.get('learn-more-about-individuals')}
				</ClayLink>
			</>
		),
		emptyIcon: {
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_satellite',
		},
		emptyTitle: Liferay.Language.get('no-individuals-were-found'),
		getColumns: ({
			router: {
				params: {channelId, groupId},
			},
		}: {
			router: {params: {channelId: string; groupId: string}};
		}) => [
			metricsListColumns.getNameEmail({
				channelId,
				groupId,
				route: Routes.CONTACTS_INDIVIDUAL,
			}),

			{
				accessor: 'accountName',
				cellRenderer: AccountNames,
				className: 'table-cell-expand-small',
				label: Liferay.Language.get('account-name'),
				sortable: false,
			},
		],
		legacyDropdownRangeKey: false,
		rowIdentifier: 'id',
		showDropdownRangeKey: false,
	});

	const VisitorsListCard = ({
		rangeSelectors: initialRangeSelectors,
		...otherProps
	}: {
		rangeSelectors: RangeSelectors;
		[key: string]: unknown;
	}) => {
		const [activeTabId, setActiveTabId] = useState(TABS[0].tabId);
		const [rangeSelectors, setRangeSelectors] = useState<RangeSelectors>(
			initialRangeSelectors
		);

		const TableWithData =
			activeTabId === 'individuals'
				? IndividualsTableWithData
				: AccountsTableWithData;

		return (
			<Card className="visitors-list-root" pageDisplay>
				<Card.Header className="align-items-center d-flex justify-content-between">
					<Card.Title>{Liferay.Language.get('visitors')}</Card.Title>

					<DropdownRangeKey
						legacy={false}
						onRangeSelectorChange={setRangeSelectors}
						rangeSelectors={rangeSelectors}
					/>
				</Card.Header>

				<CardTabs
					activeTabId={activeTabId}
					className="mx-4 mb-2"
					onChange={setActiveTabId}
					size={CardTabSizes.Small}
					tabs={TABS}
				/>

				<TableWithData
					{...otherProps}
					onRangeSelectorsChange={setRangeSelectors}
					rangeSelectors={rangeSelectors}
				/>
			</Card>
		);
	};

	return compose<React.ComponentType<any>>(
		withQueryPagination({initialOrderIOMap: createOrderIOMap(NAME)}),
		withQueryRangeSelectors()
	)(VisitorsListCard);
});
