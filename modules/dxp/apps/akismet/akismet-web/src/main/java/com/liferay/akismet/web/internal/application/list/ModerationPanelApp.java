/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.akismet.web.internal.application.list;

import com.liferay.akismet.web.internal.constants.ModerationPortletKeys;
import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jamie Sammons
 */
@Component(
	enabled = false,
	property = "panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
	service = PanelApp.class
)
public class ModerationPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ModerationPortletKeys.MODERATION;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ModerationPortletKeys.MODERATION + ")"
	)
	private Portlet _portlet;

}