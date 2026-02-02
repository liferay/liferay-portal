/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const STATES = {
	IDLE: {running: false, show: false},
	LONG_POLLING: {running: true, show: true},
	NOTIFY: {running: false, show: false},
	SHORT_POLLING: {running: true, show: false},
};

type BulkAction =
	| {type: 'check'}
	| {type: 'error'}
	| {type: 'initialDelayCompleted'}
	| {type: 'notificationCompleted'}
	| {type: 'start'}
	| {type: 'success'};

type BulkStateStatus = (typeof STATES)[keyof typeof STATES];
export interface BulkState {
	current: BulkStateStatus;
	timestamp?: number;
	toast?: {
		message: string;
		type?: 'danger' | 'success';
	};
}

const TOASTS = {
	ERROR: {
		message: Liferay.Language.get('an-unexpected-error-occurred'),
		type: 'danger',
	},
	SUCCESS: {
		message: Liferay.Language.get('changes-saved'),
	},
};

export {STATES};

export default function reducer(state: BulkState, action: BulkAction) {
	switch (action.type) {
		case 'check':
			if (state.current === STATES.LONG_POLLING) {
				return {
					...state,
					timestamp: Date.now(),
				};
			}
			break;

		case 'error':
			return {
				...state,
				current: STATES.NOTIFY,
				toast: TOASTS.ERROR,
			};

		case 'initialDelayCompleted':
			if (state.current === STATES.SHORT_POLLING) {
				return {
					...state,
					current: STATES.LONG_POLLING,
					timestamp: Date.now(),
				};
			}

			break;

		case 'notificationCompleted':
			if (state.current === STATES.NOTIFY) {
				return {
					...state,
					current: STATES.IDLE,
				};
			}

			break;

		case 'start':
			if (state.current === STATES.IDLE) {
				return {
					...state,
					current: STATES.SHORT_POLLING,
				};
			}

			break;

		case 'success':
			if (state.current !== STATES.LONG_POLLING) {
				return state;
			}

			setTimeout(() => window.location.reload(), 1000);

			return {
				...state,
				current: STATES.NOTIFY,
				toast: TOASTS.SUCCESS,
			};

		default:
			return state;
	}

	return state;
}
