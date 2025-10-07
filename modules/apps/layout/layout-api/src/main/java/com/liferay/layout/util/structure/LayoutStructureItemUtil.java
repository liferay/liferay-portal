/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutStructureItemUtil {

	public static LayoutStructureItem create(
		String itemType, String parentItemId) {

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_COLLECTION)) {

			return new CollectionStyledLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_COLLECTION_ITEM)) {

			return new CollectionItemLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(itemType, LayoutDataItemTypeConstants.TYPE_COLUMN)) {
			return new ColumnLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_CONTAINER)) {

			return new ContainerStyledLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_DROP_ZONE)) {

			return new DropZoneLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(itemType, LayoutDataItemTypeConstants.TYPE_FORM)) {
			return new FormStyledLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_FORM_RELATIONSHIP)) {

			return new FormRelationshipStyledLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_FORM_STEP)) {

			return new FormStepLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType,
				LayoutDataItemTypeConstants.TYPE_FORM_STEP_CONTAINER)) {

			return new FormStepContainerStyledLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_FRAGMENT)) {

			return new FragmentStyledLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(
				itemType,
				LayoutDataItemTypeConstants.TYPE_FRAGMENT_DROP_ZONE)) {

			return new FragmentDropZoneLayoutStructureItem(parentItemId);
		}

		if (Objects.equals(itemType, LayoutDataItemTypeConstants.TYPE_ROOT)) {
			return new RootLayoutStructureItem();
		}

		if (Objects.equals(itemType, LayoutDataItemTypeConstants.TYPE_ROW)) {
			return new RowStyledLayoutStructureItem(parentItemId);
		}

		return null;
	}

	public static LayoutStructureItem getAncestor(
		String itemId, String itemType, LayoutStructure layoutStructure) {

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		if (layoutStructureItem == null) {
			return null;
		}

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem.getParentItemId());

		if (parentLayoutStructureItem == null) {
			return null;
		}

		if (Objects.equals(parentLayoutStructureItem.getItemType(), itemType)) {
			return parentLayoutStructureItem;
		}

		if (Objects.equals(
				parentLayoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_ROOT)) {

			return null;
		}

		return getAncestor(
			parentLayoutStructureItem.getItemId(), itemType, layoutStructure);
	}

	public static List<String> getChildrenItemIds(
		String itemId, LayoutStructure layoutStructure) {

		List<String> childrenItemIds = new ArrayList<>();

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		if (layoutStructureItem == null) {
			return childrenItemIds;
		}

		for (String childItemId : layoutStructureItem.getChildrenItemIds()) {
			childrenItemIds.add(childItemId);

			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childItemId);

			childrenItemIds.addAll(
				getChildrenItemIds(
					childLayoutStructureItem.getItemId(), layoutStructure));
		}

		return childrenItemIds;
	}

	public static boolean hasAncestor(
		String itemId, String itemType, LayoutStructure layoutStructure) {

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem.getParentItemId());

		if (parentLayoutStructureItem == null) {
			return false;
		}

		if (Objects.equals(parentLayoutStructureItem.getItemType(), itemType)) {
			return true;
		}

		if (Objects.equals(
				parentLayoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_ROOT)) {

			return false;
		}

		return hasAncestor(
			parentLayoutStructureItem.getItemId(), itemType, layoutStructure);
	}

}