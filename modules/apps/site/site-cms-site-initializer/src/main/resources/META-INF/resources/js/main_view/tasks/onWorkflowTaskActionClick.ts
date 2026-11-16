/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSModal} from '../../common/utils/openCMSModal';
import AssignToModalContent from '../home/modal/AssignToModalContent';
import UpdateDueDateModalContent from '../home/modal/UpdateDueDateModalContent';

export function onWorkflowTaskActionClick({
	action,
	itemData,
	loadData,
}: {
	action: {data?: {id?: string}};
	itemData: {embedded?: {dateDue?: string; id?: string}};
	loadData: () => Promise<void>;
}) {
	const workflowTaskId = Number(itemData.embedded?.id);

	if (action?.data?.id === 'assignToMeWorkflowTask') {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				AssignToModalContent({
					assignable: false,
					closeModal,
					loadData,
					workflowTaskId,
				}),
			size: 'md',
		});
	}
	else if (action?.data?.id === 'assignToWorkflowTask') {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				AssignToModalContent({
					assignable: true,
					closeModal,
					loadData,
					workflowTaskId,
				}),
			size: 'md',
		});
	}
	else if (action?.data?.id === 'updateDueDateWorkflowTask') {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				UpdateDueDateModalContent({
					closeModal,
					dueDate: itemData.embedded?.dateDue as string,
					loadData,
					workflowTaskId,
				}),
			size: 'md',
		});
	}
}
