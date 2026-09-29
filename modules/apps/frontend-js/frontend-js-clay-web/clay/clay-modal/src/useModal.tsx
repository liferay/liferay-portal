/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useRef, useState} from 'react';

import {Observer, ObserverType} from './types';

type Props = {

	/**
	 * Set the default value of the state of the modal dialog.
	 */
	defaultOpen?: boolean;

	/**
	 * Callback called to close the modal.
	 * @deprecated since v3.52.0 - use the `open` and `onOpenChange` properties
	 * of the hook return and remove its state.
	 */
	onClose?: () => void;
};

type Return = {

	/**
	 * Observer is an internal property that must be connected to the <ClayModal /> component.
	 */
	observer: Observer;

	/**
	 * Callback to close the modal, aliased to `onOpenChange` callback.
	 */
	onClose: () => void;

	/**
	 * Callback to change open state.
	 */
	onOpenChange: (value: boolean) => void;

	/**
	 * Sets the open state of the modal.
	 */
	open: boolean;
};

function delay(fn: Function) {
	return setTimeout(() => {
		fn();
	}, 100);
}

const modalOpenClassName = 'modal-open';

const modalLockHolders = new Set<object>();

function acquireModalOpenLock(holder: object) {
	modalLockHolders.add(holder);
	document.body.classList.add(modalOpenClassName);
}

function releaseModalOpenLock(holder: object) {
	modalLockHolders.delete(holder);

	if (modalLockHolders.size === 0) {
		document.body.classList.remove(modalOpenClassName);
	}
}

export function useModal({defaultOpen = false, onClose}: Props = {}): Return {
	const [open, setOpen] = useState(defaultOpen);
	const [visible, setVisible] = useState<[boolean, boolean]>([false, false]);
	const timerIdRef = useRef<NodeJS.Timeout | null>(null);
	const restoreTriggerRef = useRef<HTMLElement | null>(null);
	const lockHolderRef = useRef<object>({});

	/**
	 * Control the close of the modal to create the component's "unmount"
	 * animation and call the onClose prop with delay.
	 */
	const handleCloseModal = () => {
		releaseModalOpenLock(lockHolderRef.current);
		setVisible([false, true]);
		timerIdRef.current = delay(() => {
			if (onClose) {
				onClose();
			}
			if (restoreTriggerRef.current) {
				restoreTriggerRef.current.focus();
				restoreTriggerRef.current = null;
			}
			setOpen(false);
			setVisible([false, false]);
		});
	};
	const handleOpenModal = () => {
		acquireModalOpenLock(lockHolderRef.current);
		setOpen(true);
		timerIdRef.current = delay(() => setVisible([true, true]));
	};
	const handleObserverDispatch = (
		type: ObserverType,
		payload: HTMLElement
	) => {
		switch (type) {
			case ObserverType.Close:
				handleCloseModal();
				break;
			case ObserverType.Open:
				handleOpenModal();
				break;
			case ObserverType.RestoreFocus:
				restoreTriggerRef.current = payload;
				break;
			case ObserverType.Unmount:
				releaseModalOpenLock(lockHolderRef.current);
				break;
			default:
				break;
		}
	};
	const onOpenChange = useCallback((value: boolean) => {
		if (value) {
			handleOpenModal();
		}
		else {
			handleCloseModal();
		}
	}, []);
	useEffect(() => {
		return () => {
			releaseModalOpenLock(lockHolderRef.current);
			if (timerIdRef.current) {
				clearTimeout(timerIdRef.current);
			}
		};
	}, []);

	return {
		observer: {
			dispatch: handleObserverDispatch,
			mutation: visible,
		},
		onClose: handleCloseModal,
		onOpenChange,
		open,
	};
}
