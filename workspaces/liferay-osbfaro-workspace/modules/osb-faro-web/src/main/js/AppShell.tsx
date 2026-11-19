import AlertFeed from 'shared/components/AlertFeed';
import Loading from 'shared/components/Loading';
import React, {Suspense, useEffect, useState} from 'react';
import TrackingConsentBanner from 'shared/components/TrackingConsentBanner';
import {FaroEnv} from 'shared/util/constants';
import {Pendo, TrackingConsentValues} from 'shared/util/pendo';
import {Project} from 'shared/util/records';
import {syncAIHubChatbot} from 'shared/util/ai-hub-chatbot';
import {useFetchCurrentUser} from 'shared/hooks/useCurrentUser';
import {useSelector} from 'react-redux';

interface IAppShellProps {
	children: React.ReactNode;
	groupId?: string;
}

const AppShell: React.FC<IAppShellProps> = ({children, groupId}) => {
	const projectGroupId = groupId ?? '0';

	const project: Project = useSelector<any, any>((state) =>
		state.getIn(['projects', projectGroupId, 'data'])
	);

	const {data: currentUser, loading} = useFetchCurrentUser(projectGroupId);

	const [trackingConsent, setTrackingConsent] =
		useState<TrackingConsentValues | null>(null);

	// The chatbot is provisioned per workspace, so it follows the route.

	useEffect(() => {
		syncAIHubChatbot(groupId);
	}, [groupId]);

	// The stored cookie decides whether Pendo may start. `trackingConsent` is
	// not read here: it only re-runs the effect once the banner stores a
	// decision, so tracking starts without a reload. A loaded workspace is
	// enough: LDP workspaces carry no corp project, so gating on one would
	// leave them untracked.

	useEffect(() => {
		const pendo = new Pendo();

		if (
			currentUser?.id &&
			project?.groupId &&
			pendo.getUserConsent() === TrackingConsentValues.Accepted
		) {
			pendo.initialize({currentUser, project});
		}
	}, [currentUser?.id, project?.groupId, trackingConsent]);

	if (loading) {
		return <Loading />;
	}

	return (
		<>
			<AlertFeed />

			<Suspense fallback={<Loading />}>{children}</Suspense>

			{!!currentUser?.id && FARO_ENV === FaroEnv.Production && (
				<TrackingConsentBanner onDecision={setTrackingConsent} />
			)}
		</>
	);
};

export default AppShell;
