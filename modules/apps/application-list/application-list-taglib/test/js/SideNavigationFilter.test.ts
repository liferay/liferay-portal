/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {filterItemsByQuery} from '../../src/main/resources/META-INF/resources/js/useSideNavigationFilter';

describe('Single layer items filtering', () => {
	const singleLayerItems = [
		{id: '1', label: 'Blogs'},
		{id: '2', label: 'Wiki'},
		{id: '3', label: 'Web Content'},
		{id: '4', label: 'Content Templates'},
	];

	it('returns all items when query is empty', () => {
		const result = filterItemsByQuery(singleLayerItems, '');

		expect(result.items).toHaveLength(singleLayerItems.length);
		expect(result.items).toBe(singleLayerItems);
	});

	it('returns matching leaf items', () => {
		const result = filterItemsByQuery(singleLayerItems, 'blo');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].label).toBe('Blogs');
	});

	it('returns empty when nothing matches', () => {
		const result = filterItemsByQuery(singleLayerItems, 'calendar');

		expect(result.items).toHaveLength(0);
	});

	it('is case-sensitive (expects query to be lower case and trimmed)', () => {
		const lowerCaseResult = filterItemsByQuery(singleLayerItems, 'wiki');

		expect(lowerCaseResult.items).toHaveLength(1);
		expect(lowerCaseResult.items[0].label).toBe('Wiki');

		const upperCaseResult = filterItemsByQuery(singleLayerItems, 'WIKI');

		expect(upperCaseResult.items).toHaveLength(0);

		const leadingTrailingSpacesResult = filterItemsByQuery(
			singleLayerItems,
			' wiki '
		);

		expect(leadingTrailingSpacesResult.items).toHaveLength(0);
	});

	it('returns all items when query is a common substring', () => {
		const result = filterItemsByQuery(singleLayerItems, 'content');

		expect(result.items).toHaveLength(2);
	});
});

describe('Multi layer items filtering', () => {
	const multiLayerItems = [
		{
			id: 'parent1',
			items: [
				{id: 'child1', label: 'Blogs'},
				{id: 'child2', label: 'Wiki'},
			],
			label: 'Content',
		},
		{
			id: 'parent2',
			items: [
				{id: 'child3', label: 'Web Content'},
				{id: 'child4', label: 'Content Templates'},
			],
			label: 'Assets',
		},
	];

	it('returns all items when query is empty', () => {
		const result = filterItemsByQuery(multiLayerItems, '');

		expect(result.items).toBe(multiLayerItems);
		expect(result.expandedKeys).toBeUndefined();
	});

	it('returns empty when nothing matches', () => {
		const result = filterItemsByQuery(multiLayerItems, 'calendar');

		expect(result.items).toHaveLength(0);
		expect(result.expandedKeys).toBeDefined();
		expect(result.expandedKeys?.size).toBe(0);
	});

	it('filters nested items and expands parent', () => {
		const result = filterItemsByQuery(multiLayerItems, 'blog');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('parent1');
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].label).toBe('Blogs');
		expect(result.expandedKeys?.has('parent1')).toBe(true);
		expect(result.expandedKeys?.has('parent2')).toBe(false);
	});

	it('matches the whole parent item if the query matches the parent label', () => {
		const result = filterItemsByQuery(multiLayerItems, 'assets');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('parent2');
		expect(result.items[0].items).toHaveLength(2);
		expect(result.items[0].items![0].label).toBe('Web Content');
		expect(result.items[0].items![1].label).toBe('Content Templates');
		expect(result.expandedKeys?.has('parent2')).toBe(true);
		expect(result.expandedKeys?.has('parent1')).toBe(false);
	});
});

