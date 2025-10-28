/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	act,
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import React from 'react';

import * as ListTypeAPI from '../../../../src/main/resources/META-INF/resources/js/data/listType';
import MultiselectPicklist from '../../../../src/main/resources/META-INF/resources/js/objects/fields/MultiselectPicklist';

// eslint-disable-next-line @liferay/no-extraneous-dependencies
global.ResizeObserver = require('resize-observer-polyfill');

jest.mock('../../../../src/main/resources/META-INF/resources/js/data/listType');

describe('MultiselectPicklist object field', () => {
	const BASE_PROPS = {
		disabled: false,
		id: 'some-id',
		label: 'test-field-name',
		mode: 'view',
		name: 'test-picklist',
		namespace: '_test_namespace_',
		onChange: jest.fn(),
		originalField: {listTypeDefinitionId: 12345},
		readOnly: false,
		required: false,
		value: 'key1',
	};

	const ISO_CODE = 'en-US';
	const I18N_VALUE_1 = 'i18nValue1';
	const I18N_VALUE_2 = 'i18nValue2';
	const KEY_1 = 'key1';
	const KEY_2 = 'key2';

	const LIST_TYPE_ENTRIES_MOCK = {
		items: [
			{
				key: KEY_1,
				name_i18n: {[ISO_CODE]: I18N_VALUE_1},
			},
			{
				key: KEY_2,
				name_i18n: {[ISO_CODE]: I18N_VALUE_2},
			},
		],
	};

	const expectUpdateAfterAPICall = (container) => {
		expect(container).toBeInTheDocument();
		expect(ListTypeAPI.getListTypeEntries).toHaveBeenCalledTimes(1);
		expect(ListTypeAPI.getListTypeEntries).toHaveBeenCalledWith(
			BASE_PROPS.originalField.listTypeDefinitionId
		);
		expect(
			window.Liferay.ThemeDisplay.getBCP47LanguageId
		).toHaveBeenCalledTimes(1);
	};

	beforeEach(() => {
		window['Liferay'] = {
			Language: {
				get: jest.fn((text) => text),
			},
			ThemeDisplay: {
				getBCP47LanguageId: jest.fn(() => ISO_CODE),
			},
		};

		// eslint-disable-next-line no-import-assign
		ListTypeAPI.getListTypeEntries = jest.fn(() =>
			Promise.resolve(LIST_TYPE_ENTRIES_MOCK)
		);
	});

	afterEach(() => {
		cleanup();
		delete global.window['Liferay'];
		jest.resetAllMocks();
	});

	describe('in "view" mode', () => {
		it('displays the object field title and a "-" if the value is empty', async () => {
			const props = {...BASE_PROPS, value: undefined};

			const {container} = render(<MultiselectPicklist {...props} />);

			await waitFor(() => {
				expectUpdateAfterAPICall(container);
			});

			const fieldTitle = container.querySelector('.sidebar-dt');
			const fieldContent = container.querySelector('.text-wrap');

			expect(fieldTitle.innerHTML).toEqual(BASE_PROPS.label);
			expect(fieldContent.innerHTML).toEqual('-');
		});

		it('displays the object field title and its i18n value', async () => {
			const props = {...BASE_PROPS, value: 'key1'};

			const {container} = render(<MultiselectPicklist {...props} />);

			await waitFor(() => {
				expectUpdateAfterAPICall(container);
			});

			const [keyValue1] = LIST_TYPE_ENTRIES_MOCK.items;
			const {name_i18n} = keyValue1;
			const [[, value1]] = Object.entries(name_i18n);

			const element = container.querySelector('.text-wrap');

			expect(element.innerHTML).toEqual(value1);
		});
	});

	describe('in "edit" mode', () => {
		it('displays a ClayMultiSelect with i18n items', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {
				...BASE_PROPS,
				mode: 'edit',
				onChange,
				value: '',
			};

			const {container} = render(<MultiselectPicklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.focus(element);
			});

			const dropDownListItems =
				container.querySelectorAll('.dropdown-item');

			dropDownListItems.forEach((button, index) => {
				const {
					key,
					name_i18n: {[ISO_CODE]: value},
				} = LIST_TYPE_ENTRIES_MOCK.items[index];

				expect(button.id).toEqual(key);
				expect(button.innerHTML).toEqual(value);
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: '',
			});
		});

		it('displays a ClayMultiSelect with i18n items with a preselected value if set', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: KEY_2};

			const {container} = render(<MultiselectPicklist {...props} />);

			await waitFor(() => {
				expectUpdateAfterAPICall(container);

				expect(onChange).toHaveBeenCalledWith({
					hasError: false,
					name: BASE_PROPS.name,
					value: KEY_2,
				});
			});
		});

		it('allows to select a key by its i18n option value', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: ''};

			const {container} = render(<MultiselectPicklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.focus(element);
			});

			await act(async () => {
				const itemElement = screen.getByText(I18N_VALUE_1);

				fireEvent.click(itemElement);
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: KEY_1,
			});
		});

		it('allows to select a key by its i18n option value multiple times', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: ''};

			const {container} = render(<MultiselectPicklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.focus(element);
			});

			await act(async () => {
				fireEvent.click(screen.getByText(I18N_VALUE_1));
			});

			await act(async () => {
				fireEvent.focus(element);
			});

			await act(async () => {
				fireEvent.click(screen.getByText(I18N_VALUE_2));
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: `${KEY_1}, ${KEY_2}`,
			});
		});

		it('allows to remove a previously selected key by its i18n option value', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: ''};

			const {container} = render(<MultiselectPicklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.focus(element);
			});

			await act(async () => {
				fireEvent.click(screen.getByText(I18N_VALUE_1));
			});

			await act(async () => {
				fireEvent.focus(element);
			});

			await act(async () => {
				fireEvent.click(screen.getByText(I18N_VALUE_2));
			});

			await act(async () => {
				fireEvent.click(
					screen.getByLabelText(`Remove ${I18N_VALUE_2}`)
				);
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: `${KEY_1}`,
			});
		});

		it('allows to remove all the previously selected keys', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: ''};

			const {container} = render(<MultiselectPicklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.focus(element);
			});

			await act(async () => {
				fireEvent.click(screen.getByText(I18N_VALUE_1));
			});

			await act(async () => {
				fireEvent.focus(element);
			});

			await act(async () => {
				fireEvent.click(screen.getByText(I18N_VALUE_2));
			});

			await act(async () => {
				fireEvent.click(screen.getByLabelText('Clear All'));
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: '',
			});
		});

		it('strips away typed-in values if not selectable', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: ''};

			const {container} = render(<MultiselectPicklist {...props} />);

			const NON_SELECTABLE = 'non-selectable';

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.change(element, {
					target: {
						value: NON_SELECTABLE,
					},
				});
			});

			expect(onChange).not.toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: NON_SELECTABLE,
			});
		});

		it('strips away typed-in values which are already selected', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {...BASE_PROPS, mode: 'edit', onChange, value: KEY_1};

			const {container} = render(<MultiselectPicklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				element = container.querySelector('input');

				fireEvent.change(element, {
					target: {
						value: I18N_VALUE_1,
					},
				});
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: false,
				name: BASE_PROPS.name,
				value: KEY_1,
			});
		});

		it('is disabled if set to be disabled', async () => {
			const props = {
				...BASE_PROPS,
				disabled: true,
				mode: 'edit',
				value: '',
			};

			const {container} = render(<MultiselectPicklist {...props} />);

			await waitFor(() => {
				expectUpdateAfterAPICall(container);
			});

			const element = container.querySelector('input');

			expect(element.disabled).toBe(true);
		});

		it('is disabled if set to be read-only', async () => {
			const props = {
				...BASE_PROPS,
				mode: 'edit',
				readOnly: true,
				value: '',
			};

			const {container} = render(<MultiselectPicklist {...props} />);

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			const element = container.querySelector('input');

			expect(element.disabled).toBe(true);
		});

		it('highlights the field if empty and set to be required', async () => {
			const onChange = jest.fn().mockName('onChange');

			const props = {
				...BASE_PROPS,
				mode: 'edit',
				onChange,
				required: true,
				value: '',
			};

			const {container} = render(<MultiselectPicklist {...props} />);

			let formParentElement;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				const element = container.querySelector('input');
				formParentElement = container.querySelector('.form-group');

				fireEvent.click(element);
				formParentElement.focus();
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: true,
				name: BASE_PROPS.name,
				value: '',
			});
			expect(formParentElement.className.includes('has-error')).toBe(
				true
			);
		});
	});
});
