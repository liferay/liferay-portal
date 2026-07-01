/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import RefineStep from '../../../src/main/resources/META-INF/resources/js/steps/RefineStep';

import type {Generation} from '../../../src/main/resources/META-INF/resources/js/types/Generation';
import type {GenerationItem} from '../../../src/main/resources/META-INF/resources/js/types/GenerationItem';

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

const baseGeneration: Generation = {
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
		itemCount: 2,
		languages: 'en,es',
	},
	{
		externalReferenceCode: 'i2',
		fileName: '07-blogs.batch-engine-data.json',
		id: 2,
		itemCount: 8,
		languages: 'en',
		previewItem: JSON.stringify({headline: 'Welcome', id: 1}),
	},
];

const noop = () => {};

describe('RefineStep', () => {
	it('renders the rich preview when the generation is ready', () => {
		render(
			<RefineStep
				generation={baseGeneration}
				items={items}
				onBack={noop}
				onCancel={noop}
				onContinue={noop}
			/>
		);

		expect(
			screen.getByText('preview-content-to-be-generated')
		).toBeInTheDocument();
		expect(
			screen.getByText('content-by-template-type')
		).toBeInTheDocument();
		expect(screen.getByText('content-samples')).toBeInTheDocument();
		expect(screen.getByText('what-will-be-generated')).toBeInTheDocument();

		expect(screen.getByText('page')).toBeInTheDocument();
		expect(screen.getAllByText('blog-article').length).toBeGreaterThan(0);

		expect(
			screen.queryByText('reference-documents')
		).not.toBeInTheDocument();
		expect(screen.getByText('total-pages')).toBeInTheDocument();
		expect(screen.getByText('templates')).toBeInTheDocument();
	});

	it('expands a content sample to reveal its fields', () => {
		render(
			<RefineStep
				generation={baseGeneration}
				items={[items[1]]}
				onBack={noop}
				onCancel={noop}
				onContinue={noop}
			/>
		);

		expect(screen.getAllByText('Headline').length).toBeGreaterThan(0);
		expect(screen.getByText('Welcome')).toBeInTheDocument();
	});

	it('disables Continue when there is no content to review', () => {
		render(
			<RefineStep
				generation={{
					...baseGeneration,
					generationStatus: {key: 'refining'},
				}}
				items={[]}
				onBack={noop}
				onCancel={noop}
				onContinue={noop}
			/>
		);

		expect(
			screen.getByRole('button', {name: 'continue-to-review'})
		).toBeDisabled();
	});

	it('collapses the tip when dismissed', () => {
		render(
			<RefineStep
				generation={baseGeneration}
				items={items}
				onBack={noop}
				onCancel={noop}
				onContinue={noop}
			/>
		);

		expect(
			screen.getByText(
				'use-the-chat-on-the-left-to-refine-your-requirements-before-generating'
			)
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: /close/i}));

		expect(
			screen.queryByText(
				'use-the-chat-on-the-left-to-refine-your-requirements-before-generating'
			)
		).not.toBeInTheDocument();
	});
});
