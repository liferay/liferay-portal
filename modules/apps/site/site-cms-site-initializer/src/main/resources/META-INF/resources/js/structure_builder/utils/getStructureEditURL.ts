/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure} from '../types/Structure';

export default function getStructureEditURL(structure: Structure) {
	if (!structure.id || !structure.type) {
		return '';
	}

	const url = new URL(window.location.href);

	url.searchParams.set('objectDefinitionId', structure.id.toString());
	url.searchParams.set('objectFolderExternalReferenceCode', structure.type);

	return url.href;
}
