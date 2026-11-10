/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';

import {
	IBulkActionFDSDataItemTransformed,
	IBulkActionTaskStarter,
	IBulkActionTaskStarterDTO,
	IBulkActionType,
} from '../../../common/types/BulkActionTask';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {downloadBlob} from '../../../common/utils/downloadBlob';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import {BulkActionTaskStarter} from '../../bulk_actions_monitor/services/BulkActionTaskStarter';

export async function triggerAssetDownloadBulkAction(
	dto: IBulkActionTaskStarterDTO<keyof IBulkActionType>
): Promise<void> {
	const bulkAction: IBulkActionTaskStarter = new BulkActionTaskStarter({
		...dto,
	});

	const downloadableItems = bulkAction.payload.bulkActionItems.filter(
		(bulkActionItem: IBulkActionFDSDataItemTransformed) =>
			bulkActionItem.file ||
			bulkActionItem.className === OBJECT_ENTRY_FOLDER_CLASS_NAME
	);

	if (!dto.selectedData.selectAll) {
		if (!downloadableItems.length) {
			displayErrorToast(
				Liferay.Language.get(
					'unable-to-process-the-bulk-download.-please-check-your-selection-and-try-again'
				)
			);

			return;
		}

		if (
			downloadableItems.length !==
			bulkAction.payload.bulkActionItems.length
		) {
			openToast({
				message: Liferay.Language.get(
					'you-have-selected-both-content-and-file-assets.-only-file-assets-can-be-downloaded.-content-assets-will-be-skipped'
				),
				type: 'warning',
			});
		}
	}

	openToast({
		message: dto.selectedData.selectAll
			? Liferay.Language.get(
					'the-download-of-all-selected-files-is-being-prepared.-please-do-not-close-this-window-or-navigate-to-another-section'
				)
			: downloadableItems.length === 1
				? sub(
						Liferay.Language.get(
							'the-download-of-one-file-is-being-prepared.-please-do-not-close-this-window-or-navigate-to-another-section'
						),
						[downloadableItems.length]
					)
				: sub(
						Liferay.Language.get(
							'the-download-of-x-files-is-being-prepared.-please-do-not-close-this-window-or-navigate-to-another-section'
						),
						[downloadableItems.length]
					),
		type: 'warning',
	});

	return fetch(bulkAction.postURL, {
		body: JSON.stringify(bulkAction.payload),
		headers: new Headers({
			'Accept': 'application/zip',
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Content-Type': 'application/json',
		}),
		method: 'POST',
	}).then(async (response): Promise<void> => {
		if (!response.ok) {
			displayErrorToast();
		}
		else {
			openToast({
				message: Liferay.Language.get(
					'the-download-will-begin-shortly'
				),
				title: Liferay.Language.get('success'),
				type: 'success',
			});

			await downloadBlob(response);
		}
	});
}
