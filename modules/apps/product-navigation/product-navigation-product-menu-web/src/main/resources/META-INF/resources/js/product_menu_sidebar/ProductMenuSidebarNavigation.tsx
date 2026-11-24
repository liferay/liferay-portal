/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {ClayVerticalNav} from '@clayui/nav';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {
	UnifiedSidebar,
	UnifiedSidebarItem,
	UnifiedSidebarItemContent,
	UnifiedSidebarResultsSkeleton,
	UnifiedSidebarSearchInput,
	countUnifiedSidebarItems,
	useUnifiedSidebarStuck,
} from 'frontend-js-components-web';
import React, {useMemo, useRef} from 'react';

import ProductMenuSidebarStaging, {
	ProductMenuSidebarStagingProps,
} from './ProductMenuSidebarStaging';

export type ProductMenuSidebarItem = UnifiedSidebarItem & {
	disabled?: boolean;
	pageTree?: boolean;
};

interface Props {
	expandedKeys: Set<React.Key>;
	initialExpandedKeys: Set<React.Key>;
	isFilterActive: boolean;
	items: Array<ProductMenuSidebarItem>;
	label: string;
	loading: boolean;
	numberOfMatches: number;
	onExpandedChange: (expandedKeys: Set<React.Key>) => void;
	onPageTreeOpen: () => void;
	onQueryChange: (query: string) => void;
	onSearchFocus: () => void;
	selectedPortletId: string;
	staging?: ProductMenuSidebarStagingProps;
}

function ProductMenuSidebarNavigation({
	expandedKeys,
	initialExpandedKeys,
	isFilterActive,
	items,
	label,
	loading,
	numberOfMatches,
	onExpandedChange,
	onPageTreeOpen,
	onQueryChange,
	onSearchFocus,
	selectedPortletId,
	staging,
}: Props) {
	const numberOfResults = useMemo(
		() =>
			isFilterActive ? numberOfMatches : countUnifiedSidebarItems(items),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	const scrollRef = useRef<HTMLDivElement>(null);

	const stuck = useUnifiedSidebarStuck(scrollRef, items);

	return (
		<>
			<UnifiedSidebar.Top>
				{staging && <ProductMenuSidebarStaging {...staging} />}

				<UnifiedSidebarSearchInput
					onChange={onQueryChange}
					onFocus={onSearchFocus}
				/>

				<SearchResultsMessage
					numberOfResults={
						showResultsSkeleton ? null : numberOfResults
					}
					resultType={Liferay.Language.get('navigation-items')}
				/>
			</UnifiedSidebar.Top>

			<div
				className={classNames('unified-sidebar-scroll', {
					'unified-sidebar-scroll-stuck': stuck,
				})}
				ref={scrollRef}
			>
				<UnifiedSidebar.Body className="c-px-0">
					{showResultsSkeleton ? (
						<UnifiedSidebarResultsSkeleton />
					) : numberOfResults ? (
						<ClayVerticalNav
							active={selectedPortletId}
							aria-label={label}
							defaultExpandedKeys={initialExpandedKeys}
							displayType="primary"
							expandedKeys={expandedKeys}
							itemAriaCurrent={true}
							items={items}
							onExpandedChange={onExpandedChange}
							stacked={true}
						>
							{(collectionItem) => {
								if (typeof collectionItem === 'string') {
									return <span>{collectionItem}</span>;
								}

								const item =
									collectionItem as ProductMenuSidebarItem;

								if (item.disabled) {
									return (
										<li
											className="nav-item"
											key={item.id}
											role="none"
										>
											<button
												className="btn btn-unstyled disabled nav-link"
												data-canonical-name={
													item.canonicalName
												}
												disabled
												role="menuitem"
												type="button"
											>
												<UnifiedSidebarItemContent
													item={item}
												/>
											</button>
										</li>
									);
								}

								return (
									<ClayVerticalNav.Item
										className={classNames({
											'unified-sidebar-section-item':
												item.parentLabel,
										})}
										data-canonical-name={item.canonicalName}
										href={item.href}
										items={item.items}
										key={item.id}
										onClick={
											item.pageTree
												? onPageTreeOpen
												: undefined
										}
										textValue={item.label}
									>
										<UnifiedSidebarItemContent
											item={item}
										/>
									</ClayVerticalNav.Item>
								);
							}}
						</ClayVerticalNav>
					) : (
						<ClayEmptyState
							className="c-mt-n2 c-px-4 text-center"
							description={Liferay.Language.get(
								'adjust-or-clear-the-search-to-view-all-navigation-items'
							)}
							small
							title={Liferay.Language.get('no-matching-items')}
						/>
					)}
				</UnifiedSidebar.Body>

				<div className="unified-sidebar-scroll-shadow" />
			</div>
		</>
	);
}

export default ProductMenuSidebarNavigation;
