/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ObjectValidationRule} from '../models/ObjectValidationRule';
		import {PageObjectValidationRule} from '../models/PageObjectValidationRule';

/**
 * @author Javier Gamarra
 * @generated
 */

export class ObjectValidationRuleAPI {
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
				 * @param objectValidationRuleId
		 * @param headers Optional custom request headers
		 */
		public async deleteObjectValidationRule(
						objectValidationRuleId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-validation-rules/{objectValidationRuleId}"
						.replace("{objectValidationRuleId}",encodeURIComponent(objectValidationRuleId))
				;

			const queryParameters: any = {};

						if (objectValidationRuleId === null || objectValidationRuleId === undefined) {
							throw new Error("Required parameter objectValidationRuleId was null or undefined when calling deleteObjectValidationRule.");
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
		public async getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
						Accept_Language?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectValidationRule;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-validation-rules"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
																								;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectValidationRule"), response};
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
		public async getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId: number,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
						Accept_Language?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageObjectValidationRule;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-validation-rules"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
																								;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling getObjectDefinitionObjectValidationRulesPage.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageObjectValidationRule"), response};
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
				 * @param objectValidationRuleId
		 * @param headers Optional custom request headers
		 */
		public async getObjectValidationRule(
						objectValidationRuleId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectValidationRule;
			response: Response;
		}> {

			const path = this._basePath + "/object-admin/v1.0/object-validation-rules/{objectValidationRuleId}"
						.replace("{objectValidationRuleId}",encodeURIComponent(objectValidationRuleId))
				;

			const queryParameters: any = {};

						if (objectValidationRuleId === null || objectValidationRuleId === undefined) {
							throw new Error("Required parameter objectValidationRuleId was null or undefined when calling getObjectValidationRule.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectValidationRule"), response};
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
				 * @param objectValidationRuleId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchObjectValidationRuleWithContentType(
						objectValidationRuleId: number,
					requestBody:
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectValidationRule;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-validation-rules/{objectValidationRuleId}"
						.replace("{objectValidationRuleId}",encodeURIComponent(objectValidationRuleId))
				;

			const queryParameters: any = {};

						if (objectValidationRuleId === null || objectValidationRuleId === undefined) {
							throw new Error("Required parameter objectValidationRuleId was null or undefined when calling patchObjectValidationRule.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectValidationRule"), response};
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
							 * @param objectValidationRuleId
						 * @param objectValidationRule
					 */
					public async patchObjectValidationRule(
									objectValidationRuleId: number,
							objectValidationRule?: ObjectValidationRule,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectValidationRule;
						response: Response;
					}> {
						return this.patchObjectValidationRuleWithContentType(
										objectValidationRuleId,
							{
								parameters: {
										objectValidationRule: objectValidationRule
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
		public async postObjectDefinitionByExternalReferenceCodeObjectValidationRuleWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectValidationRule;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/by-external-reference-code/{externalReferenceCode}/object-validation-rules"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling postObjectDefinitionByExternalReferenceCodeObjectValidationRule.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectValidationRule"), response};
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
						 * @param objectValidationRule
					 */
					public async postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
									externalReferenceCode: string,
							objectValidationRule?: ObjectValidationRule,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectValidationRule;
						response: Response;
					}> {
						return this.postObjectDefinitionByExternalReferenceCodeObjectValidationRuleWithContentType(
										externalReferenceCode,
							{
								parameters: {
										objectValidationRule: objectValidationRule
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
		public async postObjectDefinitionObjectValidationRuleWithContentType(
						objectDefinitionId: number,
					requestBody:
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectValidationRule;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-validation-rules"
						.replace("{objectDefinitionId}",encodeURIComponent(objectDefinitionId))
				;

			const queryParameters: any = {};

						if (objectDefinitionId === null || objectDefinitionId === undefined) {
							throw new Error("Required parameter objectDefinitionId was null or undefined when calling postObjectDefinitionObjectValidationRule.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectValidationRule"), response};
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
						 * @param objectValidationRule
					 */
					public async postObjectDefinitionObjectValidationRule(
									objectDefinitionId: number,
							objectValidationRule?: ObjectValidationRule,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectValidationRule;
						response: Response;
					}> {
						return this.postObjectDefinitionObjectValidationRuleWithContentType(
										objectDefinitionId,
							{
								parameters: {
										objectValidationRule: objectValidationRule
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param objectValidationRuleId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putObjectValidationRuleWithContentType(
						objectValidationRuleId: number,
					requestBody:
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										objectValidationRule?: ObjectValidationRule
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ObjectValidationRule;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.objectValidationRule, "ObjectValidationRule"));
						}

			const path = this._basePath + "/object-admin/v1.0/object-validation-rules/{objectValidationRuleId}"
						.replace("{objectValidationRuleId}",encodeURIComponent(objectValidationRuleId))
				;

			const queryParameters: any = {};

						if (objectValidationRuleId === null || objectValidationRuleId === undefined) {
							throw new Error("Required parameter objectValidationRuleId was null or undefined when calling putObjectValidationRule.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ObjectValidationRule"), response};
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
							 * @param objectValidationRuleId
						 * @param objectValidationRule
					 */
					public async putObjectValidationRule(
									objectValidationRuleId: number,
							objectValidationRule?: ObjectValidationRule,
						headers?: {[name: string]: string}
					): Promise<{
							body: ObjectValidationRule;
						response: Response;
					}> {
						return this.putObjectValidationRuleWithContentType(
										objectValidationRuleId,
							{
								parameters: {
										objectValidationRule: objectValidationRule
								},
								type: "application/json"
							},
							headers
						);
					}
}