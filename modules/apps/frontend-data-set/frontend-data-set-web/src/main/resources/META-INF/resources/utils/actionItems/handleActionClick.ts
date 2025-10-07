/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';

import {openPermissionsModal} from '../modals/openPermissionsModal';
import {resolveModalSize} from '../modals/resolveModalSize';
import {IItemsActions} from '../types';
import {ACTION_ITEM_TARGETS} from './constants';
import formatActionURL from './formatActionURL';

const {INFO_PANEL, MODAL_PERMISSIONS} = ACTION_ITEM_TARGETS;

const handleActionClick = ({
	action,
	closeMenu,
	event,
	executeAsyncItemAction,
	highlightItems,
	infoPanelOpen,
	itemData,
	itemId,
	items,
	loadData,
	onActionDropdownItemClick,
	onInfoPanelToggleButtonClick,
	onItemSelectionChange,
	openModal,
	openSidePanel,
	setLoading,
	toggleItemInlineEdit,
}: {
	action: IItemsActions;
	closeMenu?: any;
	event: Event;
	executeAsyncItemAction: Function;
	highlightItems: Function;
	infoPanelOpen?: boolean;
	itemData: any;
	itemId: string | number;
	items: any[];
	loadData: Function;
	onActionDropdownItemClick: Function;
	onInfoPanelToggleButtonClick?: Function;
	onItemSelectionChange?: Function;
	openModal: Function;
	openSidePanel: Function;
	setLoading?: Function;
	toggleItemInlineEdit: Function;
}) => {
	const {data, href, method, onClick, target} = action;

	const {
		confirmationMessage,
		disableHeader,
		errorMessage,
		requestBody,
		size,
		status,
		successMessage,
		title,
	} = data ?? {};

	const url = formatActionURL(href, itemData, target);

	const doAction = ({defaultPrevented}: {defaultPrevented: boolean}) => {
		if (target === INFO_PANEL && onInfoPanelToggleButtonClick) {
			onItemSelectionChange?.(itemData);

			!infoPanelOpen && onInfoPanelToggleButtonClick();
		}
		else if (target?.includes('modal')) {
			event.preventDefault();

			if (target === MODAL_PERMISSIONS) {
				openPermissionsModal(url);
			}
			else {
				openModal({
					disableHeader,
					size: size || resolveModalSize(target),
					title,
					url,
				});
			}
		}
		else if (target === 'sidePanel') {
			event.preventDefault();

			highlightItems([itemId]);

			openSidePanel({
				disableHeader,
				size: 'lg',
				title,
				url,
			});
		}
		else if (target === 'async' || target === 'headless') {
			event.preventDefault();

			setLoading && setLoading(true);

			executeAsyncItemAction({
				errorMessage,
				method: method ?? data?.method,
				requestBody,
				setActionItemLoading: setLoading,
				successMessage,
				url,
			});
		}
		else if (target === 'inlineEdit') {
			event.preventDefault();

			toggleItemInlineEdit(itemId);
		}
		else if (target === 'blank') {
			event.preventDefault();

			window.open(url);
		}

		const exposedProps = {
			action,
			event,
			itemData,
			items,
			loadData,
			openSidePanel,
		};

		if (onClick) {
			onClick(exposedProps);
		}

		if (onActionDropdownItemClick) {
			onActionDropdownItemClick(exposedProps);
		}

		if (target === 'link' && defaultPrevented) {
			navigate(url);
		}
	};

	if (confirmationMessage) {
		let defaultPrevented = false;

		if (target === 'link') {
			event.preventDefault();

			defaultPrevented = true;
		}

		openConfirmModal({
			message: confirmationMessage,
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					doAction({defaultPrevented});
				}
			},
			status,
			title,
		});
	}
	else {
		doAction({defaultPrevented: false});
	}

	if (closeMenu) {
		closeMenu();
	}
};

export default handleActionClick;
