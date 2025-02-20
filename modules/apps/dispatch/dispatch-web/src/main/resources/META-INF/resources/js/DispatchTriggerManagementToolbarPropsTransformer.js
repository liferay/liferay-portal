/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';

export default function propsTransformer({
	additionalProps: {deleteEntriesURL, inputId, inputValue},
	portletNamespace,
	...props
}) {
	return {
		...props,
		onActionButtonClick(event, {item}) {
			if (item?.data?.action === 'deleteEntries') {
				openConfirmModal({
					message: Liferay.Language.get(
						'are-you-sure-you-want-to-delete-the-selected-trigger'
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

							const inputElement = document.getElementById(
								`${portletNamespace}${inputId}`
							);

							if (inputElement) {
								inputElement.setAttribute('value', inputValue);
							}

							submitForm(form, deleteEntriesURL);
						}
					},
				});
			}
		},
	};
}
