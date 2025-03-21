/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.web.internal.display.context.KBArticleConfigurationDisplayContext;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
	service = ConfigurationAction.class
)
public class ArticleConfigurationAction extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/article/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			KBArticleConfigurationDisplayContext.class.getName(),
			new KBArticleConfigurationDisplayContext(
				httpServletRequest, _itemSelector, _kbArticleService,
				_portal.getLiferayPortletResponse(
					(PortletResponse)httpServletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_RESPONSE)),
				_portal));

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Portal _portal;

}