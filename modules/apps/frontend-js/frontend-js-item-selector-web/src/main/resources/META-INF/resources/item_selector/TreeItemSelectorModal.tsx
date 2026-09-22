/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView as ClayTreeView} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

import useTaxonomyCategoryTreeNodes, {
	ITaxonomyCategory,
	ITaxonomyCategoryTreeNode,
} from './useTaxonomyCategoryTreeNodes';

import '../css/TreeItemSelectorModal.scss';

export interface ITreeItemSelectorModalProps {

	/**
	 * URL navigated to when the user clicks the inline "+" button next to the
	 * search field. Renders the button only when set.
	 */
	createItemURL?: string;

	/**
	 * Multi-select mode. When false, selecting a category immediately confirms
	 * and closes the modal.
	 */
	multiSelect?: boolean;

	/**
	 * Clay useModal observer.
	 */
	observer: any;

	/**
	 * Called when the user dismisses the modal without confirming a selection.
	 */
	onCancel?: () => void;

	/**
	 * Called with the selected categories on confirmation.
	 */
	onItemsChange: (items: ITaxonomyCategory[]) => void;

	/**
	 * Clay useModal onOpenChange.
	 */
	onOpenChange: (open: boolean) => void;

	/**
	 * Clay useModal open state.
	 */
	open: boolean;

	/**
	 * Pre-selected categories. Their IDs are checked when the modal opens.
	 */
	selectedItems?: ITaxonomyCategory[];

	/**
	 * Modal title. Defaults to "Select Category".
	 */
	title?: string;

	/**
	 * Vocabulary IDs whose categories will be displayed. Categories from other
	 * vocabularies are never shown.
	 */
	vocabularyIds: ReadonlyArray<string | number>;
}

interface IFilterResult {
	autoExpandKeys: Set<React.Key>;
	nodes: ITaxonomyCategoryTreeNode[];
}

function findMatches(
	nodes: ITaxonomyCategoryTreeNode[],
	query: string
): IFilterResult {
	const normalized = query.trim().toLowerCase();

	if (!normalized) {
		return {autoExpandKeys: new Set(), nodes};
	}

	const autoExpandKeys = new Set<React.Key>();

	const visit = (
		node: ITaxonomyCategoryTreeNode
	): ITaxonomyCategoryTreeNode | null => {
		const matchedChildren = node.children
			?.map(visit)
			.filter(
				(child): child is ITaxonomyCategoryTreeNode => child !== null
			);

		const selfMatches = node.name.toLowerCase().includes(normalized);

		const hasMatchingChildren =
			matchedChildren !== undefined && !!matchedChildren.length;

		if (selfMatches || hasMatchingChildren) {
			if (hasMatchingChildren) {
				autoExpandKeys.add(node.id);
			}

			return {
				...node,
				children: matchedChildren ?? node.children,
			};
		}

		return null;
	};

	const filtered = nodes
		.map(visit)
		.filter((node): node is ITaxonomyCategoryTreeNode => node !== null);

	return {autoExpandKeys, nodes: filtered};
}

function flattenById(
	nodes: ITaxonomyCategoryTreeNode[]
): Map<string, ITaxonomyCategoryTreeNode> {
	const map = new Map<string, ITaxonomyCategoryTreeNode>();

	const walk = (list: ITaxonomyCategoryTreeNode[]) => {
		list.forEach((node) => {
			map.set(node.id, node);

			if (node.children) {
				walk(node.children);
			}
		});
	};

	walk(nodes);

	return map;
}

function ancestorIds(
	id: string,
	parentById: Map<string, string | null>
): string[] {
	const ancestors: string[] = [];

	let current = parentById.get(id) ?? null;

	while (current) {
		ancestors.push(current);
		current = parentById.get(current) ?? null;
	}

	return ancestors;
}

function buildParentMap(
	nodes: ITaxonomyCategoryTreeNode[]
): Map<string, string | null> {
	const map = new Map<string, string | null>();

	const walk = (
		list: ITaxonomyCategoryTreeNode[],
		parentId: string | null
	) => {
		list.forEach((node) => {
			map.set(node.id, parentId);

			if (node.children) {
				walk(node.children, node.id);
			}
		});
	};

	walk(nodes, null);

	return map;
}

