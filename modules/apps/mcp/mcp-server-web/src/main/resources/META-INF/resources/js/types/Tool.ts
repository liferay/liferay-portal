/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type JSONSchema = {
	additionalProperties?: JSONSchema | boolean;
	items?: JSONSchema;
	properties?: Record<string, JSONSchema>;
	readOnly?: boolean;
	type?: string;
	writeOnly?: boolean;
};

export type Tool = {
	description?: string;
	inputSchema?: JSONSchema;
	name: string;
	outputSchema?: JSONSchema;
};

export type ToolSet = {
	description?: string;
	name: string;
};

export type ToolSummary = Pick<Tool, 'name'>;
