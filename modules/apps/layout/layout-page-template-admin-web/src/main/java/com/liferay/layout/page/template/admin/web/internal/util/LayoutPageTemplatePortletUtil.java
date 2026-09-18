/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.util;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionCreateDateComparator;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionNameComparator;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateEntryCreateDateComparator;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateEntryNameComparator;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class LayoutPageTemplatePortletUtil {

	public static LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollection(
			HttpServletRequest httpServletRequest, long groupId) {

		long layoutPageTemplateCollectionId = ParamUtil.getLong(
			httpServletRequest, "layoutPageTemplateCollectionId");

		if (layoutPageTemplateCollectionId > 0) {
			LayoutPageTemplateCollection layoutPageTemplateCollection =
				LayoutPageTemplateCollectionLocalServiceUtil.
					fetchLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId);

			if ((layoutPageTemplateCollection != null) &&
				(layoutPageTemplateCollection.getGroupId() == groupId)) {

				return layoutPageTemplateCollection;
			}

			return null;
		}

		String externalReferenceCode = ParamUtil.getString(
			httpServletRequest,
			"layoutPageTemplateCollectionExternalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return LayoutPageTemplateCollectionLocalServiceUtil.
			fetchLayoutPageTemplateCollectionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	public static OrderByComparator<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollectionOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<LayoutPageTemplateCollection> orderByComparator =
			null;

		if (orderByCol.equals("create-date")) {
			orderByComparator =
				LayoutPageTemplateCollectionCreateDateComparator.getInstance(
					orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator =
				LayoutPageTemplateCollectionNameComparator.getInstance(
					orderByAsc);
		}

		return orderByComparator;
	}

	public static OrderByComparator<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntryOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<LayoutPageTemplateEntry> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator =
				LayoutPageTemplateEntryCreateDateComparator.getInstance(
					orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator =
				LayoutPageTemplateEntryNameComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

}