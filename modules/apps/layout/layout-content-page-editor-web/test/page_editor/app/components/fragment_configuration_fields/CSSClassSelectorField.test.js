/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CSSClassSelectorField from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/CSSClassSelectorField';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

const renderCSSSelectorField = ({
	field = {label: 'css', name: 'cssClasses'},
	onValueSelect = () => {},
	value = null,
} = {}) => {
	return render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				layoutData: {
					items: {
						itemId: {
							config: {
								cssClasses: ['otherClass1', 'otherClass2'],
							},
							itemId: 'itemId',
							type: LAYOUT_DATA_ITEM_TYPES.container,
						},
					},
				},
			})}
		>
			<CSSClassSelectorField
				field={field}
				onValueSelect={onValueSelect}
				value={value}
			/>
		</StoreAPIContextProvider>
	);
};

describe('CSSClassSelectorField', () => {
	beforeEach(() => {
		window.document.createRange = () => ({
			cloneRange: (range) => range,
			getBoundingClientRect: () => 1,
			getClientRects: () => 1,
			setEnd: () => {},
			setStart: () => {},
		});
	});

	it('renders', () => {
		renderCSSSelectorField();

		expect(screen.getByLabelText('css-classes')).toBeInTheDocument();
	});

	it('renders current css classes', () => {
		renderCSSSelectorField({
			value: ['customClass1', 'customClass2'],
		});

		expect(screen.getByText('customClass1')).toBeInTheDocument();
		expect(screen.getByText('customClass1')).toBeInTheDocument();
	});

	it('calls onValueSelect when selecting a new class', async () => {
		const onValueSelect = jest.fn();

		renderCSSSelectorField({
			onValueSelect,
		});

		const cssClassesInput = screen.getByLabelText('css-classes');

		await userEvent.type(cssClassesInput, 'customClass1');
		fireEvent.keyDown(cssClassesInput, {key: 'Enter'});

		await waitFor(() => {
			expect(screen.getByText('customClass1')).toBeInTheDocument();

			expect(onValueSelect).toBeCalledWith('cssClasses', [
				'customClass1',
			]);
		});
	});

	it('adds classes with comma, enter and space', async () => {
		renderCSSSelectorField();

		const cssClassesInput = screen.getByLabelText('css-classes');

		fireEvent.change(cssClassesInput, {target: {value: 'customClass1'}});

		await userEvent.type(cssClassesInput, '{Enter}');

		await waitFor(() => {
			expect(screen.getByText('customClass1')).toBeInTheDocument();
		});

		fireEvent.change(cssClassesInput, {target: {value: 'customClass2'}});

		await userEvent.type(cssClassesInput, ',');

		await waitFor(() => {
			expect(screen.getByText('customClass2')).toBeInTheDocument();
		});

		fireEvent.change(cssClassesInput, {target: {value: 'customClass3'}});
		await userEvent.type(cssClassesInput, ' ');

		await waitFor(() => {
			expect(screen.getByText('customClass3')).toBeInTheDocument();
		});
	});

	it('removes cssClass when clicking remove button', async () => {
		const onValueSelect = jest.fn();

		renderCSSSelectorField({
			onValueSelect,
			value: ['customClass1'],
		});

		await userEvent.click(screen.getByLabelText('Remove customClass1'));

		expect(screen.queryByText('customClass1')).not.toBeInTheDocument();

		expect(onValueSelect).toBeCalledWith('cssClasses', []);
	});

	it('shows modal with create option', async () => {
		renderCSSSelectorField();

		const cssClassesInput = screen.getByLabelText('css-classes');

		await userEvent.type(cssClassesInput, 'customClass1');

		await waitFor(() => {
			expect(screen.getByText('create')).toBeInTheDocument();
		});
	});

	it('add cssClass when clicking modal button', async () => {
		const onValueSelect = jest.fn();

		renderCSSSelectorField({
			onValueSelect,
		});

		const cssClassesInput = screen.getByLabelText('css-classes');

		await userEvent.type(cssClassesInput, 'customClass1');
		await userEvent.click(screen.getByText('create'));

		await waitFor(() => {
			expect(onValueSelect).toBeCalledWith('cssClasses', [
				'customClass1',
			]);
		});
	});

	it('autocomplete with classNames of other components', async () => {
		renderCSSSelectorField();

		const cssClassesInput = screen.getByLabelText('css-classes');

		await userEvent.type(cssClassesInput, 'other');

		await waitFor(() => {
			expect(screen.getByText('otherClass1')).toBeInTheDocument();
			expect(screen.getByText('otherClass2')).toBeInTheDocument();
		});
	});
});
