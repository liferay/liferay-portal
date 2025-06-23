/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {PageSiteTestEntity} from '../models/PageSiteTestEntity';
		import {SiteTestEntity} from '../models/SiteTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class SiteTestEntityAPI {
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
				 * @param siteId
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteSiteTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteId}/site-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
										.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteSiteSiteTestEntityByExternalReferenceCode.");
						}

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling deleteSiteSiteTestEntityByExternalReferenceCode.");
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
		 * @param headers Optional custom request headers
		 */
		public async getSiteSiteTestEntitiesPage(
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageSiteTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteId}/site-test-entities"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteSiteTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageSiteTestEntity"), response};
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
				 * @param siteId
		 * @param headers Optional custom request headers
		 */
		public async getSiteSiteTestEntityByExternalReferenceCode(
						externalReferenceCode: string,
						siteId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: SiteTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteId}/site-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
										.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getSiteSiteTestEntityByExternalReferenceCode.");
						}

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling getSiteSiteTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SiteTestEntity"), response};
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
				 * @param siteTestEntityId
		 * @param headers Optional custom request headers
		 */
		public async getSiteTestEntity(
						siteTestEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: SiteTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/site-test-entities/{siteTestEntityId}"
						.replace("{siteTestEntityId}",encodeURIComponent(siteTestEntityId))
				;

			const queryParameters: any = {};

						if (siteTestEntityId === null || siteTestEntityId === undefined) {
							throw new Error("Required parameter siteTestEntityId was null or undefined when calling getSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SiteTestEntity"), response};
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
				 * @param siteTestEntityId
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getSiteTestEntityPermissionsPage(
						siteTestEntityId: number,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/site-test-entities/{siteTestEntityId}/permissions"
						.replace("{siteTestEntityId}",encodeURIComponent(siteTestEntityId))
								;

			const queryParameters: any = {};

						if (siteTestEntityId === null || siteTestEntityId === undefined) {
							throw new Error("Required parameter siteTestEntityId was null or undefined when calling getSiteTestEntityPermissionsPage.");
						}

						if (roleNames !== undefined) {
							queryParameters["roleNames"] = ObjectSerializer.serialize(roleNames, "string");
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
		 * Updates only the fields received in the request body, leaving any other fields untouched.
				 * @param siteTestEntityId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchSiteTestEntityWithContentType(
						siteTestEntityId: number,
					requestBody:
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: SiteTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/site-test-entities/{siteTestEntityId}"
						.replace("{siteTestEntityId}",encodeURIComponent(siteTestEntityId))
				;

			const queryParameters: any = {};

						if (siteTestEntityId === null || siteTestEntityId === undefined) {
							throw new Error("Required parameter siteTestEntityId was null or undefined when calling patchSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SiteTestEntity"), response};
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
					 * Updates only the fields received in the request body, leaving any other fields untouched. - Default method for JSON body
							 * @param siteTestEntityId
						 * @param siteTestEntity
					 */
					public async patchSiteTestEntity(
									siteTestEntityId: number,
							siteTestEntity?: SiteTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: SiteTestEntity;
						response: Response;
					}> {
						return this.patchSiteTestEntityWithContentType(
										siteTestEntityId,
							{
								parameters: {
										siteTestEntity: siteTestEntity
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
		public async postSiteSiteTestEntityWithContentType(
						siteId: number,
					requestBody:
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: SiteTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteId}/site-test-entities"
						.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling postSiteSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SiteTestEntity"), response};
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
						 * @param siteTestEntity
					 */
					public async postSiteSiteTestEntity(
									siteId: number,
							siteTestEntity?: SiteTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: SiteTestEntity;
						response: Response;
					}> {
						return this.postSiteSiteTestEntityWithContentType(
										siteId,
							{
								parameters: {
										siteTestEntity: siteTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param externalReferenceCode
				 * @param siteId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteSiteTestEntityByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
						siteId: number,
					requestBody:
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: SiteTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteId}/site-test-entities/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
										.replace("{siteId}",encodeURIComponent(siteId))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling putSiteSiteTestEntityByExternalReferenceCode.");
						}

						if (siteId === null || siteId === undefined) {
							throw new Error("Required parameter siteId was null or undefined when calling putSiteSiteTestEntityByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SiteTestEntity"), response};
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
							 * @param siteId
						 * @param siteTestEntity
					 */
					public async putSiteSiteTestEntityByExternalReferenceCode(
									externalReferenceCode: string,
									siteId: number,
							siteTestEntity?: SiteTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: SiteTestEntity;
						response: Response;
					}> {
						return this.putSiteSiteTestEntityByExternalReferenceCodeWithContentType(
										externalReferenceCode,
										siteId,
							{
								parameters: {
										siteTestEntity: siteTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteTestEntityId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteTestEntityWithContentType(
						siteTestEntityId: number,
					requestBody:
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										siteTestEntity?: SiteTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: SiteTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.siteTestEntity, "SiteTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/site-test-entities/{siteTestEntityId}"
						.replace("{siteTestEntityId}",encodeURIComponent(siteTestEntityId))
				;

			const queryParameters: any = {};

						if (siteTestEntityId === null || siteTestEntityId === undefined) {
							throw new Error("Required parameter siteTestEntityId was null or undefined when calling putSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "SiteTestEntity"), response};
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
							 * @param siteTestEntityId
						 * @param siteTestEntity
					 */
					public async putSiteTestEntity(
									siteTestEntityId: number,
							siteTestEntity?: SiteTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: SiteTestEntity;
						response: Response;
					}> {
						return this.putSiteTestEntityWithContentType(
										siteTestEntityId,
							{
								parameters: {
										siteTestEntity: siteTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteTestEntityId
		 * @param headers Optional custom request headers
		 */
		public async putSiteTestEntityPermissionsPage(
						siteTestEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/site-test-entities/{siteTestEntityId}/permissions"
						.replace("{siteTestEntityId}",encodeURIComponent(siteTestEntityId))
				;

			const queryParameters: any = {};

						if (siteTestEntityId === null || siteTestEntityId === undefined) {
							throw new Error("Required parameter siteTestEntityId was null or undefined when calling putSiteTestEntityPermissionsPage.");
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
				method: "PUT",
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

}