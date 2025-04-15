/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.ldap.DuplicateLDAPServerNameException;
import com.liferay.portal.security.ldap.LDAPServerNameException;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.security.ldap.validator.LDAPFilterException;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Philip Jones
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/portal_settings_authentication_ldap/edit_ldap_server"
	},
	service = MVCActionCommand.class
)
public class EditLDAPServerMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateLDAPServer(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteLDAPServer(actionRequest);
			}

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			if (exception instanceof DuplicateLDAPServerNameException ||
				exception instanceof LDAPFilterException ||
				exception instanceof LDAPServerNameException) {

				SessionErrors.add(actionRequest, exception.getClass());

				PortletURL portletURL = PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						actionRequest,
						ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/portal_settings_authentication_ldap" +
						"/portal_settings_edit_ldap_server"
				).buildPortletURL();

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				portletURL.setParameter("redirect", redirect);

				actionResponse.sendRedirect(portletURL.toString());

				return;
			}

			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");

				return;
			}

			throw exception;
		}
	}

	private void _deleteLDAPServer(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ldapServerId = ParamUtil.getLong(actionRequest, "ldapServerId");

		_ldapServerConfigurationProvider.delete(
			themeDisplay.getCompanyId(), ldapServerId);
	}

	private void _splitStringArrays(
		Dictionary<String, Object> dictionary, String property) {

		Object propertyValue = dictionary.get(property);

		if (propertyValue == null) {
			return;
		}

		if (propertyValue instanceof String) {
			String[] propertyValues = StringUtil.split((String)propertyValue);

			dictionary.put(property, propertyValues);
		}
	}

	private void _updateLDAPServer(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ldapServerId = ParamUtil.getLong(
			actionRequest, LDAPConstants.LDAP_SERVER_ID);

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "ldap--");

		_validateLDAPServerName(
			ldapServerId, themeDisplay.getCompanyId(), unicodeProperties);

		_validateSearchFilters(actionRequest);

		Dictionary<String, Object> dictionary = null;

		if (ldapServerId <= 0) {
			ldapServerId = _counterLocalService.increment();

			dictionary = new HashMapDictionary<>();
		}
		else {
			dictionary =
				_ldapServerConfigurationProvider.getConfigurationProperties(
					themeDisplay.getCompanyId(), ldapServerId);
		}

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			if (Portal.TEMP_OBFUSCATION_VALUE.equals(entry.getValue())) {
				continue;
			}

			dictionary.put(entry.getKey(), entry.getValue());
		}

		_splitStringArrays(dictionary, LDAPConstants.CONTACT_CUSTOM_MAPPINGS);
		_splitStringArrays(dictionary, LDAPConstants.CONTACT_MAPPINGS);
		_splitStringArrays(
			dictionary, LDAPConstants.GROUP_DEFAULT_OBJECT_CLASSES);
		_splitStringArrays(dictionary, LDAPConstants.GROUP_MAPPINGS);
		_splitStringArrays(dictionary, LDAPConstants.USER_CUSTOM_MAPPINGS);
		_splitStringArrays(
			dictionary, LDAPConstants.USER_DEFAULT_OBJECT_CLASSES);
		_splitStringArrays(dictionary, LDAPConstants.USER_IGNORE_ATTRIBUTES);
		_splitStringArrays(dictionary, LDAPConstants.USER_MAPPINGS);

		_ldapServerConfigurationProvider.updateProperties(
			themeDisplay.getCompanyId(), ldapServerId, dictionary);
	}

	private void _validateLDAPServerName(
			long ldapServerId, long companyId,
			UnicodeProperties unicodeProperties)
		throws Exception {

		String serverName = unicodeProperties.getProperty(
			LDAPConstants.SERVER_NAME);

		if (Validator.isNull(serverName)) {
			throw new LDAPServerNameException();
		}

		List<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(companyId);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			String existingServerName = ldapServerConfiguration.serverName();

			if (serverName.equals(existingServerName) &&
				(ldapServerId != ldapServerConfiguration.ldapServerId())) {

				throw new DuplicateLDAPServerNameException();
			}
		}
	}

	private void _validateSearchFilters(ActionRequest actionRequest)
		throws Exception {

		String userFilter = ParamUtil.getString(
			actionRequest, "importUserSearchFilter");

		_ldapFilterValidator.validate(userFilter, "importUserSearchFilter");

		String groupFilter = ParamUtil.getString(
			actionRequest, "importGroupSearchFilter");

		_ldapFilterValidator.validate(groupFilter, "importGroupSearchFilter");
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private LDAPFilterValidator _ldapFilterValidator;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration)"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

}