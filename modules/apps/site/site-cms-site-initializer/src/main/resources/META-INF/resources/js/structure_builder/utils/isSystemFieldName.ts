/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SystemFieldNames} from '../types/SystemFieldNames';

export default function isSystemFieldName({
	name,
	objectDefinitionERC,
	systemFieldNames,
}: {
	name: string;
	objectDefinitionERC: string;
	systemFieldNames: SystemFieldNames;
}): boolean {
	return Boolean(systemFieldNames[objectDefinitionERC]?.includes(name));
}
