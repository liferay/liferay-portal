/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getDesignLibrariesConfirmationMessage from '../../../../src/main/resources/META-INF/resources/js/props_transformer/actions/getDesignLibrariesConfirmationMessage';

function buildItems(count: number) {
	return Array.from({length: count}, (_, index) => ({
		name: `Design Library ${index}`,
	}));
}

describe('getDesignLibrariesConfirmationMessage', () => {
	it('names the design library when only one is selected', () => {
		const {bodyHTML, successMessage, title} =
			getDesignLibrariesConfirmationMessage(buildItems(1));

		expect(title).toBe('delete-design-library-confirmation-title');
		expect(bodyHTML).toContain(
			'delete-design-library-confirmation-body-main'
		);
		expect(successMessage).toBe('x-was-successfully-deleted');
	});

	it('counts the design libraries when several are selected', () => {
		const {bodyHTML, successMessage, title} =
			getDesignLibrariesConfirmationMessage(buildItems(3));

		expect(title).toBe('delete-x-design-libraries-confirmation-title');
		expect(bodyHTML).toContain(
			'delete-design-libraries-confirmation-body-main'
		);
		expect(successMessage).toBe(
			'x-design-libraries-were-successfully-deleted'
		);
	});

	it('warns that the deletion cannot be undone', () => {
		expect(
			getDesignLibrariesConfirmationMessage(buildItems(2)).bodyHTML
		).toContain('delete-design-library-confirmation-body-warning');
	});

	it('reports how many design libraries were deleted on a partial failure', () => {
		expect(
			getDesignLibrariesConfirmationMessage(buildItems(2))
				.partialSuccessMessage
		).toBe('x-of-x-design-libraries-were-deleted');
	});
});
