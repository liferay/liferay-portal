import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {lazy} from 'react';
import RootLayout from './RootLayout';
import {createBrowserRouter, RouteObject} from 'react-router-dom';
import {ENABLE_ADD_TRIAL_WORKSPACE} from 'shared/util/constants';

const AddWorkspace = lazy(
	() =>
		import(

			/* webpackChunkName: "AddWorkspace" */ 'shared/pages/AddWorkspace'
		)
);
const SelectWorkspaceAccount = lazy(
	() =>
		import(

			/* webpackChunkName: "SelectWorkspaceAccount" */ 'shared/pages/SelectWorkspaceAccount'
		)
);
const Workspaces = lazy(
	() => import(/* webpackChunkName: "Workspaces" */ 'shared/pages/Workspaces')
);
const WorkspaceLayout = lazy(
	() =>
		import(

			/* webpackChunkName: "WorkspaceLayout" */ 'shared/components/WorkspaceLayout'
		)
);
const OAuthReceive = lazy(
	() =>
		import(

			/* webpackChunkName: "OAuthReceive" */ 'settings/pages/OAuthReceive'
		)
);

export function getRoutes(): RouteObject[] {
	return [
		{
			children: [
				{element: <BundleRouter data={Workspaces} />, index: true},
				{
					element: <BundleRouter data={Workspaces} />,
					path: 'workspaces',
				},
				{
					element: <BundleRouter data={SelectWorkspaceAccount} />,
					path: 'workspace/add',
				},
				...(ENABLE_ADD_TRIAL_WORKSPACE
					? [
							{
								element: <BundleRouter data={AddWorkspace} />,
								path: 'workspace/add/trial',
							},
						]
					: []),
				{
					element: <BundleRouter data={AddWorkspace} />,
					path: 'workspace/:corpProjectUuid/add',
				},
				{
					element: <BundleRouter data={SelectWorkspaceAccount} />,
					path: 'workspace/select-account',
				},
				{
					element: <BundleRouter data={OAuthReceive} />,
					path: 'oauth/receive',
				},
				{element: <Loading />, path: 'loading'},
				{element: <WorkspaceLayout />, path: 'workspace/:groupId/*'},
				{element: <ErrorPage />, path: '*'},
			],
			element: <RootLayout />,
			errorElement: <ErrorPage />,
		},
	];
}

export const router = createBrowserRouter(getRoutes());
