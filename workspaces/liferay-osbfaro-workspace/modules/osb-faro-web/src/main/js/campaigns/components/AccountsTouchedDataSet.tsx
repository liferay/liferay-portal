import DataValue from 'shared/components/DataValue';
import Loading from 'shared/components/Loading';
import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {omit, pickBy} from 'lodash';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes, setUriQueryValues} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {ILifecycleDetail} from 'shared/api/lifecycle';

interface IAccountsTouchedDataSetProps {
	campaignId: string;
	channelId: string;
	groupId: string;
	lifecycles: ILifecycleDetail[] | null;
	loading: boolean;
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
					contentRenderer: 'accountNameRenderer',
					fieldName: 'accountName',
					label: Liferay.Language.get('account-name'),
					sortable: true,
					truncate: true,
				},
				{
					contentRenderer: 'lifecycleStageRenderer',
					fieldName: 'lifecycleStage',
					label: Liferay.Language.get('lifecycle-stage'),
					sortable: true,
				},
				{
					contentRenderer: 'amountRenderer',
					fieldName: 'salesforce/openPipelineAmount',
					label: Liferay.Language.get('pipeline-value'),
					sortable: true,
				},
				{
					contentRenderer: 'amountRenderer',
					fieldName: 'salesforce/closedWonAmount',
					label: Liferay.Language.get('closed-won'),
					sortable: true,
				},
			],
		},
		thumbnail: 'table',
	},
];

const AccountsTouchedDataSet: React.FC<IAccountsTouchedDataSetProps> = ({
	campaignId,
	channelId,
	groupId,
	lifecycles,
	loading,
}) => {
	const rangeQueryValues = pickBy(useQueryRangeSelectors());

	const apiRangeQueryValues =
		rangeQueryValues.rangeKey === RangeKeyTimeRanges.CustomRange
			? omit(rangeQueryValues, 'rangeKey')
			: rangeQueryValues;

	if (loading) {
		return <Loading spacer />;
	}

	return (
		<FrontendDataSet
			apiURL={setUriQueryValues(
				pickBy({
					accountLifecycleId: lifecycles?.[0]?.id,
					channelId,
					...apiRangeQueryValues,
				}),
				`/o/faro/contacts/${groupId}/campaigns/${campaignId}/accounts`
			)}
			customDataRenderers={{
				accountNameRenderer: ({
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
						route: Routes.CONTACTS_ACCOUNT_OVERVIEW,
						value,
					}),

				amountRenderer: ({value}: {value?: number | string | null}) => {
					const amount =
						value === '' || value === null || value === undefined
							? NaN
							: Number(value);

					return (
						<div>
							{Number.isFinite(amount) ? toThousands(amount) : ''}
						</div>
					);
				},
				lifecycleStageRenderer: ({value}: {value: LifecycleStages}) => (
					<DataValue
						format={(stage: LifecycleStages) =>
							columns.cmsLabelRenderer({
								displayType:
									lifecycleStagesLabelMap[stage].displayType,
								label: lifecycleStagesLabelMap[stage].label,
							})
						}
						value={value}
					/>
				),
			}}
			id="campaign-accounts-dataset"
			pagination={pagination}
			showPagination
			sorts={[
				{
					active: true,
					default: true,
					direction: 'desc',
					key: 'salesforce/openPipelineAmount',
					label: Liferay.Language.get('pipeline-value'),
				},
			]}
			views={views}
		/>
	);
};

export default AccountsTouchedDataSet;
