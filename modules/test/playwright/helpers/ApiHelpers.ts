/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
	ObjectFolderAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {BrowserContext, Page} from '@playwright/test';

import {liferayConfig} from '../liferay.config';
import {AnalyticsSettingsRestApiHelper} from './AnalyticsSettingsRestApiHelper';
import {ApiBuilderHelper} from './ApiBuilderHelper';
import {CookiesApiHelper} from './CookiesApiHelper';
import {DataEngineApiHelper} from './DataEngineApiHelper';
import {DynamicDataMappingApiHelper} from './DynamicDataMappingApiHelper';
import {FeatureFlagApiHelper} from './FeatureFlagApiHelper';
import {HeadlessAdminAddressApiHelper} from './HeadlessAdminAddressApiHelper';
import {HeadlessAdminContentApiHelper} from './HeadlessAdminContentApiHelper';
import {HeadlessAdminSiteApiHelper} from './HeadlessAdminSiteApiHelper';
import {HeadlessAdminTaxonomyApiHelper} from './HeadlessAdminTaxonomyApiHelper';
import {HeadlessAdminUserApiHelper} from './HeadlessAdminUserApiHelper';
import {HeadlessAdminWorkflowApiHelper} from './HeadlessAdminWorkflowApiHelper';
import {HeadlessAssetLibraryApiHelper} from './HeadlessAssetLibraryApiHelper';
import {HeadlessBatchEngineApiHelper} from './HeadlessBatchEngineApiHelper';
import {HeadlessChangeTrackingApiHelper} from './HeadlessChangeTrackingApiHelper';
import {HeadlessCommerceAdminAccountApiHelper} from './HeadlessCommerceAdminAccountApiHelper';
import {HeadlessCommerceAdminCatalogApiHelper} from './HeadlessCommerceAdminCatalogApiHelper';
import {HeadlessCommerceAdminChannelApiHelper} from './HeadlessCommerceAdminChannelApiHelper';
import {HeadlessCommerceAdminInventoryApiHelper} from './HeadlessCommerceAdminInventoryApiHelper';
import {HeadlessCommerceAdminOrderApiHelper} from './HeadlessCommerceAdminOrderApiHelper';
import {HeadlessCommerceAdminOrderAttachmentApiHelper} from './HeadlessCommerceAdminOrderAttachmentApiHelper';
import {HeadlessCommerceAdminPaymentApiHelper} from './HeadlessCommerceAdminPaymentApiHelper';
import {HeadlessCommerceAdminPricingApiHelper} from './HeadlessCommerceAdminPricingApiHelper';
import {HeadlessCommerceAdminShipmentApiHelper} from './HeadlessCommerceAdminShipmentApiHelper';
import {HeadlessCommerceDeliveryCartApiHelper} from './HeadlessCommerceDeliveryCartApiHelper';
import {HeadlessCommerceDeliveryCatalogApiHelper} from './HeadlessCommerceDeliveryCatalogApiHelper';
import {HeadlessCommerceReturnApiHelper} from './HeadlessCommerceReturnApiHelper';
import {HeadlessDeliveryApiHelper} from './HeadlessDeliveryApiHelper';
import {HeadlessDigitalSalesRoomApiHelper} from './HeadlessDigitalSalesRoomApiHelper';
import {HeadlessPortalInstanceApiHelper} from './HeadlessPortalInstanceApiHelper';
import {LanguageApiHelper} from './LanguageApiHelper';
import {ListTypeAdminApiHelper} from './ListTypeAdminApiHelper';
import {NotificationApiHelper} from './NotificationApiHelper';
import {ObjectActionApiHelper} from './ObjectActionApiHelper';
import {ObjectAdminApiHelper} from './ObjectAdminApiHelper';
import {ObjectEntryApiHelper} from './ObjectEntryApiHelper';
import {ObjectEntryFolderApiHelper} from './ObjectEntryFolderApiHelper';
import {SCIMApiHelper} from './SCIMApiHelper';
import {SEOStudioApiHelper} from './SEOStudioApiHelper';
import {SearchExperiencesApiHelper} from './SearchExperiencesApiHelper';
import {JSONWebServicesAnnouncementsEntryApiHelper} from './json-web-services/JSONWebServicesAnnouncementsEntryApiHelper';
import {JSONWebServicesAssetDisplayPageEntryApiHelper} from './json-web-services/JSONWebServicesAssetDisplayPageEntryApiHelper';
import {JSONWebServicesAssetListEntryApiHelper} from './json-web-services/JSONWebServicesAssetListEntryApiHelper';
import {JSONWebServicesAudiencesEntryApiHelper} from './json-web-services/JSONWebServicesAudiencesEntryApiHelper';
import {JSONWebServicesCalendarApiHelper} from './json-web-services/JSONWebServicesCalendarApiHelper';
import {JSONWebServicesCalendarResourceApiHelper} from './json-web-services/JSONWebServicesCalendarResourceApiHelper';
import {JSONWebServicesClassNameApiHelper} from './json-web-services/JSONWebServicesClassNameApiHelper';
import {JSONWebServicesClientExtensionApiHelper} from './json-web-services/JSONWebServicesClientExtensionApiHelper';
import {JSONWebServicesCompanyApiHelper} from './json-web-services/JSONWebServicesCompanyApiHelper';
import {JSONWebServicesDDMApiHelper} from './json-web-services/JSONWebServicesDDMApiHelper';
import {JSONWebServicesDepotApiHelper} from './json-web-services/JSONWebServicesDepotApiHelper';
import {JSONWebServicesDepotGroupRelApiHelper} from './json-web-services/JSONWebServicesDepotGroupRelApiHelper';
import {JSONWebServicesDocumentLibraryApiHelper} from './json-web-services/JSONWebServicesDocumentLibraryApiHelper';
import {JSONWebServicesFragmentCollectionApiHelper} from './json-web-services/JSONWebServicesFragmentCollectionApiHelper';
import {JSONWebServicesFragmentEntryApiHelper} from './json-web-services/JSONWebServicesFragmentEntryApiHelper';
import {JSONWebServicesGroupApiHelper} from './json-web-services/JSONWebServicesGroupApiHelper';
import {JSONWebServicesJournalApiHelper} from './json-web-services/JSONWebServicesJournalApiHelper';
import {JSONWebServicesLayoutApiHelper} from './json-web-services/JSONWebServicesLayoutApiHelper';
import {JSONWebServicesLayoutPageTemplateCollectionApiHelper} from './json-web-services/JSONWebServicesLayoutPageTemplateCollection';
import {JSONWebServicesLayoutPageTemplateEntryApiHelper} from './json-web-services/JSONWebServicesLayoutPageTemplateEntry';
import {JSONWebServicesLayoutSetApiHelper} from './json-web-services/JSONWebServicesLayoutSetApiHelper';
import {JSONWebServicesLayoutSetPrototypeApiHelper} from './json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {JSONWebServicesMBApiHelper} from './json-web-services/JSONWebServicesMBApiHelper';
import {JSONWebServicesOSBAsahApiHelper} from './json-web-services/JSONWebServicesOSBAsahApiHelper';
import {JSONWebServicesOSBFaroApiHelper} from './json-web-services/JSONWebServicesOSBFaroApiHelper';
import {JSONWebServicesPushNotificationsDeviceApiHelper} from './json-web-services/JSONWebServicesPushNotificationsDeviceApiHelper';
import {JSONWebServicesResourcePermissionApiHelper} from './json-web-services/JSONWebServicesResourcePermissionApiHelper';
import {JSONWebServicesRoleApiHelper} from './json-web-services/JSONWebServicesRoleApiHelper';
import {JSONWebServicesSegmentsEntryApiHelper} from './json-web-services/JSONWebServicesSegmentsEntryApiHelper';
import {JSONWebServicesSiteNavigationMenuApiHelper} from './json-web-services/JSONWebServicesSiteNavigationMenuApiHelper';
import {JSONWebServicesStagingApiHelper} from './json-web-services/JSONWebServicesStagingApiHelper';
import {JSONWebServicesTeamApiHelper} from './json-web-services/JSONWebServicesTeamApiHelper';
import {JSONWebServicesUserApiHelper} from './json-web-services/JSONWebServicesUserApiHelper';
import {JSONWebServicesUserGroupApiHelper} from './json-web-services/JSONWebServicesUserGroupApiHelper';

