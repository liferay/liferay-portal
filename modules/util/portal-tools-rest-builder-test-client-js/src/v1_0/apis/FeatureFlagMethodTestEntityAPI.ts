/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {FeatureFlagMethodTestEntity} from '../models/FeatureFlagMethodTestEntity';
		import {PageFeatureFlagMethodTestEntity} from '../models/PageFeatureFlagMethodTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class FeatureFlagMethodTestEntityAPI {
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
				 * @param featureFlagMethodTestEntityId
		 * @param headers Optional custom request headers
		 */
		public async deleteFeatureFlagMethodTestEntity(
						featureFlagMethodTestEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/feature-flag-method-test-entities/{featureFlagMethodTestEntityId}"
						.replace("{featureFlagMethodTestEntityId}",encodeURIComponent(featureFlagMethodTestEntityId))
				;

			const queryParameters: any = {};

						if (featureFlagMethodTestEntityId === null || featureFlagMethodTestEntityId === undefined) {
							throw new Error("Required parameter featureFlagMethodTestEntityId was null or undefined when calling deleteFeatureFlagMethodTestEntity.");
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
		public async deleteSiteFeatureFlagMethodTestEntityByExternalReferenceCode(
						siteId: number,
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/sites/{siteId}/feature-flag-method-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{siteId}",encodeURIComponent(siteId))
										.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling deleteSiteFeatureFlagMethodTestEntityByExternalReferenceCode.");
						}

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSiteFeatureFlagMethodTestEntityByExternalReferenceCode.");
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
		 * @param headers Optional custom request headers
		 */
		public async getFeatureFlagMethodTestEntitiesPage(
			headers?: {[name: string]: string},
		): Promise<{
				body: PageFeatureFlagMethodTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/feature-flag-method-test-entities"
;

			const queryParameters: any = {};

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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageFeatureFlagMethodTestEntity"), response};
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
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getFeatureFlagMethodTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: FeatureFlagMethodTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/feature-flag-method-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getFeatureFlagMethodTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "FeatureFlagMethodTestEntity"), response};
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
		public async getSiteFeatureFlagMethodTestEntitiesPage(
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageFeatureFlagMethodTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/sites/{siteId}/feature-flag-method-test-entities"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteFeatureFlagMethodTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageFeatureFlagMethodTestEntity"), response};
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
				 * @param featureFlagMethodTestEntityId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchFeatureFlagMethodTestEntityWithContentType(
						featureFlagMethodTestEntityId: number,
					requestBody:
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: FeatureFlagMethodTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/feature-flag-method-test-entities/{featureFlagMethodTestEntityId}"
						.replace("{featureFlagMethodTestEntityId}",encodeURIComponent(featureFlagMethodTestEntityId))
				;

			const queryParameters: any = {};

						if (featureFlagMethodTestEntityId === null || featureFlagMethodTestEntityId === undefined) {
							throw new Error("Required parameter featureFlagMethodTestEntityId was null or undefined when calling patchFeatureFlagMethodTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "FeatureFlagMethodTestEntity"), response};
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
							 * @param featureFlagMethodTestEntityId
						 * @param featureFlagMethodTestEntity
					 */
					public async patchFeatureFlagMethodTestEntity(
									featureFlagMethodTestEntityId: number,
							featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: FeatureFlagMethodTestEntity;
						response: Response;
					}> {
						return this.patchFeatureFlagMethodTestEntityWithContentType(
										featureFlagMethodTestEntityId,
							{
								parameters: {
										featureFlagMethodTestEntity: featureFlagMethodTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postFeatureFlagMethodTestEntityWithContentType(
					requestBody:
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: FeatureFlagMethodTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/feature-flag-method-test-entities"
;

			const queryParameters: any = {};

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
						return {body: ObjectSerializer.deserialize(await response.json(), "FeatureFlagMethodTestEntity"), response};
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
						 * @param featureFlagMethodTestEntity
					 */
					public async postFeatureFlagMethodTestEntity(
							featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: FeatureFlagMethodTestEntity;
						response: Response;
					}> {
						return this.postFeatureFlagMethodTestEntityWithContentType(
							{
								parameters: {
										featureFlagMethodTestEntity: featureFlagMethodTestEntity
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
		public async postSiteFeatureFlagMethodTestEntityWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: FeatureFlagMethodTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/sites/{siteId}/feature-flag-method-test-entities"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling postSiteFeatureFlagMethodTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "FeatureFlagMethodTestEntity"), response};
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
						 * @param featureFlagMethodTestEntity
					 */
					public async postSiteFeatureFlagMethodTestEntity(
									siteId: number,
							featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: FeatureFlagMethodTestEntity;
						response: Response;
					}> {
						return this.postSiteFeatureFlagMethodTestEntityWithContentType(
										siteId,
							{
								parameters: {
										featureFlagMethodTestEntity: featureFlagMethodTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param externalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putFeatureFlagMethodTestEntityByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: FeatureFlagMethodTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.featureFlagMethodTestEntity, "FeatureFlagMethodTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/feature-flag-method-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putFeatureFlagMethodTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "FeatureFlagMethodTestEntity"), response};
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
							 * @param externalReferenceCode
						 * @param featureFlagMethodTestEntity
					 */
					public async putFeatureFlagMethodTestEntityByExternalReferenceCode(
									externalReferenceCode: string,
							featureFlagMethodTestEntity?: FeatureFlagMethodTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: FeatureFlagMethodTestEntity;
						response: Response;
					}> {
						return this.putFeatureFlagMethodTestEntityByExternalReferenceCodeWithContentType(
										externalReferenceCode,
							{
								parameters: {
										featureFlagMethodTestEntity: featureFlagMethodTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
}