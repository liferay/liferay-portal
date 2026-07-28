/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {extractJSON} from '../extractJSON';
import {AutofixCandidate} from '../types/Autofix';
import {AutofixDefinition} from './AutofixDefinition';

function parseCandidates(response: string): AutofixCandidate[] | undefined {
	const parsed = extractJSON(response) as {candidates?: unknown};

	if (!Array.isArray(parsed?.candidates)) {
		return undefined;
	}

	return parsed.candidates
		.filter((item) => Boolean(item?.title))
		.map((item) => ({
			rationale: item.rationale,
			value: item.title,
		}));
}

export const DESCRIPTION_AUTOFIX_DEFINITION: AutofixDefinition = {
	agentExternalReferenceCode: 'L_SEO_STUDIO_DESCRIPTION_GENERATOR',
	getApplyErrorMessage: () =>
		Liferay.Language.get('unable-to-apply-the-meta-description'),
	getCandidateLabel: (candidate) => candidate.value,
	getGenerateErrorMessage: () =>
		Liferay.Language.get('unable-to-generate-meta-description-suggestions'),
	getPromptMessage: (pageName) =>
		sub(
			Liferay.Language.get('help-me-create-a-description-for-the-page-x'),
			pageName
		),
	getResolvedPartialMessage: () =>
		Liferay.Language.get(
			'the-meta-description-was-applied-but-the-insight-could-not-be-marked-as-resolved'
		),
	getResolvedSuccessMessage: () =>
		Liferay.Language.get(
			'the-meta-description-was-applied-and-the-insight-was-resolved'
		),
	getSuggestionsIntroMessage: () =>
		Liferay.Language.get(
			'here-are-some-optimized-description-options-for-this-page'
		),
	insightTypeName: 'missingOrEmptyMetaDescription',
	parseCandidates,
};
