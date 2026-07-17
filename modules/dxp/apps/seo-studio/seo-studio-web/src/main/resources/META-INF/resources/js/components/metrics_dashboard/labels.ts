/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const CATEGORY_COLORS: Record<string, string> = {
	contentStructure: '#4B9FFF',
	images: '#FFBB00',
	metadata: '#458613',
};

const SEVERITY_COLORS: Record<string, string> = {
	1: '#E7E7ED',
	2: '#FF8F39',
	3: '#DA1414',
};

const SEVERITY_LEGEND_ORDER = ['1', '2', '3'];

const SEVERITY_STACK_ORDER = ['1', '2', '3'];

function getCategoryColor(category: string): string {
	return CATEGORY_COLORS[category] ?? '#6B6C7E';
}

function getCategoryLabel(category: string): string {
	const labels: Record<string, string> = {
		contentStructure: Liferay.Language.get('content-structure'),
		images: Liferay.Language.get('image-and-media'),
		metadata: Liferay.Language.get('page-metadata'),
	};

	return labels[category] ?? category;
}

function getSeverityColor(severity: string): string {
	return SEVERITY_COLORS[severity] ?? '#6B6C7E';
}

function getSeverityLabel(severity: string): string {
	const labels: Record<string, string> = {
		1: Liferay.Language.get('low'),
		2: Liferay.Language.get('medium'),
		3: Liferay.Language.get('high'),
	};

	return labels[severity] ?? severity;
}

export {
	SEVERITY_LEGEND_ORDER,
	SEVERITY_STACK_ORDER,
	getCategoryColor,
	getCategoryLabel,
	getSeverityColor,
	getSeverityLabel,
};
