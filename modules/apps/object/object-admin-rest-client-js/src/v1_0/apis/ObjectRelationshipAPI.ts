/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ObjectRelationship} from '../models/ObjectRelationship';
		import {PageObjectRelationship} from '../models/PageObjectRelationship';

/**
 * @author Javier Gamarra
 * @generated
 */

export class ObjectRelationshipAPI {
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
				 * @param objectRelationshipId
		 * @param headers Optional custom request headers
		 */
		public async deleteObjectRelationship(
						objectRelationshipId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-relationships/{objectRelationshipId}"
						.replace("{objectRelationshipId}",encodeURIComponent(objectRelationshipId))
				;

			const queryParameters: any = {};

						if (objectRelationshipId === null || objectRelationshipId === undefined) {
							throw new Error("Required parameter objectRelationshipId was null or undefined when calling deleteObjectRelationship.");
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
				 * @param externalReferenceCode
				 * @param filter
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode: string,
						filter?: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectRelationship;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-relationships"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
																								;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectRelationship"), response};
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
				 * @param objectDefinitionId
				 * @param filter
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId: number,
						filter?: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectRelationship;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-relationships"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
																								;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling getObjectDefinitionObjectRelationshipsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectRelationship"), response};
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
				 * @param objectRelationshipId
		 * @param headers Optional custom request headers
		 */
		public async getObjectRelationship(
						objectRelationshipId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectRelationship;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-relationships/{objectRelationshipId}"
						.replace("{objectRelationshipId}",encodeURIComponent(objectRelationshipId))
				;

			const queryParameters: any = {};

						if (objectRelationshipId === null || objectRelationshipId === undefined) {
							throw new Error("Required parameter objectRelationshipId was null or undefined when calling getObjectRelationship.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectRelationship"), response};
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
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postObjectDefinitionByExternalReferenceCodeObjectRelationshipWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectRelationship;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-relationships"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling postObjectDefinitionByExternalReferenceCodeObjectRelationship.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectRelationship"), response};
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
						 * @param objectRelationship
					 */
					public async postObjectDefinitionByExternalReferenceCodeObjectRelationship(
									externalReferenceCode: string,
							objectRelationship?: ObjectRelationship,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectRelationship;
						response: Response;
					}> {
						return this.postObjectDefinitionByExternalReferenceCodeObjectRelationshipWithContentType(
										externalReferenceCode,
							{
								parameters: {
										objectRelationship: objectRelationship
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param objectDefinitionId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postObjectDefinitionObjectRelationshipWithContentType(
						objectDefinitionId: number,
					requestBody:
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectRelationship;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-relationships"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
				;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling postObjectDefinitionObjectRelationship.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectRelationship"), response};
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
							 * @param objectDefinitionId
						 * @param objectRelationship
					 */
					public async postObjectDefinitionObjectRelationship(
									objectDefinitionId: number,
							objectRelationship?: ObjectRelationship,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectRelationship;
						response: Response;
					}> {
						return this.postObjectDefinitionObjectRelationshipWithContentType(
										objectDefinitionId,
							{
								parameters: {
										objectRelationship: objectRelationship
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param objectRelationshipId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putObjectRelationshipWithContentType(
						objectRelationshipId: number,
					requestBody:
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectRelationship;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-relationships/{objectRelationshipId}"
						.replace("{objectRelationshipId}",encodeURIComponent(objectRelationshipId))
				;

			const queryParameters: any = {};

						if (objectRelationshipId === null || objectRelationshipId === undefined) {
							throw new Error("Required parameter objectRelationshipId was null or undefined when calling putObjectRelationship.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectRelationship"), response};
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
							 * @param objectRelationshipId
						 * @param objectRelationship
					 */
					public async putObjectRelationship(
									objectRelationshipId: number,
							objectRelationship?: ObjectRelationship,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectRelationship;
						response: Response;
					}> {
						return this.putObjectRelationshipWithContentType(
										objectRelationshipId,
							{
								parameters: {
										objectRelationship: objectRelationship
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
		public async putObjectRelationshipByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectRelationship?: ObjectRelationship
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectRelationship;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectRelationship, "ObjectRelationship"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-relationships/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putObjectRelationshipByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectRelationship"), response};
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
						 * @param objectRelationship
					 */
					public async putObjectRelationshipByExternalReferenceCode(
									externalReferenceCode: string,
							objectRelationship?: ObjectRelationship,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectRelationship;
						response: Response;
					}> {
						return this.putObjectRelationshipByExternalReferenceCodeWithContentType(
										externalReferenceCode,
							{
								parameters: {
										objectRelationship: objectRelationship
								},
								type: "application/json"
							},
							headers
						);
					}
}