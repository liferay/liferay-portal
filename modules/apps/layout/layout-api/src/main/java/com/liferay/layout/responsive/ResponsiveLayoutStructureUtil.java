/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.responsive;

import com.liferay.layout.util.constants.LayoutStructureConstants;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public class ResponsiveLayoutStructureUtil {

	public static String getColumnCssClass(
		CollectionStyledLayoutStructureItem collectionStyledLayoutStructureItem,
		int index) {

		StringBundler sb = new StringBundler(_viewportSizes.length * 3);

		int size = LayoutStructureConstants.COLUMN_SIZES
			[collectionStyledLayoutStructureItem.getNumberOfColumns() - 1]
			[index];

		sb.append("col-lg-");
		sb.append(size);

		Map<String, JSONObject> viewportConfigurationJSONObjects =
			collectionStyledLayoutStructureItem.
				getViewportConfigurationJSONObjects();

		for (ViewportSize viewportSize : _viewportSizes) {
			if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
				continue;
			}

			int numberOfColumns = GetterUtil.getInteger(
				getResponsivePropertyValue(
					viewportSize, viewportConfigurationJSONObjects,
					"numberOfColumns", size));

			int columnSize =
				LayoutStructureConstants.COLUMN_SIZES[numberOfColumns - 1]
					[index % numberOfColumns];

			sb.append(" col");
			sb.append(viewportSize.getCssClassPrefix());
			sb.append(columnSize);
		}

		if (Objects.equals(
				collectionStyledLayoutStructureItem.getVerticalAlignment(),
				"middle")) {

			sb.append(" d-flex flex-column ");
		}

		return sb.toString();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getColumnCssClass(ColumnLayoutStructureItem, RowStyledLayoutStructureItem)}
	 */
	@Deprecated
	public static String getColumnCssClass(
		ColumnLayoutStructureItem columnLayoutStructureItem) {

		return getColumnCssClass(columnLayoutStructureItem, null);
	}

	public static String getColumnCssClass(
		ColumnLayoutStructureItem columnLayoutStructureItem,
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem) {

		StringBundler sb = new StringBundler(_viewportSizes.length * 3);

		sb.append("col-lg-");
		sb.append(columnLayoutStructureItem.getSize());

		Map<String, JSONObject> columnViewportConfigurationJSONObjects =
			columnLayoutStructureItem.getViewportConfigurationJSONObjects();

		for (ViewportSize viewportSize : _viewportSizes) {
			if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
				continue;
			}

			int columnSize = GetterUtil.getInteger(
				getResponsivePropertyValue(
					viewportSize, columnViewportConfigurationJSONObjects,
					"size", columnLayoutStructureItem.getSize()));

			sb.append(" col");
			sb.append(viewportSize.getCssClassPrefix());
			sb.append(columnSize);
		}

		if ((rowStyledLayoutStructureItem != null) &&
			Objects.equals(
				rowStyledLayoutStructureItem.getVerticalAlignment(),
				"middle")) {

			sb.append(" d-flex flex-column ");
		}

		return sb.toString();
	}

	public static Object getResponsivePropertyValue(
		ViewportSize currentViewportSize,
		Map<String, JSONObject> viewportConfigurationJSONObjects,
		String propertyName, Object defaultValue) {

		JSONObject viewportConfigurationJSONObject =
			viewportConfigurationJSONObjects.getOrDefault(
				currentViewportSize.getViewportSizeId(),
				JSONFactoryUtil.createJSONObject());

		if (viewportConfigurationJSONObject.has(propertyName)) {
			return viewportConfigurationJSONObject.get(propertyName);
		}

		for (ViewportSize viewportSize : _sortedViewportSizes) {
			if (viewportSize.getOrder() >= currentViewportSize.getOrder()) {
				continue;
			}

			viewportConfigurationJSONObject =
				viewportConfigurationJSONObjects.getOrDefault(
					viewportSize.getViewportSizeId(),
					JSONFactoryUtil.createJSONObject());

			if (viewportConfigurationJSONObject.has(propertyName)) {
				return viewportConfigurationJSONObject.get(propertyName);
			}
		}

		return defaultValue;
	}

	public static String getRowCssClass(
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem) {

		StringBundler sb = new StringBundler((_viewportSizes.length * 6) - 2);

		sb.append("align-items-lg-");
		sb.append(
			_getVerticalAlignmentCssClass(
				rowStyledLayoutStructureItem.getVerticalAlignment()));

		Map<String, JSONObject> rowViewportConfigurationJSONObjects =
			rowStyledLayoutStructureItem.getViewportConfigurationJSONObjects();

		for (ViewportSize viewportSize : _viewportSizes) {
			if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
				continue;
			}

			String verticalAlignment = GetterUtil.getString(
				getResponsivePropertyValue(
					viewportSize, rowViewportConfigurationJSONObjects,
					"verticalAlignment",
					rowStyledLayoutStructureItem.getVerticalAlignment()));

			sb.append(" align-items");
			sb.append(viewportSize.getCssClassPrefix());
			sb.append(_getVerticalAlignmentCssClass(verticalAlignment));
		}

		if (rowStyledLayoutStructureItem.isReverseOrder() &&
			(rowStyledLayoutStructureItem.getModulesPerRow() > 1)) {

			sb.append(" flex-lg-row-reverse");
		}
		else if (rowStyledLayoutStructureItem.isReverseOrder() &&
				 (rowStyledLayoutStructureItem.getModulesPerRow() == 1)) {

			sb.append(" flex-lg-column-reverse");
		}
		else {
			sb.append(" flex-lg-row");
		}

		for (ViewportSize viewportSize : _viewportSizes) {
			if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
				continue;
			}

			boolean reverseOrder = GetterUtil.getBoolean(
				getResponsivePropertyValue(
					viewportSize, rowViewportConfigurationJSONObjects,
					"reverseOrder",
					rowStyledLayoutStructureItem.isReverseOrder()));

			int modulesPerRow = GetterUtil.getInteger(
				getResponsivePropertyValue(
					viewportSize, rowViewportConfigurationJSONObjects,
					"modulesPerRow",
					rowStyledLayoutStructureItem.getModulesPerRow()));

			sb.append(" flex");
			sb.append(viewportSize.getCssClassPrefix());

			if (reverseOrder) {
				if (modulesPerRow > 1) {
					sb.append("row-reverse");
				}
				else if (modulesPerRow == 1) {
					sb.append("column-reverse");
				}
			}
			else {
				sb.append("row");
			}
		}

		if (!rowStyledLayoutStructureItem.isGutters()) {
			sb.append(" no-gutters");
		}

		return sb.toString();
	}

	private static String _getVerticalAlignmentCssClass(
		String verticalAlignment) {

		if (Objects.equals(verticalAlignment, "bottom")) {
			return "end";
		}
		else if (Objects.equals(verticalAlignment, "middle")) {
			return "center";
		}

		return "start";
	}

	private static final ViewportSize[] _sortedViewportSizes =
		ViewportSize.values();
	private static final ViewportSize[] _viewportSizes = ViewportSize.values();

	static {
		Comparator<ViewportSize> comparator = Comparator.comparingInt(
			ViewportSize::getOrder);

		Arrays.sort(_sortedViewportSizes, comparator.reversed());
	}

}