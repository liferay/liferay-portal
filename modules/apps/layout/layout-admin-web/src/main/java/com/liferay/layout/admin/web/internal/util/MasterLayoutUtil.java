/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

import java.util.Locale;

/**
 * @author Georgel Pop
 */
public class MasterLayoutUtil {

	public static String getMasterLayoutName(
		long groupId, LayoutPageTemplateEntry masterLayoutPageTemplateEntry,
		Locale locale) {

		Group designLibraryGroup = _getDesignLibraryGroup(
			groupId, masterLayoutPageTemplateEntry);

		if (designLibraryGroup == null) {
			return masterLayoutPageTemplateEntry.getName();
		}

		try {
			return LanguageUtil.format(
				locale, "x-from-x-design-library",
				new String[] {
					masterLayoutPageTemplateEntry.getName(),
					designLibraryGroup.getDescriptiveName(locale)
				},
				false);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return masterLayoutPageTemplateEntry.getName();
		}
	}

	public static String getMasterLayoutPageTemplateEntryScopeERC(
		long groupId, Layout layout) {

		long masterLayoutPlid = layout.getMasterLayoutPlid();

		if (masterLayoutPlid <= 0) {
			return StringPool.BLANK;
		}

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(masterLayoutPlid);

		if (masterLayoutPageTemplateEntry == null) {
			return StringPool.BLANK;
		}

		return getMasterLayoutPageTemplateEntryScopeERC(
			groupId, masterLayoutPageTemplateEntry);
	}

	public static String getMasterLayoutPageTemplateEntryScopeERC(
		long groupId, LayoutPageTemplateEntry masterLayoutPageTemplateEntry) {

		Group designLibraryGroup = _getDesignLibraryGroup(
			groupId, masterLayoutPageTemplateEntry);

		if (designLibraryGroup == null) {
			return StringPool.BLANK;
		}

		return designLibraryGroup.getExternalReferenceCode();
	}

	private static Group _getDesignLibraryGroup(
		long groupId, LayoutPageTemplateEntry masterLayoutPageTemplateEntry) {

		if ((masterLayoutPageTemplateEntry.getGroupId() == 0) ||
			(masterLayoutPageTemplateEntry.getGroupId() == groupId)) {

			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			masterLayoutPageTemplateEntry.getGroupId());

		if (!DesignLibraryUtil.isDesignLibraryScope(group)) {
			return null;
		}

		return group;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MasterLayoutUtil.class);

}