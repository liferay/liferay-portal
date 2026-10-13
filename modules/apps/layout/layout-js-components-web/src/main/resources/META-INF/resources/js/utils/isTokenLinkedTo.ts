/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Token} from '../types/ColorPicker';

export default function isTokenLinkedTo({
	editedTokenValues,
	linkedName,
	name,
}: {
	editedTokenValues: Record<string, Token> | undefined;
	linkedName: string;
	name: string;
}) {
	const visitedNames = new Set<string>();

	let nextName: string | undefined = name;

	while (nextName && !visitedNames.has(nextName)) {
		if (nextName === linkedName) {
			return true;
		}

		visitedNames.add(nextName);

		nextName = editedTokenValues?.[nextName]?.name;
	}

	return false;
}
