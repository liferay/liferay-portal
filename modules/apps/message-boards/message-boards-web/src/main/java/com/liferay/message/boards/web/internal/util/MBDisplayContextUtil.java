/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.message.boards.display.context.MBAdminListDisplayContext;
import com.liferay.message.boards.display.context.MBDisplayContextFactory;
import com.liferay.message.boards.display.context.MBHomeDisplayContext;
import com.liferay.message.boards.display.context.MBListDisplayContext;
import com.liferay.message.boards.web.internal.display.context.DefaultMBAdminListDisplayContext;
import com.liferay.message.boards.web.internal.display.context.DefaultMBHomeDisplayContext;
import com.liferay.message.boards.web.internal.display.context.DefaultMBListDisplayContext;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Iván Zaera
 */
public class MBDisplayContextUtil {

	public static MBAdminListDisplayContext getMBAdminListDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long categoryId) {

		MBAdminListDisplayContext mbAdminListDisplayContext =
			new DefaultMBAdminListDisplayContext(
				httpServletRequest, httpServletResponse, categoryId);

		for (MBDisplayContextFactory mbDisplayContextFactory :
				_serviceTrackerList) {

			mbAdminListDisplayContext =
				mbDisplayContextFactory.getMBAdminListDisplayContext(
					mbAdminListDisplayContext, httpServletRequest,
					httpServletResponse, categoryId);
		}

		return mbAdminListDisplayContext;
	}

	public static MBHomeDisplayContext getMBHomeDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		MBHomeDisplayContext mbHomeDisplayContext =
			new DefaultMBHomeDisplayContext(
				httpServletRequest, httpServletResponse);

		for (MBDisplayContextFactory mbDisplayContextFactory :
				_serviceTrackerList) {

			mbHomeDisplayContext =
				mbDisplayContextFactory.getMBHomeDisplayContext(
					mbHomeDisplayContext, httpServletRequest,
					httpServletResponse);
		}

		return mbHomeDisplayContext;
	}

	public static MBListDisplayContext getMBListDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long categoryId,
		String mvcRenderCommandName) {

		MBListDisplayContext mbListDisplayContext =
			new DefaultMBListDisplayContext(
				httpServletRequest, httpServletResponse, categoryId,
				mvcRenderCommandName);

		for (MBDisplayContextFactory mbDisplayContextFactory :
				_serviceTrackerList) {

			mbListDisplayContext =
				mbDisplayContextFactory.getMBListDisplayContext(
					mbListDisplayContext, httpServletRequest,
					httpServletResponse, categoryId);
		}

		return mbListDisplayContext;
	}

	private static final ServiceTrackerList<MBDisplayContextFactory>
		_serviceTrackerList;

	static {
		Bundle bundle = FrameworkUtil.getBundle(MBDisplayContextUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, MBDisplayContextFactory.class);
	}

}