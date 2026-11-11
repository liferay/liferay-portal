/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

type TMessage = {
	body?: string;
	categoryId?: number;
	groupId: number | string;
	messageId?: number;
	serviceContext?: any;
	subject?: string;
};

export class JSONWebServicesMBApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/mb.mbmessage';
	}

	async addMessage(message?: TMessage): Promise<TMessage> {
		const urlSearchParams = new URLSearchParams();

		message = {
			body: getRandomString(),
			categoryId: 0,
			groupId: 0,
			serviceContext: {},
			subject: getRandomString(),
			...(message || {}),
		};

		urlSearchParams.append('groupId', String(message.groupId));
		urlSearchParams.append('categoryId', String(message.categoryId));
		urlSearchParams.append('subject', message.subject);
		urlSearchParams.append('body', message.body);
		urlSearchParams.append('format', 'bbcode');
		urlSearchParams.append('inputStreamOVPs', '');
		urlSearchParams.append('anonymous', 'false');
		urlSearchParams.append('priority', '0');
		urlSearchParams.append('allowPingbacks', 'true');
		urlSearchParams.append(
			'serviceContext',
			JSON.stringify(message.serviceContext)
		);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-message`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async lockThread(threadId: string): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('threadId', threadId);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}/api/jsonws/mb.mbthread/lock-thread`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async moveMessageAttachmentToTrash(
		messageId: number | string,
		fileName: string
	): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('messageId', String(messageId));
		urlSearchParams.append('fileName', fileName);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/move-message-attachment-to-trash`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async unlockThread(threadId: string): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('threadId', threadId);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}/api/jsonws/mb.mbthread/unlock-thread`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
