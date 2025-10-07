/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';

import '@testing-library/jest-dom/extend-expect';
import {
	FormProvider,
	PageProvider,
	languageReducer,
} from 'data-engine-js-components-web';
import React from 'react';
import ReactDOM from 'react-dom';

import LocalizableText from '../../../src/main/resources/META-INF/resources/js/LocalizableText/LocalizableText.es';

const availableLocales = [
	{
		displayName: 'English (United States)',
		icon: 'en-us',
		localeId: 'en_US',
	},
	{displayName: 'العربية (السعودية)', icon: 'ar-sa', localeId: 'ar_SA'},
	{displayName: 'català (Espanya)', icon: 'ca-es', localeId: 'ca_ES'},
	{displayName: '中文 (中国)', icon: 'zh-cn', localeId: 'zh_CN'},
	{
		displayName: 'Nederlands (Nederland)',
		icon: 'nl-nl',
		localeId: 'nl_NL',
	},
	{displayName: 'suomi (Suomi)', icon: 'fi-fi', localeId: 'fi_FI'},
	{displayName: 'français (France)', icon: 'fr-fr', localeId: 'fr_FR'},
	{
		displayName: 'Deutsch (Deutschland)',
		icon: 'de-de',
		localeId: 'de_DE',
	},
	{
		displayName: 'magyar (Magyarország)',
		icon: 'hu-hu',
		localeId: 'hu_HU',
	},
	{displayName: '日本語 (日本)', icon: 'ja-jp', localeId: 'ja_JP'},
	{displayName: 'português (Brasil)', icon: 'pt-br', localeId: 'pt_BR'},
	{displayName: 'español (España)', icon: 'es-es', localeId: 'es_ES'},
	{displayName: 'svenska (Sverige)', icon: 'sv-se', localeId: 'sv_SE'},
];

const focusedField = {
	availableLocales,
	instanceId: '12345678',
	localizable: true,
	value: {ca_ES: 'Teste ES', en_US: 'Test EUA', pt_BR: 'Teste BR'},
};
const globalLanguageDirection = Liferay.Language.direction;
const pages = [
	{
		localizedDescription: {
			en_US: 'description',
			pt_BR: 'descrição',
		},
		localizedTitle: {en_US: 'title', pt_BR: 'título'},
		rows: [{columns: [{fields: [focusedField]}]}],
	},
];
const spritemap = 'icons.svg';
const state = {
	availableLanguageIds: ['en_US', 'pt_BR', 'ca_ES'],
	focusedField,
	pages,
};

const LocalizableTextWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<FormProvider
			initialState={state}
			reducers={[languageReducer]}
			value={{
				availableLocales,
				defaultLanguageId: 'en_US',
				editingLanguageId: 'en_US',
			}}
		>
			<LocalizableText {...props} />
		</FormProvider>
	</PageProvider>
);

afterAll(() => {
	Liferay.Language.direction = globalLanguageDirection;
});

afterEach(cleanup);

beforeAll(() => {
	Liferay.Language.direction = {
		en_US: 'ltr',
	};
});

const defaultLocalizableTextConfig = {
	defaultLocale: {
		displayName: 'English (United States)',
		icon: 'en-us',
		localeId: 'en_US',
	},
	editingLocale: {
		displayName: 'English (United States)',
		icon: 'en-us',
		localeId: 'en_US',
	},
	name: '_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_ddm$$emailArticleAddedSubject$uoeJR4Me$0$$en_US',
	spritemap,
};

