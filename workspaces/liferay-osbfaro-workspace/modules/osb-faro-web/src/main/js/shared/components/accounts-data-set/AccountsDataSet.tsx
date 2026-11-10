import Card from 'shared/components/Card';
import DataValue from 'shared/components/DataValue';
import Loading from 'shared/components/Loading';
import React from 'react';
import {
	columns,
	EMPTY_STATE_IMAGE,
	EMPTY_STATE_REDUCED_MOTION_IMAGE,
	FrontendDataSet,
	pagination,
	rangeSelectors,
} from 'shared/components/FrontendDataSet';
import {
	isLifecycleStage,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {buildViews} from './utils';
import {ICatalogField} from 'shared/api/catalog';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes} from 'shared/util/router';
import {useLifecycleStageFilterItems} from 'shared/hooks/useLifecycleStageFilterItems';

interface IAccountsDataSetProps {
	accountLifecycleId?: string;
	allLifecycles?: boolean;
	apiURL: string;
	channelId: string;
	countryFilter?: string;
	dataSetId?: string;
	fieldCatalog?: ICatalogField[];
	groupId: string;
	industryFilter?: string;
	lifecycleStageFilter?: string;
	rangeKeyFilter?: RangeKeyTimeRanges;
	segmentFilter?: string;
	segmentName?: string;
	stageSelectionNonce?: number;
}

interface IAccountLifecycleStage {
	lifecycleName: string;
	name: string;
	stageExternalReferenceCode: string;
}

export const buildSelectionPreloadedData = (value?: string, label?: string) =>
	value
		? {
				exclude: false,
				selectedItems: [{label: label ?? value, value}],
			}
		: undefined;

const AccountsDataSet: React.FC<IAccountsDataSetProps> = ({
	accountLifecycleId,
	allLifecycles = false,
	apiURL,
	channelId,
	countryFilter,
	dataSetId = 'accounts-list-dataset',
	fieldCatalog,
	groupId,
	industryFilter,
	lifecycleStageFilter,
	rangeKeyFilter,
	segmentFilter,
	segmentName,
	stageSelectionNonce,
}) => {
	const {
		items: lifecycleStageItems,
		lifecycleStages,
		loading: lifecycleStagesLoading,
	} = useLifecycleStageFilterItems({
		accountLifecycleId,
		allLifecycles,
		channelId,
		groupId,
	});

	const preloadedRangeSelector = rangeSelectors.find(
		({value}) => value === rangeKeyFilter
	);

	const preloadedLifecycleStage = lifecycleStageFilter
		? lifecycleStages.find(({id}) => id === lifecycleStageFilter)
		: undefined;

	// The data set reads `filters` only when its reducer is initialized on
	// mount, so hold it back until the lifecycle stage values are in. Mounting
	// early and remounting once they arrive is what made the table flash.

	if ((accountLifecycleId || allLifecycles) && lifecycleStagesLoading) {
		return (
			<Card minHeight={300}>
				<Loading />
			</Card>
		);
	}

	return (
		<Card minHeight={300}>
			<FrontendDataSet
				apiURL={apiURL}
				customDataRenderers={{
					accountLifecycleStageRenderer: ({
						itemData,
						value,
					}: {
						itemData: {lifecycleStages?: IAccountLifecycleStage[]};
						value: string;
					}) =>
						allLifecycles ? (
							<DataValue
								format={(
									accountLifecycleStages: IAccountLifecycleStage[]
								) =>
									accountLifecycleStages.map(
										({
											lifecycleName,
											name,
											stageExternalReferenceCode,
										}) => (
											<span
												className="mr-1"
												key={stageExternalReferenceCode}
											>
												{columns.cmsLabelRenderer({
													displayType: 'secondary',
													label: `${lifecycleName}: ${name}`,
												})}
											</span>
										)
									)
								}
								value={
									itemData.lifecycleStages?.length
										? itemData.lifecycleStages
										: undefined
								}
							/>
						) : (
							<DataValue
								format={(stage: string) =>
									columns.cmsLabelRenderer(
										isLifecycleStage(stage)
											? lifecycleStagesLabelMap[stage]
											: {
													displayType: 'secondary',
													label: stage,
												}
									)
								}
								value={value}
							/>
						),
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
					activitiesCountRenderer: columns.countRenderer,
					annualRevenueRenderer: ({value}: {value?: number}) => (
						<div>
							<DataValue type="count" value={value} />
						</div>
					),
					dateRenderer: ({value}: {value: string}) =>
						columns.dateRenderer({itemData: {}, value}),
					textRenderer: columns.textRenderer,
				}}
				emptyState={{
					description: Liferay.Language.get(
						'no-accounts-were-synced-from-the-connected-data-sources'
					),
					image: EMPTY_STATE_IMAGE,
					imageReducedMotion: EMPTY_STATE_REDUCED_MOTION_IMAGE,
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
					...(accountLifecycleId || allLifecycles
						? [
								{
									entityFieldType: 'string',
									id: 'lifecycleStatus',
									items: lifecycleStageItems,
									label: allLifecycles
										? Liferay.Language.get(
												'lifecycle-stage'
											)
										: Liferay.Language.get('status'),
									multiple: true,
									name: 'status',
									preloadedData: buildSelectionPreloadedData(
										preloadedLifecycleStage?.externalReferenceCode,
										lifecycleStageItems.find(
											({value}) =>
												value ===
												preloadedLifecycleStage?.externalReferenceCode
										)?.label
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
						apiURL: `/o/faro/contacts/${groupId}/individual_segment/search?channelId=${channelId}`,
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
				views={buildViews(fieldCatalog)}
			/>
		</Card>
	);
};

export default AccountsDataSet;
