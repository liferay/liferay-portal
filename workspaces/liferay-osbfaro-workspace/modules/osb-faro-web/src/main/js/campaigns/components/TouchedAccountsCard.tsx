import Card from 'shared/components/Card';
import ClayTabs from '@clayui/tabs';
import React, {useState} from 'react';
import {
	columns,
	FrontendDataSet,
	pagination,
} from 'shared/components/FrontendDataSet';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {Routes} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';

interface ITouchedAccountsCardProps {
	campaignId: string;
	channelId: string;
	groupId: string;
}

// Individuals Touched is a placeholder: the design shows the tab, but nothing
// behind it is in scope yet, so `ClayTabs.Item` renders it disabled rather
// than switching to an empty panel.

const TABS = [
	{
		tabId: 'accounts-touched',
		title: Liferay.Language.get('accounts-touched'),
	},
	{
		disabled: true,
		tabId: 'individuals-touched',
		title: Liferay.Language.get('individuals-touched'),
	},
];

const views = [
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
					label: Liferay.Language.get('name'),
					sortable: false,
					truncate: true,
				},
				{
					contentRenderer: 'lifecycleStageRenderer',
					fieldName: 'lifecycleStage',
					label: Liferay.Language.get('lifecycle-stage'),
					sortable: false,
				},
				{
					contentRenderer: 'amountRenderer',
					fieldName: 'salesforce/openPipelineAmount',
					label: Liferay.Language.get('pipeline-value'),
					sortable: false,
				},
				{
					contentRenderer: 'amountRenderer',
					fieldName: 'salesforce/closedWonAmount',
					label: Liferay.Language.get('closed-won'),
					sortable: false,
				},
			],
		},
		thumbnail: 'table',
	},
];

const TouchedAccountsCard: React.FC<ITouchedAccountsCardProps> = ({
	campaignId,
	channelId,
	groupId,
}) => {
	const [activeIndex, setActiveIndex] = useState(0);

	return (
		<Card minHeight={300}>
			<Card.Header>
				<Card.Title>{Liferay.Language.get('membership')}</Card.Title>
			</Card.Header>

			<ClayTabs
				active={activeIndex}
				className="mb-3"
				onActiveChange={(index) => setActiveIndex(Number(index))}
			>
				{TABS.map(({disabled, tabId, title}) => (
					<ClayTabs.Item disabled={disabled} key={tabId}>
						{title}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<FrontendDataSet
				apiURL={`/o/faro/contacts/${groupId}/campaigns/${campaignId}/accounts?channelId=${channelId}`}
				customDataRenderers={{
					accountNameRenderer: ({
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
							route: Routes.CONTACTS_ACCOUNT_OVERVIEW,
							value,
						}),

					// An account with no opportunity value renders an empty
					// cell. The design carries a 0 in those cells, but at zero
					// opacity, so a zero would be wrong.

					amountRenderer: ({value}: {value?: number}) => (
						<div>
							{value === undefined ? '' : toThousands(value)}
						</div>
					),
					lifecycleStageRenderer: ({
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
				}}
				id="campaign-accounts-dataset"
				pagination={pagination}

				// Same as the campaigns table: asah declares `search`, `filter`
				// and `sort` on this endpoint and reads none of them, sorting by
				// account id regardless. Offering either control would look
				// functional and do nothing.

				showManagementBar={false}
				showPagination
				showSearch={false}
				views={views}
			/>
		</Card>
	);
};

export default TouchedAccountsCard;
