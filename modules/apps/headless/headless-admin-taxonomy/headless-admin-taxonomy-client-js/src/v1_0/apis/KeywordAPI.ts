/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {Keyword} from '../models/Keyword';
		import {PageKeyword} from '../models/PageKeyword';

/**
 * @author Javier Gamarra
 * @generated
 */

export class KeywordAPI {
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
		 * Deletes the asset library's keyword by external reference code.
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteAssetLibraryKeywordByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling deleteAssetLibraryKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteAssetLibraryKeywordByExternalReferenceCode.");
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
		 * Deletes the keyword and returns a 204 if the operation succeeds.
				 * @param keywordId
		 * @param headers Optional custom request headers
		 */
		public async deleteKeyword(
						keywordId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/{keywordId}"
						.replace("{keywordId}",encodeURIComponent(keywordId))
				;

			const queryParameters: any = {};

						if (keywordId === null || keywordId === undefined) {
							throw new Error("Required parameter keywordId was null or undefined when calling deleteKeyword.");
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
		 * Deletes the site's keyword by external reference code.
				 * @param siteId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteKeywordByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling deleteSiteKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSiteKeywordByExternalReferenceCode.");
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
		 * Retrieves the asset library's keyword by external reference code.
				 * @param assetLibraryId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryKeywordByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getAssetLibraryKeywordByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
		public async getAssetLibraryKeywordPermissionsPage(
						assetLibraryId: number,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords/permissions"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
																;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryKeywordPermissionsPage.");
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
		public async getAssetLibraryKeywordsPage(
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
				body: PageKeyword;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
																																				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryKeywordsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageKeyword"), response};
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
		 * Retrieves a keyword.
				 * @param keywordId
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getKeyword(
						keywordId: number,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/{keywordId}"
						.replace("{keywordId}",encodeURIComponent(keywordId))
												;

			const queryParameters: any = {};

						if (keywordId === null || keywordId === undefined) {
							throw new Error("Required parameter keywordId was null or undefined when calling getKeyword.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
				 * @param search
				 * @param siteId
		 * @param headers Optional custom request headers
		 */
		public async getKeywordsRankedPage(
						fields?: string,
						page?: number,
						pageSize?: number,
						restrictFields?: string,
						search?: string,
						siteId?: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageKeyword;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/ranked"
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

						if (search !== undefined) {
							queryParameters["search"] = ObjectSerializer.serialize(search, "string");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageKeyword"), response};
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
		 * Retrieves the site's keyword by external reference code.
				 * @param siteId
				 * @param externalReferenceCode
				 * @param fields
				 * @param restrictFields
		 * @param headers Optional custom request headers
		 */
		public async getSiteKeywordByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
												;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getSiteKeywordByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
		public async getSiteKeywordPermissionsPage(
						siteId: number,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/permissions"
						.replace("{siteId}",encodeURIComponent(siteId))
																;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteKeywordPermissionsPage.");
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
		 * Retrieves a Site's keywords. Results can be paginated, filtered, searched, and sorted.
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
		public async getSiteKeywordsPage(
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
				body: PageKeyword;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords"
						.replace("{siteId}",encodeURIComponent(siteId))
																																				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteKeywordsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageKeyword"), response};
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
				 * @param siteId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchSiteKeywordWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling patchSiteKeyword.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
							 * @param siteId
						 * @param keyword
					 */
					public async patchSiteKeyword(
									siteId: number,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.patchSiteKeywordWithContentType(
										siteId,
							{
								parameters: {
										keyword: keyword
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Updates only the fields received in the request body. Other fields are left untouched.
				 * @param siteId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchSiteKeywordByExternalReferenceCodeWithContentType(
						siteId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling patchSiteKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling patchSiteKeywordByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
							 * @param siteId
							 * @param externalReferenceCode
						 * @param keyword
					 */
					public async patchSiteKeywordByExternalReferenceCode(
									siteId: number,
									externalReferenceCode: string,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.patchSiteKeywordByExternalReferenceCodeWithContentType(
										siteId,
										externalReferenceCode,
							{
								parameters: {
										keyword: keyword
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
		public async postAssetLibraryKeywordWithContentType(
						assetLibraryId: number,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling postAssetLibraryKeyword.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
						 * @param keyword
					 */
					public async postAssetLibraryKeyword(
									assetLibraryId: number,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.postAssetLibraryKeywordWithContentType(
										assetLibraryId,
							{
								parameters: {
										keyword: keyword
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Inserts a new keyword in a Site.
				 * @param siteId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSiteKeywordWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling postSiteKeyword.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
					 * Inserts a new keyword in a Site. - Default method for JSON body
							 * @param siteId
						 * @param keyword
					 */
					public async postSiteKeyword(
									siteId: number,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.postSiteKeywordWithContentType(
										siteId,
							{
								parameters: {
										keyword: keyword
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * Updates the asset library's keyword with the given external reference code, or creates it if it not exists.
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryKeywordByExternalReferenceCodeWithContentType(
						assetLibraryId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling putAssetLibraryKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putAssetLibraryKeywordByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
					 * Updates the asset library's keyword with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param assetLibraryId
							 * @param externalReferenceCode
						 * @param keyword
					 */
					public async putAssetLibraryKeywordByExternalReferenceCode(
									assetLibraryId: number,
									externalReferenceCode: string,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.putAssetLibraryKeywordByExternalReferenceCodeWithContentType(
										assetLibraryId,
										externalReferenceCode,
							{
								parameters: {
										keyword: keyword
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
		public async putAssetLibraryKeywordPermissionsPage(
						assetLibraryId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords/permissions"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling putAssetLibraryKeywordPermissionsPage.");
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
		 * Replaces the keyword with the information sent in the request body. Any missing fields are deleted, unless required.
				 * @param keywordId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putKeywordWithContentType(
						keywordId: number,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/{keywordId}"
						.replace("{keywordId}",encodeURIComponent(keywordId))
				;

			const queryParameters: any = {};

						if (keywordId === null || keywordId === undefined) {
							throw new Error("Required parameter keywordId was null or undefined when calling putKeyword.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
					 * Replaces the keyword with the information sent in the request body. Any missing fields are deleted, unless required. - Default method for JSON body
							 * @param keywordId
						 * @param keyword
					 */
					public async putKeyword(
									keywordId: number,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.putKeywordWithContentType(
										keywordId,
							{
								parameters: {
										keyword: keyword
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param toKeywordId
				 * @param fromKeywordIds
		 * @param headers Optional custom request headers
		 */
		public async putKeywordMerge(
						toKeywordId: number,
						fromKeywordIds: Array<number>,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/{toKeywordId}/merge"
						.replace("{toKeywordId}",encodeURIComponent(toKeywordId))
								;

			const queryParameters: any = {};

						if (toKeywordId === null || toKeywordId === undefined) {
							throw new Error("Required parameter toKeywordId was null or undefined when calling putKeywordMerge.");
						}

						if (fromKeywordIds === null || fromKeywordIds === undefined) {
							throw new Error("Required parameter fromKeywordIds was null or undefined when calling putKeywordMerge.");
						}

						if (fromKeywordIds !== undefined) {
							queryParameters["fromKeywordIds"] = ObjectSerializer.serialize(fromKeywordIds, "Array<number>");
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
		 * 
				 * @param keywordId
		 * @param headers Optional custom request headers
		 */
		public async putKeywordSubscribe(
						keywordId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/{keywordId}/subscribe"
						.replace("{keywordId}",encodeURIComponent(keywordId))
				;

			const queryParameters: any = {};

						if (keywordId === null || keywordId === undefined) {
							throw new Error("Required parameter keywordId was null or undefined when calling putKeywordSubscribe.");
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
		 * 
				 * @param keywordId
		 * @param headers Optional custom request headers
		 */
		public async putKeywordUnsubscribe(
						keywordId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/keywords/{keywordId}/unsubscribe"
						.replace("{keywordId}",encodeURIComponent(keywordId))
				;

			const queryParameters: any = {};

						if (keywordId === null || keywordId === undefined) {
							throw new Error("Required parameter keywordId was null or undefined when calling putKeywordUnsubscribe.");
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
		 * Updates the site's keyword with the given external reference code, or creates it if it not exists.
				 * @param siteId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteKeywordByExternalReferenceCodeWithContentType(
						siteId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										keyword?: Keyword
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: Keyword;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.keyword, "Keyword"));
						}

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteKeywordByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putSiteKeywordByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "Keyword"), response};
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
					 * Updates the site's keyword with the given external reference code, or creates it if it not exists. - Default method for JSON body
							 * @param siteId
							 * @param externalReferenceCode
						 * @param keyword
					 */
					public async putSiteKeywordByExternalReferenceCode(
									siteId: number,
									externalReferenceCode: string,
							keyword?: Keyword,
						headers?: {[name: string]: string}
					): Promise<{
							body: Keyword;
						response: Response;
					}> {
						return this.putSiteKeywordByExternalReferenceCodeWithContentType(
										siteId,
										externalReferenceCode,
							{
								parameters: {
										keyword: keyword
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
		public async putSiteKeywordPermissionsPage(
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/permissions"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteKeywordPermissionsPage.");
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