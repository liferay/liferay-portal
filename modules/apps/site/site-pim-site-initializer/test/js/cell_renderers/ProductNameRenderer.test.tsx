/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ProductNameRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/ProductNameRenderer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: (actions: any[], id: string) =>
		actions.find((action) => action?.data?.id === id),
	replaceTokens: (href: string, itemData: any) =>
		href.replace('{embedded.id}', itemData.embedded.id),
}));

const ACTIONS = [
	{
		data: {id: 'edit'},
		href: '/edit_content_item?objectEntryId={embedded.id}',
	},
];

describe('ProductNameRenderer', () => {
	it('links the product name to the edit page when the user can update', () => {
		render(
			<ProductNameRenderer
				actions={ACTIONS}
				itemData={{embedded: {actions: {update: {}}, id: '42'}}}
				value="Product A"
			/>
		);

		expect(screen.getByRole('link', {name: 'Product A'})).toHaveAttribute(
			'href',
			'/edit_content_item?objectEntryId=42'
		);
	});

	it('renders the product name as plain text when the user cannot update', () => {
		render(
			<ProductNameRenderer
				actions={ACTIONS}
				itemData={{embedded: {actions: {}, id: '42'}}}
				value="Product A"
			/>
		);

		expect(screen.queryByRole('link')).not.toBeInTheDocument();
		expect(screen.getByText('Product A')).toBeInTheDocument();
	});
});
