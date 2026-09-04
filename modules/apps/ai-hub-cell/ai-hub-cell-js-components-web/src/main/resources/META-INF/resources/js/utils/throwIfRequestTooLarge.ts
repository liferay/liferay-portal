/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export class RequestTooLargeError extends Error {}

export default function throwIfRequestTooLarge(
	response: Response,
	message: string
): void {
	if (response.status === 413) {
		throw new RequestTooLargeError(message);
	}
}
