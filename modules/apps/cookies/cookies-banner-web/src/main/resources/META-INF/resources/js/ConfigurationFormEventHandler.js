/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {delegate} from 'frontend-js-web';

export default function ({namespace}) {
	const delegateHandler = delegate(
		document.body,
		'change',
		'input[type="checkbox"]',
		(event) => {
			const explicitConsentMode = document.querySelector(
				`input[type='checkbox'][name='${namespace}explicitConsentMode']`
			);

			if (event.delegateTarget.id === `${namespace}enabled`) {
				if (event.delegateTarget.checked) {
					explicitConsentMode.removeAttribute('disabled');
				}
				else {
					explicitConsentMode.checked = true;

					explicitConsentMode.setAttribute('disabled', '');
				}
			}
		}
	);

	return {
		dispose() {
			delegateHandler.dispose();
		},
	};
}
