/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const DISPLAY_TYPES: Record<string, string> = {
	committed: 'success',
	failed: 'danger',
	generating: 'info',
	ready: 'success',
	refining: 'info',
};

interface Args {
	value?: {
		key?: string;
		name?: string;
	};
}

export default function CSGGenerationStatusDataRenderer({
	value,
}: Args): HTMLElement {
	const span = document.createElement('span');

	if (!value || !value.key) {
		return span;
	}

	span.className = `label label-${DISPLAY_TYPES[value.key] ?? 'secondary'}`;
	span.textContent = value.name || value.key;

	return span;
}
