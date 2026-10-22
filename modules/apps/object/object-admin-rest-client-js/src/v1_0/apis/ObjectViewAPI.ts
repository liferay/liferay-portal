/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ObjectView} from '../models/ObjectView';
		import {PageObjectView} from '../models/PageObjectView';

/**
 * @author Javier Gamarra
 * @generated
 */

export class ObjectViewAPI {
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
				 * @param objectViewId
		 * @param headers Optional custom request headers
		 */
		public async deleteObjectView(
						objectViewId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-views/{objectViewId}"
						.replace("{objectViewId}",encodeURIComponent(objectViewId))
				;

			const queryParameters: any = {};

						if (objectViewId === null || objectViewId === undefined) {
							throw new Error("Required parameter objectViewId was null or undefined when calling deleteObjectView.");
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
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
				 * @param Accept_Language
		 * @param headers Optional custom request headers
		 */
		public async getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
						Accept_Language?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectView;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-views"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
																								;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getObjectDefinitionByExternalReferenceCodeObjectViewsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectView"), response};
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
				 * @param Accept_Language
		 * @param headers Optional custom request headers
		 */
		public async getObjectDefinitionObjectViewsPage(
						objectDefinitionId: number,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
						Accept_Language?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectView;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-views"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
																								;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling getObjectDefinitionObjectViewsPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectView"), response};
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
				 * @param objectViewId
		 * @param headers Optional custom request headers
		 */
		public async getObjectView(
						objectViewId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectView;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-views/{objectViewId}"
						.replace("{objectViewId}",encodeURIComponent(objectViewId))
				;

			const queryParameters: any = {};

						if (objectViewId === null || objectViewId === undefined) {
							throw new Error("Required parameter objectViewId was null or undefined when calling getObjectView.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectView"), response};
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
		public async postObjectDefinitionByExternalReferenceCodeObjectViewWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										objectView?: ObjectView
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectView?: ObjectView
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectView;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectView, "ObjectView"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectView, "ObjectView"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-views"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling postObjectDefinitionByExternalReferenceCodeObjectView.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectView"), response};
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
						 * @param objectView
					 */
					public async postObjectDefinitionByExternalReferenceCodeObjectView(
									externalReferenceCode: string,
							objectView?: ObjectView,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectView;
						response: Response;
					}> {
						return this.postObjectDefinitionByExternalReferenceCodeObjectViewWithContentType(
										externalReferenceCode,
							{
								parameters: {
										objectView: objectView
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
		public async postObjectDefinitionObjectViewWithContentType(
						objectDefinitionId: number,
					requestBody:
							{
								parameters: {
										objectView?: ObjectView
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectView?: ObjectView
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectView;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectView, "ObjectView"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectView, "ObjectView"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-views"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
				;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling postObjectDefinitionObjectView.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectView"), response};
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
						 * @param objectView
					 */
					public async postObjectDefinitionObjectView(
									objectDefinitionId: number,
							objectView?: ObjectView,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectView;
						response: Response;
					}> {
						return this.postObjectDefinitionObjectViewWithContentType(
										objectDefinitionId,
							{
								parameters: {
										objectView: objectView
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param objectViewId
		 * @param headers Optional custom request headers
		 */
		public async postObjectViewCopy(
						objectViewId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectView;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-views/{objectViewId}/copy"
						.replace("{objectViewId}",encodeURIComponent(objectViewId))
				;

			const queryParameters: any = {};

						if (objectViewId === null || objectViewId === undefined) {
							throw new Error("Required parameter objectViewId was null or undefined when calling postObjectViewCopy.");
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
				method: "POST",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectView"), response};
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
				 * @param objectViewId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putObjectViewWithContentType(
						objectViewId: number,
					requestBody:
							{
								parameters: {
										objectView?: ObjectView
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectView?: ObjectView
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectView;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectView, "ObjectView"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectView, "ObjectView"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-views/{objectViewId}"
						.replace("{objectViewId}",encodeURIComponent(objectViewId))
				;

			const queryParameters: any = {};

						if (objectViewId === null || objectViewId === undefined) {
							throw new Error("Required parameter objectViewId was null or undefined when calling putObjectView.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectView"), response};
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
							 * @param objectViewId
						 * @param objectView
					 */
					public async putObjectView(
									objectViewId: number,
							objectView?: ObjectView,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectView;
						response: Response;
					}> {
						return this.putObjectViewWithContentType(
										objectViewId,
							{
								parameters: {
										objectView: objectView
								},
								type: "application/json"
							},
							headers
						);
					}
}