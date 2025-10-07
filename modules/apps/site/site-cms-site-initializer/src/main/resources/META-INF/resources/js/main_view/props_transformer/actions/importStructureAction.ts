/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import ImportStructureModalContent from '../../modal/ImportStructureModalContent';

export default function importStructureAction(
	importURL: string,
	objectFolderExternalReferenceCode: string,
	loadData?: () => {}
) {
	openModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			ImportStructureModalContent({
				closeModal,
				importURL,
				loadData,
				objectFolderExternalReferenceCode,
			}),
		size: 'md',
	});
}
