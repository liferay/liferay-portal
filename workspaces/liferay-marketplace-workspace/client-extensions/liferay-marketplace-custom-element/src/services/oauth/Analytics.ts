/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MarketplaceSpringBootOAuth2} from './OAuth2Client';
import {ProductPurchase} from './types';

class AnalyticsOAuth2 extends MarketplaceSpringBootOAuth2 {
	async getPages(
		searchParams: URLSearchParams = new URLSearchParams()
	): Promise<AnalyticsViews> {
		return this.get<AnalyticsViews>(`/pages?${searchParams.toString()}`);
	}

	async getPlan(accountId: string) {
		return this.get<ProductPurchase>(`/plan/${accountId}`);
	}

	async getProject(projectId: string) {
		return this.get<AnalyticsProject>(`/project/${projectId}`);
	}

	async provisioning(
		orderId: number,
		data: unknown
	): Promise<{groupId: number}> {
		return this.post(`/provisioning/${orderId}`, data);
	}
}

const analyticsOAuth2 = new AnalyticsOAuth2('/analytics');

export default analyticsOAuth2;
