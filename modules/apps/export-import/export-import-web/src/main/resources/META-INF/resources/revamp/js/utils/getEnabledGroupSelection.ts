/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type EnabledGroupSelection = 'sites' | 'sitesAndSpaces' | 'spaces';

export function getEnabledGroupSelection({
	siteSelectionEnabled = false,
	spaceSelectionEnabled = false,
}: {
	siteSelectionEnabled?: boolean;
	spaceSelectionEnabled?: boolean;
} = {}): EnabledGroupSelection | undefined {
	if (siteSelectionEnabled && spaceSelectionEnabled) {
		return 'sitesAndSpaces';
	}

	if (siteSelectionEnabled) {
		return 'sites';
	}

	if (spaceSelectionEnabled) {
		return 'spaces';
	}

	return undefined;
}
