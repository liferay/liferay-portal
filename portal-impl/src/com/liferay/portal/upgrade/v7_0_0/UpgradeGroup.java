/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class UpgradeGroup extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		dropIndexes("Group_", "name");

		alterColumnType("Group_", "name", "STRING null");

		try (SafeCloseable safeCloseable = addTemporaryIndex(
				"Group_", false, "classNameId", "classPK")) {

			updateGlobalGroupName();
			updateGroupsNames();
		}
	}

	protected void updateGlobalGroupName() throws Exception {
		for (Long companyId : PortalInstancePool.getCompanyIds()) {
			LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap();

			for (String languageId : PropsValues.LOCALES_ENABLED) {
				Locale locale = LocaleUtil.fromLanguageId(languageId);

				localizedValuesMap.put(
					locale,
					LanguageUtil.get(
						LanguageResources.getResourceBundle(locale), "global"));
			}

			String nameXML = LocalizationUtil.getXml(
				localizedValuesMap, "global");

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"update Group_ set name = ? where companyId = ? and " +
							"friendlyURL = '/global'")) {

				preparedStatement.setString(1, nameXML);
				preparedStatement.setLong(2, companyId);

				preparedStatement.executeUpdate();
			}
		}
	}

	protected void updateGroupsNames() throws Exception {
		Map<Long, String[]> companyLanguageIds = _getCompanyLanguageIds();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select Group_.companyId as companyId, ",
						"User_.languageid as companyDefaultLanguageId, ",
						"groupId, name, typeSettings from Group_ left join ",
						"User_ on Group_.companyId = User_.companyId where ",
						"User_.defaultuser = [$TRUE$] and site = [$TRUE$] and ",
						"friendlyURL != '/global'")));
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update Group_ set name = ? where groupId = ?")) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");
				String companyDefaultLanguageId = resultSet.getString(
					"companyDefaultLanguageId");
				long groupId = resultSet.getLong("groupId");
				String name = resultSet.getString("name");

				String typeSettings = resultSet.getString("typeSettings");

				UnicodeProperties typeSettingsUnicodeProperties =
					UnicodePropertiesBuilder.create(
						true
					).fastLoad(
						typeSettings
					).build();

				String defaultLanguageId = companyDefaultLanguageId;

				String[] languageIds = companyLanguageIds.getOrDefault(
					companyId, PropsValues.LOCALES_ENABLED);

				boolean inheritLocales = GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES),
					true);

				if (!inheritLocales) {
					defaultLanguageId =
						typeSettingsUnicodeProperties.getProperty(
							"languageId", defaultLanguageId);

					String typeSettingsLocales =
						typeSettingsUnicodeProperties.getProperty("locales");

					if (Validator.isNotNull(typeSettingsLocales)) {
						languageIds = StringUtil.split(typeSettingsLocales);
					}
				}

				Locale currentDefaultLocale =
					LocaleThreadLocal.getSiteDefaultLocale();

				try {
					LocaleThreadLocal.setSiteDefaultLocale(
						LocaleUtil.fromLanguageId(defaultLanguageId));

					LocalizedValuesMap localizedValuesMap =
						new LocalizedValuesMap();

					for (String languageId : languageIds) {
						localizedValuesMap.put(
							LocaleUtil.fromLanguageId(languageId), name);
					}

					String nameXML = LocalizationUtil.updateLocalization(
						localizedValuesMap.getValues(), StringPool.BLANK,
						"name", defaultLanguageId);

					preparedStatement2.setString(1, nameXML);

					preparedStatement2.setLong(2, groupId);

					preparedStatement2.addBatch();
				}
				finally {
					LocaleThreadLocal.setSiteDefaultLocale(
						currentDefaultLocale);
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private Map<Long, String[]> _getCompanyLanguageIds() throws Exception {
		Map<Long, String[]> companyLanguageIds = new HashMap<>();

		PreparedStatement preparedStatement = connection.prepareStatement(
			"select ownerId, preferences from PortalPreferences where " +
				"ownerType = " + PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		ResultSet resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			long ownerId = resultSet.getLong("ownerId");
			String preferences = resultSet.getString("preferences");

			PortalPreferencesImpl portalPreferencesImpl =
				(PortalPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
					ownerId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, preferences);

			companyLanguageIds.put(
				ownerId,
				PrefsPropsUtil.getStringArray(
					new PortalPreferencesWrapper(portalPreferencesImpl),
					PropsKeys.LOCALES, StringPool.COMMA,
					PropsValues.LOCALES_ENABLED));
		}

		return companyLanguageIds;
	}

}