type ContentType = 'application/json' | 'application/x-www-form-urlencoded';

type TDataApiHelpersData = {
	applicationName?: string;
	id: any;
	type: string;
};

interface HeadlessClientWithHeaders {
	defaultHeaders: Record<string, string>;
}

interface RequestOptions<T> {
	data?: T;
	failOnStatusCode?: boolean;
	headers?: {[key: string]: string};
	multipart?: {[key: string]: any};
}

const authTokens = new WeakMap<BrowserContext, string>();

export function clearAuthToken(page: Page) {
	authTokens.delete(page.context());
}

export async function readAuthToken(page: Page) {
	const authToken = await page.evaluate(() => Liferay.authToken);

	authTokens.set(page.context(), authToken);

	return authToken;
}

async function getCSRFTokenHeader(page: Page) {
	let authToken = authTokens.get(page.context());

	if (authToken === undefined) {
		authToken = await readAuthToken(page);
	}

	return {
		'x-csrf-token': authToken,
	};
}

export async function getHeader(
	page: Page,
	contentType: ContentType = 'application/json'
) {
	return {
		'Content-Type': contentType,
		...(await getCSRFTokenHeader(page)),
	};
}

export class ApiHelpers {
	readonly analyticsSettingsRest: AnalyticsSettingsRestApiHelper;
	readonly apiBuilder: ApiBuilderHelper;
	readonly baseUrl: string;
	readonly cookies: CookiesApiHelper;
	readonly featureFlag: FeatureFlagApiHelper;
	readonly dataEngine: DataEngineApiHelper;
	readonly dynamicDataMapping: DynamicDataMappingApiHelper;
	readonly headlessAdminAddress: HeadlessAdminAddressApiHelper;
	readonly headlessAdminContent: HeadlessAdminContentApiHelper;
	readonly headlessAdminSite: HeadlessAdminSiteApiHelper;
	readonly headlessAdminTaxonomy: HeadlessAdminTaxonomyApiHelper;
	readonly headlessAdminUser: HeadlessAdminUserApiHelper;
	readonly headlessAdminWorkflow: HeadlessAdminWorkflowApiHelper;
	readonly headlessAssetLibrary: HeadlessAssetLibraryApiHelper;
	readonly headlessBatchEngine: HeadlessBatchEngineApiHelper;
	readonly headlessChangeTracking: HeadlessChangeTrackingApiHelper;
	readonly headlessCommerceAdminAccount: HeadlessCommerceAdminAccountApiHelper;
	readonly headlessCommerceAdminCatalog: HeadlessCommerceAdminCatalogApiHelper;
	readonly headlessCommerceAdminChannel: HeadlessCommerceAdminChannelApiHelper;
	readonly headlessCommerceAdminInventoryApiHelper: HeadlessCommerceAdminInventoryApiHelper;
	readonly headlessCommerceAdminOrder: HeadlessCommerceAdminOrderApiHelper;
	readonly headlessCommerceAdminOrderAttachment: HeadlessCommerceAdminOrderAttachmentApiHelper;
	readonly headlessCommerceAdminPaymentApiHelper: HeadlessCommerceAdminPaymentApiHelper;
	readonly headlessCommerceAdminPricing: HeadlessCommerceAdminPricingApiHelper;
	readonly headlessCommerceAdminShipment: HeadlessCommerceAdminShipmentApiHelper;
	readonly headlessCommerceDeliveryCatalog: HeadlessCommerceDeliveryCatalogApiHelper;
	readonly headlessCommerceDeliveryCart: HeadlessCommerceDeliveryCartApiHelper;
	readonly headlessCommerceReturn: HeadlessCommerceReturnApiHelper;
	readonly headlessDelivery: HeadlessDeliveryApiHelper;
	readonly headlessDigitalSalesRoom: HeadlessDigitalSalesRoomApiHelper;
	readonly headlessPortalInstance: HeadlessPortalInstanceApiHelper;
	readonly jsonWebServicesAnnouncementsEntryApiHelper: JSONWebServicesAnnouncementsEntryApiHelper;
	readonly jsonWebServicesAssetDisplayPageEntry: JSONWebServicesAssetDisplayPageEntryApiHelper;
	readonly jsonWebServicesAssetListEntry: JSONWebServicesAssetListEntryApiHelper;
	readonly jsonWebServicesAudiencesEntry: JSONWebServicesAudiencesEntryApiHelper;
	readonly jsonWebServicesCalendar: JSONWebServicesCalendarApiHelper;
	readonly jsonWebServicesCalendarResource: JSONWebServicesCalendarResourceApiHelper;
	readonly jsonWebServicesClassName: JSONWebServicesClassNameApiHelper;
	readonly jsonWebServicesClientExtension: JSONWebServicesClientExtensionApiHelper;
	readonly jsonWebServicesCompany: JSONWebServicesCompanyApiHelper;
	readonly jsonWebServicesDDM: JSONWebServicesDDMApiHelper;
	readonly jsonWebServicesDepot: JSONWebServicesDepotApiHelper;
	readonly jsonWebServicesDepotGroupRel: JSONWebServicesDepotGroupRelApiHelper;
	readonly jsonWebServicesDocumentLibrary: JSONWebServicesDocumentLibraryApiHelper;
	readonly jsonWebServicesFragmentEntry: JSONWebServicesFragmentEntryApiHelper;
	readonly jsonWebServicesFragmentCollection: JSONWebServicesFragmentCollectionApiHelper;
	readonly jsonWebServicesGroup: JSONWebServicesGroupApiHelper;
	readonly jsonWebServicesJournal: JSONWebServicesJournalApiHelper;
	readonly jsonWebServicesLayout: JSONWebServicesLayoutApiHelper;
	readonly jsonWebServicesLayoutPageTemplateEntry: JSONWebServicesLayoutPageTemplateEntryApiHelper;
	readonly jsonWebServicesLayoutPageTemplateCollection: JSONWebServicesLayoutPageTemplateCollectionApiHelper;
	readonly jsonWebServicesLayoutSet: JSONWebServicesLayoutSetApiHelper;
	readonly jsonWebServicesLayoutSetPrototype: JSONWebServicesLayoutSetPrototypeApiHelper;
	readonly jsonWebServicesMBApiHelper: JSONWebServicesMBApiHelper;
	readonly jsonWebServicesOSBAsah: JSONWebServicesOSBAsahApiHelper;
	readonly jsonWebServicesOSBFaro: JSONWebServicesOSBFaroApiHelper;
	readonly jsonWebServicesPushNotificationsDevice: JSONWebServicesPushNotificationsDeviceApiHelper;
	readonly jsonWebServicesResourcePermissionApiHelper: JSONWebServicesResourcePermissionApiHelper;
	readonly jsonWebServicesRole: JSONWebServicesRoleApiHelper;
	readonly jsonWebServicesSegmentsEntry: JSONWebServicesSegmentsEntryApiHelper;
	readonly jsonWebServicesSiteNavigationMenu: JSONWebServicesSiteNavigationMenuApiHelper;
	readonly jsonWebServicesStaging: JSONWebServicesStagingApiHelper;
	readonly jsonWebServicesTeam: JSONWebServicesTeamApiHelper;
	readonly jsonWebServicesUser: JSONWebServicesUserApiHelper;
	readonly jsonWebServicesUserGroup: JSONWebServicesUserGroupApiHelper;
	readonly language: LanguageApiHelper;
	readonly listTypeAdmin: ListTypeAdminApiHelper;
	readonly notification: NotificationApiHelper;
	readonly objectAction: ObjectActionApiHelper;
	readonly objectAdmin: ObjectAdminApiHelper;
	readonly objectEntry: ObjectEntryApiHelper;
	readonly objectFolder: ObjectEntryFolderApiHelper;
	readonly page: Page;
	readonly scim: SCIMApiHelper;
	readonly searchExperiences: SearchExperiencesApiHelper;
	readonly seoStudio: SEOStudioApiHelper;

