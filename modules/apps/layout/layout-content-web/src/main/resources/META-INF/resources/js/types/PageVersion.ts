/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type Status = 'approved' | 'draft';

type Action = {
	href: string;
	method: string;
};

export type PageVersionExperience = {
	availablePreviewLanguageIds: Liferay.Language.Locale[];
	externalReferenceCode: string;
	name_i18n: Record<string, string>;
	priority: number;
};

export type PageVersion = {
	actions?: Partial<Record<'delete' | 'restore', Action>>;
	creator?: {
		externalReferenceCode?: string;
		image?: string;
		name: string;
	};
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	name: string;
	status: Status;
	statusDate: string;
	version: number;
};
