/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.kernel;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.framework.BundleContext;

/**
 * @author Eudaldo Alonso
 */
public class LayoutUtilityPageEntryViewRendererRegistryUtil {

	public static LayoutUtilityPageEntryViewRenderer
		getLayoutUtilityPageEntryViewRenderer(String type) {

		LayoutUtilityPageEntryViewRenderer layoutUtilityPageEntryViewRenderer =
			_layoutUtilityPageEntryViewRenderersServiceTrackerMap.getService(
				type);

		if (layoutUtilityPageEntryViewRenderer.isEnabled()) {
			return layoutUtilityPageEntryViewRenderer;
		}

		return null;
	}

	public static Collection<LayoutUtilityPageEntryViewRenderer>
		getLayoutUtilityPageEntryViewRenderers() {

		return ListUtil.filter(
			new ArrayList<>(
				_layoutUtilityPageEntryViewRenderersServiceTrackerMap.values()),
			LayoutUtilityPageEntryViewRenderer::isEnabled);
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTrackerMap
		<String, LayoutUtilityPageEntryViewRenderer>
			_layoutUtilityPageEntryViewRenderersServiceTrackerMap =
				ServiceTrackerMapFactory.openSingleValueMap(
					_bundleContext, LayoutUtilityPageEntryViewRenderer.class,
					"utility.page.type");

}