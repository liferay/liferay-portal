/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface ILiferay {
	Icons: {
		spritemap: string;
	};
	Language: {
		get: (key: string) => string;
	};
	Util: {
		openToast: (data: {
			message?: string;
			title?: string;
			type?: string;
		}) => void;
	};
	authToken: string;
}

declare global {
	interface Window {
		Liferay: ILiferay;
	}
}

export const Liferay = window.Liferay || {
	Icons: {
		spritemap: '',
	},
	Language: {
		get: (key: string) => key,
	},
	Util: {
		openToast: () => null,
	},
	authToken: '',
};
