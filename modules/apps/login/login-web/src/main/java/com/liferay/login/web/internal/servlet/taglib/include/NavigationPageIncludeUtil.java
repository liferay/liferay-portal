/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.servlet.taglib.include;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.taglib.include.PageInclude;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
public class NavigationPageIncludeUtil {

	public static void includePost(PageContext pageContext)
		throws JspException {

		_include(pageContext, "post");
	}

	public static void includePre(PageContext pageContext) throws JspException {
		_include(pageContext, "pre");
	}

	private static void _include(PageContext pageContext, String position)
		throws JspException {

		List<PageInclude> pageIncludes = _serviceTrackerMap.getService(
			position);

		if (pageIncludes == null) {
			return;
		}

		for (PageInclude pageInclude : pageIncludes) {
			pageInclude.include(pageContext);
		}
	}

	private static final ServiceTrackerMap<String, List<PageInclude>>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			NavigationPageIncludeUtil.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundle.getBundleContext(), PageInclude.class,
			"login.web.navigation.position");
	}

}