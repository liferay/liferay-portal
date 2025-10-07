/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '../i18n';

export enum LicenseType {
	PERPETUAL = 'perpetual',
	SUBSCRIPTION = 'subscription',
}

export enum ProductEditionOption {
	EE = 'EE',
}

export enum ProductImageFallbackCategories {
	PRODUCT_ICON = 'productIcon',
	PRODUCT_IMAGE = 'productImage',
}

export enum ProductLicense {
	BASE = 'base-license-usage-type',
	CLOUD = 'cloud-license-usage-type',
	DXP = 'dxp-license-usage-type',
}

export enum ProductLicenseTier {
	DEVELOPER = 'developer',
	STANDARD = 'standard',
	TRIAL = 'trial',
}

export enum ProductLicenseType {
	PERPETUAL = 'Perpetual',
	SUBSCRIPTION = 'Subscription',
}

export enum ProductOfferingTypes {
	LIFERAY_PAAS = 'Liferay PaaS',
	LIFERAY_SAAS = 'Liferay SaaS',
	LIFERAY_SELF_HOSTED = 'Liferay Self-Hosted',
}

export enum ProductPriceModel {
	FREE = 'Free',
	PAID = 'Paid',
}

export enum ProductSpecificationKey {
	APP_BUILD_NUMBER_OF_CPUS = 'cpu',
	APP_BUILD_RAM_IN_GBS = 'ram',
	APP_DEVELOPER_NAME = 'developer-name',
	APP_ENTRY_UUID = 'app-entry-uuid',
	APP_LICENSING_TYPE = 'license-type',
	APP_PRICING_MODEL = 'price-model',
	APP_SETTINGS = 'app-settings',
	APP_STOREFRONT_VIDEO_DESCRIPTION = 'app-storefront-video-description',
	APP_STOREFRONT_VIDEO_URL = 'app-storefront-video-url',
	APP_SUPPORT_DOCUMENTATION_URL = 'appdocumentationurl',
	APP_SUPPORT_EMAIL = 'supportemailaddress',
	APP_SUPPORT_INSTALLATION_GUIDE_URL = 'appinstallationguideurl',
	APP_SUPPORT_PHONE = 'supportphone',
	APP_SUPPORT_PUBLISHER_WEBSITE_URL = 'publisherwebsiteurl',
	APP_SUPPORT_URL = 'supporturl',
	APP_SUPPORT_USAGE_TERMS_URL = 'appusagetermsurl',
	APP_TYPE = 'type',
	APP_VERSION = 'latest-version',
	APP_VERSION_NOTES = 'product-notes',
	LIFERAY_VERSION = 'liferay-version',
	SOLUTION_COMPANY_DESCRIPTION = 'solution-company-description',
	SOLUTION_COMPANY_EMAIL = 'solution-company-email',
	SOLUTION_COMPANY_PHONE = 'solution-company-phone',
	SOLUTION_COMPANY_WEBSITE = 'solution-company-website',
	SOLUTION_CONTACT_EMAIL = 'solution-contact-email',
	SOLUTION_DETAILS_BLOCKS = 'solution-details-blocks',
	SOLUTION_HEADER_DESCRIPTION = 'solution-header-description',
	SOLUTION_HEADER_TITLE = 'solution-header-title',
	SOLUTION_HEADER_VIDEO_DESCRIPTION = 'solution-header-video-description',
	SOLUTION_HEADER_VIDEO_URL = 'solution-header-video-url',
	SOLUTION_TYPE = 'solution-type',
}

export enum ProductSupportSpecificationKey {
	APP_DOCUMENTATION_URL = 'appdocumentationurl',
	APP_INSTALLATION_GUIDE_URL = 'appinstallationguideurl',
	APP_USAGE_TERMS_URL = 'appusagetermsurl',
	PUBLISHER_WEBSITE_URL = 'publisherwebsiteurl',
	SUPPORT_EMAIL = 'supportemailaddress',
	SUPPORT_PHONE = 'supportphone',
	SUPPORT_URL = 'supporturl',
}

export enum ProductTags {
	APP_ICON = 'app-icon',
	SOLUTION_DETAILS = 'solution-details',
	SOLUTION_HEADER = 'solution-header',
	SOLUTION_PROFILE_APP_ICON = 'solution-profile-app-icon',
}

