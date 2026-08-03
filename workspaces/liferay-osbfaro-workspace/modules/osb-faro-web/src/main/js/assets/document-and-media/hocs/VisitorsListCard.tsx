import Card from 'shared/components/Card';
import {AccountNames} from 'shared/components/table/cell-components';
import CardTabs, {CardTabSizes} from 'shared/components/CardTabs';
import ClayLink from '@clayui/link';
import getMetricsMapper from 'shared/hoc/mappers/metrics';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import knownAccountsListAssetQuery from 'shared/queries/knownAccountsListAssetQuery';
import knownIndividualsListAssetQuery from 'shared/queries/knownIndividualsListAssetQuery';
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
import {createOrderIOMap, DOWNLOADS_METRIC, NAME} from 'shared/util/pagination';
import {graphql} from '@apollo/client/react/hoc';
import {RangeSelectors} from 'shared/types';
import {Routes} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

const withAccountsData = () =>
	graphql(
		knownAccountsListAssetQuery('document', DOWNLOADS_METRIC),
		getMetricsMapper((result) => ({
			items: result.document.downloadsMetric.accounts.accountNames,
			total: result.document.downloadsMetric.accounts.total,
		}))
	);

const withIndividualsData = () =>
	graphql(
		knownIndividualsListAssetQuery('document', DOWNLOADS_METRIC),
		getMetricsMapper((result) => ({
			items: result.document.downloadsMetric.individuals.individuals,
			total: result.document.downloadsMetric.individuals.total,
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
	}: any) => [
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
	}: any) => [
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

const VisitorsListCard = ({
	rangeSelectors: initialRangeSelectors,
	...otherProps
}: any) => {
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

export default compose<React.ComponentType<any>>(
	withQueryPagination({initialOrderIOMap: createOrderIOMap(NAME)}),
	withQueryRangeSelectors()
)(VisitorsListCard);
