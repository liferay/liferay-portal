/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {Page, Request, Response} from '@playwright/test';

export function trackPendingApiRequests(page: Page) {
	const pendingRequests = new Set<Request>();

	const add = (request: Request) => {
		const resourceType = request.resourceType();

		if (resourceType === 'fetch' || resourceType === 'xhr') {
			pendingRequests.add(request);
		}
	};

	const remove = (request: Request) => {
		pendingRequests.delete(request);
	};

	const removeResponse = (response: Response) => {
		pendingRequests.delete(response.request());
	};

	page.on('request', add);
	page.on('requestfailed', remove);
	page.on('response', removeResponse);

	return async () => {
		const startTime = Date.now();

		while (pendingRequests.size && Date.now() - startTime < 10000) {
			await new Promise((resolve) => setTimeout(resolve, 100));
		}

		page.off('request', add);
		page.off('requestfailed', remove);
		page.off('response', removeResponse);
	};
}
