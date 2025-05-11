/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.tck.bridge;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.InitialRequestSyncUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.tck.bridge.configuration.PortletTCKBridgeConfiguration;

import java.io.File;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Tambara
 * @author Vernon Singleton
 * @author Kyle Stiemann
 */
@Component(
	configurationPid = "com.liferay.portlet.tck.bridge.configuration.PortletTCKBridgeConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class PortletTCKBridge {

	@Activate
	protected void activate(Map<String, String> properties) throws Exception {
		PortletTCKBridgeConfiguration portletTCKBridgeConfiguration =
			ConfigurableUtil.createConfigurable(
				PortletTCKBridgeConfiguration.class, properties);

		_setUpCookies(portletTCKBridgeConfiguration.cookieNames());

		Company company = _companyLocalService.getCompanyByWebId(
			PropsValues.COMPANY_DEFAULT_WEB_ID);

		Group group = _groupLocalService.fetchGroup(
			company.getCompanyId(), _TCK_SITE_GROUP_NAME);

		if (group != null) {
			return;
		}

		InitialRequestSyncUtil.registerSyncCallable(
			() -> {
				_setUpPortletTCKSite(
					company, portletTCKBridgeConfiguration.configFile());

				return null;
			});
	}

	private void _setUpCookies(String[] cookieNames) {
		try {
			Field field = ReflectionUtil.getDeclaredField(
				_cookiesManager.getClass(), "_internalCookies");

			Map<String, Integer> internalCookies =
				(Map<String, Integer>)field.get(_cookiesManager);

			for (String cookieName : cookieNames) {
				if (_log.isInfoEnabled()) {
					_log.info("Added cookie " + cookieName);
				}

				internalCookies.put(
					cookieName, CookiesConstants.CONSENT_TYPE_NECESSARY);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _setUpPortletTCKSite(Company company, String configFile)
		throws Exception {

		User user = _userLocalService.getUserByEmailAddress(
			company.getCompanyId(),
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + "@" +
				company.getMx());

		long userId = user.getUserId();

		Map<Locale, String> nameMap = Collections.singletonMap(
			LocaleUtil.US, _TCK_SITE_GROUP_NAME);

		Group group = _groupLocalService.addGroup(
			userId, GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0L,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, nameMap,
			GroupConstants.TYPE_SITE_OPEN, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, "/portlet-tck", true,
			false, true, new ServiceContext());

		Document document = SAXReaderUtil.read(new File(configFile));

		Element rootElement = document.getRootElement();

		Element renderConfigElement = rootElement.element("render-config");

		for (Element pageElement : renderConfigElement.elements("page")) {
			List<String> portletIds = new ArrayList<>();

			for (Element portletElement : pageElement.elements("portlet")) {
				String context = portletElement.attributeValue("context");

				Matcher matcher = _portletContextPattern.matcher(context);

				if (matcher.find()) {
					portletIds.add(
						portletElement.attributeValue("name") + "_WAR_" +
							matcher.group(1));
				}
			}

			if (portletIds.isEmpty()) {
				continue;
			}

			String pageName = pageElement.attributeValue("name");

			Layout layout = _layoutLocalService.addLayout(
				null, userId, group.getGroupId(), true,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, pageName, pageName,
				pageName, LayoutConstants.TYPE_PORTLET, false,
				"/" + StringUtil.toLowerCase(pageName), new ServiceContext());

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			layoutTypePortlet.setLayoutTemplateId(userId, "1_column", false);

			for (String portletId : portletIds) {
				layoutTypePortlet.addPortletId(
					userId, portletId, "column-1", -1, false);
			}

			_layoutLocalService.updateLayout(layout);
		}
	}

	private static final String _TCK_SITE_GROUP_NAME = "Portlet TCK";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletTCKBridge.class);

	private static final Pattern _portletContextPattern = Pattern.compile(
		"/(tck-.*)(-[0-9.]+)-SNAPSHOT");

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CookiesManager _cookiesManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private UserLocalService _userLocalService;

}