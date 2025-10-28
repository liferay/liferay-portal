/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type ConsoleProjectsUsage = {
	userEmail: string;
	userProjects: ConsoleUserProject[];
};

export type ConsoleUserProject = {
	environments: {isExtensionEnvironment: boolean; projectId: string}[];
	rootProjectId: string;
	rootProjectPlanUsage: {
		cpu: ConsoleCPU;
		instance: ConsoleCPU;
		memory: ConsoleCPU;
	};
};

type ConsoleCPU = {
	free: number;
	limit: number;
	used: number;
};

export type LicenseKey = {
	active: boolean;
	complimentary: boolean;
	createDate: string;
	description: string;
	expirationDate: string;
	hostName: string;
	id: number;
	ipAddresses: string;
	key: string;
	keyType: string;
	licenseType: string;
	macAddresses: string;
	modifiedDate: string;
	modifiedUserName: string;
	modifiedUserUuid: string;
	orderId: string;
	owner: string;
	productId: string;
	productName: string;
	productVersion: string;
	startDate: string;
	userName: string;
	userUuid: string;
};

export type LicenseTypePayload = {
	licenseEntry: {
		description: string;
		hostName: string;
		ipAddresses: string;
		macAddresses: string;
		orderId: string;
		productId?: string;
		productPurchaseKey: string;
		productVersion: string;
	};
	skuId: number;
	type: string;
};

export type SubscriptionsType = {
	endDate?: string;
	name: string;
	perpetual: boolean;
	productPurchasedKey: string;
	productVersion: string;
	provisionedCount: number;
	purchasedCount: number;
	startDate: string;
};

export type Product = {
	dateCreated: string;
	dateModified: string;
	externalLinks: ExternalLink[];
	key: string;
	name: string;
	properties: Properties;
};

export type ProductPurchase = {
	accountKey: string;
	dateCreated: string;
	endDate: string;
	externalLinks: any[];
	key: string;
	originalEndDate: string;
	perpetual: boolean;
	product: Product;
	productConsumptions: any;
	productKey: string;
	properties: Properties2;
	quantity: number;
	startDate: string;
	status: string;
	statusAsString: string;
};

export type ExternalLink = {
	dateCreated: string;
	domain: string;
	entityId: string;
	entityName: string;
	key: string;
	url: string;
};

export type Properties = {
	'display-group-name': string;
	'display-name': string;
	'type': string;
};

export type Properties2 = {
	licenses: string;
	sizing: string;
	version: string;
};
