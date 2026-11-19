/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_3_0.util;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Rubén Pulido
 */
public class LayoutDataConverter {

	public static String convert(String data) {
		if (Validator.isNull(data)) {
			return StringPool.BLANK;
		}

		JSONObject inputDataJSONObject = null;

		try {
			inputDataJSONObject = JSONFactoryUtil.createJSONObject(data);
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}

		JSONArray structureJSONArray = inputDataJSONObject.getJSONArray(
			"structure");

		if (structureJSONArray == null) {
			return data;
		}

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		JSONArray nonindexableFragmentEntryLinkIdsJSONArray =
			inputDataJSONObject.getJSONArray(
				"nonIndexableFragmentEntryLinkIds");

		if (nonindexableFragmentEntryLinkIdsJSONArray == null) {
			nonindexableFragmentEntryLinkIdsJSONArray =
				JSONFactoryUtil.createJSONArray();
		}

		for (int i = 0; i < structureJSONArray.length(); i++) {
			JSONObject inputRowJSONObject = structureJSONArray.getJSONObject(i);

			JSONArray columnsJSONArray = inputRowJSONObject.getJSONArray(
				"columns");

			if (inputRowJSONObject.getInt("type") ==
					FragmentConstants.TYPE_COMPONENT) {

				ContainerStyledLayoutStructureItem
					outerContainerStyledLayoutStructureItem =
						(ContainerStyledLayoutStructureItem)
							layoutStructure.
								addContainerStyledLayoutStructureItem(
									rootLayoutStructureItem.getItemId(), i);

				outerContainerStyledLayoutStructureItem.setWidthType("fluid");

				ContainerStyledLayoutStructureItem
					innerContainerStyledLayoutStructureItem =
						(ContainerStyledLayoutStructureItem)
							layoutStructure.
								addContainerStyledLayoutStructureItem(
									outerContainerStyledLayoutStructureItem.
										getItemId(),
									0);

				JSONObject inputRowConfigJSONObject =
					inputRowJSONObject.getJSONObject("config");

				if (inputRowConfigJSONObject != null) {
					outerContainerStyledLayoutStructureItem.updateItemConfig(
						JSONUtil.put(
							"backgroundColorCssClass",
							inputRowConfigJSONObject.getString(
								"backgroundColorCssClass")
						).put(
							"styles",
							JSONUtil.put(
								"backgroundImage",
								_getBackgroundImageJSONObject(
									inputRowConfigJSONObject))
						));

					if (inputRowConfigJSONObject.has("containerType")) {
						innerContainerStyledLayoutStructureItem.setWidthType(
							inputRowConfigJSONObject.getString(
								"containerType", "fixed"));
					}
					else {
						innerContainerStyledLayoutStructureItem.setWidthType(
							inputRowConfigJSONObject.getString(
								"widthType", "fixed"));
					}

					innerContainerStyledLayoutStructureItem.updateItemConfig(
						JSONUtil.put(
							"styles",
							JSONUtil.put(
								"paddingBottom",
								inputRowConfigJSONObject.getInt(
									"paddingVertical", 0)
							).put(
								"paddingLeft",
								inputRowConfigJSONObject.getInt(
									"paddingHorizontal", 0)
							).put(
								"paddingRight",
								inputRowConfigJSONObject.getInt(
									"paddingHorizontal", 0)
							).put(
								"paddingTop",
								inputRowConfigJSONObject.getInt(
									"paddingVertical", 0)
							)));
				}

				RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
					(RowStyledLayoutStructureItem)
						layoutStructure.addRowStyledLayoutStructureItem(
							innerContainerStyledLayoutStructureItem.getItemId(),
							0, columnsJSONArray.length());

				if (inputRowConfigJSONObject != null) {
					boolean columnSpacing = inputRowConfigJSONObject.getBoolean(
						"columnSpacing", true);

					rowStyledLayoutStructureItem.setGutters(columnSpacing);

					boolean nonindexable = inputRowConfigJSONObject.getBoolean(
						"nonIndexable", false);

					rowStyledLayoutStructureItem.setIndexed(!nonindexable);
				}

				for (int j = 0; j < columnsJSONArray.length(); j++) {
					JSONObject inputColumnJSONObject =
						columnsJSONArray.getJSONObject(j);

					ColumnLayoutStructureItem columnLayoutStructureItem =
						(ColumnLayoutStructureItem)
							layoutStructure.addColumnLayoutStructureItem(
								rowStyledLayoutStructureItem.getItemId(), j);

					columnLayoutStructureItem.setSize(
						inputColumnJSONObject.getInt("size"));

					JSONArray fragmentEntryLinksJSONArray =
						inputColumnJSONObject.getJSONArray(
							"fragmentEntryLinkIds");

					for (int k = 0; k < fragmentEntryLinksJSONArray.length();
						 k++) {

						String fragmentEntryLinkId =
							fragmentEntryLinksJSONArray.getString(k);

						boolean indexed = !JSONUtil.hasValue(
							nonindexableFragmentEntryLinkIdsJSONArray,
							fragmentEntryLinkId);

						_addFragmentEntryLink(
							fragmentEntryLinkId, inputDataJSONObject, indexed,
							layoutStructure,
							columnLayoutStructureItem.getItemId(), k);
					}
				}
			}
			else {
				JSONObject columnJSONObject = columnsJSONArray.getJSONObject(0);

				JSONArray fragmentEntryLinkIdsJSONArray =
					columnJSONObject.getJSONArray("fragmentEntryLinkIds");

				String fragmentEntryLinkId =
					fragmentEntryLinkIdsJSONArray.getString(0);

				boolean indexed = !JSONUtil.hasValue(
					nonindexableFragmentEntryLinkIdsJSONArray,
					fragmentEntryLinkId);

				_addFragmentEntryLink(
					fragmentEntryLinkId, inputDataJSONObject, indexed,
					layoutStructure, rootLayoutStructureItem.getItemId(), i);
			}
		}

		JSONObject layoutStructureJSONObject = layoutStructure.toJSONObject();

		return layoutStructureJSONObject.toString();
	}

