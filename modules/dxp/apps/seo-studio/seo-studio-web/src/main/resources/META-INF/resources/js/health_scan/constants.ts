/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {EngineKey, HealthScanConfig, ScheduleConfig} from './types';

interface EngineDescriptor {
	key: EngineKey;
	label: string;
}

interface SelectOption {
	label: string;
	value: string;
}

export const ENGINE_DESCRIPTORS: EngineDescriptor[] = [
	{
		key: 'crawler',
		label: Liferay.Language.get('crawler-insights'),
	},
	{
		key: 'aiGenerated',
		label: Liferay.Language.get('ai-generated-insights'),
	},
	{
		key: 'pageSpeed',
		label: Liferay.Language.get('google-pagespeed'),
	},
	{
		key: 'gsc',
		label: Liferay.Language.get('google-search-console-insights'),
	},
];

export const FREQUENCY_OPTIONS: SelectOption[] = [
	{label: Liferay.Language.get('daily'), value: 'daily'},
	{label: Liferay.Language.get('weekly'), value: 'weekly'},
	{label: Liferay.Language.get('monthly'), value: 'monthly'},
];

export const DAY_OF_WEEK_OPTIONS: SelectOption[] = [
	{label: Liferay.Language.get('weekday.MO'), value: 'MO'},
	{label: Liferay.Language.get('weekday.TU'), value: 'TU'},
	{label: Liferay.Language.get('weekday.WE'), value: 'WE'},
	{label: Liferay.Language.get('weekday.TH'), value: 'TH'},
	{label: Liferay.Language.get('weekday.FR'), value: 'FR'},
	{label: Liferay.Language.get('weekday.SA'), value: 'SA'},
	{label: Liferay.Language.get('weekday.SU'), value: 'SU'},
];

export const DAY_OF_MONTH_OPTIONS: number[] = Array.from(
	{length: 31},
	(_, index) => index + 1
);

export const SCOPE_OPTIONS: SelectOption[] = [
	{
		label: Liferay.Language.get('all-published-pages'),
		value: 'allPublishedPages',
	},
	{
		label: Liferay.Language.get('public-sitemap-pages'),
		value: 'publicSitemapPages',
	},
	{
		label: Liferay.Language.get('included-path-only'),
		value: 'includedPathsOnly',
	},
	{
		label: Liferay.Language.get('excluded-path-only'),
		value: 'excludedPathsOnly',
	},
];

export const RANKING_METHOD_OPTIONS: SelectOption[] = [
	{label: Liferay.Language.get('top-by-page-visit'), value: 'topByPageVisit'},
	{
		label: Liferay.Language.get('most-recent-by-creation-date'),
		value: 'mostRecentByCreation',
	},
];

export const MAX_CRAWL_DEPTH_OPTIONS: number[] = [1, 2, 4, 8, 16, 24];

export const MAX_DURATION_OPTIONS: SelectOption[] = [1, 6, 12, 24, 48].map(
	(hours) => ({
		label:
			hours === 1
				? sub(Liferay.Language.get('x-hour'), String(hours))
				: sub(Liferay.Language.get('x-hours'), String(hours)),
		value: String(hours * 3600),
	})
);

export const MAX_PAGES_OPTIONS: number[] = [100, 500, 1000];

export function getDefaultConfig(defaultTimeZoneId: string): HealthScanConfig {
	const engines = {} as HealthScanConfig['engines'];

	for (const {key} of ENGINE_DESCRIPTORS) {
		engines[key] = {
			enabled: false,
			excludedPaths: '',
			includedPaths: '',
			maxPagesPerScan: 100,
			rankingMethod: 'topByPageVisit',
			scope: 'allPublishedPages',
		};
	}

	engines.crawler.maxCrawlDepth = 2;
	engines.crawler.maxDuration = 86400;

	return {
		engines,
		schedule: {
			autoScanEnabled: false,
			scanDayOfMonth: 1,
			scanDayOfWeek: 'MO',
			scanFrequency: 'daily',
			scanTime: '00:00',
			scanTimeZone: defaultTimeZoneId,
		},
	};
}

export function buildInitialConfig(
	defaultTimeZoneId: string,
	scanConfig: string | null,
	schedule: Partial<ScheduleConfig> | null
): HealthScanConfig {
	const config = getDefaultConfig(defaultTimeZoneId);

	if (scanConfig) {
		const parsedConfig = JSON.parse(
			scanConfig
		) as Partial<HealthScanConfig>;

		if (parsedConfig.engines) {
			for (const key of Object.keys(config.engines) as EngineKey[]) {
				if (parsedConfig.engines[key]) {
					config.engines[key] = {
						...config.engines[key],
						...parsedConfig.engines[key],
					};
				}
			}
		}
	}

	if (schedule) {
		config.schedule = {
			autoScanEnabled:
				schedule.autoScanEnabled ?? config.schedule.autoScanEnabled,
			scanDayOfMonth:
				schedule.scanDayOfMonth || config.schedule.scanDayOfMonth,
			scanDayOfWeek:
				schedule.scanDayOfWeek || config.schedule.scanDayOfWeek,
			scanFrequency:
				schedule.scanFrequency || config.schedule.scanFrequency,
			scanTime: schedule.scanTime || config.schedule.scanTime,
			scanTimeZone: schedule.scanTimeZone || config.schedule.scanTimeZone,
		};
	}

	return config;
}
