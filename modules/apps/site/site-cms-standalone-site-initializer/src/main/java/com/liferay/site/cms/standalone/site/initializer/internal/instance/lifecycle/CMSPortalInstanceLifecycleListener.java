/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.instance.lifecycle;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CMSPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.vulcan.internal.configuration." +
					"VulcanConfiguration",
				StringPool.QUESTION);

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				"graphQLEnabled", false
			).put(
				"path", "/headless-delivery"
			).put(
				"restEnabled", false
			).build());

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			company.getCompanyId());

		boolean modified = false;

		if (Validator.isNull(
				portletPreferences.getValue(
					PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED, null))) {

			portletPreferences.setValue(
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED, "false");

			modified = true;
		}

		if (Validator.isNull(
				portletPreferences.getValue(
					PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED, null))) {

			portletPreferences.setValue(
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED, "false");

			modified = true;
		}

		if (modified) {
			portletPreferences.store();
		}
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}