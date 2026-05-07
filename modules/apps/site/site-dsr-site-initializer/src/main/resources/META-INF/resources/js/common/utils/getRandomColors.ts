/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getRandomColor(str: string, alpha = 1) {
	const colors = [
		`rgba(11,95,255, ${alpha})`,
		`rgba(170,51,255,${alpha})`,
		`rgba(80,210,160,${alpha})`,
		`rgba(255,115,195,${alpha})`,
		`rgba(255,180,110,${alpha})`,
		`rgba(255,95,95,${alpha})`,
		`rgba(43,190,235,${alpha})`,
		`rgba(255,162,0,${alpha})`,
		`rgba(76,175,80,${alpha})`,
		`rgba(156,39,176,${alpha})`,
	];

	const hash = (str ?? '')
		.split('')
		.reduce((acc, char) => acc + char.charCodeAt(0), 0);

	return colors[hash % colors.length];
}
