/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.tracker.internal;

import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Fellwock
 * @author Raymond Augé
 */
public class PortletPropertyValidator {

	public boolean validate(String key) {
		if ((!StringUtil.startsWith(key, "com.liferay.portlet.") &&
			 !StringUtil.startsWith(key, "jakarta.portlet.")) ||
			_validKeys.contains(key)) {

			return true;
		}

		return false;
	}

	public List<String> validate(String[] keys) {
		List<String> invalidKeys = new ArrayList<>();

		for (String key : keys) {
			if (!validate(key)) {
				invalidKeys.add(key);
			}
		}

		return invalidKeys;
	}

	private final Set<String> _validKeys = SetUtil.fromArray(
		"com.liferay.portlet.action-timeout",
		"com.liferay.portlet.action-url-redirect", "com.liferay.portlet.active",
		"com.liferay.portlet.add-default-resource",
		"com.liferay.portlet.ajaxable", "com.liferay.portlet.application-type",
		"com.liferay.portlet.autopropagated-parameters",
		"com.liferay.portlet.configuration-path",
		"com.liferay.portlet.control-panel-entry-category",
		"com.liferay.portlet.control-panel-entry-weight",
		"com.liferay.portlet.css-class-wrapper",
		"com.liferay.portlet.display-category",
		"com.liferay.portlet.facebook-integration",
		"com.liferay.portlet.footer-portal-css",
		"com.liferay.portlet.footer-portal-javascript",
		"com.liferay.portlet.footer-portlet-css",
		"com.liferay.portlet.footer-portlet-javascript",
		"com.liferay.portlet.friendly-url-mapping",
		"com.liferay.portlet.friendly-url-routes",
		"com.liferay.portlet.header-portal-css",
		"com.liferay.portlet.header-portal-javascript",
		"com.liferay.portlet.header-portlet-css",
		"com.liferay.portlet.header-portlet-javascript",
		"com.liferay.portlet.icon", "com.liferay.portlet.include",
		"com.liferay.portlet.instanceable",
		"com.liferay.portlet.layout-cacheable",
		"com.liferay.portlet.maximize-edit",
		"com.liferay.portlet.maximize-help",
		"com.liferay.portlet.parent-struts-path",
		"com.liferay.portlet.pop-up-print",
		"com.liferay.portlet.preferences-company-wide",
		"com.liferay.portlet.preferences-owned-by-group",
		"com.liferay.portlet.preferences-unique-per-layout",
		"com.liferay.portlet.private-request-attributes",
		"com.liferay.portlet.private-session-attributes",
		"com.liferay.portlet.render-timeout",
		"com.liferay.portlet.render-weight",
		"com.liferay.portlet.requires-namespaced-parameters",
		"com.liferay.portlet.restore-current-view",
		"com.liferay.portlet.scopeable",
		"com.liferay.portlet.show-portlet-access-denied",
		"com.liferay.portlet.show-portlet-inactive",
		"com.liferay.portlet.single-page-application",
		"com.liferay.portlet.struts-path", "com.liferay.portlet.system",
		"com.liferay.portlet.use-default-template",
		"com.liferay.portlet.user-principal-strategy",
		"com.liferay.portlet.virtual-path", "jakarta.portlet.description",
		"jakarta.portlet.display-name", "jakarta.portlet.expiration-cache",
		"jakarta.portlet.info.keywords", "jakarta.portlet.info.short-title",
		"jakarta.portlet.info.title", "jakarta.portlet.portlet-mode",
		"jakarta.portlet.portlet-name", "jakarta.portlet.preferences",
		"jakarta.portlet.resource-bundle", "jakarta.portlet.security-role-ref",
		"jakarta.portlet.supported-processing-event",
		"jakarta.portlet.supported-public-render-parameter",
		"jakarta.portlet.supported-publishing-event",
		"jakarta.portlet.window-state");

}