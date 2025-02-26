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

export interface IAccountSubscriptionGroup {
	accountSubscriptionGroupId?: number;
	activationStatus: string;
	name?: string;
}

export interface IBusinessEvent {
	associatedTickets?: any;
	currentLiferayVersion?: {name: string};
	details?: string;
	eventStatus?: {name: string};
	eventType?: {name: string};
	id?: number;
	name?: string;
	newLiferayVersion?: {name: string};
	targetGoLiveDateTime?: Date | string;
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

export interface IUserAccount {
	accountBriefs?: IAccountBrief[];
	accountKey?: string;
	code?: string;
	email?: string;
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
