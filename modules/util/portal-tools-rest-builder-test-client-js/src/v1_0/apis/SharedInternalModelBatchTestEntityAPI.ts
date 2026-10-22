/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {PageSharedInternalModelBatchTestEntity} from '../models/PageSharedInternalModelBatchTestEntity';
		import {SharedInternalModelBatchTestEntity} from '../models/SharedInternalModelBatchTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class SharedInternalModelBatchTestEntityAPI {
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
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSharedInternalModelBatchTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/shared-internal-model-batch-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSharedInternalModelBatchTestEntityByExternalReferenceCode.");
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
		public async getSharedInternalModelBatchTestEntitiesPage(
			headers?: {[name: string]: string},
		): Promise<{
				body: PageSharedInternalModelBatchTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/shared-internal-model-batch-test-entities"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageSharedInternalModelBatchTestEntity"), response};
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
		public async getSharedInternalModelBatchTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: SharedInternalModelBatchTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/shared-internal-model-batch-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getSharedInternalModelBatchTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SharedInternalModelBatchTestEntity"), response};
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
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSharedInternalModelBatchTestEntityWithContentType(
					requestBody:
							{
								parameters: {
										sharedInternalModelBatchTestEntity?: SharedInternalModelBatchTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										sharedInternalModelBatchTestEntity?: SharedInternalModelBatchTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: SharedInternalModelBatchTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.sharedInternalModelBatchTestEntity, "SharedInternalModelBatchTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.sharedInternalModelBatchTestEntity, "SharedInternalModelBatchTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/shared-internal-model-batch-test-entities"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SharedInternalModelBatchTestEntity"), response};
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
						 * @param sharedInternalModelBatchTestEntity
					 */
					public async postSharedInternalModelBatchTestEntity(
							sharedInternalModelBatchTestEntity?: SharedInternalModelBatchTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: SharedInternalModelBatchTestEntity;
						response: Response;
					}> {
						return this.postSharedInternalModelBatchTestEntityWithContentType(
							{
								parameters: {
										sharedInternalModelBatchTestEntity: sharedInternalModelBatchTestEntity
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
		public async putSharedInternalModelBatchTestEntityByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										sharedInternalModelBatchTestEntity?: SharedInternalModelBatchTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										sharedInternalModelBatchTestEntity?: SharedInternalModelBatchTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: SharedInternalModelBatchTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.sharedInternalModelBatchTestEntity, "SharedInternalModelBatchTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.sharedInternalModelBatchTestEntity, "SharedInternalModelBatchTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/shared-internal-model-batch-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putSharedInternalModelBatchTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SharedInternalModelBatchTestEntity"), response};
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
						 * @param sharedInternalModelBatchTestEntity
					 */
					public async putSharedInternalModelBatchTestEntityByExternalReferenceCode(
									externalReferenceCode: string,
							sharedInternalModelBatchTestEntity?: SharedInternalModelBatchTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: SharedInternalModelBatchTestEntity;
						response: Response;
					}> {
						return this.putSharedInternalModelBatchTestEntityByExternalReferenceCodeWithContentType(
										externalReferenceCode,
							{
								parameters: {
										sharedInternalModelBatchTestEntity: sharedInternalModelBatchTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
}