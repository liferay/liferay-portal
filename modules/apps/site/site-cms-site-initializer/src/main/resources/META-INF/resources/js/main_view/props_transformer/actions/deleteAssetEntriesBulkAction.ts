/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import SpaceService from '../../../common/services/SpaceService';
import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {isFromRecycleBin} from '../utils/isFromRecycleBin';
import {triggerAssetBulkAction} from './triggerAssetBulkAction';

/**
 * Executes the bulk delete action.
 */
export function executeBulkDeleteAction(
	apiURL: string,
	selectedData: IBulkActionFDSData,
	processClose?: () => void
): void {
	triggerAssetBulkAction({
		apiURL,
		onCreateSuccess: () => {
			processClose?.();
		},
		selectedData,
		type: 'DeleteBulkAction',
	});
}

/**
 * Returns the confirmation message and title for bulk delete modal.
 */
function getBulkDeleteMessage(selectedData: any): {
	confirmationMessage: string;
	title: string;
} {
	if (selectedData.selectAll) {
		return {
			confirmationMessage: Liferay.Language.get(
				'delete-all-entries-confirmation'
			),
			title: Liferay.Language.get('delete-all-entries'),
		};
	}
	else if (selectedData.items.length > 1) {
		return {
			confirmationMessage: sub(
				Liferay.Language.get('delete-entries-confirmation'),
				[selectedData.items.length]
			),
			title: Liferay.Language.get('delete-entries'),
		};
	}

	return {
		confirmationMessage: Liferay.Language.get('delete-entry-confirmation'),
		title: Liferay.Language.get('delete-entry'),
	};
}

/**
 * Fetches asset library spaces for the given items.
 */
async function getEntriesSpaces(
	items: IBulkActionFDSData['items'] = []
): Promise<any[]> {
	const promises = items
		.filter((item) => item.embedded.scopeId)
		.map((item) =>
			SpaceService.getSpace({
				spaceId: item.embedded.scopeId as number,
			})
		);

	return (await Promise.all(promises)).filter(Boolean);
}

/**
 * Handles bulk deletion logic and modal display based on trash status of spaces.
 */
async function handleBulkDeletion({
	apiURL,
	selectedData,
}: {
	apiURL: string;
	selectedData: IBulkActionFDSData;
}): Promise<void> {
	const spaces = await getEntriesSpaces(selectedData?.items || []);

	// Trash status checks

	const allEntriesHaveTrashEnabled = spaces.every(
		(space) => space.settings.trashEnabled
	);
	const noEntriesHaveTrashEnabled = spaces.every(
		(space) => !space.settings.trashEnabled
	);
	const someEntriesHaveTrashEnabled = spaces.some(
		(space) => space.settings.trashEnabled
	);

	const {confirmationMessage, title} = getBulkDeleteMessage(selectedData);

	// Scenario 1: All spaces have trash disabled

	if (noEntriesHaveTrashEnabled) {
		showModal(apiURL, confirmationMessage, title, selectedData);
	}

	// Scenario 2: Some spaces have trash enabled, but not all

	else if (someEntriesHaveTrashEnabled && !allEntriesHaveTrashEnabled) {
		showModal(
			apiURL,
			Liferay.Language.get('bulk-delete-cms-entries-confirmation'),
			Liferay.Language.get('delete-entries'),
			selectedData
		);
	}

	// Scenario 3: All spaces have trash enabled

	else if (allEntriesHaveTrashEnabled) {
		if (!isFromRecycleBin(selectedData)) {
			executeBulkDeleteAction(apiURL, selectedData);
		}
		else {
			showModal(apiURL, confirmationMessage, title, selectedData);
		}
	}
}

/**
 * Shows the bulk delete confirmation modal.
 */
async function showModal(
	apiURL: string,
	confirmationMessage: string,
	title: string,
	selectedData: any
): Promise<void> {
	openModal({
		bodyHTML: `
			<div>
				<p>
					${confirmationMessage}
				</p>
			</div>
		`,
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				onClick: ({processClose}: {processClose: () => void}) => {
					processClose();
				},
				type: 'cancel',
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('delete'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					processClose();

					executeBulkDeleteAction(apiURL, selectedData, processClose);
				},
			},
		],
		center: true,
		status: 'danger',
		title,
	});
}

/**
 * Entry point for bulk delete action.
 */
export default async function deleteAssetEntriesBulkAction({
	apiURL = '',
	selectedData,
}: {
	apiURL?: string;
	selectedData: IBulkActionFDSData;
}): Promise<void> {
	await handleBulkDeletion({apiURL, selectedData});
}
