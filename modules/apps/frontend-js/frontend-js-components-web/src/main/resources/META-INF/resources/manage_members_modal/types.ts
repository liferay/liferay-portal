/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum MemberType {
	GROUPS = 'groups',
	USERS = 'users',
}

export type Role = {
	externalReferenceCode: string;
	id: number;
	name: string;
};

export type RoleExternalReferenceCode =
	| 'L_ASSET_LIBRARY_ADMINISTRATOR'
	| 'L_ASSET_LIBRARY_CONNECTED_SITE_MEMBER'
	| 'L_ASSET_LIBRARY_CONTENT_REVIEWER'
	| 'L_ASSET_LIBRARY_MEMBER'
	| 'L_ASSET_LIBRARY_OWNER'
	| 'L_DESIGN_LIBRARY_ADMINISTRATOR'
	| 'L_DESIGN_LIBRARY_CONTENT_REVIEWER'
	| 'L_DESIGN_LIBRARY_MEMBER'
	| 'L_DESIGN_LIBRARY_OWNER'
	| 'L_PROJECT_CONTRIBUTOR'
	| 'L_PROJECT_MANAGER'
	| 'L_PROJECT_MEMBER';

export interface UserRole {
	id: number;
	name: string;
}

export interface UserAccount {
	emailAddress: string;
	externalReferenceCode: string;
	id: string;
	image?: string;
	imageId: string;
	name: string;
	roles: UserRole[];
}

export interface UserGroup {
	externalReferenceCode: string;
	id: string;
	name: string;
	numberOfUserAccounts?: string;
	roles: UserRole[];
}

export interface Page<T> {
	items: T[];
	lastPage: number;
	page: number;
	totalCount: number;
}

export interface ServiceResult {
	error?: string | null;
}

export interface FetchMembersArgs {
	externalReferenceCode: string;
	keywords?: string;
	nestedFields?: string;
	page?: number;
	pageSize?: number;
}

interface MembersFeedbackMessages {
	addGroupError: string;
	addGroupSuccess: string;
	addUserError: string;
	addUserSuccess: string;
	removeGroupError: string;
	removeGroupSuccess: string;
	removeUserError: string;
	removeUserSuccess: string;
	updateGroupError: string;
	updateSuccess: string;
	updateUserError: string;
}

export interface MembersConfig {
	defaultRoleExternalReferenceCode: RoleExternalReferenceCode;
	excludedRoleExternalReferenceCodes?: RoleExternalReferenceCode[];
	messages: MembersFeedbackMessages;
	roleNames?: Partial<Record<RoleExternalReferenceCode, string>>;
}

export interface AddMembersInputApi {
	excludeMembers: (UserAccount | UserGroup)[];
	filter?: string;
	onAutocompleteItemSelected: (item: UserAccount | UserGroup) => void;
	onSelectChange: (value: MemberType) => void;
	selectValue: MemberType;
}
