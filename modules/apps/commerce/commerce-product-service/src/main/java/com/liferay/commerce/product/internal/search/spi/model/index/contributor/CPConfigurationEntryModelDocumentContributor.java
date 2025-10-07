/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.index.contributor;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPConfigurationEntry",
	service = ModelDocumentContributor.class
)
public class CPConfigurationEntryModelDocumentContributor
	implements ModelDocumentContributor<CPConfigurationEntry> {

	@Override
	public void contribute(
		Document document, CPConfigurationEntry cpConfigurationEntry) {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Indexing commerce product configuration entry " +
						cpConfigurationEntry);
			}

			document.addKeyword(
				CPField.CP_CONFIGURATION_LIST_ID,
				cpConfigurationEntry.getCPConfigurationListId());

			long[] cpConfigurationListIds = _getCPConfigurationListIds(
				cpConfigurationEntry);

			if (ArrayUtil.isEmpty(cpConfigurationListIds)) {
				document.addNumber(CPField.CP_CONFIGURATION_LIST_IDS, -1);
			}
			else {
				document.addNumber(
					CPField.CP_CONFIGURATION_LIST_IDS, cpConfigurationListIds);
			}

			document.addKeyword(
				CPField.EXTERNAL_REFERENCE_CODE,
				cpConfigurationEntry.getExternalReferenceCode());
			document.addNumber(
				CPField.MAXIMUM_ORDER_QUANTITY,
				cpConfigurationEntry.getMaxOrderQuantity());
			document.addNumber(
				CPField.MINIMUM_ORDER_QUANTITY,
				cpConfigurationEntry.getMinOrderQuantity());
			document.addNumber(
				CPField.MULTIPLE_ORDER_QUANTITY,
				cpConfigurationEntry.getMultipleOrderQuantity());
			document.addKeyword(
				CPField.PURCHASABLE, cpConfigurationEntry.isPurchasable());
			document.addKeyword(
				CPField.SHIPPABLE, cpConfigurationEntry.isShippable());
			document.addText(
				Field.CLASS_NAME_ID,
				String.valueOf(cpConfigurationEntry.getClassNameId()));
			document.addKeyword(
				Field.CLASS_PK,
				cpConfigurationEntry.getCPConfigurationEntryId());
			document.addKeyword(
				Field.ENTRY_CLASS_PK, cpConfigurationEntry.getClassPK());

			CPDefinition cpDefinition = null;

			if (StringUtil.equalsIgnoreCase(
					CPDefinition.class.getName(),
					cpConfigurationEntry.getClassName())) {

				cpDefinition = _cpDefinitionLocalService.getCPDefinition(
					cpConfigurationEntry.getClassPK());
			}
			else if (StringUtil.equalsIgnoreCase(
						CPInstance.class.getName(),
						cpConfigurationEntry.getClassName())) {

				CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
					cpConfigurationEntry.getClassPK());

				cpDefinition = cpInstance.getCPDefinition();
			}

			if (cpDefinition != null) {
				document.addKeyword(
					CPField.ASSET_CATEGORY_NAMES,
					TransformUtil.unsafeTransform(
						_assetCategoryLocalService.getCategoryNames(
							CPDefinition.class.getName(),
							cpConfigurationEntry.getClassPK()),
						String::toLowerCase, String.class));
				document.addKeyword(
					CPField.PRODUCT_TYPE_NAME,
					cpDefinition.getProductTypeName(), true);
				document.addKeyword(
					Field.ASSET_CATEGORY_IDS,
					_assetCategoryLocalService.getCategoryIds(
						CPDefinition.class.getName(),
						cpDefinition.getCPDefinitionId()));
				document.addKeyword(
					Field.NAME,
					cpDefinition.getName(
						_localization.getDefaultLanguageId(
							cpDefinition.getName())),
					true);

				List<String> languageIds =
					_cpDefinitionLocalService.
						getCPDefinitionLocalizationLanguageIds(
							cpDefinition.getCPDefinitionId());

				for (String languageId : languageIds) {
					document.addKeyword(
						_localization.getLocalizedName(Field.NAME, languageId),
						cpDefinition.getName(languageId), true);
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Commerce product configuration entry " +
						cpConfigurationEntry + " indexed successfully");
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index commerce product configuration entry " +
						cpConfigurationEntry,
					exception);
			}
		}
	}

	private long[] _getCPConfigurationListIds(
		CPConfigurationEntry cpConfigurationEntry) {

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		if (cpConfigurationEntrySetting == null) {
			return null;
		}

		return TransformUtil.transformToLongArray(
			StringUtil.split(cpConfigurationEntrySetting.getValue()),
			Long::valueOf);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationEntryModelDocumentContributor.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Localization _localization;

}