/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {PageScopedTestEntity} from '../models/PageScopedTestEntity';
		import {ScopedTestEntity} from '../models/ScopedTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class ScopedTestEntityAPI {
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
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteAssetLibraryScopedTestEntityByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling deleteAssetLibraryScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteAssetLibraryScopedTestEntityByExternalReferenceCode.");
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
				 * @param siteId
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteScopedTestEntityByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling deleteSiteScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSiteScopedTestEntityByExternalReferenceCode.");
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
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryScopedTestEntitiesPage(
						assetLibraryId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryScopedTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageScopedTestEntity"), response};
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
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getAssetLibraryScopedTestEntityByExternalReferenceCode(
						assetLibraryId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling getAssetLibraryScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getAssetLibraryScopedTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
		 * @param headers Optional custom request headers
		 */
		public async getSiteScopedTestEntitiesPage(
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteId}/scoped-test-entities"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteScopedTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageScopedTestEntity"), response};
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
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getSiteScopedTestEntityByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getSiteScopedTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchAssetLibraryScopedTestEntityByExternalReferenceCodeWithContentType(
						assetLibraryId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling patchAssetLibraryScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling patchAssetLibraryScopedTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
							 * @param externalReferenceCode
						 * @param scopedTestEntity
					 */
					public async patchAssetLibraryScopedTestEntityByExternalReferenceCode(
									assetLibraryId: number,
									externalReferenceCode: string,
							scopedTestEntity?: ScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ScopedTestEntity;
						response: Response;
					}> {
						return this.patchAssetLibraryScopedTestEntityByExternalReferenceCodeWithContentType(
										assetLibraryId,
										externalReferenceCode,
							{
								parameters: {
										scopedTestEntity: scopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchSiteScopedTestEntityByExternalReferenceCodeWithContentType(
						siteId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling patchSiteScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling patchSiteScopedTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
							 * @param siteId
							 * @param externalReferenceCode
						 * @param scopedTestEntity
					 */
					public async patchSiteScopedTestEntityByExternalReferenceCode(
									siteId: number,
									externalReferenceCode: string,
							scopedTestEntity?: ScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ScopedTestEntity;
						response: Response;
					}> {
						return this.patchSiteScopedTestEntityByExternalReferenceCodeWithContentType(
										siteId,
										externalReferenceCode,
							{
								parameters: {
										scopedTestEntity: scopedTestEntity
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
		public async postAssetLibraryScopedTestEntityWithContentType(
						assetLibraryId: number,
					requestBody:
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling postAssetLibraryScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
						 * @param scopedTestEntity
					 */
					public async postAssetLibraryScopedTestEntity(
									assetLibraryId: number,
							scopedTestEntity?: ScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ScopedTestEntity;
						response: Response;
					}> {
						return this.postAssetLibraryScopedTestEntityWithContentType(
										assetLibraryId,
							{
								parameters: {
										scopedTestEntity: scopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSiteScopedTestEntityWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteId}/scoped-test-entities"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling postSiteScopedTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
							 * @param siteId
						 * @param scopedTestEntity
					 */
					public async postSiteScopedTestEntity(
									siteId: number,
							scopedTestEntity?: ScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ScopedTestEntity;
						response: Response;
					}> {
						return this.postSiteScopedTestEntityWithContentType(
										siteId,
							{
								parameters: {
										scopedTestEntity: scopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param assetLibraryId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putAssetLibraryScopedTestEntityByExternalReferenceCodeWithContentType(
						assetLibraryId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{assetLibraryId}",encodeURIComponent(assetLibraryId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (assetLibraryId === null || assetLibraryId === undefined) {
							throw new Error("Required parameter assetLibraryId was null or undefined when calling putAssetLibraryScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putAssetLibraryScopedTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
							 * @param externalReferenceCode
						 * @param scopedTestEntity
					 */
					public async putAssetLibraryScopedTestEntityByExternalReferenceCode(
									assetLibraryId: number,
									externalReferenceCode: string,
							scopedTestEntity?: ScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ScopedTestEntity;
						response: Response;
					}> {
						return this.putAssetLibraryScopedTestEntityByExternalReferenceCodeWithContentType(
										assetLibraryId,
										externalReferenceCode,
							{
								parameters: {
										scopedTestEntity: scopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteId
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteScopedTestEntityByExternalReferenceCodeWithContentType(
						siteId: number,
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										scopedTestEntity?: ScopedTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ScopedTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.scopedTestEntity, "ScopedTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteId}/scoped-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteScopedTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putSiteScopedTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ScopedTestEntity"), response};
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
							 * @param siteId
							 * @param externalReferenceCode
						 * @param scopedTestEntity
					 */
					public async putSiteScopedTestEntityByExternalReferenceCode(
									siteId: number,
									externalReferenceCode: string,
							scopedTestEntity?: ScopedTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ScopedTestEntity;
						response: Response;
					}> {
						return this.putSiteScopedTestEntityByExternalReferenceCodeWithContentType(
										siteId,
										externalReferenceCode,
							{
								parameters: {
										scopedTestEntity: scopedTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
}