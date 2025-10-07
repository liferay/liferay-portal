/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface IAccountBrief {
	externalReferenceCode: string;
	id: number;
	name: string;
	roleBriefs: IRoleBrief[] | undefined;
}

export interface IAccountRole {
	id?: number;
	label?: string;
	name?: string;
	raysourceName?: string;
}

export interface IAccountSubscription {
	name?: string;
}

export interface IAccountSubscriptionGroup {
	accountSubscriptionGroupId?: number;
	activationStatus: string;
	name?: string;
}

export interface IBusinessEvent {
	actualGoLiveDate?: string;
	actualGoLiveDateTime?: string;
	actualGoLiveTime?: string;
	associatedTickets?: string;
	currentLiferayVersion?: {
		key: string;
		name: string;
	};
	dateModified?: string;
	description?: string;
	details?: string;
	eventStatus?: {
		key: string;
		name: string;
	};
	eventType?: {
		key: string;
		name: string;
	};
	id?: number;
	lastComment?: string;
	name?: string;
	newLiferayVersion?: {
		key: string;
		name: string;
	};
	r_accountEntryToBusinessEvents_accountEntryId?: number;
	targetGoLiveDate?: string;
	targetGoLiveDateTime?: string;
	targetGoLiveTime?: string;
	timeZone?: {
		key: string;
		name: string;
	};
}

export interface IBusinessEventVersion {
	change?: {
		key: string;
		name: string;
	};
	comment?: string;
	creator?: {
		name: string;
	};
	dateModified?: string;
	r_accountEntryToBusinessEventVersions_accountEntryId?: number;
	r_businessEventToBusinessEventVersions_c_businessEventId?: number;
}

export interface IKoroneikiAccount {
	accountKey: string;
	code: string;
	dxpVersion?: string;
	id: number;
	maxRequestors?: number;
	name: string;
	partnershipCurrent?: string;
	partnershipCurrentEndDate?: string;
	partnershipExpired?: string;
	partnershipExpiredEndDate?: string;
	partnershipFuture?: string;
	partnershipFutureStartDate?: string;
	region: string;
	slaCurrent?: string;
	slaCurrentEndDate?: string;
	slaExpired?: string;
	slaExpiredEndDate?: string;
	slaFuture?: string;
	slaFutureStartDate?: string;
	status: string;
}

export interface IOption {
	disabled?: boolean;
	label: string;
	value: string | number;
}

export interface IOrganizationBrief {
	name: string;
}

export interface IProject {
	acWorkspaceGroupId?: string;
	accountKey?: string;
	code?: string;
	dxpVersion?: string;
	externalReferenceCode?: string;
	id?: number;
	liferayContactEmailAddress?: string;
	liferayContactName?: string;
	maxRequestors?: number;
	name: string;
	partner?: any;
	slaCurrent?: string;
}

export interface IRoleBrief {
	name: string;
}

export interface ITicket {
	link: string;
	selected?: boolean;
	status: string;
	subject: string;
	ticketId: string | number;
}

export interface ITimeInput {
	hours: string;
	minutes: string;
}

export interface IUpload {
	accountKey?: string;
	attachmentName?: string;
	errorCode?: string;
	errorMessage?: string;
	gcsSessionURL?: string;
	ticketAttachmentId?: string;
	ticketId?: string;
	uploadAccountKey?: string;
}

export interface IUserAccount {
	accountBriefs?: IAccountBrief[];
	accountKey?: string;
	code?: string;
	email?: string;
	emailAddress?: string;
	familyName?: string;
	firstName?: string;
	givenName?: string;
	id?: number;
	isAccountAdmin: boolean;
	isOmniAdmin: boolean;
	isProvisioning: boolean;
	isStaff: boolean;
	lastName?: string;
	organizationBriefs?: IOrganizationBrief[];
	partnershipCurrent?: string;
	partnershipCurrentEndDate?: string;
	partnershipExpired?: string;
	partnershipExpiredEndDate?: string;
	partnershipFuture?: string;
	partnershipFutureStartDate?: string;
	region: string;
	roleBriefs?: IRoleBrief[];
	screenName?: string;
	slaCurrent?: string;
	slaCurrentEndDate?: string;
	slaExpired?: string;
	slaExpiredEndDate?: string;
	slaFuture?: string;
	slaFutureStartDate?: string;
	status: string;
	userId?: number;
	userName?: string;
	uuid?: string;
}
