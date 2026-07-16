/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface AudiencesCriteria {
	icon: string;
	inputType: 'boolean' | 'date' | 'select' | 'text';
	key: string;
	label: string;
	options: Array<{label: string; value: string}>;
	type: 'boolean' | 'number' | 'set' | 'string';
}

export interface AudiencesCriteriaRulesGroup {
	conjunction?: string;
	rules?: Array<SerializedGroup | SerializedRule>;
}

export type CriteriaNode = Group | Rule;

export interface Group {
	conjunction: string;
	id: string;
	items: CriteriaNode[];
}

export interface SerializedGroup {
	conjunction: string;
	rules: Array<SerializedGroup | SerializedRule>;
}

export interface SerializedRule {
	attribute: string;
	operator: string;
	value: string;
}

export interface AudiencesCriteriaType {
	audiencesCriterias: AudiencesCriteria[];
	key: string;
	label: string;
}

export interface Rule {
	attribute: string;
	id: string;
	operator: string;
	value: string;
}
