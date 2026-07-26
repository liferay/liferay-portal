/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AutofixCandidate} from '../types/Autofix';

export type AutofixDefinition = {
	agentExternalReferenceCode: string;
	getApplyErrorMessage: () => string;
	getCandidateLabel: (candidate: AutofixCandidate) => string;
	getGenerateErrorMessage: () => string;
	getPromptMessage: (pageName: string) => string;
	getResolvedPartialMessage: () => string;
	getResolvedSuccessMessage: () => string;
	getSuggestionsIntroMessage: () => string;
	insightTypeName: string;
	parseCandidates: (response: string) => AutofixCandidate[] | undefined;
};
