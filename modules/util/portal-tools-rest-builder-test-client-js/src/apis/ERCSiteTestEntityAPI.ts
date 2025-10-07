/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ERCSiteTestEntity} from '../models/ERCSiteTestEntity';
		import {PageERCSiteTestEntity} from '../models/PageERCSiteTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class ERCSiteTestEntityAPI {
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
				 * @param siteExternalReferenceCode
				 * @param ercSiteTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async deleteSiteERCSiteTestEntity(
						siteExternalReferenceCode: string,
						ercSiteTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities/{ercSiteTestEntityExternalReferenceCode}"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercSiteTestEntityExternalReferenceCode}",encodeURIComponent(ercSiteTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling deleteSiteERCSiteTestEntity.");
						}

						if (ercSiteTestEntityExternalReferenceCode === null || ercSiteTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercSiteTestEntityExternalReferenceCode was null or undefined when calling deleteSiteERCSiteTestEntity.");
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
				 * @param siteExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getSiteERCSiteTestEntitiesPage(
						siteExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageERCSiteTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling getSiteERCSiteTestEntitiesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageERCSiteTestEntity"), response};
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
				 * @param siteExternalReferenceCode
				 * @param ercSiteTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async getSiteERCSiteTestEntity(
						siteExternalReferenceCode: string,
						ercSiteTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCSiteTestEntity;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities/{ercSiteTestEntityExternalReferenceCode}"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercSiteTestEntityExternalReferenceCode}",encodeURIComponent(ercSiteTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling getSiteERCSiteTestEntity.");
						}

						if (ercSiteTestEntityExternalReferenceCode === null || ercSiteTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercSiteTestEntityExternalReferenceCode was null or undefined when calling getSiteERCSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCSiteTestEntity"), response};
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
				 * @param siteExternalReferenceCode
				 * @param ercSiteTestEntityExternalReferenceCode
				 * @param fields
				 * @param restrictFields
				 * @param roleNames
		 * @param headers Optional custom request headers
		 */
		public async getSiteERCSiteTestEntityPermissionsPage(
						siteExternalReferenceCode: string,
						ercSiteTestEntityExternalReferenceCode: string,
						fields?: string,
						restrictFields?: string,
						roleNames?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities/{ercSiteTestEntityExternalReferenceCode}/permissions"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercSiteTestEntityExternalReferenceCode}",encodeURIComponent(ercSiteTestEntityExternalReferenceCode))
																;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling getSiteERCSiteTestEntityPermissionsPage.");
						}

						if (ercSiteTestEntityExternalReferenceCode === null || ercSiteTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercSiteTestEntityExternalReferenceCode was null or undefined when calling getSiteERCSiteTestEntityPermissionsPage.");
						}

						if (fields !== undefined) {
							queryParameters["fields"] = ObjectSerializer.serialize(fields, "string");
						}

						if (restrictFields !== undefined) {
							queryParameters["restrictFields"] = ObjectSerializer.serialize(restrictFields, "string");
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
		 * 
				 * @param siteExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postSiteERCSiteTestEntityWithContentType(
						siteExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCSiteTestEntity?: ERCSiteTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCSiteTestEntity?: ERCSiteTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCSiteTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCSiteTestEntity, "ERCSiteTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCSiteTestEntity, "ERCSiteTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling postSiteERCSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCSiteTestEntity"), response};
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
							 * @param siteExternalReferenceCode
						 * @param eRCSiteTestEntity
					 */
					public async postSiteERCSiteTestEntity(
									siteExternalReferenceCode: string,
							eRCSiteTestEntity?: ERCSiteTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCSiteTestEntity;
						response: Response;
					}> {
						return this.postSiteERCSiteTestEntityWithContentType(
										siteExternalReferenceCode,
							{
								parameters: {
										eRCSiteTestEntity: eRCSiteTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteExternalReferenceCode
				 * @param ercSiteTestEntityExternalReferenceCode
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putSiteERCSiteTestEntityWithContentType(
						siteExternalReferenceCode: string,
						ercSiteTestEntityExternalReferenceCode: string,
					requestBody:
							{
								parameters: {
										eRCSiteTestEntity?: ERCSiteTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										eRCSiteTestEntity?: ERCSiteTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ERCSiteTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCSiteTestEntity, "ERCSiteTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.eRCSiteTestEntity, "ERCSiteTestEntity"));
						}

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities/{ercSiteTestEntityExternalReferenceCode}"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercSiteTestEntityExternalReferenceCode}",encodeURIComponent(ercSiteTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling putSiteERCSiteTestEntity.");
						}

						if (ercSiteTestEntityExternalReferenceCode === null || ercSiteTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercSiteTestEntityExternalReferenceCode was null or undefined when calling putSiteERCSiteTestEntity.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ERCSiteTestEntity"), response};
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
							 * @param siteExternalReferenceCode
							 * @param ercSiteTestEntityExternalReferenceCode
						 * @param eRCSiteTestEntity
					 */
					public async putSiteERCSiteTestEntity(
									siteExternalReferenceCode: string,
									ercSiteTestEntityExternalReferenceCode: string,
							eRCSiteTestEntity?: ERCSiteTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ERCSiteTestEntity;
						response: Response;
					}> {
						return this.putSiteERCSiteTestEntityWithContentType(
										siteExternalReferenceCode,
										ercSiteTestEntityExternalReferenceCode,
							{
								parameters: {
										eRCSiteTestEntity: eRCSiteTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param siteExternalReferenceCode
				 * @param ercSiteTestEntityExternalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async putSiteERCSiteTestEntityPermissionsPage(
						siteExternalReferenceCode: string,
						ercSiteTestEntityExternalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities/{ercSiteTestEntityExternalReferenceCode}/permissions"
						.replace("{siteExternalReferenceCode}",encodeURIComponent(siteExternalReferenceCode))
										.replace("{ercSiteTestEntityExternalReferenceCode}",encodeURIComponent(ercSiteTestEntityExternalReferenceCode))
				;

			const queryParameters: any = {};

						if (siteExternalReferenceCode === null || siteExternalReferenceCode === undefined) {
							throw new Error("Required parameter siteExternalReferenceCode was null or undefined when calling putSiteERCSiteTestEntityPermissionsPage.");
						}

						if (ercSiteTestEntityExternalReferenceCode === null || ercSiteTestEntityExternalReferenceCode === undefined) {
							throw new Error("Required parameter ercSiteTestEntityExternalReferenceCode was null or undefined when calling putSiteERCSiteTestEntityPermissionsPage.");
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