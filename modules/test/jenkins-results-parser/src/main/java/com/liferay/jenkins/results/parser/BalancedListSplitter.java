/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Peter Yoo
 */
public abstract class BalancedListSplitter<T extends WeightedItem> {

	public BalancedListSplitter(long maxListWeight) {
		_maxListWeight = maxListWeight;
	}

	public List<List<T>> split(List<T> list) {
		ListItemList listItems = new ListItemList(0L);

		for (T item : list) {
			listItems.add(new ListItem(item));
		}

		NavigableMap<Long, List<ListItemList>> listItemListsNavigableMap =
			new TreeMap<>();

		for (ListItem listItem : listItems) {
			Map.Entry<Long, List<ListItemList>> entry =
				listItemListsNavigableMap.ceilingEntry(listItem.getWeight());

			ListItemList listItemList = null;

			if (entry != null) {
				List<ListItemList> availableListItemLists = entry.getValue();

				if (!availableListItemLists.isEmpty()) {
					listItemList = availableListItemLists.remove(0);

					if (availableListItemLists.isEmpty()) {
						listItemListsNavigableMap.remove(entry.getKey());
					}
				}
			}

			if (listItemList == null) {
				listItemList = new ListItemList(getMaxListWeight());
			}

			listItemList.add(listItem);

			List<ListItemList> listItemLists =
				listItemListsNavigableMap.computeIfAbsent(
					listItemList.getAvailableWeight(), k -> new ArrayList<>());

			listItemLists.add(listItemList);
		}

		List<ListItemList> allListItemLists = new ArrayList<>();

		for (List<ListItemList> listItemLists :
				listItemListsNavigableMap.values()) {

			allListItemLists.addAll(listItemLists);
		}

		List<List<T>> lists = new ArrayList<>(allListItemLists.size());

		for (ListItemList listItemList : allListItemLists) {
			List<T> newList = listItemList.toList();

			if ((newList == null) || newList.isEmpty()) {
				continue;
			}

			lists.add(newList);
		}

		return lists;
	}

	protected long getMaxListWeight() {
		return _maxListWeight;
	}

	protected class ListItem implements Comparable<ListItem> {

		public ListItem(T item) {
			_item = item;
		}

		@Override
		public int compareTo(ListItem otherListItem) {
			Long weight = getWeight();

			return -1 * weight.compareTo(otherListItem.getWeight());
		}

		public T getItem() {
			return _item;
		}

		public long getOverheadWeight() {
			return _item.getOverheadWeight();
		}

		public long getSharedWeight() {
			return _item.getSharedWeight();
		}

		public String getSharedWeightName() {
			return _item.getSharedWeightName();
		}

		public long getWeight() {
			return _item.getWeight();
		}

		private final T _item;

	}

	protected class ListItemList
		extends ArrayList<ListItem> implements Comparable<ListItemList> {

		public ListItemList(Long targetWeight) {
			_targetWeight = targetWeight;
		}

		@Override
		public int compareTo(ListItemList otherListItemSortedSet) {
			Long availableWeight = getAvailableWeight();
			Long otherAvailableWeight =
				otherListItemSortedSet.getAvailableWeight();

			if ((availableWeight == null) && (otherAvailableWeight == null)) {
				return 0;
			}

			if (availableWeight == null) {
				return 1;
			}

			if (otherAvailableWeight == null) {
				return -1;
			}

			return -1 * availableWeight.compareTo(otherAvailableWeight);
		}

		public Long getAvailableWeight() {
			if (_targetWeight == null) {
				return null;
			}

			long availableWeight = _targetWeight - getWeight();

			if (availableWeight <= 0) {
				return 0L;
			}

			return availableWeight;
		}

		public long getWeight() {
			if (size() == 0) {
				return 0L;
			}

			Set<String> sharedWeightNames = new HashSet<>();
			long totalOverheadWeight = 0L;
			long totalSharedWeight = 0L;
			long totalWeight = 0L;

			for (ListItem listItem : this) {
				totalOverheadWeight += listItem.getOverheadWeight();
				totalWeight += listItem.getWeight();

				if (listItem.getSharedWeightName() == null) {
					continue;
				}

				if (!sharedWeightNames.contains(
						listItem.getSharedWeightName())) {

					totalSharedWeight += listItem.getSharedWeight();
				}

				sharedWeightNames.add(listItem.getSharedWeightName());
			}

			long averageOverheadWeight = totalOverheadWeight / size();

			return averageOverheadWeight + totalSharedWeight + totalWeight;
		}

		public List<T> toList() {
			List<T> list = new ArrayList<>(size());

			for (ListItem listItem : this) {
				list.add(listItem._item);
			}

			return list;
		}

		private final Long _targetWeight;

	}

	private final long _maxListWeight;

}