import AddWorkspace from 'shared/pages/AddWorkspace';
import AppProviders from '../../AppProviders';
import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import OAuthReceive from 'settings/pages/OAuthReceive';
import React, {useEffect, useState} from 'react';
import RootLayout from '../../RootLayout';
import SelectWorkspaceAccount from 'shared/pages/SelectWorkspaceAccount';
import Workspaces from 'shared/pages/Workspaces';
import {
	createMemoryRouter,
	RouteObject,
	RouterProvider,
	useLocation,
} from 'react-router-dom';
import {ENABLE_ADD_TRIAL_WORKSPACE} from 'shared/util/constants';

interface IGuestAppProps {
	initialEntry: string;
	routes: RouteObject[];
}

interface IAddWorkspaceAppProps {
	params: {corpProjectUuid?: string};
}

const FullPageLoad = () => {
	const {pathname, search} = useLocation();

	useEffect(() => {
		window.location.assign(pathname + search);
	}, [pathname, search]);

	return null;
};

const GuestApp: React.FC<IGuestAppProps> = ({initialEntry, routes}) => {
	const [router] = useState(() =>
		createMemoryRouter(
			[
				{
					children: [
						...routes,
						{element: <FullPageLoad />, path: '*'},
					],
					element: <RootLayout />,
				},
			],
			{initialEntries: [initialEntry + window.location.search]}
		)
	);

	return (
		<AppProviders>
			<RouterProvider router={router} />
		</AppProviders>
	);
};

const AddWorkspaceApp: React.FC<IAddWorkspaceAppProps> = ({params}) => (
	<GuestApp
		initialEntry={
			params.corpProjectUuid
				? `/workspace/${params.corpProjectUuid}/add`
				: '/workspace/add/trial'
		}
		routes={[
			{
				element: ENABLE_ADD_TRIAL_WORKSPACE ? (
					<BundleRouter data={AddWorkspace} />
				) : (
					<ErrorPage />
				),
				path: 'workspace/add/trial',
			},
			{
				element: <BundleRouter data={AddWorkspace} />,
				path: 'workspace/:corpProjectUuid/add',
			},
		]}
	/>
);

const OAuthReceiveApp = () => (
	<GuestApp
		initialEntry="/oauth/receive"
		routes={[
			{
				element: <BundleRouter data={OAuthReceive} />,
				path: 'oauth/receive',
			},
		]}
	/>
);

const SelectWorkspaceAccountApp = () => (
	<GuestApp
		initialEntry="/workspace/select-account"
		routes={[
			{
				element: <BundleRouter data={SelectWorkspaceAccount} />,
				path: 'workspace/select-account',
			},
		]}
	/>
);

const WorkspacesApp = () => (
	<GuestApp
		initialEntry="/"
		routes={[{element: <BundleRouter data={Workspaces} />, index: true}]}
	/>
);

export {
	AddWorkspaceApp as AddWorkspace,
	OAuthReceiveApp as OAuthReceive,
	SelectWorkspaceAccountApp as SelectWorkspaceAccount,
	WorkspacesApp as Workspaces,
};
