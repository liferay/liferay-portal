/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../liferay';
import OAuth2Client from './OAuth2Client';

export default class AIWizardContentOAuth2 extends OAuth2Client {
	constructor() {
		super('liferay-aicontentwizard-etc-spring-boot-oaua');
	}

	async deleteSetting(id: number) {
		return this.fetch(`/settings/${id}`, {method: 'DELETE'});
	}

	async fetch(url: string, options?: RequestInit) {
		return this.oAuth2Client.fetch(url, {
			...options,
			headers: {
				'Content-Type': 'application/json',
			},
		});
	}

	async generate(data: any) {
		return this.fetch('/ai/generate', {
			body: JSON.stringify({
				...data,
				siteId: Liferay.ThemeDisplay.getScopeGroupId(),
			}),
			method: 'POST',
		}) as unknown as Promise<{output: string}>;
	}

	async getSettingsStatus(): Promise<any> {
		const response = await this.fetch('/settings/status');

		return response.json();
	}

	async getSetting(id: string): Promise<any> {
		const response = await this.fetch(`/settings/${id}`);

		return response.json();
	}

	async getSettings(): Promise<any> {
		const response = await this.fetch('/settings');

		return response.json();
	}

	async saveSettings(data: unknown) {
		const response = await this.fetch('/settings', {
			body: JSON.stringify(data),
			method: 'POST',
		});

		return response.json();
	}
}