	private static readonly _authorization = `Basic ${btoa(
		`test@liferay.com:test`
	)}`;

	constructor(page: Page, baseUrl?: string) {
		this.analyticsSettingsRest = new AnalyticsSettingsRestApiHelper(this);
		this.apiBuilder = new ApiBuilderHelper(this);
		this.baseUrl = baseUrl
			? baseUrl + '/o/'
			: liferayConfig.environment.baseUrl + '/o/';
		this.cookies = new CookiesApiHelper(this);
		this.featureFlag = new FeatureFlagApiHelper(page);
		this.dataEngine = new DataEngineApiHelper(this);
		this.dynamicDataMapping = new DynamicDataMappingApiHelper(this);
		this.headlessAdminAddress = new HeadlessAdminAddressApiHelper(this);
		this.headlessAdminContent = new HeadlessAdminContentApiHelper(this);
		this.headlessAdminSite = new HeadlessAdminSiteApiHelper(this);
		this.headlessAdminTaxonomy = new HeadlessAdminTaxonomyApiHelper(this);
		this.headlessAdminUser = new HeadlessAdminUserApiHelper(this);
		this.headlessAdminWorkflow = new HeadlessAdminWorkflowApiHelper(this);
		this.headlessAssetLibrary = new HeadlessAssetLibraryApiHelper(this);
		this.headlessBatchEngine = new HeadlessBatchEngineApiHelper(this);
		this.headlessChangeTracking = new HeadlessChangeTrackingApiHelper(this);
		this.headlessCommerceAdminAccount =
			new HeadlessCommerceAdminAccountApiHelper(this);
		this.headlessCommerceAdminCatalog =
			new HeadlessCommerceAdminCatalogApiHelper(this);
		this.headlessCommerceAdminChannel =
			new HeadlessCommerceAdminChannelApiHelper(this);
		this.headlessCommerceAdminInventoryApiHelper =
			new HeadlessCommerceAdminInventoryApiHelper(this);
		this.headlessCommerceAdminOrder =
			new HeadlessCommerceAdminOrderApiHelper(this);
		this.headlessCommerceAdminOrderAttachment =
			new HeadlessCommerceAdminOrderAttachmentApiHelper(this);
		this.headlessCommerceAdminPaymentApiHelper =
			new HeadlessCommerceAdminPaymentApiHelper(this);
		this.headlessCommerceAdminPricing =
			new HeadlessCommerceAdminPricingApiHelper(this);
		this.headlessCommerceAdminShipment =
			new HeadlessCommerceAdminShipmentApiHelper(this);
		this.headlessCommerceDeliveryCatalog =
			new HeadlessCommerceDeliveryCatalogApiHelper(this);
		this.headlessCommerceDeliveryCart =
			new HeadlessCommerceDeliveryCartApiHelper(this);
		this.headlessCommerceReturn = new HeadlessCommerceReturnApiHelper(this);
		this.headlessDelivery = new HeadlessDeliveryApiHelper(this);
		this.headlessDigitalSalesRoom = new HeadlessDigitalSalesRoomApiHelper(
			this
		);
		this.headlessPortalInstance = new HeadlessPortalInstanceApiHelper(this);
		this.jsonWebServicesAnnouncementsEntryApiHelper =
			new JSONWebServicesAnnouncementsEntryApiHelper(this);
		this.jsonWebServicesAssetDisplayPageEntry =
			new JSONWebServicesAssetDisplayPageEntryApiHelper(this);
		this.jsonWebServicesAssetListEntry =
			new JSONWebServicesAssetListEntryApiHelper(this);
		this.jsonWebServicesAudiencesEntry =
			new JSONWebServicesAudiencesEntryApiHelper(this);
		this.jsonWebServicesCalendar = new JSONWebServicesCalendarApiHelper(
			this
		);
		this.jsonWebServicesCalendarResource =
			new JSONWebServicesCalendarResourceApiHelper(this);
		this.jsonWebServicesClassName = new JSONWebServicesClassNameApiHelper(
			this
		);
		this.jsonWebServicesClientExtension =
			new JSONWebServicesClientExtensionApiHelper(this);
		this.jsonWebServicesCompany = new JSONWebServicesCompanyApiHelper(this);
		this.jsonWebServicesDDM = new JSONWebServicesDDMApiHelper(this);
		this.jsonWebServicesDepot = new JSONWebServicesDepotApiHelper(this);
		this.jsonWebServicesDepotGroupRel =
			new JSONWebServicesDepotGroupRelApiHelper(this);
		this.jsonWebServicesDocumentLibrary =
			new JSONWebServicesDocumentLibraryApiHelper(this);
		this.jsonWebServicesFragmentEntry =
			new JSONWebServicesFragmentEntryApiHelper(this);
		this.jsonWebServicesFragmentCollection =
			new JSONWebServicesFragmentCollectionApiHelper(this);
		this.jsonWebServicesGroup = new JSONWebServicesGroupApiHelper(this);
		this.jsonWebServicesJournal = new JSONWebServicesJournalApiHelper(this);
		this.jsonWebServicesLayout = new JSONWebServicesLayoutApiHelper(this);
		this.jsonWebServicesLayoutPageTemplateEntry =
			new JSONWebServicesLayoutPageTemplateEntryApiHelper(this);
		this.jsonWebServicesLayoutPageTemplateCollection =
			new JSONWebServicesLayoutPageTemplateCollectionApiHelper(this);
		this.jsonWebServicesLayoutSet = new JSONWebServicesLayoutSetApiHelper(
			this
		);
		this.jsonWebServicesLayoutSetPrototype =
			new JSONWebServicesLayoutSetPrototypeApiHelper(this);
		this.jsonWebServicesMBApiHelper = new JSONWebServicesMBApiHelper(this);
		this.jsonWebServicesOSBFaro = new JSONWebServicesOSBFaroApiHelper(this);
		this.jsonWebServicesOSBAsah = new JSONWebServicesOSBAsahApiHelper(this);
		this.jsonWebServicesPushNotificationsDevice =
			new JSONWebServicesPushNotificationsDeviceApiHelper(this);
		this.jsonWebServicesResourcePermissionApiHelper =
			new JSONWebServicesResourcePermissionApiHelper(this);
		this.jsonWebServicesRole = new JSONWebServicesRoleApiHelper(this);
		this.jsonWebServicesSegmentsEntry =
			new JSONWebServicesSegmentsEntryApiHelper(this);
		this.jsonWebServicesSiteNavigationMenu =
			new JSONWebServicesSiteNavigationMenuApiHelper(this);
		this.jsonWebServicesStaging = new JSONWebServicesStagingApiHelper(this);
		this.jsonWebServicesTeam = new JSONWebServicesTeamApiHelper(this);
		this.jsonWebServicesUser = new JSONWebServicesUserApiHelper(this);
		this.jsonWebServicesUserGroup = new JSONWebServicesUserGroupApiHelper(
			this
		);
		this.language = new LanguageApiHelper(this);
		this.listTypeAdmin = new ListTypeAdminApiHelper(this);
		this.notification = new NotificationApiHelper(this);
		this.objectAction = new ObjectActionApiHelper(this);
		this.objectAdmin = new ObjectAdminApiHelper(this);
		this.objectEntry = new ObjectEntryApiHelper(this);
		this.objectFolder = new ObjectEntryFolderApiHelper(this);
		this.page = page;
		this.scim = new SCIMApiHelper(this);
		this.searchExperiences = new SearchExperiencesApiHelper(this);
		this.seoStudio = new SEOStudioApiHelper(this);
	}

