/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type GenerationStatusKey =
	| 'committed'
	| 'failed'
	| 'generating'
	| 'ready'
	| 'refining';

export interface Generation {
	commitDate?: string;
	externalReferenceCode: string;
	failureReason?: string;
	generatedSiteERC?: string;
	generationStatus: {
		key: GenerationStatusKey;
		name?: string;
	};
	id: number;
	prompt: string;
	targetLanguages?: string;
	title: string;
}
