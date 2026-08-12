/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getDesignAssetsConfirmationMessage from '../../../../src/main/resources/META-INF/resources/js/props_transformer/actions/getDesignAssetsConfirmationMessage';

function buildItems(count: number) {
	return Array.from({length: count}, (_, index) => ({
		embedded: {
			externalReferenceCode: `design-asset-${index}`,
			name: `Design Asset ${index}`,
		},
	}));
}

describe('getDesignAssetsConfirmationMessage', () => {
	it('names the design asset when only one is selected', () => {
		const {bodyHTML, successMessage, title} =
			getDesignAssetsConfirmationMessage(buildItems(1));

		expect(title).toBe('delete-design-asset-confirmation-title');
		expect(bodyHTML).toContain(
			'delete-design-asset-confirmation-body-main'
		);
		expect(successMessage).toBe('x-was-successfully-deleted');
	});

	it('counts the design assets when several are selected', () => {
		const {bodyHTML, successMessage, title} =
			getDesignAssetsConfirmationMessage(buildItems(3));

		expect(title).toBe('delete-x-design-assets-confirmation-title');
		expect(bodyHTML).toContain(
			'delete-design-assets-confirmation-body-main'
		);
		expect(successMessage).toBe(
			'x-design-assets-were-successfully-deleted'
		);
	});

	it('warns that the deletion cannot be undone', () => {
		expect(
			getDesignAssetsConfirmationMessage(buildItems(2)).bodyHTML
		).toContain('delete-design-asset-confirmation-body-warning');
	});

	it('reports how many design assets were deleted on a partial failure', () => {
		expect(
			getDesignAssetsConfirmationMessage(buildItems(2))
				.partialSuccessMessage
		).toBe('x-of-x-design-assets-were-deleted');
	});
});
