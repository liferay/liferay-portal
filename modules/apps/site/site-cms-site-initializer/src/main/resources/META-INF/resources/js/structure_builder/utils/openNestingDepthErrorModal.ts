/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {sub} from 'frontend-js-web';

import getNestingDepthError, {MAX_DEPTH} from './getNestingDepthError';

export default async function openNestingDepthErrorModal({
	error,
	operation,
}: {
	error: NonNullable<ReturnType<typeof getNestingDepthError>>;
	operation: 'publish' | 'save';
}) {
	let text = Liferay.Language.get(
		'this-content-structure-exceeds-the-maximum-nesting-depth-of-x-levels'
	);

	if (error === 'exceeded-in-references') {
		text =
			operation === 'publish'
				? Liferay.Language.get(
						'this-content-structure-cannot-be-published-because-it-is-referenced-in-other-content-structures-that-would-exceed-the-maximum-nesting-depth-of-x-levels-if-these-changes-are-propagated'
					)
				: Liferay.Language.get(
						'this-content-structure-cannot-be-saved-because-it-is-referenced-in-other-content-structures-that-would-exceed-the-maximum-nesting-depth-of-x-levels'
					);
	}

	await openConfirmModal({
		buttonLabel: Liferay.Language.get('done'),
		center: true,
		status: 'danger',
		text: sub(text, MAX_DEPTH),
		title:
			operation === 'publish'
				? Liferay.Language.get('unable-to-publish')
				: Liferay.Language.get('unable-to-save'),
	});
}
