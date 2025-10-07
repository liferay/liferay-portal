/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CommerceServiceProvider,
	modalUtils,
	slugify,
} from 'commerce-frontend-js';
import {createPortletURL, debounce} from 'frontend-js-web';

export default function ({
	defaultLanguageId,
	editOptionURL,
	namespace,
	windowState,
}) {
	const form = document.getElementById(namespace + 'fm');
	const keyInput = form.querySelector('#' + namespace + 'key');
	const nameInput = form.querySelector('#' + namespace + 'name');
	const handleOnNameInput = () => {
		keyInput.value = slugify(nameInput.value);
	};
	nameInput.addEventListener('input', debounce(handleOnNameInput, 200));

	const AdminCatalogResource = CommerceServiceProvider.AdminCatalogAPI('v1');

	Liferay.provide(window, namespace + 'apiSubmit', () => {
		modalUtils.isSubmitting();
		const formattedData = {
			fieldType: '',
			key: '',
			name: {},
		};

		formattedData.fieldType = document.getElementById(
			namespace + 'commerceOptionTypeKey'
		).value;
		formattedData.key = keyInput.value;
		formattedData.name[defaultLanguageId] = nameInput.value;

		if (defaultLanguageId !== Liferay.ThemeDisplay.getDefaultLanguageId()) {
			formattedData.name[Liferay.ThemeDisplay.getDefaultLanguageId()] =
				nameInput.value;
		}

		AdminCatalogResource.createOption(formattedData)
			.then((cpOption) => {
				const redirectURL = createPortletURL(editOptionURL, {
					cpOptionId: cpOption.id,
					p_p_state: windowState,
				});

				modalUtils.closeAndRedirect(redirectURL);
			})
			.catch(modalUtils.onSubmitFail);
	});
}
