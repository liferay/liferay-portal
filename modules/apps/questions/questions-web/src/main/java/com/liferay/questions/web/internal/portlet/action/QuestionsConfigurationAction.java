/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.questions.web.internal.constants.QuestionsPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	property = "jakarta.portlet.name=" + QuestionsPortletKeys.QUESTIONS,
	service = ConfigurationAction.class
)
public class QuestionsConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		Settings settings = getSettings(actionRequest);

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String rootTopicExternalReferenceCode = ParamUtil.getString(
			actionRequest, "preferences--rootTopicExternalReferenceCode--");

		modifiableSettings.setValue(
			"rootTopicExternalReferenceCode", rootTopicExternalReferenceCode);

		String showCardsForTopicNavigation = ParamUtil.getString(
			actionRequest, "preferences--showCardsForTopicNavigation--");

		modifiableSettings.setValue(
			"showCardsForTopicNavigation", showCardsForTopicNavigation);

		for (String key : _KEYS) {
			UnicodeProperties unicodeProperties =
				PropertiesParamUtil.getProperties(
					actionRequest, "preferences--" + key + "_");

			Map<String, String> properties = new HashMap<>();

			for (Map.Entry<String, String> entry :
					unicodeProperties.entrySet()) {

				properties.put(entry.getKey(), entry.getValue());
			}

			String xml = _localization.getXml(
				properties, StringPool.BLANK, key);

			modifiableSettings.setValue(key, xml);
		}

		modifiableSettings.store();
	}

	private static final String[] _KEYS = {
		"askQuestionButtonTextAsLocalizedXML",
		"editQuestionPageTitleAsLocalizedXML",
		"newQuestionPageTitleAsLocalizedXML",
		"postYourQuestionButtonTextAsLocalizedXML",
		"updateYourQuestionButtonTextAsLocalizedXML"
	};

	@Reference
	private Localization _localization;

}