/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.upgrade.v1_1_0;

import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Adolfo Pérez
 */
public class AssetVocabularyExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	public AssetVocabularyExternalReferenceCodeUpgradeProcess(
		CompanyLocalService companyLocalService,
		GroupLocalService groupLocalService, Language language) {

		_companyLocalService = companyLocalService;
		_groupLocalService = groupLocalService;
		_language = language;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Set<String> names = _getDefaultAssetVocabularyNames();

		_companyLocalService.forEachCompanyId(
			companyId -> {
				Map<Long, String> externalReferenceCodes =
					_upgradeAssetVocabularies(companyId, names);

				if (externalReferenceCodes.isEmpty()) {
					return;
				}

				_upgradeFragmentEntryLinks(companyId, externalReferenceCodes);
				_upgradePortletPreferenceValues(
					companyId, externalReferenceCodes);
				_upgradeSiteNavigationMenuItems(
					companyId, externalReferenceCodes);
			});
	}

	private Set<String> _getDefaultAssetVocabularyNames() {
		Set<String> names = new HashSet<>();

		for (Locale locale : _language.getAvailableLocales()) {
			String name = StringUtil.toLowerCase(
				StringUtil.trim(
					_language.get(
						locale, PropsValues.ASSET_VOCABULARY_DEFAULT)));

			names.add(StringUtil.replace(name, CharPool.SPACE, CharPool.DASH));
		}

		return names;
	}

	private long _getGroupId(
		long companyId, long defaultGroupId, String externalReferenceCode) {

		if (Validator.isNull(externalReferenceCode)) {
			return defaultGroupId;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group == null) {
			return defaultGroupId;
		}

		return group.getGroupId();
	}

	private String _getNewExternalReferenceCode(
		String externalReferenceCode, Map<Long, String> externalReferenceCodes,
		long groupId) {

		if (!externalReferenceCode.equals(
				externalReferenceCodes.get(groupId))) {

			return externalReferenceCode;
		}

		return AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC;
	}

	private String _getNewPortletPreferenceValue(
		long companyId, Map<Long, String> externalReferenceCodes, long groupId,
		String name, String value) {

		if (Validator.isNull(value)) {
			return value;
		}

		if (name.equals("assetVocabularyExternalReferenceCode") ||
			name.equals("assetVocabularyExternalReferenceCodes")) {

			return _getNewExternalReferenceCode(
				value, externalReferenceCodes, groupId);
		}

		if (name.startsWith(
				_ASSET_VOCABULARY_EXTERNAL_REFERENCE_CODES_PREFIX)) {

			return _getNewExternalReferenceCode(
				value, externalReferenceCodes,
				_getGroupId(
					companyId, groupId,
					name.substring(
						_ASSET_VOCABULARY_EXTERNAL_REFERENCE_CODES_PREFIX.
							length())));
		}

		if (!name.equals("groupVocabularyExternalReferenceCodes")) {
			return value;
		}

		return StringUtil.merge(
			TransformUtil.transformToList(
				StringUtil.split(value),
				groupVocabularyExternalReferenceCode -> {
					String[] parts = StringUtil.split(
						groupVocabularyExternalReferenceCode, "&&");

					if (parts.length != 2) {
						return groupVocabularyExternalReferenceCode;
					}

					String newExternalReferenceCode =
						_getNewExternalReferenceCode(
							parts[1], externalReferenceCodes,
							_getGroupId(companyId, 0, parts[0]));

					return parts[0] + "&&" + newExternalReferenceCode;
				}),
			StringPool.COMMA);
	}

	private Map<Long, String> _upgradeAssetVocabularies(
			long companyId, Set<String> names)
		throws Exception {

		Map<Long, String> externalReferenceCodes = new HashMap<>();

		Set<Long> groupIds = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from AssetVocabulary where companyId = ? and " +
					"externalReferenceCode = ?")) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setString(
				2, AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					groupIds.add(resultSet.getLong("groupId"));
				}
			}
		}

		Map<Long, Long> vocabularyIds = new HashMap<>();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, groupId, name, uuid_, ",
					"vocabularyId from AssetVocabulary where companyId = ? ",
					"and uuid_ = externalReferenceCode order by createDate, ",
					"vocabularyId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update AssetVocabulary set externalReferenceCode = ? " +
						"where ctCollectionId = ? and vocabularyId = ?")) {

			preparedStatement1.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");
					long vocabularyId = resultSet.getLong("vocabularyId");

					Long topicVocabularyId = vocabularyIds.get(groupId);

					if (topicVocabularyId == null) {
						if (groupIds.contains(groupId) ||
							!names.contains(resultSet.getString("name"))) {

							continue;
						}

						externalReferenceCodes.put(
							groupId, resultSet.getString("uuid_"));
						vocabularyIds.put(groupId, vocabularyId);
					}
					else if (topicVocabularyId != vocabularyId) {
						continue;
					}

					preparedStatement2.setString(
						1,
						AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC);
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(3, vocabularyId);

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}

		return externalReferenceCodes;
	}

	private void _upgradeFragmentEntryLinks(
			long companyId, Map<Long, String> externalReferenceCodes)
		throws Exception {

		if (!hasTable("FragmentEntryLink")) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, editableValues, ",
					"fragmentEntryLinkId, groupId from FragmentEntryLink ",
					"where companyId = ? and editableValues like ",
					"'%Vocabulary%'"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update FragmentEntryLink set editableValues = ? where " +
						"ctCollectionId = ? and fragmentEntryLinkId = ?")) {

			preparedStatement1.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					JSONObject editableValuesJSONObject =
						JSONFactoryUtil.createJSONObject(
							resultSet.getString("editableValues"));

					JSONObject jsonObject =
						editableValuesJSONObject.getJSONObject(
							"com.liferay.fragment.entry.processor.freemarker." +
								"FreeMarkerFragmentEntryProcessor");

					if (jsonObject == null) {
						continue;
					}

					boolean modified = false;

					for (String key : jsonObject.keySet()) {
						Object value = jsonObject.get(key);

						if (!(value instanceof JSONObject)) {
							continue;
						}

						JSONObject valueJSONObject = (JSONObject)value;

						String externalReferenceCode =
							valueJSONObject.getString("externalReferenceCode");

						if (!Objects.equals(
								valueJSONObject.getString(
									"categoryTreeNodeType"),
								"Vocabulary") ||
							Validator.isNull(externalReferenceCode)) {

							continue;
						}

						String newExternalReferenceCode =
							_getNewExternalReferenceCode(
								externalReferenceCode, externalReferenceCodes,
								_getGroupId(
									companyId, resultSet.getLong("groupId"),
									valueJSONObject.getString(
										"scopeExternalReferenceCode")));

						if (externalReferenceCode.equals(
								newExternalReferenceCode)) {

							continue;
						}

						valueJSONObject.put(
							"externalReferenceCode", newExternalReferenceCode);

						modified = true;
					}

					if (!modified) {
						continue;
					}

					preparedStatement2.setString(
						1, editableValuesJSONObject.toString());
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("fragmentEntryLinkId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _upgradePortletPreferenceValues(
			long companyId, Map<Long, String> externalReferenceCodes)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select PortletPreferenceValue.ctCollectionId, ",
					"Layout.groupId, PortletPreferenceValue.largeValue, ",
					"PortletPreferenceValue.name, ",
					"PortletPreferenceValue.portletPreferenceValueId, ",
					"PortletPreferenceValue.smallValue from ",
					"PortletPreferenceValue inner join PortletPreferences on ",
					"PortletPreferences.portletPreferencesId = ",
					"PortletPreferenceValue.portletPreferencesId inner join ",
					"Layout on Layout.plid = PortletPreferences.plid where ",
					"PortletPreferenceValue.companyId = ? and ",
					"(PortletPreferenceValue.name like ",
					"'assetVocabularyExternalReferenceCode%' or ",
					"PortletPreferenceValue.name = ",
					"'groupVocabularyExternalReferenceCodes')"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update PortletPreferenceValue set largeValue = ?, " +
						"smallValue = ? where ctCollectionId = ? and " +
							"portletPreferenceValueId = ?")) {

			preparedStatement1.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");
					String largeValue = resultSet.getString("largeValue");
					String name = resultSet.getString("name");
					String smallValue = resultSet.getString("smallValue");

					String newLargeValue = _getNewPortletPreferenceValue(
						companyId, externalReferenceCodes, groupId, name,
						largeValue);
					String newSmallValue = _getNewPortletPreferenceValue(
						companyId, externalReferenceCodes, groupId, name,
						smallValue);

					if (Objects.equals(largeValue, newLargeValue) &&
						Objects.equals(smallValue, newSmallValue)) {

						continue;
					}

					preparedStatement2.setString(1, newLargeValue);
					preparedStatement2.setString(2, newSmallValue);
					preparedStatement2.setLong(
						3, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						4, resultSet.getLong("portletPreferenceValueId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _upgradeSiteNavigationMenuItems(
			long companyId, Map<Long, String> externalReferenceCodes)
		throws Exception {

		if (!hasTable("SiteNavigationMenuItem")) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, groupId, ",
					"siteNavigationMenuItemId, typeSettings from ",
					"SiteNavigationMenuItem where companyId = ? and type_ = ",
					"'asset_vocabulary'"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SiteNavigationMenuItem set typeSettings = ? " +
						"where ctCollectionId = ? and " +
							"siteNavigationMenuItemId = ?")) {

			preparedStatement1.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					UnicodeProperties typeSettingsUnicodeProperties =
						UnicodePropertiesBuilder.fastLoad(
							resultSet.getString("typeSettings")
						).build();

					String externalReferenceCode =
						typeSettingsUnicodeProperties.get(
							"externalReferenceCode");

					if (Validator.isNull(externalReferenceCode)) {
						continue;
					}

					String newExternalReferenceCode =
						_getNewExternalReferenceCode(
							externalReferenceCode, externalReferenceCodes,
							_getGroupId(
								companyId, resultSet.getLong("groupId"),
								typeSettingsUnicodeProperties.get(
									"scopeExternalReferenceCode")));

					if (externalReferenceCode.equals(
							newExternalReferenceCode)) {

						continue;
					}

					typeSettingsUnicodeProperties.put(
						"externalReferenceCode", newExternalReferenceCode);

					preparedStatement2.setString(
						1, typeSettingsUnicodeProperties.toString());
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("siteNavigationMenuItemId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final String
		_ASSET_VOCABULARY_EXTERNAL_REFERENCE_CODES_PREFIX =
			"assetVocabularyExternalReferenceCodes_";

	private final CompanyLocalService _companyLocalService;
	private final GroupLocalService _groupLocalService;
	private final Language _language;

}