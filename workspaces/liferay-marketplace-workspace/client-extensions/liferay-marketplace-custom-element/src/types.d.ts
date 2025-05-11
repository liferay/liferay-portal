/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

declare module '*.svg' {
	const content: any;
	export default content;
}

declare module 'warning';

type Account = {
	customFields?: CustomField[];
	description: string;
	emailAddress: string;
	externalReferenceCode: string;
	id: number;
	logoURL?: string;
	name: string;
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

type ActionMap<M extends {[index: string]: any}> = {
	[Key in keyof M]: M[Key] extends undefined
		? {
				type: Key;
			}
		: {
				payload: M[Key];
				type: Key;
			};
};

type AdditionalInfoBody = {
	acceptInviteStatus: boolean;
	accountName: string;
	emailOfMember: string;
	id?: number;
	inviteURL: string;
	inviterName: string;
	mothersName: string;
	r_accountEntryToUserAdditionalInfo_accountEntryId: number;
	r_userToUserAddInfo_userId: string;
	roles: string;
	sendType: {key: string; name: string};
	userFirstName: string;
};

type AnalyticsProject = {
	accountKey: string;
	accountName: string;
	corpProjectName: string;
	corpProjectUuid: string;
	faroSubscription: FaroSubscription;
	faroSubscriptionDisplay: FaroSubscription;
	friendlyURL: string;
	groupId: number;
	incidentReportEmailAddresses: string[];
	name: string;
	ownerEmailAddress: string;
	recommendationsEnabled: boolean;
	serverLocation: string;
	state: string;
	stateEndDate: null;
	stateStartDate: null;
	timeZone: TimeZone;
	userId: number;
};

type AnalyticsViews = {
	results: {
		metrics: {
			avgTimeOnPageMetric: {
				value: number;
			};
			bounceMetric: {
				value: number;
			};
			bounceRateMetric: {
				value: number;
			};
			ctaClicksMetric: {
				value: number;
			};
			directAccessMetric: {
				value: number;
			};
			entrancesMetric: {
				value: number;
			};
			exitRateMetric: {
				value: number;
			};
			indirectAccessMetric: {
				value: number;
			};
			readsMetric: {
				value: number;
			};
			sessionsMetric: {
				value: number;
			};
			timeOnPageMetric: {
				value: number;
			};
			viewsMetric: {
				value: number;
			};
			visitorsMetric: {
				value: number;
			};
		};
		title: string;
		url: string;
	}[];
	total: number;
};

type APIResponse<Query = any> = {
	actions: ObjectActions;
	facets: Facets[];
	items: Query[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
};

type Availability = {
	active: boolean;
	available: number;
	fallback: boolean;
	max: number;
};

type BillingAddress = {
	city?: string;
	country?: string;
	countryISOCode: string;
	description?: string;
	name?: string;
	phoneNumber?: string;
	regionISOCode?: string;
	saveAddress?: boolean;
	street1?: string;
	street2?: string;
	zip?: string;
};

type Cart = {
	accountId: number;
	author?: string;
	billingAddress: BillingAddress;
	cartItems: CartItem[];
	currencyCode: string;
	customFields: any;
	id: number;
	orderStatusInfo: {[key: string]: string};
	orderTypeExternalReferenceCode: string;
	orderTypeId: number;
	paymentMethod: string;
	paymentStatusInfo: {[key: string]: string};
	paymentStatusLabel: string;
	purchaseOrderNumber?: string;
	shippingAddress: BillingAddress;
	summary: {
		totalFormatted: string;
	};
};

type CartItem = {
	customFields?: {};
	price: {
		currency: string;
		discount: number;
		finalPrice?: number;
		price?: number;
	};
	productId?: number;
	quantity: number;
	settings: {
		maxQuantity: number;
	};
	skuId: number;
};

type Categories = {
	externalReferenceCode: string;
	id: string;
	name: string;
	value?: string;
	vocabulary: string;
};

type Catalog = {
	accountId: number | null;
	currencyCode: string;
	defaultLanguageId: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	system: boolean;
};

type Channel = {
	channelId: number;
	currencyCode: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	siteGroupId: number;
	type: string;
};

type CheckboxRole = {
	isChecked: boolean;
	roleName: string;
};

type CheckboxVersion = {
	isChecked: boolean;
	versionName: string;
};

type CommerceAccount = {
	active: boolean;
	logoURL: string;
	taxId: string;
} & Omit<Account, 'description'>;

type CommerceOption = {
	id: number;
	key: string;
	name: string;
};

type ContactSales = {
	accountName: string;
	additionalAppsRequested?: string | undefined;
	comments?: string | undefined;
	email: string;
	name: string;
};

type Creator = {
	additionalName: string;
	contentType: string;
	externalReferenceCode: string;
	familyName: string;
	givenName: string;
	id: number;
	image: string;
	name: string;
};

type CustomField = {
	customValue: {
		data: string | string[];
	};
	dataType?: string;
	name: string;
};

type DefaultProperties = {
	analyticsCloudURL: string;
	cloudConsoleURL: string;
	contactSupportURL: string;
	eulaBaseURL: string;
	featureFlags: string[];
	marketoFormId: string;
	productId: string;
	trialAccountCheck: 'false' | 'true';
	trialEulaURL: string;
	useSiteTaxonomyVocabularyQuery: boolean;
};

type DeliveryProduct = {
	attachments: DeliveryProductAttachment[];
	catalogName?: string;
	categories: ProductCategories[];
	createDate: string;
	customFields?: CustomField[];
	description: string;
	externalReferenceCode: string;
	id: number;
	images: ProductImages[];
	modifiedDate: string;
	name: string;
	productId: number;
	productSpecifications: DeliveryProductSpecification[];
	productType: string;
	shortDescription: string;
	skus: DeliverySKU[];
	urlImage: string;
	urls: {en_US: string};
};

type DeliveryProductAttachment = {
	customFields: CustomField[];
	galleryEnabled: boolean;
	id: number;
	priority: number;
	src: string;
	tags?: string[];
	title: string;
	type: number;
};

type DeliveryProductSpecification = {
	id: number;
	optionCategoryId: number;
	priority: number;
	specificationGroupKey: string;
	specificationGroupTitle: string;
	specificationId: number;
	specificationKey: string;
	specificationTitle: string;
	value: string;
};

type DeliverySKU = {
	customFields?: CustomField[];
	externalReferenceCode: string;
	id: number;
	price: {price: number; priceFormatted: string};
	purchasable: boolean;
	sku: string;
	skuOptions: DeliverySKUOption[];
	tierPrices?: TierPrice[];
};

type DeliverySKUOption = {
	skuOptionKey: string;
	skuOptionValueKey: string;
};

type FaroSubscription = {
	active: boolean;
	addOns: any[];
	endDate: null;
	individualsCountSinceLastAnniversary: number;
	individualsCounts: null;
	individualsLimit: number;
	individualsStatus: number;
	lastAnniversaryDate: null;
	name: string;
	pageViewsCountSinceLastAnniversary: number;
	pageViewsCounts: null;
	pageViewsLimit: number;
	pageViewsStatus: number;
	startDate: number;
	syncedIndividualsCount: number;
};

type ListTypeDefinition = {
	externalReferenceCode: string;
	id: number;
	listTypeEntries: {
		externalReferenceCode: string;
		key: string;
		name: string;
		name_i18n: {
			[key: string]: string;
		};
	}[];
	name: string;
};

type OfferingType = {
	description: string;
	disabled?: boolean;
	label: string;
};

type Order = {
	account: {
		id: number;
		name: string;
		type: string;
	};
	accountExternalReferenceCode?: string;
	accountId: number;
	billingAddressId?: number;
	channel: {
		currencyCode?: string;
		id: number;
		type: string;
	};
	channelExternalReferenceCode?: string;
	channelId: number;
	createDate?: string;
	creatorEmailAddress?: string;
	currencyCode: string;
	customFields?: {[key: string]: string};
	externalReferenceCode?: string;
	id: number;
	marketplaceOrderType?: string;
	modifiedDate?: string;
	orderDate?: string;
	orderItems: [
		{
			id?: number;
			name?: {
				en_US: string;
			};
			quantity?: number;
			skuId: number;
			unitPriceWithTaxAmount?: number;
		},
	];
	orderStatus: number;
	orderStatusInfo: {
		code: number;
		label: string;
		label_i18n: string;
	};
	orderTypeExternalReferenceCode?: string;
	orderTypeId?: number;
	paymentStatusInfo: {
		code: number;
		label: string;
		label_i18n: string;
	};
	placedOrderItems?: any;
	shippingAmount?: number;
	shippingWithTaxAmount?: number;
	totalAmount?: number;
	totalFormatted: string;
};

type OrderType = {
	externalReferenceCode: string;
	id: number;
	name: {[key: string]: string};
};

type OptionCategory = {
	description?: {[key: string]: string};
	id?: number;
	key?: string;
	priority?: number;
	title?: {[key: string]: string};
};

type PaymentMethodSelector = 'order' | 'pay' | 'trial' | 'free';

type PermissionDescription = {
	permissionName: string;
	permissionTooltip: string;
	permittedRoles: string[];
};

type PlacedOrder = {
	account: string;
	accountId: number;
	author: string;
	createDate: string;
	customFields: {[key: string]: string};
	id: number;
	orderStatusInfo: {
		code: number;
		label: string;
		label_i18n: string;
	};
	orderTypeExternalReferenceCode: string;
	paymentStatus: number;
	placedOrderBillingAddress: any;
	placedOrderBillingAddressId: number;
	placedOrderItems: PlacedOrderItems[];
	workflowStatusInfo: {
		code: number;
		label: string;
		label_i18n: string;
	};
};

type PlacedOrderItems = {
	id: number;
	name: string;
	options: string;
	price: {
		price: number;
		priceFormatted: string;
	};
	productId: number;
	quantity: number;
	sku: string;
	skuId: number;
	subscription: boolean;
	thumbnail: string;
	version: string;
	virtualItemURLs: string;
	virtualItems: VirtualItem[];
};

type Product = {
	__marketplaceProduct: any;
	active: boolean;
	attachments: ProductAttachment[];
	catalog: Catalog;
	catalogId: number;
	catalogName?: string;
	categories: ProductCategories[];
	createDate: string;
	customFields?: CustomField[];
	description: {[key: string]: string};
	externalReferenceCode: string;
	finalPrice?: number;
	id: number;
	images: ProductImages[];
	modifiedDate: string;
	name: {[key: string]: string};
	price?: number;
	productId: number;
	productOptions: ProductOption[];
	productSpecifications: ProductSpecification[];
	productStatus: number;
	productType: string;
	skus: SKU[];
	thumbnail: string;
	urlImage: string;
	urls: {en_US: string};
	version: number;
	workflowStatusInfo: {
		code: number;
		label: string;
		label_i18n: string;
	};
};

type ProductAttachment = {
	customFields?: CustomField[];
	externalReferenceCode: string;
	fileEntryId: number;
	galleryEnabled: boolean;
	id: number;
	priority: number;
	src: string;
	tags?: string[];
	title: {[key: string]: string};
};

type ProductCategories = {
	externalReferenceCode: string;
	id: number;
	name: string;
	vocabulary: string;
};

type ProductImages = ProductAttachment;

type ProductOption = {
	customFields: any[];
	description: {[key: string]: string};
	facetable: boolean;
	fieldType: string;
	id: number;
	key: string;
	name: {[key: string]: string};
	optionExternalReferenceCode: string;
	optionId: number;
	priceType: string;
	productOptionValues: {
		id: number;
		key: string;
		name: {en_US: string};
	}[];
	required: boolean;
	skuContributor: boolean;
	typeSettings: string;
};

type ProductOptionItem = {
	id: number;
	key: string;
	name: string;
	optionId: number;
};

type ProductSpecification = {
	id?: number;
	optionCategoryId?: number;
	priority?: number;
	productId?: number;
	specificationId?: number;
	specificationKey: string;
	value: {[key: string]: string};
};

type PublisherRequestInfo = {
	creator: {name: string};
	dateCreated: string;
	emailAddress?: string;
	extension?: string;
	firstName?: string;
	id?: number;
	lastName?: string;
	phone?: {
		code: string;
		flag: string;
	};
	phoneNumber?: string;
	publisherType: string[];
	requestDescription?: string;
	requestStatus?: {
		key: string;
		name: string;
	};
};

type RadioOption<T> = {
	index: number;
	value: T;
};

type RoleBrief = {
	id: number;
	name: string;
};

type SKU = {
	cost: number;
	customFields?: CustomField[];
	externalReferenceCode: string;
	id: number;
	price: number;
	sku: string;
	skuOptions: {key: string; value: string}[];
};

type Specification = {
	description?: {[key: string]: string};
	id?: number;
	key?: string;
	optionCategory?: OptionCategory;
	title?: {[key: string]: string};
};

type TaxonomyCategory = {
	externalReferenceCode: string;
	id: number;
	name: string;
};

type TaxonomyVocabulary = {
	externalReferenceCode: string;
	id: number;
	label: string;
	name: string;
	taxonomyCategories: APIResponse<TaxonomyCategory>;
};

type TierPrice = {
	currency: string;
	price: number;
	priceFormatted: string;
	quantity: number;
};

type TimeZone = {
	country: string;
	displayTimeZone: string;
	timeZoneId: string;
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
	roleBriefs: {id: number; name: string}[];
	type: string;
	userAccountContactInformation: {
		telephones: {
			extension: string;
			phoneNumber: string;
		}[];
	};
};

type VirtualItem = {
	productVersion?: String;
	url: string;
	usages: number;
	version: string;
};
