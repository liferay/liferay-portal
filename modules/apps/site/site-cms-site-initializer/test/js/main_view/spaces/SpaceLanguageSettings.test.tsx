/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {navigate} from 'frontend-js-web';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {LabelValueObject} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import SpaceLanguageSettings from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceLanguageSettings';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	navigate: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService',
	() => ({
		updateSpace: jest
			.fn()
			.mockImplementation(() =>
				Promise.reject(new Error('Network error'))
			),
	})
);

const COMPANY_AVAILABLE_LANGUAGES: LabelValueObject[] = [
	{label: 'English (United States)', value: 'en_US'},
	{label: 'French (France)', value: 'fr_FR'},
	{label: 'German (Germany)', value: 'de_DE'},
	{label: 'Japanese (Japan)', value: 'ja_JP'},
];

const mockProps = {
	backURL: '/all-spaces',
	companyAvailableLanguages: COMPANY_AVAILABLE_LANGUAGES,
	space: {
		externalReferenceCode: 'space-external-reference-code',
		name: 'My Space',
		settings: {
			availableLanguageIds: ['en_US'],
			defaultLanguageId: 'en_US',
			useCustomLanguages: false,
		},
	},
};

const mockPropsWithCustomLanguages = {
	backURL: '/all-spaces',
	companyAvailableLanguages: COMPANY_AVAILABLE_LANGUAGES,
	space: {
		externalReferenceCode: 'space-external-reference-code',
		name: 'My Space',
		settings: {
			availableLanguageIds: ['en_US', 'fr_FR'],
			defaultLanguageId: 'en_US',
			useCustomLanguages: true,
		},
	},
};

const closeToast = async () => {
	await userEvent.click(screen.getByRole('button', {name: 'Close'}));
};

const renderComponent = (props: any = mockProps) => {
	return render(<SpaceLanguageSettings {...props} />);
};

