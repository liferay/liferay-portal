/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/FieldMappingsFDSPropsTransformer';
import FieldMappingChannelFieldRenderer from '../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingChannelFieldRenderer';
import FieldMappingSourceAttributesRenderer from '../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingSourceAttributesRenderer';

describe('FieldMappingsFDSPropsTransformer', () => {
	it('registers the cell renderers under the names the table view declares', () => {
		const result = propsTransformer({});

		expect(result.customRenderers.tableCell).toEqual([
			{
				component: FieldMappingChannelFieldRenderer,
				name: 'channelFieldTableCellRenderer',
				type: 'internal',
			},
			{
				component: FieldMappingSourceAttributesRenderer,
				name: 'sourceAttributesTableCellRenderer',
				type: 'internal',
			},
		]);
	});

	it('forces hideManagementBarInEmptyState to true and preserves the other props', () => {
		const result = propsTransformer({
			apiURL: '/o/frontend-data-set-taglib/app',
			hideManagementBarInEmptyState: false,
			id: 'field-mappings',
		});

		expect(result.apiURL).toBe('/o/frontend-data-set-taglib/app');
		expect(result.hideManagementBarInEmptyState).toBe(true);
		expect(result.id).toBe('field-mappings');
	});
});
