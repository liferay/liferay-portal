/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.knowledge.base.configuration.KBServiceConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alicia García
 */
public class KBArticleCompanyConfigurationDisplayContext {

	public KBArticleCompanyConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		KBServiceConfigurationProvider kbServiceConfigurationProvider,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_kbServiceConfigurationProvider = kbServiceConfigurationProvider;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public int getCheckInterval() throws ConfigurationException {
		return _kbServiceConfigurationProvider.getCheckInterval();
	}

	public String getEditKBArticleConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/system_settings/edit_kb_article_expiration_date_configuration"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).buildString();
	}

	public int getExpirationDateNotificationDateWeeks()
		throws ConfigurationException {

		return _kbServiceConfigurationProvider.
			getExpirationDateNotificationDateWeeks();
	}

	private final HttpServletRequest _httpServletRequest;
	private final KBServiceConfigurationProvider
		_kbServiceConfigurationProvider;
	private final LiferayPortletResponse _liferayPortletResponse;

}