/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ObjectAction} from '../models/ObjectAction';
		import {PageObjectAction} from '../models/PageObjectAction';

/**
 * @author Javier Gamarra
 * @generated
 */

export class ObjectActionAPI {
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
				 * @param objectActionId
		 * @param headers Optional custom request headers
		 */
		public async deleteObjectAction(
						objectActionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-actions/{objectActionId}"
						.replace("{objectActionId}",encodeURIComponent(objectActionId))
				;

			const queryParameters: any = {};

						if (objectActionId === null || objectActionId === undefined) {
							throw new Error("Required parameter objectActionId was null or undefined when calling deleteObjectAction.");
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
				 * @param objectActionId
		 * @param headers Optional custom request headers
		 */
		public async getObjectAction(
						objectActionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectAction;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-actions/{objectActionId}"
						.replace("{objectActionId}",encodeURIComponent(objectActionId))
				;

			const queryParameters: any = {};

						if (objectActionId === null || objectActionId === undefined) {
							throw new Error("Required parameter objectActionId was null or undefined when calling getObjectAction.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectAction"), response};
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
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectAction;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-actions"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
																				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getObjectDefinitionByExternalReferenceCodeObjectActionsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectAction"), response};
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
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getObjectDefinitionObjectActionsPage(
						objectDefinitionId: number,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectAction;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-actions"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
																				;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling getObjectDefinitionObjectActionsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectAction"), response};
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
				 * @param objectActionId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchObjectActionWithContentType(
						objectActionId: number,
					requestBody:
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectAction;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-actions/{objectActionId}"
						.replace("{objectActionId}",encodeURIComponent(objectActionId))
				;

			const queryParameters: any = {};

						if (objectActionId === null || objectActionId === undefined) {
							throw new Error("Required parameter objectActionId was null or undefined when calling patchObjectAction.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectAction"), response};
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
							 * @param objectActionId
						 * @param objectAction
					 */
					public async patchObjectAction(
									objectActionId: number,
							objectAction?: ObjectAction,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectAction;
						response: Response;
					}> {
						return this.patchObjectActionWithContentType(
										objectActionId,
							{
								parameters: {
										objectAction: objectAction
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
		public async postObjectDefinitionByExternalReferenceCodeObjectActionWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectAction;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-actions"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling postObjectDefinitionByExternalReferenceCodeObjectAction.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectAction"), response};
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
						 * @param objectAction
					 */
					public async postObjectDefinitionByExternalReferenceCodeObjectAction(
									externalReferenceCode: string,
							objectAction?: ObjectAction,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectAction;
						response: Response;
					}> {
						return this.postObjectDefinitionByExternalReferenceCodeObjectActionWithContentType(
										externalReferenceCode,
							{
								parameters: {
										objectAction: objectAction
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
		public async postObjectDefinitionObjectActionWithContentType(
						objectDefinitionId: number,
					requestBody:
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectAction;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-actions"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
				;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling postObjectDefinitionObjectAction.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectAction"), response};
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
						 * @param objectAction
					 */
					public async postObjectDefinitionObjectAction(
									objectDefinitionId: number,
							objectAction?: ObjectAction,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectAction;
						response: Response;
					}> {
						return this.postObjectDefinitionObjectActionWithContentType(
										objectDefinitionId,
							{
								parameters: {
										objectAction: objectAction
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param objectActionId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putObjectActionWithContentType(
						objectActionId: number,
					requestBody:
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectAction?: ObjectAction
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectAction;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectAction, "ObjectAction"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-actions/{objectActionId}"
						.replace("{objectActionId}",encodeURIComponent(objectActionId))
				;

			const queryParameters: any = {};

						if (objectActionId === null || objectActionId === undefined) {
							throw new Error("Required parameter objectActionId was null or undefined when calling putObjectAction.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectAction"), response};
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
							 * @param objectActionId
						 * @param objectAction
					 */
					public async putObjectAction(
									objectActionId: number,
							objectAction?: ObjectAction,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectAction;
						response: Response;
					}> {
						return this.putObjectActionWithContentType(
										objectActionId,
							{
								parameters: {
										objectAction: objectAction
								},
								type: "application/json"
							},
							headers
						);
					}
}