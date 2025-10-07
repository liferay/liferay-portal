/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IItemActionsData,
	IItemsActions,
} from '../../../src/main/resources/META-INF/resources';
import filterItemActions from '../../../src/main/resources/META-INF/resources/utils/actionItems/filterItemActions';
const baseItemActions: IItemsActions[] = [
	{
		href: '/o/data-test-endpoint/{id}',
		label: 'Link action sample',
		target: 'link',
	},
	{
		href: '/home',
		label: 'Another one',
		target: 'link',
	},
	{
		label: 'View Details',
		target: 'infoPanel',
	},
];

const generateCustomItemActions = function (
	data?: Array<IItemActionsData>
): IItemsActions[] {
	if (data && data.length !== baseItemActions.length) {
		return baseItemActions.map((action) => {
			return {
				...action,
				data: data[0],
			};
		});
	}
	else if (data && data.length === baseItemActions.length) {
		return baseItemActions.map((action, index) => {
			return {
				...action,
				data: data[index],
			};
		});
	}

	return baseItemActions;
};

const availableItemData = {
	actions: {
		delete: {
			href: 'http://someurl/o/data-test-endpoint/fields/38212',
			method: 'DELETE',
		},
		get: {
			href: 'http://someurl/o/data-test-endpoint/fields/38212',
			method: 'GET',
		},
		permissions: {
			href: 'http://someurl/o/data-test-endpoint/fields/38212/permissions',
			method: 'GET',
		},
		replace: {
			href: 'http://someurl/o/data-test-endpoint/fields/38212',
			method: 'PUT',
		},
		standAloneAction: {
			href: 'http://someurl/o/data-test-endpoint/fields/38212',
			method: 'POST',
		},
		update: {
			href: 'http://someurl/o/data-test-endpoint/fields/38212',
			method: 'PATCH',
		},
	},
	color: 'blue',
	creator: {
		additionalName: '',
		contentType: 'UserAccount',
		familyName: 'Test',
		givenName: 'Test',
		id: 2222,
		name: 'Test Test',
	},
	dateCreated: '2025-03-13T08:27:46Z',
	id: 38212,
	label: 'id',
	label_i18n: {
		en_US: 'id',
	},
	name: 'id',
	rating: 3,
	renderer: 'default',
	sortable: true,
	type: 'integer',
};

