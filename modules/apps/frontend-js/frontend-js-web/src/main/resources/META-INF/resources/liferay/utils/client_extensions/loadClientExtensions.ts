/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {loadModule} from './loadModule';

export interface ClientExtensionDefinition<T> {
	context?: T;
	importDeclaration: string;
}

export interface ClientExtensionResolution<T> {
	binding?: any;
	context: T;
	error?: Error;
}

export interface ClientExtensionHandler<T> {
	onLoad(resolutions: Array<ClientExtensionResolution<T>>): void;
	clientExtensionDefinitions: Array<ClientExtensionDefinition<T>>;
}

export function loadClientExtensions(
	clientExtensionsHandlers: Array<ClientExtensionHandler<unknown>>
) {
	for (const {
		clientExtensionDefinitions,
		onLoad,
	} of clientExtensionsHandlers) {
		if (!clientExtensionDefinitions.length) {
			continue;
		}

		const promises = clientExtensionDefinitions.map(
			({context, importDeclaration}) => {
				return loadModule(importDeclaration)
					.then((binding) => ({
						binding,
						context,
					}))
					.catch((error: Error) => {
						if (process.env.NODE_ENV === 'development') {
							console.error(error);
						}

						return {
							context,
							error,
						};
					});
			}
		);

		Promise.all(promises).then(onLoad);
	}
}
