/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Tool} from '../../src/main/resources/META-INF/resources/js/types';
import {mockTool} from './mockTool';

export const mockPageTool: Tool = {
	description: 'GET /',
	inputSchema: {
		properties: {
			pageSize: {type: 'integer'},
		},
		type: 'object',
	},
	name: 'getMCPServerPromptsPage',
	outputSchema: {
		properties: {
			facets: {items: {type: 'object'}, type: 'array'},
			items: {items: mockTool.outputSchema, type: 'array'},
			lastPage: {type: 'integer'},
			page: {type: 'integer'},
			pageSize: {type: 'integer'},
			totalCount: {type: 'integer'},
		},
		type: 'object',
	},
};