	async buildRestClient<
		T extends new (
			baseUrl: string
		) => InstanceType<T> & HeadlessClientWithHeaders,
	>(ApiClientClass: T): Promise<InstanceType<T>> {
		const apiInstance = new ApiClientClass(
			liferayConfig.environment.baseUrl + '/o'
		);

		apiInstance.defaultHeaders = {
			Cookie: `JSESSIONID=${await this.getJSessionId()};`,
			...(await getCSRFTokenHeader(this.page)),
		};

		return apiInstance;
	}

	private async _sendRequest(
		method: 'delete' | 'get' | 'patch' | 'post' | 'put',
		url: string,
		options: {[key: string]: unknown} = {},
		headers?: {[key: string]: string},
		extraHeaders?: {[key: string]: string}
	) {
		const buildHeaders = async () =>
			headers || {
				...(await getHeader(this.page)),
				...(extraHeaders || {}),
			};

		const response = await this.page.request[method](url, {
			...options,
			headers: await buildHeaders(),
		});

		if (headers || response.status() !== 403) {
			return response;
		}

		clearAuthToken(this.page);

		return await this.page.request[method](url, {
			...options,
			headers: await buildHeaders(),
		});
	}

	async postResponse<T>(
		url: string,
		{data, failOnStatusCode, headers, multipart}: RequestOptions<T> = {}
	) {
		return await this._sendRequest(
			'post',
			url,
			{data, failOnStatusCode: failOnStatusCode || false, multipart},
			headers
		);
	}

