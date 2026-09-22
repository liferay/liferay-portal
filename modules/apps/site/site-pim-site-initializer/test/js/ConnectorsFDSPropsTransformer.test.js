/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/ConnectorsFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: () => null,
	replaceTokens: (href) => href,
}));

describe('ConnectorsFDSPropsTransformer', () => {
	it('marks the delete action as text-danger and leaves the other actions unchanged', () => {
		const result = propsTransformer({
			itemsActions: [
				{data: {id: 'edit'}, icon: 'pencil'},
				{data: {id: 'delete'}, icon: 'trash'},
			],
		});

		expect(result.itemsActions[0].className).toBeUndefined();
		expect(result.itemsActions[1].className).toBe('text-danger');
	});

	it('forces hideManagementBarInEmptyState to true and preserves the other props', () => {
		const result = propsTransformer({
			apiURL: '/o/c/pimconnectors',
			hideManagementBarInEmptyState: false,
			id: 'connectors',
		});

		expect(result.apiURL).toBe('/o/c/pimconnectors');
		expect(result.hideManagementBarInEmptyState).toBe(true);
		expect(result.id).toBe('connectors');
	});
});
