/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import {ApiHelpers} from '../ApiHelpers';

type CommercePaymentMethodGroupRel = {
	active: boolean;
	commercePaymentMethodGroupRelId: number;
	groupId: number;
	paymentIntegrationKey: string;
};

export class JSONWebServicesCommercePaymentMethodGroupRelApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/commerce.commercepaymentmethodgrouprel';
	}

	async addCommercePaymentMethodGroupRel(
		groupId: number,
		name: string,
		paymentIntegrationKey: string
	): Promise<CommercePaymentMethodGroupRel> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', String(groupId));
		urlSearchParams.append('nameMap', JSON.stringify({en_US: name}));
		urlSearchParams.append('-descriptionMap', '');
		urlSearchParams.append('active', 'true');
		urlSearchParams.append('-imageFile', '');
		urlSearchParams.append('paymentIntegrationKey', paymentIntegrationKey);
		urlSearchParams.append('priority', '1');
		urlSearchParams.append('-typeSettings', '');

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-commerce-payment-method-group-rel`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