describe('Filter-only items filtering', () => {
	const filterOnlyItems = [
		{
			id: 'users',
			items: [
				{
					href: 'usersAndOrganizationsHref',
					id: 'usersAndOrganizations',
					items: [
						{
							canonicalName: 'Users',
							filterOnly: true,
							href: 'usersTabHref',
							id: 'usersTab',
							label: 'Users',
						},
						{
							canonicalName: 'Organizations',
							filterOnly: true,
							href: 'organizationsTabHref',
							id: 'organizationsTab',
							label: 'Organizations',
						},
					],
					label: 'Users and Organizations',
				},
				{href: 'rolesHref', id: 'roles', label: 'Roles'},
			],
			label: 'Users',
		},
	];

	it('hides filter-only items when the query is empty', () => {
		const result = filterItemsByQuery(filterOnlyItems, '');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].items).toHaveLength(2);
		expect(result.items[0].items![0].items).toBeUndefined();
	});

	it('shows a matching filter-only item with its parent', () => {
		const result = filterItemsByQuery(filterOnlyItems, 'organizations');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('users');
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].id).toBe('usersAndOrganizations');
		expect(result.items[0].items![0].items).toHaveLength(1);
		expect(result.items[0].items![0].items![0].id).toBe('organizationsTab');
		expect(result.expandedKeys?.has('users')).toBe(true);
		expect(result.expandedKeys?.has('usersAndOrganizations')).toBe(true);
	});

	it('keeps filter-only items hidden when only the parent label matches', () => {
		const result = filterItemsByQuery(filterOnlyItems, 'users and');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('users');
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].id).toBe('usersAndOrganizations');
		expect(result.items[0].items![0].items).toBeUndefined();
	});

	it('shows a matching filter-only item when its parent also matches', () => {
		const result = filterItemsByQuery(filterOnlyItems, 'users');

		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].items).toHaveLength(1);
		expect(result.items[0].items![0].items![0].id).toBe('usersTab');
	});

	it('handles items without children', () => {
		const items = [{href: 'rolesHref', id: 'roles', label: 'Roles'}];

		expect(filterItemsByQuery(items, '').items).toBe(items);
		expect(filterItemsByQuery(items, 'roles').items).toHaveLength(1);
	});

	it('keeps the branches without filter-only items identical', () => {
		const untouchedItem = {
			href: 'rolesHref',
			id: 'roles',
			label: 'Roles',
		};

		const result = filterItemsByQuery(
			[
				untouchedItem,
				{
					id: 'assets',
					items: [
						{
							filterOnly: true,
							href: 'categoriesHref',
							id: 'categories',
							label: 'Categories',
						},
					],
					label: 'Assets',
				},
			],
			''
		);

		expect(result.items[0]).toBe(untouchedItem);
		expect(result.items[1].items).toBeUndefined();
	});
});

describe('Section items filtering', () => {
	const sectionItems = [
		{
			id: 'configuration',
			items: [
				{
					href: 'systemSettingsHref',
					id: 'systemSettings',
					items: [
						{
							filterOnly: true,
							href: 'featureFlagsHref',
							id: 'featureFlags',
							label: 'Feature Flags',
						},
						{
							filterOnly: true,
							href: 'releaseHref',
							id: 'release',
							label: 'Release',
							parentLabel: 'Feature Flags',
						},
						{
							filterOnly: true,
							href: 'betaHref',
							id: 'beta',
							label: 'Beta',
							parentLabel: 'Feature Flags',
						},
					],
					label: 'System Settings',
				},
			],
			label: 'Configuration',
		},
	];

	it('shows a matching section with its own parent as context', () => {
		const result = filterItemsByQuery(sectionItems, 'beta');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].id).toBe('systemSettings');

		const items = result.items[0].items![0].items;

		expect(items).toHaveLength(1);
		expect(items![0].label).toBe('Beta');
		expect(items![0].parentLabel).toBe('Feature Flags');
		expect(result.expandedKeys?.has('systemSettings')).toBe(true);
	});

	it('leaves the sections hidden when only their own parent matches', () => {
		const result = filterItemsByQuery(sectionItems, 'feature flags');

		const items = result.items[0].items![0].items;

		expect(items).toHaveLength(1);
		expect(items![0].label).toBe('Feature Flags');
		expect(items![0].parentLabel).toBeUndefined();
	});

	it('counts the matching items without their context ancestors', () => {
		expect(filterItemsByQuery(sectionItems, 'beta').numberOfMatches).toBe(
			1
		);
		expect(
			filterItemsByQuery(sectionItems, 'feature flags').numberOfMatches
		).toBe(1);
	});

	it('hides the sections when the query is empty', () => {
		const result = filterItemsByQuery(sectionItems, '');

		expect(result.items[0].items![0].items).toBeUndefined();
	});
});
