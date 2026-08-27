/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SegmentExperience} from '@liferay/layout-js-components-web';

import {AvailableLanguage, config} from '../config';
import {PageVersion} from '../types/PageVersion';

/**
 * Resolves what the toolbar and the preview need for the selected item: the
 * experiences and languages it offers, and which of them are selected
 */
export function getVersionData({
	currentExperienceERC,
	currentLanguageId,
	version,
}: {
	currentExperienceERC?: string;
	currentLanguageId: Liferay.Language.Locale;
	version?: PageVersion;
}) {
	const experiences = getExperiences(version);

	const selectedExperience =
		experiences.find(
			(experience) =>
				experience.segmentsExperienceERC === currentExperienceERC
		) ?? experiences[0];

	const languages = getLanguages(
		version,
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

function getExperiences(version?: PageVersion): SegmentExperience[] {
	if (!version) {
		return config.availableSegmentsExperiences;
	}

	return [...(version.pageSpecificationVersionPageExperiences ?? [])]
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
	version?: PageVersion,
	experienceERC?: string
): Partial<Record<Liferay.Language.Locale, AvailableLanguage>> {
	if (!version) {
		return config.availableLanguages;
	}

	const languageIds =
		version.pageSpecificationVersionPageExperiences?.find(
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
