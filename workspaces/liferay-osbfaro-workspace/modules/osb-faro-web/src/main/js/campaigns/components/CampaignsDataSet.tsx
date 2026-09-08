import Card from 'shared/components/Card';
import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
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
					sortable: false,
					truncate: true,
				},
				{
					contentRenderer: 'countRenderer',
					fieldName: 'accountsTouched',
					label: Liferay.Language.get('accounts-touched'),
					sortable: false,
				},
				{
					contentRenderer: 'countRenderer',
					fieldName: 'individualsTouched',
					label: Liferay.Language.get('individuals-touched'),
					sortable: false,
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
			id="campaigns-list-dataset"
			pagination={pagination}

			// The endpoint takes `search`, `filter` and `sort` and acts on
			// none of them: asah declares all three on the controller and
			// never reads them. Turning them on here would give the table a
			// search box that filters nothing and headers that sort nothing,
			// which reads as broken rather than as absent, so they stay off
			// until asah implements them.

			showManagementBar={false}
			showPagination
			showSearch={false}
			views={views}
		/>
	</Card>
);

export default CampaignsDataSet;
