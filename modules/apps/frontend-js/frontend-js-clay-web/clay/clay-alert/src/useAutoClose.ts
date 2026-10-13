/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useRef} from 'react';

import {usePrefersPersistentAlerts} from './usePrefersPersistentAlerts';

interface IProps {
	autoClose?: boolean | number;
	onClose?: () => void;
}

export function useAutoClose({autoClose, onClose}: IProps) {
	const prefersPersistentAlerts = usePrefersPersistentAlerts();

	const elapsedRef = useRef(0);
	const expiredRef = useRef(false);
	const onCloseRef = useRef(onClose);
	const pauseRequestedRef = useRef(false);
	const startedAtRef = useRef<number>(0);
	const timerRef = useRef<number | null>(null);

	const pauseTimer = useCallback(() => {
		if (!timerRef.current) {
			return;
		}

		elapsedRef.current =
			elapsedRef.current + (Date.now() - startedAtRef.current);

		clearTimeout(timerRef.current);

		timerRef.current = null;
	}, []);

	const startTimer = useCallback(() => {
		if (
			!autoClose ||
			prefersPersistentAlerts ||
			expiredRef.current ||
			pauseRequestedRef.current ||
			timerRef.current
		) {
			return;
		}

		const autoCloseDuration = autoClose === true ? 10000 : autoClose;

		startedAtRef.current = Date.now();

		timerRef.current = window.setTimeout(
			() => {
				expiredRef.current = true;
				timerRef.current = null;

				onCloseRef.current?.();
			},
			Math.max(0, autoCloseDuration - elapsedRef.current)
		);
	}, [autoClose, prefersPersistentAlerts]);

	useEffect(() => {
		onCloseRef.current = onClose;
	}, [onClose]);

	useEffect(() => {
		startTimer();

		return pauseTimer;
	}, [pauseTimer, startTimer]);

	return {
		pauseAutoCloseTimer: () => {
			pauseRequestedRef.current = true;

			pauseTimer();
		},
		startAutoCloseTimer: () => {
			pauseRequestedRef.current = false;

			startTimer();
		},
	};
}
