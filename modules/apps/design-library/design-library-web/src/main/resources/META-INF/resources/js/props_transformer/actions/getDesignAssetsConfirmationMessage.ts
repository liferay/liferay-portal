/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {escapeHTML, sub} from 'frontend-js-web';

import {ConfirmationMessage, DesignAsset} from '../../types';

export default function getDesignAssetsConfirmationMessage(
	items: Array<Pick<DesignAsset, 'embedded'>>
): ConfirmationMessage {
	const partialSuccessMessage = Liferay.Language.get(
		'x-of-x-design-assets-were-deleted'
	);
	const warningMessage = Liferay.Language.get(
		'delete-design-asset-confirmation-body-warning'
	);

	if (items.length === 1) {
		const [{embedded}] = items;

		return {
			bodyHTML: `
				<p>${Liferay.Language.get('delete-design-asset-confirmation-body-main')}</p>
				<p>${warningMessage}</p>
			`,
			partialSuccessMessage,
			successMessage: sub(
				Liferay.Language.get('x-was-successfully-deleted'),
				`<strong>${escapeHTML(embedded.name)}</strong>`
			),
			title: sub(
				Liferay.Language.get('delete-design-asset-confirmation-title'),
				embedded.name
			),
		};
	}

	return {
		bodyHTML: `
			<p>${Liferay.Language.get('delete-design-assets-confirmation-body-main')}</p>
			<p>${warningMessage}</p>
		`,
		partialSuccessMessage,
		successMessage: sub(
			Liferay.Language.get('x-design-assets-were-successfully-deleted'),
			items.length
		),
		title: sub(
			Liferay.Language.get('delete-x-design-assets-confirmation-title'),
			items.length
		),
	};
}
