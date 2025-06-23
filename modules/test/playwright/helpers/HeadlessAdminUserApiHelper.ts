/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TAccount = {
	alternateName?: string;
	description?: string;
	externalReferenceCode?: string;
	id?: number;
	name?: string;
	status?: number;
	taxID?: string;
	type?: string;
};

type TAccountGroup = {
	description?: string;
	externalReferenceCode?: string;
	id?: number;
	name: string;
};

type TExportBatch = {
	className?: string;
	contentType?: string;
	errorMessage?: string;
	executeStatus?: string;
	externalReferenceCode?: string;
	id?: number;
	processedItemsCount?: number;
	startTime?: string;
	totalItemsCount?: number;
};

type THoursAvailable = {
	closes: string;
	dayOfWeek?: string;
	opens: string;
};

type TOrganization = {
	externalReferenceCode?: string;
	id?: string;
	name?: string;
	organizationContactInformation?: TOrganizationContactInformation;
	parentOrganization?: TOrganization;
	services?: TServices[];
};

export type TOrganizationContactInformation = {
	postalAddresses: TPostalAddresses[];
};

export type TPostalAddresses = {
	addressCountry: string;
	addressLocality: string;
	addressType: string;
	externalReferenceCode?: string;
	id?: string;
	postalCode: string;
	streetAddressLine1: string;
};

export type TPermission = {
	actionIds: string[];
	primaryKey: string;
	resourceName: string;
	scope: number;
};

export type TRole = {
	externalReferenceCode?: string;
	id?: number;
	name: string;
	name_i18n?: {
		[key: string]: string;
	};
	rolePermissions?: Array<TPermission>;
	roleType?: number | string;
};

type TServices = {
	hoursAvailable: THoursAvailable[];
	serviceType: string;
};

type TTicket = {
	expirationDate: string;
	extraInfo: string;
	id: number;
	key: string;
};

type TUserGroup = {
	description?: string;
	externalReferenceCode?: string;
	id?: number;
	name: string;
};

