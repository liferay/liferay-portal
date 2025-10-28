/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {RichText} from 'dynamic-data-mapping-form-field-type';
import React from 'react';

import {Editor} from '../../../../../../src/main/resources/META-INF/resources/js/components/rules/editor/Editor.es';
import {DEFAULT_RULE} from '../../../../../../src/main/resources/META-INF/resources/js/components/rules/editor/config.es';
import {
	FIELDS,
	FIELDS_TYPES,
	NUMBER_OPERATORS,
	NUMBER_TYPE_FIELDS,
	OPERATORS_BY_TYPE,
	ROLES,
	STRING_DATATYPE_FIELDS,
	TEXT_OPERATORS,
	UPLOAD_TYPE_FIELD,
	USER_OPERATORS,
} from '../../../../../mock/fields.es';

global.fetch.enableFetchMocks();

const globalLanguageDirection = Liferay.Language.direction;

const pages = [
	{
		label: '1 Page title',
		name: '0',
		value: '0',
	},
	{
		label: '2 Page title',
		name: '1',
		value: '1',
	},
	{
		label: '3 Page title',
		name: '2',
		value: '2',
	},
];

const conditions = [
	{
		operands: [
			{
				type: '',
				value: '',
			},
		],
		operator: '',
	},
	{
		operands: [
			{
				type: '',
				value: '',
			},
		],
		operator: '',
	},
];

const newRule = {
	...DEFAULT_RULE,
	conditions,
};

const defaultProps = (fieldsList = FIELDS) => {
	return {
		allowActions: [
			'auto-fill',
			'calculate',
			'enable',
			'jump-to-page',
			'require',
			'show',
		],
		dataProvider: [
			{
				id: '39421',
				label: 'Get countries',
				name: 'Get countries',
				uuid: 'a6ab90fd-c4ac-b91b-c9a5-cf756170d9d3',
				value: '39421',
			},
		],
		dataProviderInstanceParameterSettingsURL:
			'/o/dynamic-data-mapping-form-builder-provider-instance-parameter-settings/',
		fields: fieldsList,
		operatorsByType: OPERATORS_BY_TYPE,
		pages,
		roles: ROLES,
		rule: DEFAULT_RULE,
	};
};

globalThis.RichText = RichText;

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	loadModule: jest.fn((fieldModule) => {
		const {
			ColorPicker,
			DatePicker,
			DocumentLibrary,
			Grid,
			ImagePicker,
			Numeric,
			Select,
			Text,
		} = jest.requireActual('dynamic-data-mapping-form-field-type');

		let component = null;

		switch (fieldModule) {
			case 'color':
				component = ColorPicker;
				break;
			case 'date':
				component = DatePicker;
				break;
			case 'grid':
				component = Grid;
				break;
			case 'image':
				component = ImagePicker;
				break;
			case 'numeric':
				component = Numeric;
				break;
			case 'rich_text':
				component = globalThis.RichText;
				break;
			case 'select':
				component = Select;
				break;
			case 'text':
				component = Text;
				break;
			case 'document_library':
				component = DocumentLibrary;
				break;
			default:
				break;
		}

		return Promise.resolve(component);
	}),
}));

