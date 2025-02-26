/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface Window {
	Liferay: {
		ThemeDisplay?: {
			getPathContext?: () => string;
			getUserEmailAddress?: () => string;
			getUserName?: () => string;
		};
		Util?: {
			Cookie?: {
				TYPES?: {
					[key: string]: string;
				};
				get?: (name: string) => string;
				set?: (
					key: string,
					data: string,
					type: any,
					options: {}
				) => void;
			};
		};
	};
}

declare namespace Analytics {
	type Config = {
		channelId: string;
		dataSourceId: string;
		endpointUrl: string;
		flushInterval: number;
		identity: {
			emailAddressHashed: string;
		};
		identityEndpoint: string;
		projectId: string;
		userId: string;
	};

	type Middleware = {
		[key: string]: Function;
	};

	type Plugin = (analytics?: Instance) => void;

	type EventProps = {
		[key: string]: boolean | number | string;
	};

	type Identity = {
		email: string;
		name: string;
	};
}
