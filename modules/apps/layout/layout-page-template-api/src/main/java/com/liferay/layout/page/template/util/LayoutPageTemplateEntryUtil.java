/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.util;

import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistryUtil;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Mikel Lorza
 * @author Javier Moral
 */
public class LayoutPageTemplateEntryUtil {

	public static LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long classNameId, long classTypeId, long[] designLibraryGroupIds,
		long groupId) {

		String classTypeKey = getClassTypeKey(
			classNameId, classTypeId, groupId);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					groupId, classNameId, classTypeKey);

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry;
		}

		for (long designLibraryGroupId : designLibraryGroupIds) {
			layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchDefaultLayoutPageTemplateEntry(
						designLibraryGroupId, classNameId, classTypeKey);

			if (layoutPageTemplateEntry != null) {
				return layoutPageTemplateEntry;
			}
		}

		return null;
	}

	public static long getClassTypeId(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return getClassTypeId(
			layoutPageTemplateEntry.getClassNameId(),
			layoutPageTemplateEntry.getClassTypeKey(),
			layoutPageTemplateEntry.getGroupId());
	}

	public static long getClassTypeId(
		long classNameId, String classTypeKey, long groupId) {

		if (classNameId == 0) {
			return 0;
		}

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			InfoItemServiceRegistryUtil.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				PortalUtil.fetchClassName(classNameId));

		if (Validator.isNull(classTypeKey) &&
			(infoItemFormVariationsProvider == null)) {

			return 0;
		}

		if (Validator.isNull(classTypeKey) ||
			(infoItemFormVariationsProvider == null)) {

			return -1;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.
				getInfoItemFormVariationByExternalReferenceCode(
					classTypeKey, groupId);

		if (infoItemFormVariation == null) {
			return -1;
		}

		return GetterUtil.getLong(infoItemFormVariation.getKey());
	}

	public static String getClassTypeKey(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return getClassTypeKey(
			layoutPageTemplateEntry.getClassNameId(),
			layoutPageTemplateEntry.getClassTypeId(),
			layoutPageTemplateEntry.getGroupId());
	}

	public static String getClassTypeKey(
		long classNameId, long classTypeId, long groupId) {

		if ((classNameId == 0) || (classTypeId < 0)) {
			return null;
		}

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			InfoItemServiceRegistryUtil.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				PortalUtil.fetchClassName(classNameId));

		if (infoItemFormVariationsProvider == null) {
			return null;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				groupId, String.valueOf(classTypeId));

		if (infoItemFormVariation == null) {
			return null;
		}

		return infoItemFormVariation.getExternalReferenceCode();
	}

}