/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSModal} from '../../../common/utils/openCMSModal';
import ImportTranslationModalContent from '../../modal/ImportTranslationModalContent';

export default function importTranslationAction(
	data: ItemData,
	actionLink: string,
	loadData?: () => void
) {
	openCMSModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			ImportTranslationModalContent({
				actionLink,
				groupId: data.embedded.scopeId,
				itemId: data.embedded.id,
				itemName: data.embedded.title,
				loadData,
				onModalClose: closeModal,
			}),
		size: 'md',
	});
}
