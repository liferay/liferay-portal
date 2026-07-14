/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {initialSpace} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/SpacePicker';
import {
	InventoryContext,
	initialLanguage,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/inventory/InventoryContext';
import {
	LanguagesDropdown,
	localizations,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/inventory/components/LanguagesDropdown';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

const mockedSpaceService = SpaceService as jest.Mocked<typeof SpaceService>;

const mockedContext = {
	changeLanguage: jest.fn(),
	changeSpace: jest.fn(),
	constants: {},
	filters: {
		language: initialLanguage,
		space: initialSpace,
	},
};

describe('[CMS Dashboard] Components: LanguagesDropdown', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly with all languages when all spaces is selected', async () => {
		render(
			<InventoryContext.Provider value={mockedContext}>
				<LanguagesDropdown />
			</InventoryContext.Provider>
		);

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-languages',
		});

		expect(trigger).toHaveTextContent('all-languages');

		fireEvent.click(trigger);

		const listbox = await screen.findByRole('listbox');

		expect(
			within(listbox).getByRole('option', {name: 'all-languages'})
		).toBeInTheDocument();

		Object.values(localizations).forEach((translation) => {
			expect(
				within(listbox).getByRole('option', {name: translation})
			).toBeInTheDocument();
		});
	});

	it('filters languages when searching and restores them when reopening', async () => {
		render(
			<InventoryContext.Provider value={mockedContext}>
				<LanguagesDropdown />
			</InventoryContext.Provider>
		);

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-languages',
		});

		fireEvent.click(trigger);

		let listbox = await screen.findByRole('listbox');

		const searchInput = screen.getByPlaceholderText('search');

		// Search for English

		fireEvent.change(searchInput, {target: {value: localizations.en_US}});

		await waitFor(() => {
			expect(
				within(listbox).getByRole('option', {name: localizations.en_US})
			).toBeInTheDocument();

			expect(
				within(listbox).queryByRole('option', {
					name: localizations.es_ES,
				})
			).not.toBeInTheDocument();
		});

		// Search for something that doesn't exist

		fireEvent.change(searchInput, {target: {value: 'NonExistentLanguage'}});

		await waitFor(() => {
			expect(
				within(listbox).queryByRole('option', {
					name: localizations.en_US,
				})
			).not.toBeInTheDocument();
		});

		// Close and reopen the dropdown

		fireEvent.click(trigger);

		fireEvent.click(trigger);

		listbox = await screen.findByRole('listbox');

		// Should show all languages again

		await waitFor(() => {
			expect(
				within(listbox).getByRole('option', {name: localizations.en_US})
			).toBeInTheDocument();

			expect(
				within(listbox).getByRole('option', {name: localizations.es_ES})
			).toBeInTheDocument();
		});
	});

	it('filters languages when a specific space is selected', async () => {
		const spaceWithLimitedLanguages = {
			...initialSpace,
			externalReferenceCode: 'SPACE_ERC',
			value: '123',
		};

		mockedSpaceService.getSpace.mockResolvedValue({
			settings: {
				availableLanguageIds: ['en-US', 'es-ES'],
			},
		} as any);

		render(
			<InventoryContext.Provider
				value={{
					...mockedContext,
					filters: {
						...mockedContext.filters,
						space: spaceWithLimitedLanguages,
					},
				}}
			>
				<LanguagesDropdown />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(mockedSpaceService.getSpace).toHaveBeenCalledWith(
				'SPACE_ERC'
			)
		);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-languages'})
		);

		const listbox = await screen.findByRole('listbox');

		expect(
			within(listbox).getByRole('option', {name: 'all-languages'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: localizations.en_US})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: localizations.es_ES})
		).toBeInTheDocument();

		// Should not show other languages

		expect(
			within(listbox).queryByRole('option', {name: localizations.pt_BR})
		).not.toBeInTheDocument();
	});

	it('resets language selection if the current language is not available in the new space', async () => {
		const spaceWithLimitedLanguages = {
			...initialSpace,
			externalReferenceCode: 'SPACE_ERC',
			value: '123',
		};

		// Current selected language is Portuguese (pt_BR)

		const mockedContextWithSelection = {
			...mockedContext,
			filters: {
				language: {label: localizations.pt_BR, value: 'pt_BR'},
				space: spaceWithLimitedLanguages,
			},
		};

		// New space only supports English and Spanish

		mockedSpaceService.getSpace.mockResolvedValue({
			settings: {
				availableLanguageIds: ['en-US', 'es-ES'],
			},
		} as any);

		render(
			<InventoryContext.Provider value={mockedContextWithSelection}>
				<LanguagesDropdown />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(mockedContext.changeLanguage).toHaveBeenCalledWith(
				initialLanguage
			)
		);
	});

	it('shows all languages if space settings does not have availableLanguageIds', async () => {
		const spaceWithoutSettings = {
			...initialSpace,
			externalReferenceCode: 'SPACE_ERC',
			value: '123',
		};

		mockedSpaceService.getSpace.mockResolvedValue({
			settings: {},
		} as any);

		render(
			<InventoryContext.Provider
				value={{
					...mockedContext,
					filters: {
						...mockedContext.filters,
						space: spaceWithoutSettings,
					},
				}}
			>
				<LanguagesDropdown />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(mockedSpaceService.getSpace).toHaveBeenCalled()
		);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-languages'})
		);

		const listbox = await screen.findByRole('listbox');

		Object.values(localizations).forEach((translation) => {
			expect(
				within(listbox).getByRole('option', {name: translation})
			).toBeInTheDocument();
		});
	});
});
