/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MarketplaceSpringBootOAuth2} from './OAuth2Client';

export type Availability = {
	active: boolean;
	available: number;
	fallback: boolean;
	max: number;
};

class TrialOAuth2 extends MarketplaceSpringBootOAuth2 {
	async checkDomainAvailability(projectId: string) {
		return this.get(`/domain-availability/${projectId}`);
	}

	async deleteTrial(orderId: number | string) {
		await this.delete(`/${orderId}`);
	}

	async expireTrial(orderId: number | string) {
		await this.post(`/expire/${orderId}`);
	}

	async extendTrial(extendTrialId: number | string) {
		return this.post(`/extend/${extendTrialId}`);
	}

	async getAvailability(): Promise<Availability> {
		try {
			return this.get('/availability');
		}
		catch {
			return {
				active: false,
				available: 0,
				fallback: true,
				max: 0,
			};
		}
	}

	async provisioningTrial(orderId: number): Promise<void> {
		await this.post(`/provisioning/${orderId}`);
	}
}

const trialOAuth2 = new TrialOAuth2('/trial');

export default trialOAuth2;
