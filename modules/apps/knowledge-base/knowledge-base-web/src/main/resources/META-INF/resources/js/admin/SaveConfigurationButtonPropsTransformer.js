/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function propsTransformer({
	additionalProps: {emailParam},
	portletNamespace,
	...props
}) {
	return {
		...props,
		onClick() {
			const form = document.getElementById(`${portletNamespace}fm`);

			if (form) {
				const emailParamEditor =
					emailParam && window[`${portletNamespace}${emailParam}`];

				if (emailParamEditor) {
					document.getElementById(
						`${portletNamespace}${emailParam}Body`
					).value = emailParamEditor.getHTML();
				}

				submitForm(form);
			}
		},
	};
}
