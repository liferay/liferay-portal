/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {MultipartTestEntity} from '../models/MultipartTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class MultipartTestEntityAPI {
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
				 * @param multipartTestEntityId
		 * @param headers Optional custom request headers
		 */
		public async getMultipartTestEntity(
						multipartTestEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: MultipartTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/multipart-test-entities/{multipartTestEntityId}"
						.replace("{multipartTestEntityId}",encodeURIComponent(multipartTestEntityId))
				;

			const queryParameters: any = {};

						if (multipartTestEntityId === null || multipartTestEntityId === undefined) {
							throw new Error("Required parameter multipartTestEntityId was null or undefined when calling getMultipartTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "MultipartTestEntity"), response};
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
				 * @param multipartTestEntityId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchMultipartTestEntityWithContentType(
						multipartTestEntityId: number,
					requestBody:
							{
								parameters: {
										multipartTestEntity?: MultipartTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										multipartTestEntity?: MultipartTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: MultipartTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.multipartTestEntity, "MultipartTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.multipartTestEntity, "MultipartTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/multipart-test-entities/{multipartTestEntityId}"
						.replace("{multipartTestEntityId}",encodeURIComponent(multipartTestEntityId))
				;

			const queryParameters: any = {};

						if (multipartTestEntityId === null || multipartTestEntityId === undefined) {
							throw new Error("Required parameter multipartTestEntityId was null or undefined when calling patchMultipartTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "MultipartTestEntity"), response};
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
							 * @param multipartTestEntityId
						 * @param multipartTestEntity
					 */
					public async patchMultipartTestEntity(
									multipartTestEntityId: number,
							multipartTestEntity?: MultipartTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: MultipartTestEntity;
						response: Response;
					}> {
						return this.patchMultipartTestEntityWithContentType(
										multipartTestEntityId,
							{
								parameters: {
										multipartTestEntity: multipartTestEntity
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
		public async postMultipartTestEntityWithContentType(
					requestBody:
							{
								parameters: {
										multipartTestEntity?: MultipartTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										multipartTestEntity?: MultipartTestEntity
								},
								type: "application/xml"
							}
								|
							{
								parameters: {
										multipartTestEntity?: MultipartTestEntity
								},
								type: "multipart/form-data"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: MultipartTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.multipartTestEntity, "MultipartTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.multipartTestEntity, "MultipartTestEntity"));
						}
						if (requestBody.type === "multipart/form-data") {
								const formData = new FormData();
										formData.append("multipartTestEntity", JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.multipartTestEntity, "MultipartTestEntity")));
								body = formData;
						}

			const path = this._basePath + "/test/v1.0/multipart-test-entity"
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
								,(requestBody.type !== "multipart/form-data") ?
									{"Content-Type": requestBody.type} : {}
					,headers || {}
					),
				method: "POST",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "MultipartTestEntity"), response};
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
						 * @param multipartTestEntity
					 */
					public async postMultipartTestEntity(
							multipartTestEntity?: MultipartTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: MultipartTestEntity;
						response: Response;
					}> {
						return this.postMultipartTestEntityWithContentType(
							{
								parameters: {
										multipartTestEntity: multipartTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param multipartTestEntityId
				 	* @param multipartTestEntity
		 * @param headers Optional custom request headers
		 */
		public async putMultipartTestEntity(
						multipartTestEntityId: number,
						multipartTestEntity?: MultipartTestEntity,
			headers?: {[name: string]: string},
		): Promise<{
				body: MultipartTestEntity;
			response: Response;
		}> {
				let body;
						const formData = new FormData();
								formData.append("multipartTestEntity", JSON.stringify(ObjectSerializer.serialize(multipartTestEntity, "MultipartTestEntity")));
						body = formData;

			const path = this._basePath + "/test/v1.0/multipart-test-entities/{multipartTestEntityId}"
						.replace("{multipartTestEntityId}",encodeURIComponent(multipartTestEntityId))
				;

			const queryParameters: any = {};

						if (multipartTestEntityId === null || multipartTestEntityId === undefined) {
							throw new Error("Required parameter multipartTestEntityId was null or undefined when calling putMultipartTestEntity.");
						}

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "multipart/form-data"
						}
					,headers || {}
					),
				method: "PUT",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "MultipartTestEntity"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

}