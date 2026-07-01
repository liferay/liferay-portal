/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {
	ContentSample,
	ContentSampleField,
	DetectedConfig,
	SummaryStat,
	Template,
} from '../types/ContentModel';
import type {GeneratedPage} from '../types/GeneratedPage';
import type {Generation} from '../types/Generation';
import type {GenerationItem} from '../types/GenerationItem';

const KNOWN_FIELD_KEYS: Record<string, string> = {
	excerpt: 'excerpt',
	friendlyURLPath: 'url',
	h1Heading: 'h1-heading',
	metaDescription: 'meta-description',
	seoTitle: 'seo-title',
	urlTitle: 'url',
};

const METADATA_KEYS = new Set([
	'actions',
	'classNameId',
	'classPK',
	'createDate',
	'creator',
	'dateCreated',
	'dateModified',
	'externalReferenceCode',
	'groupId',
	'id',
	'modifiedDate',
	'parentExternalReferenceCode',
	'priority',
	'siteId',
	'sortOrder',
	'status',
	'userId',
]);

const PAGE_FILE_TOKENS = new Set(['blogs', 'pages']);

const TEMPLATE_ICONS: Record<string, string> = {
	'asset-library': 'documents-and-media',
	'blogs': 'document-text',
	'connected-site': 'link',
	'fragment-set': 'code',
	'fragments': 'code',
	'pages': 'page',
	'site': 'home',
};

const TEMPLATE_LABELS: Record<string, string> = {
	'asset-library': 'asset-library',
	'blogs': 'blog-article',
	'connected-site': 'connected-site',
	'fragment-set': 'fragment-set',
	'fragments': 'fragment',
	'pages': 'page',
	'site': 'site',
};

export function buildDetectedConfig(
	generation: Generation,
	items: GenerationItem[]
): DetectedConfig {
	const languages = new Set<string>();

	for (const item of items) {
		for (const language of getItemLanguages(item)) {
			languages.add(language);
		}
	}

	if (!languages.size && generation.targetLanguages) {
		for (const language of parseLanguagesList(generation.targetLanguages)) {
			languages.add(language);
		}
	}

	return {
		languageLabels: [...languages].map(getLanguageLabel),
	};
}

export function buildPages(
	item: GenerationItem,
	entries: Record<string, unknown>[]
): GeneratedPage[] {
	const icon = getTemplateIcon(item.fileName);
	const languages = getItemLanguages(item);
	const templateLabel = getTemplateLabel(item.fileName);

	return entries
		.filter((entry) => entry && typeof entry === 'object')
		.map((entry, index) => ({
			icon,
			id: `${item.id}-${index}`,
			itemCount: 1,
			languages,
			templateLabel,
			title: getEntryTitle(entry) || `${templateLabel} ${index + 1}`,
			url: getEntryURL(entry),
		}));
}

export function buildSummary(
	generation: Generation,
	items: GenerationItem[]
): SummaryStat[] {
	const languages = new Set<string>();

	let pages = 0;
	let totalEntries = 0;

	for (const item of items) {
		totalEntries += item.itemCount ?? 0;

		if (PAGE_FILE_TOKENS.has(getFileToken(item.fileName))) {
			pages += item.itemCount ?? 0;
		}

		for (const language of getItemLanguages(item)) {
			languages.add(language);
		}
	}

	if (!languages.size && generation.targetLanguages) {
		for (const language of parseLanguagesList(generation.targetLanguages)) {
			languages.add(language);
		}
	}

	return [
		{
			icon: 'document',
			label: Liferay.Language.get('total-pages'),
			value: pages,
		},
		{
			icon: 'automatic-translate',
			label: Liferay.Language.get('languages'),
			value: languages.size,
		},
		{
			icon: 'stars',
			label: Liferay.Language.get('templates'),
			value: items.length,
		},
		{
			icon: 'document',
			label: Liferay.Language.get('total-entries'),
			value: totalEntries,
		},
	];
}

export function buildTemplates(items: GenerationItem[]): Template[] {
	return items.map((item) => ({
		icon: getTemplateIcon(item.fileName),
		itemCount: item.itemCount ?? 0,
		label: getTemplateLabel(item.fileName),
		languageCount: getItemLanguages(item).length,
		pageCount: PAGE_FILE_TOKENS.has(getFileToken(item.fileName))
			? item.itemCount ?? 0
			: 0,
	}));
}

