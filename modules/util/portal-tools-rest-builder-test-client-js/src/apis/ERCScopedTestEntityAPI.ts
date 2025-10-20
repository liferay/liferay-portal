/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ERCScopedTestEntity} from '../models/ERCScopedTestEntity';
		import {PageERCScopedTestEntity} from '../models/PageERCScopedTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class ERCScopedTestEntityAPI {
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
		 * 
				 * @param assetLibraryExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteAssetLibraryERCScopedTestEntity(
						assetLibraryExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling deleteAssetLibraryERCScopedTestEntity.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling deleteAssetLibraryERCScopedTestEntity.");
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
				 * @param siteExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteERCScopedTestEntity(
						siteExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling deleteSiteERCScopedTestEntity.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling deleteSiteERCScopedTestEntity.");
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
				 * @param assetLibraryExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryERCScopedTestEntitiesPage(
						assetLibraryExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageERCScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling getAssetLibraryERCScopedTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageERCScopedTestEntity"), response};
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
				 * @param assetLibraryExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryERCScopedTestEntity(
						assetLibraryExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling getAssetLibraryERCScopedTestEntity.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling getAssetLibraryERCScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCScopedTestEntity"), response};
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
				 * @param assetLibraryExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryERCScopedTestEntityPermissionsPage(
						assetLibraryExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}/permissions"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
																;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling getAssetLibraryERCScopedTestEntityPermissionsPage.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling getAssetLibraryERCScopedTestEntityPermissionsPage.");
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
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getERCScopedTestEntitiesPage(
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageERCScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/erc-scoped-test-entities"
												;

			const queryParameters: any = {};

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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageERCScopedTestEntity"), response};
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
				 * @param ercScopedTestEntityExternalReferenceCode
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getERCScopedTestEntityPermissionsPage(
						ercScopedTestEntityExternalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}/permissions"
						.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
																;

			const queryParameters: any = {};

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling getERCScopedTestEntityPermissionsPage.");
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
				 * @param siteExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getSiteERCScopedTestEntitiesPage(
						siteExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageERCScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling getSiteERCScopedTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageERCScopedTestEntity"), response};
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
				 * @param siteExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getSiteERCScopedTestEntity(
						siteExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling getSiteERCScopedTestEntity.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling getSiteERCScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCScopedTestEntity"), response};
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
				 * @param siteExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getSiteERCScopedTestEntityPermissionsPage(
						siteExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}/permissions"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
																;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling getSiteERCScopedTestEntityPermissionsPage.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling getSiteERCScopedTestEntityPermissionsPage.");
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
				 * @param assetLibraryExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postAssetLibraryERCScopedTestEntityWithContentType(
						assetLibraryExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling postAssetLibraryERCScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCScopedTestEntity"), response};
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
							 * @param assetLibraryExternalReferenceCode
						 * @param eRCScopedTestEntity
					 */
					public async postAssetLibraryERCScopedTestEntity(
									assetLibraryExternalReferenceCode: string,
							eRCScopedTestEntity?: ERCScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCScopedTestEntity;
						response: Response;
					}> {
						return this.postAssetLibraryERCScopedTestEntityWithContentType(
										assetLibraryExternalReferenceCode,
							{
								parameters: {
										eRCScopedTestEntity: eRCScopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSiteERCScopedTestEntityWithContentType(
						siteExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling postSiteERCScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCScopedTestEntity"), response};
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
							 * @param siteExternalReferenceCode
						 * @param eRCScopedTestEntity
					 */
					public async postSiteERCScopedTestEntity(
									siteExternalReferenceCode: string,
							eRCScopedTestEntity?: ERCScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCScopedTestEntity;
						response: Response;
					}> {
						return this.postSiteERCScopedTestEntityWithContentType(
										siteExternalReferenceCode,
							{
								parameters: {
										eRCScopedTestEntity: eRCScopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryERCScopedTestEntityWithContentType(
						assetLibraryExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling putAssetLibraryERCScopedTestEntity.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling putAssetLibraryERCScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCScopedTestEntity"), response};
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
							 * @param assetLibraryExternalReferenceCode
							 * @param ercScopedTestEntityExternalReferenceCode
						 * @param eRCScopedTestEntity
					 */
					public async putAssetLibraryERCScopedTestEntity(
									assetLibraryExternalReferenceCode: string,
									ercScopedTestEntityExternalReferenceCode: string,
							eRCScopedTestEntity?: ERCScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCScopedTestEntity;
						response: Response;
					}> {
						return this.putAssetLibraryERCScopedTestEntityWithContentType(
										assetLibraryExternalReferenceCode,
										ercScopedTestEntityExternalReferenceCode,
							{
								parameters: {
										eRCScopedTestEntity: eRCScopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryERCScopedTestEntityPermissionsPage(
						assetLibraryExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}/permissions"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling putAssetLibraryERCScopedTestEntityPermissionsPage.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling putAssetLibraryERCScopedTestEntityPermissionsPage.");
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
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async putERCScopedTestEntityPermissionsPage(
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}/permissions"
						.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling putERCScopedTestEntityPermissionsPage.");
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
				 * @param siteExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteERCScopedTestEntityWithContentType(
						siteExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCScopedTestEntity?: ERCScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCScopedTestEntity, "ERCScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling putSiteERCScopedTestEntity.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling putSiteERCScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCScopedTestEntity"), response};
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
							 * @param siteExternalReferenceCode
							 * @param ercScopedTestEntityExternalReferenceCode
						 * @param eRCScopedTestEntity
					 */
					public async putSiteERCScopedTestEntity(
									siteExternalReferenceCode: string,
									ercScopedTestEntityExternalReferenceCode: string,
							eRCScopedTestEntity?: ERCScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCScopedTestEntity;
						response: Response;
					}> {
						return this.putSiteERCScopedTestEntityWithContentType(
										siteExternalReferenceCode,
										ercScopedTestEntityExternalReferenceCode,
							{
								parameters: {
										eRCScopedTestEntity: eRCScopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteExternalReferenceCode
				 * @param ercScopedTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async putSiteERCScopedTestEntityPermissionsPage(
						siteExternalReferenceCode: string,
						ercScopedTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-scoped-test-entities/{ercScopedTestEntityExternalReferenceCode}/permissions"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercScopedTestEntityExternalReferenceCode}",encodeURIComponent(ercScopedTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling putSiteERCScopedTestEntityPermissionsPage.");
						}

						if (ercScopedTestEntityExternalReferenceCode === null || ercScopedTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercScopedTestEntityExternalReferenceCode was null or undefined when calling putSiteERCScopedTestEntityPermissionsPage.");
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