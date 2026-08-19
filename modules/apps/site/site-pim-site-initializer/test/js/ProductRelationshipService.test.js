/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import addProductRelationships, {
	getRelatedObjectEntryIds,
} from '../../src/main/resources/META-INF/resources/js/services/ProductRelationshipService';

const mockFetch = jest.fn(() => Promise.resolve({ok: true}));

jest.mock('frontend-js-web', () => ({
	fetch: (...args) => mockFetch(...args),
}));

describe('ProductRelationshipService', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('POSTs the source and target references to the scoped links endpoint', () => {
		addProductRelationships({
			scopeKey: '37868',
			sourceLinkReference: {
				className: 'com.liferay.object.model.ObjectDefinition#P4R4',
				externalReferenceCode: 'source-erc',
			},
			targetLinkReferences: [
				{
					className: 'com.liferay.object.model.ObjectDefinition#P4R4',
					externalReferenceCode: 'target-erc',
				},
			],
			type: 'variant',
		});

		expect(mockFetch).toHaveBeenCalledWith(
			'/o/headless-pim/v1.0/scopes/37868/links',
			expect.objectContaining({
				body: JSON.stringify({
					sourceLinkReference: {
						className:
							'com.liferay.object.model.ObjectDefinition#P4R4',
						externalReferenceCode: 'source-erc',
					},
					targetLinkReferences: [
						{
							className:
								'com.liferay.object.model.ObjectDefinition#P4R4',
							externalReferenceCode: 'target-erc',
						},
					],
					type: 'variant',
				}),
				method: 'POST',
			})
		);
	});

	it('GETs the current related object entry ids for the source product', async () => {
		mockFetch.mockResolvedValueOnce({
			json: () =>
				Promise.resolve({
					items: [{id: 111}, {id: 222}],
				}),
		});

		const relatedObjectEntryIds = await getRelatedObjectEntryIds({
			className: 'com.liferay.object.model.ObjectDefinition#P4R4',
			externalReferenceCode: 'source-erc',
			scopeKey: '37868',
		});

		expect(mockFetch).toHaveBeenCalledWith(
			'/o/headless-pim/v1.0/scopes/37868/links?className=com.liferay.object.model.ObjectDefinition%23P4R4&externalReferenceCode=source-erc&pageSize=200'
		);
		expect(relatedObjectEntryIds).toEqual([111, 222]);
	});
});
