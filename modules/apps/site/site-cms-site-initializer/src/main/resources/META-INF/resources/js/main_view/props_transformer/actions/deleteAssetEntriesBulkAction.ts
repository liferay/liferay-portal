/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import SpaceService from '../../../common/services/SpaceService';
import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {getScopeExternalReferenceCode} from '../../../common/utils/getScopeExternalReferenceCode';
import {openBulkActionConfirmationModal} from '../../../common/utils/openBulkActionConfirmationModal';
import {triggerAssetBulkAction} from './triggerAssetBulkAction';

/**
 * Executes the bulk delete action.
 */
export function executeBulkDeleteAction(
	apiURL: string,
	dataSetId: string,
	selectedData: IBulkActionFDSData,
	processClose?: () => void
): void {
	triggerAssetBulkAction({
		apiURL,
		dataSetId,
		keyValues: {
			className: selectedData.items
				? selectedData.items[0]?.entryClassName
				: '',
		},
		onCreateSuccess: () => {
			processClose?.();
		},
		selectedData,
		type: 'DeleteObjectBulkSelectionAction',
	});
}

/**
 * Returns the confirmation message and title for bulk delete modal.
 */
function getBulkDeleteMessage(
	selectedData: any,
	isMixedDeleteStrategy: boolean
): {
	messages: string[];
	title: string;
} {
	if (isMixedDeleteStrategy) {
		return {
			messages: [
				Liferay.Language.get(
					'you-are-about-to-delete-the-selected-items-from-multiple-spaces'
				),
				Liferay.Language.get(
					'bulk-delete-from-multiple-spaces-warning'
				),
				Liferay.Language.get('are-you-sure-you-want-to-continue'),
			],
			title: selectedData.items.length
				? sub(Liferay.Language.get('delete-x-items'), [
						selectedData.items.length,
					])
				: Liferay.Language.get('delete-all-items'),
		};
	}

	if (selectedData.selectAll) {
		return {
			messages: [Liferay.Language.get('delete-all-items-confirmation')],
			title: Liferay.Language.get('delete-all-items'),
		};
	}

	if (selectedData.items.length > 1) {
		return {
			messages: [
				sub(Liferay.Language.get('delete-x-items-confirmation'), [
					selectedData.items.length,
				]),
			],
			title: sub(Liferay.Language.get('delete-x-items'), [
				selectedData.items.length,
			]),
		};
	}

	return {
		messages: [Liferay.Language.get('delete-item-confirmation')],
		title: Liferay.Language.get('delete-item'),
	};
}

/**
 * Fetches asset library spaces for the given items.
 */
async function getEntriesSpaces(
	items: IBulkActionFDSData['items'] = []
): Promise<any[]> {
	const promises = items.flatMap((item) => {
		const externalReferenceCode = getScopeExternalReferenceCode(item);

		return externalReferenceCode
			? [SpaceService.getSpace(externalReferenceCode)]
			: [];
	});

	return (await Promise.all(promises)).filter(Boolean);
}

/**
 * Handles bulk deletion logic and modal display based on trash status of spaces.
 */
async function handleBulkDeletion({
	apiURL,
	dataSetId,
	getCustomBulkDeleteMessage,
	selectedData,
	showConfirmationModal,
	trashEnabled,
}: {
	apiURL: string;
	dataSetId: string;
	getCustomBulkDeleteMessage?: typeof getBulkDeleteMessage;
	selectedData: IBulkActionFDSData;
	showConfirmationModal?: boolean;
	trashEnabled?: boolean;
}): Promise<void> {
	const spaces = await getEntriesSpaces(selectedData?.items || []);

	const allEntriesHaveTrashEnabled =
		trashEnabled === true ||
		(!!spaces.length &&
			spaces.every((space) => space.settings.trashEnabled));

	const isRecycleBinView =
		dataSetId === 'com.liferay.site.cms.site.initializer-recycleBinSection';

	if (
		showConfirmationModal ||
		!allEntriesHaveTrashEnabled ||
		isRecycleBinView
	) {
		const bulkDeleteMessage =
			getCustomBulkDeleteMessage ?? getBulkDeleteMessage;

		const isMultipleSpacesView =
			trashEnabled === null || trashEnabled === undefined;

		const someEntriesHaveTrashEnabled = spaces.some(
			(space) => space.settings.trashEnabled
		);

		const {messages, title} = bulkDeleteMessage(
			selectedData,
			!isRecycleBinView &&
				isMultipleSpacesView &&
				(selectedData.selectAll || someEntriesHaveTrashEnabled)
		);

		showModal(apiURL, messages, dataSetId, title, selectedData);
	}
	else {
		executeBulkDeleteAction(apiURL, dataSetId, selectedData);
	}
}

/**
 * Shows the bulk delete confirmation modal.
 */
function showModal(
	apiURL: string,
	messages: string[],
	dataSetId: string,
	title: string,
	selectedData: any
): void {
	openBulkActionConfirmationModal({
		confirmDisplayType: 'danger',
		confirmLabel: Liferay.Language.get('delete'),
		message: messages,
		onConfirm: () => {
			executeBulkDeleteAction(apiURL, dataSetId, selectedData);
		},
		status: 'danger',
		title,
	});
}

/**
 * Entry point for bulk delete action.
 */
export default async function deleteAssetEntriesBulkAction({
	apiURL = '',
	dataSetId = '',
	getCustomBulkDeleteMessage,
	selectedData,
	showConfirmationModal,
	trashEnabled,
}: {
	apiURL?: string;
	dataSetId?: string;
	getCustomBulkDeleteMessage?: typeof getBulkDeleteMessage;
	selectedData: IBulkActionFDSData;
	showConfirmationModal?: boolean;
	trashEnabled?: boolean;
}): Promise<void> {
	await handleBulkDeletion({
		apiURL,
		dataSetId,
		getCustomBulkDeleteMessage,
		selectedData,
		showConfirmationModal,
		trashEnabled,
	});
}
