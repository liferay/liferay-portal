/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import FolderService from '../../../common/services/FolderService';
import {AssetLibrary} from '../../../common/types/AssetLibrary';
import CreationModalContent from '../../modal/CreationModalContent';

export type FolderData = {
	action: 'createFolder';
	assetLibraries: AssetLibrary[];
	baseAssetLibraryViewURL: string;
	baseFolderViewURL: string;
	parentObjectEntryFolderExternalReferenceCode: string;
};

export default function createFolderAction(
	data: FolderData,
	loadData?: () => {}
) {
	openModal({
		center: true,
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			CreationModalContent({
				...data,
				closeModal,
				onSubmit: async ({groupId, name: title}) => {
					const {data: folderData, error} =
						await FolderService.createFolder<{
							id: string;
							scopeKey: string;
							title: string;
						}>(
							groupId,
							data.parentObjectEntryFolderExternalReferenceCode,
							title
						);

					if (!error) {
						loadData?.();

						closeModal();

						const {
							id: folderId,
							scopeKey: spaceName,
							title: folderName,
						} = folderData || {};

						openToast({
							message: sub(
								Liferay.Language.get(
									'x-was-created-successfully-to-x-space'
								),
								[
									`<a href="${data.baseFolderViewURL}${folderId}" class="alert-link lead"><strong>${folderName}</strong></a>`,
									`<a href="${data.baseAssetLibraryViewURL}${groupId}" class="alert-link lead"><strong>${spaceName}</strong></a>`,
								]
							),
							type: 'success',
						});
					}
					else {
						openToast({
							message: error,
							type: 'danger',
						});
					}
				},
				title: Liferay.Language.get('new-folder'),
			}),
		size: 'sm',
	});
}
