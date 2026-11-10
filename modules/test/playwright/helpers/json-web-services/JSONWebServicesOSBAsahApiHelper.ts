/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {asahConfig} from '../../tests/osb-faro-web/main/asah.config';
import {Nanites} from '../../tests/osb-faro-web/main/utils/nanites';
import {ApiHelpers} from '../ApiHelpers';

type BlogDaily = {
	assetId: string;
	assetTitle: string;
	canonicalUrl: string;
	channelId: string;
	clicks: number;
	comments: number;
	eventDate: string;
	pageTitle?: string;
	ratings: number;
	ratingsScore: number;
	readTime: number;
	sessions: number;
	userId: string | null;
	views: number;
};

type DocumentLibraryDaily = {
	assetId: string;
	assetTitle: string;
	canonicalUrl: string;
	channelId: string;
	downloads: number;
	eventDate: string;
	pageTitle?: string;
	previews: number;
	ratings: number;
	ratingsScore: number;
	userId: string;
};

type Event = {
	applicationId: string;
	assetId?: string;
	assetTitle?: string;
	browserName?: string;
	canonicalUrl: string;
	channelId: string;
	context?: string;
	dataSourceId?: number | string;
	deviceType?: string;
	emailAddressHashed?: string;
	eventDate: string;
	eventId: string;
	eventProperties?: string;
	platformName?: string;
	properties?: Property[];
	referrer?: string;
	sessionId?: string;
	title: string;
	url?: string;
	userId: string;
};

type EventAttributeDefinition = {
	dataType: string;
	displayName: string;
	name: string;
	type: string;
};

type EventDefinition = {
	applicationId: string;
	description?: string;
	displayName: string;
	eventAttributeDefinitions?: EventAttributeDefinition[];
	eventDate?: string;
	name: string;
	type: string;
	url?: string;
};

type Identity = {
	createDate: string;
	id: string;
	individualId?: string;
};

type JournalDaily = {
	assetId: string;
	assetTitle: string;
	canonicalUrl: string;
	channelId: string;
	eventDate: string;
	pageTitle?: string;
	userId: string;
	views: number;
};

type Field = {
	dataSourceId: number | string;
	name: string;
	value: string;
};

type IdentityActivitySummary = {
	activitiesCount: number;
	channelId: string;
	country?: string;
	dataSourceId: number | string;
	eventId: string;
	firstActivityDate: string;
	identityId: string;
	individualId: string;
	lastActivityDate: string;
};

type Individual = {
	emailAddress: string;
	fields: Field[];
	firstName: string;
	id: string;
	lastName: string;
};

type PageDaily = {
	canonicalUrl: string;
	channelId: string;
	eventDate: string;
	title: string;
	userId: string;
	views: number;
};

type Property = {
	name: string;
	value: string;
};

type Session = {
	channelId: string;
	events?: Event[];
	id: string;
	sessionEnd: string;
	sessionStart: string;
	userId: string;
};

export class JSONWebServicesOSBAsahApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/functional';
	}

	getHeaders() {
		return {
			'Content-Type': 'application/json',
			'OSB-Asah-Project-ID': asahConfig.environment.projectId,
		};
	}

	async runNanites(nanites: Nanites[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.batchCuratordUrl}/nanites/run`,
			{
				data: nanites,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createEvents(events: Event[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-events`,
			{
				data: events.map((event) => ({
					context: '{}',
					deviceType: 'Desktop',
					...event,
				})),
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createEventDefinition(
		eventDefinition: EventDefinition[]
	): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/event-definitions`,
			{
				data: eventDefinition,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async deleteEventDefinitions(names: string[]): Promise<any> {
		return this.apiHelpers.delete(
			`${asahConfig.environment.backendUrl}${this.basePath}/event-definitions`,
			{
				data: names,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createIdentities(identities: Identity[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-identities`,
			{
				data: identities,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createIdentityActivitiesSummary(
		identityActivitiesSummary: IdentityActivitySummary[]
	): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-identity-activities-summary`,
			{
				data: identityActivitiesSummary,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createIndividuals(individuals: Individual[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-individuals`,
			{
				data: individuals,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createPagesDaily(pagesDaily: PageDaily[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-pages-daily`,
			{
				data: pagesDaily,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createBlogsDaily(blogsDaily: BlogDaily[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-blogs-daily`,
			{
				data: blogsDaily,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createDocumentLibrariesDaily(
		documentLibrariesDaily: DocumentLibraryDaily[]
	): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-document-libraries-daily`,
			{
				data: documentLibrariesDaily,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createJournalsDaily(journalsdaily: JournalDaily[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-journals-daily`,
			{
				data: journalsdaily,
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async createSessions(sessions: Session[]): Promise<any> {
		return this.apiHelpers.post(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-sessions`,
			{
				data: sessions.map(({events = [], ...session}) => ({
					...session,
					bqevents: events.map((event) => ({
						context: JSON.stringify({
							devicePixelRatio: '1',
							screenHeight: '1080',
							screenWidth: '1920',
							userAgent: 'Mozilla/5.0',
						}),
						createDate: event.eventDate,
						deviceType: 'Desktop',
						...event,
					})),
				})),
				failOnStatusCode: true,
				headers: this.getHeaders(),
			}
		);
	}

	async closeSessions(): Promise<any> {
		return this.apiHelpers.delete(
			`${asahConfig.environment.backendUrl}${this.basePath}/bq-sessions/close`,
			{headers: this.getHeaders()}
		);
	}
}
