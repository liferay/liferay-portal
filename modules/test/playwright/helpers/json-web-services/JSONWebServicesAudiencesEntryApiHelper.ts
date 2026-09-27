/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import {ApiHelpers, DataApiHelpers} from '../ApiHelpers';

type AudiencesEntry = {
	audiencesEntryId: number;
	externalReferenceCode: string;
	name: string;
};

type AudiencesEntryRule = {
	attribute: string;
	operator: string;
	value: boolean | number | string;
};

export class JSONWebServicesAudiencesEntryApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers | DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/audiences.audiencesentry';
	}

	async addAudiencesEntry({
		groupERCs,
		name,
		rules = [],
	}: {
		groupERCs?: string[];
		name: string;
		rules?: AudiencesEntryRule[];
	}): Promise<AudiencesEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', '');

		if (groupERCs) {
			urlSearchParams.append('groupERCs', JSON.stringify(groupERCs));
		}
		else {
			urlSearchParams.append('-groupERCs', '');
		}

		urlSearchParams.append(
			'json',
			JSON.stringify({conjunction: 'AND', rules})
		);
		urlSearchParams.append('name', name);

		const audiencesEntry: AudiencesEntry = await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-audiences-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: audiencesEntry.audiencesEntryId,
				type: 'audiencesEntry',
			});
		}

		return audiencesEntry;
	}

	async deleteAudiencesEntry(audiencesEntryId: number) {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('audiencesEntryId', String(audiencesEntryId));

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/delete-audiences-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async updateAudiencesEntry({
		audiencesEntry,
		groupERCs,
		rules = [],
	}: {
		audiencesEntry: AudiencesEntry;
		groupERCs?: string[];
		rules?: AudiencesEntryRule[];
	}): Promise<AudiencesEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append(
			'audiencesEntryId',
			String(audiencesEntry.audiencesEntryId)
		);
		urlSearchParams.append(
			'externalReferenceCode',
			audiencesEntry.externalReferenceCode
		);

		if (groupERCs) {
			urlSearchParams.append('groupERCs', JSON.stringify(groupERCs));
		}
		else {
			urlSearchParams.append('-groupERCs', '');
		}

		urlSearchParams.append(
			'json',
			JSON.stringify({conjunction: 'AND', rules})
		);
		urlSearchParams.append('name', audiencesEntry.name);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-audiences-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
