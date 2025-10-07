/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MarketplaceSpringBootOAuth2} from './OAuth2Client';
import {SubscriptionsType} from './types';

class KoroneikiOAuth2 extends MarketplaceSpringBootOAuth2 {
	async getSubscriptions(orderId: number) {
		return this.get<SubscriptionsType[]>(`/subscriptions/${orderId}`);
	}

	async syncProduct(productId: number | string) {
		await this.post(`/product/${productId}`);
	}
}

const koroneikiOAuth2 = new KoroneikiOAuth2('/koroneiki');

export default koroneikiOAuth2;
