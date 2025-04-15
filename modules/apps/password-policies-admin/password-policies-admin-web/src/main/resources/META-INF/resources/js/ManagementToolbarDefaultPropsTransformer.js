/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {createActionURL, getCheckedCheckboxes} from 'frontend-js-web';

const ACTIONS = {
	deletePasswordPolicies(portletNamespace, basePortletURL) {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const form = document.getElementById(
						`${portletNamespace}fm`
					);

					if (!form) {
						return;
					}

					form.setAttribute('method', 'post');

					const passwordPolicyIdsInput = form.querySelector(
						`#${portletNamespace}passwordPolicyIds`
					);

					if (passwordPolicyIdsInput) {
						passwordPolicyIdsInput.setAttribute(
							'value',
							getCheckedCheckboxes(
								form,
								`${portletNamespace}allRowIds`
							)
						);
					}

					const lifecycleInput = form.querySelector('#p_p_lifecycle');

					if (lifecycleInput) {
						lifecycleInput.setAttribute('value', '1');
					}

					const actionURL = createActionURL(basePortletURL, {
						'jakarta.portlet.action': 'deletePasswordPolicies',
					});

					submitForm(form, actionURL.toString());
				}
			},
		});
	},
};

export default function propsTransformer({
	additionalProps,
	initialActionDropdownItems,
	portletNamespace,
	...props
}) {
	const {basePortletURL} = additionalProps;

	return {
		...props,
		initialActionDropdownItems: initialActionDropdownItems.map((item) => {
			return {
				...item,
				onClick() {
					const action = item?.data?.action;

					ACTIONS[action](portletNamespace, basePortletURL);
				},
			};
		}),
		onActionButtonClick() {
			ACTIONS.deletePasswordPolicies(portletNamespace, basePortletURL);
		},
	};
}
