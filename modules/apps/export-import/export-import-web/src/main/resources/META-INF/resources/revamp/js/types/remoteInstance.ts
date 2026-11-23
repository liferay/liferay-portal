/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface RemoteInstance {
	clientId: string;
	hostname: string;
	id: number;
	name: string;
	port: number | null;
	secureConnection: boolean;
}

export type RemoteInstanceRequest = Omit<RemoteInstance, 'id'> & {
	clientSecret?: string;
};

export type RemoteInstanceFormValues = Omit<RemoteInstance, 'id' | 'port'> & {
	clientSecret: string;
	port: string;
};
