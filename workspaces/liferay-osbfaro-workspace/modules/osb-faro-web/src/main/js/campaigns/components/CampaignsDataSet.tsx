import Card from 'shared/components/Card';
import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
	SATELLITE_EMPTY_STATE_IMAGE,
} from 'shared/components/FrontendDataSet';
import {Routes} from 'shared/util/router';

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
}) => (
	<Card minHeight={300}>
		<FrontendDataSet
			apiURL={`/o/faro/contacts/${groupId}/campaigns?channelId=${channelId}`}
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
						route: Routes.CAMPAIGNS_DETAIL,
						value,
					}),
				countRenderer: columns.countRenderer,
			}}
			emptyState={{
				description: Liferay.Language.get(
					'no-campaigns-were-synced-from-the-connected-data-sources'
				),
				image: SATELLITE_EMPTY_STATE_IMAGE,
				imageReducedMotion: SATELLITE_EMPTY_STATE_IMAGE,
				title: Liferay.Language.get('no-campaigns-found'),
			}}
			id="campaigns-list-dataset"
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

export default CampaignsDataSet;
