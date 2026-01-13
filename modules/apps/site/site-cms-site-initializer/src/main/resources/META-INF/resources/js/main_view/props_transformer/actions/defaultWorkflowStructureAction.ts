/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSModal} from '../../../common/utils/openCMSModal';
import AssignDefaultWorkflowModalContent from '../../modal/AssignDefaultWorkflowModalContent';

export default function defaultWorkflowStructureAction(structureIds: string[]) {
	openCMSModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			AssignDefaultWorkflowModalContent({
				closeModal,
				structureIds,
			}),
		size: 'md',
	});
}
