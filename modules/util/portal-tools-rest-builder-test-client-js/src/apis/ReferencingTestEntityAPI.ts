/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {ReferencingTestEntity} from '../models/ReferencingTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class ReferencingTestEntityAPI {
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
		public async postReferencingTestEntityWithContentType(
					requestBody:
							{
								parameters: {
										referencingTestEntity?: ReferencingTestEntity
								},
								type: "application/json"
							}
								|
							{
								parameters: {
										referencingTestEntity?: ReferencingTestEntity
								},
								type: "application/xml"
							}
								,
			headers?: {[name: string]: string},
		): Promise<{
				body: ReferencingTestEntity;
			response: Response;
		}> {
				let body;
						if (requestBody.type === "application/json") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.referencingTestEntity, "ReferencingTestEntity"));
						}
						if (requestBody.type === "application/xml") {
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.referencingTestEntity, "ReferencingTestEntity"));
						}

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/referencing-test-entities"
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
						return {body: ObjectSerializer.deserialize(await response.json(), "ReferencingTestEntity"), response};
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
						 * @param referencingTestEntity
					 */
					public async postReferencingTestEntity(
							referencingTestEntity?: ReferencingTestEntity,
						headers?: {[name: string]: string}
					): Promise<{
							body: ReferencingTestEntity;
						response: Response;
					}> {
						return this.postReferencingTestEntityWithContentType(
							{
								parameters: {
										referencingTestEntity: referencingTestEntity
								},
								type: "application/json"
							},
							headers
						);
					}
}