describe('filterItemActions', () => {
	describe('when permissionKey is defined for an action', () => {
		it('returns the actions where the permissionKey matches the itemData.actions key', () => {
			const customActionsWithPermissionKey = generateCustomItemActions([
				{permissionKey: 'DELETE'},
				{permissionKey: 'POST'},
				{permissionKey: 'PUT'},
			]);
			const filteredActions = filterItemActions({
				actions: customActionsWithPermissionKey,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions.length).toBeLessThan(
				customActionsWithPermissionKey.length
			);
		});

		it('returns the actions where the permissionKey matches the itemData.actions key regardless of letter case', () => {
			const customItemActions = generateCustomItemActions([
				{
					permissionKey: 'STANDALONEACTION',
				},
			]);
			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions[0].data).toMatchObject(
				customItemActions[0].data!
			);
		});
	});

	describe('when permissionKey is not defined for an item', () => {
		it('returns all the actions', () => {
			const customItemActions = generateCustomItemActions();

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectable: true,
				selectedItemsKey: 'id',
			});

			expect(filteredActions).toMatchObject(customItemActions);
			expect(filteredActions.length).toEqual(3);
		});

		it('returns all the actions but those with target "infoPanel" if selectable is "false"', () => {
			const customItemActions = generateCustomItemActions();

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectable: false,
				selectedItemsKey: 'id',
			});

			expect(filteredActions).not.toMatchObject(customItemActions);
			expect(filteredActions.length).toEqual(2);
		});
	});

	describe('when permissionKey and action visibility filters are defined for an action item', () => {
		it('returns only the action that matches the permissionKey and the action visibility filter criteria', () => {
			const customItemActions = generateCustomItemActions([
				{
					permissionKey: 'UPDATE',
					visibilityFilters: {color: 'green'},
				},
				{
					permissionKey: 'UPDATE',
					visibilityFilters: {color: 'blue'},
				},
				{
					permissionKey: 'UPDATE',
					visibilityFilters: {color: 'yellow'},
				},
			]);

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions.length).toEqual(1);
			expect(filteredActions[0]).toMatchObject(customItemActions[1]);
		});
	});

	describe('when only action visibility filters are defined for an action item', () => {
		it('returns only the action that matches the action visibility filter criteria', () => {
			const customItemActions = generateCustomItemActions([
				{
					visibilityFilters: {color: 'green'},
				},
				{
					visibilityFilters: {color: 'blue'},
				},
				{
					visibilityFilters: {color: 'yellow'},
				},
			]);

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions.length).toEqual(1);

			expect(filteredActions[0]).toMatchObject(customItemActions[1]);
		});

		it('returns only the actions that matches all action visibility filters criteria', () => {
			const customItemActions = generateCustomItemActions([
				{
					visibilityFilters: {color: 'green'},
				},
				{
					visibilityFilters: {
						color: 'blue',
						type: 'boolean',
					},
				},
				{
					visibilityFilters: {color: 'yellow'},
				},
			]);

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions.length).toEqual(0);
		});

		it('returns actions if the filter criteria key includes a composed field name', () => {
			const customItemActions = generateCustomItemActions([
				{
					visibilityFilters: {color: 'green'},
				},
				{
					visibilityFilters: {
						'color': 'blue',
						'creator.name': 'Test Test',
					},
				},
				{
					visibilityFilters: {color: 'green'},
				},
			]);

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions.length).toEqual(1);
			expect(filteredActions[0]).toMatchObject(customItemActions[1]);
		});

		it('returns actions if the filter criteria is based on a boolean value', () => {
			const customItemActions = generateCustomItemActions([
				{
					visibilityFilters: {color: 'green'},
				},
				{
					visibilityFilters: {
						color: 'blue',
						sortable: true,
					},
				},
				{
					visibilityFilters: {color: 'green'},
				},
			]);

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});

			expect(filteredActions.length).toEqual(1);
			expect(filteredActions[0]).toMatchObject(customItemActions[1]);
		});
	});

	describe('when only isVisible action callback is defined for item actions', () => {
		it('returns the actions that match the action isVisible callback criteria', () => {
			const isVisibleFn = {
				isPopular(item: any) {
					return item.type === 'integer' && item.rating > 3;
				},
			};

			const spyCallback = jest.spyOn(isVisibleFn, 'isPopular');

			const testActionsWithIsVisibleCallback: IItemsActions[] =
				baseItemActions.map((action) => {
					return {
						...action,
						isVisible: isVisibleFn.isPopular,
					};
				});

			const filteredActions = filterItemActions({
				actions: testActionsWithIsVisibleCallback,
				itemData: availableItemData,
				selectable: true,
				selectedItemsKey: 'id',
			});

			expect(spyCallback).toHaveBeenCalledTimes(3);
			expect(filteredActions.length).toEqual(0);
		});
	});

	describe('disable actions', () => {
		it('returns all actions enabled by default', () => {
			const customItemActions = generateCustomItemActions();

			const filteredActions = filterItemActions({
				actions: customItemActions,
				itemData: availableItemData,
				selectedItemsKey: 'id',
			});
			const disabledActions = filteredActions.filter(
				(action) => action.disabled
			);

			expect(disabledActions.length).toEqual(0);
		});

		it('returns an action of type "infoPanel" as disabled when the info panel is open and an item is selected', () => {
			const customItemActions = generateCustomItemActions();

			const filteredActions = filterItemActions({
				actions: customItemActions,
				infoPanelOpen: true,
				itemData: availableItemData,
				selectable: true,
				selectedItemsKey: 'id',
				selectedItemsValue: [38212],
			});

			const disabledActions = filteredActions.filter(
				(action) => action.disabled
			);

			expect(disabledActions.length).toEqual(1);
			expect(disabledActions[0].target).toEqual('infoPanel');
		});
	});
});
