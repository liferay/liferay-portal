import DataSourcesProvider from 'shared/context/dataSources';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import ProjectGate from 'shared/components/workspaces/ProjectGate';
import React from 'react';
import {DownloadReportProvider} from 'shared/components/download-report/DownloadReportContext';
import {isValidChannel} from 'shared/components/channels-menu';
import {matchPath, Outlet, useLocation, useParams} from 'react-router-dom';
import {Routes} from 'shared/util/router';
import {useChannelContext} from 'shared/context/channel';
import {useChannels} from 'shared/hooks/useChannels';
import {useOnboardingModal} from 'shared/hooks/useOnboardingModal';
import {useUnassignedSegmentsModal} from 'shared/hooks/useUnassignedSegmentsModal';

/**
 * Only the workspace home and segment links may leave the channel out of the
 * URL: the home picks the default channel, and a segment link resolves the
 * channel of its segment.
 */
const isChannelOptional = (pathname: string) =>
	!!matchPath({end: true, path: Routes.WORKSPACE_WITH_ID}, pathname) ||
	(!!matchPath({end: true, path: Routes.CONTACTS_SEGMENT}, pathname) &&
		!matchPath(
			{end: true, path: Routes.CONTACTS_SEGMENT_CREATE},
			pathname
		));

const WorkspaceModals: React.FC<{groupId: string}> = ({groupId}) => {
	const onboardingModal = useOnboardingModal({groupId});

	const unassignedSegmentsModal = useUnassignedSegmentsModal({groupId});

	return (
		<>
			{onboardingModal}

			{unassignedSegmentsModal}
		</>
	);
};

interface IChannelGateProps {
	children?: React.ReactNode;
	groupId: string;
}

const ChannelGate: React.FC<IChannelGateProps> = ({children, groupId}) => {
	const {channelId} = useParams<{channelId?: string}>();

	const {pathname} = useLocation();

	const {channels, error, loading} = useChannels({
		channelId,
		groupId,
	});

	const {selectedChannel} = useChannelContext();

	if (error) {
		return <ErrorPage />;
	}

	if (loading) {
		return <Loading />;
	}

	if (
		!isValidChannel(channelId, channels) ||
		(!channelId && !!channels.length && !isChannelOptional(pathname))
	) {
		return <ErrorPage />;
	}

	return (
		<>
			<WorkspaceModals groupId={groupId} />

			<DataSourcesProvider groupId={groupId} skip={!selectedChannel}>
				<DownloadReportProvider>
					{children ?? <Outlet />}
				</DownloadReportProvider>
			</DataSourcesProvider>
		</>
	);
};

interface IWorkspaceGateProps {
	children?: React.ReactNode;
}

const WorkspaceGate: React.FC<IWorkspaceGateProps> = ({children}) => {
	const {groupId = ''} = useParams<{groupId: string}>();

	return (
		<ProjectGate groupId={groupId}>
			<ChannelGate groupId={groupId}>{children}</ChannelGate>
		</ProjectGate>
	);
};

export default WorkspaceGate;
