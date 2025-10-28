import * as API from 'shared/api';
import BasePage from 'settings/components/BasePage';
import BundleRouter from 'route-middleware/BundleRouter';
import Card from 'shared/components/Card';
import ClayBadge from '@clayui/badge';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useState} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {getMatchedRoute, Routes, toRoute} from 'shared/util/router';
import {Switch, useParams} from 'react-router-dom';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {UserStatuses} from 'shared/util/constants';

const UserList = lazy(
	() => import(/* webpackChunkName: "UserManagement" */ './UserList')
);
const UserRequest = lazy(
	() => import(/* webpackChunkName: "UserRequest" */ './UserRequest')
);

export const User = ({className}) => {
	const {groupId} = useParams();
	const currentUser = useCurrentUser();
	const [userRequest, setUserRequest] = useState<number>(0);

	const onSetUserRequest = userRequest => setUserRequest(userRequest);

	API.user
		.fetchCount({
			groupId,
			statuses: [UserStatuses.Requested]
		})
		.then(setUserRequest);

	const NAV_ITEMS = [
		{
			exact: true,
			label: Liferay.Language.get('manage-users'),
			route: Routes.SETTINGS_USERS
		},
		{
			exact: true,
			label: (
				<>
					{Liferay.Language.get('requests')}
					{userRequest > 0 && (
						<ClayBadge className='ml-2' label={userRequest} />
					)}
				</>
			),
			route: Routes.SETTINGS_USERS_REQUESTS
		}
	];

	const matchedRoute = getMatchedRoute(NAV_ITEMS);

	const initialItem =
		NAV_ITEMS.find(item => item.route === matchedRoute) ?? NAV_ITEMS[0];

	const [activeLabel, setActiveLabel] = useState(initialItem.label);

	return (
		<BasePage
			className={getCN('user-list-page-root', className)}
			key='userListPage'
			pageDescription={Liferay.Language.get(
				'invite-new-users-to-analytics-cloud-and-or-configure-existing-users'
			)}
			pageTitle={Liferay.Language.get('user-management')}
		>
			<Card key='cardContainer' pageDisplay>
				{currentUser.isAdmin() && (
					<ClayNavigationBar
						className='page-subnav mx-4 my-3'
						triggerLabel={activeLabel}
					>
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
				)}

				<Suspense fallback={<Loading />}>
					<Switch>
						<BundleRouter
							componentProps={{currentUser}}
							data={UserList}
							exact
							path={Routes.SETTINGS_USERS}
						/>

						<BundleRouter
							componentProps={{onSetUserRequest}}
							data={UserRequest}
							exact
							path={Routes.SETTINGS_USERS_REQUESTS}
						/>

						<RouteNotFound />
					</Switch>
				</Suspense>
			</Card>
		</BasePage>
	);
};

export default User;
