/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import OrderableTable from '../../src/main/resources/META-INF/resources/js/components/OrderableTable';

const ITEMS = [
	{externalReferenceCode: 'MASK_1', name: 'Email Address'},
	{externalReferenceCode: 'MASK_2', name: 'Phone Number'},
	{externalReferenceCode: 'MASK_3', name: 'Email Alias'},
];

function renderTable(onOrderChange = jest.fn()) {
	return render(
		<OrderableTable
			fields={[{label: 'Name', name: 'name'}]}
			items={ITEMS}
			noItemsButtonLabel="add"
			noItemsDescription="no items yet"
			noItemsTitle="no items"
			onOrderChange={onOrderChange}
		/>
	);
}

describe('OrderableTable', () => {
	it('keeps the rows matching the search query', async () => {
		renderTable();

		await userEvent.type(screen.getByPlaceholderText('search'), 'email');

		const table = screen.getByRole('table');

		expect(table).toHaveTextContent('Email Address');
		expect(table).toHaveTextContent('Email Alias');
		expect(table).not.toHaveTextContent('Phone Number');
	});

	it('allows reordering while there is no search query', () => {
		renderTable();

		for (const dragButton of screen.getAllByRole('button', {
			name: /drag/i,
		})) {
			expect(dragButton).toBeEnabled();
		}
	});

	it('prevents reordering a partial list while searching', async () => {
		renderTable();

		await userEvent.type(screen.getByPlaceholderText('search'), 'email');

		const dragButtons = screen.getAllByRole('button', {name: /drag/i});

		expect(dragButtons).toHaveLength(2);

		for (const dragButton of dragButtons) {
			expect(dragButton).toBeDisabled();
		}
	});
});
