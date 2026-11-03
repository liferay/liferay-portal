/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DataMasksFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/props_transformer/DataMasksFDSPropsTransformer';

import type {DataMask} from '../../src/main/resources/META-INF/resources/js/types/DataMask';

describe('DataMasksFDSPropsTransformer', () => {
	const dataMask = (
		key: DataMask['maskType']['key'],
		actions: DataMask['actions']
	): DataMask => ({
		actions,
		detectionRegex: '\\d+',
		id: 1,
		maskType: {key, name: key},
		name: 'mask',
		replacementValue: '[X]',
	});

	const isActionVisible = (id: string, item: DataMask) => {
		const {itemsActions} = DataMasksFDSPropsTransformer({
			itemsActions: [{data: {id: 'view'}}, {data: {id: 'edit'}}],
		});

		const action = itemsActions.find(
			(itemsAction) => itemsAction.data?.id === id
		) as {isVisible: (item: DataMask) => boolean};

		return action.isVisible(item);
	};

	it('shows view and hides edit for a system mask', () => {
		const item = dataMask('system', {get: {}, update: {}});

		expect(isActionVisible('edit', item)).toBe(false);
		expect(isActionVisible('view', item)).toBe(true);
	});

	it('shows edit and hides view for a custom mask with the update action', () => {
		const item = dataMask('custom', {get: {}, update: {}});

		expect(isActionVisible('edit', item)).toBe(true);
		expect(isActionVisible('view', item)).toBe(false);
	});

	it('shows view for a custom mask without the update action', () => {
		expect(isActionVisible('view', dataMask('custom', {get: {}}))).toBe(
			true
		);
	});
});
