import * as API from 'shared/api';
import Card from 'shared/components/Card';
import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
	rangeSelectors,
} from 'shared/components/FrontendDataSet';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useRequest} from 'shared/hooks/useRequest';

interface IAccountsDataSetProps {
	accountLifecycleId?: string;
	apiURL: string;
	channelId: string;
	countryFilter?: string;
	dataSetId?: string;
	groupId: string;
	industryFilter?: string;
	lifecycleStageFilter?: LifecycleStages;
	rangeKeyFilter?: RangeKeyTimeRanges;
	segmentFilter?: string;
	segmentName?: string;
	stageSelectionNonce?: number;
}

interface ILifecycleStageFieldValue {
	id: string;
	stageType: LifecycleStages;
}

const buildSelectionPreloadedData = (value?: string, label?: string) =>
	value
		? {
				exclude: false,
				selectedItems: [{label: label ?? value, value}],
			}
		: undefined;

const AccountsDataSet: React.FC<IAccountsDataSetProps> = ({
	accountLifecycleId,
	apiURL,
	channelId,
	countryFilter,
	dataSetId = 'accounts-list-dataset',
	groupId,
	industryFilter,
	lifecycleStageFilter,
	rangeKeyFilter,
	segmentFilter,
	segmentName,
	stageSelectionNonce,
}) => {
	const {data: lifecycleStageFieldValues} = useRequest({
		dataSourceFn: API.accounts.fetchLifecycleStageFieldValues,
		skipRequest: !accountLifecycleId,
		variables: {
			accountLifecycleId,
			channelId,
			groupId,
		},
	});

	const lifecycleStages: ILifecycleStageFieldValue[] =
		lifecycleStageFieldValues?.items ?? [];

	const lifecycleStageItems = lifecycleStages.map(({id, stageType}) => ({
		label: lifecycleStagesLabelMap[stageType].label,
		value: id,
	}));

	const preloadedRangeSelector = rangeSelectors.find(
		({value}) => value === rangeKeyFilter
	);

	const preloadedLifecycleStage = lifecycleStageFilter
		? lifecycleStages.find(
				({stageType}) => stageType === lifecycleStageFilter
			)
		: undefined;

	return (
		<Card minHeight={300}>
			<FrontendDataSet
				apiURL={apiURL}
				customDataRenderers={{
					accountLifecycleStageRenderer: ({
						value,
					}: {
						value: LifecycleStages;
					}) =>
						value &&
						columns.cmsLabelRenderer({
							displayType:
								lifecycleStagesLabelMap[value].displayType,
							label: lifecycleStagesLabelMap[value].label,
						}),
					accountNameRenderer: ({
						itemData,
						value,
					}: {
						itemData: {id: string | number};
						value: string;
					}) =>
						columns.nameAndLinkRenderer({
							channelId,
							groupId,
							itemData,
							route: Routes.CONTACTS_ACCOUNT,
							value,
						}),
					activitiesCountRenderer: ({value}: {value?: number}) => (
						<div>{toThousands(value ?? 0)}</div>
					),
					annualRevenueRenderer: ({value}: {value: number}) => (
						<div>{toThousands(value)}</div>
					),
					dateRenderer: ({value}: {value: string}) =>
						columns.dateRenderer({itemData: {}, value}),
				}}
				emptyState={{
					description: Liferay.Language.get(
						'no-accounts-were-synced-from-the-connected-data-sources'
					),
					image: '/states/satellite.svg',
					title: Liferay.Language.get('no-accounts-found'),
				}}
				filters={[
					{
						id: 'rangeKey',
						items: rangeSelectors,
						label: Liferay.Language.get('active-individuals'),
						name: 'rangeKey',
						preloadedData: buildSelectionPreloadedData(
							rangeKeyFilter,
							preloadedRangeSelector?.label
						),
						type: 'selection',
					},
					...(accountLifecycleId
						? [
								{
									id: 'lifecycleStatus',
									items: lifecycleStageItems,
									label: Liferay.Language.get('status'),
									name: 'status',
									preloadedData: buildSelectionPreloadedData(
										preloadedLifecycleStage?.id,
										lifecycleStageFilter
											? lifecycleStagesLabelMap[
													lifecycleStageFilter
												].label
											: undefined
									),
									type: 'selection' as const,
								},
							]
						: []),
					{
						apiURL: `/o/faro/contacts/${groupId}/account/fds_field_values?channelId=${channelId}&fieldMappingFieldName=industry`,
						entityFieldType: 'string',
						id: 'industry',
						itemKey: 'name',
						itemLabel: 'name',
						label: Liferay.Language.get('industry'),
						multiple: true,
						preloadedData:
							buildSelectionPreloadedData(industryFilter),
						type: 'selection',
					},
					{
						apiURL: `/o/faro/contacts/${groupId}/account/fds_field_values?channelId=${channelId}&fieldMappingFieldName=country`,
						entityFieldType: 'string',
						id: 'country',
						itemKey: 'name',
						itemLabel: 'name',
						label: Liferay.Language.get('country'),
						multiple: true,
						preloadedData:
							buildSelectionPreloadedData(countryFilter),
						type: 'selection',
					},
					{
						apiURL: `/o/faro/contacts/${groupId}/individual_segment?channelId=${channelId}`,
						entityFieldType: 'string',
						id: 'segmentId',
						itemKey: 'id',
						itemLabel: 'name',
						label: Liferay.Language.get('segment'),
						preloadedData: buildSelectionPreloadedData(
							segmentFilter,
							segmentName
						),
						type: 'selection',
					},
				]}
				id={dataSetId}
				key={[
					countryFilter,
					industryFilter,
					lifecycleStageFilter,
					segmentFilter,
					stageSelectionNonce,
					lifecycleStages.length,
				].join()}
				pagination={pagination}
				showPagination
				snapshotsEnabled
				sorts={[
					{
						active: true,
						direction: 'desc',
						key: 'lastActive',
						label: Liferay.Language.get('last-active'),
					},
				]}
				views={[
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
									label: Liferay.Language.get('account'),
									sortable: true,
									truncate: true,
								},
								{
									fieldName: 'industry',
									label: Liferay.Language.get('industry'),
									sortable: true,
								},
								{
									contentRenderer:
										'accountLifecycleStageRenderer',
									fieldName: 'lifecycleStage',
									label: Liferay.Language.get(
										'lifecycle-stage'
									),
									sortable: true,
								},
								{
									contentRenderer: 'annualRevenueRenderer',
									fieldName: 'annualRevenue',
									label: Liferay.Language.get(
										'annual-revenue'
									),
									sortable: true,
								},
								{
									fieldName: 'country',
									label: Liferay.Language.get('country'),
									sortable: true,
								},
								{
									contentRenderer: 'dateRenderer',
									fieldName: 'firstActive',
									label: Liferay.Language.get('first-active'),
									sortable: true,
								},
								{
									contentRenderer: 'dateRenderer',
									fieldName: 'lastActive',
									label: Liferay.Language.get('last-active'),
									sortable: true,
								},
								{
									contentRenderer: 'activitiesCountRenderer',
									fieldName: 'activitiesCount',
									label: Liferay.Language.get(
										'recent-activities'
									),
									sortable: true,
								},
								{
									contentRenderer: 'dateRenderer',
									fieldName: 'lastEnriched',
									label: Liferay.Language.get(
										'last-enriched'
									),
									sortable: true,
								},
							],
						},
						thumbnail: 'table',
					},
				]}
			/>
		</Card>
	);
};

export default AccountsDataSet;
