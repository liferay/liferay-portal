import AppProviders from '../../AppProviders';
import BundleRouter from 'route-middleware/BundleRouter';
import React, {useEffect, useState} from 'react';
import RootLayout from '../../RootLayout';
import Workspaces from 'shared/pages/Workspaces';
import {
	createMemoryRouter,
	RouterProvider,
	useLocation,
} from 'react-router-dom';

const FullPageLoad = () => {
	const {pathname, search} = useLocation();

	useEffect(() => {
		window.location.assign(pathname + search);
	}, [pathname, search]);

	return null;
};

const WorkspacesApp = () => {
	const [router] = useState(() =>
		createMemoryRouter([
			{
				children: [
					{element: <BundleRouter data={Workspaces} />, index: true},
					{element: <FullPageLoad />, path: '*'},
				],
				element: <RootLayout />,
			},
		])
	);

	return (
		<AppProviders>
			<RouterProvider router={router} />
		</AppProviders>
	);
};

export default WorkspacesApp;
