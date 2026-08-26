/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {transformFDSBulkActions} from '../../js/utils/transformFDSBulkActions';

const deleteAction = {data: {id: 'delete', permissionKey: 'delete'}};

describe('transformFDSBulkActions', () => {
	it('hides an action when a selected item has no actions map', () => {
		const [action] = transformFDSBulkActions([deleteAction]);

		expect(
			action.isVisible({
				selectedItems: [{actions: {delete: {}}}, {}],
			})
		).toBe(false);
	});

	it('hides an action when any selected item lacks the permission', () => {
		const [action] = transformFDSBulkActions([deleteAction]);

		expect(
			action.isVisible({
				selectedItems: [{actions: {delete: {}}}, {actions: {get: {}}}],
			})
		).toBe(false);
	});

	it('hides an action when no selection context is given', () => {
		const [action] = transformFDSBulkActions([deleteAction]);

		expect(action.isVisible()).toBe(false);
		expect(action.isVisible({})).toBe(false);
	});

	it('keeps an action without a permission key visible', () => {
		const [action] = transformFDSBulkActions([{data: {id: 'assign-to'}}]);

		expect(
			action.isVisible({
				selectedItems: [{actions: {get: {}}}],
			})
		).toBe(true);
	});

	it('matches permission keys case-insensitively', () => {
		const [action] = transformFDSBulkActions([
			{data: {id: 'delete', permissionKey: 'DELETE'}},
		]);

		expect(
			action.isVisible({
				selectedItems: [{actions: {delete: {}}}],
			})
		).toBe(true);
	});

	it('preserves a preexisting visibility check', () => {
		const [action] = transformFDSBulkActions([
			{...deleteAction, isVisible: () => false},
		]);

		expect(
			action.isVisible({
				selectedItems: [{actions: {delete: {}}}],
			})
		).toBe(false);
	});

	it('resolves the permission key per item with a custom resolver', () => {
		const [action] = transformFDSBulkActions(
			[{data: {id: 'assign-to', permissionKey: 'update'}}],
			(bulkAction, item) =>
				item.workflow ? 'assignToUser' : bulkAction.data.permissionKey
		);

		expect(
			action.isVisible({
				selectedItems: [
					{actions: {update: {}}},
					{actions: {assignToUser: {}}, workflow: true},
				],
			})
		).toBe(true);
		expect(
			action.isVisible({
				selectedItems: [{actions: {update: {}}, workflow: true}],
			})
		).toBe(false);
	});

	it('shows an action when all items are selected', () => {
		const [action] = transformFDSBulkActions([deleteAction]);

		expect(action.isVisible({allItemsSelectedActive: true})).toBe(true);
		expect(
			action.isVisible({
				allItemsSelectedActive: true,
				selectedItems: [{actions: {get: {}}}],
			})
		).toBe(true);
	});

	it('shows an action when every selected item has the permission', () => {
		const [action] = transformFDSBulkActions([deleteAction]);

		expect(
			action.isVisible({
				selectedItems: [
					{actions: {delete: {}}},
					{actions: {delete: {}, get: {}}},
				],
			})
		).toBe(true);
	});
});
