/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {CTRemote} from '../models/CTRemote';
		import {PageCTRemote} from '../models/PageCTRemote';

/**
 * @author David Truong
 * @generated
 */

export class CTRemoteAPI {
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
				 * @param id
		 * @param headers Optional custom request headers
		 */
		public async deleteCTRemote(
						id: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-remotes/{id}"
						.replace("{id}",encodeURIComponent(id))
				;

			const queryParameters: any = {};

						if (id === null || id === undefined) {
							throw new Error("Required parameter id was null or undefined when calling deleteCTRemote.");
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
				 * @param id
		 * @param headers Optional custom request headers
		 */
		public async getCTRemote(
						id: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTRemote;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-remotes/{id}"
						.replace("{id}",encodeURIComponent(id))
				;

			const queryParameters: any = {};

						if (id === null || id === undefined) {
							throw new Error("Required parameter id was null or undefined when calling getCTRemote.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTRemote"), response};
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
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
		 * @param headers Optional custom request headers
		 */
		public async getCTRemotesPage(
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageCTRemote;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-remotes"
																;

			const queryParameters: any = {};

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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageCTRemote"), response};
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
				 * @param id
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchCTRemoteWithContentType(
						id: number,
					requestBody:
							{
								parameters: {
										cTRemote?: CTRemote
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTRemote?: CTRemote
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTRemote;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTRemote, "CTRemote"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTRemote, "CTRemote"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-remotes/{id}"
						.replace("{id}",encodeURIComponent(id))
				;

			const queryParameters: any = {};

						if (id === null || id === undefined) {
							throw new Error("Required parameter id was null or undefined when calling patchCTRemote.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTRemote"), response};
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
							 * @param id
						 * @param cTRemote
					 */
					public async patchCTRemote(
									id: number,
							cTRemote?: CTRemote,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTRemote;
						response: Response;
					}> {
						return this.patchCTRemoteWithContentType(
										id,
							{
								parameters: {
										cTRemote: cTRemote
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
		public async postCTRemoteWithContentType(
					requestBody:
							{
								parameters: {
										cTRemote?: CTRemote
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTRemote?: CTRemote
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTRemote;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTRemote, "CTRemote"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTRemote, "CTRemote"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-remotes"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTRemote"), response};
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
						 * @param cTRemote
					 */
					public async postCTRemote(
							cTRemote?: CTRemote,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTRemote;
						response: Response;
					}> {
						return this.postCTRemoteWithContentType(
							{
								parameters: {
										cTRemote: cTRemote
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param id
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putCTRemoteWithContentType(
						id: number,
					requestBody:
							{
								parameters: {
										cTRemote?: CTRemote
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTRemote?: CTRemote
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTRemote;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTRemote, "CTRemote"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTRemote, "CTRemote"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-remotes/{id}"
						.replace("{id}",encodeURIComponent(id))
				;

			const queryParameters: any = {};

						if (id === null || id === undefined) {
							throw new Error("Required parameter id was null or undefined when calling putCTRemote.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTRemote"), response};
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
							 * @param id
						 * @param cTRemote
					 */
					public async putCTRemote(
									id: number,
							cTRemote?: CTRemote,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTRemote;
						response: Response;
					}> {
						return this.putCTRemoteWithContentType(
										id,
							{
								parameters: {
										cTRemote: cTRemote
								},
								type: "application/json"
							},
							headers
						);
					}
}