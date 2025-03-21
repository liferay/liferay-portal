/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.asset;

import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.trash.BaseTrashRenderer;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class WikiNodeTrashRenderer extends BaseTrashRenderer {

	public static final String TYPE = "wiki_node";

	public WikiNodeTrashRenderer(WikiNode node, TrashHelper trashHelper) {
		_node = node;
		_trashHelper = trashHelper;
	}

	@Override
	public String getClassName() {
		return WikiNode.class.getName();
	}

	@Override
	public long getClassPK() {
		return _node.getPrimaryKey();
	}

	@Override
	public String getIconCssClass() {
		return "folder";
	}

	@Override
	public String getPortletId() {
		return WikiPortletKeys.WIKI;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return HtmlUtil.stripHtml(_node.getDescription());
	}

	@Override
	public String getTitle(Locale locale) {
		if (!_node.isInTrash() || (_trashHelper == null)) {
			return _node.getName();
		}

		return _trashHelper.getOriginalTitle(_node.getName());
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String template) {

		return false;
	}

	private final WikiNode _node;
	private final TrashHelper _trashHelper;

}