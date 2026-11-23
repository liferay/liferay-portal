import AppProviders from '../AppProviders';
import AppShell from '../AppShell';
import HelpWidget from 'shared/components/HelpWidget';
import Loading from 'shared/components/Loading';
import NoPropertiesAvailable from 'shared/pages/NoPropertiesAvailable';
import React, {Suspense, useState} from 'react';
import WorkspaceGate from 'shared/components/WorkspaceGate';
import {INavigationItem, buildConfig} from 'shared/util/config';
import {PageConfigProvider} from 'shared/context/pageConfig';
import {setDefaultGroupId} from 'shared/apollo/client';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useModalNotifications} from 'shared/hooks/useModalNotifications';

export interface IPageAppProps<
	Params extends {[name: string]: string | undefined} = {
		[name: string]: string | undefined;
	},
> {
	channelId: string;
	channelScoped?: boolean;
	friendlyURL: string;
	groupId: string;
	navigationItems?: INavigationItem[] | null;
	pageURLs: {[friendlyURL: string]: string};
	params?: Params | null;
	sidebarNavigationItems?: INavigationItem[] | null;
	signOutURL: string;
}

interface IPageLayoutProps {
	children: React.ReactNode;
	groupId: string;
}

const PageLayout: React.FC<IPageLayoutProps> = ({children, groupId}) => {
	const modalNotification = useModalNotifications(groupId);

	return (
		<>
			<Suspense fallback={<Loading />}>{children}</Suspense>

			{modalNotification}

			<HelpWidget groupId={groupId} />
		</>
	);
};

const PropertyGate: React.FC<IPageLayoutProps> = ({children, groupId}) => {
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

	return <>{children}</>;
};

const PageApp: React.FC<IPageAppProps & {children: React.ReactNode}> = ({
	channelId,
	channelScoped = true,
	children,
	friendlyURL,
	groupId,
	navigationItems,
	pageURLs,
	params,
	sidebarNavigationItems,
	signOutURL,
}) => {
	const [config] = useState(() => {
		setDefaultGroupId(groupId);

		return buildConfig({
			channelId,
			friendlyURL,
			groupId,
			navigationItems,
			pageURLs,
			params,
			sidebarNavigationItems,
			signOutURL,
		});
	});

	return (
		<PageConfigProvider config={config}>
			<AppProviders>
				<AppShell groupId={groupId}>
					<PageLayout groupId={groupId}>
						<WorkspaceGate>
							{channelScoped ? (
								<PropertyGate groupId={groupId}>
									{children}
								</PropertyGate>
							) : (
								children
							)}
						</WorkspaceGate>
					</PageLayout>
				</AppShell>
			</AppProviders>
		</PageConfigProvider>
	);
};

export default PageApp;
