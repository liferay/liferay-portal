/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render, within} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import TranslationAdminSelector from '../../src/main/resources/META-INF/resources/translation_manager/TranslationAdminSelector';

const activeLanguageIds = ['en_US', 'ca_ES'];

const availableLocales = [
	{
		displayName: 'English (United States)',
		id: 'en_US',
		label: 'en-US',
		symbol: 'en-us',
	},
	{
		displayName: 'Arabic (Saudi Arabia)',
		id: 'ar_SA',
		label: 'ar-SA',
		symbol: 'ar-sa',
	},
	{
		displayName: 'Catalan (Spain)',
		id: 'ca_ES',
		label: 'ca-ES',
		symbol: 'ca-es',
	},
	{
		displayName: 'Chinese (China)',
		id: 'zh_CN',
		label: 'zh-CN',
		symbol: 'zh-cn',
	},
	{
		displayName: 'Dutch (Netherlands)',
		id: 'nl_NL',
		label: 'nl-NL',
		symbol: 'nl-nl',
	},
	{
		displayName: 'Finnish (Finland)',
		id: 'fi_FI',
		label: 'fi-FI',
		symbol: 'fi-fi',
	},
	{
		displayName: 'French (France)',
		id: 'fr_FR',
		label: 'fr-FR',
		symbol: 'fr-fr',
	},
	{
		displayName: 'German (Germany)',
		id: 'de_DE',
		label: 'de-DE',
		symbol: 'de-de',
	},
	{
		displayName: 'Hungarian (Hungary)',
		id: 'hu_HU',
		label: 'hu-HU',
		symbol: 'hu-hu',
	},
	{
		displayName: 'Japanese (Japan)',
		id: 'ja_JP',
		label: 'ja-JP',
		symbol: 'ja-jp',
	},
	{
		displayName: 'Portuguese (Brazil)',
		id: 'pt_BR',
		label: 'pt-BR',
		symbol: 'pt-br',
	},
	{
		displayName: 'Spanish (Spain)',
		id: 'es_ES',
		label: 'es-ES',
		symbol: 'es-es',
	},
	{
		displayName: 'Swedish (Sweden)',
		id: 'sv_SE',
		label: 'sv-SE',
		symbol: 'sv-se',
	},
];

const defaultLanguageId = 'en_US';

const selectedLanguageId = 'en_US';

const props = {
	activeLanguageIds,
	availableLocales,
	defaultLanguageId,
	onActiveLanguageIdsChange: jest.fn(),
	onSelectedLanguageIdChange: jest.fn(),
	selectedLanguageId,
};

jest.mock(
	'../../src/main/resources/META-INF/resources/translation_manager/TranslationAdminModal',
	() => {
		return ({onClose, visible}) => {
			const handleOnClose = () => {
				onClose(['ar_SA', 'en_US', 'nl_NL']);
			};

			return (
				<>
					{visible && (
						<div className="modal">
							<button className="close" onClick={handleOnClose}>
								Close
							</button>
						</div>
					)}
				</>
			);
		};
	}
);

const TranslationAdminSelectorWithState = () => {
	const [activeLanguageIds, setActiveLanguageIds] = React.useState(
		props.activeLanguageIds
	);

	return (
		<>
			<button
				data-testid="change-state"
				onClick={() =>
					setActiveLanguageIds(['ar_SA', 'en_US', 'nl_NL'])
				}
			>
				Change State
			</button>

			<TranslationAdminSelector
				{...props}
				activeLanguageIds={activeLanguageIds}
			/>
		</>
	);
};

