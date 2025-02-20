/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest) {

		this.cmsSiteInitializerConfiguration = cmsSiteInitializerConfiguration;
		this.httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		String[] entryClassNames = getEntryClassNames();

		if (entryClassNames.length == 0) {
			return "/o/search/v1.0/search?emptySearch=true" +
				"&nestedFields=embedded";
		}

		StringBundler sb = new StringBundler(3);

		sb.append("/o/search/v1.0/search?emptySearch=true&entryClassNames=");
		sb.append(ArrayUtil.toString(entryClassNames, StringPool.BLANK));
		sb.append("&nestedFields=embedded");

		return sb.toString();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return new ArrayList<>();
	}

	public CreationMenu getCreationMenu() {
		return new CreationMenu();
	}

	public Map<String, Object> getEmptyState() {
		return Collections.emptyMap();
	}

	public String[] getEntryClassNames() {
		return new String[0];
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return new ArrayList<>();
	}

	protected final CMSSiteInitializerConfiguration
		cmsSiteInitializerConfiguration;
	protected final HttpServletRequest httpServletRequest;

}