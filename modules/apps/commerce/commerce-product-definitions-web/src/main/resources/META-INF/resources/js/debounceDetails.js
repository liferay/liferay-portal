/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {slugify} from 'commerce-frontend-js';
import {debounce} from 'frontend-js-web';

export default function main({namespace}) {
	const form = document.getElementById(`${namespace}fm`);

	const nameInput = form.querySelector(`#${namespace}nameMapAsXML`);
	const urlInput = form.querySelector(`#${namespace}urlTitleMapAsXML`);
	const urlTitleInputLocalized = Liferay.component(
		`${namespace}urlTitleMapAsXML`
	);

	const handleOnNameInput = function () {
		const slug = slugify(nameInput.value);
		urlInput.value = slug;

		urlTitleInputLocalized.updateInputLanguage(slug);
	};

	nameInput.addEventListener('input', debounce(handleOnNameInput, 200));
}
