/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.struts;

import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.FindStrutsAction;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageResourceLocalService;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Kong
 */
@Component(property = "path=/wiki/find_page", service = StrutsAction.class)
public class FindPageStrutsAction extends FindStrutsAction {

	@Override
	protected void addRequiredParameters(
		HttpServletRequest httpServletRequest, String portletId,
		PortletURL portletURL) {

		portletURL.setParameter("struts_action", _getStrutsAction(portletId));
	}

	@Override
	protected long getGroupId(long primaryKey) throws Exception {
		WikiPageResource pageResource =
			_wikiPageResourceLocalService.getPageResource(primaryKey);

		WikiNode node = _wikiNodeLocalService.getNode(pageResource.getNodeId());

		return node.getGroupId();
	}

	@Override
	protected PortletLayoutFinder getPortletLayoutFinder() {
		return _portletLayoutFinder;
	}

	@Override
	protected String getPrimaryKeyParameterName() {
		return "pageResourcePrimKey";
	}

	@Override
	protected PortletURL processPortletURL(
			HttpServletRequest httpServletRequest, PortletURL portletURL)
		throws Exception {

		long pageResourcePrimKey = ParamUtil.getLong(
			httpServletRequest, getPrimaryKeyParameterName());

		WikiPageResource pageResource =
			_wikiPageResourceLocalService.getPageResource(pageResourcePrimKey);

		WikiNode node = _wikiNodeLocalService.getNode(pageResource.getNodeId());

		portletURL.setParameter("nodeName", node.getName());

		portletURL.setParameter("title", pageResource.getTitle());

		return portletURL;
	}

	private String _getStrutsAction(String portletId) {
		if (portletId.equals(WikiPortletKeys.WIKI) ||
			portletId.equals(WikiPortletKeys.WIKI_ADMIN)) {

			return "/wiki/view";
		}

		return "/wiki_display/view";
	}

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiPage)")
	private PortletLayoutFinder _portletLayoutFinder;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference
	private WikiPageResourceLocalService _wikiPageResourceLocalService;

}