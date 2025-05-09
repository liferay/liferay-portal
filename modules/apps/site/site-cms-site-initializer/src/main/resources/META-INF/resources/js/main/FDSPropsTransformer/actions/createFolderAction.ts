/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import FolderService from '../../../services/FolderService';
import CreationModalContent, {
	AssetLibrary,
} from '../../components/modal/CreationModalContent';

export type FolderData = {
	action: 'createFolder';
	assetLibraries: AssetLibrary[];
};

export default function createFolderAction(
	data: FolderData,
	additionalProps: {parentObjectEntryFolderExternalReferenceCode: string},
	loadData?: () => {}
) {
	openModal({
		center: true,
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			CreationModalContent({
				...data,
				closeModal,
				onSubmit: async ({groupId, name: title}) => {
					const {errorMessage, success} =
						await FolderService.createFolder(
							groupId,
							title,
							additionalProps.parentObjectEntryFolderExternalReferenceCode
						);

					if (success) {
						loadData?.();

						closeModal();

						openToast({
							message: sub(
								Liferay.Language.get(
									'x-was-created-successfully'
								),
								`<strong>${title}</strong>`
							),
							type: 'success',
						});
					}
					else {
						openToast({
							message: errorMessage,
							type: 'danger',
						});
					}
				},
				title: Liferay.Language.get('new-folder'),
			}),
		size: 'sm',
	});
}
