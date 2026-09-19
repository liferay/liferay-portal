/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v0_0_3;

import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleTypeUpgradeProcess extends UpgradeProcess {

	public JournalArticleTypeUpgradeProcess(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetEntryAssetCategoryRelLocalService
			assetEntryAssetCategoryRelLocalService,
		AssetEntryLocalService assetEntryLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		CompanyLocalService companyLocalService,
		UserLocalService userLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetEntryAssetCategoryRelLocalService =
			assetEntryAssetCategoryRelLocalService;
		_assetEntryLocalService = assetEntryLocalService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_companyLocalService = companyLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateArticleType();

		_alterTable();
	}

	private AssetCategory _addAssetCategory(
			long groupId, long companyId, String title, long assetVocabularyId)
		throws Exception {

		long userId = _userLocalService.getGuestUserId(companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		return _assetCategoryLocalService.addCategory(
			userId, groupId, title, assetVocabularyId, serviceContext);
	}

	private AssetVocabulary _addAssetVocabulary(
			long groupId, long companyId, String title,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap)
		throws Exception {

		long userId = _userLocalService.getGuestUserId(companyId);

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			new long[] {
				PortalUtil.getClassNameId(JournalArticle.class.getName())
			},
			new long[] {-1}, new boolean[] {false});
		assetVocabularySettingsHelper.setMultiValued(false);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		return _assetVocabularyLocalService.addVocabulary(
			userId, groupId, title, nameMap, descriptionMap,
			assetVocabularySettingsHelper.toString(), serviceContext);
	}

	private void _alterTable() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			alterTableDropColumn("JournalArticle", "type_");
		}
	}

	private Set<String> _getArticleTypes() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select distinct type_ from JournalArticle");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			Set<String> types = new HashSet<>();

			while (resultSet.next()) {
				types.add(StringUtil.toLowerCase(resultSet.getString("type_")));
			}

			return types;
		}
	}

	private boolean _hasSelectedArticleTypes() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count from JournalArticle where type_ != " +
					"'general'");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				if (resultSet.getLong("count") > 0) {
					return true;
				}
			}

			return false;
		}
	}

	private void _updateArticleType() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			if (!_hasSelectedArticleTypes()) {
				return;
			}

			Set<String> types = _getArticleTypes();

			if (types.size() <= 0) {
				return;
			}

			Locale localeThreadLocalDefaultLocale =
				LocaleThreadLocal.getDefaultLocale();

			try {
				_companyLocalService.forEachCompany(
					company -> {
						LocaleThreadLocal.setDefaultLocale(company.getLocale());

						Set<Locale> locales = LanguageUtil.getAvailableLocales(
							company.getGroupId());

						Locale defaultLocale = LocaleUtil.fromLanguageId(
							UpgradeProcessUtil.getDefaultLanguageId(
								company.getCompanyId()));

						Map<Locale, String> nameMap =
							LocalizationUtil.getLocalizationMap(
								locales, defaultLocale, "type");

						AssetVocabulary assetVocabulary = _addAssetVocabulary(
							company.getGroupId(), company.getCompanyId(),
							"type", nameMap, new HashMap<Locale, String>());

						Map<String, Long>
							journalArticleTypesToAssetCategoryIds =
								new HashMap<>();

						for (String type : types) {
							AssetCategory assetCategory = _addAssetCategory(
								company.getGroupId(), company.getCompanyId(),
								type, assetVocabulary.getVocabularyId());

							journalArticleTypesToAssetCategoryIds.put(
								type, assetCategory.getCategoryId());
						}

						_updateArticles(
							company.getCompanyId(),
							journalArticleTypesToAssetCategoryIds);
					});
			}
			finally {
				LocaleThreadLocal.setDefaultLocale(
					localeThreadLocalDefaultLocale);
			}
		}
	}

	private void _updateArticles(
			long companyId,
			Map<String, Long> journalArticleTypesToAssetCategoryIds)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select JournalArticle.resourcePrimKey, ",
					"JournalArticle.type_ from JournalArticle left join ",
					"JournalArticle tempJournalArticle on ",
					"(JournalArticle.groupId = tempJournalArticle.groupId) ",
					"and (JournalArticle.articleId = ",
					"tempJournalArticle.articleId) and ",
					"(JournalArticle.version < tempJournalArticle.version) ",
					"where JournalArticle.companyId = ? and ",
					"tempJournalArticle.id_ is null"))) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long resourcePrimKey = resultSet.getLong("resourcePrimKey");

					AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
						JournalArticle.class.getName(), resourcePrimKey);

					if (assetEntry == null) {
						continue;
					}

					String type = StringUtil.toLowerCase(
						resultSet.getString("type_"));

					long assetCategoryId =
						journalArticleTypesToAssetCategoryIds.get(type);

					_assetEntryAssetCategoryRelLocalService.
						addAssetEntryAssetCategoryRel(
							assetEntry.getEntryId(), assetCategoryId);
				}
			}
		}
	}

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;
	private final AssetEntryLocalService _assetEntryLocalService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final CompanyLocalService _companyLocalService;
	private final UserLocalService _userLocalService;

}