describe('Field LocalizableText', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;
	});

	beforeAll(() => {

		// eslint-disable-next-line no-console
		console.warn = (...args) => {
			if (/DataProvider: Trying/.test(args[0])) {
				return;
			}
			originalWarn.call(console, ...args);
		};

		// @ts-ignore

		ReactDOM.createPortal = jest.fn((element) => {
			return element;
		});
	});

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponse(JSON.stringify({}));
		Liferay.component = jest.fn();
	});

	describe('Submit Button Label', () => {
		it('changes the placeholder according to the current editing locale', async () => {
			const {findByTestId, getByTestId} = render(
				<LocalizableTextWithProvider
					{...defaultLocalizableTextConfig}
					fieldName="submitLabel"
					onChange={jest.fn()}
					placeholdersSubmitLabel={[
						{localeId: 'de_DE', placeholderSubmitLabel: 'Senden'},
						{localeId: 'en_US', placeholderSubmitLabel: 'Submit'},
						{localeId: 'es_ES', placeholderSubmitLabel: 'Enviar'},
					]}
				/>
			);

			const triggerButton = getByTestId('triggerButton');

			fireEvent.click(triggerButton);

			act(() => {
				jest.runAllTimers();
			});

			const dropdownItem = await findByTestId(
				'availableLocalesDropdownde_DE'
			);

			fireEvent.click(dropdownItem);

			act(() => {
				jest.runAllTimers();
			});

			const inputComponent = await findByTestId('visibleChangeInput');

			expect(inputComponent.placeholder).toBe('Senden');
		});

		it('does not have the maxLength property equal to 25', () => {
			const {getByTestId} = render(
				<LocalizableTextWithProvider
					{...defaultLocalizableTextConfig}
				/>
			);

			const inputComponent = getByTestId('visibleChangeInput');

			expect(inputComponent.maxLength).not.toBe(25);
		});

		it('has by default the dropdown description equal to translated/not-translated for non-default locales', () => {
			const {queryAllByText} = render(
				<LocalizableTextWithProvider
					{...defaultLocalizableTextConfig}
					value={{
						de_DE: 'Test DE',
						es_ES: 'Test ES',
					}}
				/>
			);

			expect(queryAllByText('default')).toHaveLength(1);

			expect(queryAllByText('not-translated')).toHaveLength(
				availableLocales.length - 3
			);
			expect(queryAllByText('translated')).toHaveLength(2);
		});

		it('has by default the placeholder of the default locale', () => {
			const {getByTestId} = render(
				<LocalizableTextWithProvider
					{...defaultLocalizableTextConfig}
					fieldName="submitLabel"
					placeholdersSubmitLabel={[
						{localeId: 'de_DE', placeholderSubmitLabel: 'Senden'},
						{localeId: 'en_US', placeholderSubmitLabel: 'Submit'},
						{localeId: 'es_ES', placeholderSubmitLabel: 'Enviar'},
					]}
				/>
			);

			const inputComponent = getByTestId('visibleChangeInput');

			expect(inputComponent.placeholder).toBe('Submit');
		});

		it('has the dropdown description equal to customized/not-customized for the Submit Button Label input', () => {
			const {queryAllByText} = render(
				<LocalizableTextWithProvider
					{...defaultLocalizableTextConfig}
					fieldName="submitLabel"
					placeholdersSubmitLabel={[
						{localeId: 'de_DE', placeholderSubmitLabel: 'Senden'},
						{localeId: 'en_US', placeholderSubmitLabel: 'Submit'},
						{localeId: 'es_ES', placeholderSubmitLabel: 'Enviar'},
					]}
					value={{
						de_DE: 'Test DE',
						es_ES: 'Test ES',
					}}
				/>
			);

			expect(queryAllByText('customized')).toHaveLength(2);

			expect(queryAllByText('not-customized')).toHaveLength(
				availableLocales.length - 2
			);
		});

		it('has the maxLength property equal to 25 for the Submit Button Label input', () => {
			const {getByTestId} = render(
				<LocalizableTextWithProvider
					{...defaultLocalizableTextConfig}
					fieldName="submitLabel"
					placeholdersSubmitLabel={[
						{localeId: 'de_DE', placeholderSubmitLabel: 'Senden'},
						{localeId: 'en_US', placeholderSubmitLabel: 'Submit'},
						{localeId: 'es_ES', placeholderSubmitLabel: 'Enviar'},
					]}
				/>
			);

			const inputComponent = getByTestId('visibleChangeInput');

			expect(inputComponent.maxLength).toBe(25);
		});
	});

	it('adds a new translation for an untranslated item', async () => {
		const {container, findByTestId, getByTestId} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				onChange={jest.fn()}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		const triggerElement = getByTestId('triggerText');

		expect(triggerElement.textContent).toEqual('en-us');

		fireEvent.click(triggerElement);

		act(() => {
			jest.runAllTimers();
		});

		const dropdownItem = await findByTestId(
			'availableLocalesDropdownja_JP'
		);

		fireEvent.click(dropdownItem);

		act(() => {
			jest.runAllTimers();
		});

		const inputComponent = getByTestId('visibleChangeInput');

		expect(inputComponent.textContent).toEqual('');

		fireEvent.change(inputComponent, {
			target: {
				value: 'Test JP',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(inputComponent.value).toEqual('Test JP');

		expect(container).toMatchSnapshot();
	});

	it('emits field edit event on field change', () => {
		const EXPECTED_VALUE =
			'{"ca_ES":"Teste ES","en_US":"Test 2 EUA","pt_BR":"Teste BR"}';

		const handleFieldEdited = jest.fn();

		const {container, getByTestId} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				onChange={handleFieldEdited}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		const inputComponent = getByTestId('visibleChangeInput');

		fireEvent.change(inputComponent, {
			target: {
				value: 'Test 2 EUA',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(handleFieldEdited).toHaveBeenCalledWith(
			expect.any(Object),
			EXPECTED_VALUE
		);

		expect(container).toMatchSnapshot();
	});

	it('fills with the default language value when the selected language is not translated', async () => {
		const {container, findByTestId, getByTestId} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				onChange={jest.fn()}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		const triggerElement = getByTestId('triggerText');

		expect(triggerElement.textContent).toEqual('en-us');

		fireEvent.click(triggerElement);

		act(() => {
			jest.runAllTimers();
		});

		const dropdownItem = await findByTestId(
			'availableLocalesDropdownja_JP'
		);

		fireEvent.click(dropdownItem);

		act(() => {
			jest.runAllTimers();
		});

		const inputComponent = getByTestId('visibleChangeInput');

		expect(triggerElement.textContent).toEqual('ja-jp');

		expect(inputComponent.value).toEqual('Test EUA');

		expect(container).toMatchSnapshot();
	});

	it('fills with the selected language value when the selected language is translated', async () => {
		const {container, findByTestId, getByTestId} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				onChange={jest.fn()}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		const triggerButton = getByTestId('triggerButton');

		fireEvent.click(triggerButton);

		act(() => {
			jest.runAllTimers();
		});

		const dropdownItem = await findByTestId(
			'availableLocalesDropdownca_ES'
		);

		fireEvent.click(dropdownItem);

		act(() => {
			jest.runAllTimers();
		});

		const inputElement = await findByTestId('visibleChangeInput');

		expect(inputElement.value).toEqual('Teste ES');

		expect(triggerButton.textContent).toEqual('ca-es');

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				label="label"
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has aria-label when the displayStyle is multiline', () => {
		const {getByRole} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				displayStyle="multiline"
				label="label"
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		const textarea = getByRole('textbox');

		expect(textarea).toHaveAttribute('aria-label', 'label');
	});

	it('has a placeholder', () => {
		const {container} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				placeholder="Placeholder"
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not readOnly', () => {
		const {container} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				readOnly={false}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				required={false}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('removes the translation of an item already translated', async () => {
		const {container, findByTestId, getByTestId} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				onChange={jest.fn()}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		const triggerElement = getByTestId('triggerText');

		fireEvent.click(triggerElement);

		act(() => {
			jest.runAllTimers();
		});

		const dropdownItem = await findByTestId(
			'availableLocalesDropdownpt_BR'
		);

		fireEvent.click(dropdownItem);

		act(() => {
			jest.runAllTimers();
		});

		const inputComponent = getByTestId('visibleChangeInput');

		expect(inputComponent.value).toEqual('Teste BR');

		fireEvent.change(inputComponent, {
			target: {
				value: '',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(inputComponent.value).toEqual('');

		expect(container).toMatchSnapshot();
	});

	it('renders no values when values come empty', () => {
		const {container} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				value={{}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders values', () => {
		const {container} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('shows default language value when no other language is selected', () => {
		const {container, getByTestId} = render(
			<LocalizableTextWithProvider
				{...defaultLocalizableTextConfig}
				value={{
					ca_ES: 'Teste ES',
					en_US: 'Test EUA',
					pt_BR: 'Teste BR',
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const triggerElement = getByTestId('triggerText');

		expect(triggerElement.textContent).toEqual('en-us');

		expect(container).toMatchSnapshot();
	});
});
