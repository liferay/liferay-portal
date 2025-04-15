/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.frontend.taglib.form.navigator;

import com.liferay.asset.publisher.constants.AssetPublisherConstants;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "form.navigator.entry.order:Integer=200",
	service = FormNavigatorEntry.class
)
public class RSSFormNavigatorEntry extends BaseConfigurationFormNavigatorEntry {

	@Override
	public String getCategoryKey() {
		return AssetPublisherConstants.CATEGORY_KEY_SUBSCRIPTIONS;
	}

	@Override
	public String getKey() {
		return "rss";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, Object formModelBean) {
		if (!_portal.isRSSFeedsEnabled() || !isDynamicAssetSelection()) {
			return false;
		}

		return true;
	}

	@Override
	protected String getJspPath() {
		return "/configuration/rss.jsp";
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.asset.publisher.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}