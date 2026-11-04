import Card from 'shared/components/Card';
import React from 'react';
import {buildSelectionPreloadedData} from 'shared/components/accounts-data-set/AccountsDataSet';
import {
	columns,
	EMPTY_STATE_IMAGE,
	EMPTY_STATE_REDUCED_MOTION_IMAGE,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {omit, pickBy} from 'lodash';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes, setUriQueryValues} from 'shared/util/router';
import {useAccountFilter} from 'shared/hooks/useAccountFilter';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

interface ICampaignsDataSetProps {
	channelId: string;
	groupId: string;
}

const views = [
	{
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('default-view'),
		name: 'table',
		schema: {
			fields: [
				{
					contentRenderer: 'campaignNameRenderer',
					fieldName: 'campaignName',
					label: Liferay.Language.get('campaign-name'),
					sortable: true,
					truncate: true,
				},
				{
					contentRenderer: 'countRenderer',
					fieldName: 'accountsTouched',
					label: Liferay.Language.get('accounts-touched'),
					sortable: true,
				},
				{
					contentRenderer: 'countRenderer',
					fieldName: 'individualsTouched',
					label: Liferay.Language.get('individuals-touched'),
					sortable: true,
				},
			],
		},
		thumbnail: 'table',
	},
];

const CampaignsDataSet: React.FC<ICampaignsDataSetProps> = ({
	channelId,
	groupId,
}) => {
	const {accountId, accountName, accountQueryValues} = useAccountFilter();
	const rangeSelectors = useQueryRangeSelectors();

	const rangeQueryValues = pickBy(rangeSelectors);

	// The link carries the range, `CUSTOM` included, and the account.

	const linkQueryValues = {...rangeQueryValues, ...accountQueryValues};

	// `CUSTOM` is a client side sentinel the API answers with a 500, so a
	// custom range travels as its bounds alone.

	const apiRangeQueryValues =
		rangeQueryValues.rangeKey === RangeKeyTimeRanges.CustomRange
			? omit(rangeQueryValues, 'rangeKey')
			: rangeQueryValues;

	return (
		<Card minHeight={300}>
			<FrontendDataSet
				apiURL={setUriQueryValues(
					{channelId, ...apiRangeQueryValues},
					`/o/faro/contacts/${groupId}/campaigns`
				)}
				customDataRenderers={{
					campaignNameRenderer: ({
						itemData,
						value,
					}: {
						itemData: {id: string};
						value: string;
					}) =>
						columns.nameAndLinkRenderer({
							channelId,
							groupId,
							itemData,
							queryValues: linkQueryValues,
							route: Routes.CAMPAIGNS_DETAIL,
							value,
						}),
					countRenderer: columns.countRenderer,
				}}
				emptyState={{
					description: Liferay.Language.get(
						'no-campaigns-were-synced-from-the-connected-data-sources'
					),
					image: EMPTY_STATE_IMAGE,
					imageReducedMotion: EMPTY_STATE_REDUCED_MOTION_IMAGE,
					title: Liferay.Language.get('no-campaigns-found'),
				}}
				filters={[
					{
						apiURL: `/o/faro/contacts/${groupId}/account/account-names?channelId=${channelId}`,
						entityFieldType: 'string',
						id: 'accountId',
						itemKey: 'id',
						itemLabel: 'name',
						label: Liferay.Language.get('account'),
						preloadedData: buildSelectionPreloadedData(
							accountId,
							accountName
						),
						type: 'selection',
					},
				]}
				id="campaigns-list-dataset"

				// `filters` is read only on mount: remount to seed it again.

				key={accountId}
				pagination={pagination}
				showPagination
				sorts={[
					{
						active: true,
						default: true,
						direction: 'desc',
						key: 'accountsTouched',
						label: Liferay.Language.get('accounts-touched'),
					},
				]}
				views={views}
			/>
		</Card>
	);
};

export default CampaignsDataSet;