export enum ProductType {
	CLIENT_EXTENSION = 'client-extension',
	CLOUD = 'cloud',
	COMPOSITE_APP = 'composite-app',
	DXP = 'dxp',
	LOW_CODE_CONFIGURATION = 'low-code-configuration',
	OTHER = 'other',
	SSA_SAAS = 'ssa-saas',
}

export enum ProductTypeVocabulary {
	APP = 'App',
	SOLUTION = 'Solution',
}

export enum ProductUploadType {
	GITHUB = 'GitHub',
	LXC = 'Liferay SaaS',
	ZIP_UPLOAD = 'upload',
}

export enum ProductVersionOption {
	'7.4x' = '7.4',
}

export enum ProductVocabulary {
	APP_AREA = 'Marketplace App Category',
	APP_CATEGORY = 'Marketplace Category',
	APP_TAGS = 'Marketplace App Tags',
	AVAILABILITY = 'Marketplace Availability',
	LIFERAY_PLATFORM_OFFERING = 'Marketplace Liferay Platform Offering',
	LIFERAY_VERSION = 'Marketplace Liferay Version',
	PRODUCT_TYPE = 'Marketplace Product Type',
	SOLUTION_CATEGORY = 'Marketplace Solution Category',
	SOLUTION_TAGS = 'Marketplace Solution Tags',
}

export enum ProductWorkflowStatusCode {
	APPROVED = 0,
	DRAFT = 2,
	PENDING = 1,
}

export enum SkuOptions {
	DEVELOPER = 'developer',
	STANDARD = 'standard',
	TRIAL = 'trial',
}

export enum SolutionTypes {
	ANALYTICS = 'analytics',
	PRE_BUILT_TRIAL = 'pre-built-trial',
}

const ALL_OFFERINGS = [
	ProductOfferingTypes.LIFERAY_PAAS,
	ProductOfferingTypes.LIFERAY_SAAS,
	ProductOfferingTypes.LIFERAY_SELF_HOSTED,
];

const offeringTypes = {
	'client-extension': ALL_OFFERINGS,
	'cloud': [ProductOfferingTypes.LIFERAY_SAAS],
	'composite-app': [ProductOfferingTypes.LIFERAY_SELF_HOSTED],
	'dxp': [
		ProductOfferingTypes.LIFERAY_PAAS,
		ProductOfferingTypes.LIFERAY_SELF_HOSTED,
	],
	'low-code-configuration': ALL_OFFERINGS,
	'other': ALL_OFFERINGS,
};

export const ProductTypeLabels = {
	[ProductType.CLIENT_EXTENSION]: 'Client Extension',
	[ProductType.CLOUD]: 'Cloud',
	[ProductType.COMPOSITE_APP]: 'Composite App',
	[ProductType.DXP]: 'DXP',
	[ProductType.LOW_CODE_CONFIGURATION]: 'Low-Code Configuration',
	[ProductType.OTHER]: 'Other',
	[ProductType.SSA_SAAS]: 'SSA SaaS',
} as const;

export const ProductTypeLicenseOptions = {
	[ProductType.CLIENT_EXTENSION]: [ProductLicenseTier.STANDARD],
	[ProductType.CLOUD]: [ProductLicenseTier.STANDARD],
	[ProductType.COMPOSITE_APP]: [ProductLicenseTier.STANDARD],
	[ProductType.DXP]: [
		ProductLicenseTier.STANDARD,
		ProductLicenseTier.DEVELOPER,
		ProductLicenseTier.TRIAL,
	],
	[ProductType.LOW_CODE_CONFIGURATION]: [ProductLicenseTier.STANDARD],
	[ProductType.OTHER]: [ProductLicenseTier.STANDARD],
	[ProductType.SSA_SAAS]: [ProductLicenseTier.STANDARD],
};

export const ProductWorkflowDisplayType = {
	[ProductWorkflowStatusCode.APPROVED]: 'success',
	[ProductWorkflowStatusCode.DRAFT]: 'secondary',
	[ProductWorkflowStatusCode.PENDING]: 'warn',
};

export const ProductWorkflowStatusLabel = {
	[ProductWorkflowStatusCode.APPROVED]: i18n.translate('approved'),
	[ProductWorkflowStatusCode.DRAFT]: i18n.translate('draft'),
	[ProductWorkflowStatusCode.PENDING]: i18n.translate('under-review'),
};

export function getOfferingTypes(type: ProductType) {
	return offeringTypes[type as keyof typeof offeringTypes];
}
