/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useLayoutEffect, useRef} from 'react';

/**
 * Hook that returns a stable function identity which always invokes the
 * latest callback. Useful to keep memoized values and effects from
 * recreating when a caller passes an inline callback.
 */

export default function useStableCallback<A extends unknown[], R>(
	callback: (...args: A) => R
) {
	const callbackRef = useRef(callback);

	const stableCallback = useCallback(
		(...args: A) => callbackRef.current(...args),
		[]
	);

	useLayoutEffect(() => {
		callbackRef.current = callback;
	}, [callback]);

	return stableCallback;
}
