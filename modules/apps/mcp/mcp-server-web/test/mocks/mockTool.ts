/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Tool} from '../../src/main/resources/META-INF/resources/js/types';

export const mockTool: Tool = {
	description: 'GET /{mCPServerPromptId}',
	inputSchema: {
		properties: {
			mCPServerPromptId: {type: 'string'},
		},
		type: 'object',
	},
	name: 'getMCPServerPrompt',
	outputSchema: {
		properties: {
			'actions': {type: 'object'},
			'auditEvents': {
				items: {
					properties: {
						creator: {
							properties: {
								name: {readOnly: true, type: 'string'},
							},
							type: 'object',
						},
						eventType: {readOnly: true, type: 'string'},
					},
					type: 'object',
				},
				readOnly: true,
				type: 'array',
			},
			'description': {type: 'string'},
			'embeddedTaxonomyCategory': {readOnly: true, type: 'object'},
			'friendlyUrlPath_i18n': {
				additionalProperties: {type: 'string'},
				type: 'object',
			},
			'keywords': {items: {type: 'string'}, type: 'array'},
			'modifiedBy': {
				properties: {
					id: {readOnly: true, type: 'integer'},
					name: {readOnly: true, type: 'string'},
					userGroupBriefs: {
						items: {
							properties: {
								id: {readOnly: true, type: 'integer'},
								name: {readOnly: true, type: 'string'},
							},
							type: 'object',
						},
						type: 'array',
					},
				},
				type: 'object',
			},
			'name': {type: 'string'},
			'promptStatus': {
				properties: {
					key: {type: 'string'},
					name: {type: 'string'},
					name_i18n: {
						additionalProperties: {type: 'string'},
						type: 'object',
					},
				},
				type: 'object',
			},
			'taxonomyCategoryBriefs': {
				items: {
					properties: {
						scope: {
							properties: {
								key: {type: 'string'},
								label: {type: 'string'},
							},
							type: 'object',
						},
						taxonomyCategoryName: {readOnly: true, type: 'string'},
					},
					type: 'object',
				},
				type: 'array',
			},
			'taxonomyCategoryIds': {
				items: {type: 'integer'},
				type: 'array',
				writeOnly: true,
			},
			'x-schema-name': {type: 'string'},
		},
		type: 'object',
	},
};
