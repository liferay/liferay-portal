/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * @author Lance Ji
 */
public interface PortletRegistry {

	public List<String> getFragmentEntryLinkPortletIds(
		Document document, FragmentEntryLink fragmentEntryLink);

	public default List<String> getFragmentEntryLinkPortletIds(
		FragmentEntryLink fragmentEntryLink) {

		return Collections.emptyList();
	}

	public List<String> getPortletAliases();

	public String getPortletName(String alias);

	public void registerAlias(String alias, String portletName);

	public void unregisterAlias(String alias);

	public void writePortletPaths(
			FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException;

}