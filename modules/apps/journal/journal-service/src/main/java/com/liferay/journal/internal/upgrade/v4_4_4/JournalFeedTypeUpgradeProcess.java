/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v4_4_4;

import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFeed;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Georgel Pop
 */
public class JournalFeedTypeUpgradeProcess extends UpgradeProcess {

	public JournalFeedTypeUpgradeProcess(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetEntryAssetCategoryRelLocalService
			assetEntryAssetCategoryRelLocalService,
		AssetEntryLocalService assetEntryLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		CompanyLocalService companyLocalService, Language language,
		Localization localization, Portal portal,
		UserLocalService userLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetEntryAssetCategoryRelLocalService =
			assetEntryAssetCategoryRelLocalService;
		_assetEntryLocalService = assetEntryLocalService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_companyLocalService = companyLocalService;
		_language = language;
		_localization = localization;
		_portal = portal;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (hasColumn("JournalFeed", "type_")) {
			_addJournalFeedTypesAndAssetEntries();
		}
		else {
			_addJournalFeedAssetEntries();
		}
	}

	private void _addAssetEntries(long classNameId, Long companyId)
		throws Exception {

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select uuid_, id_, groupId, userId, createDate, " +
					"modifiedDate, name, description from JournalFeed where " +
						"companyId = ?")) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					_addAssetEntry(0, classNameId, guestUserId, resultSet);
				}
			}
		}
	}

	private void _addAssetEntry(
			long assetCategoryId, long classNameId, long guestUserId,
			ResultSet resultSet)
		throws Exception {

		long id = resultSet.getLong("id_");

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			classNameId, id);

		if (assetEntry != null) {
			return;
		}

		long userId = resultSet.getLong("userId");
		Date createDate = resultSet.getDate("createDate");

		if (_userLocalService.fetchUser(userId) == null) {
			userId = guestUserId;
		}

		assetEntry = _assetEntryLocalService.updateEntry(
			userId, resultSet.getLong("groupId"),
			resultSet.getDate("createDate"), resultSet.getDate("modifiedDate"),
			JournalFeed.class.getName(), id, resultSet.getString("uuid_"), 0,
			new long[0], new String[0], true, true, null, null, createDate,
			null, ContentTypes.TEXT_PLAIN, resultSet.getString("name"),
			resultSet.getString("description"), null, null, null, 0, 0, 0.0);

		if (assetCategoryId > 0) {
			_assetEntryAssetCategoryRelLocalService.
				addAssetEntryAssetCategoryRel(
					assetEntry.getEntryId(), assetCategoryId);
		}
	}

	private void _addJournalFeedAssetEntries() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			long classNameId = _portal.getClassNameId(
				JournalFeed.class.getName());

			_companyLocalService.forEachCompanyId(
				companyId -> _addAssetEntries(classNameId, companyId));
		}
	}

	private void _addJournalFeedAssetEntries(
			long classNameId, long companyId, long guestUserId,
			Map<String, Long> journalFeedTypesMap)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select uuid_, id_, groupId, userId, createDate, " +
					"modifiedDate, name, description, type_ from JournalFeed " +
						"where companyId = ?")) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long assetCategoryId = 0;

					String type = StringUtil.toLowerCase(
						resultSet.getString("type_"));

					if (Validator.isNotNull(type) &&
						journalFeedTypesMap.containsKey(type)) {

						assetCategoryId = journalFeedTypesMap.get(type);
					}

					_addAssetEntry(
						assetCategoryId, classNameId, guestUserId, resultSet);
				}
			}
		}
	}

	private void _addJournalFeedTypesAndAssetEntries() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			Locale localeThreadLocalDefaultLocale =
				LocaleThreadLocal.getDefaultLocale();

			long journalArticleClassNameId = _portal.getClassNameId(
				JournalArticle.class.getName());
			long journalFeedClassNameId = _portal.getClassNameId(
				JournalFeed.class.getName());

			try {
				_companyLocalService.forEachCompany(
					company -> {
						Set<String> journalFeedTypes = _getJournalFeedTypes(
							company.getCompanyId());

						if (SetUtil.isEmpty(journalFeedTypes)) {
							_addAssetEntries(
								journalFeedClassNameId, company.getCompanyId());

							return;
						}

						LocaleThreadLocal.setDefaultLocale(company.getLocale());

						long userId = _userLocalService.getGuestUserId(
							company.getCompanyId());

						Locale defaultLocale = LocaleUtil.fromLanguageId(
							UpgradeProcessUtil.getDefaultLanguageId(
								company.getCompanyId()));
						String assetVocabularyTitle = "type";

						Map<Locale, String> assetVocabularyTitleMap =
							_localization.getLocalizationMap(
								_language.getAvailableLocales(
									company.getGroupId()),
								defaultLocale, assetVocabularyTitle);

						ServiceContext serviceContext = new ServiceContext();

						serviceContext.setAddGroupPermissions(true);
						serviceContext.setAddGuestPermissions(true);

						AssetVocabulary assetVocabulary =
							_assetVocabularyLocalService.fetchGroupVocabulary(
								company.getGroupId(),
								assetVocabularyTitleMap.get(defaultLocale));

						if (assetVocabulary == null) {
							AssetVocabularySettingsHelper
								assetVocabularySettingsHelper =
									new AssetVocabularySettingsHelper();

							assetVocabularySettingsHelper.
								setClassNameIdsAndClassTypePKs(
									new long[] {
										journalArticleClassNameId,
										journalFeedClassNameId
									},
									new long[] {-1, -1},
									new boolean[] {false, false});
							assetVocabularySettingsHelper.setMultiValued(false);

							assetVocabulary =
								_assetVocabularyLocalService.addVocabulary(
									userId, company.getGroupId(),
									assetVocabularyTitle,
									assetVocabularyTitleMap,
									Collections.emptyMap(),
									assetVocabularySettingsHelper.toString(),
									serviceContext);
						}
						else {
							assetVocabulary = _updateAssetVocabulary(
								assetVocabulary, journalFeedClassNameId);
						}

						Map<String, Long> journalFeedTypesMap = new HashMap<>();

						for (String type : journalFeedTypes) {
							journalFeedTypesMap.put(
								type,
								_getAssetCategoryId(
									assetVocabulary, type, serviceContext,
									userId));
						}

						_addJournalFeedAssetEntries(
							journalFeedClassNameId, company.getCompanyId(),
							userId, journalFeedTypesMap);
					});
			}
			finally {
				LocaleThreadLocal.setDefaultLocale(
					localeThreadLocalDefaultLocale);
			}
		}
	}

	private long _getAssetCategoryId(
			AssetVocabulary assetVocabulary, String name,
			ServiceContext serviceContext, long userId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select categoryId from AssetCategory where name = ? and " +
					"vocabularyId = ?")) {

			preparedStatement.setString(1, name);
			preparedStatement.setLong(2, assetVocabulary.getVocabularyId());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("categoryId");
				}
			}
		}

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			userId, assetVocabulary.getGroupId(), name,
			assetVocabulary.getVocabularyId(), serviceContext);

		return assetCategory.getCategoryId();
	}

	private Set<String> _getJournalFeedTypes(long companyId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select distinct type_ from JournalFeed where companyId = ? " +
					"and type_ != 'general'")) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Set<String> types = new HashSet<>();

				while (resultSet.next()) {
					String type = StringUtil.toLowerCase(
						resultSet.getString("type_"));

					if (Validator.isNotNull(type)) {
						types.add(type);
					}
				}

				return types;
			}
		}
	}

	private AssetVocabulary _updateAssetVocabulary(
			AssetVocabulary assetVocabulary, long journalFeedClassNameId)
		throws Exception {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper(assetVocabulary.getSettings());

		long[] selectedClassNameIds =
			assetVocabularySettingsHelper.getClassNameIds();

		if (ArrayUtil.contains(selectedClassNameIds, 0) ||
			ArrayUtil.contains(selectedClassNameIds, journalFeedClassNameId)) {

			return assetVocabulary;
		}

		long[] requiredClassNameIds =
			assetVocabularySettingsHelper.getRequiredClassNameIds();

		selectedClassNameIds = ArrayUtil.append(
			selectedClassNameIds, journalFeedClassNameId);

		boolean[] requireds = new boolean[selectedClassNameIds.length];

		for (int i = 0; i < selectedClassNameIds.length; i++) {
			if (ArrayUtil.contains(
					requiredClassNameIds, selectedClassNameIds[i])) {

				requireds[i] = true;
			}
			else {
				requireds[i] = false;
			}
		}

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			selectedClassNameIds,
			ArrayUtil.append(
				assetVocabularySettingsHelper.getClassTypePKs(), -1),
			requireds);

		return _assetVocabularyLocalService.updateVocabulary(
			assetVocabulary.getVocabularyId(), assetVocabulary.getTitleMap(),
			assetVocabulary.getDescriptionMap(),
			assetVocabularySettingsHelper.toString());
	}

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;
	private final AssetEntryLocalService _assetEntryLocalService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final CompanyLocalService _companyLocalService;
	private final Language _language;
	private final Localization _localization;
	private final Portal _portal;
	private final UserLocalService _userLocalService;

}