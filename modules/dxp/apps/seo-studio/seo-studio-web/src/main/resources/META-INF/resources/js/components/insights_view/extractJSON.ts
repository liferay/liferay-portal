/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// An agent response may be a bare JSON object, one wrapped in a markdown code
// fence, or either followed by trailing prose.

export function extractJSON(response: string): unknown | undefined {
	const braceMatch = response.match(/\{[\s\S]*\}/);
	const fenceMatch = response.match(/```(?:json)?\s*([\s\S]*?)```/);

	for (const candidate of [fenceMatch?.[1], response, braceMatch?.[0]]) {
		if (!candidate) {
			continue;
		}

		try {
			return JSON.parse(candidate);
		}
		catch {
			continue;
		}
	}

	return undefined;
}
