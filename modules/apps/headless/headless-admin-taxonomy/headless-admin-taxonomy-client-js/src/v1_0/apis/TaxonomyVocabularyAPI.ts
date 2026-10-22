/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {PageTaxonomyVocabulary} from '../models/PageTaxonomyVocabulary';
		import {TaxonomyVocabulary} from '../models/TaxonomyVocabulary';

/**
 * @author Javier Gamarra
 * @generated
 */

export class TaxonomyVocabularyAPI {
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
		 * Deletes the asset library's taxonomy vocabulary by external reference code.
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode.");
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
		 * Deletes the site's taxonomy vocabulary by external reference code.
				 * @param siteId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteTaxonomyVocabularyByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling deleteSiteTaxonomyVocabularyByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSiteTaxonomyVocabularyByExternalReferenceCode.");
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
		 * Deletes the taxonomy vocabulary and returns a 204 if the operation succeeds.
				 * @param taxonomyVocabularyId
		 * @param headers Optional custom request headers
		 */
		public async deleteTaxonomyVocabulary(
						taxonomyVocabularyId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling deleteTaxonomyVocabulary.");
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
		 * 
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
		public async getAssetLibraryTaxonomyVocabulariesPage(
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
				body: PageTaxonomyVocabulary;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
																																				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryTaxonomyVocabulariesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyVocabulary"), response};
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
		 * Retrieves the asset library's taxonomy vocabulary by external reference code.
				 * @param assetLibraryId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryTaxonomyVocabularyByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getAssetLibraryTaxonomyVocabularyByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
				 * @param assetLibraryId
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryTaxonomyVocabularyPermissionsPage(
						assetLibraryId: number,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies/permissions"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
																;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryTaxonomyVocabularyPermissionsPage.");
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
		 * Retrieves a Site's taxonomy vocabularies. Results can be paginated, filtered, searched, and sorted.
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
		public async getSiteTaxonomyVocabulariesPage(
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
				body: PageTaxonomyVocabulary;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies"
						.replace("{siteId}",encodeURIComponent(siteId))
																																				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteTaxonomyVocabulariesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTaxonomyVocabulary"), response};
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
		 * Retrieves the site's taxonomy vocabulary by external reference code.
				 * @param siteId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getSiteTaxonomyVocabularyByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteTaxonomyVocabularyByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getSiteTaxonomyVocabularyByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
				 * @param siteId
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getSiteTaxonomyVocabularyPermissionsPage(
						siteId: number,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies/permissions"
						.replace("{siteId}",encodeURIComponent(siteId))
																;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteTaxonomyVocabularyPermissionsPage.");
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
		 * Retrieves a taxonomy vocabulary.
				 * @param taxonomyVocabularyId
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyVocabulary(
						taxonomyVocabularyId: number,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
												;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling getTaxonomyVocabulary.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
				 * @param taxonomyVocabularyId
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getTaxonomyVocabularyPermissionsPage(
						taxonomyVocabularyId: number,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/permissions"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
																;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling getTaxonomyVocabularyPermissionsPage.");
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
		 * Updates only the fields received in the request body. Any other fields are left untouched.
				 * @param taxonomyVocabularyId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchTaxonomyVocabularyWithContentType(
						taxonomyVocabularyId: number,
					requestBody:
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling patchTaxonomyVocabulary.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
					 * Updates only the fields received in the request body. Any other fields are left untouched. - Default method for JSON body
							 * @param taxonomyVocabularyId
						 * @param taxonomyVocabulary
					 */
					public async patchTaxonomyVocabulary(
									taxonomyVocabularyId: number,
							taxonomyVocabulary?: TaxonomyVocabulary,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyVocabulary;
						response: Response;
					}> {
						return this.patchTaxonomyVocabularyWithContentType(
										taxonomyVocabularyId,
							{
								parameters: {
										taxonomyVocabulary: taxonomyVocabulary
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postAssetLibraryTaxonomyVocabularyWithContentType(
						assetLibraryId: number,
					requestBody:
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling postAssetLibraryTaxonomyVocabulary.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
					 *  - Default method for JSON body
							 * @param assetLibraryId
						 * @param taxonomyVocabulary
					 */
					public async postAssetLibraryTaxonomyVocabulary(
									assetLibraryId: number,
							taxonomyVocabulary?: TaxonomyVocabulary,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyVocabulary;
						response: Response;
					}> {
						return this.postAssetLibraryTaxonomyVocabularyWithContentType(
										assetLibraryId,
							{
								parameters: {
										taxonomyVocabulary: taxonomyVocabulary
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Inserts a new taxonomy vocabulary in a Site.
				 * @param siteId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSiteTaxonomyVocabularyWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling postSiteTaxonomyVocabulary.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
					 * Inserts a new taxonomy vocabulary in a Site. - Default method for JSON body
							 * @param siteId
						 * @param taxonomyVocabulary
					 */
					public async postSiteTaxonomyVocabulary(
									siteId: number,
							taxonomyVocabulary?: TaxonomyVocabulary,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyVocabulary;
						response: Response;
					}> {
						return this.postSiteTaxonomyVocabularyWithContentType(
										siteId,
							{
								parameters: {
										taxonomyVocabulary: taxonomyVocabulary
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Updates the asset library's taxonomy vocabulary with the given external reference code, or creates it if it not exists.
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryTaxonomyVocabularyByExternalReferenceCodeWithContentType(
						assetLibraryId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling putAssetLibraryTaxonomyVocabularyByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putAssetLibraryTaxonomyVocabularyByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
					 * Updates the asset library's taxonomy vocabulary with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param assetLibraryId
							 * @param externalReferenceCode
						 * @param taxonomyVocabulary
					 */
					public async putAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
									assetLibraryId: number,
									externalReferenceCode: string,
							taxonomyVocabulary?: TaxonomyVocabulary,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyVocabulary;
						response: Response;
					}> {
						return this.putAssetLibraryTaxonomyVocabularyByExternalReferenceCodeWithContentType(
										assetLibraryId,
										externalReferenceCode,
							{
								parameters: {
										taxonomyVocabulary: taxonomyVocabulary
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryId
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryTaxonomyVocabularyPermissionsPage(
						assetLibraryId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies/permissions"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling putAssetLibraryTaxonomyVocabularyPermissionsPage.");
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
		 * Updates the site's taxonomy vocabulary with the given external reference code, or creates it if it not exists.
				 * @param siteId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteTaxonomyVocabularyByExternalReferenceCodeWithContentType(
						siteId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteTaxonomyVocabularyByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putSiteTaxonomyVocabularyByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
					 * Updates the site's taxonomy vocabulary with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param siteId
							 * @param externalReferenceCode
						 * @param taxonomyVocabulary
					 */
					public async putSiteTaxonomyVocabularyByExternalReferenceCode(
									siteId: number,
									externalReferenceCode: string,
							taxonomyVocabulary?: TaxonomyVocabulary,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyVocabulary;
						response: Response;
					}> {
						return this.putSiteTaxonomyVocabularyByExternalReferenceCodeWithContentType(
										siteId,
										externalReferenceCode,
							{
								parameters: {
										taxonomyVocabulary: taxonomyVocabulary
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteId
		 * @param headers Optional custom request headers
		 */
		public async putSiteTaxonomyVocabularyPermissionsPage(
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies/permissions"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteTaxonomyVocabularyPermissionsPage.");
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
		 * Replaces the taxonomy vocabulary with the information sent in the request body. Any missing fields are deleted unless they are required.
				 * @param taxonomyVocabularyId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putTaxonomyVocabularyWithContentType(
						taxonomyVocabularyId: number,
					requestBody:
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										taxonomyVocabulary?: TaxonomyVocabulary
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TaxonomyVocabulary;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.taxonomyVocabulary, "TaxonomyVocabulary"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling putTaxonomyVocabulary.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TaxonomyVocabulary"), response};
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
					 * Replaces the taxonomy vocabulary with the information sent in the request body. Any missing fields are deleted unless they are required. - Default method for JSON body
							 * @param taxonomyVocabularyId
						 * @param taxonomyVocabulary
					 */
					public async putTaxonomyVocabulary(
									taxonomyVocabularyId: number,
							taxonomyVocabulary?: TaxonomyVocabulary,
						headers?: {[name: string]: string}
					): Promise<{
							body: TaxonomyVocabulary;
						response: Response;
					}> {
						return this.putTaxonomyVocabularyWithContentType(
										taxonomyVocabularyId,
							{
								parameters: {
										taxonomyVocabulary: taxonomyVocabulary
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param taxonomyVocabularyId
		 * @param headers Optional custom request headers
		 */
		public async putTaxonomyVocabularyPermissionsPage(
						taxonomyVocabularyId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/permissions"
						.replace("{taxonomyVocabularyId}",encodeURIComponent(taxonomyVocabularyId))
				;

			const queryParameters: any = {};

						if (taxonomyVocabularyId === null || taxonomyVocabularyId === undefined) {
							throw new Error("Required parameter taxonomyVocabularyId was null or undefined when calling putTaxonomyVocabularyPermissionsPage.");
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

}