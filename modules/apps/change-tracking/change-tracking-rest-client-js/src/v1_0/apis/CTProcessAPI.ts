/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {CTProcess} from '../models/CTProcess';
		import {PageCTProcess} from '../models/PageCTProcess';

/**
 * @author David Truong
 * @generated
 */

export class CTProcessAPI {
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
				 * @param ctProcessId
		 * @param headers Optional custom request headers
		 */
		public async deleteCTProcess(
						ctProcessId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-processes/{ctProcessId}"
						.replace("{ctProcessId}",encodeURIComponent(ctProcessId))
				;

			const queryParameters: any = {};

						if (ctProcessId === null || ctProcessId === undefined) {
							throw new Error("Required parameter ctProcessId was null or undefined when calling deleteCTProcess.");
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
				 * @param ctProcessId
		 * @param headers Optional custom request headers
		 */
		public async getCTProcess(
						ctProcessId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: CTProcess;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-processes/{ctProcessId}"
						.replace("{ctProcessId}",encodeURIComponent(ctProcessId))
				;

			const queryParameters: any = {};

						if (ctProcessId === null || ctProcessId === undefined) {
							throw new Error("Required parameter ctProcessId was null or undefined when calling getCTProcess.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "CTProcess"), response};
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
		public async getCTProcessesPage(
						filter?: string,
						page?: number,
						pageSize?: number,
						search?: string,
						sort?: string,
						status?: Array<number>,
			headers?: {[name: string]: string},
		): Promise<{
				body: PageCTProcess;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-processes"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "PageCTProcess"), response};
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
				 * @param ctProcessId
				 * @param description
				 * @param name
		 * @param headers Optional custom request headers
		 */
		public async postCTProcessRevert(
						ctProcessId: number,
						description?: string,
						name?: string,
			headers?: {[name: string]: string},
		): Promise<{
				body?: any;
			response: Response;
		}> {

			const path = this._basePath + "/change-tracking-rest/v1.0/ct-processes/{ctProcessId}/revert"
						.replace("{ctProcessId}",encodeURIComponent(ctProcessId))
												;

			const queryParameters: any = {};

						if (ctProcessId === null || ctProcessId === undefined) {
							throw new Error("Required parameter ctProcessId was null or undefined when calling postCTProcessRevert.");
						}

						if (description !== undefined) {
							queryParameters["description"] = ObjectSerializer.serialize(description, "string");
						}

						if (name !== undefined) {
							queryParameters["name"] = ObjectSerializer.serialize(name, "string");
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

}