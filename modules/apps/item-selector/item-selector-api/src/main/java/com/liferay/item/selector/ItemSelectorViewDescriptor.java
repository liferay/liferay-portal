/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.portlet.RenderRequest;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Alejandro Tardín
 */
public interface ItemSelectorViewDescriptor<T> {

	public default String getDefaultDisplayStyle() {
		return "icon";
	}

	public default String[] getDisplayViews() {
		return new String[] {"descriptive", "icon", "list"};
	}

	public default List<LabelItem> getFilterLabelItems() {
		return null;
	}

	public default List<DropdownItem> getFilterNavigationDropdownItems() {
		return null;
	}

	public ItemDescriptor getItemDescriptor(T t);

	public ItemSelectorReturnType getItemSelectorReturnType();

	public default String getKeyProperty() {
		return "primaryKeyObj";
	}

	public default String[] getOrderByKeys() {
		return null;
	}

	public default ResultRowSplitter getResultRowSplitter() {
		return null;
	}

	public SearchContainer<T> getSearchContainer() throws PortalException;

	public default TableItemView getTableItemView(T t) {
		return null;
	}

	public default boolean isMultipleSelection() {
		return false;
	}

	public default boolean isShowBreadcrumb() {
		return true;
	}

	public default boolean isShowManagementToolbar() {
		return true;
	}

	public default boolean isShowSearch() {
		return false;
	}

	public interface ItemDescriptor {

		public default HorizontalCard getHorizontalCard(
			RenderRequest renderRequest, RowChecker rowChecker) {

			return null;
		}

		public String getIcon();

		public String getImageURL();

		public default Date getModifiedDate() {
			return null;
		}

		public String getPayload();

		public default Integer getStatus() {
			return null;
		}

		/**
		 * @deprecated As of Athanasius (7.3.x), replaced by {@link
		 *             #getSubtitle(Locale)}
		 */
		@Deprecated
		public default String getSubtitle() {
			return getSubtitle(LocaleUtil.getDefault());
		}

		public String getSubtitle(Locale locale);

		/**
		 * @deprecated As of Athanasius (7.3.x), replaced by {@link
		 *             #getTitle(Locale)}
		 */
		@Deprecated
		public default String getTitle() {
			return getTitle(LocaleUtil.getDefault());
		}

		public String getTitle(Locale locale);

		public default long getUserId() {
			return UserConstants.USER_ID_DEFAULT;
		}

		public default String getUserName() {
			return StringPool.BLANK;
		}

		public default VerticalCard getVerticalCard(
			RenderRequest renderRequest, RowChecker rowChecker) {

			return null;
		}

		public default boolean isCompact() {
			return false;
		}

	}

}