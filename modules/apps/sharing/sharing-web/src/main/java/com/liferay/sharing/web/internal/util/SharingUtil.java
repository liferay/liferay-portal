/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplay;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplayAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Sergio González
 */
public class SharingUtil {

	public static SharingEntryPermissionDisplayAction
		getSharingEntryPermissionDisplayActionKey(SharingEntry sharingEntry) {

		if (sharingEntry.hasSharingPermission(SharingEntryAction.UPDATE)) {
			return SharingEntryPermissionDisplayAction.UPDATE;
		}

		if (sharingEntry.hasSharingPermission(
				SharingEntryAction.ADD_DISCUSSION)) {

			return SharingEntryPermissionDisplayAction.COMMENTS;
		}

		if (sharingEntry.hasSharingPermission(SharingEntryAction.VIEW)) {
			return SharingEntryPermissionDisplayAction.VIEW;
		}

		return null;
	}

	public static List<SharingEntryPermissionDisplay>
		getSharingEntryPermissionDisplays(
			long classNameId, long classPK, long groupId, Locale locale,
			PermissionChecker permissionChecker, Portal portal,
			SharingPermission sharingPermission) {

		List<SharingEntryAction> sharingEntryActions = new ArrayList<>();

		for (SharingEntryAction sharingEntryAction :
				SharingEntryAction.values()) {

			try {
				if (sharingPermission.contains(
						permissionChecker, classNameId, classPK, groupId,
						Collections.singletonList(sharingEntryAction))) {

					sharingEntryActions.add(sharingEntryAction);
				}
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, SharingUtil.class);

		return SharingEntryPermissionDisplay.getSharingEntryPermissionDisplays(
			portal.getClassName(classNameId), sharingEntryActions,
			resourceBundle);
	}

	private static final Log _log = LogFactoryUtil.getLog(SharingUtil.class);

}