/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DEFAULT_LDAP_CONFIGURATION_VALUES = {
	autogenerateUserPasswordOnImport: false,
	createRolePerGroupOnImport: false,
	defaultUserPassword: 'test',
	enableExport: false,
	enableGroupCacheOnImport: true,
	enableGroupExport: true,
	enableImport: false,
	enableImportOnStartup: false,
	enableUserPasswordOnImport: true,
	enabled: false,
	importInterval: 10,
	importMethod: 'User',
	importUserSyncStrategy: 'Auth Type',
	lockExpirationTime: 86400000,
	method: 'Bind',
	passwordEncryptionAlgorithm: 'None',
	required: false,
	userLdapPasswordPolicy: false,
};

export type TLdapConfiguration = {
	autogenerateUserPasswordOnImport?: boolean;
	createRolePerGroupOnImport?: boolean;
	defaultUserPassword?: string;
	enableExport?: boolean;
	enableGroupCacheOnImport?: boolean;
	enableGroupExport?: boolean;
	enableImport?: boolean;
	enableImportOnStartup?: boolean;
	enableUserPasswordOnImport?: boolean;
	enabled?: boolean;
	importInterval?: number;
	importMethod?: string | 'Group' | 'User';
	importUserSyncStrategy?: string | 'Auth Type' | 'UUID';
	lockExpirationTime?: number;
	method?: string | 'Bind' | 'Password Compare';
	passwordEncryptionAlgorithm?:
		| string
		| 'BCRYPT'
		| 'MD2'
		| 'MD5'
		| 'None'
		| 'SHA'
		| 'SHA-256'
		| 'SHA-384'
		| 'SSHA'
		| 'UFC-CRYPT';
	required?: boolean;
	userLdapPasswordPolicy?: boolean;
};

export type TLdapServer = {
	authenticationSearchFilter?: string;
	baseDn?: string;
	baseProviderUrl?: string;
	clockSkew?: number;
	credentials?: string;
	customContactMapping?: string;
	customUserMapping?: string;
	defaultValues?:
		| 'Apache Directory Server'
		| 'Fedora Directory Server'
		| 'Microsoft Active Directory Server'
		| 'Novell eDirectory'
		| 'OpenLDAP'
		| 'other Directory Server';
	description?: string;
	emailAddress?: string;
	firstName?: string;
	fullName?: string;
	group?: string;
	groupDefaultObjectClasses?: string;
	groupName?: string;
	groupsDn?: string;
	ignoreUserSearchFilterForAuthentication?: boolean;
	importSearchFilterGroup?: string;
	importSearchFilterUser?: string;
	jobTitle?: string;
	lastName?: string;
	middleName?: string;
	password?: string;
	portrait?: string;
	principal?: string;
	screenName?: string;
	serverName: string;
	status?: string;
	user?: string;
	userDefaultObjectClasses?: string;
	userIgnoreAttributes?: string;
	usersDn?: string;
	uuid?: string;
};
