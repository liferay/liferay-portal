/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.audit.web.internal.constants.AuditPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = "jakarta.portlet.name=" + AuditPortletKeys.AUDIT,
	service = PortletConfigurationIcon.class
)
public class ExportAuditEventsPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "exportAuditEvents"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/configuration/icon/export_audit_events.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "export-audit-events");
	}

	@Override
	public double getWeight() {
		return 101;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return true;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.audit.web)"
	)
	private ServletContext _servletContext;

}