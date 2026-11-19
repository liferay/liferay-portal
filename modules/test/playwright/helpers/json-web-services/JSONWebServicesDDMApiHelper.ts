/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

type DDMStructure = {
	structureId: string;
};

type DDMTemplate = {
	templateId: string;
	templateKey: string;
};

export class JSONWebServicesDDMApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/ddm.ddmstructure';
	}

	async addTemplate({
		classNameId,
		classPK,
		groupId,
		language = 'ftl',
		nameMap,
		resourceClassNameId,
		script,
		type = 'display',
	}: {
		classNameId: number | string;
		classPK: number | string;
		groupId: number | string;
		language?: string;
		nameMap: {[key: string]: string};
		resourceClassNameId: number | string;
		script: string;
		type?: string;
	}): Promise<DDMTemplate> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('classNameId', String(classNameId));
		urlSearchParams.append('classPK', String(classPK));
		urlSearchParams.append('descriptionMap', JSON.stringify({}));
		urlSearchParams.append('externalReferenceCode', getRandomString());
		urlSearchParams.append('groupId', String(groupId));
		urlSearchParams.append('language', language);
		urlSearchParams.append('mode', '');
		urlSearchParams.append('nameMap', JSON.stringify(nameMap));
		urlSearchParams.append(
			'resourceClassNameId',
			String(resourceClassNameId)
		);
		urlSearchParams.append('script', script);
		urlSearchParams.append('serviceContext', JSON.stringify({}));
		urlSearchParams.append('type', type);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}/api/jsonws/ddm.ddmtemplate/add-template`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async fetchStructure(
		groupId: string,
		classNameId: string,
		structureKey: string
	): Promise<DDMStructure> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('classNameId', classNameId);
		urlSearchParams.append('structureKey', structureKey);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/fetch-structure`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