	async post<T>(url: string, options: RequestOptions<T> = {}) {
		const response = await this.postResponse(url, options);
		const status = response.status();

		if (!response.ok()) {
			const error = await response.text();

			throw new Error(
				`POST request to ${url} failed with code ${status}:\n\n${error}`
			);
		}

		if (status === 204) {
			return;
		}

		return response.json();
	}

	async getResponse(
		url: string,
		failOnStatusCode?: boolean,
		headers?: {[key: string]: string}
	) {
		return await this._sendRequest(
			'get',
			url,
			{failOnStatusCode: failOnStatusCode || false},
			headers
		);
	}

	async put<T>(url: string, options: RequestOptions<T> = {}) {
		const response = await this.putResponse(url, options);

		if (response.status() === 204) {
			return;
		}

		return response.json();
	}

	async putResponse<T>(
		url: string,
		{data, failOnStatusCode, headers, multipart}: RequestOptions<T> = {}
	) {
		return await this._sendRequest(
			'put',
			url,
			{data, failOnStatusCode: failOnStatusCode || false, multipart},
			headers
		);
	}

	async delete<T>(
		url: string,
		{data, failOnStatusCode, headers}: RequestOptions<T> = {}
	) {
		return this._sendRequest(
			'delete',
			url,
			{data, failOnStatusCode: failOnStatusCode || false},
			undefined,
			headers
		);
	}

