/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AttachmentsFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/AttachmentsFDSPropsTransformer';

jest.mock('commerce-frontend-js', () => ({
	DSRequestStatusDataRenderer: () => null,
	formatActionUrl: jest.fn(),
	removeDSRequestStatusColumn: (views) => views,
}));

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
	openToast: jest.fn(),
}));

function isRequestable(additionalProps, item) {
	const {itemsActions} = AttachmentsFDSPropsTransformer({
		additionalProps,
		itemsActions: [{data: {id: 'request-signature'}}],
	});

	return itemsActions[0].isVisible(item);
}

describe('AttachmentsFDSPropsTransformer', () => {
	const addDSRequestModalProps = {allowedFileExtensions: ['pdf']};

	it('does not offer a signature request without the modal props', () => {
		expect(isRequestable({}, {extension: 'pdf', id: 1})).toBe(false);
	});

	it('offers a signature request only for allowed file extensions', () => {
		expect(
			isRequestable({addDSRequestModalProps}, {extension: 'pdf', id: 1})
		).toBe(true);
		expect(
			isRequestable({addDSRequestModalProps}, {extension: 'zip', id: 1})
		).toBe(false);
	});

	it('offers a signature request only when an existing request can be replaced', () => {
		const additionalProps = {
			addDSRequestModalProps,
			dsRequestStatuses: {1: 'sent', 2: 'voided'},
			replaceableCommerceOrderAttachmentIds: ['2'],
		};

		expect(isRequestable(additionalProps, {extension: 'pdf', id: 1})).toBe(
			false
		);
		expect(isRequestable(additionalProps, {extension: 'pdf', id: 2})).toBe(
			true
		);
		expect(isRequestable(additionalProps, {extension: 'pdf', id: 3})).toBe(
			true
		);
	});
});
