/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function getProfileURL(profilePath: string): string {
	const baseURL = `${window.location.origin}/o/mcp`;

	return profilePath ? `${baseURL}/${profilePath}` : baseURL;
}
