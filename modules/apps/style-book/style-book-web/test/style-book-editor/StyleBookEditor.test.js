/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import StyleBookEditor from '../../src/main/resources/META-INF/resources/js/style-book-editor/StyleBookEditor';

const DEFAULT_TOKEN_DEFINITION_PRIORITY = 300;

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/useCloseProductMenu',
	() => ({
		useCloseProductMenu: jest.fn(),
	})
);

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/Toolbar',
	() => () => <div data-testid="Toolbar" />
);

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/LayoutPreview',
	() => () => <div data-testid="LayoutPreview" />
);

global.Liferay = {
	Language: {
		get: jest.fn((key) => key),
	},
};

const frontendTokenDefinitions = [
	{
		frontendTokenCategories: [
			{
				frontendTokenSets: [
					{
						frontendTokens: [
							{
								defaultValue: '#000',
								label: 'Theme Token',
								mappings: [
									{type: 'cssVariable', value: 'theme-token'},
								],
								name: 'themeToken',
								type: 'color',
							},
							{
								defaultValue: '#000',
								label: 'Conflicting Token (Theme)',
								mappings: [
									{
										type: 'cssVariable',
										value: 'conflicting-token',
									},
								],
								name: 'conflictingToken',
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
		priority: 300,
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
									{type: 'cssVariable', value: 'clay-token'},
								],
								name: 'clayToken',
								type: 'color',
							},
							{
								defaultValue: '#000',
								label: 'Conflicting Token (Clay)',
								mappings: [
									{
										type: 'cssVariable',
										value: 'conflicting-token',
									},
								],
								name: 'conflictingToken',
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
		priority: 100,
	},
];

const previewOptions = [
	{data: {recentLayouts: []}, type: 'page'},
	{data: {recentLayouts: []}, type: 'master'},
	{data: {recentLayouts: []}, type: 'pageTemplate'},
	{data: {recentLayouts: []}, type: 'displayPageTemplate'},
	{data: {recentLayouts: []}, type: 'fragmentCollection'},
];

describe('StyleBookEditor', () => {
	it('respects priority precedence when multiple tokens map to the same CSS variable', () => {
		const conflictingValues = {
			'clay:conflictingToken': {
				cssVariableMapping: 'conflicting-token',
				tokenDefinitionId: 'clay',
				value: '#fff',
			},
			'theme:conflictingToken': {
				cssVariableMapping: 'conflicting-token',
				tokenDefinitionId: 'theme',
				value: '#000',
			},
		};

		const {container} = render(
			<StyleBookEditor
				defaultTokenDefinitionPriority={
					DEFAULT_TOKEN_DEFINITION_PRIORITY
				}
				frontendTokenDefinitions={frontendTokenDefinitions}
				frontendTokensValues={conflictingValues}
				previewOptions={previewOptions}
				themeFrontendTokenDefinitionId="theme"
			/>
		);

		const sidebar = container.querySelector('.style-book-editor__sidebar');

		expect(sidebar.style.getPropertyValue('--conflicting-token')).toBe(
			'#000'
		);

		const {container: reversedContainer} = render(
			<StyleBookEditor
				defaultTokenDefinitionPriority={
					DEFAULT_TOKEN_DEFINITION_PRIORITY
				}
				frontendTokenDefinitions={frontendTokenDefinitions}
				frontendTokensValues={Object.fromEntries(
					Object.entries(conflictingValues).toReversed()
				)}
				previewOptions={previewOptions}
				themeFrontendTokenDefinitionId="theme"
			/>
		);

		const reversedSidebar = reversedContainer.querySelector(
			'.style-book-editor__sidebar'
		);

		expect(
			reversedSidebar.style.getPropertyValue('--conflicting-token')
		).toBe('#000');
	});
});
