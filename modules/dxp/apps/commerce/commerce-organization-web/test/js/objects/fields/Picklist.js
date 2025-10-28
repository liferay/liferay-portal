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
import Picklist from '../../../../src/main/resources/META-INF/resources/js/objects/fields/Picklist';

jest.mock('../../../../src/main/resources/META-INF/resources/js/data/listType');

describe('Picklist object field', () => {
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
		global.window['Liferay'] = {
			Language: {
				get: jest.fn((text) => text),
			},
			ThemeDisplay: {
				getBCP47LanguageId: jest.fn(() => ISO_CODE),
			},
		};

		// eslint-disable-next-line
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

			const {container} = render(<Picklist {...props} />);

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

			const {container} = render(<Picklist {...props} />);

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
		it('displays a ClaySelect with i18n options', async () => {
			const props = {...BASE_PROPS, mode: 'edit', value: ''};

			const {container} = render(<Picklist {...props} />);

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			const element = container.querySelector('select');
			const defaultOption = screen.getByText('select-item');
			const option1 = screen.getByText(I18N_VALUE_1);
			const option2 = screen.getByText(I18N_VALUE_2);

			expect(element.value).toEqual('select-item');
			expect(defaultOption).toBeInTheDocument();
			expect(option1).toBeInTheDocument();
			expect(option2).toBeInTheDocument();
		});

		it('displays a ClaySelect with i18n options with a preselected value if set', async () => {
			const props = {...BASE_PROPS, mode: 'edit', value: KEY_2};

			const {container} = render(<Picklist {...props} />);

			await waitFor(() => {
				expectUpdateAfterAPICall(container);
			});

			await waitFor(() => {
				const select = container.querySelector('select');

				expect(select.value).toEqual(I18N_VALUE_2);
			});
		});

		it('allows to select a key by its i18n option value', async () => {
			const onChange = jest.fn();
			const props = {
				...BASE_PROPS,
				mode: 'edit',
				onChange,
				value: '',
			};
			const {container} = render(<Picklist {...props} />);

			let element;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);

				element = container.querySelector('select');

				expect(element.value).toEqual('select-item');
			});

			await act(async () => {
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
			expect(element.value).toEqual(I18N_VALUE_1);
		});

		it('is disabled if set to be disabled', async () => {
			const props = {
				...BASE_PROPS,
				disabled: true,
				mode: 'edit',
				value: '',
			};

			const {container} = render(<Picklist {...props} />);

			await waitFor(() => {
				expectUpdateAfterAPICall(container);
			});

			const element = container.querySelector('select');

			expect(element.value).toEqual('select-item');
			expect(element.disabled).toBe(true);
		});

		it('is disabled if set to be read-only', async () => {
			const props = {
				...BASE_PROPS,
				mode: 'edit',
				readOnly: true,
				value: '',
			};

			const {container} = render(<Picklist {...props} />);

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			const element = container.querySelector('select');

			expect(element.value).toEqual('select-item');
			expect(element.disabled).toBe(true);
		});

		it('highlights the field if empty and set to be required', async () => {
			const onChange = jest.fn();

			const props = {
				...BASE_PROPS,
				mode: 'edit',
				onChange,
				required: true,
				value: '',
			};

			const {container} = render(<Picklist {...props} />);

			let element;
			let formParentElement;

			await waitFor(async () => {
				expectUpdateAfterAPICall(container);
			});

			await act(async () => {
				formParentElement = container.querySelector('.form-group');
				element = container.querySelector('select');

				fireEvent.click(element);
				formParentElement.focus();
			});

			expect(onChange).toHaveBeenCalledWith({
				hasError: true,
				name: BASE_PROPS.name,
				value: '',
			});
			expect(element.value).toEqual('select-item');
			expect(formParentElement.className.includes('has-error')).toBe(
				true
			);
		});
	});
});
