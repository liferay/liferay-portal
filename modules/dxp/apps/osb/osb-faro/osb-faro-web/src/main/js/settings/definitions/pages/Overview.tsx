import BasePage from 'settings/components/BasePage';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import React from 'react';
import {ACCOUNTS, Routes, toRoute} from 'shared/util/router';
import {DEVELOPER_MODE, ENABLE_BLOCKLIST_KEYWORDS} from 'shared/util/constants';

interface IOverviewProps {
	groupId: string;
}

type ListItem = {
	header: string;
	items: {
		description: string;
		route: string;
		routeParams?: object;
		title: string;
	}[];
};

// TODO: LRAC-4511 Remove developer only mode and add devItems back into items
const items = (devMode: boolean = false): ListItem[] => [
	{
		header: Liferay.Language.get('people'),
		items: [
			{
				description: Liferay.Language.get(
					'view-and-manage-the-data-model-of-your-individuals.-this-data-is-mapped-from-your-dxp,-salesforce,-or-csv-datasources'
				),
				route: Routes.SETTINGS_DEFINITIONS_INDIVIDUAL_ATTRIBUTES,
				title: Liferay.Language.get('individuals')
			},
			devMode && {
				description: Liferay.Language.get(
					'view-and-manage-the-data-model-of-your-accounts.-this-data-is-automatically-mapped-from-a-salesforce-datasource'
				),
				route: Routes.CONTACTS_LIST_ENTITY,
				routeParams: {type: ACCOUNTS},
				title: Liferay.Language.get('accounts')
			}
		]
	},
	{
		header: Liferay.Language.get('behaviors'),
		items: [
			devMode && {
				description: Liferay.Language.get(
					'view-and-manage-the-tracked-behaviors-in-analytics-cloud.-you-will-also-find-instructions-for-tagging-non-liferay-assets-to-track-them-in-analytics-cloud'
				),
				route: Routes.SETTINGS_DEFINITIONS_BEHAVIORS,
				title: Liferay.Language.get('behaviors')
			},
			{
				description: Liferay.Language.get(
					'view-and-manage-your-default-events-custom-events-and-event-attributes'
				),
				route: Routes.SETTINGS_DEFINITIONS_EVENTS_DEFAULT,
				title: Liferay.Language.get('events')
			},
			{
				description: Liferay.Language.get(
					'view-and-manage-the-data-model-of-your-event-attributes.-event-attributes-provide-additional-context-to-your-events'
				),
				route: Routes.SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_GLOBAL,
				title: Liferay.Language.get('event-attributes')
			},
			{
				description: Liferay.Language.get(
					'define-the-search-query-parameters-specific-to-your-properties'
				),
				route: Routes.SETTINGS_DEFINITIONS_SEARCH,
				title: Liferay.Language.get('search')
			}
		]
	},

	ENABLE_BLOCKLIST_KEYWORDS
		? {
				header: Liferay.Language.get('derived-data'),
				items: [
					{
						description: Liferay.Language.get(
							'view-and-manage-the-blocked-keywords-for-interest-analysis.-blocked-keywords-will-affect-content-recommendations-feature-available-in-liferay-dxp'
						),
						route: Routes.SETTINGS_DEFINITIONS_INTEREST_TOPICS,
						title: Liferay.Language.get('interests')
					}
				]
		  }
		: null
];

export const Overview: React.FC<IOverviewProps> = ({groupId}) => (
	<BasePage
		className='definitions-overview-root'
		pageDescription={Liferay.Language.get(
			'select-the-entity-to-view-its-data-model'
		)}
		pageTitle={Liferay.Language.get('definitions')}
	>
		<div className='row'>
			<div className='col-xl-8'>
				<Card>
					<ClayList>
						{items(DEVELOPER_MODE)
							.filter(Boolean)
							.map(({header, items}) => (
								<React.Fragment key={header}>
									{header && (
										<ClayList.Header>
											{header}
										</ClayList.Header>
									)}

									{items
										.filter(Boolean)
										.map(
											({
												description,
												route,
												routeParams = {},
												title
											}) => (
												<ClayList.Item key={title}>
													<ClayList.ItemTitle>
														<ClayLink
															decoration='none'
															href={toRoute(
																route,
																{
																	groupId,
																	...routeParams
																}
															)}
														>
															{title}
														</ClayLink>
													</ClayList.ItemTitle>

													<ClayList.ItemText>
														{description}
													</ClayList.ItemText>
												</ClayList.Item>
											)
										)}
								</React.Fragment>
							))}
					</ClayList>
				</Card>
			</div>
		</div>
	</BasePage>
);

export default Overview;
