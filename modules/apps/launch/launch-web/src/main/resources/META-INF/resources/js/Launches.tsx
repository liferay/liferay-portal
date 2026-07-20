/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {Launch} from './api/launches';
import LaunchDetails from './components/LaunchDetails';
import LaunchesLanding from './components/LaunchesLanding';
import NewLaunchForm from './components/NewLaunchForm';
import {
	View,
	pushViewToURL,
	readViewFromURL,
	replaceViewInURL,
} from './util/viewURLState';

export {default as AddToLaunchModal} from './components/AddToLaunchModal';

interface Props {
	getLaunchEntryContentResourceURL: string;
	portletNamespace: string;
}

export function Launches({
	getLaunchEntryContentResourceURL,
	portletNamespace,
}: Props) {
	const [view, setView] = useState<View>(() => readViewFromURL());

	useEffect(() => {
		function handlePopState() {
			setView(readViewFromURL());
		}

		window.addEventListener('popstate', handlePopState);

		return () => window.removeEventListener('popstate', handlePopState);
	}, []);

	function navigate(nextView: View) {
		setView(nextView);
		pushViewToURL(nextView);
	}

	switch (view.type) {
		case 'new':
			return (
				<NewLaunchForm
					onCancel={() => navigate({type: 'landing'})}
					onCreated={(launch: Launch) =>
						navigate({launchId: launch.id, type: 'details'})
					}
				/>
			);
		case 'details':
			return (
				<LaunchDetails
					getLaunchEntryContentResourceURL={
						getLaunchEntryContentResourceURL
					}
					launchId={view.launchId}
					onBack={() => navigate({type: 'landing'})}
					onInvalid={() => replaceViewInURL({type: 'landing'})}
					portletNamespace={portletNamespace}
				/>
			);
		case 'landing':
		default:
			return (
				<LaunchesLanding
					onNew={() => navigate({type: 'new'})}
					onSelect={(launch: Launch) =>
						navigate({launchId: launch.id, type: 'details'})
					}
				/>
			);
	}
}
