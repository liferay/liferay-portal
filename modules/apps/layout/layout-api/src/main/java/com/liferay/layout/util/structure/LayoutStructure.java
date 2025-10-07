/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.fragment.service.FragmentEntryLinkServiceUtil;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.constants.LayoutStructureConstants;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class LayoutStructure {

	public static LayoutStructure of(JSONObject layoutStructureJSONObject) {
		if (JSONUtil.isEmpty(layoutStructureJSONObject)) {
			return new LayoutStructure();
		}

		Set<String> deletedItemIds = new HashSet<>();
		Set<String> deletedPortletIds = new HashSet<>();

		JSONObject rootItemsJSONObject =
			layoutStructureJSONObject.getJSONObject("rootItems");

		JSONObject itemsJSONObject = layoutStructureJSONObject.getJSONObject(
			"items");

		List<CollectionStyledLayoutStructureItem>
			collectionStyledLayoutStructureItems = new ArrayList<>();
		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			new ArrayList<>();
		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			new HashMap<>(itemsJSONObject.length());
		Map<String, LayoutStructureItem> layoutStructureItems = new HashMap<>(
			itemsJSONObject.length());

		for (String key : itemsJSONObject.keySet()) {
			LayoutStructureItem layoutStructureItem = LayoutStructureItem.of(
				itemsJSONObject.getJSONObject(key));

			layoutStructureItems.put(key, layoutStructureItem);

			_updateLayoutStructureItemMaps(
				layoutStructureItem, collectionStyledLayoutStructureItems,
				formStyledLayoutStructureItems, fragmentLayoutStructureItems);
		}

		JSONArray deletedLayoutStructureItemJSONArray =
			layoutStructureJSONObject.getJSONArray("deletedItems");

		if (deletedLayoutStructureItemJSONArray == null) {
			deletedLayoutStructureItemJSONArray =
				JSONFactoryUtil.createJSONArray();
		}

		Map<String, DeletedLayoutStructureItem> deletedLayoutStructureItems =
			new HashMap<>(deletedLayoutStructureItemJSONArray.length());

		deletedLayoutStructureItemJSONArray.forEach(
			deletedLayoutStructureItemJSONObject -> {
				DeletedLayoutStructureItem deletedLayoutStructureItem =
					DeletedLayoutStructureItem.of(
						(JSONObject)deletedLayoutStructureItemJSONObject);

				deletedItemIds.add(deletedLayoutStructureItem.getItemId());
				deletedItemIds.addAll(
					deletedLayoutStructureItem.getChildrenItemIds());

				deletedPortletIds.addAll(
					deletedLayoutStructureItem.getPortletIds());

				deletedLayoutStructureItems.put(
					deletedLayoutStructureItem.getItemId(),
					deletedLayoutStructureItem);
			});

		List<LayoutStructureRule> layoutStructureRules = new ArrayList<>();
		Map<String, LayoutStructureRule> layoutStructureRulesMap =
			new HashMap<>();

		JSONArray layoutStructureRulesJSONArray =
			layoutStructureJSONObject.getJSONArray("pageRules");

		if (!JSONUtil.isEmpty(layoutStructureRulesJSONArray)) {
			try {
				layoutStructureRules = JSONUtil.toList(
					layoutStructureJSONObject.getJSONArray("pageRules"),
					jsonObject -> {
						LayoutStructureRule layoutStructureRule =
							LayoutStructureRule.of(jsonObject);

						layoutStructureRulesMap.put(
							layoutStructureRule.getId(), layoutStructureRule);

						return layoutStructureRule;
					});
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return new LayoutStructure(
			collectionStyledLayoutStructureItems, deletedItemIds,
			deletedLayoutStructureItems, deletedPortletIds,
			formStyledLayoutStructureItems, fragmentLayoutStructureItems,
			layoutStructureItems, layoutStructureRules, layoutStructureRulesMap,
			rootItemsJSONObject.getString("main"));
	}

	public static LayoutStructure of(String layoutStructure) {
		try {
			return of(JSONFactoryUtil.createJSONObject(layoutStructure));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON", jsonException);
			}
		}

		return new LayoutStructure();
	}

	public LayoutStructure() {
		_collectionStyledLayoutStructureItems = new ArrayList<>();
		_deletedItemIds = new HashSet<>();
		_deletedLayoutStructureItems = new HashMap<>();
		_deletedPortletIds = new HashSet<>();
		_formStyledLayoutStructureItems = new ArrayList<>();
		_fragmentLayoutStructureItems = new HashMap<>();
		_layoutStructureItems = new HashMap<>();
		_layoutStructureRules = new ArrayList<>();
		_layoutStructureRulesMap = new HashMap<>();
		_mainItemId = StringPool.BLANK;
	}

	public LayoutStructureItem addCollectionItemLayoutStructureItem(
		String parentItemId, int position) {

		return addCollectionItemLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addCollectionItemLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		CollectionItemLayoutStructureItem collectionItemLayoutStructureItem =
			new CollectionItemLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(collectionItemLayoutStructureItem, position);

		return collectionItemLayoutStructureItem;
	}

	public LayoutStructureItem addCollectionStyledLayoutStructureItem(
		String parentItemId, int position) {

		return addCollectionStyledLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addCollectionStyledLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		return addCollectionStyledLayoutStructureItem(
			PortalUUIDUtil.generate(), itemId, parentItemId, position);
	}

	public LayoutStructureItem addCollectionStyledLayoutStructureItem(
		String collectionItemItemId, String itemId, String parentItemId,
		int position) {

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				new CollectionStyledLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(collectionStyledLayoutStructureItem, position);

		addCollectionItemLayoutStructureItem(
			collectionItemItemId,
			collectionStyledLayoutStructureItem.getItemId(), 0);

		return collectionStyledLayoutStructureItem;
	}

	public LayoutStructureItem addColumnLayoutStructureItem(
		String parentItemId, int position) {

		return addColumnLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addColumnLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		ColumnLayoutStructureItem columnLayoutStructureItem =
			new ColumnLayoutStructureItem(itemId, parentItemId);

		columnLayoutStructureItem.setSize(_MAX_COLUMNS);

		_updateLayoutStructure(columnLayoutStructureItem, position);

		return columnLayoutStructureItem;
	}

	public LayoutStructureItem addContainerStyledLayoutStructureItem(
		String parentItemId, int position) {

		return addContainerStyledLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addContainerStyledLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			new ContainerStyledLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(containerStyledLayoutStructureItem, position);

		return containerStyledLayoutStructureItem;
	}

	public LayoutStructureItem addDropZoneLayoutStructureItem(
		String parentItemId, int position) {

		return addDropZoneLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addDropZoneLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			new DropZoneLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(dropZoneLayoutStructureItem, position);

		return dropZoneLayoutStructureItem;
	}

	public LayoutStructureItem addFormRelationshipStyledLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		LayoutStructureItem parentLayoutStructureItem =
			_layoutStructureItems.get(parentItemId);

		if (!(parentLayoutStructureItem instanceof
				FormStyledLayoutStructureItem) &&
			!LayoutStructureItemUtil.hasAncestor(
				parentItemId, LayoutDataItemTypeConstants.TYPE_FORM, this)) {

			throw new UnsupportedOperationException(
				"Form relationship can only be added inside of a form");
		}

		FormRelationshipStyledLayoutStructureItem
			formRelationshipStyledLayoutStructureItem =
				new FormRelationshipStyledLayoutStructureItem(
					itemId, parentItemId);

		_updateLayoutStructure(
			formRelationshipStyledLayoutStructureItem, position);

		return formRelationshipStyledLayoutStructureItem;
	}

	public LayoutStructureItem addFormStepContainerStyledLayoutStructureItem(
		String parentItemId, int position) {

		return addFormStepContainerStyledLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addFormStepContainerStyledLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		LayoutStructureItem parentLayoutStructureItem =
			_layoutStructureItems.get(parentItemId);

		if (!(parentLayoutStructureItem instanceof
				FormStyledLayoutStructureItem)) {

			throw new UnsupportedOperationException(
				"Form step container can only be added inside of a form");
		}

		FormStepContainerStyledLayoutStructureItem
			formStepContainerStyledLayoutStructureItem =
				new FormStepContainerStyledLayoutStructureItem(
					itemId, parentItemId);

		_updateLayoutStructure(
			formStepContainerStyledLayoutStructureItem, position);

		return formStepContainerStyledLayoutStructureItem;
	}

	public LayoutStructureItem addFormStepLayoutStructureItem(
		String parentItemId, int position) {

		return addFormStepLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addFormStepLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		LayoutStructureItem parentLayoutStructureItem =
			_layoutStructureItems.get(parentItemId);

		if (!(parentLayoutStructureItem instanceof
				FormStepContainerStyledLayoutStructureItem)) {

			throw new UnsupportedOperationException(
				"Form step can only be added inside of a form step container");
		}

		FormStepLayoutStructureItem formStepLayoutStructureItem =
			new FormStepLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(formStepLayoutStructureItem, position);

		return formStepLayoutStructureItem;
	}

	public LayoutStructureItem addFormStyledLayoutStructureItem(
		String parentItemId, int position) {

		return addFormStyledLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addFormStyledLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			new FormStyledLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(formStyledLayoutStructureItem, position);

		_formStyledLayoutStructureItems.add(formStyledLayoutStructureItem);

		return formStyledLayoutStructureItem;
	}

	public LayoutStructureItem addFragmentDropZoneLayoutStructureItem(
		String parentItemId, int position) {

		return addFragmentDropZoneLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position);
	}

	public LayoutStructureItem addFragmentDropZoneLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				new FragmentDropZoneLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(fragmentDropZoneLayoutStructureItem, position);

		return fragmentDropZoneLayoutStructureItem;
	}

	public LayoutStructureItem addFragmentStyledLayoutStructureItem(
		long fragmentEntryLinkId, String parentItemId, int position) {

		return addFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId, PortalUUIDUtil.generate(), parentItemId,
			position);
	}

	public LayoutStructureItem addFragmentStyledLayoutStructureItem(
		long fragmentEntryLinkId, String itemId, String parentItemId,
		int position) {

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			new FragmentStyledLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(fragmentStyledLayoutStructureItem, position);

		fragmentStyledLayoutStructureItem.setFragmentEntryLinkId(
			fragmentEntryLinkId);

		_fragmentLayoutStructureItems.put(
			fragmentEntryLinkId, fragmentStyledLayoutStructureItem);

		return fragmentStyledLayoutStructureItem;
	}

	public LayoutStructureItem addLayoutStructureItem(
		LayoutStructureItem layoutStructureItem) {

		_layoutStructureItems.put(
			layoutStructureItem.getItemId(), layoutStructureItem);

		_updateLayoutStructureItemMaps(
			layoutStructureItem, _collectionStyledLayoutStructureItems,
			_formStyledLayoutStructureItems, _fragmentLayoutStructureItems);

		return layoutStructureItem;
	}

	public LayoutStructureItem addLayoutStructureItem(
		String itemType, String parentItemId, int position) {

		return addLayoutStructureItem(
			PortalUUIDUtil.generate(), itemType, parentItemId, position);
	}

	public LayoutStructureItem addLayoutStructureItem(
		String itemId, String itemType, String parentItemId, int position) {

		LayoutStructureItem layoutStructureItem =
			LayoutStructureItemUtil.create(itemType, parentItemId);

		if (Validator.isNotNull(itemId)) {
			layoutStructureItem.setItemId(itemId);
		}

		_updateLayoutStructure(layoutStructureItem, position);

		return layoutStructureItem;
	}

	public LayoutStructureRule addLayoutStructureRule(String name) {
		LayoutStructureRule layoutStructureRule = new LayoutStructureRule(
			PortalUUIDUtil.generate(), name);

		_layoutStructureRules.add(layoutStructureRule);

		return layoutStructureRule;
	}

	public LayoutStructureRule addLayoutStructureRule(String id, String name) {
		LayoutStructureRule layoutStructureRule = new LayoutStructureRule(
			id, name);

		_layoutStructureRules.add(layoutStructureRule);

		return layoutStructureRule;
	}

	public LayoutStructureItem addRootLayoutStructureItem() {
		return addRootLayoutStructureItem(PortalUUIDUtil.generate());
	}

	public LayoutStructureItem addRootLayoutStructureItem(String itemId) {
		RootLayoutStructureItem rootLayoutStructureItem =
			new RootLayoutStructureItem(itemId);

		_updateLayoutStructure(rootLayoutStructureItem, 0);

		if (Validator.isNull(_mainItemId)) {
			_mainItemId = rootLayoutStructureItem.getItemId();
		}

		return rootLayoutStructureItem;
	}

	public LayoutStructureItem addRowStyledLayoutStructureItem(
		String parentItemId, int position, int numberOfColumns) {

		return addRowStyledLayoutStructureItem(
			PortalUUIDUtil.generate(), parentItemId, position, numberOfColumns);
	}

	public LayoutStructureItem addRowStyledLayoutStructureItem(
		String itemId, String parentItemId, int position, int numberOfColumns) {

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			new RowStyledLayoutStructureItem(itemId, parentItemId);

		_updateLayoutStructure(rowStyledLayoutStructureItem, position);

		rowStyledLayoutStructureItem.setNumberOfColumns(numberOfColumns);

		return rowStyledLayoutStructureItem;
	}

	public List<LayoutStructureItem> copyLayoutStructureItems(
		List<String> itemIds, String parentItemId) {

		LayoutStructureItem parentLayoutStructureItem =
			_layoutStructureItems.get(parentItemId);

		if (parentLayoutStructureItem instanceof
				CollectionItemLayoutStructureItem) {

			throw new UnsupportedOperationException(
				StringBundler.concat(
					"Unable to copy items because layout structure item of ",
					"type ", parentLayoutStructureItem.getItemType(),
					" cannot be selected as parent item"));
		}

		int position = 0;

		List<LayoutStructureItem> copiedLayoutStructureItems =
			new ArrayList<>();

		for (String itemId : itemIds) {
			if (Objects.equals(itemId, parentItemId)) {
				parentLayoutStructureItem = _layoutStructureItems.get(
					parentItemId);
			}

			String currentParentItemId = parentLayoutStructureItem.getItemId();

			if (Objects.equals(itemId, parentItemId) ||
				(parentLayoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) ||
				(parentLayoutStructureItem instanceof
					RowStyledLayoutStructureItem)) {

				String oldParentItemId = parentLayoutStructureItem.getItemId();

				currentParentItemId =
					parentLayoutStructureItem.getParentItemId();

				parentLayoutStructureItem = _layoutStructureItems.get(
					currentParentItemId);

				List<String> childrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				position = childrenItemIds.indexOf(oldParentItemId) + 1;
			}
			else if (parentLayoutStructureItem instanceof
						CollectionStyledLayoutStructureItem) {

				List<String> childrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				if (ListUtil.isEmpty(childrenItemIds)) {
					throw new UnsupportedOperationException(
						"Unable to copy items because collection does not " +
							"have collection items");
				}

				currentParentItemId = childrenItemIds.get(0);

				position = 0;
			}
			else if (_isMultistepFormTypeFormStyledLayoutStructureItem(
						parentLayoutStructureItem)) {

				List<String> childrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				if (ListUtil.isEmpty(childrenItemIds)) {
					throw new UnsupportedOperationException(
						"Unable to copy items because form step does not " +
							"have a form step container");
				}

				for (String childItemId : childrenItemIds) {
					LayoutStructureItem layoutStructureItem =
						_layoutStructureItems.get(childItemId);

					if (!(layoutStructureItem instanceof
							FormStepContainerStyledLayoutStructureItem)) {

						continue;
					}

					FormStepContainerStyledLayoutStructureItem
						formStepContainerStyledLayoutStructureItem =
							(FormStepContainerStyledLayoutStructureItem)
								layoutStructureItem;

					childrenItemIds =
						formStepContainerStyledLayoutStructureItem.
							getChildrenItemIds();

					if (ListUtil.isEmpty(childrenItemIds)) {
						throw new UnsupportedOperationException(
							"Unable to copy items because form step does not " +
								"have any step items");
					}

					currentParentItemId = childrenItemIds.get(0);

					break;
				}

				position = 0;
			}

			List<String> childrenItemIds =
				LayoutStructureItemUtil.getChildrenItemIds(itemId, this);

			if (childrenItemIds.contains(currentParentItemId)) {
				throw new UnsupportedOperationException(
					"Unable to copy items because parent item ID cannot be a " +
						"child of item ID");
			}

			LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
				itemId);

			if (layoutStructureItem instanceof ColumnLayoutStructureItem ||
				layoutStructureItem instanceof DropZoneLayoutStructureItem ||
				layoutStructureItem instanceof FormStepLayoutStructureItem ||
				layoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem ||
				layoutStructureItem instanceof RootLayoutStructureItem) {

				throw new UnsupportedOperationException(
					StringBundler.concat(
						"Unable to copy items because layout structure item ",
						"of type ", layoutStructureItem.getItemType(),
						" cannot be copied"));
			}

			List<LayoutStructureItem> duplicatedLayoutStructureItems =
				_duplicateLayoutStructureItem(
					itemId, currentParentItemId, position);

			copiedLayoutStructureItems.addAll(duplicatedLayoutStructureItems);

			parentLayoutStructureItem = duplicatedLayoutStructureItems.get(0);
		}

		return copiedLayoutStructureItems;
	}

	public List<LayoutStructureItem> deleteLayoutStructureItem(String itemId) {
		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
			throw new UnsupportedOperationException(
				"Removing the drop zone of a layout structure is not allowed");
		}

		List<LayoutStructureItem> deletedLayoutStructureItems =
			new ArrayList<>();

		List<String> childrenItemIds = new ArrayList<>(
			layoutStructureItem.getChildrenItemIds());

		for (String childrenItemId : childrenItemIds) {
			deletedLayoutStructureItems.addAll(
				deleteLayoutStructureItem(childrenItemId));
		}

		deletedLayoutStructureItems.add(layoutStructureItem);

		if (Validator.isNotNull(layoutStructureItem.getParentItemId())) {
			LayoutStructureItem parentLayoutStructureItem =
				_layoutStructureItems.get(
					layoutStructureItem.getParentItemId());

			if (parentLayoutStructureItem != null) {
				parentLayoutStructureItem.deleteChildrenItem(itemId);
			}
		}

		_deletedLayoutStructureItems.remove(itemId);
		_layoutStructureItems.remove(itemId);

		return deletedLayoutStructureItems;
	}

	public LayoutStructureRule deleteLayoutStructureRule(String ruleId) {
		LayoutStructureRule layoutStructureRule = _layoutStructureRulesMap.get(
			ruleId);

		if (layoutStructureRule != null) {
			_layoutStructureRules.remove(layoutStructureRule);
		}

		return layoutStructureRule;
	}

	public Map<String, List<LayoutStructureItem>> duplicateLayoutStructureItem(
		List<String> itemIds) {

		Map<String, List<LayoutStructureItem>>
			duplicatedLayoutStructureItemsMap = new HashMap<>();

		for (String itemId : itemIds) {
			LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
				itemId);

			LayoutStructureItem parentLayoutStructureItem =
				_layoutStructureItems.get(
					layoutStructureItem.getParentItemId());

			List<String> childrenItemIds =
				parentLayoutStructureItem.getChildrenItemIds();

			int position = childrenItemIds.indexOf(itemId) + 1;

			List<LayoutStructureItem> duplicatedLayoutStructureItems =
				_duplicateLayoutStructureItem(
					itemId, layoutStructureItem.getParentItemId(), position);

			if (ListUtil.isEmpty(duplicatedLayoutStructureItems)) {
				continue;
			}

			LayoutStructureItem duplicatedLayoutStructure =
				duplicatedLayoutStructureItems.get(0);

			duplicatedLayoutStructureItemsMap.put(
				duplicatedLayoutStructure.getItemId(),
				duplicatedLayoutStructureItems);
		}

		return duplicatedLayoutStructureItemsMap;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutStructure)) {
			return false;
		}

		LayoutStructure layoutStructure = (LayoutStructure)object;

		if (Objects.equals(_mainItemId, layoutStructure._mainItemId) &&
			Objects.equals(
				_layoutStructureItems, layoutStructure._layoutStructureItems)) {

			return true;
		}

		return false;
	}

	public List<CollectionStyledLayoutStructureItem>
		getCollectionStyledLayoutStructureItems() {

		return _collectionStyledLayoutStructureItems;
	}

	public List<DeletedLayoutStructureItem> getDeletedLayoutStructureItems() {
		return ListUtil.fromCollection(_deletedLayoutStructureItems.values());
	}

	public LayoutStructureItem getDropZoneLayoutStructureItem() {
		for (LayoutStructureItem layoutStructureItem :
				getLayoutStructureItems()) {

			if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
				return layoutStructureItem;
			}
		}

		return null;
	}

	public List<FormStyledLayoutStructureItem>
		getFormStyledLayoutStructureItems() {

		return _formStyledLayoutStructureItems;
	}

	public Map<Long, LayoutStructureItem> getFragmentLayoutStructureItems() {
		return _fragmentLayoutStructureItems;
	}

	public LayoutStructureItem getLayoutStructureItem(String itemId) {
		return _layoutStructureItems.get(itemId);
	}

	public LayoutStructureItem getLayoutStructureItemByFragmentEntryLinkId(
		long fragmentEntryLinkId) {

		return _fragmentLayoutStructureItems.get(fragmentEntryLinkId);
	}

	public List<LayoutStructureItem> getLayoutStructureItems() {
		return ListUtil.fromCollection(_layoutStructureItems.values());
	}

	public List<LayoutStructureRule> getLayoutStructureRules() {
		return _layoutStructureRules;
	}

	public String getMainItemId() {
		return _mainItemId;
	}

	public LayoutStructureItem getMainLayoutStructureItem() {
		return _layoutStructureItems.get(_mainItemId);
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getMainItemId());
	}

	public boolean isItemMarkedForDeletion(String itemId) {
		return _deletedItemIds.contains(itemId);
	}

	public boolean isPortletMarkedForDeletion(String portletId) {
		return _deletedPortletIds.contains(portletId);
	}

	public void markLayoutStructureItemForDeletion(
		List<String> itemIds, List<String> portletIds) {

		for (String itemId : itemIds) {
			LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
				itemId);

			if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
				throw new UnsupportedOperationException(
					"Removing the drop zone of a layout structure is not " +
						"allowed");
			}

			DeletedLayoutStructureItem deletedLayoutStructureItem = null;

			if (Validator.isNotNull(layoutStructureItem.getParentItemId())) {
				LayoutStructureItem parentLayoutStructureItem =
					_layoutStructureItems.get(
						layoutStructureItem.getParentItemId());

				List<String> childrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				deletedLayoutStructureItem = new DeletedLayoutStructureItem(
					itemId, portletIds, childrenItemIds.indexOf(itemId),
					_getChildrenItemIds(itemId));
			}
			else {
				deletedLayoutStructureItem = new DeletedLayoutStructureItem(
					itemId, portletIds, 0, _getChildrenItemIds(itemId));
			}

			_updateFragmentEntryLinks(itemId, true);

			_deletedLayoutStructureItems.put(
				itemId, deletedLayoutStructureItem);

			_deletedItemIds.add(itemId);
			_deletedItemIds.addAll(
				deletedLayoutStructureItem.getChildrenItemIds());
		}

		for (String itemId : itemIds) {
			LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
				itemId);

			if (Validator.isNotNull(layoutStructureItem.getParentItemId())) {
				LayoutStructureItem parentLayoutStructureItem =
					_layoutStructureItems.get(
						layoutStructureItem.getParentItemId());

				List<String> childrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				childrenItemIds.remove(layoutStructureItem.getItemId());
			}
		}

		_deletedPortletIds.addAll(portletIds);
	}

	public LayoutStructureItem moveLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		LayoutStructureItem oldParentLayoutStructureItem =
			_layoutStructureItems.get(layoutStructureItem.getParentItemId());

		oldParentLayoutStructureItem.deleteChildrenItem(itemId);

		LayoutStructureItem newParentLayoutStructureItem =
			_layoutStructureItems.get(parentItemId);

		if (position >= 0) {
			newParentLayoutStructureItem.addChildrenItem(
				position, layoutStructureItem.getItemId());
		}
		else {
			newParentLayoutStructureItem.addChildrenItem(
				layoutStructureItem.getItemId());
		}

		layoutStructureItem.setParentItemId(parentItemId);

		return layoutStructureItem;
	}

	public void setMainItemId(String mainItemId) {
		_mainItemId = mainItemId;
	}

	public JSONObject toJSONObject() {
		String dropZoneItemId = StringPool.BLANK;
		JSONObject layoutStructureItemsJSONObject =
			JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, LayoutStructureItem> entry :
				_layoutStructureItems.entrySet()) {

			LayoutStructureItem layoutStructureItem = entry.getValue();

			if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
				dropZoneItemId = layoutStructureItem.getItemId();
			}

			if (layoutStructureItem == null) {
				throw new RuntimeException(
					"Invalid layout structure item for key " + entry.getKey());
			}

			layoutStructureItemsJSONObject.put(
				entry.getKey(), layoutStructureItem.toJSONObject());
		}

		JSONArray deletedLayoutStructureItemsJSONArray =
			JSONFactoryUtil.createJSONArray();

		for (DeletedLayoutStructureItem deletedLayoutStructureItem :
				_deletedLayoutStructureItems.values()) {

			deletedLayoutStructureItemsJSONArray.put(
				deletedLayoutStructureItem.toJSONObject());
		}

		JSONArray layoutStructureRulesJSONArray =
			JSONFactoryUtil.createJSONArray();

		for (LayoutStructureRule layoutStructureRule : _layoutStructureRules) {
			layoutStructureRulesJSONArray.put(
				layoutStructureRule.toJSONObject());
		}

		return JSONUtil.put(
			"deletedItems", deletedLayoutStructureItemsJSONArray
		).put(
			"items", layoutStructureItemsJSONObject
		).put(
			"pageRules", layoutStructureRulesJSONArray
		).put(
			"rootItems",
			JSONUtil.put(
				"dropZone", dropZoneItemId
			).put(
				"main", _mainItemId
			)
		).put(
			"version", LayoutStructureConstants.LATEST_PAGE_DEFINITION_VERSION
		);
	}

	@Override
	public String toString() {
		JSONObject jsonObject = toJSONObject();

		return jsonObject.toString();
	}

	public void unmarkLayoutStructureItemForDeletion(String itemId) {
		DeletedLayoutStructureItem deletedLayoutStructureItem =
			_deletedLayoutStructureItems.get(itemId);

		if (deletedLayoutStructureItem == null) {
			return;
		}

		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		LayoutStructureItem parentLayoutStructureItemId =
			_layoutStructureItems.get(layoutStructureItem.getParentItemId());

		parentLayoutStructureItemId.addChildrenItem(
			deletedLayoutStructureItem.getPosition(),
			deletedLayoutStructureItem.getItemId());

		_updateFragmentEntryLinks(itemId, false);

		_deletedItemIds.remove(itemId);
		_deletedItemIds.removeAll(
			deletedLayoutStructureItem.getChildrenItemIds());

		_deletedPortletIds.removeAll(
			deletedLayoutStructureItem.getPortletIds());

		_deletedLayoutStructureItems.remove(itemId);
	}

	public LayoutStructureItem updateItemConfig(
		JSONObject itemConfigJSONObject, String itemId) {

		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		layoutStructureItem.updateItemConfig(itemConfigJSONObject);

		if (layoutStructureItem instanceof RowStyledLayoutStructureItem) {
			RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
				(RowStyledLayoutStructureItem)layoutStructureItem;

			int modulesPerRow = itemConfigJSONObject.getInt("modulesPerRow");

			if (modulesPerRow > 0) {
				_updateColumnSizes(
					rowStyledLayoutStructureItem,
					ViewportSize.DESKTOP.getViewportSizeId(), modulesPerRow,
					true);
			}

			for (ViewportSize viewportSize : _viewportSizes) {
				if (viewportSize.equals(ViewportSize.DESKTOP) ||
					!itemConfigJSONObject.has(
						viewportSize.getViewportSizeId())) {

					continue;
				}

				JSONObject viewportItemConfigJSONObject =
					itemConfigJSONObject.getJSONObject(
						viewportSize.getViewportSizeId());

				modulesPerRow = viewportItemConfigJSONObject.getInt(
					"modulesPerRow");

				if (modulesPerRow == 0) {
					continue;
				}

				_updateColumnSizes(
					rowStyledLayoutStructureItem,
					viewportSize.getViewportSizeId(), modulesPerRow, true);
			}
		}

		return layoutStructureItem;
	}

	public LayoutStructureRule updateLayoutStructureRule(
		JSONArray actionsJSONArray, JSONArray conditionsJSONArray,
		String conditionType, String name, String ruleId) {

		LayoutStructureRule layoutStructureRule = _layoutStructureRulesMap.get(
			ruleId);

		if (layoutStructureRule != null) {
			layoutStructureRule.setActionsJSONArray(actionsJSONArray);
			layoutStructureRule.setConditionsJSONArray(conditionsJSONArray);
			layoutStructureRule.setConditionType(conditionType);
			layoutStructureRule.setName(name);
		}

		return layoutStructureRule;
	}

	public void updateRowColumnsLayoutStructureItem(
		String itemId, int numberOfColumns) {

		if (numberOfColumns > _MAX_COLUMNS) {
			return;
		}

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)_layoutStructureItems.get(itemId);

		int oldNumberOfColumns =
			rowStyledLayoutStructureItem.getNumberOfColumns();

		if (oldNumberOfColumns == numberOfColumns) {
			return;
		}

		rowStyledLayoutStructureItem.setModulesPerRow(numberOfColumns);
		rowStyledLayoutStructureItem.setNumberOfColumns(numberOfColumns);

		for (ViewportSize viewportSize : _viewportSizes) {
			if (viewportSize.equals(ViewportSize.DESKTOP)) {
				continue;
			}

			_updateNumberOfColumns(
				rowStyledLayoutStructureItem, viewportSize.getViewportSizeId(),
				numberOfColumns);
		}

		List<String> childrenItemIds = new ArrayList<>(
			rowStyledLayoutStructureItem.getChildrenItemIds());

		if (oldNumberOfColumns < numberOfColumns) {
			for (int i = 0; i < oldNumberOfColumns; i++) {
				String childrenItemId = childrenItemIds.get(i);

				ColumnLayoutStructureItem columnLayoutStructureItem =
					(ColumnLayoutStructureItem)_layoutStructureItems.get(
						childrenItemId);

				columnLayoutStructureItem.setSize(
					LayoutStructureConstants.COLUMN_SIZES[numberOfColumns - 1]
						[i]);
			}

			for (int i = oldNumberOfColumns; i < numberOfColumns; i++) {
				_addColumnLayoutStructureItem(
					itemId, i,
					LayoutStructureConstants.COLUMN_SIZES[numberOfColumns - 1]
						[i]);
			}

			return;
		}

		for (int i = 0; i < numberOfColumns; i++) {
			String childrenItemId = childrenItemIds.get(i);

			ColumnLayoutStructureItem columnLayoutStructureItem =
				(ColumnLayoutStructureItem)_layoutStructureItems.get(
					childrenItemId);

			columnLayoutStructureItem.setSize(
				LayoutStructureConstants.COLUMN_SIZES[numberOfColumns - 1][i]);
		}

		for (int i = numberOfColumns; i < oldNumberOfColumns; i++) {
			String childrenItemId = childrenItemIds.get(i);

			markLayoutStructureItemForDeletion(
				Collections.singletonList(childrenItemId),
				Collections.emptyList());
		}
	}

	private static void _updateLayoutStructureItemMaps(
		LayoutStructureItem layoutStructureItem,
		List<CollectionStyledLayoutStructureItem>
			collectionStyledLayoutStructureItems,
		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems,
		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems) {

		if (layoutStructureItem instanceof
				CollectionStyledLayoutStructureItem) {

			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem =
					(CollectionStyledLayoutStructureItem)layoutStructureItem;

			collectionStyledLayoutStructureItems.add(
				collectionStyledLayoutStructureItem);
		}
		else if (layoutStructureItem instanceof FormStyledLayoutStructureItem) {
			FormStyledLayoutStructureItem formStyledLayoutStructureItem =
				(FormStyledLayoutStructureItem)layoutStructureItem;

			formStyledLayoutStructureItems.add(formStyledLayoutStructureItem);
		}
		else if (layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) {

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			fragmentLayoutStructureItems.put(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId(),
				fragmentStyledLayoutStructureItem);
		}
	}

	private LayoutStructure(
		List<CollectionStyledLayoutStructureItem>
			collectionStyledLayoutStructureItems,
		Set<String> deletedItemIds,
		Map<String, DeletedLayoutStructureItem> deletedLayoutStructureItems,
		Set<String> deletedPortletIds,
		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems,
		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems,
		Map<String, LayoutStructureItem> layoutStructureItems,
		List<LayoutStructureRule> layoutStructureRules,
		Map<String, LayoutStructureRule> layoutStructureRulesMap,
		String mainItemId) {

		_collectionStyledLayoutStructureItems =
			collectionStyledLayoutStructureItems;
		_deletedItemIds = deletedItemIds;
		_deletedLayoutStructureItems = deletedLayoutStructureItems;
		_deletedPortletIds = deletedPortletIds;
		_formStyledLayoutStructureItems = formStyledLayoutStructureItems;
		_fragmentLayoutStructureItems = fragmentLayoutStructureItems;
		_layoutStructureItems = layoutStructureItems;
		_layoutStructureRules = layoutStructureRules;
		_layoutStructureRulesMap = layoutStructureRulesMap;
		_mainItemId = mainItemId;
	}

	private void _addColumnLayoutStructureItem(
		String parentItemId, int position, int size) {

		ColumnLayoutStructureItem columnLayoutStructureItem =
			new ColumnLayoutStructureItem(parentItemId);

		columnLayoutStructureItem.setSize(size);
		columnLayoutStructureItem.setViewportConfiguration(
			ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
			JSONUtil.put("size", 12));

		_updateLayoutStructure(columnLayoutStructureItem, position);
	}

	private List<LayoutStructureItem> _duplicateLayoutStructureItem(
		String itemId, String parentItemId, int position) {

		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
			throw new UnsupportedOperationException(
				"Duplicating the drop zone of a layout structure is not " +
					"allowed");
		}

		LayoutStructureItem newLayoutStructureItem =
			LayoutStructureItemUtil.create(
				layoutStructureItem.getItemType(), parentItemId);

		List<LayoutStructureItem> duplicatedLayoutStructureItems =
			new ArrayList<>();

		newLayoutStructureItem.setItemId(PortalUUIDUtil.generate());

		newLayoutStructureItem.updateItemConfig(
			layoutStructureItem.getItemConfigJSONObject());

		_updateLayoutStructure(newLayoutStructureItem, position);

		duplicatedLayoutStructureItems.add(newLayoutStructureItem);

		for (String childrenItemId : layoutStructureItem.getChildrenItemIds()) {
			duplicatedLayoutStructureItems.addAll(
				_duplicateLayoutStructureItem(
					childrenItemId, newLayoutStructureItem.getItemId(), -1));
		}

		return duplicatedLayoutStructureItems;
	}

	private Set<String> _getChildrenItemIds(String itemId) {
		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		Set<String> childrenItemIds = new HashSet<>(
			layoutStructureItem.getChildrenItemIds());

		for (String childrenItemId : layoutStructureItem.getChildrenItemIds()) {
			childrenItemIds.addAll(_getChildrenItemIds(childrenItemId));
		}

		return childrenItemIds;
	}

	private boolean _isMultistepFormTypeFormStyledLayoutStructureItem(
		LayoutStructureItem layoutStructureItem) {

		if (!(layoutStructureItem instanceof FormStyledLayoutStructureItem)) {
			return false;
		}

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)layoutStructureItem;

		return Objects.equals(
			formStyledLayoutStructureItem.getFormType(), "multistep");
	}

	private void _updateColumnSizes(
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem,
		String viewportSizeId, int modulesPerRow, boolean updateEmpty) {

		int[] defaultSizes = LayoutStructureConstants.COLUMN_SIZES
			[rowStyledLayoutStructureItem.getNumberOfColumns() - 1];

		if (rowStyledLayoutStructureItem.getNumberOfColumns() !=
				modulesPerRow) {

			defaultSizes =
				_MODULE_SIZES
					[rowStyledLayoutStructureItem.getNumberOfColumns() - 2]
					[modulesPerRow - 1];
		}

		int position = 0;

		for (String childItemId :
				rowStyledLayoutStructureItem.getChildrenItemIds()) {

			LayoutStructureItem layoutStructureItem = getLayoutStructureItem(
				childItemId);

			if (!(layoutStructureItem instanceof ColumnLayoutStructureItem)) {
				continue;
			}

			ColumnLayoutStructureItem columnLayoutStructureItem =
				(ColumnLayoutStructureItem)layoutStructureItem;

			if (position > (defaultSizes.length - 1)) {
				position = 0;
			}

			int columnSize = defaultSizes[position++];

			if (Objects.equals(
					viewportSizeId, ViewportSize.DESKTOP.getViewportSizeId())) {

				columnLayoutStructureItem.setSize(columnSize);

				continue;
			}

			if (!updateEmpty &&
				Objects.equals(
					ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
					viewportSizeId)) {

				columnLayoutStructureItem.setViewportConfiguration(
					viewportSizeId, JSONUtil.put("size", 12));

				continue;
			}

			Map<String, JSONObject> columnViewportConfigurationJSONObjects =
				columnLayoutStructureItem.getViewportConfigurationJSONObjects();

			if (!columnViewportConfigurationJSONObjects.containsKey(
					viewportSizeId)) {

				continue;
			}

			JSONObject columnViewportConfigurationJSONObject =
				columnViewportConfigurationJSONObjects.get(viewportSizeId);

			if (!columnViewportConfigurationJSONObject.has("size") &&
				!updateEmpty) {

				continue;
			}

			if (columnViewportConfigurationJSONObject.has("size") &&
				!updateEmpty &&
				Objects.equals(
					ViewportSize.PORTRAIT_MOBILE.getViewportSizeId(),
					viewportSizeId)) {

				columnViewportConfigurationJSONObject.remove("size");

				continue;
			}

			columnViewportConfigurationJSONObject.put("size", columnSize);
		}
	}

	private void _updateFragmentEntryLinks(
		String itemId, boolean markForDeletion) {

		LayoutStructureItem layoutStructureItem = _layoutStructureItems.get(
			itemId);

		if (layoutStructureItem instanceof FragmentStyledLayoutStructureItem) {
			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			try {
				FragmentEntryLinkServiceUtil.updateDeleted(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId(),
					markForDeletion);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		for (String childrenItemId : layoutStructureItem.getChildrenItemIds()) {
			_updateFragmentEntryLinks(childrenItemId, markForDeletion);
		}
	}

	private void _updateLayoutStructure(
		LayoutStructureItem layoutStructureItem, int position) {

		_layoutStructureItems.put(
			layoutStructureItem.getItemId(), layoutStructureItem);

		if (Validator.isNull(layoutStructureItem.getParentItemId())) {
			return;
		}

		LayoutStructureItem parentLayoutStructureItem =
			_layoutStructureItems.get(layoutStructureItem.getParentItemId());

		if (position >= 0) {
			parentLayoutStructureItem.addChildrenItem(
				position, layoutStructureItem.getItemId());
		}
		else {
			parentLayoutStructureItem.addChildrenItem(
				layoutStructureItem.getItemId());
		}
	}

	private void _updateNumberOfColumns(
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem,
		String viewportSizeId, int numberOfColumns) {

		Map<String, JSONObject> rowViewportConfigurationJSONObjects =
			rowStyledLayoutStructureItem.getViewportConfigurationJSONObjects();

		JSONObject viewportConfigurationJSONObject =
			rowViewportConfigurationJSONObjects.getOrDefault(
				viewportSizeId, JSONFactoryUtil.createJSONObject());

		viewportConfigurationJSONObject.put("numberOfColumns", numberOfColumns);

		if (Objects.equals(
				ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
				viewportSizeId)) {

			viewportConfigurationJSONObject.put("modulesPerRow", 1);
		}
		else if (Objects.equals(
					ViewportSize.PORTRAIT_MOBILE.getViewportSizeId(),
					viewportSizeId) &&
				 viewportConfigurationJSONObject.has("modulesPerRow")) {

			viewportConfigurationJSONObject.remove("modulesPerRow");
		}
		else if (viewportConfigurationJSONObject.has("modulesPerRow")) {
			viewportConfigurationJSONObject.put(
				"modulesPerRow", numberOfColumns);
		}

		_updateColumnSizes(
			rowStyledLayoutStructureItem, viewportSizeId, numberOfColumns,
			false);
	}

	private static final int _MAX_COLUMNS = 12;

	private static final int[][][] _MODULE_SIZES = {
		{{12}}, {{12}}, {{12}, {6, 6}}, {{12}, {6, 6, 4, 4, 4}},
		{{12}, {6, 6}, {4, 4, 4}}, {}, {}, {}, {}, {},
		{{12}, {6, 6}, {4, 4, 4}, {}, {}, {2, 2, 2, 2, 2, 2}}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructure.class);

	private static final ViewportSize[] _viewportSizes = ViewportSize.values();

	private final List<CollectionStyledLayoutStructureItem>
		_collectionStyledLayoutStructureItems;
	private final Set<String> _deletedItemIds;
	private final Map<String, DeletedLayoutStructureItem>
		_deletedLayoutStructureItems;
	private final Set<String> _deletedPortletIds;
	private final List<FormStyledLayoutStructureItem>
		_formStyledLayoutStructureItems;
	private final Map<Long, LayoutStructureItem> _fragmentLayoutStructureItems;
	private final Map<String, LayoutStructureItem> _layoutStructureItems;
	private final List<LayoutStructureRule> _layoutStructureRules;
	private final Map<String, LayoutStructureRule> _layoutStructureRulesMap;
	private String _mainItemId;

}