import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useContext} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {ACCOUNTS, Routes, toRoute} from 'shared/util/router';
import {ChannelContext} from 'shared/context/channel';
import {Redirect, Switch, useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const Activities = lazy(
	() => import(/* webpackChunkName: "AccountActivities" */ './Activities')
);
const Profile = lazy(
	() => import(/* webpackChunkName: "AccountProfile" */ './Profile')
);
const Overview = lazy(
	() => import(/* webpackChunkName: "Overview" */ './Overview')
);

const NAV_ITEMS = [
	{
		exact: true,
		label: Liferay.Language.get('overview'),
		route: Routes.CONTACTS_ACCOUNT_OVERVIEW,
	},
	{
		exact: true,
		label: Liferay.Language.get('activities'),
		route: Routes.CONTACTS_ACCOUNT_ACTIVITIES,
	},
	{
		exact: true,
		label: Liferay.Language.get('profile'),
		route: Routes.CONTACTS_ACCOUNT_PROFILE,
	},
];

const AccountProfileRoutes = () => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

	const {data, error, loading} = useRequest({
		dataSourceFn: API.accounts.fetch,
		variables: {accountId: id!, channelId: channelId!, groupId: groupId!},
	});

	if (loading) {
		return <Loading />;
	}

	if (error || !data) {
		return (
			<ErrorPage
				href={toRoute(Routes.CONTACTS_LIST_ENTITY, {
					channelId: channelId!,
					groupId: groupId!,
					type: ACCOUNTS,
				})}
				linkLabel={Liferay.Language.get('go-to-accounts')}
				message={Liferay.Language.get(
					'the-account-you-are-looking-for-does-not-exist'
				)}
				subtitle={Liferay.Language.get('account-not-found')}
			/>
		);
	}

	const accountName: string =
		data.accountName || Liferay.Language.get('account');

	return (
		<BasePage
			documentTitle={`${accountName} - ${Liferay.Language.get(
				'account'
			)}`}
			fluid
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
					breadcrumbs.getAccounts({
						channelId: channelId!,
						groupId: groupId!,
					}),
					breadcrumbs.getEntityName({label: accountName}),
				]}
				groupId={groupId!}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection title={accountName} />
				</BasePage.Row>

				<BasePage.Header.NavBar
					items={NAV_ITEMS}
					routeParams={{channelId, groupId, id}}
				/>
			</BasePage.Header>

			<BasePage.Body>
				<Suspense fallback={<Loading />}>
					<Switch>
						<Redirect
							exact
							from={Routes.CONTACTS_ACCOUNT}
							to={toRoute(Routes.CONTACTS_ACCOUNT_OVERVIEW, {
								channelId: channelId!,
								groupId: groupId!,
								id: id!,
							})}
						/>

						<BundleRouter
							componentProps={{account: data, loading}}
							data={Profile}
							exact
							path={Routes.CONTACTS_ACCOUNT_PROFILE}
						/>

						<BundleRouter
							componentProps={{accountName}}
							data={Activities}
							exact
							path={Routes.CONTACTS_ACCOUNT_ACTIVITIES}
						/>

						<BundleRouter
							componentProps={{account: data}}
							data={Overview}
							exact
							path={Routes.CONTACTS_ACCOUNT_OVERVIEW}
						/>

						<RouteNotFound />
					</Switch>
				</Suspense>
			</BasePage.Body>
		</BasePage>
	);
};

export default AccountProfileRoutes;
