/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import {logError} from './logError';

export function saveViewSettings({
	appURL,
	id,
	portletId,
	settings,
}: {
	appURL?: string;
	id?: string;
	portletId?: string;
	settings: any;
}) {
	if (Liferay.FeatureFlags['LPD-10683'] || !appURL) {
		return;
	}

	const url = new URL(`${appURL}/data-set/${id}/save-active-view-settings`);

	url.searchParams.append(
		'groupId',
		String(Liferay.ThemeDisplay.getScopeGroupId())
	);
	url.searchParams.append('plid', String(Liferay.ThemeDisplay.getPlid()));
	url.searchParams.append('portletId', portletId ?? '');

	return fetch(url.toString(), {
		body: JSON.stringify(settings),
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json',
		},
		method: 'POST',
	}).catch((error) => {
		logError(error);

		openToast({
			message: Liferay.Language.get('unexpected-error'),
			type: 'danger',
		});
	});
}
