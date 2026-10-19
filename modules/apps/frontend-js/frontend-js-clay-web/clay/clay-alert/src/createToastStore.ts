/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {IClayAlertProps} from './Alert';

export type ToastItem = {

	/**
	 * Unique key of the toast in the store. It is never rendered as a DOM ID.
	 */
	key: string;

	/**
	 * Props used to render the toast alert.
	 */
	props: IClayAlertProps;
};

export type ToastStoreListener = (toasts: ReadonlyArray<ToastItem>) => void;

export type ToastStore = {

	/**
	 * Appends a toast and returns its key (not a DOM ID).
	 */
	add: (toast: IClayAlertProps) => string;

	/**
	 * Runs the toast's `onClose(event)` and removes it, unless the default of
	 * the event was prevented.
	 */
	close: (key: string, event?: React.MouseEvent<HTMLButtonElement>) => void;

	/**
	 * Returns an immutable snapshot of the toasts. Its identity changes on
	 * every change.
	 */
	getToasts: () => ReadonlyArray<ToastItem>;

	/**
	 * Removes the toast silently, without calling its `onClose`.
	 */
	remove: (key: string) => void;

	/**
	 * Registers a listener for changes and returns a function to unsubscribe.
	 */
	subscribe: (listener: ToastStoreListener) => () => void;
};

function createToastStore(): ToastStore {
	let counter = 0;
	const listeners = new Set<ToastStoreListener>();
	let toasts: ReadonlyArray<ToastItem> = [];

	function setToasts(nextToasts: ReadonlyArray<ToastItem>) {
		toasts = nextToasts;

		listeners.forEach((listener) => listener(nextToasts));
	}

	function remove(key: string) {
		const nextToasts = toasts.filter((toast) => toast.key !== key);

		if (nextToasts.length !== toasts.length) {
			setToasts(nextToasts);
		}
	}

	function add({onClose, ...props}: IClayAlertProps) {
		const key = `toast-${++counter}`;
		const newToast = {
			key,
			props: {
				...props,
				...(onClose && {
					onClose: (event?: React.MouseEvent<HTMLButtonElement>) => {
						onClose(event);

						if (!event?.defaultPrevented) {
							remove(key);
						}
					},
				}),
			},
		};

		setToasts(toasts.concat(newToast));

		return key;
	}

	function close(key: string, event?: React.MouseEvent<HTMLButtonElement>) {
		const toast = toasts.find((item) => item.key === key);

		if (toast) {
			if (toast.props.onClose) {
				toast.props.onClose(event);
			}
			else {
				remove(key);
			}
		}
	}

	function subscribe(listener: ToastStoreListener) {
		listeners.add(listener);

		return () => {
			listeners.delete(listener);
		};
	}

	return {
		add,
		close,
		getToasts: () => toasts,
		remove,
		subscribe,
	};
}

export {createToastStore};
