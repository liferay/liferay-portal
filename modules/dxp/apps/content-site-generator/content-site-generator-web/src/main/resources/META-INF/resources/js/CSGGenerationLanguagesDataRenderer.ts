/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getLanguageLabel} from './util/contentModel';

interface Args {
	value?: string;
}

export default function CSGGenerationLanguagesDataRenderer({
	value,
}: Args): HTMLElement {
	const span = document.createElement('span');

	if (!value) {
		return span;
	}

	span.textContent = value
		.split(',')
		.map((code) => code.trim())
		.filter(Boolean)
		.map((code) => getLanguageLabel(code))
		.join(', ');

	return span;
}