describe('TranslationAdminSelector', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	afterEach(() => {
		jest.clearAllTimers();
		jest.restoreAllMocks();
		cleanup();
	});

	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders a dropdown trigger with the selected locale flag icon as content', () => {
		const {asFragment} = render(<TranslationAdminSelector {...props} />);

		const buttonElement = document.querySelector('button');

		buttonElement.id = '';

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders an open dropdown with the list of active languages', async () => {
		const {getByTitle} = render(<TranslationAdminSelector {...props} />);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		const dropdownMenu = document.querySelector('.dropdown-menu');

		expect(dropdownMenu).toMatchSnapshot();
	});

	it('renders an open dropdown with the list of active languages and manage translation option', async () => {
		const {getByTitle} = render(
			<TranslationAdminSelector adminMode={true} {...props} />
		);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		const dropdownMenu = document.querySelector('.dropdown-menu');

		expect(dropdownMenu).toMatchSnapshot();
	});

	it('renders a modal when click on Manage Translations', async () => {
		const {getByTitle} = render(
			<TranslationAdminSelector adminMode={true} {...props} />
		);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		const dropdownMenu = document.querySelector('.dropdown-menu');

		const manageTranslationsTrigger = dropdownMenu.querySelector(
			'[data-testid="translation-modal-trigger"]'
		);

		fireEvent.click(manageTranslationsTrigger);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const modal = document.querySelector('.modal');

		expect(modal).toMatchSnapshot();
	});

	it('renders an open dropdown with updated active locales when added from the Manage Translations Modal', async () => {
		const {getByTitle} = render(
			<TranslationAdminSelector adminMode={true} {...props} />
		);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		const dropdownMenu = document.querySelector('.dropdown-menu');

		const manageTranslationsTrigger = dropdownMenu.querySelector(
			'[data-testid="translation-modal-trigger"]'
		);

		fireEvent.click(manageTranslationsTrigger);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const modalCloseButton = document.querySelector('.modal .close');

		fireEvent.click(modalCloseButton);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		fireEvent.click(trigger);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		expect(dropdownMenu).toMatchSnapshot();
		expect(props.onActiveLanguageIdsChange).toHaveBeenLastCalledWith([
			'ar_SA',
			'en_US',
			'nl_NL',
		]);
	});

	it('calls onSelectedLocaleChange callback on dropdown locale selection', () => {
		const {getByRole, getByTitle} = render(
			<TranslationAdminSelector {...props} />
		);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const dropdownMenu = document.querySelector('.dropdown-menu');

		const localeElement =
			dropdownMenu.querySelectorAll('.dropdown-item')[1];

		fireEvent.click(localeElement);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		expect(props.onSelectedLanguageIdChange).toHaveBeenLastCalledWith(
			'ca_ES'
		);

		const languageSelector = getByRole('combobox');

		expect(within(languageSelector).getByText('ca-ES')).toBeInTheDocument();
	});

	it('is used as a controlled component', () => {
		const {getByTestId, getByTitle} = render(
			<TranslationAdminSelectorWithState />
		);

		const changeStateButton = getByTestId('change-state');

		fireEvent.click(changeStateButton);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const dropdownMenu = document.querySelector('.dropdown-menu');

		expect(dropdownMenu).toMatchSnapshot();
	});

	it('selects the default locale if the current selected one is removed from the active locales list', () => {
		const {asFragment, getByTitle} = render(
			<TranslationAdminSelector
				adminMode={true}
				selectedLanguageId="ca_ES"
				{...props}
			/>
		);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		const dropdownMenu = document.querySelector('.dropdown-menu');

		const manageTranslationsTrigger = dropdownMenu.querySelector(
			'[data-testid="translation-modal-trigger"]'
		);

		fireEvent.click(manageTranslationsTrigger);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const modalCloseButton = document.querySelector('.modal .close');

		fireEvent.click(modalCloseButton);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders horizontal selector when the display type is HORIZONTAL', () => {
		render(
			<TranslationAdminSelector
				displayType="HORIZONTAL"
				selectedLanguageId="ca_ES"
				{...props}
			/>
		);

		const horizontalSelector = document.querySelector(
			'.form-control-select'
		);

		expect(horizontalSelector).toBeTruthy();
	});

	it('calls onSelectorActiveChange when the trigger is clicked', () => {
		const onSelectorActiveChange = jest.fn();

		const {getByTitle} = render(
			<TranslationAdminSelector
				{...props}
				onSelectorActiveChange={onSelectorActiveChange}
				selectedLanguageId="en_US"
			/>
		);

		const trigger = getByTitle('select-a-language');

		fireEvent.click(trigger);

		expect(onSelectorActiveChange).toBeCalled();
	});
});
