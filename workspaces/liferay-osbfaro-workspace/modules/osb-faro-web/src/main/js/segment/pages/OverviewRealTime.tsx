import * as API from 'shared/api';
import Button from '@clayui/button';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import CriteriaCard from 'segment/components/criteria-card';
import Loading from 'shared/components/Loading';
import MembershipMetrics from '../components/MembershipMetrics';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useMemo, useState} from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import SegmentActivationCard from 'segment/components/SegmentActivationCard';
import URLConstants from 'shared/util/url-constants';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {
	CUSTOM_DATE_FORMAT,
	formatUTCDate,
	ISO_8601_DATE_FORMAT,
} from 'shared/util/date';
import {fetchMembershipChangesAggregations} from 'shared/api/individual-segment';
import {FilterOptionType} from 'shared/types';
import {membershipChangesColumns} from 'shared/util/table-columns';
import {ReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {Segment} from 'shared/util/records';
import {SegmentGrowthChart} from 'segment/components/Growth';
import {SegmentTypes, TimeIntervals} from 'shared/util/constants';
import {Text} from '@clayui/core';
import {useRequest} from 'shared/hooks/useRequest';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const DEFAULT_ORDER_BY_OPTIONS = [
	{
		label: Liferay.Language.get('name'),
		value: 'name',
	},
	{
		label: Liferay.Language.get('account-name'),
		value: 'accountName',
	},
	{
		label: Liferay.Language.get('first-seen'),
		value: 'firstSeenTime',
	},
	{
		label: Liferay.Language.get('last-active'),
		value: 'lastActivityTime',
	},
	{
		label: Liferay.Language.get('profile-type'),
		value: 'profileType',
	},
];

const MEMBERSHIP_CHANGE_ORDER_BY_OPTION = {
	label: Liferay.Language.get('membership-change'),
	value: 'membershipChange',
};

const FILTER_BY_DEFAULT_OPTIONS: FilterOptionType[] = [
	{
		key: 'profileTypes',
		label: Liferay.Language.get('profile-type'),
		values: [
			{label: Liferay.Language.get('known'), value: 'KNOWN'},
			{label: Liferay.Language.get('anonymous'), value: 'ANONYMOUS'},
		],
	},
];

const MEMBERSHIP_CHANGE_FILTER_OPTION: FilterOptionType = {
	key: 'types',
	label: Liferay.Language.get('membership-change'),
	values: [
		{label: Liferay.Language.get('added'), value: 'ADDED'},
		{label: Liferay.Language.get('removed'), value: 'REMOVED'},
	],
};

const getMembershipChanges = (data: Data) => {
	const {delta, filters, groupId, id, orderIOMap, query, selectedDate} = data;

	return API.individualSegment.fetchRealTimeMembershipChanges({
		date: selectedDate,
		delta,
		filters,
		groupId,
		orderIOMap,
		query,
		segmentId: id,
	});
};

type Data = {
	channelId: string;
	delta: number;
	filters: {string: string[]};
	groupId: string;
	id: string;
	selectedDate: string;
	orderIOMap: string;
	page: number;
	query: string;
};

interface IOverviewProps {
	channelId: string;
	groupId: string;
	segment: Segment;
}

const SelectedPointInfo = ({
	dateRange,
	onClear,
	selectedPointState,
}: {
	dateRange: string | null;
	onClear: () => void;
	selectedPointState: {
		hasSelectedPoint: boolean;
		selectedPoint: number | null;
	};
}) => {
	const membersLanguageKey = selectedPointState.hasSelectedPoint
		? Liferay.Language.get('members-on')
		: Liferay.Language.get('members-from');

	return (
		<div>
			<div className="selected-point-info">
				<Text color="secondary" size={3}>
					{membersLanguageKey} {dateRange}
					{selectedPointState.hasSelectedPoint && (
						<Button
							className="ml-3"
							displayType="unstyled"
							onClick={onClear}
						>
							<Text color="primary" size={3} weight="semi-bold">
								{Liferay.Language.get('clear-date-selection')}
							</Text>
						</Button>
					)}
				</Text>
			</div>
		</div>
	);
};

const RealTimeSegmentOverview: React.FC<IOverviewProps> = ({
	channelId,
	groupId,
	segment,
}) => {
	const fetchMembers = (params: Record<string, any>) =>
		getMembershipChanges(params as Data);

	const {activation, criteriaString, id, includeAnonymousUsers, sequential} =
		segment;

	const {timeZoneId} = useTimeZone();

	const {data, loading} = useRequest({
		dataSourceFn: fetchMembershipChangesAggregations,
		variables: {
			channelId,
			groupId,
			id,
			interval: TimeIntervals.Day,
			max: 30,
		},
	});

	const [selectedPointState, setSelectedPointState] = useState<{
		hasSelectedPoint: boolean;
		selectedPoint: number | null;
	}>({
		hasSelectedPoint: false,
		selectedPoint: null,
	});

	const dateRange = useMemo(() => {
		if (data) {
			if (
				selectedPointState.hasSelectedPoint &&
				selectedPointState.selectedPoint !== null
			) {
				return `${formatUTCDate(
					data[selectedPointState.selectedPoint].intervalInitDate,
					CUSTOM_DATE_FORMAT
				)}`;
			}

			return `${formatUTCDate(
				data[0].intervalInitDate,
				CUSTOM_DATE_FORMAT
			)} - ${formatUTCDate(
				data[data.length - 1].intervalInitDate,
				CUSTOM_DATE_FORMAT
			)}`;
		}

		return null;
	}, [data, selectedPointState]);

	const getColumns = () => {
		const baseColumns = [
			membershipChangesColumns.individualName,
			membershipChangesColumns.accountNames,
			membershipChangesColumns.firstSeen,
			membershipChangesColumns.lastActive,
			membershipChangesColumns.profileType,
		];

		const columns = selectedPointState.hasSelectedPoint
			? [...baseColumns, membershipChangesColumns.membershipChanges]
			: baseColumns;

		return columns;
	};

	const paginationParams = useStatefulPagination(undefined, {
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const orderByOptions = useMemo(
		() =>
			selectedPointState.hasSelectedPoint
				? [
						...DEFAULT_ORDER_BY_OPTIONS,
						MEMBERSHIP_CHANGE_ORDER_BY_OPTION,
					]
				: DEFAULT_ORDER_BY_OPTIONS,
		[selectedPointState.hasSelectedPoint]
	);

	const filterByOptions = useMemo(
		() =>
			selectedPointState.hasSelectedPoint
				? [
						...FILTER_BY_DEFAULT_OPTIONS,
						MEMBERSHIP_CHANGE_FILTER_OPTION,
					]
				: FILTER_BY_DEFAULT_OPTIONS,
		[selectedPointState.hasSelectedPoint]
	);

	const selectedDate = useMemo(
		() =>
			selectedPointState.hasSelectedPoint &&
			data &&
			selectedPointState.selectedPoint !== null
				? formatUTCDate(
						data[selectedPointState.selectedPoint].intervalInitDate,
						ISO_8601_DATE_FORMAT
					)
				: undefined,
		[
			data,
			selectedPointState.hasSelectedPoint,
			selectedPointState.selectedPoint,
		]
	);

	const selectedFilters = {
		profileTypes:
			paginationParams.filterBy?.get('profileTypes')?.toArray() || [],
		types: paginationParams.filterBy?.get('types')?.toArray() || [],
	};

	const handleClearDateSelection = () => {
		setSelectedPointState({
			hasSelectedPoint: false,
			selectedPoint: null,
		});

		paginationParams.onFilterByChange?.(
			paginationParams.filterBy?.delete('types')
		);
	};

	return (
		<div>
			<ReferencedObjectsProvider segment={segment}>
				<CriteriaCard
					channelId={channelId}
					criteriaString={criteriaString ?? ''}
					groupId={groupId}
					includeAnonymousUsers={includeAnonymousUsers}
					segmentType={SegmentTypes.RealTime}
					sequential={sequential}
					timeZoneId={timeZoneId}
				/>
			</ReferencedObjectsProvider>

			{activation && (
				<SegmentActivationCard
					segmentActivation={activation}
					segmentType={SegmentTypes.RealTime}
				/>
			)}

			<MembershipMetrics />

			<Card
				className="segment-membership-root"
				reportContainer={ReportContainer.SegmentMembershipTrendCard}
			>
				<Card.Header className="align-items-center d-flex justify-content-between">
					<Card.Title>
						{Liferay.Language.get('segment-membership-trend')}
					</Card.Title>
					<span className="text-secondary text-uppercase">
						<strong>{Liferay.Language.get('last-30-days')}</strong>
					</span>
				</Card.Header>

				<Card.Body className="segment-growth-root">
					{loading ? (
						<Loading />
					) : (
						<>
							<div className="segment-growth-chart-container">
								<SegmentGrowthChart
									alwaysShowSelectedTooltip
									data={data?.map((item: any) => ({
										added: item.addedIndividualsCount,
										anonymousCount:
											item.anonymousIndividualsCount,
										knownCount: item.knownIndividualsCount,
										modifiedDate: item.intervalInitDate,
										removed: item.removedIndividualsCount,
										value: item.individualsCount,
									}))}
									hasSelectedPoint={
										selectedPointState.hasSelectedPoint
									}
									height={360}
									individualCounts={{
										anonymousCount:
											data[data.length - 1]
												.anonymousIndividualsCount,
										knownCount:
											data[data.length - 1]
												.knownIndividualsCount,
									}}
									onSelectedPointChange={(
										selectedPoint: number
									) => {
										setSelectedPointState({
											hasSelectedPoint: true,
											selectedPoint,
										});
									}}
									selectedPoint={
										selectedPointState.selectedPoint ??
										undefined
									}
								/>
							</div>
							<SelectedPointInfo
								dateRange={dateRange}
								onClear={handleClearDateSelection}
								selectedPointState={selectedPointState}
							/>
							<SearchableEntityTable
								{...paginationParams}
								columns={getColumns()}
								dataSourceFn={fetchMembers}
								dataSourceParams={{
									channelId,
									filters: selectedFilters,
									groupId,
									id,
									selectedDate,
								}}
								filterByOptions={filterByOptions}
								key={
									selectedPointState.hasSelectedPoint
										? 'changes-view'
										: 'all-members-view'
								}
								noResultsRenderer={() => (
									<NoResultsDisplay
										description={
											<>
												<span className="mr-1">
													{Liferay.Language.get(
														'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
													)}
												</span>

												<ClayLink
													href={
														URLConstants.SegmentsMembershipDocumentationLink
													}
													key="DOCUMENTATION"
													target="_blank"
												>
													{Liferay.Language.get(
														'learn-more-about-individuals'
													)}
												</ClayLink>
											</>
										}
										spacer
										title={Liferay.Language.get(
											'no-members-were-found-on-the-selected-time-period'
										)}
									/>
								)}
								orderByOptions={orderByOptions}
								rowIdentifier="id"
							/>
						</>
					)}
				</Card.Body>
			</Card>
		</div>
	);
};

export default RealTimeSegmentOverview;
