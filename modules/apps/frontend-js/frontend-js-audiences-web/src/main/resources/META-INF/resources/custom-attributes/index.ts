/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

declare let Liferay: any;

export function language(): string {
	return Liferay.ThemeDisplay.getLanguageId();
}

export function signed_in(): boolean {
	return Liferay.ThemeDisplay.isSignedIn();
}
