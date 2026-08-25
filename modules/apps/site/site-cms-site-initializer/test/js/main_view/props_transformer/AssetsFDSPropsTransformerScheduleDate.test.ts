/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AssetsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AssetsFDSPropsTransformer';
import {openScheduleDateModal} from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/createScheduleDateModalOpener';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/createScheduleDateModalOpener',
	() => ({
		...(jest.requireActual(
			'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/createScheduleDateModalOpener'
		) as object),
		openScheduleDateModal: jest.fn(),
	})
);

const ITEM = {
	embedded: {
		expirationDate: '2099-01-31T10:00:00Z',
		id: 7,
		reviewDate: '2099-12-31T10:00:00Z',
	},
	entryClassName: 'com.liferay.object.model.ObjectEntry',
};

const SELECTED_DATA = {items: [ITEM], selectAll: false};

function getTransformerProps(itemsActions: unknown[] = []) {
	return AssetsFDSPropsTransformer({
		additionalProps: {},
		creationMenu: {primaryItems: []},
		id: 'allSection',
		itemsActions,
		views: [],
	} as any);
}

describe('[CMS] AssetsFDSPropsTransformer schedule date actions', () => {
	beforeEach(() => jest.clearAllMocks());

	it('opens the modal from a row action with the item it belongs to', async () => {
		const {onActionDropdownItemClick} = getTransformerProps();

		const event = {preventDefault: jest.fn()};

		await onActionDropdownItemClick({
			action: {data: {id: 'update-review-date'}},
			event,
			itemData: ITEM,
		} as any);

		expect(event.preventDefault).toHaveBeenCalled();
		expect(openScheduleDateModal).toHaveBeenCalledWith({
			actionId: 'update-review-date',
			apiURL: undefined,
			dataSetId: 'allSection',
			itemData: ITEM,
		});
	});

	it('opens the modal from a bulk action with the whole selection', async () => {
		const {onBulkActionItemClick} = getTransformerProps();

		await onBulkActionItemClick({
			action: {data: {id: 'update-expiration-date'}},
			selectedData: SELECTED_DATA,
		} as any);

		expect(openScheduleDateModal).toHaveBeenCalledWith({
			actionId: 'update-expiration-date',
			apiURL: undefined,
			dataSetId: 'allSection',
			selectedData: SELECTED_DATA,
		});
	});

	it('leaves the other actions alone', async () => {
		const {onActionDropdownItemClick} = getTransformerProps();

		await onActionDropdownItemClick({
			action: {data: {id: 'download'}},
			event: {preventDefault: jest.fn()},
			itemData: ITEM,
		} as any);

		expect(openScheduleDateModal).not.toHaveBeenCalled();
	});

	it.each(['update-expiration-date', 'update-review-date'])(
		'hides the %s action for folders and shows it for assets',
		(actionId) => {
			const {itemsActions} = getTransformerProps([
				{data: {id: actionId}},
			]);

			const [action] = itemsActions;

			expect(action.isVisible(ITEM)).toBe(true);
			expect(
				action.isVisible({
					entryClassName:
						'com.liferay.object.model.ObjectEntryFolder',
				})
			).toBe(false);
		}
	);
});
