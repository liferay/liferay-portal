/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.analytics.settings.web.internal.model.Field;
import com.liferay.analytics.settings.web.internal.search.FieldChecker;
import com.liferay.analytics.settings.web.internal.search.FieldSearch;
import com.liferay.analytics.settings.web.internal.user.AnalyticsUsersManager;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 */
public class FieldDisplayContext {

	public static final String[] REQUIRED_CONTACT_FIELD_NAMES = {
		"classPK", "contactId", "createDate", "emailAddress", "modifiedDate"
	};

	public static final String[] REQUIRED_USER_FIELD_NAMES = {
		"createDate", "emailAddress", "modifiedDate", "userId", "uuid"
	};

	public FieldDisplayContext(
		String mvcRenderCommandName, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_mvcRenderCommandName = mvcRenderCommandName;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_analyticsConfiguration =
			(AnalyticsConfiguration)renderRequest.getAttribute(
				AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);
		_analyticsUsersManager =
			(AnalyticsUsersManager)renderRequest.getAttribute(
				AnalyticsSettingsWebKeys.ANALYTICS_USERS_MANAGER);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_companyId = themeDisplay.getCompanyId();
	}

	public FieldSearch getFieldSearch() {
		FieldSearch fieldSearch = new FieldSearch(
			_renderRequest, getPortletURL());

		List<Field> fields = new ArrayList<>();

		if (StringUtil.equalsIgnoreCase(
				_mvcRenderCommandName,
				"/analytics_settings/edit_synced_contacts_fields")) {

			for (String fieldName : REQUIRED_CONTACT_FIELD_NAMES) {
				fields.add(
					new Field(
						"Default Field", _contactFieldNames.get(fieldName),
						fieldName));
			}

			String[] recommendedContactFieldNames = new String[0];

			String[] syncedContactFieldNames = GetterUtil.getStringValues(
				_analyticsConfiguration.syncedContactFieldNames());

			if (syncedContactFieldNames.length <=
					REQUIRED_CONTACT_FIELD_NAMES.length) {

				recommendedContactFieldNames = new String[] {
					"birthday", "firstName", "jobTitle", "lastName"
				};

				for (String fieldName : recommendedContactFieldNames) {
					fields.add(
						new Field(
							"Default Field", _contactFieldNames.get(fieldName),
							fieldName));
				}
			}

			for (Map.Entry<String, String> entry :
					_contactFieldNames.entrySet()) {

				if (ArrayUtil.contains(
						REQUIRED_CONTACT_FIELD_NAMES, entry.getKey()) ||
					ArrayUtil.contains(
						recommendedContactFieldNames, entry.getKey())) {

					continue;
				}

				fields.add(
					new Field(
						"Default Field", entry.getValue(), entry.getKey()));
			}

			fieldSearch.setResultsAndTotal(
				() -> fields,
				_contactFieldNames.size() -
					REQUIRED_CONTACT_FIELD_NAMES.length);
			fieldSearch.setRowChecker(
				new FieldChecker(
					_mvcRenderCommandName, _renderResponse,
					recommendedContactFieldNames, REQUIRED_CONTACT_FIELD_NAMES,
					syncedContactFieldNames));
		}
		else if (StringUtil.equalsIgnoreCase(
					_mvcRenderCommandName,
					"/analytics_settings/edit_synced_users_fields")) {

			for (String fieldName : REQUIRED_USER_FIELD_NAMES) {
				fields.add(
					new Field(
						"Default Field", _userFieldNames.get(fieldName),
						fieldName));
			}

			String[] recommendedUserFieldNames = new String[0];

			String[] syncedUserFieldNames = GetterUtil.getStringValues(
				_analyticsConfiguration.syncedUserFieldNames());

			if (syncedUserFieldNames.length <=
					REQUIRED_USER_FIELD_NAMES.length) {

				recommendedUserFieldNames = new String[] {
					"firstName", "jobTitle", "lastName", "timeZoneId"
				};

				for (String fieldName : recommendedUserFieldNames) {
					fields.add(
						new Field(
							"Default Field", _userFieldNames.get(fieldName),
							fieldName));
				}
			}

			for (Map.Entry<String, String> entry : _userFieldNames.entrySet()) {
				if (ArrayUtil.contains(
						REQUIRED_USER_FIELD_NAMES, entry.getKey()) ||
					ArrayUtil.contains(
						recommendedUserFieldNames, entry.getKey())) {

					continue;
				}

				fields.add(
					new Field(
						"Default Field", entry.getValue(), entry.getKey()));
			}

			Map<String, String> userCustomFieldNames =
				_getUserCustomFieldNames();

			for (Map.Entry<String, String> entry :
					userCustomFieldNames.entrySet()) {

				fields.add(
					new Field(
						"Custom Field", entry.getValue(), entry.getKey()));
			}

			fieldSearch.setResultsAndTotal(
				() -> fields,
				_userFieldNames.size() + userCustomFieldNames.size() -
					REQUIRED_USER_FIELD_NAMES.length);
			fieldSearch.setRowChecker(
				new FieldChecker(
					_mvcRenderCommandName, _renderResponse,
					recommendedUserFieldNames, REQUIRED_USER_FIELD_NAMES,
					syncedUserFieldNames));
		}

		return fieldSearch;
	}

