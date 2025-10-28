/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CustomCSSField from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/CustomCSSField';
import {FRAGMENT_CLASS_PLACEHOLDER} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/fragmentClassPlaceholder';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

const renderCustomCSSField = ({
	field = {label: 'Custom CSS', name: 'customCSS'},
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
							itemId: 'itemId',
							type: LAYOUT_DATA_ITEM_TYPES.container,
						},
					},
				},
			})}
		>
			<CustomCSSField
				field={field}
				onValueSelect={onValueSelect}
				value={value}
			/>
		</StoreAPIContextProvider>,
		{
			baseElement: document.body,
		}
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
		renderCustomCSSField();

		expect(
			screen.getByLabelText('custom-css', {
				selector: 'textarea',
			})
		).toBeInTheDocument();
	});

	it('allow editing the custom css in the modal after clicking on expand button', async () => {
		const onValueSelect = jest.fn();

		renderCustomCSSField({onValueSelect});

		// Clay modal have an animation when are opened
		// This will make sure that the body is visible before asserting

		jest.useFakeTimers();

		await act(async () => {
			await userEvent.click(screen.getByTitle('expand'), {
				advanceTimers: jest.advanceTimersByTime,
			});
		});

		act(() => {
			jest.runAllTimers();
		});

		jest.useRealTimers();

		const addButton = screen.getByText('add');

		expect(addButton).toBeInTheDocument();

		act(() => {
			document
				.querySelector('.CodeMirror')
				.CodeMirror.setValue(
					`.${FRAGMENT_CLASS_PLACEHOLDER} { color: blue }`
				);
		});

		await userEvent.click(addButton);

		expect(onValueSelect).toBeCalledWith(
			'customCSS',
			`.${FRAGMENT_CLASS_PLACEHOLDER} { color: blue }`
		);
	});

	it('shows default placeholder by default', () => {
		renderCustomCSSField();

		expect(
			screen.getByDisplayValue(`${FRAGMENT_CLASS_PLACEHOLDER}`, {
				exact: false,
			})
		).toBeInTheDocument();
	});

	it('calls onValueSelect when typing something in the textarea', async () => {
		const onValueSelect = jest.fn();

		renderCustomCSSField({onValueSelect});

		const textarea = screen.getByLabelText('custom-css', {
			selector: 'textarea',
		});

		const css = `
			.${FRAGMENT_CLASS_PLACEHOLDER} { color: blue; }
			.${FRAGMENT_CLASS_PLACEHOLDER}:hover { color: red; }
		`;

		fireEvent.change(textarea, {target: {value: css}});

		fireEvent.blur(textarea);

		expect(onValueSelect).toBeCalledWith('customCSS', css);
	});

	it('does not save onValueSelect when typing the same as the default value', async () => {
		const onValueSelect = jest.fn();

		renderCustomCSSField({onValueSelect});

		const textarea = screen.getByLabelText('custom-css', {
			selector: 'textarea',
		});

		fireEvent.change(textarea, {
			target: {value: `.${FRAGMENT_CLASS_PLACEHOLDER} {\n\n}`},
		});

		fireEvent.blur(textarea);

		expect(onValueSelect).not.toBeCalled();
	});
});
