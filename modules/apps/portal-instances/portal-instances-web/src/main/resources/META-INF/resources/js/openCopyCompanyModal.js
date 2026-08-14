/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

export default function openCopyCompanyModal({portletNamespace, url}) {
	openModal({
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				formId: `${portletNamespace}fm`,
				label: Liferay.Language.get('copy'),
				type: 'submit',
			},
		],
		height: '60vh',
		iframeBodyCssClass: '',
		size: 'md',
		title: sub(
			Liferay.Language.get('copy-x'),
			Liferay.Language.get('instance')
		),
		url,
	});
}