function TreeItemSelectorModal({
	createItemURL,
	multiSelect = false,
	observer,
	onCancel,
	onItemsChange,
	onOpenChange,
	open,
	selectedItems: externalSelectedItems = [],
	title,
	vocabularyIds,
}: ITreeItemSelectorModalProps) {
	const {error, loading, nodes} = useTaxonomyCategoryTreeNodes(vocabularyIds);

	const [filterQuery, setFilterQuery] = useState('');

	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(
		() => new Set(externalSelectedItems.map((item) => String(item.id)))
	);

	const [expandedKeys, setExpandedKeys] = useState<Set<React.Key>>(new Set());

	const itemsById = useMemo(() => flattenById(nodes), [nodes]);

	const parentById = useMemo(() => buildParentMap(nodes), [nodes]);

	useEffect(() => {
		if (!open) {
			return;
		}

		setSelectedKeys(
			new Set(externalSelectedItems.map((item) => String(item.id)))
		);

		// Auto-expand ancestor chains of pre-selected categories so
		// indicators are visible when the modal opens.

		const ancestors = new Set<React.Key>();

		externalSelectedItems.forEach((item) => {
			ancestorIds(String(item.id), parentById).forEach((id) =>
				ancestors.add(id)
			);
		});

		setExpandedKeys(ancestors);
	}, [externalSelectedItems, open, parentById]);

	const {autoExpandKeys, nodes: filteredNodes} = useMemo(
		() => findMatches(nodes, filterQuery),
		[nodes, filterQuery]
	);

	useEffect(() => {
		if (autoExpandKeys.size === 0) {
			return;
		}

		setExpandedKeys((current) => {
			const next = new Set(current);

			autoExpandKeys.forEach((key) => next.add(key));

			return next;
		});
	}, [autoExpandKeys]);

	useEffect(() => {
		if (!error) {
			return;
		}

		openToast({
			message: Liferay.Language.get('an-unexpected-error-occurred'),
			title: Liferay.Language.get('error'),
			type: 'danger',
		});
	}, [error]);

	const selectedItems = useMemo(() => {
		const result: ITaxonomyCategory[] = [];

		selectedKeys.forEach((key) => {
			const node = itemsById.get(String(key));

			if (node) {
				result.push(node.raw);
			}
		});

		return result;
	}, [itemsById, selectedKeys]);

	const handleConfirm = (items: ITaxonomyCategory[]) => {
		onItemsChange(items);
		onOpenChange(false);
	};

	const handleCancel = () => {
		onCancel?.();
		onOpenChange(false);
	};

	const handleSelectionChange = (keys: Set<React.Key>) => {
		setSelectedKeys(keys);

		if (!multiSelect) {
			const [first] = Array.from(keys);

			if (first === undefined) {
				return;
			}

			const node = itemsById.get(String(first));

			if (node) {
				handleConfirm([node.raw]);
			}
		}
	};

	const toggleWithShift = (
		selection: {
			toggle: (
				id: React.Key,
				options?: {
					parentSelection?: boolean;
					selectionMode?: 'multiple-recursive' | null;
				}
			) => void;
		},
		id: React.Key,
		shiftKey: boolean
	) => {
		selection.toggle(id, {
			parentSelection: false,
			selectionMode: shiftKey ? 'multiple-recursive' : null,
		});
	};

	if (!open) {
		return <></>;
	}

	const resolvedTitle = title ?? Liferay.Language.get('select-category');

	const hasError = !loading && !!error;

	const isEmpty = !loading && !error && !nodes.length;

	const hasFilterMisses =
		!loading && !error && !!nodes.length && !filteredNodes.length;

	const showSelectionBanner = multiSelect && selectedKeys.size > 0;

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{resolvedTitle}
			</ClayModal.Header>

			<ClayModal.Body className="d-flex flex-column p-0">
				<form
					className="d-flex mb-0 px-3 py-3 select-category-filter"
					onSubmit={(event) => event.preventDefault()}
					role="search"
				>
					<div className="input-group">
						<div className="input-group-item">
							<input
								aria-label={Liferay.Language.get(
									'search-categories'
								)}
								className="form-control h-100 input-group-inset input-group-inset-after"
								onChange={(event) =>
									setFilterQuery(event.target.value)
								}
								placeholder={Liferay.Language.get('search')}
								type="search"
								value={filterQuery}
							/>

							<div className="input-group-inset-item input-group-inset-item-after pr-3">
								<ClayIcon symbol="search" />
							</div>
						</div>
					</div>

					{createItemURL && (
						<ClayButton
							className="btn-monospaced ml-3 nav-btn nav-btn-monospaced"
							displayType="primary"
							onClick={() => navigate(createItemURL)}
						>
							<ClayIcon symbol="plus" />
						</ClayButton>
					)}
				</form>

				{showSelectionBanner && (
					<div className="align-items-center category-tree-count-feedback d-flex justify-content-between px-3 py-2">
						<span className="m-0 text-2">
							{selectedKeys.size > 1
								? `${selectedKeys.size} ${Liferay.Language.get('items-selected')}`
								: `${selectedKeys.size} ${Liferay.Language.get('item-selected')}`}
						</span>

						<ClayButton
							className="text-3 text-dark text-weight-semi-bold"
							displayType="link"
							onClick={() => setSelectedKeys(new Set())}
						>
							{Liferay.Language.get('clear-all')}
						</ClayButton>
					</div>
				)}

				<div className="d-flex flex-column flex-grow-1 px-3 py-3">
					{(loading || hasError || isEmpty || hasFilterMisses) && (
						<div className="align-items-center d-flex flex-column flex-grow-1 justify-content-center">
							{loading && <ClayLoadingIndicator />}

							{hasError && (
								<ClayEmptyState
									description={Liferay.Language.get(
										'an-unexpected-error-occurred'
									)}
									imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
									title={Liferay.Language.get(
										'unable-to-load-content'
									)}
								/>
							)}

							{isEmpty && (
								<ClayEmptyState
									description={Liferay.Language.get(
										'no-categories-were-found'
									)}
									imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
									title={Liferay.Language.get(
										'no-results-found'
									)}
								/>
							)}

							{hasFilterMisses && (
								<ClayEmptyState
									description={Liferay.Language.get(
										'try-again-with-a-different-search'
									)}
									imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
									small
									title={Liferay.Language.get(
										'no-results-found'
									)}
								/>
							)}
						</div>
					)}

					{multiSelect && !loading && !!filteredNodes.length && (
						<p
							className="mb-3 text-2"
							dangerouslySetInnerHTML={{
								__html: sub(
									Liferay.Language.get(
										'press-x-to-select-or-deselect-a-parent-node-and-all-its-child-items'
									),
									'<kbd class="c-kbd c-kbd-light">⇧</kbd>'
								),
							}}
						/>
					)}

					{!loading && !error && !!nodes.length && (
						<SearchResultsMessage
							numberOfResults={filteredNodes.length}
							resultType={
								filteredNodes.length > 1
									? Liferay.Language.get('main-categories')
									: Liferay.Language.get('main-category')
							}
						/>
					)}

					{!loading && !error && !!filteredNodes.length && (
						<ClayTreeView
							expandedKeys={expandedKeys}
							items={filteredNodes}
							nestedKey="children"
							onExpandedChange={(keys: Set<React.Key>) =>
								setExpandedKeys(keys)
							}
							onSelectionChange={handleSelectionChange}
							selectedKeys={selectedKeys}
							selectionMode={multiSelect ? 'multiple' : 'single'}
							showExpanderOnHover={false}
						>
							{(item: ITaxonomyCategoryTreeNode, selection) => (
								<ClayTreeView.Item>
									<ClayTreeView.ItemStack>
										{multiSelect && (
											<ClayCheckbox
												checked={selection.has(item.id)}
												onChange={(event) =>
													toggleWithShift(
														selection,
														item.id,
														(
															event.nativeEvent as MouseEvent
														).shiftKey
													)
												}
												onClick={(event) =>
													event.stopPropagation()
												}
												tabIndex={-1}
											/>
										)}

										<ClayIcon symbol="categories" />

										{item.name}
									</ClayTreeView.ItemStack>

									{item.children && (
										<ClayTreeView.Group
											items={item.children}
										>
											{(
												child: ITaxonomyCategoryTreeNode
											) => (
												<ClayTreeView.Item>
													{multiSelect && (
														<ClayCheckbox
															checked={selection.has(
																child.id
															)}
															onChange={(event) =>
																toggleWithShift(
																	selection,
																	child.id,
																	(
																		event.nativeEvent as MouseEvent
																	).shiftKey
																)
															}
															onClick={(event) =>
																event.stopPropagation()
															}
															tabIndex={-1}
														/>
													)}

													<ClayIcon symbol="categories" />

													{child.name}
												</ClayTreeView.Item>
											)}
										</ClayTreeView.Group>
									)}
								</ClayTreeView.Item>
							)}
						</ClayTreeView>
					)}
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="btn-cancel"
							displayType="secondary"
							onClick={handleCancel}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						{multiSelect && (
							<ClayButton
								disabled={selectedKeys.size === 0}
								onClick={() => handleConfirm(selectedItems)}
							>
								{Liferay.Language.get('done')}
							</ClayButton>
						)}
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

export default TreeItemSelectorModal;
