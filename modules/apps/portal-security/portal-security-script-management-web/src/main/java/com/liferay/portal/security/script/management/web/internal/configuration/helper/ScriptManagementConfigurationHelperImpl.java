/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.configuration.helper;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import java.io.IOException;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	configurationPid = "com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration",
	service = ScriptManagementConfigurationHelper.class
)
public class ScriptManagementConfigurationHelperImpl
	implements ScriptManagementConfigurationHelper {

	@Override
	public String getScriptManagementConfigurationPortletURL()
		throws PortalException {

		Company company = _companyLocalService.getCompany(
			PortalInstancePool.getDefaultCompanyId());

		String url = StringBundler.concat(
			company.getPortalURL(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
			GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
			PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		url = HttpComponentsUtil.addParameter(
			url, "p_p_id", ConfigurationAdminPortletKeys.SYSTEM_SETTINGS);
		url = HttpComponentsUtil.addParameter(url, "p_p_lifecycle", "0");
		url = HttpComponentsUtil.addParameter(
			url, "p_p_state", WindowState.MAXIMIZED.toString());
		url = HttpComponentsUtil.addParameter(
			url, "p_p_mode", PortletMode.VIEW.toString());

		String namespace = _portal.getPortletNamespace(
			ConfigurationAdminPortletKeys.SYSTEM_SETTINGS);

		url = HttpComponentsUtil.addParameter(
			url, namespace + "configurationScreenKey", "script-management");

		return HttpComponentsUtil.addParameter(
			url, namespace + "mvcRenderCommandName",
			"/configuration_admin/view_configuration_screen");
	}

	@Override
	public boolean isAllowScriptContentToBeExecutedOrIncluded() {
		if (!PropsValues.SCRIPT_MANAGEMENT_CONFIGURATION_ENABLED) {
			return false;
		}

		return _systemScriptManagementConfiguration.
			allowScriptContentToBeExecutedOrIncluded();
	}

	@Override
	public boolean isScriptManagementConfigurationDefined()
		throws ConfigurationException {

		try {
			String filterString = StringBundler.concat(
				"(", Constants.SERVICE_PID, StringPool.EQUAL,
				ScriptManagementConfiguration.class.getName(), ")");

			if (_configurationAdmin.listConfigurations(filterString) != null) {
				return true;
			}

			return false;
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationException(exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemScriptManagementConfiguration =
			ConfigurableUtil.createConfigurable(
				ScriptManagementConfiguration.class, properties);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private Portal _portal;

	private volatile ScriptManagementConfiguration
		_systemScriptManagementConfiguration;

}