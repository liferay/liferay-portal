/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.upgrade.v3_0_0;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

/**
 * @author Joao Victor Alves
 */
public class SiteNavigationMenuItemExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	public SiteNavigationMenuItemExternalReferenceCodeUpgradeProcess(
		AssetVocabularyLocalService assetVocabularyLocalService,
		JournalArticleLocalService journalArticleLocalService,
		KBArticleLocalService kbArticleLocalService,
		LayoutLocalService layoutLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
		_journalArticleLocalService = journalArticleLocalService;
		_kbArticleLocalService = kbArticleLocalService;
		_layoutLocalService = layoutLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, siteNavigationMenuItemId, type_, " +
					"typeSettings from SiteNavigationMenuItem");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SiteNavigationMenuItem set typeSettings = ? " +
						"where ctCollectionId = ? and " +
							"siteNavigationMenuItemId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				_upgradeSiteNavigationMenuItem(preparedStatement2, resultSet);
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _upgradeSiteNavigationMenuItem(
			PreparedStatement preparedStatement2, ResultSet resultSet)
		throws Exception {

		PersistedModel persistedModel = null;

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				resultSet.getString("typeSettings")
			).build();

		String className = typeSettingsUnicodeProperties.getProperty(
			"className");

		String type = resultSet.getString("type_");

		if (Objects.equals(
				type, SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY)) {

			persistedModel =
				_assetVocabularyLocalService.
					fetchAssetVocabularyByUuidAndGroupId(
						typeSettingsUnicodeProperties.getProperty("uuid"),
						GetterUtil.getLong(
							typeSettingsUnicodeProperties.getProperty(
								"groupId")));
		}
		else if (Objects.equals(type, JournalArticle.class.getName())) {
			persistedModel = _journalArticleLocalService.getLatestArticle(
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.getProperty("classPK")));
		}
		else if (Objects.equals(type, KBArticle.class.getName())) {
			persistedModel = _kbArticleLocalService.getLatestKBArticle(
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.getProperty("classPK")));
		}
		else if (Objects.equals(
					type, SiteNavigationMenuItemTypeConstants.LAYOUT)) {

			persistedModel = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				typeSettingsUnicodeProperties.getProperty("layoutUuid"),
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.getProperty("groupId")),
				GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						"privateLayout")));
		}
		else {
			if (className == null) {
				return;
			}

			if (className.equals(FileEntry.class.getName())) {
				className = DLFileEntry.class.getName();
			}
			else if (className.contains(ObjectDefinition.class.getName())) {
				className = ObjectEntry.class.getName();
			}

			PersistedModelLocalService persistedModelLocalService =
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(className);

			persistedModel = persistedModelLocalService.getPersistedModel(
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.getProperty("classPK")));
		}

		if (persistedModel == null) {
			return;
		}

		String externalReferenceCode = null;

		if (Objects.equals(
				className, "com.liferay.commerce.product.model.CPDefinition")) {

			String sql = StringBundler.concat(
				"select CProduct.externalReferenceCode from CProduct inner ",
				"join CPDefinition on CProduct.CProductId = CPDefinition.",
				"CProductId where CPDefinition.cpDefinitionId = ",
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.getProperty("classPK")));

			try (PreparedStatement preparedStatement3 =
					connection.prepareStatement(sql);
				ResultSet resultSet3 = preparedStatement3.executeQuery()) {

				if (resultSet3.next()) {
					externalReferenceCode = resultSet3.getString(
						"externalReferenceCode");
				}
			}
		}
		else if (persistedModel instanceof ExternalReferenceCodeModel) {
			ExternalReferenceCodeModel externalReferenceCodeModel =
				(ExternalReferenceCodeModel)persistedModel;

			externalReferenceCode =
				externalReferenceCodeModel.getExternalReferenceCode();
		}

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		typeSettingsUnicodeProperties.setProperty(
			"externalReferenceCode", externalReferenceCode);

		preparedStatement2.setString(
			1, typeSettingsUnicodeProperties.toString());
		preparedStatement2.setLong(2, resultSet.getLong("ctCollectionId"));
		preparedStatement2.setLong(
			3, resultSet.getLong("siteNavigationMenuItemId"));

		preparedStatement2.addBatch();
	}

	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final JournalArticleLocalService _journalArticleLocalService;
	private final KBArticleLocalService _kbArticleLocalService;
	private final LayoutLocalService _layoutLocalService;

}