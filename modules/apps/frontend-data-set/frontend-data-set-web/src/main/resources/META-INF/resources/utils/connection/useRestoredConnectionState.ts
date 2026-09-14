/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useLayoutEffect, useRef, useState} from 'react';

import {EViewsActionTypes} from '../../views/viewsReducer';
import {EConfigInURLBehavior, EConfigInURLKeys} from '../types';
import useConfigInURL from '../useConfigInURL';

const RESTORE_TIMEOUT = 10000;

/**
 * Keeping in the page URL whatever a connection asked the data set to
 * remember, and offering it back on the next visit.
 *
 * Nothing here knows what the value means or which part of the data set it
 * belongs to: it is one opaque value per connection, which is what lets the
 * same mechanism serve anything a consumer takes over. What is specific to a
 * capability stays with the data set — deciding when the value is worth
 * keeping — or with the consumer, which is the only side that can turn it
 * back into a request.
 *
 * Returns the value the URL carries, for the data set to offer, and whether
 * the offer has been taken. Until it has, the data set has nothing worth
 * requesting.
 */
export function useRestoredConnectionState({
	configInURLBehavior,
	connectionStateOffered,
	filteringOwnerAppId,
	id,
	onGiveUp,
	restoredConnectionState,
}: {
	configInURLBehavior: EConfigInURLBehavior;
	connectionStateOffered: boolean;
	filteringOwnerAppId: string | undefined;
	id: string;
	onGiveUp: () => void;
	restoredConnectionState: unknown;
}): {
	getConnectionState: () => unknown;
	restored: boolean;
} {
	const [getConnectionState] = useConfigInURL({
		configInURLBehavior,
		configReader: (connectionState: unknown) => connectionState,
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.CONNECTION_STATE,
			type: EViewsActionTypes.NOOP,
		},
	});

	const [restored, setRestored] = useState(
		() => getConnectionState() === undefined
	);

	const onGiveUpRef = useRef(onGiveUp);

	const ownerRestorePending =
		filteringOwnerAppId !== undefined &&
		typeof restoredConnectionState === 'object' &&
		restoredConnectionState !== null &&
		filteringOwnerAppId in restoredConnectionState;

	const urlConnectionState = getConnectionState();

	const ownerRestoreExpected =
		filteringOwnerAppId !== undefined &&
		typeof urlConnectionState === 'object' &&
		urlConnectionState !== null &&
		filteringOwnerAppId in urlConnectionState;

	// Kept in a ref, and out of the dependencies below, so that giving up
	// stays a single timeout rather than one restarted by every render.

	useLayoutEffect(() => {
		onGiveUpRef.current = onGiveUp;
	});

	useEffect(() => {
		if (restored) {
			return;
		}

		const restoreOffered =
			connectionStateOffered || restoredConnectionState !== undefined;

		if (restoreOffered && !ownerRestorePending) {
			if (ownerRestoreExpected || restoredConnectionState === undefined) {
				setRestored(true);

				return;
			}

			if (filteringOwnerAppId !== undefined) {
				onGiveUpRef.current();

				setRestored(true);

				return;
			}
		}

		const timeoutId = setTimeout(() => {
			onGiveUpRef.current();

			setRestored(true);
		}, RESTORE_TIMEOUT);

		return () => clearTimeout(timeoutId);
	}, [
		connectionStateOffered,
		filteringOwnerAppId,
		ownerRestoreExpected,
		ownerRestorePending,
		restored,
		restoredConnectionState,
	]);

	return {getConnectionState, restored};
}
