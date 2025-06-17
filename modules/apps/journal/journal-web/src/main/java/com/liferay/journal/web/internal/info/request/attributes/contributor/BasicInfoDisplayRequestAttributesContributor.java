/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.request.attributes.contributor;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.display.request.attributes.contributor.InfoDisplayRequestAttributesContributor;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderResponseFactory;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = InfoDisplayRequestAttributesContributor.class)
public class BasicInfoDisplayRequestAttributesContributor
	implements InfoDisplayRequestAttributesContributor {

	@Override
	public void addAttributes(HttpServletRequest httpServletRequest) {
		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if ((infoItemDetails == null) ||
			!Objects.equals(
				JournalArticle.class.getName(),
				infoItemDetails.getClassName())) {

			return;
		}

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if (portletRequest != null) {
			return;
		}

		Portlet portlet = _portletLocalService.getPortletById(
			JournalPortletKeys.JOURNAL);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
				portlet, httpServletRequest.getServletContext());

			PortletConfig portletConfig = PortletConfigFactoryUtil.create(
				portlet, httpServletRequest.getServletContext());

			LiferayRenderRequest liferayRenderRequest =
				RenderRequestFactory.create(
					httpServletRequest, portlet, invokerPortlet,
					portletConfig.getPortletContext(), WindowState.NORMAL,
					PortletMode.VIEW,
					PortletPreferencesFactoryUtil.fromDefaultXML(
						portlet.getDefaultPreferences()),
					themeDisplay.getPlid());

			httpServletRequest.setAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST, liferayRenderRequest);
			httpServletRequest.setAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE,
				RenderResponseFactory.create(
					themeDisplay.getResponse(), liferayRenderRequest));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasicInfoDisplayRequestAttributesContributor.class);

	@Reference
	private PortletLocalService _portletLocalService;

}