	private static void _addFragmentEntryLink(
		String fragmentEntryLinkId, JSONObject inputDataJSONObject,
		boolean indexed, LayoutStructure layoutStructure, String parentItemId,
		int position) {

		if (fragmentEntryLinkId.equals(
				LayoutDataItemTypeConstants.TYPE_DROP_ZONE)) {

			DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
				(DropZoneLayoutStructureItem)
					layoutStructure.addDropZoneLayoutStructureItem(
						parentItemId, position);

			dropZoneLayoutStructureItem.setAllowNewFragmentEntries(
				inputDataJSONObject.getBoolean(
					"allowNewFragmentEntries", true));

			JSONArray fragmentEntryKeysJSONArray =
				inputDataJSONObject.getJSONArray("fragmentEntryKeys");

			if (fragmentEntryKeysJSONArray != null) {
				dropZoneLayoutStructureItem.setFragmentEntriesJSONArray(
					JSONUtil.toJSONArray(
						JSONUtil.toStringList(fragmentEntryKeysJSONArray),
						fragmentEntryKey -> JSONUtil.put(
							"fragmentEntryRendererKey", fragmentEntryKey),
						_log));
			}

			return;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.addFragmentStyledLayoutStructureItem(
					GetterUtil.getLong(fragmentEntryLinkId), parentItemId,
					position);

		fragmentStyledLayoutStructureItem.setIndexed(indexed);
	}

	private static JSONObject _getBackgroundImageJSONObject(
		JSONObject inputRowConfigJSONObject) {

		if (inputRowConfigJSONObject.isNull("backgroundImage")) {
			return JSONFactoryUtil.createJSONObject();
		}

		Object backgroundImage = inputRowConfigJSONObject.get(
			"backgroundImage");

		if (backgroundImage instanceof JSONObject) {
			return (JSONObject)backgroundImage;
		}

		return JSONUtil.put("url", backgroundImage);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutDataConverter.class);

}