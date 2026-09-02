/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React from 'react';

import AssetCategories from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/components/AssetCategories';

function MockItemSelector({
	apiURL,
	itemsFilter,
	placeholder,
}: {
	apiURL?: string;
	itemsFilter?: (item: any) => boolean;
	placeholder?: string;
}) {
	const filteredVocabularyIds = itemsFilter
		? [10, 20]
				.filter((id) => !itemsFilter({parentTaxonomyVocabulary: {id}}))
				.join(',')
		: '';

	return (
		<div
			data-api-url={apiURL}
			data-filtered-vocabulary-ids={filteredVocabularyIds}
			data-placeholder={placeholder}
			data-testid="item-selector"
		/>
	);
}

function MockAIAssistantTriggerButton({
	anchorId,
	label,
	onOpen,
	presentation,
}: {
	anchorId?: string;
	label?: string;
	onOpen?: () => void;
	presentation?: string;
}) {
	return (
		<button
			aria-label={label}
			data-anchor-id={anchorId}
			data-presentation={presentation}
			onClick={onOpen}
		>
			{label}
		</button>
	);
}

function MockItemSelectorItem({children}: {children: React.ReactNode}) {
	return <div>{children}</div>;
}

jest.mock('@liferay/ai-hub-cell-js-components-web', () => ({
	AIAssistantTriggerButton: MockAIAssistantTriggerButton,
}));

jest.mock('@liferay/frontend-js-item-selector-web', () => {
	return {
		ItemSelector: Object.assign(MockItemSelector, {
			Item: MockItemSelectorItem,
		}),
	};
});

function buildCategoryBrief({
	id,
	name,
	taxonomyVocabularyId,
	vocabularyName,
}: {
	id: number;
	name: string;
	taxonomyVocabularyId: number;
	vocabularyName: string;
}) {
	return {
		embeddedTaxonomyCategory: {
			id,
			name,
			parentTaxonomyVocabulary: {
				id: taxonomyVocabularyId,
				name: vocabularyName,
			},
			taxonomyVocabularyId,
		},
	};
}

function renderComponent({
	classNameId = 1,
	cmsGroupId = 456,
	collapsable,
	contentRawText,
	externalReferenceCode,
	getContent,
	placeholder,
	scopeId = 123,
	systemVocabularyIds,
	taxonomyCategoryBriefs = [],
	title,
	vocabularyId,
}: {
	classNameId?: number;
	cmsGroupId?: number;
	collapsable?: boolean;
	contentRawText?: string;
	externalReferenceCode?: string;
	getContent?: (
		objectDefinitionExternalReferenceCode?: string
	) => Promise<string>;
	placeholder?: string;
	scopeId?: number | null;
	systemVocabularyIds?: number[];
	taxonomyCategoryBriefs?: ReturnType<typeof buildCategoryBrief>[];
	title?: string;
	vocabularyId?: number;
} = {}) {
	return render(
		<AssetCategories
			cmsGroupId={cmsGroupId}
			collapsable={collapsable}
			getContent={getContent}
			hasUpdatePermission={true}
			objectEntry={
				{
					...(scopeId !== null ? {scopeId} : {}),
					contentRawText,
					systemProperties: {
						objectDefinitionBrief: {
							classNameId,
							externalReferenceCode,
						},
					},
					taxonomyCategoryBriefs,
				} as any
			}
			placeholder={placeholder}
			systemVocabularyIds={systemVocabularyIds}
			title={title}
			updateObjectEntry={jest.fn()}
			vocabularyId={vocabularyId}
		/>
	);
}

