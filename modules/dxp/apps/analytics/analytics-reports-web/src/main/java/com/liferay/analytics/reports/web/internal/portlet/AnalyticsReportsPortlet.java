/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.portlet;

import com.liferay.analytics.reports.constants.AnalyticsReportsWebKeys;
import com.liferay.analytics.reports.info.item.ClassNameClassPKInfoItemIdentifier;
import com.liferay.analytics.reports.web.internal.constants.AnalyticsReportsPortletKeys;
import com.liferay.analytics.reports.web.internal.display.context.AnalyticsReportsDisplayContext;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 * @author Sarai Díaz
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.display-name=Content Performance",
		"jakarta.portlet.name=" + AnalyticsReportsPortletKeys.ANALYTICS_REPORTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AnalyticsReportsPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.PREVIEW)) {
			return;
		}

		InfoItemReference infoItemReference = _getInfoItemReference(
			httpServletRequest);

		renderRequest.setAttribute(
			AnalyticsReportsWebKeys.ANALYTICS_REPORTS_DISPLAY_CONTEXT,
			new AnalyticsReportsDisplayContext(
				infoItemReference, _portal, renderRequest, renderResponse));

		super.doDispatch(renderRequest, renderResponse);
	}

	private String _getClassName(HttpServletRequest httpServletRequest) {
		String className = ParamUtil.getString(httpServletRequest, "className");

		if (Validator.isNull(className)) {
			return Layout.class.getName();
		}

		return className;
	}

	private long _getClassPK(HttpServletRequest httpServletRequest) {
		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		if (classPK == 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return themeDisplay.getPlid();
		}

		return classPK;
	}

	private String _getClassTypeName(HttpServletRequest httpServletRequest) {
		return ParamUtil.getString(httpServletRequest, "classTypeName");
	}

	private InfoItemReference _getInfoItemReference(
		HttpServletRequest httpServletRequest) {

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				AnalyticsReportsWebKeys.ANALYTICS_INFO_ITEM_REFERENCE);

		if (infoItemReference != null) {
			return infoItemReference;
		}

		String classTypeName = _getClassTypeName(httpServletRequest);

		if (Validator.isNull(classTypeName)) {
			return new InfoItemReference(
				_getClassName(httpServletRequest),
				_getClassPK(httpServletRequest));
		}

		return new InfoItemReference(
			_getClassName(httpServletRequest),
			new ClassNameClassPKInfoItemIdentifier(
				classTypeName, _getClassPK(httpServletRequest)));
	}

	@Reference
	private Portal _portal;

}