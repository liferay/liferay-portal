import BundleRouter from 'route-middleware/BundleRouter';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useState} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {getMatchedRoute, Routes, toRoute} from 'shared/util/router';
import {Switch} from 'react-router';

const CustomEventList = lazy(
	() => import(/* webpackChunkName: "CustomEventList" */ './CustomEventList')
);

const EventList = lazy(
	() => import(/* webpackChunkName: "EventList" */ './EventList')
);

const NAV_ITEMS = [
	{
		exact: true,
		label: Liferay.Language.get('default-events'),
		route: Routes.SETTINGS_DEFINITIONS_EVENTS_DEFAULT
	},
	{
		exact: true,
		label: Liferay.Language.get('custom-events'),
		route: Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM
	}
];

interface ITabsCardProps {
	groupId: string;
}

const TabsCard: React.FC<ITabsCardProps> = ({groupId}) => {
	const matchedRoute = getMatchedRoute(NAV_ITEMS);

	const customEventTab =
		matchedRoute === Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM;

	const initialItem =
		NAV_ITEMS.find(item => item.route === matchedRoute) ?? NAV_ITEMS[0];

	const [activeLabel, setActiveLabel] = useState(initialItem.label);

	return (
		<Card key='cardContainer' pageDisplay>
			<ClayNavigationBar className='my-3' triggerLabel={activeLabel}>
				{NAV_ITEMS.map(({label, route}) => (
					<ClayNavigationBar.Item
						active={matchedRoute === route}
						key={route}
					>
						<ClayLink
							href={toRoute(route, {groupId})}
							onClick={() => setActiveLabel(label)}
						>
							{label}
						</ClayLink>
					</ClayNavigationBar.Item>
				))}
			</ClayNavigationBar>

			{customEventTab && (
				<ClayLink
					borderless
					button
					className='block-list-button button-root m-3'
					displayType='secondary'
					href={toRoute(
						Routes.SETTINGS_DEFINITIONS_EVENTS_BLOCK_LIST,
						{groupId}
					)}
					small
				>
					<ClayIcon className='icon-root mr-2' symbol='ac_block' />

					{Liferay.Language.get('block-list')}
				</ClayLink>
			)}

			<Suspense fallback={<Loading />}>
				<Switch>
					<BundleRouter
						data={EventList}
						exact
						path={Routes.SETTINGS_DEFINITIONS_EVENTS_DEFAULT}
					/>

					<BundleRouter
						data={CustomEventList}
						exact
						path={Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM}
					/>

					<RouteNotFound />
				</Switch>
			</Suspense>
		</Card>
	);
};

export default TabsCard;
