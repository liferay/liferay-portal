/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/FieldMappingsFDSPropsTransformer';

describe('FieldMappingsFDSPropsTransformer', () => {
	it('registers a renderer for every custom column', () => {
		const {customRenderers} = propsTransformer({});

		expect(customRenderers.tableCell.map(({name}) => name)).toEqual([
			'channelFieldTableCellRenderer',
			'requiredTableCellRenderer',
			'sourceAttributeTableCellRenderer',
			'statusTableCellRenderer',
		]);
	});

	it('forces hideManagementBarInEmptyState to true and preserves the other props', () => {
		const result = propsTransformer({
			hideManagementBarInEmptyState: false,
			id: 'fieldMappings',
		});

		expect(result.hideManagementBarInEmptyState).toBe(true);
		expect(result.id).toBe('fieldMappings');
	});
});
