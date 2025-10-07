/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSimpleInputModal} from 'frontend-js-components-web';

const ACTIONS = {
	taskAssign({assignURL, namespace, title}) {
		Liferay.Util.openModal({
			center: true,
			className: 'workflow-tasks-modal',
			containerProps: {},
			height: 370,
			id: namespace + 'assignToDialog',
			iframeBodyCssClass: 'task-dialog',
			size: 'lg',
			title,
			url: assignURL,
		});
	},
	taskAssignToMe({assignToMeURL, namespace, title}) {
		Liferay.Util.openModal({
			center: true,
			className: 'workflow-tasks-modal',
			containerProps: {},
			height: 280,
			id: namespace + 'assignToDialog',
			iframeBodyCssClass: 'task-dialog',
			size: 'lg',
			title,
			url: assignToMeURL,
		});
	},
	taskEditWorkflowTask({formSubmitURL, namespace, title}) {
		openSimpleInputModal({
			buttonSubmitLabel: Liferay.Language.get('done'),
			center: true,
			dialogTitle: title,
			formSubmitURL,
			mainFieldComponent: 'textarea',
			mainFieldLabel: Liferay.Language.get('comment'),
			mainFieldName: 'comment',
			mainFieldPlaceholder: Liferay.Language.get('comment'),
			namespace,
			onFormSuccess: () => window.location.reload(),
			required: false,
			size: 'lg',
		});
	},
	updateDueDate({namespace, title, updateDueDateURL}) {
		Liferay.Util.openModal({
			center: true,
			className: 'workflow-tasks-modal',
			containerProps: {},
			height: 370,
			id: namespace + 'updateDialog',
			iframeBodyCssClass: 'task-dialog',
			size: 'lg',
			title,
			url: updateDueDateURL,
		});
	},
};

export default function propsTransformer({items, ...props}) {
	return {
		...props,
		items: items.map((item) => {
			return {
				...item,
				onClick(event) {
					const action = item.data?.action;

					if (action) {
						event.preventDefault();

						ACTIONS[action](item.data);
					}
				},
			};
		}),
	};
}
