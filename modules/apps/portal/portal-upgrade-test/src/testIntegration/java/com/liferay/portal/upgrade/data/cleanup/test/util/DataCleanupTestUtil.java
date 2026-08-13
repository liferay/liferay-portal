/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.service.SystemEventLocalServiceUtil;

import java.util.List;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DataCleanupTestUtil {

	public static SafeCloseable getClassNamesSavepointSafeCloseable() {
		List<ClassName> classNames = ClassNameLocalServiceUtil.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return () -> {
			for (ClassName className :
					ClassNameLocalServiceUtil.getClassNames(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				if (!classNames.contains(className)) {
					ClassNameLocalServiceUtil.deleteClassName(className);
				}
			}
		};
	}

	public static SafeCloseable getResourceActionsSavepointSafeCloseable() {
		List<ResourceAction> resourceActions =
			ResourceActionLocalServiceUtil.getResourceActions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return () -> {
			for (ResourceAction resourceAction :
					ResourceActionLocalServiceUtil.getResourceActions(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				if (!resourceActions.contains(resourceAction)) {
					ResourceActionLocalServiceUtil.deleteResourceAction(
						resourceAction);
				}
			}
		};
	}

	public static SafeCloseable getSystemEventsSavepointSafeCloseable() {
		List<SystemEvent> systemEvents =
			SystemEventLocalServiceUtil.getSystemEvents(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return () -> {
			for (SystemEvent systemEvent :
					SystemEventLocalServiceUtil.getSystemEvents(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				if (!systemEvents.contains(systemEvent)) {
					SystemEventLocalServiceUtil.deleteSystemEvent(systemEvent);
				}
			}
		};
	}

}