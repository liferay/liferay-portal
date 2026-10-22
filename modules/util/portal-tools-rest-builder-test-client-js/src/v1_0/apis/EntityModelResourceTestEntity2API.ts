/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {EntityModelResourceTestEntity2} from '../models/EntityModelResourceTestEntity2';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class EntityModelResourceTestEntity2API {
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
		 * Retrieve a EntityModelResourceTestEntity2 item. (EntityModelResource and VulcanBatchEngineTaskItemDelegate interfaces will not be implemented automatically)
				 * @param entityModelResourceTestEntity2Id
		 * @param headers Optional custom request headers
		 */
		public async getEntityModelResourceTestEntity2(
						entityModelResourceTestEntity2Id: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: EntityModelResourceTestEntity2;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/entity-model-resource-test-entities2/{entityModelResourceTestEntity2Id}"
						.replace("{entityModelResourceTestEntity2Id}",encodeURIComponent(entityModelResourceTestEntity2Id))
				;

			const queryParameters: any = {};

						if (entityModelResourceTestEntity2Id === null || entityModelResourceTestEntity2Id === undefined) {
							throw new Error("Required parameter entityModelResourceTestEntity2Id was null or undefined when calling getEntityModelResourceTestEntity2.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "EntityModelResourceTestEntity2"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

}