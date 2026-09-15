/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useLayoutEffect, useRef, useState} from 'react';

import {EViewsActionTypes} from '../../views/viewsReducer';
import {EConfigInURLBehavior, EConfigInURLKeys} from '../types';
import useConfigInURL from '../useConfigInURL';

const APPLY_TIMEOUT = 10000;

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
 * Returns the custom configs the URL carries, for the data set to offer,
 * and whether the wait for them is settled: applied by the connection that
 * owns the filtering, or given up on. Until it is, the data set has nothing
 * worth requesting.
 */
export function useOfferedCustomConfigs({
	configInURLBehavior,
	customConfigsOffered,
	filteringOwnerAppId,
	id,
	offeredCustomConfigs,
	onGiveUp,
}: {
	configInURLBehavior: EConfigInURLBehavior;
	customConfigsOffered: boolean;
	filteringOwnerAppId: string | undefined;
	id: string;
	offeredCustomConfigs: unknown;
	onGiveUp: () => void;
}): {
	getCustomConfigs: () => unknown;
	settled: boolean;
} {
	const [getCustomConfigs] = useConfigInURL({
		configInURLBehavior,
		configReader: (customConfigs: unknown) => customConfigs,
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.CUSTOM_CONFIGS,
			type: EViewsActionTypes.NOOP,
		},
	});

	const [settled, setSettled] = useState(
		() => getCustomConfigs() === undefined
	);

	const onGiveUpRef = useRef(onGiveUp);

	const filteringOwnerAppIdInAtom =
		filteringOwnerAppId !== undefined &&
		typeof offeredCustomConfigs === 'object' &&
		offeredCustomConfigs !== null &&
		filteringOwnerAppId in offeredCustomConfigs;

	const customConfigs = getCustomConfigs();

	const filteringOwnerAppIdInURL =
		filteringOwnerAppId !== undefined &&
		typeof customConfigs === 'object' &&
		customConfigs !== null &&
		filteringOwnerAppId in customConfigs;

	// Kept in a ref, and out of the dependencies below, so that giving up
	// stays a single timeout rather than one restarted by every render.

	useLayoutEffect(() => {
		onGiveUpRef.current = onGiveUp;
	});

	useEffect(() => {
		if (settled) {
			return;
		}

		const everOffered =
			customConfigsOffered || offeredCustomConfigs !== undefined;

		if (everOffered && !filteringOwnerAppIdInAtom) {
			if (
				filteringOwnerAppIdInURL ||
				offeredCustomConfigs === undefined
			) {
				setSettled(true);

				return;
			}

			if (filteringOwnerAppId !== undefined) {
				onGiveUpRef.current();

				setSettled(true);

				return;
			}
		}

		const timeoutId = setTimeout(() => {
			onGiveUpRef.current();

			setSettled(true);
		}, APPLY_TIMEOUT);

		return () => clearTimeout(timeoutId);
	}, [
		customConfigsOffered,
		filteringOwnerAppId,
		offeredCustomConfigs,
		filteringOwnerAppIdInAtom,
		filteringOwnerAppIdInURL,
		settled,
	]);

	return {getCustomConfigs, settled};
}
