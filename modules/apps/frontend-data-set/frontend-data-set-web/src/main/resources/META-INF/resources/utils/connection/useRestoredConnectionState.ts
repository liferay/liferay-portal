/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useLayoutEffect, useRef, useState} from 'react';

import {EViewsActionTypes} from '../../views/viewsReducer';
import {EConfigInURLBehavior, EConfigInURLKeys} from '../types';
import useConfigInURL from '../useConfigInURL';

// How long a data set whose URL carries state a consumer left waits for the
// connection that owns it, matched to the timeout a connection itself waits
// for the data set with, so that neither side gives up first.

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
	filteringOwnerAppId,
	id,
	onGiveUp,
	restoredConnectionState,
}: {
	configInURLBehavior: EConfigInURLBehavior;
	filteringOwnerAppId: string | undefined;
	id: string;
	onGiveUp: () => void;
	restoredConnectionState: unknown;
}): {
	getConnectionState: () => unknown;
	restored: boolean;
} {

	// The data set cannot check a value it cannot read, so it checks only
	// that the URL held one at all. Whether the value still makes sense is
	// the consumer's to judge, and the consumer is the only side that can.

	const [getConnectionState] = useConfigInURL({
		configInURLBehavior,
		configReader: (connectionState: unknown) => connectionState,
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.CONNECTION_STATE,
			type: EViewsActionTypes.NOOP,
		},
	});

	// A URL carrying state a consumer left is a promise that a connection is
	// coming to claim it, and the data set has nothing to request until it
	// does: asking now would fetch data the URL already says is filtered, and
	// asking again afterwards would show the user those results first. This
	// waits for the connection the same way the data set already waits for
	// the client extensions its filters and cells are drawn with.

	const [restored, setRestored] = useState(
		() => getConnectionState() === undefined
	);

	const restoreOfferedRef = useRef(false);
	const onGiveUpRef = useRef(onGiveUp);

	// What is offered is filed under the app id of the connection that left
	// it, and a connection deletes its own key as soon as it has been handed
	// what was under it. The key of the owner is therefore what this waits
	// on, rather than the whole of what is offered: keys of other apps
	// outlive the one restore that was ever going to happen.

	const ownerRestorePending =
		filteringOwnerAppId !== undefined &&
		typeof restoredConnectionState === 'object' &&
		restoredConnectionState !== null &&
		filteringOwnerAppId in restoredConnectionState;

	// Whether a restore for the owner was ever coming, which the address
	// still says: the data set does not write the URL until this wait is
	// over. It is what tells a restore that has happened from one that was
	// never going to, since what is offered looks the same either way.

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

		// Nothing can be read into a restore that has not been offered yet:
		// it looks exactly like one that has happened, and a data set that
		// has offered nothing so far would say the same thing.

		if (restoredConnectionState !== undefined) {
			restoreOfferedRef.current = true;
		}

		const restoreOffered =
			restoreOfferedRef.current || restoredConnectionState !== undefined;

		if (restoreOffered && !ownerRestorePending) {
			if (ownerRestoreExpected || restoredConnectionState === undefined) {

				// The key of the owner is gone from what the address says
				// held it, which is the owner having been handed it. Nothing
				// left on offer at all says as much, whether or not this has
				// seen who the owner is.
				//
				// Keys of other apps stay on offer for whatever connects
				// later: they were never this wait's to take, nor to drop.

				setRestored(true);

				return;
			}

			if (filteringOwnerAppId !== undefined) {

				// The connection that owns the filtering is the only one the
				// offer would ever be handed to, and the address holds no key
				// of it: nobody is coming for what is left.

				onGiveUpRef.current();

				setRestored(true);

				return;
			}
		}

		// Nothing guarantees the consumer is still on the page: its widget
		// may have been removed while the link was in someone's inbox. Give
		// up on what the URL carries rather than leave the data set waiting
		// on a consumer that will never connect, and let it filter and offer
		// its own UI as it does without any consumer at all.

		const timeoutId = setTimeout(() => {
			onGiveUpRef.current();

			setRestored(true);
		}, RESTORE_TIMEOUT);

		return () => clearTimeout(timeoutId);
	}, [
		filteringOwnerAppId,
		ownerRestoreExpected,
		ownerRestorePending,
		restored,
		restoredConnectionState,
	]);

	return {getConnectionState, restored};
}
