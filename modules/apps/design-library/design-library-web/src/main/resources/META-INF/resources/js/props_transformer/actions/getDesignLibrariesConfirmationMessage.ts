/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {escapeHTML, sub} from 'frontend-js-web';

import {ConfirmationMessage, DesignLibrary} from '../../types';

export default function getDesignLibrariesConfirmationMessage(
	items: Array<Pick<DesignLibrary, 'name'>>
): ConfirmationMessage {
	const partialSuccessMessage = Liferay.Language.get(
		'x-of-x-design-libraries-were-deleted'
	);
	const warningMessage = Liferay.Language.get(
		'delete-design-library-confirmation-body-warning'
	);

	if (items.length === 1) {
		const [{name}] = items;

		return {
			bodyHTML: `
				<p>${Liferay.Language.get('delete-design-library-confirmation-body-main')}</p>
				<p>${warningMessage}</p>
			`,
			partialSuccessMessage,
			successMessage: sub(
				Liferay.Language.get('x-was-successfully-deleted'),
				`<strong>${escapeHTML(name)}</strong>`
			),
			title: sub(
				Liferay.Language.get(
					'delete-design-library-confirmation-title'
				),
				name
			),
		};
	}

	return {
		bodyHTML: `
			<p>${Liferay.Language.get('delete-design-libraries-confirmation-body-main')}</p>
			<p>${warningMessage}</p>
		`,
		partialSuccessMessage,
		successMessage: sub(
			Liferay.Language.get(
				'x-design-libraries-were-successfully-deleted'
			),
			items.length
		),
		title: sub(
			Liferay.Language.get(
				'delete-x-design-libraries-confirmation-title'
			),
			items.length
		),
	};
}
