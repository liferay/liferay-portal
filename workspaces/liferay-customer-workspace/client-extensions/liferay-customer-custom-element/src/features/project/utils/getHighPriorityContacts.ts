/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApolloClient, FetchResult} from '@apollo/client';
import {
	createAccountUserRoles,
	deleteAccountUserRoles,
	getAccountAccountRolesByExternalReferenceCode,
} from '~/services/liferay/graphql/queries';
import {
	addContactRoleNameByEmailByProject,
	deleteContactRoleNameByEmailByProject,
} from '~/services/liferay/rest/raysource/TeamMembers';
import i18n from '~/utils/I18n';
import {IAccountRole, IProject} from '~/utils/types';

interface IContact {
	category: {role: string};
	email: string;
	filter?: string[];
	filterId?: string;
	label: string;
}

const addContactRoleLiferay = async (
	item: IContact,
	project: IProject,
	client: ApolloClient<object>
): Promise<FetchResult> => {
	return client.mutate({
		context: {
			displaySuccess: false,
		},
		mutation: createAccountUserRoles,
		variables: {
			accountRoleId: item.filterId,
			emailAddress: item.email,
			externalReferenceCode: project.accountKey,
		},
	});
};

const addContactRoleRaysource = (
	item: IContact,
	oAuthToken: string,
	project: IProject,
	provisioningServerAPI: string
): Promise<Response> => {
	return addContactRoleNameByEmailByProject(
		project.accountKey as string,
		encodeURI(item.email),
		item.label,
		item.label,
		oAuthToken,
		provisioningServerAPI,
		item.category.role
	);
};

const getAccountRolesId = async (
	project: IProject,
	client: ApolloClient<object>
): Promise<IAccountRole[]> => {
	const result = await client.query({
		context: {
			displaySuccess: false,
		},
		query: getAccountAccountRolesByExternalReferenceCode,
		variables: {
			externalReferenceCode: project.accountKey,
		},
	});

	return result.data.accountAccountRolesByExternalReferenceCode.items;
};

const getContactRoleByFilter = (filter: string): string | undefined => {
	if (filter.includes('privacy')) {
		return 'Data Breach Contact';
	}

	if (filter.includes('security')) {
		return 'Security Incident Contact';
	}

	if (filter.includes('critical')) {
		return 'Critical Incident Contact';
	}

	return undefined;
};

const HIGH_PRIORITY_CONTACT_CATEGORIES = {
	criticalIncident: i18n.translate('critical-incident'),
	privacyBreach: i18n.translate('privacy-breach'),
	securityBreach: i18n.translate('security-breach'),
};

const removeContactRoleLiferay = async (
	item: IContact,
	project: IProject,
	client: ApolloClient<object>
): Promise<FetchResult> => {
	return client.mutate({
		context: {
			displaySuccess: false,
		},
		mutation: deleteAccountUserRoles,
		variables: {
			accountKey: project.accountKey,
			accountRoleId: item.filterId,
			emailAddress: item.email,
		},
	});
};

const removeContactRoleRaysource = async (
	item: IContact,
	oAuthToken: string,
	project: IProject,
	provisioningServerAPI: string
): Promise<Response> => {
	return await deleteContactRoleNameByEmailByProject(
		project.accountKey as string,
		encodeURI(item.email),
		oAuthToken,
		provisioningServerAPI,
		item.filter?.toString() as string
	);
};

const rolesHighPriorityContacts = [
	'Data Breach Contact',
	'Security Incident Contact',
	'Critical Incident Contact',
];

const updateLiferayContact = (
	items: IContact[] | undefined,
	fn: (
		item: IContact,
		project: IProject,
		client: ApolloClient<object>
	) => Promise<FetchResult>,
	project: IProject,
	client: ApolloClient<object>
): Promise<any[]> => {
	return Promise.all(items?.map((item) => fn(item, project, client)) || []);
};

const updateRaysourceContact = (
	fn: (
		item: IContact,
		oAuthToken: string,
		project: IProject,
		provisioningServerAPI: string
	) => Promise<any>,
	contacts: IContact[] | undefined,
	oAuthToken: string,
	project: IProject,
	provisioningServerAPI: string
): Promise<any[]> => {
	return Promise.all(
		contacts?.map((item) =>
			fn(item, oAuthToken, project, provisioningServerAPI)
		) || []
	);
};

export {
	addContactRoleLiferay,
	addContactRoleRaysource,
	getAccountRolesId,
	getContactRoleByFilter,
	HIGH_PRIORITY_CONTACT_CATEGORIES,
	removeContactRoleLiferay,
	removeContactRoleRaysource,
	rolesHighPriorityContacts,
	updateLiferayContact,
	updateRaysourceContact,
};