	async get(
		url: string,
		failOnStatusCode?: boolean,
		headers?: {[key: string]: string}
	) {
		const response = await this.getResponse(url, failOnStatusCode, headers);

		return response.json();
	}

	async patch(url: string, data: DataObject) {
		const response = await this._sendRequest('patch', url, {data});

		const text = await response.text();

		if (!text) {
			return response;
		}

		return response.json();
	}

	async patchRequestOptions<T>(url: string, options: RequestOptions<T> = {}) {
		const response = await this._sendRequest(
			'patch',
			url,
			{
				data: options.data,
				failOnStatusCode: options.failOnStatusCode || false,
				multipart: options.multipart,
			},
			options.headers
		);

		const text = await response.text();

		if (!text) {
			return response;
		}

		return response.json();
	}

	async getJSONWebServicesHeaders() {
		return {
			'Authorization': ApiHelpers._authorization,
			'Content-Type': 'application/x-www-form-urlencoded',
			...(await getCSRFTokenHeader(this.page)),
		};
	}

	async getJSessionId() {
		const cookies = await this.page.context().cookies();

		return cookies.find((cookie) => cookie.name === 'JSESSIONID').value;
	}

	async getCSRFTokenHeader() {
		return getCSRFTokenHeader(this.page);
	}

	getAuthorizationHeader() {
		return ApiHelpers._authorization;
	}
}

export class DataApiHelpers extends ApiHelpers {
	readonly data: TDataApiHelpersData[];

	constructor(page: Page, baseUrl?: string) {
		super(page, baseUrl);

		this.data = [];
	}

