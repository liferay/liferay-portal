/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {faroConfig} from '../../tests/osb-faro-web/main/faro.config';
import {ApiHelpers} from '../ApiHelpers';

type Channel = {
	id: string;
};

type FaroUser = {
	emailAddress: string;
	groupId: string;
	id: string;
	roleName: string;
	status: number;
};

type Project = {
	groupId: string;
	name: string;
};

type ApiToken = {
	createDate: string;
	expirationDate: string;
	lastAccessDate: string;
	token: string;
};

const _authorization = `Basic ${btoa(
	`${faroConfig.user.login}:${faroConfig.user.password}`
)}`;

export class JSONWebServicesOSBFaroApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/o/faro';
	}

	async getProjects(): Promise<Project[]> {
		return this.apiHelpers.get(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/project`,
			true,
			await this.apiHelpers.getJSONWebServicesHeaders()
		);
	}

	async createChannel(name: string, groupId: string): Promise<Channel> {
		const formdata = new FormData();

		formdata.append('name', name);

		const header = new Headers();

		header.append('Authorization', _authorization);

		return fetch(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/${groupId}/channel`,
			{
				body: formdata,
				headers: header,
				method: 'POST',
			}
		).then((response) => response.json());
	}

	async createUser(
		emailAddress: string,
		groupId: string,
		roleName: string,
		sendEmail: boolean = false
	): Promise<FaroUser[]> {
		const formdata = new FormData();

		formdata.append('emailAddresses', JSON.stringify([emailAddress]));
		formdata.append('roleName', roleName);
		formdata.append('sendEmail', String(sendEmail));

		const header = new Headers();

		header.append('Authorization', _authorization);

		return fetch(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/${groupId}/user`,
			{
				body: formdata,
				headers: header,
				method: 'POST',
			}
		).then((response) => response.json());
	}

	async createProject(name: string): Promise<Project> {
		const formdata = new FormData();

		formdata.append('emailAddressDomains', '[]');
		formdata.append('incidentReportEmailAddresses', '["test@liferay.com"]');
		formdata.append('name', name);
		formdata.append('serverLocation', 'us-west1-ac4-c1');

		const header = new Headers();

		header.append('Authorization', _authorization);

		return fetch(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/project/trial`,
			{
				body: formdata,
				headers: header,
				method: 'POST',
			}
		).then((response) => response.json());
	}

	async deleteChannel(ids: string, groupId: string): Promise<Response> {
		const formdata = new FormData();

		formdata.append('ids', ids);

		const header = new Headers();

		header.append('Authorization', _authorization);

		return fetch(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/${groupId}/channel`,
			{
				body: formdata,
				headers: header,
				method: 'DELETE',
			}
		).then((response) => response);
	}

	async deleteUser(id: string, groupId: string): Promise<Response> {
		const header = new Headers();

		header.append('Authorization', _authorization);

		return fetch(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/${groupId}/user/${id}`,
			{
				headers: header,
				method: 'DELETE',
			}
		).then((response) => response);
	}

	async deleteProject(groupId: number): Promise<Project> {
		const header = new Headers();

		header.append('Authorization', _authorization);

		return fetch(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/project/${groupId}`,
			{
				headers: header,
				method: 'DELETE',
			}
		).then((response) => {
			return response.json();
		});
	}

	async fetchApiToken(groupId: string, expiresIn: number): Promise<ApiToken> {
		return this.apiHelpers.post(
			`${faroConfig.environment.baseUrl}${this.basePath}/main/${groupId}/oauth2/tokens/new?expiresIn=${expiresIn}`,
			{
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
