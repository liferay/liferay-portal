/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function getFormattedLabel(label: string) {
	return `<strong>${Liferay.Util.escapeHTML(label)}</strong>`;
}

export {getFormattedLabel};
