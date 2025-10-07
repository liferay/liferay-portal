/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const INTERVAL = 3000;

export default function scheduleEmbedVideoStatusCheck({
	getEmbedVideoStatusURL,
}: {
	getEmbedVideoStatusURL: string;
}) {
	setTimeout(() => {
		fetch(getEmbedVideoStatusURL)
			.then(({status}) => {
				if (status !== 202) {
					window.location.reload();
				}
				else {
					scheduleEmbedVideoStatusCheck({getEmbedVideoStatusURL});
				}
			})
			.catch(() => {
				window.location.reload();
			});
	}, INTERVAL);
}
