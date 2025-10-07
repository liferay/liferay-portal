/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const SECONDS_IN_MINUTE = 60;
export const SECONDS_IN_HOUR = 3600;
export const SECONDS_IN_DAY = 86400;
export const SECONDS_IN_WEEK = 604800;
export const SECONDS_IN_MONTH = 2592000;
export const SECONDS_IN_YEAR = 31536000;

export const FIRST_DAY_OF_WEEK_MAP = {
	'ar-AE': 6,
	'ar-BH': 6,
	'ar-EG': 6,
	'ar-IQ': 6,
	'ar-JO': 6,
	'ar-KW': 6,
	'ar-LB': 6,
	'ar-OM': 6,
	'ar-QA': 6,
	'ar-SA': 1,
	'ar-SY': 6,
	'ar-YE': 6,
	'au-AU': 0,
	'ca-ES': 1,
	'cn-HK': 0,
	'cs-CZ': 1,
	'da-DK': 1,
	'de-DE': 1,
	'el-GR': 1,
	'en-CA': 0,
	'en-GB': 1,
	'en-US': 0,
	'es-ES': 1,
	'et-EE': 1,
	'eu-ES': 1,
	'fi-FI': 1,
	'fr-FR': 1,
	'hu-HU': 1,
	'id-ID': 1,
	'it-IT': 1,
	'jp-JP': 0,
	'ko-KR': 1,
	'lt-LT': 1,
	'lv-LV': 1,
	'ms-MY': 1,
	'my-MM': 0,
	'nl-NL': 1,
	'no-NO': 1,
	'ph-PH': 0,
	'pl-PL': 1,
	'pt-PT': 1,
	'ro-RO': 1,
	'ru-RU': 1,
	'sg-SG': 0,
	'sk-SK': 1,
	'sl-SI': 1,
	'sv-SE': 1,
	'th-TH': 1,
	'tr-TR': 1,
	'tw-TW': 0,
	'vi-VN': 1,
	'zh-CN': 1,
} as const;

export const WEEKDAYS_SHORT_MAP = {
	'eu-ES': ['ig', 'al', 'ar', 'az', 'og', 'or', 'lr'],
} as const;

export const MONTHS_LONG_MAP = {
	'eu-ES': [
		'urtarrila',
		'otsaila',
		'martxoa',
		'apirila',
		'maiatza',
		'ekaina',
		'uztaila',
		'abuztua',
		'iraila',
		'urria',
		'azaroa',
		'abendua',
	],
} as const;
