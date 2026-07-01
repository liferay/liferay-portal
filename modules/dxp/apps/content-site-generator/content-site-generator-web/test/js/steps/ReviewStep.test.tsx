/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {getGenerationPages} from '../../../src/main/resources/META-INF/resources/js/services/generations';
import {getSiteByExternalReferenceCode} from '../../../src/main/resources/META-INF/resources/js/services/sites';
import ReviewStep from '../../../src/main/resources/META-INF/resources/js/steps/ReviewStep';

import type {GeneratedPage} from '../../../src/main/resources/META-INF/resources/js/types/GeneratedPage';
import type {Generation} from '../../../src/main/resources/META-INF/resources/js/types/Generation';
import type {GenerationItem} from '../../../src/main/resources/META-INF/resources/js/types/GenerationItem';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/services/generations',
	() => ({getGenerationPages: jest.fn()})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/services/sites',
	() => ({getSiteByExternalReferenceCode: jest.fn()})
);

beforeAll(() => {
	const liferay = Liferay as unknown as Record<string, unknown>;

	liferay.Icons = liferay.Icons ?? {spritemap: 'spritemap.svg'};
	liferay.Util = {
		...((liferay.Util as object) ?? {}),
		sub: (template: string, ...args: string[]) =>
			args.reduce(
				(result, value, index) =>
					result.replace(`{${index}}`, String(value)),
				template
			),
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
		fileName: '06-pages.batch-engine-data.json',
		id: 1,
		itemCount: 1,
		languages: 'en,es',
	},
	{
		externalReferenceCode: 'i2',
		fileName: '07-blogs.batch-engine-data.json',
		id: 2,
		itemCount: 2,
		languages: 'en',
	},
];

const pages: GeneratedPage[] = [
	{
		icon: 'page',
		id: '1-0',
		itemCount: 1,
		languages: ['en', 'es'],
		templateLabel: 'page',
		title: 'Home Page',
		url: '/home',
	},
	{
		icon: 'document-text',
		id: '2-0',
		itemCount: 1,
		languages: ['en'],
		templateLabel: 'blog-article',
		title: 'First Blog',
		url: '/first-blog',
	},
	{
		icon: 'document-text',
		id: '2-1',
		itemCount: 1,
		languages: ['en'],
		templateLabel: 'blog-article',
		title: 'Second Blog',
		url: '/second-blog',
	},
];

const noop = () => {};

beforeEach(() => {
	(getGenerationPages as jest.Mock).mockResolvedValue(pages);
	(getSiteByExternalReferenceCode as jest.Mock).mockResolvedValue(null);
});

describe('ReviewStep', () => {
	it('filters the pages by content type', async () => {
		render(
			<ReviewStep
				generation={generation}
				items={items}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		await screen.findByText('Home Page');

		fireEvent.change(screen.getByLabelText('filter'), {
			target: {value: 'blog-article'},
		});

		expect(screen.queryByText('Home Page')).not.toBeInTheDocument();
		expect(screen.getByText('First Blog')).toBeInTheDocument();
		expect(screen.getByText('Second Blog')).toBeInTheDocument();
	});

	it('lists the individual generated pages with their URLs', async () => {
		render(
			<ReviewStep
				generation={generation}
				items={items}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		expect(await screen.findByText('Home Page')).toBeInTheDocument();
		expect(screen.getByText('First Blog')).toBeInTheDocument();
		expect(screen.getByText('Second Blog')).toBeInTheDocument();

		expect(screen.getByText('/home')).toBeInTheDocument();
		expect(screen.getByText('/first-blog')).toBeInTheDocument();
		expect(screen.getAllByText('url').length).toBeGreaterThan(0);

		expect(screen.getByText('total-items')).toBeInTheDocument();
		expect(screen.getByText('draft')).toBeInTheDocument();
	});

	it('omits the localizing phase for single-language generations', () => {
		render(
			<ReviewStep
				generation={{
					...generation,
					generationStatus: {key: 'generating'},
				}}
				items={[
					{
						externalReferenceCode: 'i1',
						fileName: '07-blogs.batch-engine-data.json',
						id: 1,
						itemCount: 2,
						languages: 'en',
					},
				]}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		expect(
			screen.queryByText('localizing-to-target-languages')
		).not.toBeInTheDocument();
	});

	it('reveals the selection bar when pages are selected', async () => {
		render(
			<ReviewStep
				generation={generation}
				items={items}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		await screen.findByText('Home Page');

		fireEvent.click(screen.getByLabelText('select-all'));

		expect(screen.getByText('x-selected')).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'open-selected'})
		).toBeInTheDocument();
	});

	it('shows the generate progress while the status is generating', () => {
		render(
			<ReviewStep
				generation={{
					...generation,
					generationStatus: {key: 'generating'},
				}}
				items={[]}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		expect(screen.getByText('generate')).toBeInTheDocument();
		expect(screen.getByText('analyzing-your-prompt')).toBeInTheDocument();
		expect(
			screen.getByText('extracting-key-topics-and-features')
		).toBeInTheDocument();
		expect(screen.getByText('generating-content')).toBeInTheDocument();
		expect(
			screen.getByText('generating-content-pages')
		).toBeInTheDocument();

		expect(
			screen.queryByText('review-and-publish')
		).not.toBeInTheDocument();
	});

	it('shows the localizing phase for multi-language generations', () => {
		render(
			<ReviewStep
				generation={{
					...generation,
					generationStatus: {key: 'generating'},
				}}
				items={[
					{
						externalReferenceCode: 'i1',
						fileName: '07-blogs.batch-engine-data.json',
						id: 1,
						itemCount: 2,
						languages: 'en,es',
					},
				]}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		expect(
			screen.getByText('localizing-to-target-languages')
		).toBeInTheDocument();
	});

	it('shows the success alert, View Site, and disables publish once committed', async () => {
		(getSiteByExternalReferenceCode as jest.Mock).mockResolvedValue({
			friendlyUrlPath: '/freshwater',
		});

		render(
			<ReviewStep
				generation={{
					...generation,
					generatedSiteERC: 'site-erc',
					generationStatus: {key: 'committed'},
				}}
				items={items}
				onBack={noop}
				onCancel={noop}
				onPublish={noop}
				publishing={false}
			/>
		);

		expect(
			await screen.findByRole('button', {name: 'view-site'})
		).toBeInTheDocument();

		expect(
			screen.getByText('the-generated-content-was-published')
		).toBeInTheDocument();
		expect(screen.getByText('published')).toBeInTheDocument();
		expect(screen.getByRole('button', {name: 'publish'})).toBeDisabled();
	});
});
