/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {CTCollection} from '../models/CTCollection';
		import {PageCTCollection} from '../models/PageCTCollection';

/**
 * @author David Truong
 * @generated
 */

export class CTCollectionAPI {
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
				 * @param ctCollectionId
		 * @param headers Optional custom request headers
		 */
		public async deleteCTCollection(
						ctCollectionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling deleteCTCollection.");
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
		 * @param headers Optional custom request headers
		 */
		public async deleteCTCollectionByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling deleteCTCollectionByExternalReferenceCode.");
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
				 * @param ctCollectionId
		 * @param headers Optional custom request headers
		 */
		public async getCTCollection(
						ctCollectionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTCollection;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling getCTCollection.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTCollection"), response};
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
		public async getCTCollectionByExternalReferenceCode(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTCollection;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getCTCollectionByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTCollection"), response};
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
		public async getCTCollectionByExternalReferenceCodeShareLink(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body: string;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/by-external-reference-code/{externalReferenceCode}/share-link"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling getCTCollectionByExternalReferenceCodeShareLink.");
						}

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
						return {body: ObjectSerializer.deserialize(await response.json(), "string"), response};
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
				 * @param ctCollectionId
		 * @param headers Optional custom request headers
		 */
		public async getCTCollectionShareLink(
						ctCollectionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: string;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/b{ctCollectionId}/share-link"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling getCTCollectionShareLink.");
						}

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
						return {body: ObjectSerializer.deserialize(await response.json(), "string"), response};
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
				 * @param filter
				 * @param page
				 * @param pageSize
				 * @param search
				 * @param sort
				 * @param status
		 * @param headers Optional custom request headers
		 */
		public async getCTCollectionsPage(
						filter?: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
						status?: Array<number>,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageCTCollection;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections"
																								;

			const queryParameters: any = {};

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

						if (status !== undefined) {
							queryParameters["status"] = ObjectSerializer.serialize(status, "Array<number>");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageCTCollection"), response};
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
				 * @param ctCollectionId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async patchCTCollectionWithContentType(
						ctCollectionId: number,
					requestBody:
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTCollection;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling patchCTCollection.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTCollection"), response};
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
							 * @param ctCollectionId
						 * @param cTCollection
					 */
					public async patchCTCollection(
									ctCollectionId: number,
							cTCollection?: CTCollection,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTCollection;
						response: Response;
					}> {
						return this.patchCTCollectionWithContentType(
										ctCollectionId,
							{
								parameters: {
										cTCollection: cTCollection
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
		public async patchCTCollectionByExternalReferenceCodeWithContentType(
						externalReferenceCode: string,
					requestBody:
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTCollection;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/by-external-reference-code/{externalReferenceCode}"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling patchCTCollectionByExternalReferenceCode.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTCollection"), response};
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
						 * @param cTCollection
					 */
					public async patchCTCollectionByExternalReferenceCode(
									externalReferenceCode: string,
							cTCollection?: CTCollection,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTCollection;
						response: Response;
					}> {
						return this.patchCTCollectionByExternalReferenceCodeWithContentType(
										externalReferenceCode,
							{
								parameters: {
										cTCollection: cTCollection
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
		public async postCTCollectionWithContentType(
					requestBody:
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTCollection;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTCollection"), response};
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
						 * @param cTCollection
					 */
					public async postCTCollection(
							cTCollection?: CTCollection,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTCollection;
						response: Response;
					}> {
						return this.postCTCollectionWithContentType(
							{
								parameters: {
										cTCollection: cTCollection
								},
								type: "application/json"
							},
							headers
						);
					}
		/**
		 * 
				 * @param externalReferenceCode
		 * @param headers Optional custom request headers
		 */
		public async postCTCollectionByExternalReferenceCodePublish(
						externalReferenceCode: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/by-external-reference-code/{externalReferenceCode}/publish"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
				;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling postCTCollectionByExternalReferenceCodePublish.");
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
				 * @param publishDate
		 * @param headers Optional custom request headers
		 */
		public async postCTCollectionByExternalReferenceCodeSchedulePublish(
						externalReferenceCode: string,
						publishDate?: Date,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/by-external-reference-code/{externalReferenceCode}/schedule-publish"
						.replace("{externalReferenceCode}",encodeURIComponent(externalReferenceCode))
								;

			const queryParameters: any = {};

						if (externalReferenceCode === null || externalReferenceCode === undefined) {
							throw new Error("Required parameter externalReferenceCode was null or undefined when calling postCTCollectionByExternalReferenceCodeSchedulePublish.");
						}

						if (publishDate !== undefined) {
							queryParameters["publishDate"] = ObjectSerializer.serialize(publishDate, "Date");
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
				 * @param ctCollectionId
		 * @param headers Optional custom request headers
		 */
		public async postCTCollectionCheckout(
						ctCollectionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}/checkout"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling postCTCollectionCheckout.");
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
				 * @param ctCollectionId
		 * @param headers Optional custom request headers
		 */
		public async postCTCollectionPublish(
						ctCollectionId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}/publish"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling postCTCollectionPublish.");
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
				 * @param ctCollectionId
				 * @param publishDate
		 * @param headers Optional custom request headers
		 */
		public async postCTCollectionSchedulePublish(
						ctCollectionId: number,
						publishDate?: Date,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}/schedule-publish"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
								;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling postCTCollectionSchedulePublish.");
						}

						if (publishDate !== undefined) {
							queryParameters["publishDate"] = ObjectSerializer.serialize(publishDate, "Date");
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
				 * @param ctCollectionId
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async putCTCollectionWithContentType(
						ctCollectionId: number,
					requestBody:
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										cTCollection?: CTCollection
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTCollection;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.cTCollection, "CTCollection"));
						}

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-collections/{ctCollectionId}"
						.replace("{ctCollectionId}",encodeURIComponent(ctCollectionId))
				;

			const queryParameters: any = {};

						if (ctCollectionId === null || ctCollectionId === undefined) {
							throw new Error("Required parameter ctCollectionId was null or undefined when calling putCTCollection.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTCollection"), response};
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
							 * @param ctCollectionId
						 * @param cTCollection
					 */
					public async putCTCollection(
									ctCollectionId: number,
							cTCollection?: CTCollection,
						headers?: {[name: string]: string}
					): Promise<{
							body: CTCollection;
						response: Response;
					}> {
						return this.putCTCollectionWithContentType(
										ctCollectionId,
							{
								parameters: {
										cTCollection: cTCollection
								},
								type: "application/json"
							},
							headers
						);
					}
}