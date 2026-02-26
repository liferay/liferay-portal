/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {SetStateAction} from 'react';

export interface IAccount {
	externalReferenceCode: string;
	id: number;
	logoURL: string | undefined;
	name: string;
	status: number;
	type: string;
}

export interface IAccounts {
	items: Array<IAccount>;
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

export interface IInvitedMember {
	emailAddress: string;
	id: number;
	roleKey?: string;
}

export interface IInvitedMembersDTO {
	items: Array<IInvitedMember>;
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

export interface IRoom {
	actions: {
		[action: string]: {
			href: string;
			method: string | 'DELETE' | 'GET' | 'PATCH' | 'POST' | 'PUT';
		};
	};
	dateCreated: string;
	dateModified: string;
	embedded: IRoomObjectEntry;
	entryClassName: string;
	score: number;
}

export interface IRoomContext {
	dataContext: IRoomDataContext;
	loading: boolean;
	setDataContext: React.Dispatch<React.SetStateAction<IRoomDataContext>>;
	setLoading?: React.Dispatch<React.SetStateAction<boolean>>;
}

export interface IRoomDataContext {
	accountId?: number;
	accountName?: string;
	description?: string;
	errors: {
		accountId?: null | string;
		description?: null | string;
		friendlyURL?: null | string;
		roomName?: null | string;
		share?: null | string;
	};
	friendlyURL: string;
	roomName: string;
	share?: {
		emailAddresses: Array<string>;
		roleKey?: string;
	};
	templateKey?: string;
}

export interface IRoomInitializerProps {
	closeModal: () => void;
	createRedirectURL?: string;
	numberOfSteps?: number;
	siteTemplates?: Array<ISiteTemplate>;
}

export interface IRoomObjectEntry {
	actions: any;
	creator: {
		additionalName: string;
		contentType: string;
		externalReferenceCode: string;
		familyName: string;
		givenName: string;
		id: number;
		name: string;
	};
	dateCreated: string;
	dateModified: string;
	description: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	r_accountToDSRRooms_accountEntry: {
		description: string;
		externalReferenceCode: string;
		id: number;
		logoId: number;
		logoURL: string;
		name: string;
	};
	r_accountToDSRRooms_accountEntryId: number;
	siteId: number;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
}

export interface IRoomShareProps {
	closeModal?: () => void;
	roomId: number;
}

export interface IRoomStepProps {
	setHandleStepSubmit(
		callback: SetStateAction<(event: Event) => Promise<boolean>>
	): void;
	numberOfSteps: number;
	showHeader?: boolean;
	siteTemplates?: Array<ISiteTemplate>;
	step?: number;
}

export interface ISiteTemplate {
	description: string;
	friendlyURL: string;
	name: string;
	uuid: string;
}

export interface IUserAccount {
	alternateName?: string;
	emailAddress: string;
	externalReferenceCode?: string;
	id: number;
	image?: string;
	isInvitedMember?: boolean;
	name: string;
	roleKey?: string;
}

export type IUserAccountsDTO = {
	items: Array<IUserAccount>;
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
};
