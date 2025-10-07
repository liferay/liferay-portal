/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import {FormProvider, PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import FieldBase, {
	normalizeInputValue,
	updateFieldNameLocale,
} from '../../../src/main/resources/META-INF/resources/js/api/FieldBase/ReactFieldBase';

const spritemap = 'icons.svg';

const FieldBaseWithProvider = (props) => (
	<FormProvider initialState={{pages: []}}>
		<PageProvider value={{editingLanguageId: 'en_US'}}>
			<FieldBase {...props} />
		</PageProvider>
	</FormProvider>
);

describe('ReactFieldBase', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;
	});

	beforeAll(() => {
		window.themeDisplay = {
			...window.themeDisplay,
			getPathThemeImages: () => 'http://localhost:8080',
		};

		// eslint-disable-next-line no-console
		console.warn = (...args) => {
			if (/DataProvider: Trying/.test(args[0])) {
				return;
			}
			originalWarn.call(console, ...args);
		};
	});

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponseOnce(JSON.stringify({}));
	});

	describe('Hide Field', () => {
		it('renders the FieldBase with hideField markup', () => {
			const {getAllByText, getByText} = render(
				<FieldBaseWithProvider
					hideField
					label="Text"
					spritemap={spritemap}
				/>
			);

			expect(getByText('hidden')).toBeInTheDocument();

			const allByText = getAllByText('Text');
			expect(allByText).toHaveLength(2);
			expect(allByText[0]).toBeInTheDocument();
			expect(allByText[1]).toBeInTheDocument();

			expect(getByText('hidden').parentNode).toHaveAttribute(
				'class',
				'label ml-1 label-secondary'
			);
			expect(allByText[0]).toHaveAttribute('class', 'text-secondary');
			expect(allByText[1]).toHaveAttribute('class', 'sr-only');
		});

		it('renders the FieldBase with hideField markup when the label is empty', () => {
			const {getByText} = render(
				<FieldBaseWithProvider
					hideField
					label=""
					spritemap={spritemap}
				/>
			);

			expect(getByText('hidden')).toBeInTheDocument();

			expect(getByText('hidden').parentNode).toHaveAttribute(
				'class',
				'label ml-1 label-secondary'
			);
		});
	});

	it('does not render the add button when repeatable is true and the maximum limit of repetions is reached', () => {
		const {container} = render(
			<FieldBaseWithProvider
				id="id"
				label="Text"
				overMaximumRepetitionsLimit={true}
				repeatable={true}
				showLabel={false}
				spritemap={spritemap}
			/>
		);
		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('does not render the label if showLabel is false', () => {
		const {container} = render(
			<FieldBaseWithProvider
				label="Text"
				showLabel={false}
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the add button when repeatable is true', () => {
		const {container} = render(
			<FieldBaseWithProvider
				label="Text"
				repeatable={true}
				showLabel={false}
				spritemap={spritemap}
			/>
		);
		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the default markup', () => {
		const {container} = render(
			<FieldBaseWithProvider spritemap={spritemap} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the FieldBase with contentRenderer', () => {
		const {container} = render(
			<FieldBaseWithProvider spritemap={spritemap}>
				<div>
					<h1>Foo bar</h1>
				</div>
			</FieldBaseWithProvider>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the FieldBase with help text', () => {
		const {container} = render(
			<FieldBaseWithProvider
				spritemap={spritemap}
				tip="Type something!"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the FieldBase with id', () => {
		const {container} = render(
			<FieldBaseWithProvider id="Id" spritemap={spritemap} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the FieldBase with label', () => {
		const {container} = render(
			<FieldBaseWithProvider label="Text" spritemap={spritemap} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the FieldBase with required', () => {
		const {container} = render(
			<FieldBaseWithProvider required spritemap={spritemap} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the FieldBase with tooltip', () => {
		const {findByTestId} = render(
			<FieldBaseWithProvider spritemap={spritemap} tooltip="Tooltip" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(findByTestId('tooltip')).not.toBeNull();
	});

	it('renders the hidden inputs with data-languageid and data-field-name', () => {
		const localizedValue = {ca_ES: 'test_ca_ES', en_US: 'test_en_US'};

		render(
			<FieldBaseWithProvider
				fieldName="field_name"
				instanceId="instance_id"
				localizedValue={localizedValue}
				name="test_name"
			/>
		);

		const inputs = document.querySelectorAll('[name="test_name"]');

		inputs.forEach((input, i) => {
			expect(input).toHaveAttribute(
				'data-field-name',
				'field_nameinstance_id'
			);
			expect(input).toHaveAttribute(
				'data-languageid',
				Object.keys(localizedValue)[i]
			);
		});
	});

	it('renders the label with info icon and its corresponding styles when the field is non-localizable', () => {
		const {getByLabelText, getByTitle} = render(
			<FieldBaseWithProvider
				editOnlyInDefaultLanguage
				label="my-label"
				readOnly
			/>
		);

		expect(
			getByTitle('this-field-cannot-be-localized')
		).toBeInTheDocument();

		expect(getByLabelText('my-label')).toHaveClass('text-secondary');
	});

	it('shows the popover for Format field when hovering over the tooltip icon', async () => {
		const {findByTestId, getByRole, getByText} = render(
			<FieldBaseWithProvider
				fieldName="inputMaskFormat"
				popover={{
					alignPosition: 'right-bottom',
					content: 'Tooltip Description',
					header: 'input-mask-format',
					image: {
						alt: 'input-mask-format',
						height: 170,
						src: 'http://localhost:8080/forms/input_mask_format.png',
						width: 232,
					},
				}}
				spritemap={spritemap}
				tooltip="Tooltip Description"
			/>
		);

		const tooltipIcon = await findByTestId('tooltip');

		fireEvent.click(tooltipIcon);

		const clayPopover = await findByTestId('clayPopover');

		expect(clayPopover.style).toHaveProperty('maxWidth', '256px');

		expect(getByRole('img')).toHaveAttribute('height', '170');
		expect(getByRole('img')).toHaveAttribute(
			'src',
			'http://localhost:8080/forms/input_mask_format.png'
		);
		expect(getByRole('img')).toHaveAttribute('width', '232');

		expect(getByText('input-mask-format')).toBeInTheDocument();
		expect(getByText('Tooltip Description')).toBeInTheDocument();
	});

	describe('normalizeInputValue function', () => {
		it('checks if the value is being formatted according to their fieldType', () => {

			// no value and any fieldType

			expect(normalizeInputValue('text', null)).toBe('');

			// text fieldType

			const textValue = 'this is a text';

			expect(normalizeInputValue('text', textValue)).toBe(textValue);

			// date and date_time fieldType

			const dateValue = '2024-12-25';
			const dateTimeValue = '2024-12-25 21:00';

			expect(normalizeInputValue('date', dateValue)).toBe(dateValue);

			expect(normalizeInputValue('date_time', dateTimeValue)).toBe(
				dateTimeValue
			);

			// image fieldType

			const imageValue = {
				alt: 'this is an alt text',
				classNameId: 22222,
				description: 'this is a description',
				fileEntryId: '33333',
				groupId: '10000',
				height: 900,
				title: 'my_image',
				type: 'document',
				url: '/documents/images/my_image',
				width: 900,
			};

			expect(normalizeInputValue('image', imageValue)).toBe(
				JSON.stringify(imageValue)
			);
		});
	});

	describe('updateFieldNameLocale function', () => {
		it('checks if the name only changes the language id at the end even when using a custom language', () => {

			// en_US -> language out-of-the-box
			// co -> language customized

			const customLanguageFieldName = 'com_liferay_fieldname$$co';
			const defaultLanguageFieldName = 'com_liferay_fieldname$$en_US';

			expect(
				updateFieldNameLocale('co', 'en_US', customLanguageFieldName)
			).toBe(defaultLanguageFieldName);

			expect(
				updateFieldNameLocale('en_US', 'co', defaultLanguageFieldName)
			).toBe(customLanguageFieldName);
		});
	});
});
