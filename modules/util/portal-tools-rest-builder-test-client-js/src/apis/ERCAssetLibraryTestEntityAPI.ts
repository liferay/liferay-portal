/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ERCAssetLibraryTestEntity} from '../models/ERCAssetLibraryTestEntity';
		import {PageERCAssetLibraryTestEntity} from '../models/PageERCAssetLibraryTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class ERCAssetLibraryTestEntityAPI {
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
				 * @param ercAssetLibraryTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteAssetLibraryERCAssetLibraryTestEntity(
						assetLibraryExternalReferenceCode: string,
						ercAssetLibraryTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercAssetLibraryTestEntityExternalReferenceCode}",encodeURIComponent(ercAssetLibraryTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling deleteAssetLibraryERCAssetLibraryTestEntity.");
						}

						if (ercAssetLibraryTestEntityExternalReferenceCode === null || ercAssetLibraryTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercAssetLibraryTestEntityExternalReferenceCode was null or undefined when calling deleteAssetLibraryERCAssetLibraryTestEntity.");
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
		public async getAssetLibraryERCAssetLibraryTestEntitiesPage(
						assetLibraryExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageERCAssetLibraryTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling getAssetLibraryERCAssetLibraryTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageERCAssetLibraryTestEntity"), response};
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
				 * @param ercAssetLibraryTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryERCAssetLibraryTestEntity(
						assetLibraryExternalReferenceCode: string,
						ercAssetLibraryTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCAssetLibraryTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercAssetLibraryTestEntityExternalReferenceCode}",encodeURIComponent(ercAssetLibraryTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling getAssetLibraryERCAssetLibraryTestEntity.");
						}

						if (ercAssetLibraryTestEntityExternalReferenceCode === null || ercAssetLibraryTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercAssetLibraryTestEntityExternalReferenceCode was null or undefined when calling getAssetLibraryERCAssetLibraryTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCAssetLibraryTestEntity"), response};
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
				 * @param ercAssetLibraryTestEntityExternalReferenceCode
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
						assetLibraryExternalReferenceCode: string,
						ercAssetLibraryTestEntityExternalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}/permissions"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercAssetLibraryTestEntityExternalReferenceCode}",encodeURIComponent(ercAssetLibraryTestEntityExternalReferenceCode))
																;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling getAssetLibraryERCAssetLibraryTestEntityPermissionsPage.");
						}

						if (ercAssetLibraryTestEntityExternalReferenceCode === null || ercAssetLibraryTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercAssetLibraryTestEntityExternalReferenceCode was null or undefined when calling getAssetLibraryERCAssetLibraryTestEntityPermissionsPage.");
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
		public async postAssetLibraryERCAssetLibraryTestEntityWithContentType(
						assetLibraryExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCAssetLibraryTestEntity?: ERCAssetLibraryTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCAssetLibraryTestEntity?: ERCAssetLibraryTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCAssetLibraryTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCAssetLibraryTestEntity, "ERCAssetLibraryTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCAssetLibraryTestEntity, "ERCAssetLibraryTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling postAssetLibraryERCAssetLibraryTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCAssetLibraryTestEntity"), response};
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
						 * @param eRCAssetLibraryTestEntity
					 */
					public async postAssetLibraryERCAssetLibraryTestEntity(
									assetLibraryExternalReferenceCode: string,
							eRCAssetLibraryTestEntity?: ERCAssetLibraryTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCAssetLibraryTestEntity;
						response: Response;
					}> {
						return this.postAssetLibraryERCAssetLibraryTestEntityWithContentType(
										assetLibraryExternalReferenceCode,
							{
								parameters: {
										eRCAssetLibraryTestEntity: eRCAssetLibraryTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryExternalReferenceCode
				 * @param ercAssetLibraryTestEntityExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryERCAssetLibraryTestEntityWithContentType(
						assetLibraryExternalReferenceCode: string,
						ercAssetLibraryTestEntityExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCAssetLibraryTestEntity?: ERCAssetLibraryTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCAssetLibraryTestEntity?: ERCAssetLibraryTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCAssetLibraryTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCAssetLibraryTestEntity, "ERCAssetLibraryTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCAssetLibraryTestEntity, "ERCAssetLibraryTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercAssetLibraryTestEntityExternalReferenceCode}",encodeURIComponent(ercAssetLibraryTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling putAssetLibraryERCAssetLibraryTestEntity.");
						}

						if (ercAssetLibraryTestEntityExternalReferenceCode === null || ercAssetLibraryTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercAssetLibraryTestEntityExternalReferenceCode was null or undefined when calling putAssetLibraryERCAssetLibraryTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCAssetLibraryTestEntity"), response};
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
							 * @param ercAssetLibraryTestEntityExternalReferenceCode
						 * @param eRCAssetLibraryTestEntity
					 */
					public async putAssetLibraryERCAssetLibraryTestEntity(
									assetLibraryExternalReferenceCode: string,
									ercAssetLibraryTestEntityExternalReferenceCode: string,
							eRCAssetLibraryTestEntity?: ERCAssetLibraryTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCAssetLibraryTestEntity;
						response: Response;
					}> {
						return this.putAssetLibraryERCAssetLibraryTestEntityWithContentType(
										assetLibraryExternalReferenceCode,
										ercAssetLibraryTestEntityExternalReferenceCode,
							{
								parameters: {
										eRCAssetLibraryTestEntity: eRCAssetLibraryTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryExternalReferenceCode
				 * @param ercAssetLibraryTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
						assetLibraryExternalReferenceCode: string,
						ercAssetLibraryTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/{ercAssetLibraryTestEntityExternalReferenceCode}/permissions"
						.replace("{assetLibraryExternalReferenceCode}",encodeURIComponent(assetLibraryExternalReferenceCode))
										.replace("{ercAssetLibraryTestEntityExternalReferenceCode}",encodeURIComponent(ercAssetLibraryTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryExternalReferenceCode === null || assetLibraryExternalReferenceCode === undefined) {
							throw new Error("Required parameter assetLibraryExternalReferenceCode was null or undefined when calling putAssetLibraryERCAssetLibraryTestEntityPermissionsPage.");
						}

						if (ercAssetLibraryTestEntityExternalReferenceCode === null || ercAssetLibraryTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercAssetLibraryTestEntityExternalReferenceCode was null or undefined when calling putAssetLibraryERCAssetLibraryTestEntityPermissionsPage.");
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