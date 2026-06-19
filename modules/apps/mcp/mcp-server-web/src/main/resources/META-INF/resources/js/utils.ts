/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataMask} from './types';

export function isSystemMask(dataMask: DataMask | null): boolean {
	return dataMask?.maskType?.key === 'system';
}

export function validateRegex(value: string): string {
	if (!value) {
		return '';
	}

	try {
		new RegExp(value);

		return '';
	}
	catch (error) {
		return Liferay.Language.get(
			'patterns-must-be-valid-regular-expressions'
		);
	}
}
