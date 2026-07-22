import AlertFeed from 'shared/components/AlertFeed';
import Loading from 'shared/components/Loading';
import ModalRenderer from 'shared/components/ModalRenderer';
import React, {Suspense, useEffect, useState} from 'react';
import TrackingConsentBanner from 'shared/components/TrackingConsentBanner';
import {FaroEnv} from 'shared/util/constants';
import {Outlet, useMatch} from 'react-router-dom';
import {Pendo, TrackingConsentValues} from 'shared/util/pendo';
import {Project} from 'shared/util/records';
import {useFetchCurrentUser} from 'shared/hooks/useCurrentUser';
import {useSelector} from 'react-redux';

const RootLayout = () => {
	const match = useMatch('/workspace/:groupId/*');
	const groupId = match?.params.groupId ?? '0';

	const project: Project = useSelector<any, any>((state) =>
		state.getIn(['projects', groupId, 'data'])
	);

	const {data: currentUser, loading} = useFetchCurrentUser(groupId);

	const [trackingConsent, setTrackingConsent] =
		useState<TrackingConsentValues | null>(null);

	// The stored cookie decides whether Pendo may start. `trackingConsent` is
	// not read here: it only re-runs the effect once the banner stores a
	// decision, so tracking starts without a reload.

	useEffect(() => {
		const pendo = new Pendo();

		if (
			currentUser?.id &&
			project?.corpProjectName &&
			pendo.getUserConsent() === TrackingConsentValues.Accepted
		) {
			pendo.initialize({currentUser, project});
		}
	}, [currentUser?.id, project?.corpProjectName, trackingConsent]);

	if (loading) {
		return <Loading />;
	}

	return (
		<>
			<AlertFeed />

			<ModalRenderer />

			<Suspense fallback={<Loading />}>
				<Outlet />
			</Suspense>

			{!!currentUser?.id && FARO_ENV === FaroEnv.Production && (
				<TrackingConsentBanner onDecision={setTrackingConsent} />
			)}
		</>
	);
};

export default RootLayout;
