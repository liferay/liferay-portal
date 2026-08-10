/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokenValuesSorter} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/getFrontendTokenValuesSorter';

const DEFINITIONS = [
	{id: 'global', priority: 100},
	{id: 'theme', priority: 300},
];

const DEFAULT_PRIORITY = 300;

describe('getFrontendTokenValuesSorter', () => {
	it('sorts global values before theme values', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: DEFINITIONS,
		});

		const result = sortFrontendTokenValues({
			a: {
				cssVariableMapping: 'color-1',
				tokenDefinitionId: 'theme',
				value: 'red',
			},
			b: {
				cssVariableMapping: 'color-2',
				tokenDefinitionId: 'global',
				value: 'blue',
			},
		});

		expect(result[0].tokenDefinitionId).toBe('global');
		expect(result[1].tokenDefinitionId).toBe('theme');
	});

	it('sorts custom-tagged values above theme values regardless of definitions order', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			customTokenDefinitionId: 'custom',
			customTokenDefinitionPriority: 500,
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: DEFINITIONS,
		});

		const result = sortFrontendTokenValues({
			a: {
				cssVariableMapping: 'color-1',
				tokenDefinitionId: 'theme',
				value: 'red',
			},
			b: {
				cssVariableMapping: 'color-2',
				tokenDefinitionId: 'custom',
				value: 'green',
			},
		});

		expect(result[0].tokenDefinitionId).toBe('theme');
		expect(result[1].tokenDefinitionId).toBe('custom');
	});

	it('does not treat a missing tokenDefinitionId as a match for an unset customTokenDefinitionId', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: DEFINITIONS,
		});

		const result = sortFrontendTokenValues({
			a: {cssVariableMapping: 'color-1', value: 'red'},
			b: {
				cssVariableMapping: 'color-2',
				tokenDefinitionId: 'global',
				value: 'blue',
			},
		});

		expect(result[0].tokenDefinitionId).toBe('global');
		expect(result[1].value).toBe('red');
	});

	it('does not apply customTokenDefinitionPriority to a value missing tokenDefinitionId when customTokenDefinitionId is unset', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			customTokenDefinitionPriority: 500,
			defaultPriority: 400,
			frontendTokenDefinitions: [{id: 'other', priority: 450}],
		});

		const result = sortFrontendTokenValues({
			a: {cssVariableMapping: 'color-1', value: 'red'},
			b: {
				cssVariableMapping: 'color-2',
				tokenDefinitionId: 'other',
				value: 'blue',
			},
		});

		expect(result[0].value).toBe('red');
		expect(result[1].value).toBe('blue');
	});

	it('applies defaultPriority to values without tokenDefinitionId', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: DEFINITIONS,
		});

		const result = sortFrontendTokenValues({
			a: {
				cssVariableMapping: 'color-1',
				tokenDefinitionId: 'global',
				value: 'blue',
			},
			b: {cssVariableMapping: 'color-2', value: 'red'},
		});

		expect(result[0].tokenDefinitionId).toBe('global');
		expect(result[1].value).toBe('red');
	});

	it('returns empty array for empty values', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: DEFINITIONS,
		});

		expect(sortFrontendTokenValues({})).toEqual([]);
	});

	it('returns all values when definitions list is empty', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: [],
		});

		const result = sortFrontendTokenValues({
			a: {
				cssVariableMapping: 'color-1',
				tokenDefinitionId: 'theme',
				value: 'red',
			},
		});

		expect(result).toHaveLength(1);
	});

	it('reuses memoized priorities across calls', () => {
		const sortFrontendTokenValues = getFrontendTokenValuesSorter({
			defaultPriority: DEFAULT_PRIORITY,
			frontendTokenDefinitions: DEFINITIONS,
		});

		expect(
			sortFrontendTokenValues({
				a: {tokenDefinitionId: 'theme', value: 'red'},
			})[0].value
		).toBe('red');

		expect(
			sortFrontendTokenValues({
				a: {tokenDefinitionId: 'global', value: 'blue'},
				b: {tokenDefinitionId: 'theme', value: 'red'},
			})[0].value
		).toBe('blue');
	});
});
