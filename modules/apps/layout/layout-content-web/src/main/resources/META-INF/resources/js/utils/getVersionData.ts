/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SegmentExperience} from '@liferay/layout-js-components-web';

import {AvailableLanguage, config} from '../config';
import {PageVersionExperience} from '../types/PageVersion';

/**
 * Resolves what the toolbar and the preview need for the selected item: the
 * experiences and languages it offers, and which of them are selected
 */
export function getVersionData({
	currentExperienceERC,
	currentLanguageId,
	pageVersionExperiences,
}: {
	currentExperienceERC?: string;
	currentLanguageId: Liferay.Language.Locale;
	pageVersionExperiences?: PageVersionExperience[];
}) {
	const experiences = getExperiences(pageVersionExperiences);

	const selectedExperience =
		experiences.find(
			(experience) =>
				experience.segmentsExperienceERC === currentExperienceERC
		) ?? experiences[0];

	const languages = getLanguages(
		pageVersionExperiences,
		selectedExperience?.segmentsExperienceERC
	);

	return {
		experiences,
		languages,
		selectedExperience,
		selectedLanguageId:
			currentLanguageId in languages
				? currentLanguageId
				: ((Object.keys(languages)[0] ??
						config.defaultLanguageId) as Liferay.Language.Locale),
	};
}

function getExperiences(
	pageVersionExperiences?: PageVersionExperience[]
): SegmentExperience[] {
	if (!pageVersionExperiences) {
		return config.availableSegmentsExperiences;
	}

	return [...pageVersionExperiences]
		.sort((a, b) => b.priority - a.priority)
		.map(({externalReferenceCode, name_i18n, priority}) => {
			let statusLabel = Liferay.Language.get('default');

			if (priority > 0) {
				statusLabel = Liferay.Language.get('active');
			}
			else if (priority < 0) {
				statusLabel = Liferay.Language.get('inactive');
			}

			return {
				active: priority >= 0,
				segmentsExperienceERC: externalReferenceCode,
				segmentsExperienceName:
					getName(name_i18n) ?? externalReferenceCode,
				statusLabel,
			};
		});
}

function getLanguages(
	pageVersionExperiences?: PageVersionExperience[],
	experienceERC?: string
): Partial<Record<Liferay.Language.Locale, AvailableLanguage>> {
	if (!pageVersionExperiences) {
		return config.availableLanguages;
	}

	const languageIds =
		pageVersionExperiences.find(
			({externalReferenceCode}) => externalReferenceCode === experienceERC
		)?.availablePreviewLanguageIds ?? [];

	return Object.fromEntries(
		Object.entries(config.availableLanguages).filter(([languageId]) =>
			languageIds.includes(languageId as Liferay.Language.Locale)
		)
	);
}

function getName(names: Record<string, string>) {
	const languageIds = [
		Liferay.ThemeDisplay.getLanguageId(),
		config.defaultLanguageId,
	];

	for (const languageId of languageIds) {
		const {w3cLanguageId} =
			config.availableLanguages[languageId as Liferay.Language.Locale] ??
			{};

		if (w3cLanguageId && names[w3cLanguageId]) {
			return names[w3cLanguageId];
		}
	}

	return Object.values(names)[0];
}
