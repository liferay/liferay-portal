/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerWebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.permission.KaleoDefinitionVersionPermission;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jeyvison Nascimento
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoDesignerPortletKeys.KALEO_DESIGNER,
		"path=/designer/edit_kaleo_definition_version.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class UnpublishDefinitionPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "unpublish");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, KaleoDesignerPortletKeys.KALEO_DESIGNER,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/kaleo_designer/unpublish_kaleo_definition_version"
		).setMVCPath(
			portletRequest.getParameter("mvcPath")
		).buildPortletURL();

		KaleoDefinition kaleoDefinition = getKaleoDefinition(portletRequest);

		portletURL.setParameter("name", kaleoDefinition.getName());
		portletURL.setParameter(
			"version", String.valueOf(kaleoDefinition.getVersion()));

		KaleoDefinitionVersion kaleoDefinitionVersion =
			(KaleoDefinitionVersion)portletRequest.getAttribute(
				KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION);

		portletURL.setParameter(
			"draftVersion",
			String.valueOf(kaleoDefinitionVersion.getVersion()));

		return portletURL.toString();
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		KaleoDefinition kaleoDefinition = getKaleoDefinition(portletRequest);

		if ((kaleoDefinition != null) && kaleoDefinition.isActive()) {
			KaleoDefinitionVersion kaleoDefinitionVersion =
				getKaleoDefinitionVersion(portletRequest);

			if (kaleoDefinitionVersion == null) {
				return false;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			try {
				if (KaleoDefinitionVersionPermission.contains(
						themeDisplay.getPermissionChecker(),
						kaleoDefinitionVersion, ActionKeys.UPDATE)) {

					return true;
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return false;
	}

	protected KaleoDefinition getKaleoDefinition(
		PortletRequest portletRequest) {

		KaleoDefinitionVersion kaleoDefinitionVersion =
			getKaleoDefinitionVersion(portletRequest);

		if (kaleoDefinitionVersion == null) {
			return null;
		}

		try {
			return kaleoDefinitionVersion.getKaleoDefinition();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	protected KaleoDefinitionVersion getKaleoDefinitionVersion(
		PortletRequest portletRequest) {

		return (KaleoDefinitionVersion)portletRequest.getAttribute(
			KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnpublishDefinitionPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}