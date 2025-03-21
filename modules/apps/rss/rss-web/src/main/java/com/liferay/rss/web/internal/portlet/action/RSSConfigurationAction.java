/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.portlet.action;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;
import com.liferay.rss.constants.RSSPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + RSSPortletKeys.RSS,
	service = ConfigurationAction.class
)
public class RSSConfigurationAction extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		_updateSubscriptions(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _updateSubscriptions(ActionRequest actionRequest)
		throws Exception {

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "preferences--");

		long entriesPerFeed = GetterUtil.getLong(
			unicodeProperties.getProperty("entriesPerFeed"));
		long expandedEntriesPerFeed = GetterUtil.getLong(
			unicodeProperties.getProperty("expandedEntriesPerFeed"));

		if ((entriesPerFeed < 1) || (expandedEntriesPerFeed < 0)) {
			throw new ConfigurationException();
		}

		int[] subscriptionIndexes = StringUtil.split(
			ParamUtil.getString(actionRequest, "subscriptionIndexes"), 0);

		Map<String, String> subscriptions = new LinkedHashMap<>();

		for (int subscriptionIndex : subscriptionIndexes) {
			String url = ParamUtil.getString(
				actionRequest, "url" + subscriptionIndex);

			if (Validator.isNull(url)) {
				continue;
			}

			String title = ParamUtil.getString(
				actionRequest, "title" + subscriptionIndex);

			subscriptions.put(url, title);
		}

		String[] urls = new String[subscriptions.size()];
		String[] titles = new String[subscriptions.size()];

		int i = 0;

		for (Map.Entry<String, String> entry : subscriptions.entrySet()) {
			urls[i] = entry.getKey();
			titles[i] = entry.getValue();

			i++;
		}

		setPreference(actionRequest, "urls", urls);
		setPreference(actionRequest, "titles", titles);
	}

}