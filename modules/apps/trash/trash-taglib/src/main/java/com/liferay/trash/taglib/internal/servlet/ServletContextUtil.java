/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.taglib.internal.servlet;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.trash.TrashHelper;

import jakarta.servlet.ServletContext;

/**
 * @author Michael Bradford
 */
public class ServletContextUtil {

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	public static TrashHelper getTrashHelper() {
		return _trashHelperSnapshot.get();
	}

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.trash.taglib)");
	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(ServletContextUtil.class, TrashHelper.class);

}