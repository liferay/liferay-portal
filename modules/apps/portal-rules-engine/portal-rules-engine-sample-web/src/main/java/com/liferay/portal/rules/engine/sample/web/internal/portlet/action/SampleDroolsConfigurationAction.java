/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.rules.engine.sample.web.internal.portlet.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.resource.StringResourceRetriever;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.rules.engine.RulesEngine;
import com.liferay.portal.rules.engine.RulesEngineException;
import com.liferay.portal.rules.engine.RulesLanguage;
import com.liferay.portal.rules.engine.RulesResourceRetriever;
import com.liferay.portal.rules.engine.sample.web.internal.constants.SampleDroolsPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "jakarta.portlet.name=" + SampleDroolsPortletKeys.SAMPLE_DROOLS,
	service = ConfigurationAction.class
)
public class SampleDroolsConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (!cmd.equals(Constants.UPDATE)) {
			return;
		}

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		_updatePreferences(actionRequest, portletPreferences);

		if (SessionErrors.isEmpty(actionRequest)) {
			portletPreferences.store();

			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_UPDATED_CONFIGURATION);
		}
	}

	private void _updatePreferences(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		String domainName = ParamUtil.getString(actionRequest, "domainName");
		String rules = ParamUtil.getString(actionRequest, "rules");
		long[] classNameIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "classNameIds"), 0L);

		if (Validator.isNull(domainName)) {
			SessionErrors.add(actionRequest, "domainName");
		}
		else if (Validator.isNull(rules)) {
			SessionErrors.add(actionRequest, "rules");
		}
		else if (classNameIds.length == 0) {
			SessionErrors.add(actionRequest, "classNameIds");
		}
		else {
			RulesResourceRetriever rulesResourceRetriever =
				new RulesResourceRetriever(
					new StringResourceRetriever(rules),
					String.valueOf(RulesLanguage.DROOLS_RULE_LANGUAGE));

			try {
				_rulesEngine.update(domainName, rulesResourceRetriever);
			}
			catch (RulesEngineException rulesEngineException) {
				_log.error(rulesEngineException);

				SessionErrors.add(actionRequest, "rulesEngineException");
			}
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			portletPreferences.setValue("rules", rules);
			portletPreferences.setValue("domain-name", domainName);

			String userCustomAttributeNames = ParamUtil.getString(
				actionRequest, "userCustomAttributeNames");

			portletPreferences.setValue(
				"user-custom-attribute-names", userCustomAttributeNames);

			portletPreferences.setValues(
				"class-name-ids", ArrayUtil.toStringArray(classNameIds));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SampleDroolsConfigurationAction.class);

	@Reference
	private Portal _portal;

	@Reference
	private RulesEngine _rulesEngine;

}