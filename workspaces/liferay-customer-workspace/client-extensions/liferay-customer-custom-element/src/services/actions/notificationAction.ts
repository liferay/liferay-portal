/* eslint-disable prefer-const */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApolloClient} from '@apollo/client';

import {
	getNotificationTemplateByExternalRefenceCode,
	notificationQueueEntry,
} from '../liferay/graphql/queries';

type NotificationTemplateLanguage = {
	en_US: string;
};

type NotificationTemplateType = {
	body: NotificationTemplateLanguage;
	recipients: {
		from: string;
		fromName: NotificationTemplateLanguage;
		to: NotificationTemplateLanguage;
	}[];
	subject: NotificationTemplateLanguage;
	type: string;
};

type DataToReplaceType = {
	[key: string]: string;
};

type ExternalReferenceCodeOptions =
	| 'SETUP-ANALYTICS-CLOUD-ENVIRONMENT'
	| 'SETUP-DXP-CLOUD-ENVIRONMENT'
	| 'SETUP-LXC-ENVIRONMENT';

export default class NotificationQueueService {
	private client: ApolloClient<any>;

	constructor(client: ApolloClient<any>) {
		this.client = client;
	}

	private async getNotificationTemplateByExternalReferenceCode(
		externalReferenceCode: string
	) {
		const {
			data: {notificationTemplateByExternalReferenceCode},
		} = await this.client.query({
			query: getNotificationTemplateByExternalRefenceCode,
			variables: {
				externalReferenceCode,
			},
		});

		return notificationTemplateByExternalReferenceCode as NotificationTemplateType;
	}

	public async send(
		externalReferenceCode: ExternalReferenceCodeOptions,
		dataToReplace: DataToReplaceType
	) {
		const notificationTemplate =
			await this.getNotificationTemplateByExternalReferenceCode(
				externalReferenceCode
			);

		let {
			body: {en_US: body},
			recipients,
			subject: {en_US: subject},
			type,
		} = notificationTemplate;

		for (const key in dataToReplace) {
			const value = dataToReplace[key];

			body = body.replace(key, value);
			subject = subject.replace(key, value);
		}

		await this.client.mutate({
			context: {
				displaySuccess: false,
			},
			mutation: notificationQueueEntry,
			variables: {
				notificationQueueEntry: {
					body,
					recipients: recipients.map(({from, fromName, to}) => ({
						from,
						fromName: fromName.en_US,
						to: to.en_US,
					})),
					subject,
					type,
				},
			},
		});
	}
}
