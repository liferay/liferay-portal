/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface Action {
	disabled?: boolean;
	name: string;
	symbolLeft?: string;
	symbolRight?: string;
	type:
		| 'Improve Writing'
		| 'Fix Spelling Grammar'
		| 'Translate To'
		| 'Make Shorter'
		| 'Make Longer'
		| 'Generate Based On Title';
}
