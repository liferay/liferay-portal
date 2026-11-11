/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers} from './ApiHelpers';

type EmailNotificationRecipients = {
	[key in 'roleName']?: string;
};

type TNotificationTemplate = {
	body?: {
		[key: string]: string;
	};
	editorType: string;
	id?: number;
	name: string;
	recipientType: string;
	recipients?: TRecipient[] | TTermRecipient[];
	subject: {
		[key: string]: string;
	};
	type: string;
};

type TRecipient = {
	from: string;
	fromName: {
		[key: string]: string;
	};
	to: LocalizedValue<string> | EmailNotificationRecipients[];
	toType: string;
};

type TTermRecipient = {
	term: string;
};

export class NotificationApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'notification/v1.0';
	}

	async deleteNotificationTemplate(notificationTemplateId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/notification-templates/${notificationTemplateId}`
		);
	}

	async deleteNotificationQueueEntry(notificationQueueEntryId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/notification-queue-entries/${notificationQueueEntryId}`
		);
	}

	async getNotificationQueueEntriesPage(search: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/notification-queue-entries?search=${search}`
		);
	}

	async getNotificationTemplateByName(
		name: string
	): Promise<TNotificationTemplate> {
		let notificationTemplate: TNotificationTemplate;

		await expect
			.poll(async () => {
				const {items} = await this.apiHelpers.get(
					`${this.apiHelpers.baseUrl}${this.basePath}/notification-templates?search=${encodeURIComponent(name)}`
				);

				notificationTemplate = items.find(
					(item: TNotificationTemplate) => item.name === name
				);

				return notificationTemplate;
			})
			.toBeDefined();

		return notificationTemplate;
	}

	async postNotificationTemplate(
		notificationTemplate?: TNotificationTemplate
	): Promise<TNotificationTemplate> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/notification-templates`,
			{data: notificationTemplate}
		);
	}

	async postRandomNotificationTemplate(
		name: string = 'test ' + getRandomInt(),
		fromEmail: string = 'do-not-replay@liferay.com',
		toEmail: string = 'to' + getRandomInt() + '@liferay.com'
	): Promise<TNotificationTemplate> {
		const requestBody = {
			editorType: 'richText',
			name,
			recipientType: 'email',
			recipients: [
				{
					from: fromEmail,
					fromName: {
						en_US: fromEmail,
					},
					to: {
						en_US: toEmail,
					},
					toType: 'email',
				},
			],
			subject: {
				en_US: 'subject' + getRandomInt(),
			},
			type: 'email',
		} as TNotificationTemplate;

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/notification-templates`,
			{data: requestBody}
		);
	}
}
