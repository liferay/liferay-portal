/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.navigation.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.navigation.web.internal.util.constants.WikiNavigationConstants;
import com.liferay.wiki.service.WikiPageServiceUtil;

import jakarta.portlet.PortletURL;

import java.io.Serializable;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thiago Moreira
 * @author Peter Shin
 */
public class MenuItem implements Serializable {

	public static List<MenuItem> fromWikiNode(
		long nodeId, int depth, PortletURL portletURL) {

		List<WikiPage> wikiPages = null;

		try {
			wikiPages = WikiPageServiceUtil.getNodePages(
				nodeId, WikiNavigationConstants.MAX_PAGES);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return new LinkedList<>();
		}

		return _fromWikiNode(wikiPages, 1, depth, portletURL);
	}

	public static List<MenuItem> fromWikiPage(
		WikiPage wikiPage, PortletURL portletURL) {

		return _fromWikiPage(wikiPage, portletURL);
	}

	public void addChild(MenuItem child) {
		_children.add(child);
	}

	public void addChildren(List<MenuItem> children) {
		_children.addAll(children);
	}

	public List<MenuItem> getChildren() {
		return _children;
	}

	public boolean getExternalURL() {
		return _externalURL;
	}

	public String getIcon() {
		return _icon;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getURL() {
		return _url;
	}

	public void setChildren(List<MenuItem> children) {
		_children = children;
	}

	public void setExternalURL(boolean externalURL) {
		_externalURL = externalURL;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setURL(String url) {
		_url = url;
	}

	private static List<MenuItem> _fromWikiNode(
		List<WikiPage> wikiPages, int curDepth, int depth,
		PortletURL portletURL) {

		List<MenuItem> menuItems = new LinkedList<>();

		for (WikiPage wikiPage : wikiPages) {
			if ((wikiPage.fetchParentPage() != null) && (curDepth == 1)) {
				continue;
			}

			String title = wikiPage.getTitle();

			WikiNode wikiNode = wikiPage.getNode();

			portletURL.setParameter(
				"nodeId", String.valueOf(wikiNode.getNodeId()));

			portletURL.setParameter("title", title);

			MenuItem menuItem = new MenuItem();

			menuItem.setIcon("wiki-page");
			menuItem.setId(String.valueOf(wikiPage.getPageId()));
			menuItem.setName(title);
			menuItem.setURL(portletURL.toString());

			if ((depth >= curDepth) ||
				(depth == WikiNavigationConstants.DEPTH_ALL)) {

				List<MenuItem> children = _fromWikiNode(
					wikiPage.getViewableChildPages(), curDepth + 1, depth,
					portletURL);

				menuItem.addChildren(children);
			}

			menuItems.add(menuItem);
		}

		return menuItems;
	}

	private static List<MenuItem> _fromWikiPage(
		WikiPage wikiPage, PortletURL portletURL) {

		List<MenuItem> menuItems = new LinkedList<>();

		Matcher matcher = _pattern.matcher(wikiPage.getContent());

		while (matcher.find()) {
			String title = GetterUtil.getString(matcher.group(1));

			MenuItem menuItem = new MenuItem();

			menuItem.setName(title);

			menuItems.add(menuItem);

			String s = matcher.group(2);

			if (s == null) {
				continue;
			}

			MenuItem childMenuItem = new MenuItem();

			int index = s.indexOf(StringPool.PIPE);

			String name = null;
			String url = null;

			if (index != -1) {
				name = s.substring(index + 1);
				url = s.substring(0, index);
			}
			else {
				name = s;
				url = s;
			}

			if (!url.startsWith(Http.HTTP)) {
				portletURL.setParameter("title", url);
				portletURL.setParameter(
					"nodeId", String.valueOf(wikiPage.getNodeId()));

				url = portletURL.toString();
			}
			else {
				childMenuItem.setExternalURL(true);
			}

			childMenuItem.setName(name);
			childMenuItem.setURL(url);

			menuItem.addChild(childMenuItem);
		}

		return menuItems;
	}

	private static final Log _log = LogFactoryUtil.getLog(MenuItem.class);

	private static final Pattern _pattern = Pattern.compile(
		"(?:(?:==\\s(.*?)\\s==)*(?:\\Q[[\\E(.*?)\\Q]]\\E)*)*");

	private List<MenuItem> _children = new LinkedList<>();
	private boolean _externalURL;
	private String _icon;
	private String _id;
	private String _name;
	private String _url;

}