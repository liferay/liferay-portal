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

function OrderableTableWrapper({
	items,
	onOrderChange,
}: {
	items: Array<any>;
	onOrderChange: (args: {order: string}) => void;
}) {
	return (
		<OrderableTable
			fields={[{label: 'Name', name: 'name'}]}
			items={items}
			noItemsButtonLabel="add"
			noItemsDescription="no items yet"
			noItemsTitle="no items"
			onOrderChange={onOrderChange}
		/>
	);
}

function renderTable(onOrderChange = jest.fn()) {
	return render(
		<OrderableTableWrapper items={ITEMS} onOrderChange={onOrderChange} />
	);
}

async function reorderFirstRow() {
	const [dragButton] = screen.getAllByRole('button', {name: /drag/i});

	dragButton.focus();

	await userEvent.keyboard('{Enter}{ArrowDown}{Enter}');
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

	it('reorders the items with the keyboard', async () => {
		const onOrderChange = jest.fn();

		renderTable(onOrderChange);

		await reorderFirstRow();

		expect(onOrderChange).toHaveBeenCalledWith({
			order: 'MASK_2,MASK_1,MASK_3',
		});
	});

	it('reorders again after the items come back in their previous order', async () => {
		const onOrderChange = jest.fn();

		const {rerender} = render(
			<OrderableTableWrapper
				items={ITEMS}
				onOrderChange={onOrderChange}
			/>
		);

		await reorderFirstRow();

		rerender(
			<OrderableTableWrapper
				items={[...ITEMS]}
				onOrderChange={onOrderChange}
			/>
		);

		await reorderFirstRow();

		expect(onOrderChange).toHaveBeenCalledTimes(2);
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
