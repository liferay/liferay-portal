/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ObjectFolder} from '../models/ObjectFolder';
		import {PageObjectFolder} from '../models/PageObjectFolder';

/**
 * @author Javier Gamarra
 * @generated
 */

export class ObjectFolderAPI {
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
				 * @param objectFolderId
		 * @param headers Optional custom request headers
		 */
		public async deleteObjectFolder(
						objectFolderId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-folders/{objectFolderId}"
						.replace("{objectFolderId}",encodeURIComponent(objectFolderId))
				;

			const queryParameters: any = {};

						if (objectFolderId === null || objectFolderId === undefined) {
							throw new Error("Required parameter objectFolderId was null or undefined when calling deleteObjectFolder.");
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
				 * @param objectFolderId
		 * @param headers Optional custom request headers
		 */
		public async getObjectFolder(
						objectFolderId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectFolder;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-folders/{objectFolderId}"
						.replace("{objectFolderId}",encodeURIComponent(objectFolderId))
				;

			const queryParameters: any = {};

						if (objectFolderId === null || objectFolderId === undefined) {
							throw new Error("Required parameter objectFolderId was null or undefined when calling getObjectFolder.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectFolder"), response};
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
		public async getObjectFolderByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectFolder;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-folders/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getObjectFolderByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectFolder"), response};
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
				 * @param Accept_Language
		 * @param headers Optional custom request headers
		 */
		public async getObjectFoldersPage(
						page?: number,
						pageSize?: number,
						search?: string,
						Accept_Language?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectFolder;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-folders"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectFolder"), response};
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
				 * @param objectFolderId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchObjectFolderWithContentType(
						objectFolderId: number,
					requestBody:
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectFolder;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-folders/{objectFolderId}"
						.replace("{objectFolderId}",encodeURIComponent(objectFolderId))
				;

			const queryParameters: any = {};

						if (objectFolderId === null || objectFolderId === undefined) {
							throw new Error("Required parameter objectFolderId was null or undefined when calling patchObjectFolder.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectFolder"), response};
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
							 * @param objectFolderId
						 * @param objectFolder
					 */
					public async patchObjectFolder(
									objectFolderId: number,
							objectFolder?: ObjectFolder,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectFolder;
						response: Response;
					}> {
						return this.patchObjectFolderWithContentType(
										objectFolderId,
							{
								parameters: {
										objectFolder: objectFolder
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
		public async postObjectFolderWithContentType(
					requestBody:
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectFolder;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-folders"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectFolder"), response};
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
						 * @param objectFolder
					 */
					public async postObjectFolder(
							objectFolder?: ObjectFolder,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectFolder;
						response: Response;
					}> {
						return this.postObjectFolderWithContentType(
							{
								parameters: {
										objectFolder: objectFolder
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param objectFolderId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putObjectFolderWithContentType(
						objectFolderId: number,
					requestBody:
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectFolder;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-folders/{objectFolderId}"
						.replace("{objectFolderId}",encodeURIComponent(objectFolderId))
				;

			const queryParameters: any = {};

						if (objectFolderId === null || objectFolderId === undefined) {
							throw new Error("Required parameter objectFolderId was null or undefined when calling putObjectFolder.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectFolder"), response};
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
							 * @param objectFolderId
						 * @param objectFolder
					 */
					public async putObjectFolder(
									objectFolderId: number,
							objectFolder?: ObjectFolder,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectFolder;
						response: Response;
					}> {
						return this.putObjectFolderWithContentType(
										objectFolderId,
							{
								parameters: {
										objectFolder: objectFolder
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
		public async putObjectFolderByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectFolder?: ObjectFolder
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectFolder;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectFolder, "ObjectFolder"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-folders/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putObjectFolderByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectFolder"), response};
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
						 * @param objectFolder
					 */
					public async putObjectFolderByExternalReferenceCode(
									externalReferenceCode: string,
							objectFolder?: ObjectFolder,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectFolder;
						response: Response;
					}> {
						return this.putObjectFolderByExternalReferenceCodeWithContentType(
										externalReferenceCode,
							{
								parameters: {
										objectFolder: objectFolder
								},
								type: "application/json"
							},
							headers
						);
					}
}