export class HeadlessAdminUserApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-admin-user/v1.0/';
	}

	async assignAccountRoles(
		accountERC: string,
		roleId: number,
		userEmail: string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/by-external-reference-code/${accountERC}/account-roles/${roleId}/user-accounts/by-email-address/${userEmail}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async assignAccountToAccountGroup(
		accountExternalReferenceCode: string,
		accountGroupExternalReferenceCode: string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/account-groups/by-external-reference-code/${accountExternalReferenceCode}/accounts/by-external-reference-code/${accountGroupExternalReferenceCode}`
		);
	}

	async assignAccountToOrganization(
		accountId: number,
		organizationId: string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/organizations/${organizationId}`
		);
	}

	async assignUserToAccountByEmailAddress(
		accountId: number,
		emails: string[]
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/user-accounts/by-email-address`,
			{data: emails || []}
		);
	}

	async assignUserToAccountRole(
		accountId: number | string,
		accountRoleId: number | string,
		userId: number | string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}accounts/${accountId}/account-roles/${accountRoleId}/user-accounts/${userId}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async assignUserToOrganizationByEmailAddress(
		organizationId: string,
		emailAddress: string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations/${organizationId}/user-accounts/by-email-address/${emailAddress}`
		);
	}

	async assignUserToOrganizationRole(
		roleId: number | string,
		userAccountId: string,
		organizationId: string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles/${roleId}/association/user-account/${userAccountId}/organization/${organizationId}`
		);
	}

	async assignUserToRole(
		roleExternalReferenceCode: string,
		userId: number | string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}roles/by-external-reference-code/${roleExternalReferenceCode}/association/user-account/${userId}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async assignUserToSite(
		roleId: number | string,
		siteId: number | string,
		userId: number | string
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}roles/${roleId}/association/user-account/${userId}/site/${siteId}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async assignUsersToUserGroup(userGroupId: number, userIds: string[]) {
		const association = this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-groups/${userGroupId}/user-group-users`,
			{
				data: userIds,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: `${userGroupId}_${userIds.join('_')}`,
				type: 'userGroupUserAccountAssociation',
			});
		}

		return association;
	}

	async deleteAccount(accountId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}`
		);
	}

	async deleteAccountGroup(accountGroupId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/account-groups/${accountGroupId}`
		);
	}

	async deleteOrganization(organizationId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations/${organizationId}`
		);
	}

	async deleteOrganizationUserAccountAssociation(
		organizationId: string,
		emailAddress: string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations/${organizationId}/user-accounts/by-email-address/${emailAddress}`
		);
	}

	async deletePostalAddress(postalAddressId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/postal-addresses/${postalAddressId}`
		);
	}

	async deleteRole(roleId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles/${roleId}`
		);
	}

	async deleteRoleUserAccountAssociation(
		roleId: number,
		userAccountId: number
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles/${roleId}/association/user-account/${userAccountId}`
		);
	}

	async deleteUserAccount(userAccountId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-accounts/${userAccountId}`
		);
	}

	async deleteUserFromAccountByEmailAddress(
		accountId: number,
		emailAddress: string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/user-accounts/by-email-address/${emailAddress}`
		);
	}

	async deleteUserFromOrganizationByEmailAddress(
		organizationId: string,
		emailAddress: string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations/${organizationId}/user-accounts/by-email-address/${emailAddress}`
		);
	}

	async deleteUserGroup(userGroupId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-groups/${userGroupId}`
		);
	}

	async deleteUserGroupUsers(userGroupId: number, userIds: string[]) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-groups/${userGroupId}/user-group-users`,
			{
				data: userIds,
			}
		);
	}

	async deleteUserRole(
		roleExternalReferenceCode: string,
		userId: number | string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}roles/by-external-reference-code/${roleExternalReferenceCode}/association/user-account/${userId}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async getAccountByName(accountName: string): Promise<TAccount> {
		const accountResponse = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts?filter=name eq '${accountName}'`
		);

		return accountResponse?.items?.at(0);
	}

	async getAccountByExternalReferenceCode(externalReferenceCode: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async getAccountGroupByExternalReferenceCode(
		externalReferenceCode: string
	): Promise<TAccountGroup> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/account-groups/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async getAccountRoles(accountId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/account-roles`
		);
	}

	async getAccountRolesByRoleName(
		accountId: number,
		accountRoleName: string
	) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/account-roles?filter=name eq '${accountRoleName}'`
		);
	}

	async getMyUserAccount(): Promise<TAccount> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/my-user-account`
		);
	}

	async getOrganizationByName(organizationName: string): Promise<TAccount> {
		const organizationResponse = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations?filter=name eq '${organizationName}'&flatten=true`
		);

		return organizationResponse?.items?.at(0);
	}

	async getSiteByFriendlyUrlPath(friendlyUrlPath: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/by-friendly-url-path/${friendlyUrlPath}`
		);
	}

	async getRoleByExternalReferenceCode(externalReferenceCode: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async getRoleByName(name: string, restrictFields = '') {
		const response = await this.getRoles(name, restrictFields);

		const roles = response.items || [];

		for (const role of roles as TRole[]) {
			if (role.name.toLowerCase() === name.toLowerCase()) {
				return role;
			}
		}

		return null;
	}

	async getRoles(search: string, restrictFields = '') {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles?search=${search}&restrictFields=${restrictFields}`
		);
	}

	async getUserAccountByEmailAddress(emailAddress: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-accounts/by-email-address/${emailAddress}`
		);
	}

	async getUserAccountPasswordResetTicket(
		userAccountId: string
	): Promise<TTicket> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-accounts/${userAccountId}/password-reset-ticket`
		);
	}

	async patchUserAccount(
		userAccount?: TUserAccount,
		patchedUserAccount?: DataObject
	): Promise<TUserAccount> {
		userAccount = await this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-accounts/${userAccount.id}`,
			patchedUserAccount
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: userAccount.id,
				type: 'userAccount',
			});
		}

		return userAccount;
	}

	async postAccount(account?: TAccount): Promise<TAccount> {
		account = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts`,
			{
				data: {name: 'Account' + getRandomInt(), ...(account || {})},
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: account.id,
				type: 'account',
			});
		}

		return account;
	}

	async postAccountAccountRoles(
		accountId: number,
		accountRole?: TRole
	): Promise<TRole> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/account-roles`,
			{
				data: {name: 'Role' + getRandomInt(), ...(accountRole || {})},
				failOnStatusCode: true,
			}
		);
	}

	async postAccountGroup(
		accountGroup?: TAccountGroup
	): Promise<TAccountGroup> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/account-groups`,
			{
				data: {
					name: 'AccountGroup' + getRandomInt(),
					...(accountGroup || {}),
				},
			}
		);
	}

	async postAccountOrganization(accountId: number, organizationId: string) {
		return this.apiHelpers.postResponse(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/organizations/${organizationId}`
		);
	}

	async postAccountUserAccountByEmailAddress(
		accountId: number,
		accountRoleIds: number[],
		emailAddresses: string[]
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}
			/user-accounts/by-email-address${
				accountRoleIds ? `?accountRoleIds=${accountRoleIds}` : ''
			}`,
			{data: emailAddresses}
		);
	}

	async postOrganization(
		organization?: TOrganization
	): Promise<TOrganization> {
		organization = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations`,
			{
				data: {
					name: 'Organization' + getRandomInt(),
					...(organization || {}),
				},
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: organization.id,
				type: 'organization',
			});
		}

		return organization;
	}

	async postOrganizationAccounts(
		organizationId: number,
		accountIds: number[]
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/organizations/${organizationId}/accounts`,
			{data: accountIds}
		);
	}

	async postRole(role: TRole) {
		role = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles`,
			{
				data: {
					roleType: 'regular',
					...role,
				},
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: role.id,
				type: 'role',
			});
		}

		return role;
	}

	async postRoleByExternalReferenceCodeUserAccountAssociation(
		roleExternalReferenceCode: string,
		userAccountId: string
	) {
		return this.apiHelpers.postResponse(
			`${this.apiHelpers.baseUrl}${this.basePath}roles/by-external-reference-code/${roleExternalReferenceCode}/association/user-account/${userAccountId}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async postRoleUserAccountAssociation(
		roleId: number,
		userAccountId: number
	) {
		return this.apiHelpers.postResponse(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles/${roleId}/association/user-account/${userAccountId}`,
			{data: {}, failOnStatusCode: true}
		);
	}

	async postRolesPageExportBatch(): Promise<TExportBatch> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/roles/export-batch`
		);
	}

	async postUserAccount(
		userAccount?: TUserAccount,
		randomNumber = getRandomInt()
	): Promise<TUserAccount> {
		userAccount = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-accounts`,
			{
				data: {
					alternateName: 'User' + randomNumber,
					emailAddress: 'User' + randomNumber + '@liferay.com',
					familyName: 'User' + randomNumber,
					givenName: 'User' + randomNumber,
					password: 'test',
					...(userAccount || {}),
				},
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: userAccount.id,
				type: 'userAccount',
			});
		}

		return userAccount;
	}

	async postUserGroup(userGroup?: TUserGroup): Promise<TUserGroup> {
		userGroup = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/user-groups`,
			{
				data: {
					name: 'UserGroup' + getRandomInt(),
					...(userGroup || {}),
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: userGroup.id,
				type: 'userGroup',
			});
		}

		return userGroup;
	}
}
