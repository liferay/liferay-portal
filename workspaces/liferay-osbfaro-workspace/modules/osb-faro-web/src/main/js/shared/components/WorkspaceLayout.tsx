import BundleRouter from 'route-middleware/BundleRouter';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import {close, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {
	matchPath,
	Route,
	Routes as RouterRoutes,
	useLocation,
} from 'react-router-dom';
import {Project} from 'shared/util/records';
import {RootState} from 'shared/store';
import {Routes} from 'shared/util/router';
import {useModalNotifications} from 'shared/hooks/useModalNotifications';
import {withHelpWidget} from 'shared/hoc';

const AppSidebarRoutes = lazy(
	() =>
		import(

			/* webpackChunkName: "AppSidebarRoutes" */ 'shared/pages/AppSidebarRoutes'
		)
);
const Settings = lazy(
	() => import(/* webpackChunkName: "Settings" */ 'settings/pages/Settings')
);

const connector = connect(
	(
		store: RootState,
		{location: {pathname}}: {location: {pathname: string}}
	) => {
		const match = matchPath(
			{end: false, path: Routes.WORKSPACE_WITH_ID},
			pathname
		);

		const groupId = match?.params?.groupId ?? '0';

		const project =
			store.getIn(['projects', groupId, 'data'], new Project()) ||
			new Project();

		const faroSubscriptionIMap = project.get('faroSubscription');

		return {
			currentUserId: String(store.getIn(['currentUser', 'data'])),
			groupId,
			serverLocation: project.get('serverLocation'),
			subscriptionName: faroSubscriptionIMap.get('name'),
			workspaceName: project.get('name'),
		};
	},
	{close, open}
);

const WorkspaceLayer = ({
	close,
	groupId,
	open,
}: {
	close: any;
	groupId: string;
	open: any;
}) => {
	useModalNotifications(close, groupId, open);

	return (
		<Suspense fallback={<Loading />}>
			<RouterRoutes>
				<Route
					element={<BundleRouter data={Settings} />}
					path="settings/*"
				/>

				<Route
					element={<BundleRouter data={AppSidebarRoutes} />}
					path="*"
				/>
			</RouterRoutes>
		</Suspense>
	);
};

const ConnectedWorkspaceLayer = compose<any>(
	connector,
	withHelpWidget
)(WorkspaceLayer);

const WorkspaceLayout = () => {
	const location = useLocation();

	return <ConnectedWorkspaceLayer location={location} />;
};

export default WorkspaceLayout;
