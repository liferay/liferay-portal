/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.language.override.service.PLOEntryService;
import com.liferay.portal.language.override.web.internal.constants.PLOPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.ResourceURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Erick Monteiro
 */
@Component(
	property = "jakarta.portlet.name=" + PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE,
	service = PortletConfigurationIcon.class
)
public class ExportPLOEntriesPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getCssClass() {
		return "text-nowrap";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(
			getLocale(portletRequest), "export-overridden-translations");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(portletResponse);

		ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

		resourceURL.setResourceID("exportPLOEntries");

		return resourceURL.toString();
	}

	@Override
	public double getWeight() {
		return 200;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			int ploEntriesCount = _ploEntryService.getPLOEntriesCount(
				_portal.getCompanyId(portletRequest));

			if (ploEntriesCount > 0) {
				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportPLOEntriesPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private PLOEntryService _ploEntryService;

	@Reference
	private Portal _portal;

}