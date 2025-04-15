/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet;

import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = PortletProvider.class
)
public class JournalContentAddPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return JournalContentPortletKeys.JOURNAL_CONTENT;
	}

	@Override
	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group)
		throws PortalException {

		return PortletURLFactoryUtil.create(
			httpServletRequest, getPortletName(), PortletRequest.RENDER_PHASE);
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	private final Action[] _supportedActions = {Action.ADD};

}