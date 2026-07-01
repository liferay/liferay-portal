/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ContentSampleField {
	label: string;
	value: string;
}

export interface ContentSample {
	chips: string[];
	fields: ContentSampleField[];
	title: string;
}

export interface DetectedConfig {
	languageLabels: string[];
}

export interface SummaryStat {
	icon: string;
	label: string;
	value: number | string;
}

export interface Template {
	icon: string;
	itemCount: number;
	label: string;
	languageCount: number;
	pageCount: number;
}
