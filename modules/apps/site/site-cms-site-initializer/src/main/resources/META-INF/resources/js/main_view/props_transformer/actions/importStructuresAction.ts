/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSModal} from '../../../common/utils/openCMSModal';
import ImportStructuresModalContent from '../../modal/ImportStructuresModalContent';

export default function importStructuresAction(
	importURL: string,
	loadData?: () => void
) {
	openCMSModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			ImportStructuresModalContent({
				closeModal,
				importURL,
				loadData,
			}),
		size: 'md',
	});
}