	async clearData() {
		for await (const item of this.data.reverse()) {
			if (item.type === 'account') {
				await this.headlessAdminUser.deleteAccount(item.id);
			}
			else if (item.type === 'accountGroup') {
				await this.headlessAdminUser.deleteAccountGroup(item.id);
			}
			else if (item.type === 'address') {
				await this.headlessAdminUser.deletePostalAddress(item.id);
			}
			else if (item.type === 'announcement') {
				await this.jsonWebServicesAnnouncementsEntryApiHelper.deleteEntry(
					item.id
				);
			}
			else if (item.type === 'apiApplication') {
				await this.apiBuilder.deleteApiApplication(item.id);
			}
			else if (item.type === 'assetLibrary') {
				await this.headlessAssetLibrary.deleteAssetLibrary(item.id);
			}
			else if (item.type === 'audiencesEntry') {
				await this.jsonWebServicesAudiencesEntry.deleteAudiencesEntry(
					item.id
				);
			}
			else if (item.type === 'catalog') {
				await this.headlessCommerceAdminCatalog.deleteCatalog(item.id);
			}
			else if (item.type === 'channel') {
				await this.headlessCommerceAdminChannel.deleteChannel(item.id);
			}
			else if (item.type === 'commerceReturn') {
				await this.headlessCommerceReturn.deleteCommerceReturn(item.id);
			}
			else if (item.type === 'ctCollection') {
				await this.headlessChangeTracking.deleteCTCollection(item.id);
			}
			else if (item.type === 'currency') {
				await this.headlessCommerceAdminCatalog.deleteCurrency(item.id);
			}
			else if (item.type === 'discount') {
				await this.headlessCommerceAdminPricing.deleteDiscount(item.id);
			}
			else if (item.type === 'document') {
				await this.headlessDelivery.deleteDocument(item.id);
			}
			else if (item.type === 'documentDataDefinitionType') {
				await this.headlessDelivery.deleteDocumentDataDefinitionType(
					item.id
				);
			}
			else if (item.type === 'documentFolder') {
				await this.headlessDelivery.deleteDocumentFolder(item.id);
			}
			else if (item.type === 'keyword') {
				await this.headlessAdminTaxonomy.deleteKeyword({
					id: item.id,
				});
			}
			else if (item.type === 'layoutSetPrototype') {
				await this.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
					item.id
				);
			}
			else if (item.type === 'listTypeDefinition') {
				await this.listTypeAdmin.deleteListTypeDefinition(item.id);
			}
			else if (item.type === 'navigationMenu') {
				const [
					siteExternalReferenceCode,
					navigationMenuExternalReferenceCode,
				] = item.id.split('|');

				await this.headlessAdminSite.deleteSiteNavigationMenu(
					siteExternalReferenceCode,
					navigationMenuExternalReferenceCode
				);
			}
			else if (item.type === 'notificationQueueEntry') {
				await this.notification.deleteNotificationQueueEntry(item.id);
			}
			else if (item.type === 'notificationTemplate') {
				await this.notification.deleteNotificationTemplate(item.id);
			}
			else if (item.type === 'objectAction') {
				const objectActionAPIClient =
					await this.buildRestClient(ObjectActionAPI);
				await objectActionAPIClient.deleteObjectAction(item.id);
			}
			else if (item.type === 'objectDefinition') {
				await this.deleteObjectDefinition(item.id);
			}
			else if (item.type === 'objectEntry') {
				await this.objectEntry.deleteObjectEntry(
					item.applicationName!,
					item.id
				);
			}
			else if (item.type === 'objectFolder') {
				const objectFolderRESTClient =
					await this.buildRestClient(ObjectFolderAPI);
				await objectFolderRESTClient.deleteObjectFolder(item.id);
			}
			else if (item.type === 'objectRelationship') {
				const objectRelationshipRESTClient = await this.buildRestClient(
					ObjectRelationshipAPI
				);
				await objectRelationshipRESTClient.deleteObjectRelationship(
					item.id
				);
			}
			else if (item.type === 'option') {
				await this.headlessCommerceAdminCatalog.deleteOption(item.id);
			}
			else if (item.type === 'optionCategory') {
				await this.headlessCommerceAdminCatalog.deleteOptionCategory(
					item.id
				);
			}
			else if (item.type === 'order') {
				await this.headlessCommerceAdminOrder.deleteOrder(item.id);
			}
			else if (item.type === 'orderAttachment') {
				const [orderId, attachmentId] = String(item.id)
					.split('_')
					.map(Number);

				await this.headlessCommerceAdminOrderAttachment.deleteOrderAttachment(
					attachmentId,
					orderId
				);
			}
			else if (item.type === 'orderRule') {
				await this.headlessCommerceAdminOrder.deleteOrderRules(item.id);
			}
			else if (item.type === 'orderType') {
				await this.headlessCommerceAdminOrder.deleteOrderTypes(item.id);
			}
			else if (item.type === 'organization') {
				await this.headlessAdminUser.deleteOrganization(item.id);
			}
			else if (item.type === 'organizationUserAccountAssociation') {
				const [organizationId, emailAddress] = item.id.split('_');
				await this.headlessAdminUser.deleteOrganizationUserAccountAssociation(
					organizationId,
					emailAddress
				);
			}
			else if (item.type === 'payment') {
				await this.headlessCommerceAdminPaymentApiHelper.deletePayment(
					item.id
				);
			}
			else if (item.type === 'pin') {
				await this.headlessCommerceAdminCatalog.deletePin(item.id);
			}
			else if (item.type === 'price-entry') {
				await this.headlessCommerceAdminPricing.deletePriceEntry(
					item.id
				);
			}
			else if (item.type === 'price-list') {
				await this.headlessCommerceAdminPricing.deletePriceList(
					item.id
				);
			}
			else if (item.type === 'product') {
				await this.headlessCommerceAdminCatalog.deleteProduct(item.id);
			}
			else if (item.type === 'productGroup') {
				await this.headlessCommerceAdminCatalog.deleteProductGroup(
					item.id
				);
			}
			else if (item.type === 'productConfiguration') {
				await this.headlessCommerceAdminCatalog.deleteProductConfiguration(
					item.id
				);
			}
			else if (item.type === 'productConfigurationList') {
				await this.headlessCommerceAdminCatalog.deleteProductConfigurationList(
					item.id
				);
			}
			else if (item.type === 'pushNotificationsDevice') {
				await this.jsonWebServicesPushNotificationsDevice.deletePushNotificationsDevice(
					item.id
				);
			}
			else if (item.type === 'relatedProduct') {
				await this.headlessCommerceAdminCatalog.deleteRelatedProduct(
					item.id
				);
			}
			else if (item.type === 'role') {
				await this.headlessAdminUser.deleteRole(item.id);
			}
			else if (item.type === 'roleUserAccountAssociation') {
				const [roleId, userId] = item.id.split('_');
				await this.headlessAdminUser.deleteRoleUserAccountAssociation(
					roleId,
					userId
				);
			}
			else if (item.type === 'shipment') {
				await this.headlessCommerceAdminShipment.deleteShipment(
					item.id
				);
			}
			else if (item.type === 'site') {
				await this.headlessAdminSite.deleteSite(item.id);
			}
			else if (item.type === 'skuUnitOfMeasure') {
				await this.headlessCommerceAdminCatalog.deleteSkuUnitOfMeasure(
					item.id
				);
			}
			else if (item.type === 'specification') {
				await this.headlessCommerceAdminCatalog.deleteSpecification(
					item.id
				);
			}
			else if (item.type === 'sxpBlueprint') {
				await this.searchExperiences.deleteSXPBlueprint(item.id);
			}
			else if (item.type === 'sxpElement') {
				await this.searchExperiences.deleteSXPElement(item.id);
			}
			else if (item.type === 'taxonomyVocabulary') {
				await this.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
					item.id
				);
			}
			else if (item.type === 'terms') {
				await this.headlessCommerceAdminOrder.deleteTerms(item.id);
			}
			else if (item.type === 'userAccount') {
				await this.headlessAdminUser.deleteUserAccount(item.id);
			}
			else if (item.type === 'userGroup') {
				await this.headlessAdminUser.deleteUserGroup(item.id);
			}
			else if (item.type === 'userGroupUserAccountAssociation') {
				const [userGroupId, ...userIds] = item.id.split('_');
				await this.headlessAdminUser.deleteUserGroupUsers(
					userGroupId,
					userIds
				);
			}
			else if (item.type === 'virtual-instance') {
				await this.headlessPortalInstance.deleteVirtualInstance(
					item.id
				);
			}
			else if (item.type === 'warehouse') {
				await this.headlessCommerceAdminInventoryApiHelper.deleteWarehouse(
					item.id
				);
			}
			else if (item.type === 'warehouse-item') {
				await this.headlessCommerceAdminInventoryApiHelper.deleteWarehouseItem(
					item.id
				);
			}
			else if (item.type === 'webContent') {
				const [siteId, articleId] = item.id.split('_');
				await this.jsonWebServicesJournal.moveArticleToTrash(
					siteId,
					articleId
				);
			}
			else if (item.type === 'wishList') {
				await this.headlessCommerceDeliveryCatalog.deleteWishList(
					item.id
				);
			}
			else if (item.type === 'workflowDefinition') {
				await this.headlessAdminWorkflow.deleteWorkflowDefinition(
					item.id
				);
			}
		}
	}

	async collectObjectDefinitionIds(
		objectDefinitionId: number,
		objectDefinitionIds: number[],
		visitedObjectDefinitionIds: Set<number>
	) {
		if (visitedObjectDefinitionIds.has(objectDefinitionId)) {
			return;
		}

		visitedObjectDefinitionIds.add(objectDefinitionId);

		const objectDefinitionAPIClient =
			await this.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.getObjectDefinition(
				objectDefinitionId
			);

		const objectRelationshipAPIClient = await this.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationships} =
			await objectRelationshipAPIClient.getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
				objectDefinition.externalReferenceCode
			);

		const isCMSObjectDefinition =
			objectDefinition.objectFolderExternalReferenceCode?.startsWith(
				'L_CMS'
			);

		for (const objectRelationship of objectRelationships.items) {
			if (!objectRelationship.edge) {
				continue;
			}

			if (
				isCMSObjectDefinition &&
				objectRelationship.objectDefinitionId2
			) {
				const {body: relatedObjectDefinition} =
					await objectDefinitionAPIClient.getObjectDefinition(
						objectRelationship.objectDefinitionId2
					);

				if (
					relatedObjectDefinition.objectFolderExternalReferenceCode ===
					'L_CMS_STRUCTURE_REPEATABLE_GROUPS'
				) {
					await this.collectObjectDefinitionIds(
						objectRelationship.objectDefinitionId2,
						objectDefinitionIds,
						visitedObjectDefinitionIds
					);
				}
			}

			await objectRelationshipAPIClient.putObjectRelationship(
				objectRelationship.id,
				{
					...objectRelationship,
					edge: false,
				}
			);
		}

		objectDefinitionIds.push(objectDefinitionId);
	}

	async deleteObjectDefinition(objectDefinitionId: number) {
		const objectDefinitionAPIClient =
			await this.buildRestClient(ObjectDefinitionAPI);

		const objectDefinitionIds: number[] = [];

		await this.collectObjectDefinitionIds(
			objectDefinitionId,
			objectDefinitionIds,
			new Set()
		);

		for (const id of objectDefinitionIds) {
			await objectDefinitionAPIClient.deleteObjectDefinition(id);
		}
	}

	setData(data: TDataApiHelpersData[]) {
		this.data.length = 0;

		while (data.length) {
			this.data.unshift(data.pop());
		}
	}
}
