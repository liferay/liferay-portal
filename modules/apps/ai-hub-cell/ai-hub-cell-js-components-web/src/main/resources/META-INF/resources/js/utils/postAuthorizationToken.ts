/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export interface AuthorizationToken {
	accessToken: string;
	serviceURL: string;
	userToken: string;
}

export default async function postAuthorizationToken(): Promise<
	AuthorizationToken | undefined
> {
	try {
		const response = await fetch(
			'/o/ai-hub-cell/v1.0/authorization-tokens',
			{
				method: 'POST',
			}
		);

		if (!response.ok) {
			throw new Error(
				`Unable to generate authorization token: ${response.statusText}`
			);
		}

		const data = await response.json();

		if (!data?.accessToken) {
			throw new Error('Unable to generate authorization token.');
		}

		if (!data?.userToken) {
			throw new Error('Unable to generate user token.');
		}

		if (!data?.serviceURL) {
			throw new Error('Unable to find service URL.');
		}

		return data;
	}
	catch (error) {
		console.warn((error as Error).message);
	}
}
