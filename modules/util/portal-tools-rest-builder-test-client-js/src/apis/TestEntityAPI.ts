/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {PageTestEntity} from '../models/PageTestEntity';
		import {TestEntity} from '../models/TestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class TestEntityAPI {
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
				 * @param testEntityId
				 * @param permanent
		 * @param headers Optional custom request headers
		 */
		public async deleteTestEntity(
						testEntityId: number,
						permanent?: boolean,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/test-entities/{testEntityId}"
						.replace("{testEntityId}",encodeURIComponent(testEntityId))
								;

			const queryParameters: any = {};

						if (testEntityId === null || testEntityId === undefined) {
							throw new Error("Required parameter testEntityId was null or undefined when calling deleteTestEntity.");
						}

						if (permanent !== undefined) {
							queryParameters["permanent"] = ObjectSerializer.serialize(permanent, "boolean");
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
				 * @param filter
		 * @param headers Optional custom request headers
		 */
		public async getTestEntitiesPage(
						filter?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/test-entities"
				;

			const queryParameters: any = {};

						if (filter !== undefined) {
							queryParameters["filter"] = ObjectSerializer.serialize(filter, "string");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageTestEntity"), response};
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
				 * @param testEntityId
		 * @param headers Optional custom request headers
		 */
		public async getTestEntity(
						testEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: TestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/test-entities/{testEntityId}"
						.replace("{testEntityId}",encodeURIComponent(testEntityId))
				;

			const queryParameters: any = {};

						if (testEntityId === null || testEntityId === undefined) {
							throw new Error("Required parameter testEntityId was null or undefined when calling getTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TestEntity"), response};
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
		 * Retrieves the count.
		 * @param headers Optional custom request headers
		 */
		public async getTestEntityCount(
			headers?: {[name: string]: string},
		): Promise<{
				body: number;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/test-entities/count"
;

			const queryParameters: any = {};

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				headers:
					Object.assign({}, this._defaultHeaders
						,{
								Accept: "text/plain"
						}
					,headers || {}
					),
				method: "GET",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "number"), response};
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
				 * @param testEntityId
				 * @param optionalParameter
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchTestEntityWithContentType(
						testEntityId: number,
					requestBody:
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/xml"
							}
								,
						optionalParameter?: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: TestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}

			const path = this._basePath + "/test/v1.0/test-entities/{testEntityId}"
						.replace("{testEntityId}",encodeURIComponent(testEntityId))
								;

			const queryParameters: any = {};

						if (testEntityId === null || testEntityId === undefined) {
							throw new Error("Required parameter testEntityId was null or undefined when calling patchTestEntity.");
						}

						if (optionalParameter !== undefined) {
							queryParameters["optionalParameter"] = ObjectSerializer.serialize(optionalParameter, "number");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TestEntity"), response};
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
							 * @param testEntityId
							 * @param optionalParameter
						 * @param testEntity
					 */
					public async patchTestEntity(
									testEntityId: number,
							testEntity?: TestEntity,
									optionalParameter?: number,
						headers?: {[name: string]: string}
					): Promise<{
							body: TestEntity;
						response: Response;
					}> {
						return this.patchTestEntityWithContentType(
										testEntityId,
							{
								parameters: {
										testEntity: testEntity
								},
								type: "application/json"
							},
										optionalParameter,
							headers
						);
					}
		/**
		 * 
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postReservedWordWithContentType(
					requestBody:
							{
								parameters: {
										body?: boolean
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										body?: boolean
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.body, "boolean"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.body, "boolean"));
						}

			const path = this._basePath + "/test/v1.0/reserved-word"
;

			const queryParameters: any = {};

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
								,{"Content-Type": requestBody.type}
					,headers || {}
					),
				method: "POST",
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
					 *  - Default method for JSON body
						 * @param body
					 */
					public async postReservedWord(
							body?: boolean,
						headers?: {[name: string]: string}
					): Promise<{
							body?: any;
						response: Response;
					}> {
						return this.postReservedWordWithContentType(
							{
								parameters: {
										body: body
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
		public async postTestEntityWithContentType(
					requestBody:
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}

			const path = this._basePath + "/test/v1.0/test-entities"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TestEntity"), response};
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
						 * @param testEntity
					 */
					public async postTestEntity(
							testEntity?: TestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: TestEntity;
						response: Response;
					}> {
						return this.postTestEntityWithContentType(
							{
								parameters: {
										testEntity: testEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 	* @param testEntities
		 * @param headers Optional custom request headers
		 */
		public async postTestEntityMultipartBulk(
						testEntities?: Array<TestEntity>,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {
				let body;
						const formData = new FormData();
								formData.append("testEntities", JSON.stringify(ObjectSerializer.serialize(testEntities, "Array<TestEntity>")));
						body = formData;

			const path = this._basePath + "/test/v1.0/test-entities/multipart/bulk"
;

			const queryParameters: any = {};

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
					body: body,
				headers:
					Object.assign({}, this._defaultHeaders
					,headers || {}
					),
				method: "POST",
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
				 * @param testEntityId
				 * @param optionalParameter
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putTestEntityWithContentType(
						testEntityId: number,
					requestBody:
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/xml"
							}
								,
						optionalParameter?: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: TestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}

			const path = this._basePath + "/test/v1.0/test-entities/{testEntityId}"
						.replace("{testEntityId}",encodeURIComponent(testEntityId))
								;

			const queryParameters: any = {};

						if (testEntityId === null || testEntityId === undefined) {
							throw new Error("Required parameter testEntityId was null or undefined when calling putTestEntity.");
						}

						if (optionalParameter !== undefined) {
							queryParameters["optionalParameter"] = ObjectSerializer.serialize(optionalParameter, "number");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TestEntity"), response};
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
							 * @param testEntityId
							 * @param optionalParameter
						 * @param testEntity
					 */
					public async putTestEntity(
									testEntityId: number,
							testEntity?: TestEntity,
									optionalParameter?: number,
						headers?: {[name: string]: string}
					): Promise<{
							body: TestEntity;
						response: Response;
					}> {
						return this.putTestEntityWithContentType(
										testEntityId,
							{
								parameters: {
										testEntity: testEntity
								},
								type: "application/json"
							},
										optionalParameter,
							headers
						);
					}
		/**
		 * 
				 * @param testEntityId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putTestEntityStatusWithContentType(
						testEntityId: number,
					requestBody:
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										testEntity?: TestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: TestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.testEntity, "TestEntity"));
						}

			const path = this._basePath + "/test/v1.0/test-entities/{testEntityId}/status"
						.replace("{testEntityId}",encodeURIComponent(testEntityId))
				;

			const queryParameters: any = {};

						if (testEntityId === null || testEntityId === undefined) {
							throw new Error("Required parameter testEntityId was null or undefined when calling putTestEntityStatus.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TestEntity"), response};
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
							 * @param testEntityId
						 * @param testEntity
					 */
					public async putTestEntityStatus(
									testEntityId: number,
							testEntity?: TestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: TestEntity;
						response: Response;
					}> {
						return this.putTestEntityStatusWithContentType(
										testEntityId,
							{
								parameters: {
										testEntity: testEntity
								},
								type: "application/json"
							},
							headers
						);
					}
}