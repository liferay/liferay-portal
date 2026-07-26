/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type AutofixCandidate = {
	rationale?: string;
	value: string;
};

export type ScanInsightItem = {
	externalReferenceCode?: string;
	id: number;
	r_seoStudioPageToSEOStudioScanInsights_seoStudioPage?: {
		pageURL?: string;
		title?: string;
		type?: string;
	};
	state?: number;
};
