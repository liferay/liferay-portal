/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type Status = 'danger' | 'info' | 'success' | 'warning';

export type Size = 'full-screen' | 'lg' | 'sm';

export enum ObserverType {
	Close = 0,
	Open = 1,
	RestoreFocus = 2,
	Unmount = 3,
}

export type Observer = {
	dispatch: (type: ObserverType, payload?: any) => void;
	mutation: [boolean, boolean];
};
