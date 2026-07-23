import BundleRouter from 'route-middleware/BundleRouter';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import {close, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {Route, Routes as RouterRoutes, useParams} from 'react-router-dom';
import {Project} from 'shared/util/records';
import {RootState} from 'shared/store';
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
	(store: RootState, {groupId}: {groupId: string}) => {
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
	const {groupId = '0'} = useParams<{groupId: string}>();

	return <ConnectedWorkspaceLayer groupId={groupId} />;
};

export default WorkspaceLayout;
