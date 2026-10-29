/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const APPLY_FRAGMENT_CODE_EVENT =
	'fragment:aiAssistant:applyFragmentCode';

export const APPLY_OBJECT_FIELD_VALUES_EVENT =
	'cms:aiAssistant:applyObjectFieldValues';

export const EDIT_FRAGMENT_AGENT_EXTERNAL_REFERENCE_CODE = 'L_EDIT_FRAGMENT';

export const GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE =
	'L_GENERATE_FIELD_VALUE';

export interface ApplyFragmentCodePayload {
	configuration: object;
	css: string;
	html: string;
	js: string;
	summary: string;
}

export interface ApplyObjectFieldValuesPayload {
	values: Record<string, string>;
}
