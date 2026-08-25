/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import transformFDSBulkActions from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/transformFDSBulkActions';

jest.mock('@liferay/frontend-data-set-web', () => ({}));

const action = (id: string) => ({data: {id}}) as any;

const isVisibleFor = (id: string) => {
	const [{isVisible}] = transformFDSBulkActions([action(id)]);

	return isVisible!;
};

const itemWith = (actions: Record<string, unknown>) => ({actions});

const fileItemWithLink = () => ({
	embedded: {file: {link: {href: 'http://example.com/file.png'}}},
});

const fileItemWithoutLink = () => ({embedded: {file: {}}});

const folderItemWith = (actions: Record<string, unknown>) => ({
	actions,
	entryClassName: 'com.liferay.object.model.ObjectEntryFolder',
});

describe('transformFDSBulkActions', () => {
	it('passes through actions whose id is not in the permission map', () => {
		const unknown = action('unknown-action');

		const [transformed] = transformFDSBulkActions([unknown]);

		expect(transformed).toBe(unknown);
		expect(transformed.isVisible).toBeUndefined();
	});

	it('attaches an isVisible callback to actions in the permission map', () => {
		const [transformed] = transformFDSBulkActions([action('delete')]);

		expect(typeof transformed.isVisible).toBe('function');
	});

	describe('isVisible (allItemsSelectedActive=false)', () => {
		it('shows the action when every selected item has the permission', () => {
			expect(
				isVisibleFor('delete')({
					selectedItems: [
						itemWith({delete: {}}),
						itemWith({delete: {}}),
					],
				})
			).toBe(true);
		});

		it('hides the action when any selected item lacks the permission', () => {
			expect(
				isVisibleFor('delete')({
					selectedItems: [itemWith({delete: {}}), itemWith({})],
				})
			).toBe(false);
		});

		it('hides the action when no items are selected', () => {
			const isVisible = isVisibleFor('delete');

			expect(isVisible({})).toBe(false);
			expect(isVisible({selectedItems: undefined})).toBe(false);
		});

		it('checks the mapped permission key, not the action id', () => {
			const isVisible = isVisibleFor('move-to');

			expect(isVisible({selectedItems: [itemWith({update: {}})]})).toBe(
				true
			);
			expect(
				isVisible({selectedItems: [itemWith({['move-to']: {}})]})
			).toBe(false);
		});

		it.each(['update-expiration-date', 'update-review-date'])(
			'hides the %s action when the selection contains a folder',
			(id) => {
				const isVisible = isVisibleFor(id);

				expect(
					isVisible({selectedItems: [itemWith({update: {}})]})
				).toBe(true);
				expect(
					isVisible({
						selectedItems: [
							itemWith({update: {}}),
							folderItemWith({update: {}}),
						],
					})
				).toBe(false);
			}
		);

		it.each(['update-expiration-date', 'update-review-date'])(
			'hides the %s action when any selected item cannot be updated',
			(id) => {
				expect(
					isVisibleFor(id)({
						selectedItems: [itemWith({update: {}}), itemWith({})],
					})
				).toBe(false);
			}
		);

		it('checks file link presence for the download action', () => {
			const isVisible = isVisibleFor('download');

			expect(isVisible({selectedItems: [fileItemWithLink()]})).toBe(true);
			expect(
				isVisible({
					selectedItems: [fileItemWithLink(), fileItemWithoutLink()],
				})
			).toBe(false);
		});
	});

	describe('isVisible (allItemsSelectedActive=true)', () => {
		it('shows actions other than download and duplicate even when no items are passed', () => {
			expect(isVisibleFor('delete')({allItemsSelectedActive: true})).toBe(
				true
			);
		});

		it('hides download to prevent downloading the entire data set', () => {
			expect(
				isVisibleFor('download')({allItemsSelectedActive: true})
			).toBe(false);
		});

		it('hides duplicate to prevent duplicating the entire data set', () => {
			expect(
				isVisibleFor('duplicate')({allItemsSelectedActive: true})
			).toBe(false);
		});
	});
});
