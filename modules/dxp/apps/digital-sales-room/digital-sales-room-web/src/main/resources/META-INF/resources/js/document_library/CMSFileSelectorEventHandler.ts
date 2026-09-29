/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSFileSelectorModal} from '@liferay/frontend-js-item-selector-web';

import {
	displayErrorToast,
	displaySuccessToast,
} from '../common/utils/toastUtil';

interface CMSFileEventData {
	folderId?: number;
	groupId: number;
	redirect?: string;
}

interface CMSFileItem {
	description: string;
	embedded: {
		file?: {
			link?: {
				href?: string;
			};
			mimeType: string;
			thumbnailURL: string;
		};
		id: number;
		title: string;
		videoURL?: string;
	};
	title: string;
}

function uploadCMSFileToDocumentLibrary(
	items: CMSFileItem[],
	folderId: number | string,
	redirect: string
) {
	const uploadPromises = items.map((item) => {
		const fileLink = item.embedded?.file?.link?.href;

		if (!fileLink) {
			return Promise.resolve();
		}

		const fileName = item.title || 'untitled';

		return Liferay.Util.fetch(fileLink)
			.then((response: Response) => response.blob())
			.then((blob: Blob) => {
				const formData = new FormData();

				formData.append('file', blob, fileName);

				const uploadURL =
					parseInt(String(folderId), 10) > 0
						? `/o/headless-delivery/v1.0/document-folders/${folderId}/documents`
						: `/o/headless-delivery/v1.0/sites/${Liferay.ThemeDisplay.getSiteGroupId()}/documents`;

				return Liferay.Util.fetch(uploadURL, {
					body: formData,
					method: 'POST',
				});
			})
			.then((response: Response) => {
				if (!response.ok) {
					throw new Error(`Failed to upload ${fileName}`);
				}

				return response.json().then((item) => {
					Liferay.fire('fileEntrySaved', {
						fileEntryId: item.id,
						fileName: item.fileName,
						groupId: Liferay.ThemeDisplay.getScopeGroupId(),
					});

					return item;
				});
			});
	});

	Promise.all(uploadPromises)
		.then(() => {
			displaySuccessToast();

			window.location.href = redirect;
		})
		.catch(() => {
			displayErrorToast();
		});
}

function handleOpenCMSFileSelector(event: {data?: CMSFileEventData}) {
	const data = event.data || ({} as CMSFileEventData);

	const folderId = data.folderId || 0;
	const groupId = data.groupId;
	const redirect = data.redirect || window.location.href;

	openCMSFileSelectorModal({
		allowDragAndDrop: false,
		config: {
			multiSelect: true,
		},
		folderMemoryKey: `cms-file-selector-document-library:${groupId}`,
		groupId,
		onSelect(items: CMSFileItem[]) {
			if (!items || !items.length) {
				return;
			}

			uploadCMSFileToDocumentLibrary(items, folderId, redirect);
		},
	});
}

export default function CMSFileSelectorEventHandler() {
	Liferay.detach('openCMSFileSelector', handleOpenCMSFileSelector);
	Liferay.on('openCMSFileSelector', handleOpenCMSFileSelector);

	return {
		dispose() {
			Liferay.detach('openCMSFileSelector', handleOpenCMSFileSelector);
		},
	};
}
