/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getLocalizedFieldValue from './getLocalizedFieldValue';

interface LocalizedRichTextDataRendererProps {
	itemData: {[key: string]: any};
	options: {fieldName: string};
	value?: any;
}

const domParser = new DOMParser();

function stripHTML(value: string) {
	const {body} = domParser.parseFromString(value, 'text/html');

	return body.textContent ?? '';
}

export default function LocalizedRichTextDataRenderer({
	itemData,
	options,
	value,
}: LocalizedRichTextDataRendererProps) {
	const localizedFieldValue = getLocalizedFieldValue(
		itemData,
		options?.fieldName
	);

	const resolvedValue =
		localizedFieldValue === undefined ? value : localizedFieldValue;

	if (typeof resolvedValue === 'string') {
		return stripHTML(resolvedValue);
	}

	return resolvedValue ?? '';
}
