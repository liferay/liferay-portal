/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

export default function ({title, url, warningButtonId}) {
	const warningButton = document.getElementById(warningButtonId);

	if (!warningButton) {
		return;
	}

	const handleClick = (event) => {
		event.preventDefault();

		openModal({
			buttons: [
				{
					autoFocus: true,
					displayType: 'secondary',
					label: Liferay.Language.get('close'),
					type: 'cancel',
				},
			],
			size: 'full-screen',
			title,
			url,
		});
	};

	warningButton.addEventListener('click', handleClick);

	return {
		dispose() {
			warningButton.removeEventListener('click', handleClick);
		},
	};
}
