/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.io.IOException;

import org.osgi.service.cm.Configuration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kamesh Sampath
 * @author Raymond Augé
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/configuration_admin/delete_configuration"
	},
	service = MVCActionCommand.class
)
public class DeleteConfigurationMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		String pid = ParamUtil.getString(actionRequest, "pid");

		if (_log.isDebugEnabled()) {
			_log.debug("Deleting configuration for service " + pid);
		}

		try {
			Configuration configuration =
				_configurationModelRetriever.getConfiguration(
					pid, ExtendedObjectClassDefinition.Scope.SYSTEM, null);

			if (configuration == null) {
				return false;
			}

			configuration.delete();
		}
		catch (IOException ioException) {
			throw new PortletException(ioException);
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteConfigurationMVCActionCommand.class);

	@Reference(target = "(!(filter.visibility=*))")
	private ConfigurationModelRetriever _configurationModelRetriever;

}