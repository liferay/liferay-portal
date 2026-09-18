/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {BatchTestEntityAction} from '../models/BatchTestEntityAction';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class BatchTestEntityActionAPI {
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
		 		* @param requestBody Request body that can be one of multiple content types
		 * @param headers Optional custom request headers
		 */
		public async postBatchTestEntityActionWithContentType(
					requestBody:
							{
								parameters: {
										batchTestEntityAction?: BatchTestEntityAction
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										batchTestEntityAction?: BatchTestEntityAction
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: BatchTestEntityAction;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.batchTestEntityAction, "BatchTestEntityAction"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.batchTestEntityAction, "BatchTestEntityAction"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/batch-test-entities/action"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "BatchTestEntityAction"), response};
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
						 * @param batchTestEntityAction
					 */
					public async postBatchTestEntityAction(
							batchTestEntityAction?: BatchTestEntityAction,
						headers?: {[name: string]: string}
					): Promise<{
							body: BatchTestEntityAction;
						response: Response;
					}> {
						return this.postBatchTestEntityActionWithContentType(
							{
								parameters: {
										batchTestEntityAction: batchTestEntityAction
								},
								type: "application/json"
							},
							headers
						);
					}
}