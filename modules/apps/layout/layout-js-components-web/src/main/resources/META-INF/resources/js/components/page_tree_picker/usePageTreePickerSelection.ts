/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useEffect, useRef, useState} from 'react';

import {
	PageTreePickerItem,
	PageTreePickerSelectionEntry,
} from '../../types/PageTreePicker';

interface PageTreePickerSelectionRule {
	descendants?: boolean;
	self?: boolean;
}

export interface PageTreePickerSelection<T> {
	getSelectedItems: () => Array<PageTreePickerItem<T>>;
	isDescendant: (itemId: string, ancestorId: string) => boolean;
	registerItems: (
		items: Array<PageTreePickerItem<T>>,
		parentId: string | null
	) => void;
	select: (item: PageTreePickerItem<T>) => void;
	selectedKeys: Set<React.Key>;
	toggleKey: (item: PageTreePickerItem<T>) => void;
	toggleSubtree: (item: PageTreePickerItem<T>) => void;
}

export default function usePageTreePickerSelection<T>({
	defaultRegisteredItems,
	defaultSelectedEntries,
	onSelectionChange,
}: {
	defaultRegisteredItems?: Array<PageTreePickerItem<T>>;
	defaultSelectedEntries?: Array<PageTreePickerSelectionEntry<T>>;
	onSelectionChange?: (
		entries: Array<PageTreePickerSelectionEntry<T>>
	) => void;
}): PageTreePickerSelection<T> {
	const [{itemsById, parentIdsById}] = useState(() => {
		const itemsById = new Map<string, PageTreePickerItem<T>>();
		const parentIdsById = new Map<string, string | null>();

		const defaultItems = [
			...(defaultRegisteredItems ?? []),
			...(defaultSelectedEntries?.map((entry) => entry.item) ?? []),
		];

		defaultItems.forEach((item) => {
			itemsById.set(item.id, item);

			if (item.parentId !== undefined) {
				parentIdsById.set(item.id, item.parentId);
			}
		});

		return {itemsById, parentIdsById};
	});

	const isEffectivelySelected = useCallback(
		(
			itemId: string,
			rulesById: Map<string, PageTreePickerSelectionRule>
		) => {
			const rule = rulesById.get(itemId);

			if (rule?.self !== undefined) {
				return rule.self;
			}

			let parentId = parentIdsById.get(itemId);

			while (parentId) {
				const parentRule = rulesById.get(parentId);

				if (parentRule?.descendants !== undefined) {
					return parentRule.descendants;
				}

				parentId = parentIdsById.get(parentId);
			}

			return false;
		},
		[parentIdsById]
	);

	const getSelectedKeys = useCallback(
		(rulesById: Map<string, PageTreePickerSelectionRule>) => {
			const selectedKeys = new Set<React.Key>();

			itemsById.forEach((item, itemId) => {
				if (
					!item.disabled &&
					isEffectivelySelected(itemId, rulesById)
				) {
					selectedKeys.add(itemId);
				}
			});

			return selectedKeys;
		},
		[isEffectivelySelected, itemsById]
	);

	const [rulesById, setRulesByIdState] = useState<
		Map<string, PageTreePickerSelectionRule>
	>(() => {
		const rulesById = new Map<string, PageTreePickerSelectionRule>();

		defaultSelectedEntries?.forEach((entry) => {
			const rule = rulesById.get(entry.item.id) ?? {};

			if (entry.includeDescendants) {
				rulesById.set(entry.item.id, {
					descendants: !entry.excluded,
					self: rule.self ?? !entry.excluded,
				});
			}
			else {
				rulesById.set(entry.item.id, {...rule, self: !entry.excluded});
			}
		});

		return rulesById;
	});

	const rulesByIdRef = useRef(rulesById);

	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(() =>
		getSelectedKeys(rulesById)
	);

	const setRulesById = useCallback(
		(nextRulesById: Map<string, PageTreePickerSelectionRule>) => {
			rulesByIdRef.current = nextRulesById;

			setRulesByIdState(nextRulesById);
			setSelectedKeys(getSelectedKeys(nextRulesById));
		},
		[getSelectedKeys]
	);

	const getSelectedItems = useCallback(() => {
		const selectedItems: Array<PageTreePickerItem<T>> = [];

		itemsById.forEach((item, itemId) => {
			if (!item.disabled && isEffectivelySelected(itemId, rulesById)) {
				selectedItems.push(item);
			}
		});

		return selectedItems;
	}, [isEffectivelySelected, itemsById, rulesById]);

	const registerItems = useCallback(
		(items: Array<PageTreePickerItem<T>>, parentId: string | null) => {
			items.forEach((item) => {
				itemsById.set(item.id, {...item});

				if (item.parentId !== undefined) {
					parentIdsById.set(item.id, item.parentId);
				}
				else if (!parentIdsById.has(item.id)) {
					parentIdsById.set(item.id, parentId);
				}
			});

			setSelectedKeys(getSelectedKeys(rulesByIdRef.current));
		},
		[getSelectedKeys, itemsById, parentIdsById]
	);

	const isDescendant = useCallback(
		(itemId: string, ancestorId: string) => {
			let parentId = parentIdsById.get(itemId);

			while (parentId) {
				if (parentId === ancestorId) {
					return true;
				}

				parentId = parentIdsById.get(parentId);
			}

			return false;
		},
		[parentIdsById]
	);

	const select = useCallback(
		(item: PageTreePickerItem<T>) => {
			setRulesById(new Map([[item.id, {self: true}]]));
		},
		[setRulesById]
	);

	const toggleKey = useCallback(
		(item: PageTreePickerItem<T>) => {
			const nextRulesById = new Map(rulesByIdRef.current);

			const selected = isEffectivelySelected(item.id, nextRulesById);

			const {descendants} = nextRulesById.get(item.id) ?? {};

			nextRulesById.delete(item.id);

			const rule: PageTreePickerSelectionRule = {};

			if (descendants !== undefined) {
				rule.descendants = descendants;
			}

			if (
				descendants !== undefined ||
				isEffectivelySelected(item.id, nextRulesById) === selected
			) {
				rule.self = !selected;
			}

			if (Object.keys(rule).length) {
				nextRulesById.set(item.id, rule);
			}

			setRulesById(nextRulesById);
		},
		[isEffectivelySelected, setRulesById]
	);

	const toggleSubtree = useCallback(
		(item: PageTreePickerItem<T>) => {
			const nextRulesById = new Map(rulesByIdRef.current);

			const selected = isEffectivelySelected(item.id, nextRulesById);

			nextRulesById.delete(item.id);

			nextRulesById.forEach((rule, ruleItemId) => {
				if (isDescendant(ruleItemId, item.id)) {
					nextRulesById.delete(ruleItemId);
				}
			});

			if (isEffectivelySelected(item.id, nextRulesById) === selected) {
				nextRulesById.set(item.id, {
					self: !selected,
					...(item.hasChildren && {descendants: !selected}),
				});
			}

			setRulesById(nextRulesById);
		},
		[isDescendant, isEffectivelySelected, setRulesById]
	);

	const onSelectionChangeRef = useRef(onSelectionChange);

	useEffect(() => {
		onSelectionChangeRef.current = onSelectionChange;
	}, [onSelectionChange]);

	useEffect(() => {
		const entries: Array<PageTreePickerSelectionEntry<T>> = [];

		rulesById.forEach((rule, itemId) => {
			const item = itemsById.get(itemId);

			if (!item) {
				return;
			}

			if (rule.descendants !== undefined) {
				entries.push({
					excluded: !rule.descendants,
					includeDescendants: true,
					item,
				});
			}

			if (rule.self !== undefined && rule.self !== rule.descendants) {
				entries.push({
					excluded: !rule.self,
					includeDescendants: false,
					item,
				});
			}
		});

		onSelectionChangeRef.current?.(entries);
	}, [itemsById, rulesById]);

	return {
		getSelectedItems,
		isDescendant,
		registerItems,
		select,
		selectedKeys,
		toggleKey,
		toggleSubtree,
	};
}
