/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ViewVersionHistoryFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/ViewVersionHistoryFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

const bulkAction = (id: string) => ({data: {id}}) as any;

const transform = (bulkActions: Array<any>) =>
	ViewVersionHistoryFDSPropsTransformer({
		additionalProps: {},
		bulkActions,
	}) as any;

const isDisabledForCompare = () => {
	const [{isDisabled}] = transform([bulkAction('compare')]).bulkActions;

	return isDisabled;
};

const versions = (count: number) => new Array(count).fill({});

describe('ViewVersionHistoryFDSPropsTransformer', () => {
	describe('compare bulk action', () => {
		it('is enabled with two versions selected', () => {
			expect(isDisabledForCompare()({selectedItems: versions(2)})).toBe(
				false
			);
		});

		it('is disabled with fewer than two versions selected', () => {
			const isDisabled = isDisabledForCompare();

			expect(isDisabled({})).toBe(true);
			expect(isDisabled({selectedItems: versions(1)})).toBe(true);
		});

		it('is disabled with more than two versions selected', () => {
			expect(isDisabledForCompare()({selectedItems: versions(3)})).toBe(
				true
			);
		});

		it('is disabled when the whole data set is selected', () => {
			expect(
				isDisabledForCompare()({
					allItemsSelectedActive: true,
					selectedItems: versions(2),
				})
			).toBe(true);
		});
	});

	it('leaves the other bulk actions untouched', () => {
		const [{isDisabled}] = transform([bulkAction('delete')]).bulkActions;

		expect(isDisabled).toBeUndefined();
	});
});
