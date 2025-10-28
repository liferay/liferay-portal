/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch, sub} from 'frontend-js-web';

import SpaceService from '../../../common/services/SpaceService';
import {
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
	getScopeExternalReferenceCode,
} from '../../../common/utils/getScopeExternalReferenceCode';
import {
	displayDeleteSuccessToast,
	displayErrorToast,
} from '../../../common/utils/toastUtil';
import {DEFAULT_HEADERS} from '../utils/constants';
import displayUndoDeleteSuccessToast from '../utils/displayUndoDeleteSuccessToast';
import confirmAndDeleteEntryAction from './confirmAndDeleteEntryAction';

/**
 * Shows different success messages by having Recycle Bin enabled or not
 * Enabled: Toast with success message, undo action and link to the Recycle Bin
 * Disabled: Toast with default success message
 */
async function showSuccessToast(
	getURL: string,
	label: string,
	loadData: () => {},
	method: string
) {
	const getItemResponse = await fetch(getURL, {
		headers: {
			...DEFAULT_HEADERS,
			'Content-Type': 'application/json',
		},
		method,
	});

	const deletedEntry = await getItemResponse.json();

	if (getItemResponse.ok) {
		loadData();

		const {actions} = deletedEntry;

		if (actions) {
			displayUndoDeleteSuccessToast(
				label,
				loadData,
				actions.restore.method,
				actions.restore.href
			);
		}
	}
	else {
		if (getItemResponse.status === 404) {
			loadData();
			displayDeleteSuccessToast(label);
		}
		else {
			throw new Error();
		}
	}
}

export default async function deleteItemAction(
	itemData: ItemData,
	loadData: () => {}
) {
	const {actions, embedded} = itemData;

	if (embedded) {
		const itemSpace = await SpaceService.getSpace(
			getScopeExternalReferenceCode(itemData)
		);

		if (!itemSpace.settings?.trashEnabled) {
			confirmAndDeleteEntryAction({
				bodyHTML:
					itemData.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME
						? sub(
								Liferay.Language.get(
									'delete-folder-confirmation-body'
								),
								itemData.title
							)
						: sub(
								Liferay.Language.get(
									'delete-asset-confirmation-body'
								),
								itemData.title
							),
				deleteAction: actions.delete,
				loadData,
				successMessage: sub(
					Liferay.Language.get('x-was-successfully-deleted'),
					`<strong>${itemData.title}</strong>`
				),
				title: sub(
					Liferay.Language.get('delete-asset-confirmation-title'),
					itemData.title
				),
			});
		}
		else {
			try {
				await fetch(actions.delete.href, {
					headers: DEFAULT_HEADERS,
					method: actions.delete.method,
				});

				showSuccessToast(
					actions.get.href,
					embedded.title,
					loadData,
					actions.get.method
				);
			}
			catch (error) {
				displayErrorToast();
			}
		}
	}
}
