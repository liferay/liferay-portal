/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import filterCreationActions from '../../../src/main/resources/META-INF/resources/utils/actionItems/filterCreationActions';

describe('filterCreationActions', () => {
	const globalCollectionActions = {create: {}};

	it('keeps actions without a permission key or with a granted one', () => {
		expect(
			filterCreationActions({
				customActions: [
					{label: 'Free'},
					{data: {permissionKey: 'CREATE'}, label: 'Granted'},
					{data: {permissionKey: 'update'}, label: 'Denied'},
				],
				globalCollectionActions,
			})
		).toEqual([
			{label: 'Free'},
			{data: {permissionKey: 'CREATE'}, label: 'Granted'},
		]);
	});

	it('filters the items of groups and contextual actions', () => {
		expect(
			filterCreationActions({
				customActions: [
					{
						items: [
							{data: {permissionKey: 'create'}, label: 'Granted'},
							{data: {permissionKey: 'update'}, label: 'Denied'},
						],
						label: 'Group',
						type: 'group',
					},
					{
						items: [
							{
								items: [
									{
										data: {permissionKey: 'update'},
										label: 'Denied',
									},
									{label: 'Free'},
								],
								label: 'Nested Contextual',
								type: 'contextual',
							},
						],
						label: 'Contextual',
						type: 'contextual',
					},
				],
				globalCollectionActions,
			})
		).toEqual([
			{
				items: [{data: {permissionKey: 'create'}, label: 'Granted'}],
				label: 'Group',
				type: 'group',
			},
			{
				items: [
					{
						items: [{label: 'Free'}],
						label: 'Nested Contextual',
						type: 'contextual',
					},
				],
				label: 'Contextual',
				type: 'contextual',
			},
		]);
	});

	it('removes groups that end up without items', () => {
		expect(
			filterCreationActions({
				customActions: [
					{
						items: [
							{data: {permissionKey: 'update'}, label: 'Denied'},
						],
						label: 'Denied Group',
						type: 'group',
					},
					{items: [], label: 'Empty Contextual', type: 'contextual'},
					{label: 'Free'},
				],
				globalCollectionActions,
			})
		).toEqual([{label: 'Free'}]);
	});

	it('removes a group whose own permission key is not granted', () => {
		expect(
			filterCreationActions({
				customActions: [
					{
						data: {permissionKey: 'update'},
						items: [{label: 'Free'}],
						label: 'Denied Group',
						type: 'group',
					},
				],
				globalCollectionActions,
			})
		).toEqual([]);
	});

	it('does not filter the items of an action of type item', () => {
		const action = {
			items: [{data: {permissionKey: 'update'}, label: 'Denied'}],
			label: 'Item',
			type: 'item' as const,
		};

		expect(
			filterCreationActions({
				customActions: [action],
				globalCollectionActions,
			})
		).toEqual([action]);
	});
});
