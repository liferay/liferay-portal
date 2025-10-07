/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {
	displayDeleteSuccessToast,
	displayErrorToast,
} from '../../../common/utils/toastUtil';
import {DEFAULT_HEADERS} from '../utils/constants';
import displayUndoDeleteSuccessToast from '../utils/displayUndoDeleteSuccessToast';

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

	if (actions && embedded) {
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
		catch {
			displayErrorToast();
		}
	}
	else {
		displayErrorToast();
	}
}
