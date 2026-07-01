/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sites.kernel.util;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public interface Sites {

	public static final String ANALYTICS_PREFIX = "analytics_";

	public static final int CONTENT_SHARING_WITH_CHILDREN_DEFAULT_VALUE = -1;

	public static final int CONTENT_SHARING_WITH_CHILDREN_DISABLED = 0;

	public static final int CONTENT_SHARING_WITH_CHILDREN_DISABLED_BY_DEFAULT =
		1;

	public static final int CONTENT_SHARING_WITH_CHILDREN_ENABLED = 3;

	public static final int CONTENT_SHARING_WITH_CHILDREN_ENABLED_BY_DEFAULT =
		2;

	public static final String LAST_MERGE_TIME = "last-merge-time";

	public static final String LAYOUT_UPDATEABLE = "layoutUpdateable";

	public static final String MERGE_FAIL_COUNT = "merge-fail-count";

	public static final String SHOW_SITE_NAME = "showSiteName";

	public void applyLayoutPrototype(
			LayoutPrototype layoutPrototype, Layout targetLayout,
			boolean linkEnabled)
		throws Exception;

	public default void copyExpandoBridgeAttributes(
		Layout sourceLayout, Layout targetLayout) {

		ExpandoBridge sourceExpandoBridge =
			ExpandoBridgeFactoryUtil.getExpandoBridge(
				sourceLayout.getCompanyId(), Layout.class.getName(),
				sourceLayout.getPlid());

		ExpandoBridge targetExpandoBridge =
			ExpandoBridgeFactoryUtil.getExpandoBridge(
				targetLayout.getCompanyId(), Layout.class.getName(),
				targetLayout.getPlid());

		Map<String, Serializable> attributes =
			sourceExpandoBridge.getAttributes();

		for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
			targetExpandoBridge.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	public void copyPortletPermissions(Layout targetLayout, Layout sourceLayout)
		throws Exception;

	public void copyPortletSetups(Layout sourceLayout, Layout targetLayout)
		throws Exception;

	public boolean isLayoutSetMergeable(LayoutSet layoutSet)
		throws PortalException;

	public void mergeLayoutPrototypeLayout(Layout layout) throws Exception;

	public void mergeLayoutSetPrototypeLayouts(LayoutSet layoutSet)
		throws Exception;

	public void mergeLayoutSetPrototypeLayouts(
			LayoutSetPrototype layoutSetPrototype, long userId)
		throws Exception;

	public void updateLayoutSetPrototypesLinks(
			Group group, boolean mergeLayoutSetPrototype,
			long publicLayoutSetPrototypeId, long privateLayoutSetPrototypeId,
			boolean publicLayoutSetPrototypeLinkEnabled,
			boolean privateLayoutSetPrototypeLinkEnabled)
		throws Exception;

	public void updateLayoutSetPrototypesLinks(
			Group group, long publicLayoutSetPrototypeId,
			long privateLayoutSetPrototypeId,
			boolean publicLayoutSetPrototypeLinkEnabled,
			boolean privateLayoutSetPrototypeLinkEnabled)
		throws Exception;

}