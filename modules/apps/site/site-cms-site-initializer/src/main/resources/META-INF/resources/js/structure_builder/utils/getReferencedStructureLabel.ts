/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure, Structures} from '../types/Structure';

export default function getReferencedStructureLabel(
	structureErc: Structure['erc'],
	structures: Structures
) {
	const structure = structures.get(structureErc);

	if (!structure) {
		return '';
	}

	return structure.label[Liferay.ThemeDisplay.getDefaultLanguageId()] || '';
}