describe('Editor', () => {
	const originalLiferayLoader = window.Liferay.Loader;

	beforeEach(() => {
		global.fetch.mockResponse(JSON.stringify(FIELDS_TYPES));
		jest.useFakeTimers();
	});

	beforeAll(() => {
		jest.setTimeout(30000);

		Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	afterAll(() => {
		window.Liferay.Loader = originalLiferayLoader;

		Liferay.Language.direction = globalLanguageDirection;
	});

	describe('Editor', () => {
		describe('Conditions', () => {
			describe('Field operator', () => {
				it.each(STRING_DATATYPE_FIELDS)(
					'shows operators related to texts when field left is a %p',
					async ({type}) => {
						const props = defaultProps();
						const {getByTestId, getByText} = render(
							<Editor
								{...props}
								onChange={() => {}}
								onValidator={() => {}}
							/>
						);

						await waitFor(() => {
							return document
								.querySelectorAll('.timeline-item')[1]
								.querySelectorAll('.ddm-field')[0];
						});

						const fieldLeft = await waitFor(() => {
							return getByTestId('field-left-id');
						});

						userEvent.click(fieldLeft);

						const selectedItem = getByText(type);

						fireEvent.click(selectedItem);

						await waitFor(() => getByTestId('field-operator-id'));

						act(() => {
							jest.advanceTimersByTime(100);
						});

						const fieldOperator = getByTestId('field-operator-id');

						fireEvent.click(fieldOperator);

						TEXT_OPERATORS.forEach((operator) => {
							expect(getByText(operator)).toBeTruthy();
						});
					}
				);

				it.each(NUMBER_TYPE_FIELDS)(
					'shows operators related to numbers when field left is a %p',
					async ({type}) => {
						const props = defaultProps();
						const {getByTestId, getByText} = render(
							<Editor
								{...props}
								onChange={() => {}}
								onValidator={() => {}}
							/>
						);

						await waitFor(() => {
							return document
								.querySelectorAll('.timeline-item')[1]
								.querySelectorAll('.ddm-field')[0];
						});

						const fieldLeft = await waitFor(() => {
							return getByTestId('field-left-id');
						});

						userEvent.click(fieldLeft);

						const selectedItem = getByText(type);

						fireEvent.click(selectedItem);

						await waitFor(() => getByTestId('field-operator-id'));

						act(() => {
							jest.advanceTimersByTime(100);
						});

						const fieldOperator = getByTestId('field-operator-id');

						fireEvent.click(fieldOperator);

						NUMBER_OPERATORS.forEach((operator) => {
							expect(getByText(operator)).toBeTruthy();
						});
					}
				);

				it('shows operators related to roles when field left is an User', async () => {
					const props = defaultProps();
					const {getByTestId, getByText} = render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
						/>
					);

					await waitFor(() => {
						return document
							.querySelectorAll('.timeline-item')[1]
							.querySelectorAll('.ddm-field')[0];
					});

					const fieldLeft = await waitFor(() => {
						return getByTestId('field-left-id');
					});

					userEvent.click(fieldLeft);

					const selectedItem = getByText('user');

					fireEvent.click(selectedItem);

					await waitFor(() => getByTestId('field-operator-id'));

					act(() => {
						jest.advanceTimersByTime(100);
					});

					const fieldOperator = getByTestId('field-operator-id');

					fireEvent.click(fieldOperator);

					USER_OPERATORS.forEach((operator) => {
						expect(getByText(operator)).toBeTruthy();
					});
				});
			});

			describe('Binary operations', () => {
				it.each(
					STRING_DATATYPE_FIELDS.concat(NUMBER_TYPE_FIELDS).concat(
						UPLOAD_TYPE_FIELD
					)
				)(
					'shows a related field input on the right operand when the left operand is a %p and action type is value',
					async ({selector, type}) => {
						const props = defaultProps();
						const mockIsSignedIn = jest.fn();

						/** For document_library field to be displayed a user must by signed in */
						Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;
						const {getByTestId, getByText} = render(
							<Editor
								{...props}
								onChange={() => {}}
								onValidator={() => {}}
							/>
						);

						await waitFor(() => {
							return document
								.querySelectorAll('.timeline-item')[1]
								.querySelectorAll('.ddm-field')[0];
						});

						const fieldLeft = await waitFor(() => {
							return getByTestId('field-left-id');
						});

						userEvent.click(fieldLeft);

						const selectedItem = getByText(type);
						fireEvent.click(selectedItem);

						await waitFor(() => getByTestId('field-operator-id'));

						act(() => {
							jest.advanceTimersByTime(100);
						});

						const fieldOperator = getByTestId('field-operator-id');

						fireEvent.click(fieldOperator);

						act(() => {
							jest.advanceTimersByTime(100);
						});

						await act(async () => {
							fireEvent.click(getByText('Is equal to'));
						});

						const binaryOperator = await waitFor(() => {
							return getByTestId('field-binary-operator-id');
						});

						fireEvent.click(binaryOperator);

						await act(async () => {
							fireEvent.click(getByText('value'));
						});

						if (type === 'rich_text') {
							expect(
								document.querySelectorAll(selector)
							).toBeTruthy();

							return;
						}

						await waitFor(() => {
							const fieldElement = document
								.querySelectorAll('.timeline-item')[1]
								.querySelectorAll('.ddm-field')[3]
								.querySelector(selector);

							expect(fieldElement).toBeTruthy();
						});
					}
				);

				it('shows all others fields when action type is Other field', async () => {
					const props = defaultProps();
					const mockIsSignedIn = jest.fn();

					Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;
					const {getAllByText, getByTestId, getByText} = render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
						/>
					);

					await waitFor(() => {
						return document
							.querySelectorAll('.timeline-item')[1]
							.querySelectorAll('.ddm-field')[0];
					});

					const fieldLeft = await waitFor(() => {
						return getByTestId('field-left-id');
					});

					userEvent.click(fieldLeft);

					const selectedItem = getByText('text');

					fireEvent.click(selectedItem);

					await waitFor(() => getByTestId('field-operator-id'));

					act(() => {
						jest.advanceTimersByTime(100);
					});

					const fieldOperator = getByTestId('field-operator-id');

					fireEvent.click(fieldOperator);

					act(() => {
						jest.advanceTimersByTime(100);
					});

					await act(async () => {
						fireEvent.click(getByText('Is equal to'));
					});

					const binaryOperator = await waitFor(() => {
						return getByTestId('field-binary-operator-id');
					});

					fireEvent.click(binaryOperator);

					await act(async () => {
						fireEvent.click(getByText('other-field'));
					});

					const allFields =
						STRING_DATATYPE_FIELDS.concat(
							NUMBER_TYPE_FIELDS
						).concat(UPLOAD_TYPE_FIELD);

					const otherValueButton = await waitFor(() => {
						return getByTestId('field-right-id');
					});

					fireEvent.click(otherValueButton);

					allFields.forEach(({type}) => {
						const fieldOccurency = getAllByText(type).length;
						if (type === 'text') {
							expect(fieldOccurency).toBe(2);
						}
						else {
							expect(fieldOccurency).toBe(1);
						}
					});
				});
			});

			describe('Conditions logical operatores', () => {
				it('shows the OR/AND select disabled by default', async () => {
					const props = defaultProps();
					render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
						/>
					);

					await waitFor(() => {
						return document.querySelector('.option-selected');
					});

					expect(
						document.querySelector(
							'.timeline-first .dropdown-toggle'
						).disabled
					).toBe(true);
				});

				it('enables the OR/AND select when there are more than one condition', async () => {
					const props = defaultProps();
					render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
							rule={newRule}
						/>
					);

					await waitFor(() => {
						return document.querySelector('.option-selected');
					});

					expect(
						document.querySelector(
							'.timeline-first .dropdown-toggle'
						).disabled
					).toBe(false);
				});
			});

			describe('Add/Remove conditions', () => {
				it('shows the container trash when there are more than one condition', () => {
					const props = defaultProps();
					render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
						/>
					);

					expect(
						document.querySelectorAll('.container-trash')[0]
					).toBeFalsy();

					fireEvent.click(
						document.querySelectorAll(
							'.timeline-increment button'
						)[0]
					);

					expect(
						document.querySelectorAll('.container-trash')[0]
					).toBeTruthy();
				});
			});
		});

		describe('Actions', () => {
			it('shows the action types', async () => {
				const props = defaultProps();
				const {getByText} = render(
					<Editor
						{...props}
						onChange={() => {}}
						onValidator={() => {}}
					/>
				);

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[0];
				});

				const actionType = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[0]
					.querySelector('button');

				fireEvent.click(actionType);

				expect(getByText('show')).toBeTruthy();
				expect(getByText('enable')).toBeTruthy();
				expect(getByText('require')).toBeTruthy();
				expect(getByText('autofill')).toBeTruthy();
				expect(getByText('calculate')).toBeTruthy();
			});

			describe('Add/Remove actions', () => {
				it('shows the container trash when there are more than one action', () => {
					const props = defaultProps();
					render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
						/>
					);

					expect(
						document.querySelectorAll('.container-trash')[1]
					).toBeFalsy();

					fireEvent.click(
						document.querySelectorAll(
							'.timeline-increment button'
						)[1]
					);

					expect(
						document.querySelectorAll('.container-trash')[1]
					).toBeTruthy();
				});
			});

			it.each(['show', 'require', 'enable'])(
				'shows all fields on target dropdown when the type is %p',
				async (type) => {
					const fields =
						STRING_DATATYPE_FIELDS.concat(
							NUMBER_TYPE_FIELDS
						).concat(UPLOAD_TYPE_FIELD);
					const props = defaultProps();
					const {getByText, queryAllByText} = render(
						<Editor
							{...props}
							onChange={() => {}}
							onValidator={() => {}}
						/>
					);

					await waitFor(() => {
						return document
							.querySelectorAll('.timeline-item')[4]
							.querySelectorAll('.ddm-field')[0];
					});

					const actionType = document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[0]
						.querySelector('button');

					fireEvent.click(actionType);
					fireEvent.click(getByText(type));

					await waitFor(() => {
						return document
							.querySelectorAll('.timeline-item')[4]
							.querySelectorAll('.ddm-field')[1];
					});

					const actionTarget = document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[1]
						.querySelector('button');

					fireEvent.click(actionTarget);

					fields.forEach(({type}) => {
						const fieldOccurences = queryAllByText(type);
						expect(fieldOccurences).toBeTruthy();
					});
				}
			);
			it('shows dataprovider when autofill action is selected', async () => {
				const props = defaultProps();
				const {getByText} = render(
					<Editor
						{...props}
						onChange={() => {}}
						onValidator={() => {}}
					/>
				);

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[0];
				});

				const actionType = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[0]
					.querySelector('button');

				fireEvent.click(actionType);

				fireEvent.click(getByText('autofill'));

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[1];
				});

				const actionTarget = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[1]
					.querySelector('button');

				fireEvent.click(actionTarget);

				expect(getByText('Get countries')).toBeTruthy();
			});

			it('shows the calculator area when calculate action is selected', async () => {
				const props = defaultProps();
				const {getByText, queryAllByText} = render(
					<Editor
						{...props}
						onChange={() => {}}
						onValidator={() => {}}
					/>
				);

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[0];
				});

				const actionType = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[0]
					.querySelector('button');

				fireEvent.click(actionType);
				fireEvent.click(getByText('calculate'));

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[1];
				});

				const actionTarget = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[1]
					.querySelector('button');

				fireEvent.click(actionTarget);

				expect(queryAllByText('integer')).toBeTruthy();
				expect(queryAllByText('double')).toBeTruthy();

				fireEvent.click(getByText('integer'));

				await waitFor(() => {
					return document.querySelector('.calculate-container');
				});

				expect(
					document.querySelector('.calculate-container')
				).toBeTruthy();
			});

			it('shows available pages when jump to page action is selected', async () => {
				const props = defaultProps();
				const {getByText, queryAllByText} = render(
					<Editor
						{...props}
						onChange={() => {}}
						onValidator={() => {}}
					/>
				);

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[0];
				});

				const actionType = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[0]
					.querySelector('button');

				fireEvent.click(actionType);
				fireEvent.click(getByText('jump-to-page'));

				await waitFor(() => {
					return document
						.querySelectorAll('.timeline-item')[4]
						.querySelectorAll('.ddm-field')[1];
				});

				const actionTarget = document
					.querySelectorAll('.timeline-item')[4]
					.querySelectorAll('.ddm-field')[1]
					.querySelector('button');

				fireEvent.click(actionTarget);

				expect(queryAllByText('2 Page title')).toBeTruthy();
				expect(queryAllByText('3 Page title')).toBeTruthy();
			});
		});
	});
});