describe('SpaceLanguageSettings', () => {
	beforeEach(() => {
		SpaceService.updateSpace = jest.fn().mockResolvedValue({data: {}});
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the fields with the correct values', () => {
		renderComponent();

		expect(
			screen.getByRole('radio', {
				name: /use-the-default-language-options/,
			})
		).toBeChecked();
	});

	it('checks the accessibility of the language settings', async () => {
		renderComponent();

		const {container} = renderComponent();

		await checkAccessibility({
			context: {exclude: ['.form-control-select'], include: container},
		});
	});

	it('submits form with correct values', async () => {
		renderComponent();

		await userEvent.click(
			screen.getByRole('radio', {
				name: /define-a-custom-default-language-and-additional-active-languages-for-this-space/,
			})
		);

		expect(
			screen.getByRole('combobox', {
				name: /default-language/,
			})
		).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {
				name: 'save',
			})
		);

		await waitFor(() => {
			const {
				space: {externalReferenceCode, settings},
			} = mockProps;

			expect(SpaceService.updateSpace).toBeCalledWith(
				externalReferenceCode,
				{
					externalReferenceCode,
					settings: {
						...settings,
						useCustomLanguages: true,
					},
				}
			);

			expect(
				screen.getByText('My Space-was-saved-successfully')
			).toBeInTheDocument();
		});

		await closeToast();
	});

	it('redirects to the previous url when the cancel button is pressed', async () => {
		renderComponent();

		await userEvent.click(
			screen.getByRole('button', {
				name: 'cancel',
			})
		);

		await waitFor(() => {
			expect(navigate).toHaveBeenCalledWith('/all-spaces');
		});
	});

	it('shows an error toast when the request fails', async () => {
		SpaceService.updateSpace = jest
			.fn()
			.mockResolvedValue({error: 'Error'});

		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(SpaceService.updateSpace).toBeCalled();

			expect(
				screen.queryByText('My Space-was-saved-successfully')
			).not.toBeInTheDocument();

			expect(
				screen.getByText(
					'an-unexpected-error-occurred-while-saving-the-space'
				)
			).toBeInTheDocument();
		});

		await closeToast();
	});

	describe('CustomDefaultLanguage', () => {
		it('renders a LanguageSelectionBox with available and selected languages', () => {
			renderComponent(mockPropsWithCustomLanguages);

			const defaultLanguageSelectionBox = screen.getByRole('combobox', {
				name: /default-language/,
			});

			expect(defaultLanguageSelectionBox).toBeInTheDocument();

			const availableLanguages = screen
				.getByRole('listbox', {
					name: 'available',
				})
				.querySelectorAll('option');

			const selectedLanguages = screen
				.getByRole('listbox', {
					name: 'selected',
				})
				.querySelectorAll('option');

			expect(availableLanguages.length).toBe(2);
			expect(selectedLanguages.length).toBe(2);
		});

		it('moves a language from left to right', async () => {
			renderComponent(mockPropsWithCustomLanguages);

			const availableLanguagesSelect = screen.getByRole('listbox', {
				name: 'available',
			});

			const selectedLanguagesSelect = screen.getByRole('listbox', {
				name: 'selected',
			});

			const ltrButton = screen.getByRole('button', {
				name: 'Transfer Item Left to Right',
			});

			await userEvent.selectOptions(availableLanguagesSelect, ['de_DE']);
			await userEvent.click(ltrButton);

			const availableLanguages =
				availableLanguagesSelect.querySelectorAll('option');
			const selectedLanguages =
				selectedLanguagesSelect.querySelectorAll('option');

			expect(availableLanguages.length).toBe(1);
			expect(selectedLanguages.length).toBe(3);
		});

		it('moves all languages from left to right', async () => {
			renderComponent(mockPropsWithCustomLanguages);

			const availableLanguagesSelect = screen.getByRole('listbox', {
				name: 'available',
			});

			const selectedLanguagesSelect = screen.getByRole('listbox', {
				name: 'selected',
			});

			const ltrButton = screen.getByRole('button', {
				name: 'Transfer Item Left to Right',
			});

			await userEvent.selectOptions(availableLanguagesSelect, [
				'de_DE',
				'ja_JP',
			]);
			await userEvent.click(ltrButton);

			const availableLanguages =
				availableLanguagesSelect.querySelectorAll('option');
			const selectedLanguages =
				selectedLanguagesSelect.querySelectorAll('option');

			expect(availableLanguages.length).toBe(0);
			expect(selectedLanguages.length).toBe(4);
			expect(ltrButton).toBeDisabled();
		});

		it('moves a language from right to left', async () => {
			renderComponent(mockPropsWithCustomLanguages);

			const availableLanguagesSelect = screen.getByRole('listbox', {
				name: 'available',
			});

			const selectedLanguagesSelect = screen.getByRole('listbox', {
				name: 'selected',
			});

			const rtlButton = screen.getByRole('button', {
				name: 'Transfer Item Right to Left',
			});

			await userEvent.selectOptions(selectedLanguagesSelect, ['fr_FR']);
			await userEvent.click(rtlButton);

			const availableLanguages =
				availableLanguagesSelect.querySelectorAll('option');
			const selectedLanguages =
				selectedLanguagesSelect.querySelectorAll('option');

			expect(availableLanguages.length).toBe(3);
			expect(selectedLanguages.length).toBe(1);
			expect(rtlButton).toBeDisabled();
		});

		it('shows an error when trying to move a default language', async () => {
			renderComponent(mockPropsWithCustomLanguages);

			const availableLanguagesSelect = screen.getByRole('listbox', {
				name: 'available',
			});

			const selectedLanguagesSelect = screen.getByRole('listbox', {
				name: 'selected',
			});

			const rtlButton = screen.getByRole('button', {
				name: 'Transfer Item Right to Left',
			});

			await userEvent.selectOptions(selectedLanguagesSelect, ['en_US']);
			await userEvent.click(rtlButton);

			const availableLanguages =
				availableLanguagesSelect.querySelectorAll('option');
			const selectedLanguages =
				selectedLanguagesSelect.querySelectorAll('option');

			expect(availableLanguages.length).toBe(2);
			expect(selectedLanguages.length).toBe(2);
			expect(
				screen.getByText(
					'you-cannot-remove-a-language-that-is-the-current-default-language'
				)
			).toBeInTheDocument();
		});
	});
});
