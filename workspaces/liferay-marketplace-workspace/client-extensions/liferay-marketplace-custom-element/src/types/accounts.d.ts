/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type Account = {
	customFields?: CustomField[];
	dateCreated: string;
	description: string;
	emailAddress: string;
	externalReferenceCode: string;
	id: number;
	logoURL?: string;
	name: string;
	numberOfUsers: number;
	status: number;
	taxId: string;
	type: string;
};

type AccountBrief = {
	customFields?: any;
	externalReferenceCode: string;
	id: number;
	logoURL?: string;
	name: string;
	roleBriefs: RoleBrief[];
};

type AccountGroup = {
	customFields: {};
	externalReferenceCode: string;
	id: number;
	name: string;
};

type AccountPostalAddresses = {
	addressCountry: string;
	addressLocality: string;
	addressRegion: string;
	addressType: string;
	id: number;
	name: string;
	postalCode: number;
	primary: boolean;
	streetAddressLine1: string;
	streetAddressLine2: string;
	streetAddressLine3: string;
};

type AccountRole = {
	accountId: number;
	description: string;
	displayName: string;
	id: number;
	name: string;
	roleId: number;
};

type RoleBrief = {
	id: number;
	name: string;
};

type UserAccount = {
	accountBriefs: AccountBrief[];
	alternateName: string;
	currentPassword: string;
	emailAddress: string;
	externalReferenceCode: string;
	familyName: string;
	givenName: string;
	id: number;
	image: string;
	isCustomerAccount: boolean;
	isPublisherAccount: boolean;
	logoURL: string;
	name: string;
	newsSubscription: boolean;
	password: string;
	roleBriefs: RoleBrife[];
	type: string;
	userAccountContactInformation: {
		telephones: {
			extension: string;
			phoneNumber: string;
		}[];
	};
};