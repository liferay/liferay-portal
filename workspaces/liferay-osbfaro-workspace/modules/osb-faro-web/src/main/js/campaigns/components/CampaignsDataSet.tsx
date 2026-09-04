import Card from 'shared/components/Card';
import React from 'react';
import {FrontendDataSet, pagination} from 'shared/components/FrontendDataSet';
import {ICampaign} from '../utils/mock-campaigns';
import {Text} from '@clayui/core';
import {toThousands} from 'shared/util/numbers';

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
					label: Liferay.Language.get('accounts'),
					sortable: false,
				},
				{
					contentRenderer: 'countRenderer',
					fieldName: 'individualsTouched',
					label: Liferay.Language.get('members'),
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
				countRenderer: ({value}: {value?: number}) => (
					<div>{toThousands(value ?? 0)}</div>
				),
			}}
			id="campaigns-list-dataset"
			items={items}
			pagination={pagination}
			showPagination
			views={views}
		/>
	</Card>
);

export default CampaignsDataSet;
