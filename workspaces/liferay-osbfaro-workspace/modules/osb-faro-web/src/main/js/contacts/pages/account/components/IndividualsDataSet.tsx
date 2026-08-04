import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {formatTime} from 'shared/util/time';
import {Routes} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useParams} from 'react-router-dom';

const FDS_ID = 'account-individuals-dataset';

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

const IndividualsDataSet: React.FC = () => {
	const {channelId, groupId, id} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();

	return (
		<FrontendDataSet
			apiURL={`/o/faro/contacts/${groupId}/account/${id}/individuals?channelId=${channelId}`}
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
			id={FDS_ID}
			pagination={pagination}
			showPagination
			sorts={SORTS}
			views={[
				{
					contentRenderer: 'table',
					default: true,
					label: Liferay.Language.get('default-view'),
					name: 'table',
					schema: {
						fields: [
							{
								contentRenderer: 'individualNameRenderer',
								fieldName: 'name',
								label: Liferay.Language.get('individual-name'),
								sortable: true,
							},
							{
								contentRenderer: 'jobTitleRenderer',
								fieldName: 'jobTitle',
								label: Liferay.Language.get('job-title'),
								sortable: true,
							},
							{
								contentRenderer: 'visitorTypeRenderer',
								fieldName: 'sessionsCount',
								label: Liferay.Language.get('visitor-type'),
								sortable: true,
							},
							{
								contentRenderer: 'totalEventsRenderer',
								fieldName: 'activitiesCount',
								label: Liferay.Language.get('total-events'),
								sortable: true,
							},
							{
								contentRenderer: 'avgSessionDurationRenderer',
								fieldName: 'averageSessionDuration',
								label: Liferay.Language.get(
									'avg-session-duration'
								),
								sortable: true,
							},
							{
								contentRenderer: 'lastActiveRenderer',
								fieldName: 'lastActivityDate',
								label: Liferay.Language.get('last-active'),
								sortable: true,
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
