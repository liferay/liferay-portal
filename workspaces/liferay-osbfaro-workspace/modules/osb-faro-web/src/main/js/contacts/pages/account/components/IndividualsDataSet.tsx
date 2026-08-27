import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {formatTime} from 'shared/util/time';
import {pickBy} from 'lodash';
import {Routes, setUriQueryValues} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useParams} from 'react-router-dom';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const FDS_ID = 'account-individuals-dataset';

const PREVIEW_FDS_ID = 'most-engaged-individuals-dataset';

const PREVIEW_DELTA = 3;

const SORTS = [
	{
		active: true,
		default: true,
		direction: 'desc' as const,
		key: 'activitiesCount',
		label: Liferay.Language.get('total-events'),
	},
];

interface IIndividualItemData {
	id: string | number;
	properties?: {jobTitle?: string};
}

export const getVisitorType = (sessionsCount: number) => {
	if (!sessionsCount) {
		return {
			displayType: 'secondary' as const,
			label: Liferay.Language.get('no-activities'),
		};
	}

	if (sessionsCount === 1) {
		return {
			displayType: 'success' as const,
			label: Liferay.Language.get('first-time'),
		};
	}

	return {
		displayType: 'info' as const,
		label: Liferay.Language.get('returning'),
	};
};

interface IIndividualsDataSetProps {

	/**
	 * Renders the read only preview shown on the overview tab: the first few
	 * individuals with no search, sorting or pagination. The full table on the
	 * profile tab is the default.
	 */
	preview?: boolean;
}

const IndividualsDataSet: React.FC<IIndividualsDataSetProps> = ({
	preview = false,
}) => {
	const {
		channelId = '',
		groupId = '',
		id = '',
	} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();

	const rangeSelectors = useQueryRangeSelectors();

	/**
	 * `rangeEnd` and `rangeStart` are only set for a custom range, so drop the
	 * empty ones rather than sending them as `null`. The individual profile
	 * link carries the same range so the selection survives the redirect.
	 */

	const rangeQueryValues = pickBy(rangeSelectors);

	return (
		<FrontendDataSet
			apiURL={setUriQueryValues(
				{channelId, ...rangeQueryValues},
				`/o/faro/contacts/${groupId}/account/${id}/individuals`
			)}
			customDataRenderers={{
				avgSessionDurationRenderer: ({value}: {value?: number}) =>
					value ? formatTime(value) : '',
				individualNameRenderer: ({
					itemData,
					value,
				}: {
					itemData: IIndividualItemData;
					value: string;
				}) =>
					columns.nameAndLinkRenderer({
						channelId,
						groupId,
						itemData,
						queryValues: rangeQueryValues,
						route: Routes.CONTACTS_INDIVIDUAL,
						value,
					}),
				jobTitleRenderer: ({
					itemData,
				}: {
					itemData: IIndividualItemData;
				}) => itemData.properties?.jobTitle ?? '',
				lastActiveRenderer: ({value}: {value: string}) =>
					columns.dateRenderer({itemData: {}, value}),
				totalEventsRenderer: ({value}: {value?: number}) =>
					typeof value === 'number' ? toThousands(value) : '',
				visitorTypeRenderer: ({value}: {value?: number}) =>
					typeof value === 'number'
						? columns.cmsLabelRenderer(getVisitorType(value))
						: '',
			}}
			emptyState={{
				description: Liferay.Language.get(
					'no-activities-were-found-on-the-selected-period'
				),
				title: Liferay.Language.get('no-individuals-were-found'),
			}}
			id={preview ? PREVIEW_FDS_ID : FDS_ID}
			pagination={preview ? undefined : pagination}
			showManagementBar={!preview}
			showPagination={!preview}
			showSearch={!preview}
			sorts={SORTS}
			views={[
				{
					contentRenderer: 'table',
					default: true,
					...(preview && {initialPaginationDelta: PREVIEW_DELTA}),
					label: Liferay.Language.get('default-view'),
					name: 'table',
					schema: {
						fields: [
							{
								contentRenderer: 'individualNameRenderer',
								fieldName: 'name',
								label: Liferay.Language.get('individual-name'),
								sortable: !preview,
							},
							{
								contentRenderer: 'jobTitleRenderer',
								fieldName: 'jobTitle',
								label: Liferay.Language.get('job-title'),
								sortable: !preview,
							},
							{
								contentRenderer: 'visitorTypeRenderer',
								fieldName: 'sessionsCount',
								label: Liferay.Language.get('visitor-type'),
								sortable: !preview,
							},
							{
								contentRenderer: 'totalEventsRenderer',
								fieldName: 'activitiesCount',
								label: Liferay.Language.get('total-events'),
								sortable: !preview,
							},
							{
								contentRenderer: 'avgSessionDurationRenderer',
								fieldName: 'averageSessionDuration',
								label: Liferay.Language.get(
									'avg-session-duration'
								),
								sortable: !preview,
							},
							{
								contentRenderer: 'lastActiveRenderer',
								fieldName: 'lastActivityDate',
								label: Liferay.Language.get('last-active'),
								sortable: !preview,
							},
						],
					},
					thumbnail: 'table',
				},
			]}
		/>
	);
};

export default IndividualsDataSet;
