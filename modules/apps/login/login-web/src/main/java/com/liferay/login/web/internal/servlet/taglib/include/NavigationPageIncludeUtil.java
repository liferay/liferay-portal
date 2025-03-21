/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.servlet.taglib.include;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.taglib.include.PageInclude;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
public class NavigationPageIncludeUtil {

	public static void includePost(PageContext pageContext)
		throws JspException {

		for (PageInclude pageInclude : _postPageIncludes) {
			pageInclude.include(pageContext);
		}
	}

	public static void includePre(PageContext pageContext) throws JspException {
		for (PageInclude pageInclude : _prePageIncludes) {
			pageInclude.include(pageContext);
		}
	}

	private static final ServiceTrackerList<PageInclude> _postPageIncludes;
	private static final ServiceTrackerList<PageInclude> _prePageIncludes;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			NavigationPageIncludeUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_postPageIncludes = ServiceTrackerListFactory.open(
			bundleContext, PageInclude.class,
			"(login.web.navigation.position=post)");
		_prePageIncludes = ServiceTrackerListFactory.open(
			bundleContext, PageInclude.class,
			"(login.web.navigation.position=pre)");
	}

}