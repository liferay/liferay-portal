/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';

import {openPermissionsModal} from '../modals/openPermissionsModal';
import {openWorkflowTransitionModal} from '../modals/openWorkflowTransitionModal';
import {resolveModalSize} from '../modals/resolveModalSize';
import {recordVisit} from '../recordVisit';
import {IItemsActions} from '../types';
import {ACTION_ITEM_TARGETS} from './constants';
import formatActionURL from './formatActionURL';

const {BLANK, INFO_PANEL, LINK, MODAL_PERMISSIONS, MODAL_WORKFLOW_TRANSITION} =
	ACTION_ITEM_TARGETS;

const handleActionClick = ({
	accessibleNameField,
	action,
	closeMenu,
	event,
	executeAsyncItemAction,
	fdsName,
	highlightItems,
	infoPanelOpen,
	isItemSelected,
	itemData,
	itemId,
	items,
	loadData,
	onActionDropdownItemClick,
	onInfoPanelToggleButtonClick,
	onItemSelectionChange,
	openModal,
	openSidePanel,
	searchSuggestionsEnabled,
	setLoading,
	toggleItemInlineEdit,
}: {
	accessibleNameField?: string;
	action: IItemsActions;
	closeMenu?: any;
	event: Event;
	executeAsyncItemAction: Function;
	fdsName: string;
	highlightItems: Function;
	infoPanelOpen?: boolean;
	isItemSelected?: boolean;
	itemData: any;
	itemId: string | number;
	items: any[];
	loadData: Function;
	onActionDropdownItemClick: Function;
	onInfoPanelToggleButtonClick?: Function;
	onItemSelectionChange?: Function;
	openModal: Function;
	openSidePanel: Function;
	searchSuggestionsEnabled: boolean;
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
			!isItemSelected && onItemSelectionChange?.(itemData);

			!infoPanelOpen && onInfoPanelToggleButtonClick();
		}
		else if (target?.includes('modal')) {
			event.preventDefault();

			if (target === MODAL_PERMISSIONS) {
				openPermissionsModal(url);
			}
			else if (target === MODAL_WORKFLOW_TRANSITION) {
				openWorkflowTransitionModal({
					action,
					executeAsyncItemAction,
					itemId,
				});
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

		if (!target || target === BLANK || target === LINK) {
			recordVisit({
				accessibleNameField,
				fdsName,
				href: url,
				itemData,
				label: action.label,
				searchSuggestionsEnabled,
			});
		}

		if (target === LINK && defaultPrevented) {
			navigate(url);
		}
	};

	if (confirmationMessage) {
		let defaultPrevented = false;

		if (target === LINK) {
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
