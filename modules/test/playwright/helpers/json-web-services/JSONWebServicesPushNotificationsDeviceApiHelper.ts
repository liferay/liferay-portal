/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {request} from '@playwright/test';

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from '../ApiHelpers';

type TPushNotificationsDevice = {

	// A device belongs to whoever registers it. Passing credentials registers
	// it for that user instead of the signed in one

	authorization?: string;
	platform?: string;
	pushNotificationsDeviceId?: string;
	token?: string;
	userId?: string;
};

export class JSONWebServicesPushNotificationsDeviceApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/pushnotifications.pushnotificationsdevice';
	}

	async addPushNotificationsDevice(
		pushNotificationsDevice?: TPushNotificationsDevice
	): Promise<TPushNotificationsDevice> {
		const {authorization, platform, token} = {
			platform: 'android',
			token: getRandomString(),
			...(pushNotificationsDevice || {}),
		};

		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('platform', platform);
		urlSearchParams.append('token', token);

		const url = `${liferayConfig.environment.baseUrl}${this.basePath}/add-push-notifications-device`;

		let device: TPushNotificationsDevice;

		if (authorization) {

			// The signed in session outranks the credentials, so the call has
			// to be made from a context that carries no cookies

			const context = await request.newContext({
				extraHTTPHeaders: {
					'Authorization': authorization,
					'Content-Type': 'application/x-www-form-urlencoded',
				},
			});

			const response = await context.post(url, {
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
			});

			device = await response.json();

			await context.dispose();
		}
		else {
			device = await this.apiHelpers.post(url, {
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			});
		}

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: device.pushNotificationsDeviceId,
				type: 'pushNotificationsDevice',
			});
		}

		return device;
	}

	async deletePushNotificationsDevice(pushNotificationsDeviceId: string) {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append(
			'pushNotificationsDeviceId',
			pushNotificationsDeviceId
		);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/delete-push-notifications-device`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
