/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {PageTaxonomyCategory} from '../models/PageTaxonomyCategory';
		import {TaxonomyCategory} from '../models/TaxonomyCategory';

/**
 * @author Javier Gamarra
 * @generated
 */

export class TaxonomyCategoryAPI {
	protected _basePath: string;
	protected _defaultHeaders: any = {};

	constructor(basePath?: string) {
		if (basePath) {
			this._basePath = basePath;
		}
	}

	set defaultHeaders(defaultHeaders: any) {
		this._defaultHeaders = defaultHeaders;
	}

		/**
		 * Deletes the asset library's taxonomy category by external reference code.
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "DELETE",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Deletes the site's taxonomy category by external reference code.
				 * @param siteId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteTaxonomyCategoryByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling deleteSiteTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSiteTaxonomyCategoryByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "DELETE",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Deletes the taxonomy category and returns a 204 if the operation succeeds.
				 * @param taxonomyCategoryId
		 * @param headers Optional custom request headers
		 */
		public async deleteTaxonomyCategory(
						taxonomyCategoryId: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}"
						.replace("{taxonomyCategoryId}",encodeURIComponent(taxonomyCategoryId))
				;

			const queryParameters: any = {};

						if (taxonomyCategoryId === null || taxonomyCategoryId === undefined) {
							throw new Error("Required parameter taxonomyCategoryId was null or undefined when calling deleteTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "DELETE",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Deletes the site's taxonomy category by external reference code.
				 * @param taxonomyVocabularyId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
						taxonomyVocabularyId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "DELETE",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves the asset library's taxonomy categories.
				 * @param assetLibraryId
				 * @param aggregationTerms
				 * @param fields
				 * @param filter
				 * @param page
				 * @param pageSize
				 * @param restrictFields
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryTaxonomyCategoriesPage(
						assetLibraryId: number,
						aggregationTerms?: Array<string>,
						fields?: string,
						filter?: string,
						page?: number,
						pageSize?: number,
						restrictFields?: string,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageTaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
																																				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryTaxonomyCategoriesPage.");
						}

						if (aggregationTerms !== undefined) {
							queryParameters["aggregationTerms"] = ObjectSerializer.serialize(aggregationTerms, "Array<string>");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (filter !== undefined) {
							queryParameters["filter"] = ObjectSerializer.serialize(filter, "string");
						}

						if (page !== undefined) {
							queryParameters["page"] = ObjectSerializer.serialize(page, "number");
						}

						if (pageSize !== undefined) {
							queryParameters["pageSize"] = ObjectSerializer.serialize(pageSize, "number");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

						if (search !== undefined) {
							queryParameters["search"] = ObjectSerializer.serialize(search, "string");
						}

						if (sort !== undefined) {
							queryParameters["sort"] = ObjectSerializer.serialize(sort, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves the asset library's taxonomy category by external reference code.
				 * @param assetLibraryId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getAssetLibraryTaxonomyCategoryByExternalReferenceCode.");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves the site's taxonomy categories.
				 * @param siteId
				 * @param aggregationTerms
				 * @param fields
				 * @param filter
				 * @param page
				 * @param pageSize
				 * @param restrictFields
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getSiteTaxonomyCategoriesPage(
						siteId: number,
						aggregationTerms?: Array<string>,
						fields?: string,
						filter?: string,
						page?: number,
						pageSize?: number,
						restrictFields?: string,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageTaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories"
						.replace("{siteId}",encodeURIComponent(siteId))
																																				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteTaxonomyCategoriesPage.");
						}

						if (aggregationTerms !== undefined) {
							queryParameters["aggregationTerms"] = ObjectSerializer.serialize(aggregationTerms, "Array<string>");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (filter !== undefined) {
							queryParameters["filter"] = ObjectSerializer.serialize(filter, "string");
						}

						if (page !== undefined) {
							queryParameters["page"] = ObjectSerializer.serialize(page, "number");
						}

						if (pageSize !== undefined) {
							queryParameters["pageSize"] = ObjectSerializer.serialize(pageSize, "number");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

						if (search !== undefined) {
							queryParameters["search"] = ObjectSerializer.serialize(search, "string");
						}

						if (sort !== undefined) {
							queryParameters["sort"] = ObjectSerializer.serialize(sort, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves the site's taxonomy category by external reference code.
				 * @param siteId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getSiteTaxonomyCategoryByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getSiteTaxonomyCategoryByExternalReferenceCode.");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * 
				 * @param fields
				 * @param page
				 * @param pageSize
				 * @param restrictFields
				 * @param siteId
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyCategoriesRankedPage(
						fields?: string,
						page?: number,
						pageSize?: number,
						restrictFields?: string,
						siteId?: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageTaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/ranked"
																				;

			const queryParameters: any = {};

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (page !== undefined) {
							queryParameters["page"] = ObjectSerializer.serialize(page, "number");
						}

						if (pageSize !== undefined) {
							queryParameters["pageSize"] = ObjectSerializer.serialize(pageSize, "number");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

						if (siteId !== undefined) {
							queryParameters["siteId"] = ObjectSerializer.serialize(siteId, "number");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves a taxonomy category.
				 * @param taxonomyCategoryId
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyCategory(
						taxonomyCategoryId: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}"
						.replace("{taxonomyCategoryId}",encodeURIComponent(taxonomyCategoryId))
												;

			const queryParameters: any = {};

						if (taxonomyCategoryId === null || taxonomyCategoryId === undefined) {
							throw new Error("Required parameter taxonomyCategoryId was null or undefined when calling getTaxonomyCategory.");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * 
				 * @param taxonomyCategoryId
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyCategoryPermissionsPage(
						taxonomyCategoryId: string,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}/permissions"
						.replace("{taxonomyCategoryId}",encodeURIComponent(taxonomyCategoryId))
																;

			const queryParameters: any = {};

						if (taxonomyCategoryId === null || taxonomyCategoryId === undefined) {
							throw new Error("Required parameter taxonomyCategoryId was null or undefined when calling getTaxonomyCategoryPermissionsPage.");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

						if (roleNames !== undefined) {
							queryParameters["roleNames"] = ObjectSerializer.serialize(roleNames, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves a taxonomy category's child taxonomy categories. Results can be paginated, filtered, searched, and sorted.
				 * @param parentTaxonomyCategoryId
				 * @param aggregationTerms
				 * @param fields
				 * @param filter
				 * @param page
				 * @param pageSize
				 * @param restrictFields
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId: string,
						aggregationTerms?: Array<string>,
						fields?: string,
						filter?: string,
						page?: number,
						pageSize?: number,
						restrictFields?: string,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageTaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{parentTaxonomyCategoryId}/taxonomy-categories"
						.replace("{parentTaxonomyCategoryId}",encodeURIComponent(parentTaxonomyCategoryId))
																																				;

			const queryParameters: any = {};

						if (parentTaxonomyCategoryId === null || parentTaxonomyCategoryId === undefined) {
							throw new Error("Required parameter parentTaxonomyCategoryId was null or undefined when calling getTaxonomyCategoryTaxonomyCategoriesPage.");
						}

						if (aggregationTerms !== undefined) {
							queryParameters["aggregationTerms"] = ObjectSerializer.serialize(aggregationTerms, "Array<string>");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (filter !== undefined) {
							queryParameters["filter"] = ObjectSerializer.serialize(filter, "string");
						}

						if (page !== undefined) {
							queryParameters["page"] = ObjectSerializer.serialize(page, "number");
						}

						if (pageSize !== undefined) {
							queryParameters["pageSize"] = ObjectSerializer.serialize(pageSize, "number");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

						if (search !== undefined) {
							queryParameters["search"] = ObjectSerializer.serialize(search, "string");
						}

						if (sort !== undefined) {
							queryParameters["sort"] = ObjectSerializer.serialize(sort, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves a vocabulary's taxonomy categories. Results can be paginated, filtered, searched, and sorted.
				 * @param taxonomyVocabularyId
				 * @param aggregationTerms
				 * @param fields
				 * @param filter
				 * @param flatten
				 * @param page
				 * @param pageSize
				 * @param restrictFields
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId: number,
						aggregationTerms?: Array<string>,
						fields?: string,
						filter?: string,
						flatten?: boolean,
						page?: number,
						pageSize?: number,
						restrictFields?: string,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageTaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
																																								;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling getTaxonomyVocabularyTaxonomyCategoriesPage.");
						}

						if (aggregationTerms !== undefined) {
							queryParameters["aggregationTerms"] = ObjectSerializer.serialize(aggregationTerms, "Array<string>");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (filter !== undefined) {
							queryParameters["filter"] = ObjectSerializer.serialize(filter, "string");
						}

						if (flatten !== undefined) {
							queryParameters["flatten"] = ObjectSerializer.serialize(flatten, "boolean");
						}

						if (page !== undefined) {
							queryParameters["page"] = ObjectSerializer.serialize(page, "number");
						}

						if (pageSize !== undefined) {
							queryParameters["pageSize"] = ObjectSerializer.serialize(pageSize, "number");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

						if (search !== undefined) {
							queryParameters["search"] = ObjectSerializer.serialize(search, "string");
						}

						if (sort !== undefined) {
							queryParameters["sort"] = ObjectSerializer.serialize(sort, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Retrieves the site's taxonomy category by external reference code.
				 * @param taxonomyVocabularyId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
						taxonomyVocabularyId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode.");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Updates only the fields received in the request body. Other fields are left untouched.
				 * @param taxonomyCategoryId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchTaxonomyCategoryWithContentType(
						taxonomyCategoryId: string,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}"
						.replace("{taxonomyCategoryId}",encodeURIComponent(taxonomyCategoryId))
				;

			const queryParameters: any = {};

						if (taxonomyCategoryId === null || taxonomyCategoryId === undefined) {
							throw new Error("Required parameter taxonomyCategoryId was null or undefined when calling patchTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "PATCH",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Updates only the fields received in the request body. Other fields are left untouched. - Default method for JSON body
							 * @param taxonomyCategoryId
						 * @param taxonomyCategory
					 */
					public async patchTaxonomyCategory(
									taxonomyCategoryId: string,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.patchTaxonomyCategoryWithContentType(
										taxonomyCategoryId,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Inserts a new Category in a Scope.
				 * @param assetLibraryId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postAssetLibraryTaxonomyCategoryWithContentType(
						assetLibraryId: number,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling postAssetLibraryTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "POST",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Inserts a new Category in a Scope. - Default method for JSON body
							 * @param assetLibraryId
						 * @param taxonomyCategory
					 */
					public async postAssetLibraryTaxonomyCategory(
									assetLibraryId: number,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.postAssetLibraryTaxonomyCategoryWithContentType(
										assetLibraryId,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Inserts a new Category in a Scope.
				 * @param siteId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSiteTaxonomyCategoryWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling postSiteTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "POST",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Inserts a new Category in a Scope. - Default method for JSON body
							 * @param siteId
						 * @param taxonomyCategory
					 */
					public async postSiteTaxonomyCategory(
									siteId: number,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.postSiteTaxonomyCategoryWithContentType(
										siteId,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Inserts a new child taxonomy category.
				 * @param parentTaxonomyCategoryId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postTaxonomyCategoryTaxonomyCategoryWithContentType(
						parentTaxonomyCategoryId: string,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{parentTaxonomyCategoryId}/taxonomy-categories"
						.replace("{parentTaxonomyCategoryId}",encodeURIComponent(parentTaxonomyCategoryId))
				;

			const queryParameters: any = {};

						if (parentTaxonomyCategoryId === null || parentTaxonomyCategoryId === undefined) {
							throw new Error("Required parameter parentTaxonomyCategoryId was null or undefined when calling postTaxonomyCategoryTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "POST",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Inserts a new child taxonomy category. - Default method for JSON body
							 * @param parentTaxonomyCategoryId
						 * @param taxonomyCategory
					 */
					public async postTaxonomyCategoryTaxonomyCategory(
									parentTaxonomyCategoryId: string,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.postTaxonomyCategoryTaxonomyCategoryWithContentType(
										parentTaxonomyCategoryId,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Inserts a new taxonomy category in a taxonomy vocabulary.
				 * @param taxonomyVocabularyId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postTaxonomyVocabularyTaxonomyCategoryWithContentType(
						taxonomyVocabularyId: number,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling postTaxonomyVocabularyTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "POST",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Inserts a new taxonomy category in a taxonomy vocabulary. - Default method for JSON body
							 * @param taxonomyVocabularyId
						 * @param taxonomyCategory
					 */
					public async postTaxonomyVocabularyTaxonomyCategory(
									taxonomyVocabularyId: number,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.postTaxonomyVocabularyTaxonomyCategoryWithContentType(
										taxonomyVocabularyId,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Updates the asset library's taxonomy category with the given external reference code, or creates it if it not exists.
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryTaxonomyCategoryByExternalReferenceCodeWithContentType(
						assetLibraryId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling putAssetLibraryTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putAssetLibraryTaxonomyCategoryByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "PUT",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Updates the asset library's taxonomy category with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param assetLibraryId
							 * @param externalReferenceCode
						 * @param taxonomyCategory
					 */
					public async putAssetLibraryTaxonomyCategoryByExternalReferenceCode(
									assetLibraryId: number,
									externalReferenceCode: string,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.putAssetLibraryTaxonomyCategoryByExternalReferenceCodeWithContentType(
										assetLibraryId,
										externalReferenceCode,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Updates the site's taxonomy category with the given external reference code, or creates it if it not exists.
				 * @param siteId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteTaxonomyCategoryByExternalReferenceCodeWithContentType(
						siteId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putSiteTaxonomyCategoryByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "PUT",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Updates the site's taxonomy category with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param siteId
							 * @param externalReferenceCode
						 * @param taxonomyCategory
					 */
					public async putSiteTaxonomyCategoryByExternalReferenceCode(
									siteId: number,
									externalReferenceCode: string,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.putSiteTaxonomyCategoryByExternalReferenceCodeWithContentType(
										siteId,
										externalReferenceCode,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Replaces the taxonomy category with the information sent in the request body. Any missing fields are deleted unless they are required.
				 * @param taxonomyCategoryId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putTaxonomyCategoryWithContentType(
						taxonomyCategoryId: string,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}"
						.replace("{taxonomyCategoryId}",encodeURIComponent(taxonomyCategoryId))
				;

			const queryParameters: any = {};

						if (taxonomyCategoryId === null || taxonomyCategoryId === undefined) {
							throw new Error("Required parameter taxonomyCategoryId was null or undefined when calling putTaxonomyCategory.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "PUT",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Replaces the taxonomy category with the information sent in the request body. Any missing fields are deleted unless they are required. - Default method for JSON body
							 * @param taxonomyCategoryId
						 * @param taxonomyCategory
					 */
					public async putTaxonomyCategory(
									taxonomyCategoryId: string,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.putTaxonomyCategoryWithContentType(
										taxonomyCategoryId,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param taxonomyCategoryId
		 * @param headers Optional custom request headers
		 */
		public async putTaxonomyCategoryPermissionsPage(
						taxonomyCategoryId: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}/permissions"
						.replace("{taxonomyCategoryId}",encodeURIComponent(taxonomyCategoryId))
				;

			const queryParameters: any = {};

						if (taxonomyCategoryId === null || taxonomyCategoryId === undefined) {
							throw new Error("Required parameter taxonomyCategoryId was null or undefined when calling putTaxonomyCategoryPermissionsPage.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
					,headers || {}
					),
				method: "PUT",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		/**
		 * Updates the site's taxonomy category with the given external reference code, or creates it if it not exists.
				 * @param taxonomyVocabularyId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeWithContentType(
						taxonomyVocabularyId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyCategory?: TaxonomyCategory
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyCategory;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyCategory, "TaxonomyCategory"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "application/json"
						}
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "PUT",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyCategory"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

					/**
					 * Updates the site's taxonomy category with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param taxonomyVocabularyId
							 * @param externalReferenceCode
						 * @param taxonomyCategory
					 */
					public async putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
									taxonomyVocabularyId: number,
									externalReferenceCode: string,
							taxonomyCategory?: TaxonomyCategory,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyCategory;
						response: Response;
					}> {
						return this.putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeWithContentType(
										taxonomyVocabularyId,
										externalReferenceCode,
							{
								parameters: {
										taxonomyCategory: taxonomyCategory
								},
								type: "application/json"
							},
							headers
						);
					}
}