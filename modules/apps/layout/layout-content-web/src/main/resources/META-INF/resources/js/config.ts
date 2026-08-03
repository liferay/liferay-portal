/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SegmentExperience} from '@liferay/layout-js-components-web';

import {Status} from './types/PageVersion';

export type AvailableLanguage = {
	languageIcon: string;
	w3cLanguageId: string;
};

export type CurrentVersion = {
	name: string;
	status: Status;
};

export type Config = {
	availableLanguages: Partial<
		Record<Liferay.Language.Locale, AvailableLanguage>
	>;
	availableSegmentsExperiences: SegmentExperience[];
	currentVersion: CurrentVersion;
	defaultLanguageId: Liferay.Language.Locale;
	defaultUserImageSrc: string;
	pageSpecificationVersionsURL: string;
};

export let config = {} as Config;

export function initializeConfig(backendConfig: Config) {
	config = backendConfig;
}
