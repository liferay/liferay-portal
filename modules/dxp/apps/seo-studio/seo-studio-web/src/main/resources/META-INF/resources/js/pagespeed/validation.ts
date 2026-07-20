/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const PAGESPEED_VALIDATION_URL =
	'https://www.googleapis.com/pagespeedonline/v5/runPagespeed?url=invalid_url&key=';

export function validateAPIKey(apiKey: string): Promise<boolean> {
	return Liferay.Util.fetch(
		`${PAGESPEED_VALIDATION_URL}${encodeURIComponent(apiKey)}`
	)
		.then((response) => response.json())
		.then((data) => {
			const errorDetails = data.error?.details || [];

			const errorDetail = errorDetails.find(
				(detail: {reason?: string}) => detail.reason
			);

			const reason = errorDetail?.reason || '';
			const status = data.error?.status || '';

			return !(reason || status === 'PERMISSION_DENIED');
		})
		.catch(() => false);
}
