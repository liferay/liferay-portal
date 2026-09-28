/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const TYPE_DYNAMIC_VALUE = 'dynamicValue';

export const TYPE_FIXED_VALUE = 'fixedValue';

export interface IFieldMapping {
	id?: number;
	key?: string;
	sourceClassName: string;
	sourceFieldName: string;
	type: string;
	value: string;
}

export interface IObjectDefinition {
	className: string;
	label: string;
	objectFields: IObjectField[];
}

export interface IObjectField {
	label: string;
	name: string;
}