describe('AssetCategories', () => {
	beforeEach(() => {
		(global as any).Liferay = {
			Language: {
				get: jest.fn((key: string) => key),
			},
			ThemeDisplay: {
				getPortalURL: () => 'https://www.liferay.com',
			},
		};
	});

	afterEach(() => {
		jest.resetAllMocks();
	});

	it('builds the categories apiURL against the asset library when the scope is positive', () => {
		renderComponent({classNameId: 1, scopeId: 123});

		const apiURL = screen
			.getByTestId('item-selector')
			.getAttribute('data-api-url');

		expect(apiURL).toContain(
			'/o/headless-admin-taxonomy/v1.0/asset-libraries/123/taxonomy-categories'
		);
		expect(apiURL).toContain("assetTypes in ('0', '1')");
		expect(apiURL).not.toContain('assetLibraries in');
	});

	it('builds the categories apiURL against the site with an asset library filter when the scope is negative', () => {
		renderComponent({classNameId: 1, cmsGroupId: 456, scopeId: -1});

		const apiURL = screen
			.getByTestId('item-selector')
			.getAttribute('data-api-url');

		expect(apiURL).toContain(
			'/o/headless-admin-taxonomy/v1.0/sites/456/taxonomy-categories'
		);
		expect(apiURL).toContain("assetLibraries in ('-1')");
	});

	it('builds the vocabulary-scoped apiURL and uses the custom placeholder when scoped to a vocabulary', () => {
		renderComponent({placeholder: 'add-personas', vocabularyId: 10});

		const itemSelector = screen.getByTestId('item-selector');

		expect(itemSelector.getAttribute('data-api-url')).toContain(
			'/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/10/taxonomy-categories'
		);
		expect(itemSelector.getAttribute('data-placeholder')).toBe(
			'add-personas'
		);
	});

	it('does not filter the dropdown when scoped to a vocabulary', () => {
		renderComponent({vocabularyId: 10});

		expect(
			screen
				.getByTestId('item-selector')
				.getAttribute('data-filtered-vocabulary-ids')
		).toBe('');
	});

	it('does not render the category selector before the asset scope is known', () => {
		renderComponent({scopeId: null});

		expect(screen.queryByTestId('item-selector')).not.toBeInTheDocument();
	});

	it('does not render the generate categories button when there is no content source', () => {
		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};

		renderComponent();

		expect(
			screen.queryByRole('button', {name: 'add-categories-with-ai'})
		).not.toBeInTheDocument();
	});

	it('falls back to the persisted content when getContent returns nothing', async () => {
		const fire = jest.fn();
		const getContent = jest.fn().mockResolvedValue('');

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({contentRawText: 'persisted content', getContent});

		fireEvent.click(
			screen.getByRole('button', {name: 'add-categories-with-ai'})
		);

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({content: 'persisted content'})
			)
		);
	});

	it('filters system vocabulary categories out of the generic dropdown', () => {
		renderComponent({scopeId: 123, systemVocabularyIds: [10]});

		expect(
			screen
				.getByTestId('item-selector')
				.getAttribute('data-filtered-vocabulary-ids')
		).toBe('10');
	});

	it('fires the categorize event when the generate categories button is clicked', async () => {
		const fire = jest.fn();

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({
			classNameId: 1,
			cmsGroupId: 456,
			contentRawText: 'persisted content',
			scopeId: 123,
		});

		fireEvent.click(
			screen.getByRole('button', {name: 'add-categories-with-ai'})
		);

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({
					agent: 'L_AUTO_CATEGORIZE',
					classNameId: 1,
					cmsGroupId: 456,
					scopeId: 123,
				})
			)
		);
	});

	it('hides categories from system vocabularies', () => {
		renderComponent({
			systemVocabularyIds: [10],
			taxonomyCategoryBriefs: [
				buildCategoryBrief({
					id: 1,
					name: 'category-1',
					taxonomyVocabularyId: 10,
					vocabularyName: 'vocabulary-a',
				}),
				buildCategoryBrief({
					id: 3,
					name: 'category-3',
					taxonomyVocabularyId: 20,
					vocabularyName: 'vocabulary-b',
				}),
			],
		});

		expect(screen.queryByText('vocabulary-a')).not.toBeInTheDocument();
		expect(screen.queryByText('category-1')).not.toBeInTheDocument();

		expect(screen.getByText('vocabulary-b')).toBeInTheDocument();
		expect(screen.getByText('category-3')).toBeInTheDocument();
	});

	it('opens the AI assistant as a dropdown anchored to the toolbar trigger when adding categories', () => {
		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};

		renderComponent({contentRawText: 'persisted content'});

		const trigger = screen.getByRole('button', {
			name: 'add-categories-with-ai',
		});

		expect(trigger).toHaveAttribute(
			'data-anchor-id',
			'ai-assistant-toolbar-trigger'
		);
		expect(trigger).toHaveAttribute('data-presentation', 'dropdown');
	});

	it('prefers the edited content from getContent over the persisted content', async () => {
		const fire = jest.fn();
		const getContent = jest.fn().mockResolvedValue('edited content');

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({
			contentRawText: 'persisted content',
			externalReferenceCode: 'C_ARTICLE',
			getContent,
		});

		fireEvent.click(
			screen.getByRole('button', {name: 'add-categories-with-ai'})
		);

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({content: 'edited content'})
			)
		);

		expect(getContent).toHaveBeenCalledWith('C_ARTICLE');
	});

	it('renders categories grouped under their vocabulary names', () => {
		renderComponent({
			taxonomyCategoryBriefs: [
				buildCategoryBrief({
					id: 1,
					name: 'category-1',
					taxonomyVocabularyId: 10,
					vocabularyName: 'vocabulary-a',
				}),
				buildCategoryBrief({
					id: 2,
					name: 'category-2',
					taxonomyVocabularyId: 10,
					vocabularyName: 'vocabulary-a',
				}),
				buildCategoryBrief({
					id: 3,
					name: 'category-3',
					taxonomyVocabularyId: 20,
					vocabularyName: 'vocabulary-b',
				}),
			],
		});

		const vocabularyAGroup = screen
			.getByText('vocabulary-a')
			.closest('div')!;
		const vocabularyBGroup = screen
			.getByText('vocabulary-b')
			.closest('div')!;

		expect(
			within(vocabularyAGroup).getByText('category-1')
		).toBeInTheDocument();
		expect(
			within(vocabularyAGroup).getByText('category-2')
		).toBeInTheDocument();
		expect(
			within(vocabularyAGroup).queryByText('category-3')
		).not.toBeInTheDocument();

		expect(
			within(vocabularyBGroup).getByText('category-3')
		).toBeInTheDocument();
	});

	it('renders only the scoped vocabulary categories with a custom title and no vocabulary header', () => {
		renderComponent({
			taxonomyCategoryBriefs: [
				buildCategoryBrief({
					id: 1,
					name: 'persona-1',
					taxonomyVocabularyId: 10,
					vocabularyName: 'Personas',
				}),
				buildCategoryBrief({
					id: 3,
					name: 'stage-1',
					taxonomyVocabularyId: 20,
					vocabularyName: 'Funnel Stage',
				}),
			],
			title: 'personas',
			vocabularyId: 10,
		});

		expect(screen.getByText('personas')).toBeInTheDocument();
		expect(screen.getByText('persona-1')).toBeInTheDocument();

		expect(screen.queryByText('stage-1')).not.toBeInTheDocument();

		expect(screen.queryByText('Personas')).not.toBeInTheDocument();
	});

	it('renders the generate categories button when only getContent supplies the content', () => {
		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};

		renderComponent({
			getContent: jest.fn().mockResolvedValue('edited content'),
		});

		expect(
			screen.getByRole('button', {name: 'add-categories-with-ai'})
		).toBeInTheDocument();
	});

	it('renders the panel as collapsable by default', () => {
		renderComponent();

		const toggle = screen.getByRole('button', {name: 'categories'});

		expect(toggle).toHaveAttribute('aria-expanded', 'true');
	});

	it('renders the panel as non-collapsable when collapsable is false', () => {
		renderComponent({collapsable: false});

		expect(
			screen.queryByRole('button', {name: 'categories'})
		).not.toBeInTheDocument();

		expect(screen.getByText('categories')).toBeInTheDocument();
	});
});
