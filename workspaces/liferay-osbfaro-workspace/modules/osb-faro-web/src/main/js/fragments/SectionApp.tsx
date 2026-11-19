import AppProviders from '../AppProviders';
import HelpWidget from 'shared/components/HelpWidget';
import Loading from 'shared/components/Loading';
import NoPropertiesAvailable from 'shared/pages/NoPropertiesAvailable';
import React, {Suspense, useEffect, useState} from 'react';
import RootLayout from '../RootLayout';
import WorkspaceGate from 'shared/components/WorkspaceGate';
import {
	createMemoryRouter,
	Outlet,
	RouteObject,
	RouterProvider,
	useLocation,
	useParams,
} from 'react-router-dom';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {setDefaultGroupId} from 'shared/apollo/client';
import {useModalNotifications} from 'shared/hooks/useModalNotifications';

interface ISectionAppProps {
	channelId?: string;
	channelScoped?: boolean;
	groupId: string;
	path: string;
	routes: RouteObject[];
	state?: {[name: string]: string | undefined};
}

const FullPageLoad = () => {
	const {pathname, search} = useLocation();

	useEffect(() => {
		window.location.assign(pathname + search);
	}, [pathname, search]);

	return null;
};

const PropertyGate = () => {
	const {groupId = ''} = useParams<{groupId: string}>();

	const {selectedChannel} = useChannelContext();

	const currentUser = useCurrentUser();

	if (!selectedChannel) {
		return (
			<NoPropertiesAvailable
				currentUser={currentUser}
				groupId={groupId}
			/>
		);
	}

	return <Outlet />;
};

const SectionLayout = () => {
	const {groupId = ''} = useParams<{groupId: string}>();

	const modalNotification = useModalNotifications(groupId);

	return (
		<>
			<Suspense fallback={<Loading />}>
				<Outlet />
			</Suspense>

			{modalNotification}

			<HelpWidget groupId={groupId} />
		</>
	);
};

const SectionApp: React.FC<ISectionAppProps> = ({
	channelId,
	channelScoped = true,
	groupId,
	path,
	routes,
	state,
}) => {
	const [router] = useState(() => {
		setDefaultGroupId(groupId);

		const pathname =
			channelScoped && channelId
				? `/workspace/${groupId}/${channelId}/${path}`
				: `/workspace/${groupId}/${path}`;

		const memoryRouter = createMemoryRouter(
			[
				{
					children: [
						{
							children: channelScoped
								? [
										{
											children: [
												{
													children: routes,
													element: <PropertyGate />,
													path: ':channelId?',
												},
											],
											element: <WorkspaceGate />,
										},
									]
								: routes,
							element: <SectionLayout />,
							path: '/workspace/:groupId',
						},
						{element: <FullPageLoad />, path: '*'},
					],
					element: <RootLayout />,
				},
			],
			{
				initialEntries: [
					{pathname, search: window.location.search, state},
				],
			}
		);

		memoryRouter.subscribe(({location}) => {
			if (location.pathname !== pathname) {
				return;
			}

			const url = new URL(window.location.href);

			url.search = location.search;

			window.history.replaceState(
				window.history.state,
				'',
				url.toString()
			);
		});

		return memoryRouter;
	});

	return (
		<AppProviders>
			<RouterProvider router={router} />
		</AppProviders>
	);
};

export default SectionApp;
