/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const DATA_MASK_ID_TOKEN = '__DATA_MASK_ID__';

export function maskEditURL(editURL: string, id: number): string {
	return editURL.replace(DATA_MASK_ID_TOKEN, String(id));
}
