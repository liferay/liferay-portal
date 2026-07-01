/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	buildDetectedConfig,
	buildSummary,
	buildTemplates,
	getFileToken,
	getItemLanguages,
	getItemURL,
	getLanguageLabel,
	getTemplateIcon,
	getTemplateLabel,
	parseContentSample,
} from '../../../src/main/resources/META-INF/resources/js/util/contentModel';

import type {Generation} from '../../../src/main/resources/META-INF/resources/js/types/Generation';
import type {GenerationItem} from '../../../src/main/resources/META-INF/resources/js/types/GenerationItem';

beforeAll(() => {
	const liferay = Liferay as unknown as Record<string, unknown>;

	liferay.Language = {
		get: (key: string) => key,
	};
});

const generation: Generation = {
	externalReferenceCode: 'erc',
	generationStatus: {key: 'ready'},
	id: 1,
	prompt: 'Build a landing page',
	targetLanguages: 'en,es',
	title: 'Landing page',
};

const items: GenerationItem[] = [
	{
		externalReferenceCode: 'i1',
		fileName: '01-site.batch-engine-data.json',
		id: 1,
		itemCount: 1,
		languages: 'en,es',
	},
	{
		externalReferenceCode: 'i2',
		fileName: '06-pages.batch-engine-data.json',
		id: 2,
		itemCount: 3,
		languages: 'en,es',
	},
	{
		externalReferenceCode: 'i3',
		fileName: '07-blogs.batch-engine-data.json',
		id: 3,
		itemCount: 8,
		languages: 'en',
		previewItem: JSON.stringify({
			externalReferenceCode: 'should-be-stripped',
			headline: 'Welcome',
			id: 99,
			subtitle: {en_US: 'A great intro'},
		}),
	},
];

describe('contentModel', () => {
	it('derives the file token from a batch file name', () => {
		expect(getFileToken('06-pages.batch-engine-data.json')).toBe('pages');
		expect(getFileToken('04-fragment-set.batch-engine-data.json')).toBe(
			'fragment-set'
		);
	});

	it('resolves localized language labels for any code', () => {
		expect(getLanguageLabel('es')).toBe('Spanish');
		expect(getLanguageLabel('nl')).toBe('Dutch');
	});

	it('splits item languages', () => {
		expect(getItemLanguages(items[0])).toEqual(['en', 'es']);
		expect(getItemLanguages({...items[0], languages: undefined})).toEqual(
			[]
		);
	});

	it('builds one template per item with counts and icons', () => {
		const templates = buildTemplates(items);

		expect(templates).toHaveLength(3);
		expect(templates[0].icon).toBe('home');
		expect(templates[1].itemCount).toBe(3);
		expect(templates[1].pageCount).toBe(3);
		expect(templates[0].pageCount).toBe(0);
		expect(templates[2].languageCount).toBe(1);
	});

	it('builds summary stats: pages, languages, templates, total entries', () => {
		const summary = buildSummary(generation, items);

		expect(summary[0].value).toBe(11);
		expect(summary[1].value).toBe(2);
		expect(summary[2].value).toBe(3);
		expect(summary[3].value).toBe(12);
	});

	it('falls back to target languages when items carry none', () => {
		const config = buildDetectedConfig(generation, [
			{...items[0], languages: undefined},
		]);

		expect(config.languageLabels).toEqual(['English', 'Spanish']);
	});

	it('parses a content sample, stripping metadata and localizing maps', () => {
		const sample = parseContentSample(items[2]);

		expect(sample).not.toBeNull();
		expect(sample?.chips).toEqual(['Headline', 'Subtitle']);
		expect(sample?.fields).toEqual([
			{label: 'Headline', value: 'Welcome'},
			{label: 'Subtitle', value: 'A great intro'},
		]);
	});

	it('returns null for a missing or malformed preview item', () => {
		expect(
			parseContentSample({...items[0], previewItem: undefined})
		).toBeNull();
		expect(
			parseContentSample({...items[0], previewItem: '{not json'})
		).toBeNull();
	});

	it('resolves template labels and icons by file token', () => {
		expect(getTemplateLabel('06-pages.batch-engine-data.json')).toBe(
			'page'
		);
		expect(getTemplateLabel('07-blogs.batch-engine-data.json')).toBe(
			'blog-article'
		);
		expect(getTemplateIcon('06-pages.batch-engine-data.json')).toBe('page');
		expect(getTemplateIcon('99-unknown.batch-engine-data.json')).toBe(
			'document'
		);
	});

	it('labels known content keys and keeps chips for every key', () => {
		const sample = parseContentSample({
			...items[0],
			previewItem: JSON.stringify({
				excerpt: 'A short intro',
				friendlyURLPath: '/products/widget',
				metaDescription: 'Buy widgets',
				seoTitle: 'Widgets | Buy Online',
			}),
		});

		expect(sample?.fields).toEqual([
			{label: 'excerpt', value: 'A short intro'},
			{label: 'url', value: '/products/widget'},
			{label: 'meta-description', value: 'Buy widgets'},
			{label: 'seo-title', value: 'Widgets | Buy Online'},
		]);
		expect(sample?.chips).toEqual([
			'Excerpt',
			'Friendly U R L Path',
			'Meta Description',
			'Seo Title',
		]);
	});

	it('derives an item URL only when the preview carries one', () => {
		expect(getItemURL(items[0])).toBeNull();
		expect(
			getItemURL({
				...items[0],
				previewItem: JSON.stringify({friendlyURLPath: '/about'}),
			})
		).toBe('/about');
		expect(
			getItemURL({
				...items[0],
				previewItem: JSON.stringify({urlTitle: 'about-us'}),
			})
		).toBe('about-us');
	});
});
