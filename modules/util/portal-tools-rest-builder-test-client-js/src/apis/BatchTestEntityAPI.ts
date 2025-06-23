/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {BatchTestEntity} from '../models/BatchTestEntity';
		import {PageBatchTestEntity} from '../models/PageBatchTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class BatchTestEntityAPI {
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
		public async deleteBatchTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/batch-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteBatchTestEntityByExternalReferenceCode.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
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
		public async getBatchTestEntitiesPage(
			headers?: {[name: string]: string},
		): Promise<{
				body: PageBatchTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/batch-test-entities"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageBatchTestEntity"), response};
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
				 * @param batchTestEntityId
		 * @param headers Optional custom request headers
		 */
		public async getBatchTestEntity(
						batchTestEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: BatchTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/batch-test-entities/{batchTestEntityId}"
						.replace("{batchTestEntityId}",encodeURIComponent(batchTestEntityId))
				;

			const queryParameters: any = {};

						if (batchTestEntityId === null || batchTestEntityId === undefined) {
							throw new Error("Required parameter batchTestEntityId was null or undefined when calling getBatchTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "BatchTestEntity"), response};
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
		public async getBatchTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: BatchTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/batch-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getBatchTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "BatchTestEntity"), response};
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
		public async postBatchTestEntityWithContentType(
					requestBody:
							{
								parameters: {
										batchTestEntity?: BatchTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										batchTestEntity?: BatchTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: BatchTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.batchTestEntity, "BatchTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.batchTestEntity, "BatchTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/batch-test-entities"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "BatchTestEntity"), response};
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
						 * @param batchTestEntity
					 */
					public async postBatchTestEntity(
							batchTestEntity?: BatchTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: BatchTestEntity;
						response: Response;
					}> {
						return this.postBatchTestEntityWithContentType(
							{
								parameters: {
										batchTestEntity: batchTestEntity
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
		public async putBatchTestEntityByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										batchTestEntity?: BatchTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										batchTestEntity?: BatchTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: BatchTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.batchTestEntity, "BatchTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.batchTestEntity, "BatchTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/batch-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putBatchTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "BatchTestEntity"), response};
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
						 * @param batchTestEntity
					 */
					public async putBatchTestEntityByExternalReferenceCode(
									externalReferenceCode: string,
							batchTestEntity?: BatchTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: BatchTestEntity;
						response: Response;
					}> {
						return this.putBatchTestEntityByExternalReferenceCodeWithContentType(
										externalReferenceCode,
							{
								parameters: {
										batchTestEntity: batchTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
}