export function getEntryTitle(
	entry: Record<string, unknown> | null | undefined
): string {
	if (!entry || typeof entry !== 'object') {
		return '';
	}

	return (
		stringifyValue(entry.title) ||
		stringifyValue(entry.title_i18n) ||
		stringifyValue(entry.name) ||
		stringifyValue(entry.name_i18n) ||
		stringifyValue(entry.headline)
	);
}

export function getEntryURL(
	entry: Record<string, unknown> | null | undefined
): string | null {
	if (!entry || typeof entry !== 'object') {
		return null;
	}

	const url =
		stringifyValue(entry.friendlyUrlPath) ||
		stringifyValue(entry.friendlyUrlPath_i18n) ||
		stringifyValue(entry.friendlyURLPath) ||
		stringifyValue(entry.urlTitle);

	return url || null;
}

export function getFileToken(fileName: string): string {
	const base = fileName.replace(/\.batch-engine-data\.json$/i, '');

	return base.replace(/^\d+-/, '');
}

export function getItemLanguages(item: GenerationItem): string[] {
	return item.languages ? parseLanguagesList(item.languages) : [];
}

export function getItemURL(item: GenerationItem): string | null {
	if (!item.previewItem) {
		return null;
	}

	try {
		return getEntryURL(JSON.parse(item.previewItem));
	}
	catch (exception) {
		return null;
	}
}

function getKnownFieldLabel(key: string): string | null {
	const translationKey = KNOWN_FIELD_KEYS[key];

	return translationKey ? Liferay.Language.get(translationKey) : null;
}

export function getLanguageLabel(code: string): string {
	const languageCode = code.toLowerCase();

	try {
		return (
			new Intl.DisplayNames([Liferay.ThemeDisplay.getBCP47LanguageId()], {
				type: 'language',
			}).of(languageCode) ?? languageCode.toUpperCase()
		);
	}
	catch (exception) {
		return languageCode.toUpperCase();
	}
}

export function getTemplateIcon(fileName: string): string {
	return TEMPLATE_ICONS[getFileToken(fileName)] ?? 'document';
}

export function getTemplateLabel(fileName: string): string {
	const key = TEMPLATE_LABELS[getFileToken(fileName)];

	return key ? Liferay.Language.get(key) : fileName;
}

function humanizeKey(key: string): string {
	return key
		.replace(/_i18n$/, '')
		.replace(/([A-Z])/g, ' $1')
		.replace(/[_-]+/g, ' ')
		.replace(/^./, (character) => character.toUpperCase())
		.trim();
}

export function parseContentSample(item: GenerationItem): ContentSample | null {
	if (!item.previewItem) {
		return null;
	}

	let parsed: Record<string, unknown>;

	try {
		parsed = JSON.parse(item.previewItem);
	}
	catch (exception) {
		return null;
	}

	const chips: string[] = [];
	const fields: ContentSampleField[] = [];

	for (const [key, value] of Object.entries(parsed)) {
		if (METADATA_KEYS.has(key)) {
			continue;
		}

		const label = humanizeKey(key);

		chips.push(label);

		const stringValue = stringifyValue(value);

		if (stringValue) {
			fields.push({
				label: getKnownFieldLabel(key) ?? label,
				value: stringValue,
			});
		}
	}

	return {
		chips,
		fields,
		title: getTemplateLabel(item.fileName),
	};
}

function parseLanguagesList(languages: string): string[] {
	return languages
		.split(',')
		.map((language) => language.trim().toLowerCase())
		.filter(Boolean);
}

function stringifyValue(value: unknown): string {
	if (value === null || value === undefined) {
		return '';
	}

	if (typeof value === 'string') {
		return value;
	}

	if (typeof value === 'number' || typeof value === 'boolean') {
		return String(value);
	}

	if (typeof value === 'object') {
		const map = value as Record<string, string>;

		const localized = map.en_US ?? map[Object.keys(map)[0] ?? ''];

		if (typeof localized === 'string') {
			return localized;
		}
	}

	return '';
}
