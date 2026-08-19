/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';

import openProductRelationshipSelectorModal from '../../src/main/resources/META-INF/resources/js/openProductRelationshipSelectorModal';
import addProductRelationships, {
	getRelatedObjectEntryIds,
} from '../../src/main/resources/META-INF/resources/js/services/ProductRelationshipService';

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	openItemSelectorModal: jest.fn(),
}));

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	sub: (template: string) => template,
}));

jest.mock(
	'../../src/main/resources/META-INF/resources/js/cell_renderers/ProductRelationshipSelectorNameRenderer',
	() => ({
		__esModule: true,
		default: () => null,
	})
);

jest.mock(
	'../../src/main/resources/META-INF/resources/js/services/ProductRelationshipService',
	() => ({
		__esModule: true,
		default: jest.fn(() => Promise.resolve({ok: true})),
		getRelatedObjectEntryIds: jest.fn(() => Promise.resolve([])),
	})
);

const DATA = {
	className: 'com.liferay.object.model.ObjectDefinition#P',
	externalReferenceCode: 'erc-source',
	filters: '[{"id":"status"}]',
	name: 'Product A',
	scopeKey: '123',
	searchAPIURL:
		'/o/search/v1.0/search?filter=not%20%28entryClassPK%20in%20%2842{relatedObjectEntryIds}%29%29',
};

describe('openProductRelationshipSelectorModal', () => {
	beforeAll(() => {
		(window as any).Liferay = {
			...((window as any).Liferay || {}),
			Language: {get: (key: string) => key},
			ThemeDisplay: {getPathContext: () => ''},
		};
	});

	beforeEach(() => {
		(getRelatedObjectEntryIds as jest.Mock).mockResolvedValue([]);
		(addProductRelationships as jest.Mock).mockResolvedValue({ok: true});
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('resolves the related-ids token and opens a multi-select item selector', async () => {
		(getRelatedObjectEntryIds as jest.Mock).mockResolvedValue([111, 222]);

		await openProductRelationshipSelectorModal(DATA);

		expect(getRelatedObjectEntryIds).toHaveBeenCalledWith({
			className: 'com.liferay.object.model.ObjectDefinition#P',
			externalReferenceCode: 'erc-source',
			scopeKey: '123',
		});

		const props = (openItemSelectorModal as jest.Mock).mock.calls[0][0];

		expect(props.apiURL).toContain('/o/search/v1.0/search');
		expect(props.apiURL).toContain('%2842,111,222%29%29');
		expect(props.confirmButtonLabel).toBe('add');
		expect(props.fdsProps.filters).toEqual([{id: 'status'}]);
		expect(props.multiSelect).toBe(true);
		expect(props.size).toBe('lg');
		expect(props.title).toBe('create-relationship-with-x');
	});

	it('adds the selected products and reloads on confirm', async () => {
		const loadData = jest.fn();

		await openProductRelationshipSelectorModal(DATA, loadData);

		const {onItemsChange} = (openItemSelectorModal as jest.Mock).mock
			.calls[0][0];

		await onItemsChange([
			{embedded: {externalReferenceCode: 'erc-2'}},
			{embedded: {externalReferenceCode: 'erc-3'}},
		]);

		expect(addProductRelationships).toHaveBeenCalledWith({
			scopeKey: '123',
			sourceLinkReference: {
				className: 'com.liferay.object.model.ObjectDefinition#P',
				externalReferenceCode: 'erc-source',
			},
			targetLinkReferences: [
				{
					className: 'com.liferay.object.model.ObjectDefinition#P',
					externalReferenceCode: 'erc-2',
				},
				{
					className: 'com.liferay.object.model.ObjectDefinition#P',
					externalReferenceCode: 'erc-3',
				},
			],
			type: 'variant',
		});
		expect(loadData).toHaveBeenCalled();
	});
});
