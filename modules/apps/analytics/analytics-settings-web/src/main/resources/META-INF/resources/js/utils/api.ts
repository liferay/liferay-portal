/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TTableRequestParams} from '../components/table/types';
import {serializeTableRequestParams} from '../components/table/utils';
import request from './request';

export function createProperty(name: string) {
	return request('/channels', {
		body: JSON.stringify({
			name,
		}),
		method: 'POST',
	});
}

export function deleteConnection() {
	return request('/data-sources', {method: 'DELETE'});
}

export function sync() {
	return request('/configuration/wizard-mode', {
		method: 'POST',
	});
}

export function fetchAccountGroups(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/contacts/account-groups?${queryString}`, {
		method: 'GET',
	});
}

export function fetchChannels(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/commerce-channels?${queryString}`, {
		method: 'GET',
	});
}

export function fetchConnection(token: string) {
	return request(
		'/data-sources',
		{
			body: JSON.stringify({
				token,
			}),
			method: 'POST',
		},
		Liferay.Language.get(
			'token-is-not-valid.-please-insert-a-valid-analytics-cloud-token'
		)
	);
}

export function fetchContactsOrganization(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/contacts/organizations?${queryString}`, {
		method: 'GET',
	});
}

export function fetchContactsUsersGroup(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/contacts/user-groups?${queryString}`, {
		method: 'GET',
	});
}

export function fetchAttributesConfiguration() {
	return request('/contacts/configuration', {
		method: 'GET',
	});
}

export function fetchProperties(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/channels?${queryString}`, {method: 'GET'});
}

export function fetchSites(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/sites?${queryString}`, {
		method: 'GET',
	});
}

interface IProperty {
	channelId: string;
	commerceChannelIds?: number[];
	commerceSyncEnabled: boolean;
	dataSourceId?: string;
	siteIds?: number[];
}

export function updateProperty({
	channelId,
	commerceChannelIds = [],
	commerceSyncEnabled,
	dataSourceId,
	siteIds = [],
}: IProperty) {
	return request('/channels', {
		body: JSON.stringify({
			channelId,
			commerceSyncEnabled,
			dataSources: [
				{
					commerceChannelIds,
					dataSourceId,
					siteIds,
				},
			],
		}),
		method: 'PATCH',
	});
}

export function updatecommerceSyncEnabled({
	channelId,
	commerceSyncEnabled,
}: {
	channelId: string;
	commerceSyncEnabled: boolean;
}) {
	return request('/channels', {
		body: JSON.stringify({
			channelId,
			commerceSyncEnabled,
		}),
		method: 'PATCH',
	});
}

export function updateAttributesConfiguration({
	syncAllAccounts,
	syncAllContacts,
	syncedAccountGroupIds,
	syncedOrganizationIds,
	syncedUserGroupIds,
}: {
	syncAllAccounts: boolean;
	syncAllContacts: boolean;
	syncedAccountGroupIds?: string[];
	syncedOrganizationIds?: string[];
	syncedUserGroupIds?: string[];
}) {
	return request('/contacts/configuration', {
		body: JSON.stringify({
			syncAllAccounts,
			syncAllContacts,
			syncedAccountGroupIds,
			syncedOrganizationIds,
			syncedUserGroupIds,
		}),
		method: 'PUT',
	});
}

export function fetchSelectedFields() {
	return request('/fields', {method: 'GET'});
}

export function fetchAccountsFields(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(
		`/fields/accounts?${queryString.replace('keywords', 'keyword')}`,
		{method: 'GET'}
	);
}

export function fetchPeopleFields(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(
		`/fields/people?${queryString.replace('keywords', 'keyword')}`,
		{method: 'GET'}
	);
}

type TField = {
	example: string;
	name: string;
	required: boolean;
	selected: boolean;
	source: string;
	type: string;
};

export function updateAccountsFields(fields: TField[]) {
	return request('/fields/accounts', {
		body: JSON.stringify(fields),
		method: 'PATCH',
	});
}

export function updatePeopleFields(fields: TField[]) {
	return request('/fields/people', {
		body: JSON.stringify(fields),
		method: 'PATCH',
	});
}

export enum JobId {
	ContentRecommenderMostPopularItems = 'contentRecommenderMostPopularItems',
	ContentRecommenderUserPersonalization = 'contentRecommenderUserPersonalization',
}

export enum JobStatus {
	Enabled = 'Enabled',
	Disabled = 'Disabled',
	Configuring = 'Configuring',
}

export type TRecommendationConfiguration = {
	[key in JobId]: {
		enabled: boolean;
	};
};

export type TExtendedRecommendationConfiguration = {
	[key in JobId]: {
		enabled: boolean;
		status: JobStatus;
	};
};

export function fetchRecommendationConfiguration() {
	return request('/recommendation/configuration', {
		method: 'GET',
	});
}

export function updateRecommendationConfiguration(
	recommendationConfiguration: TRecommendationConfiguration
) {
	return request(
		'/recommendation/configuration',
		{
			body: JSON.stringify(recommendationConfiguration),
			method: 'PUT',
		},
		Liferay.Language.get(
			'configuration-couldnt-be-completed.-please-try-again-later'
		)
	);
}
