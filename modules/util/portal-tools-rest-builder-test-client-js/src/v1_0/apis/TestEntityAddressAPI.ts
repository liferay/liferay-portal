/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectSerializer} from '../utils/SerDes';

		import {TestEntityAddress} from '../models/TestEntityAddress';

/**
 * @author Alejandro Tardín
 * @generated
 */

export class TestEntityAddressAPI {
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
				 * @param testEntityId
		 * @param headers Optional custom request headers
		 */
		public async getTestEntityTestEntityAddress(
						testEntityId: number,
			headers?: {[name: string]: string},
		): Promise<{
				body: TestEntityAddress;
			response: Response;
		}> {

			const path = this._basePath + "/portal-tools-rest-builder-test/v1.0/test-entities/{testEntityId}/test-entity-address"
						.replace("{testEntityId}",encodeURIComponent(testEntityId))
				;

			const queryParameters: any = {};

						if (testEntityId === null || testEntityId === undefined) {
							throw new Error("Required parameter testEntityId was null or undefined when calling getTestEntityTestEntityAddress.");
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
						return {body: ObjectSerializer.deserialize(await response.json(), "TestEntityAddress"), response};
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