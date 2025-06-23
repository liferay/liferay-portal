/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ViewportSizeSelector from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/ViewportSizeSelector';

const INITIAL_STATE = {
	selectedViewportSize: 'desktop',
};

const renderComponent = ({onSelect = () => {}, state = INITIAL_STATE} = {}) => {
	return render(
		<ViewportSizeSelector
			onSizeSelected={onSelect}
			selectedSize={state.selectedViewportSize}
		/>
	);
};

describe('ViewportSizeSelector', () => {
	it('renders ViewportSizeSelector component', () => {
		renderComponent();

		expect(screen.getByLabelText('Desktop')).toBeInTheDocument();
		expect(screen.getByLabelText('Mobile')).toBeInTheDocument();
		expect(screen.getByLabelText('Tablet')).toBeInTheDocument();
	});

	it('renders ViewportSizeSelector dropdown button and makes sure it has tooltip and aria-label', () => {
		renderComponent();

		expect(screen.getByTitle('select-a-viewport')).toBeInTheDocument();
		expect(
			screen.getByLabelText('select-a-viewport.-current-viewport-Desktop')
		).toBeInTheDocument();
	});

	it('calls onSizeSelected with sizeId when a size is selected', async () => {
		const onSelect = jest.fn();
		renderComponent({
			onSelect,
		});

		await userEvent.click(screen.getByLabelText('Mobile'));

		expect(onSelect).toHaveBeenLastCalledWith('mobile');
	});

	it('calls onSizeSelected with sizeId when a size is selected from the dropdown', async () => {
		const onSelect = jest.fn();

		renderComponent({
			onSelect,
		});

		await userEvent.click(screen.getByRole('combobox'));

		const option = screen.getByText('Mobile');

		await waitFor(() => {
			userEvent.click(option);

			expect(onSelect).toHaveBeenLastCalledWith('mobile');
		});
	});
});