	public String getMVCRenderCommandName() {
		return _mvcRenderCommandName;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			_mvcRenderCommandName
		).buildPortletURL();
	}

	private String _getDataType(int type) {
		if ((type == ExpandoColumnConstants.BOOLEAN) ||
			(type == ExpandoColumnConstants.BOOLEAN_ARRAY)) {

			return "Boolean";
		}
		else if ((type == ExpandoColumnConstants.DATE) ||
				 (type == ExpandoColumnConstants.DATE_ARRAY)) {

			return "Date";
		}
		else if ((type == ExpandoColumnConstants.DOUBLE) ||
				 (type == ExpandoColumnConstants.DOUBLE_ARRAY) ||
				 (type == ExpandoColumnConstants.FLOAT) ||
				 (type == ExpandoColumnConstants.FLOAT_ARRAY)) {

			return "Decimal";
		}
		else if ((type == ExpandoColumnConstants.INTEGER) ||
				 (type == ExpandoColumnConstants.INTEGER_ARRAY)) {

			return "Integer";
		}
		else if ((type == ExpandoColumnConstants.LONG) ||
				 (type == ExpandoColumnConstants.LONG_ARRAY)) {

			return "Long";
		}
		else if ((type == ExpandoColumnConstants.NUMBER) ||
				 (type == ExpandoColumnConstants.NUMBER_ARRAY) ||
				 (type == ExpandoColumnConstants.SHORT) ||
				 (type == ExpandoColumnConstants.SHORT_ARRAY)) {

			return "Number";
		}

		return "String";
	}

	private Map<String, String> _getUserCustomFieldNames() {
		Map<String, String> userCustomFieldNames = new TreeMap<>();

		for (ExpandoColumn expandoColumn :
				_analyticsUsersManager.getUserExpandoColumns(_companyId)) {

			userCustomFieldNames.put(
				expandoColumn.getName(), _getDataType(expandoColumn.getType()));
		}

		return userCustomFieldNames;
	}

	private static final Map<String, String> _contactFieldNames =
		TreeMapBuilder.put(
			"birthday", "Date"
		).put(
			"classNameId", "Long"
		).put(
			"classPK", "Long"
		).put(
			"companyId", "Long"
		).put(
			"contactId", "Long"
		).put(
			"createDate", "Date"
		).put(
			"emailAddress", "String"
		).put(
			"employeeNumber", "String"
		).put(
			"employeeStatusId", "String"
		).put(
			"facebookSn", "String"
		).put(
			"firstName", "String"
		).put(
			"hoursOfOperation", "String"
		).put(
			"jabberSn", "String"
		).put(
			"jobClass", "String"
		).put(
			"jobTitle", "String"
		).put(
			"lastName", "String"
		).put(
			"male", "Boolean"
		).put(
			"middleName", "String"
		).put(
			"modifiedDate", "Date"
		).put(
			"parentContactId", "Long"
		).put(
			"prefixListTypeId", "Long"
		).put(
			"skypeSn", "String"
		).put(
			"smsSn", "String"
		).put(
			"suffixListTypeId", "Long"
		).put(
			"twitterSn", "String"
		).put(
			"userName", "String"
		).build();
	private static final Map<String, String> _userFieldNames =
		TreeMapBuilder.put(
			"agreedToTermsOfUse", "Boolean"
		).put(
			"comments", "String"
		).put(
			"companyId", "Long"
		).put(
			"contactId", "Long"
		).put(
			"createDate", "Date"
		).put(
			"emailAddress", "String"
		).put(
			"emailAddressVerified", "Boolean"
		).put(
			"externalReferenceCode", "String"
		).put(
			"facebookId", "Long"
		).put(
			"firstName", "String"
		).put(
			"googleUserId", "String"
		).put(
			"greeting", "String"
		).put(
			"jobTitle", "String"
		).put(
			"languageId", "String"
		).put(
			"lastName", "String"
		).put(
			"ldapServerId", "Long"
		).put(
			"middleName", "String"
		).put(
			"modifiedDate", "Date"
		).put(
			"openId", "String"
		).put(
			"portraitId", "Long"
		).put(
			"screenName", "String"
		).put(
			"status", "Integer"
		).put(
			"timeZoneId", "String"
		).put(
			"userId", "Long"
		).put(
			"uuid", "String"
		).build();

	private final AnalyticsConfiguration _analyticsConfiguration;
	private final AnalyticsUsersManager _analyticsUsersManager;
	private final long _companyId;
	private final String _mvcRenderCommandName;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}