/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import Sidebar from '../../src/main/resources/META-INF/resources/js/style-book-editor/Sidebar';
import {StyleBookEditorContextProvider} from '../../src/main/resources/META-INF/resources/js/style-book-editor/contexts/StyleBookEditorContext';

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/config',
	() => ({
		config: {
			getFrontendTokenDefinitions: () => [
				{
					frontendTokenCategories: [
						{
							frontendTokenSets: [
								{
									frontendTokens: [
										{
											defaultValue: '#000',
											label: 'Token 1',
											mappings: [
												{
													type: 'cssVariable',
													value: 'token-1',
												},
											],
											name: 'token1',
											tokenDefinitionId: 'theme',
											type: 'color',
										},
									],
									label: 'Set 1',
									name: 'set1',
								},
							],
							label: 'Category 1',
							name: 'category1',
						},
					],
					id: 'theme',
					name: 'Theme Tokens',
				},
				{
					frontendTokenCategories: [
						{
							frontendTokenSets: [
								{
									frontendTokens: [
										{
											defaultValue: '#fff',
											label: 'Clay Token',
											mappings: [
												{
													type: 'cssVariable',
													value: 'clay-token',
												},
											],
											name: 'clayToken',
											tokenDefinitionId: 'clay',
											type: 'color',
										},
									],
									label: 'Clay Set',
									name: 'claySet',
								},
							],
							label: 'Clay Category',
							name: 'clayCategory',
						},
					],
					id: 'clay',
					name: 'Clay Tokens',
				},
			],
			getFrontendTokens: () => ({}),
			sortFrontendTokenValues: (frontendTokensValues) =>
				Object.values(frontendTokensValues),
			themeFrontendTokenDefinitionId: 'theme',
			themeName: 'Classic',
		},
	})
);

global.Liferay = {
	Language: {
		get: jest.fn((key) => key),
	},
};

const renderComponent = () => {
	render(
		<StyleBookEditorContextProvider
			initialState={{
				customFrontendTokenDefinition: {},
				frontendTokensValues: {},
			}}
		>
			<Sidebar />
		</StyleBookEditorContextProvider>
	);
};

describe('Sidebar', () => {
	it('renders Sidebar with definition selector when multiple definitions are present', () => {
		renderComponent();

		expect(
			screen.getByText('frontend-token-definition-provided-by')
		).toBeInTheDocument();
		expect(screen.getAllByText('Classic')[0]).toBeInTheDocument();
	});

	it('switches between definitions using the dropdown', () => {
		renderComponent();

		const triggers = screen.getAllByText('Classic');
		fireEvent.click(triggers[0]);

		const clayOption = screen.getByText('Clay Tokens');
		fireEvent.click(clayOption);

		expect(screen.getAllByText('Clay Tokens')[0]).toBeInTheDocument();
		expect(screen.getAllByText('Clay Category')[0]).toBeInTheDocument();
		expect(screen.getByText('Clay Set')).toBeInTheDocument();
		expect(screen.getByText('Clay Token')).toBeInTheDocument();
	});

	it('resets selected category when switching definitions', () => {
		renderComponent();

		// Initially in Classic, Category 1 is selected

		expect(screen.getAllByText('Category 1')[0]).toBeInTheDocument();

		// Switch to Clay Tokens

		fireEvent.click(screen.getAllByText('Classic')[0]);
		fireEvent.click(screen.getByText('Clay Tokens'));

		// Should show Clay Category now

		expect(screen.getAllByText('Clay Category')[0]).toBeInTheDocument();
		expect(screen.queryByText('Category 1')).not.toBeInTheDocument();
	});
});
