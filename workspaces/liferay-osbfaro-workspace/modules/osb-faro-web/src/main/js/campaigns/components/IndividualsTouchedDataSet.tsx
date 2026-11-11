import React from 'react';
import {
	columns,
	EMPTY_STATE_IMAGE,
	EMPTY_STATE_REDUCED_MOTION_IMAGE,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {getVisitorType} from 'contacts/pages/account/components/IndividualsDataSet';
import {Routes, setUriQueryValues} from 'shared/util/router';
import {useRangeQueryValues} from 'shared/hooks/useRangeQueryValues';

interface IIndividualsTouchedDataSetProps {
	campaignId: string;
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
					contentRenderer: 'individualNameRenderer',
					fieldName: 'name',
					label: Liferay.Language.get('individual-name'),
					sortable: true,
					truncate: true,
				},
				{
					fieldName: 'accountName',
					label: Liferay.Language.get('account-name'),
					sortable: true,
					truncate: true,
				},
				{
					contentRenderer: 'visitorTypeRenderer',
					fieldName: 'sessionsCount',
					label: Liferay.Language.get('visitor-type'),
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
];

const IndividualsTouchedDataSet: React.FC<IIndividualsTouchedDataSetProps> = ({
	campaignId,
	channelId,
	groupId,
}) => {
	const {apiRangeParams, routeRangeParams} = useRangeQueryValues();

	return (
		<FrontendDataSet
			apiURL={setUriQueryValues(
				{channelId, ...apiRangeParams},
				`/o/faro/contacts/${groupId}/campaigns/${campaignId}/individuals`
			)}
			customDataRenderers={{
				individualNameRenderer: ({
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
						queryValues: routeRangeParams,
						route: Routes.CONTACTS_INDIVIDUAL,
						value,
					}),
				lastActiveRenderer: ({value}: {value: string}) =>
					columns.dateRenderer({itemData: {}, value}),
				visitorTypeRenderer: ({value}: {value?: number}) =>
					typeof value === 'number'
						? columns.cmsLabelRenderer(getVisitorType(value))
						: '',
			}}
			emptyState={{
				description: Liferay.Language.get(
					'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
				),
				image: EMPTY_STATE_IMAGE,
				imageReducedMotion: EMPTY_STATE_REDUCED_MOTION_IMAGE,
				title: Liferay.Language.get('no-individuals-were-found'),
			}}
			id="campaign-individuals-dataset"
			pagination={pagination}
			showPagination
			sorts={[
				{
					active: true,
					default: true,
					direction: 'desc',
					key: 'lastActivityDate',
					label: Liferay.Language.get('last-active'),
				},
			]}
			views={views}
		/>
	);
};

export default IndividualsTouchedDataSet;
