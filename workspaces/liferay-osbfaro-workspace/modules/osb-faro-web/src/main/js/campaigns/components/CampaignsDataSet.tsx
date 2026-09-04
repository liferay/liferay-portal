import Card from 'shared/components/Card';
import React from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {ICampaign} from '../utils/mock-campaigns';
import {Text} from '@clayui/core';

interface ICampaignsDataSetProps {
	items: ICampaign[];
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

const CampaignsDataSet: React.FC<ICampaignsDataSetProps> = ({items}) => (
	<Card minHeight={300}>
		<FrontendDataSet
			customDataRenderers={{
				campaignNameRenderer: ({value}: {value: string}) => (
					<Text weight="semi-bold">{value}</Text>
				),
				countRenderer: columns.countRenderer,
			}}
			id="campaigns-list-dataset"
			items={items}
			pagination={pagination}

			// Search is served by the request, so it does nothing while the
			// data set runs on `items`. Hiding it empties the management bar,
			// which then renders as 65px of blank space, so that goes too. The
			// backend integration task brings both back with the endpoint.

			showManagementBar={false}
			showPagination
			showSearch={false}
			views={views}
		/>
	</Card>
);

export default CampaignsDataSet;
