/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useMemo, useState} from 'react';

import fetchAllPages from './fetchAllPages';

// The vocabulary-scoped endpoint is used (not /sites/{id}/taxonomy-categories)
// because the site path filters out categories of global vocabularies, while
// this path returns the full set regardless of the requesting site.

const HEADLESS_TAXONOMY_BASE = '/o/headless-admin-taxonomy/v1.0';

export interface ITaxonomyCategory {
	availableLanguages?: string[];
	creator?: unknown;
	dateCreated?: string;
	dateModified?: string;
	description?: string;
	externalReferenceCode?: string;
	id: number;
	name: string;
	name_i18n?: Record<string, string>;
	numberOfTaxonomyCategories?: number;
	parentTaxonomyCategory?: {id: number; name?: string};
	parentTaxonomyVocabulary?: {id: number; name?: string};
	siteId?: number;
	taxonomyCategoryProperties?: Array<{key: string; value: string}>;
}

export interface ITaxonomyCategoryTreeNode {
	children?: ITaxonomyCategoryTreeNode[];
	hasChildren: boolean;
	id: string;
	name: string;
	raw: ITaxonomyCategory;
	vocabularyId: string;
}

function buildTree(
	categories: ITaxonomyCategory[],
	vocabularyId: string
): ITaxonomyCategoryTreeNode[] {
	const childrenByParent = new Map<string | null, ITaxonomyCategory[]>();

	categories.forEach((category) => {
		const parentId =
			category.parentTaxonomyCategory?.id !== undefined
				? String(category.parentTaxonomyCategory.id)
				: null;

		const siblings = childrenByParent.get(parentId) ?? [];

		siblings.push(category);
		childrenByParent.set(parentId, siblings);
	});

	const buildNode = (
		category: ITaxonomyCategory
	): ITaxonomyCategoryTreeNode => {
		const children = childrenByParent
			.get(String(category.id))
			?.map(buildNode);

		return {
			hasChildren: (category.numberOfTaxonomyCategories ?? 0) > 0,
			id: String(category.id),
			name: category.name,
			raw: category,
			vocabularyId,
			...(children?.length && {children}),
		};
	};

	// Defensive: orphans (parent missing from response, e.g. permission filter)
	// are kept by treating any unknown parent as a root.

	const knownIds = new Set(categories.map((category) => String(category.id)));

	const rootCategories = categories.filter((category) => {
		const parentId = category.parentTaxonomyCategory?.id;

		return parentId === undefined || !knownIds.has(String(parentId));
	});

	return rootCategories.map(buildNode);
}

async function loadVocabularyTree(
	vocabularyId: string,
	signal: AbortSignal
): Promise<ITaxonomyCategoryTreeNode[]> {
	const url = new URL(
		`${HEADLESS_TAXONOMY_BASE}/taxonomy-vocabularies/${vocabularyId}/taxonomy-categories`,
		window.location.origin
	);

	url.searchParams.set('flatten', 'true');

	const categories = await fetchAllPages<ITaxonomyCategory>(
		url.toString(),
		signal
	);

	return buildTree(categories, vocabularyId);
}

export interface IUseTaxonomyCategoryTreeNodesResult {
	error: Error | null;
	loading: boolean;
	nodes: ITaxonomyCategoryTreeNode[];
	reload: () => void;
}

export default function useTaxonomyCategoryTreeNodes(
	vocabularyIds: ReadonlyArray<string | number>
): IUseTaxonomyCategoryTreeNodesResult {
	const [nodes, setNodes] = useState<ITaxonomyCategoryTreeNode[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<Error | null>(null);
	const [reloadCount, setReloadCount] = useState(0);

	const idsKey = useMemo(() => {
		return vocabularyIds
			.map((id) => String(id).trim())
			.filter(Boolean)
			.sort()
			.join(',');
	}, [vocabularyIds]);

	useEffect(() => {
		const controller = new AbortController();

		setLoading(true);
		setError(null);

		Promise.all(
			idsKey
				.split(',')
				.filter(Boolean)
				.map((id) => loadVocabularyTree(id, controller.signal))
		)
			.then((trees) => {
				if (controller.signal.aborted) {
					return;
				}

				setNodes(trees.flat());
			})
			.catch((reason: unknown) => {
				if (controller.signal.aborted) {
					return;
				}

				setError(
					reason instanceof Error ? reason : new Error(String(reason))
				);
			})
			.finally(() => {
				if (!controller.signal.aborted) {
					setLoading(false);
				}
			});

		return () => {
			controller.abort();
		};
	}, [idsKey, reloadCount]);

	const reload = useCallback(() => setReloadCount((count) => count + 1), []);

	return {error, loading, nodes, reload};
}
