/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type DayOfWeek = 'FR' | 'MO' | 'SA' | 'SU' | 'TH' | 'TU' | 'WE';

export type EngineKey = 'aiGenerated' | 'crawler' | 'gsc' | 'pageSpeed';

export type Frequency = 'daily' | 'monthly' | 'weekly';

export type RankingMethod = 'mostRecentByCreation' | 'topByPageVisit';

export type ScanScope =
	| 'allPublishedPages'
	| 'excludedPathsOnly'
	| 'includedPathsOnly'
	| 'publicSitemapPages';

export interface EngineConfig {
	enabled: boolean;
	excludedPaths: string;
	includedPaths: string;
	maxCrawlDepth?: number;
	maxDuration?: number;
	maxPagesPerScan: number;
	rankingMethod: RankingMethod;
	scope: ScanScope;
}

export interface ScheduleConfig {
	autoScanEnabled: boolean;
	scanDayOfMonth: number;
	scanDayOfWeek: DayOfWeek;
	scanFrequency: Frequency;
	scanTime: string;
	scanTimeZone: string;
}

export interface HealthScanConfig {
	engines: Record<EngineKey, EngineConfig>;
	schedule: ScheduleConfig;
}

export interface TimeZoneOption {
	label: string;
	value: string;
}
