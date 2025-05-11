/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.index.contributor;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.links.CPDefinitionLinkTypeRegistry;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPDefinition",
	service = ModelDocumentContributor.class
)
public class CPDefinitionModelDocumentContributor
	implements ModelDocumentContributor<CPDefinition> {

	@Override
	public void contribute(Document document, CPDefinition cpDefinition) {
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Indexing commerce product definition " + cpDefinition);
			}

			String cpDefinitionDefaultLanguageId =
				_localization.getDefaultLanguageId(cpDefinition.getName());

			long classNameId = _classNameLocalService.getClassNameId(
				CProduct.class);

			Map<String, String> languageIdToUrlTitleMap = new HashMap<>();

			try {
				FriendlyURLEntry friendlyURLEntry =
					_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
						classNameId, cpDefinition.getCProductId());

				languageIdToUrlTitleMap =
					friendlyURLEntry.getLanguageIdToUrlTitleMap();
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			document.addKeyword(
				CPField.ACCOUNT_GROUP_FILTER_ENABLED,
				cpDefinition.isAccountGroupFilterEnabled());
			document.addKeyword(
				CPField.ASSET_CATEGORY_NAMES,
				TransformUtil.unsafeTransform(
					_assetCategoryLocalService.getCategoryNames(
						CPDefinition.class.getName(),
						cpDefinition.getCPDefinitionId()),
					String::toLowerCase, String.class));

			BigDecimal basePrice = _getBasePrice(cpDefinition.getCPInstances());

			if (basePrice != null) {
				document.addNumber(CPField.BASE_PRICE, basePrice);
			}

			document.addKeyword(
				CPField.CHANNEL_FILTER_ENABLED,
				cpDefinition.isChannelFilterEnabled());
			document.addNumber(
				CPField.COMMERCE_CHANNEL_GROUP_IDS,
				TransformUtil.transformToLongArray(
					_commerceChannelRelLocalService.getCommerceChannelRels(
						cpDefinition.getModelClassName(),
						cpDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null),
					commerceChannelRel -> {
						CommerceChannel commerceChannel =
							commerceChannelRel.getCommerceChannel();

						return commerceChannel.getGroupId();
					}));

			document.addKeyword(
				CPField.CP_CONFIGURATION_LIST_IDS,
				_getCPConfigurationListIds(cpDefinition.getCPDefinitionId()));

			long cpAttachmentFileEntryId = 0;

			CPAttachmentFileEntry cpAttachmentFileEntry =
				_cpDefinitionLocalService.getDefaultImageCPAttachmentFileEntry(
					cpDefinition.getCPDefinitionId());

			if (cpAttachmentFileEntry != null) {
				document.addNumber(
					CPField.DEFAULT_IMAGE_FILE_ENTRY_ID,
					cpAttachmentFileEntry.getFileEntryId());

				cpAttachmentFileEntryId =
					cpAttachmentFileEntry.getCPAttachmentFileEntryId();
			}

			if (cpAttachmentFileEntryId == 0) {
				document.addKeyword(
					CPField.DEFAULT_IMAGE_FILE_URL,
					_commerceMediaResolver.getDefaultURL(
						cpDefinition.getGroupId()));
			}
			else {
				document.addKeyword(
					CPField.DEFAULT_IMAGE_FILE_URL,
					_commerceMediaResolver.getURL(
						AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
						cpAttachmentFileEntryId, false, false, false));
			}

			document.addNumber(CPField.DEPTH, cpDefinition.getDepth());
			document.addDateSortable(
				CPField.DISPLAY_DATE, cpDefinition.getDisplayDate());

			CProduct cProduct = cpDefinition.getCProduct();

			document.addKeyword(
				CPField.EXTERNAL_REFERENCE_CODE,
				cProduct.getExternalReferenceCode(), true);

			document.addKeyword(
				CPField.GTINS,
				TransformUtil.transformToArray(
					cpDefinition.getCPInstances(), CPInstance::getGtin,
					String.class));
			document.addNumber(CPField.HEIGHT, cpDefinition.getHeight());
			document.addKeyword(
				CPField.IS_IGNORE_SKU_COMBINATIONS,
				cpDefinition.isIgnoreSKUCombinations());
			document.addText(
				CPField.META_DESCRIPTION,
				cpDefinition.getMetaDescription(cpDefinitionDefaultLanguageId));
			document.addText(
				CPField.META_KEYWORDS,
				cpDefinition.getMetaKeywords(cpDefinitionDefaultLanguageId));
			document.addText(
				CPField.META_TITLE,
				cpDefinition.getMetaTitle(cpDefinitionDefaultLanguageId));

			List<CPDefinitionOptionRel> cpDefinitionOptionRels =
				cpDefinition.getCPDefinitionOptionRels();

			document.addNumber(
				CPField.OPTION_IDS,
				TransformUtil.transformToArray(
					_getCPOptions(cpDefinitionOptionRels),
					CPOption::getCPOptionId, Long.class));
			document.addText(
				CPField.OPTION_NAMES,
				TransformUtil.transformToArray(
					_getCPOptions(cpDefinitionOptionRels), CPOption::getKey,
					String.class));

			document.addKeyword(
				CPField.PRODUCT_ID, cpDefinition.getCProductId());
			document.addKeyword(
				CPField.PRODUCT_TYPE_NAME, cpDefinition.getProductTypeName(),
				true);
			document.addKeyword(CPField.PUBLISHED, cpDefinition.isPublished());
			document.addText(
				CPField.SHORT_DESCRIPTION,
				cpDefinition.getShortDescription(
					cpDefinitionDefaultLanguageId));

			List<CPDefinitionSpecificationOptionValue>
				cpDefinitionSpecificationOptionValues =
					_getFilteredCPDefinitionSpecificationOptionValues(
						_cpDefinitionSpecificationOptionValueLocalService.
							getCPDefinitionSpecificationOptionValues(
								cpDefinition.getCPDefinitionId(), null,
								QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

			document.addNumber(
				CPField.SPECIFICATION_IDS,
				TransformUtil.transformToArray(
					cpDefinitionSpecificationOptionValues,
					CPDefinitionSpecificationOptionValue::
						getCPSpecificationOptionId,
					Long.class));
			document.addText(
				CPField.SPECIFICATION_NAMES,
				TransformUtil.transformToArray(
					cpDefinitionSpecificationOptionValues,
					CPDefinitionSpecificationOptionValue ->
						_getCPSpecificationOptionKey(
							CPDefinitionSpecificationOptionValue.
								getCPSpecificationOption()),
					String.class));
			document.addKeyword(
				CPField.SPECIFICATION_VALUES_NAMES,
				TransformUtil.transformToArray(
					_getFilteredCPDefinitionSpecificationOptionValues(
						cpDefinitionSpecificationOptionValues),
					CPDefinitionSpecificationOptionValue ->
						CPDefinitionSpecificationOptionValue.getValue(
							cpDefinitionDefaultLanguageId),
					String.class));

			document.addText(
				CPField.SKUS,
				_cpInstanceLocalService.getSKUs(
					cpDefinition.getCPDefinitionId()));
			document.addKeyword(
				CPField.SUBSCRIPTION_ENABLED,
				cpDefinition.isSubscriptionEnabled());

			List<String> languageIds =
				_cpDefinitionLocalService.
					getCPDefinitionLocalizationLanguageIds(
						cpDefinition.getCPDefinitionId());

			for (String languageId : languageIds) {
				String description = cpDefinition.getDescription(languageId);
				String metaDescription = cpDefinition.getMetaDescription(
					languageId);
				String metaKeywords = cpDefinition.getMetaKeywords(languageId);
				String metaTitle = cpDefinition.getMetaTitle(languageId);
				String name = cpDefinition.getName(languageId);
				String shortDescription = cpDefinition.getShortDescription(
					languageId);
				String urlTitle = languageIdToUrlTitleMap.get(languageId);

				document.addText(
					_localization.getLocalizedName(
						CPField.META_DESCRIPTION, languageId),
					metaDescription);
				document.addText(
					_localization.getLocalizedName(
						CPField.META_KEYWORDS, languageId),
					metaKeywords);
				document.addText(
					_localization.getLocalizedName(
						CPField.META_TITLE, languageId),
					metaTitle);
				document.addText(
					_localization.getLocalizedName(
						CPField.SHORT_DESCRIPTION, languageId),
					shortDescription);
				document.addText(Field.CONTENT, description);
				document.addText(
					_localization.getLocalizedName(
						Field.DESCRIPTION, languageId),
					description);
				document.addText(
					_localization.getLocalizedName(Field.NAME, languageId),
					name);
				document.addText(
					_localization.getLocalizedName(Field.URL, languageId),
					urlTitle);
			}

			document.addText(
				Field.DESCRIPTION,
				_htmlParser.extractText(
					cpDefinition.getDescription(
						cpDefinitionDefaultLanguageId)));
			document.addKeyword(
				Field.HIDDEN, _isHidden(cpDefinition, cProduct));
			document.addText(
				Field.NAME,
				cpDefinition.getName(cpDefinitionDefaultLanguageId));
			document.addText(
				Field.URL,
				languageIdToUrlTitleMap.get(cpDefinitionDefaultLanguageId));

			document.addNumber(
				"commerceAccountGroupIds",
				TransformUtil.transformToLongArray(
					_accountGroupRelLocalService.getAccountGroupRels(
						CPDefinition.class.getName(),
						cpDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null),
					AccountGroupRel::getAccountGroupId));

			CommerceCatalog commerceCatalog = cpDefinition.getCommerceCatalog();

			if (commerceCatalog != null) {
				document.addKeyword(
					"commerceCatalogId",
					commerceCatalog.getCommerceCatalogId());
			}

			document.addText(
				"defaultLanguageId", cpDefinitionDefaultLanguageId);

			for (CPDefinitionOptionRel cpDefinitionOptionRel :
					cpDefinitionOptionRels) {

				if (!cpDefinitionOptionRel.isFacetable()) {
					continue;
				}

				CPOption cpOption = cpDefinitionOptionRel.getCPOption();

				List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
					cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

				List<String> optionValueIds = new ArrayList<>();

				Set<Locale> availableLocales = _language.getAvailableLocales(
					cpDefinitionOptionRel.getGroupId());

				for (Locale locale : availableLocales) {
					String languageId = _language.getLanguageId(locale);

					List<String> localizedOptionValues = new ArrayList<>();

					for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
							cpDefinitionOptionValueRels) {

						optionValueIds.add(cpDefinitionOptionValueRel.getKey());

						String localizedOptionValue =
							cpDefinitionOptionValueRel.getName(languageId);

						if (Validator.isBlank(localizedOptionValue)) {
							localizedOptionValue =
								cpDefinitionOptionValueRel.getName(
									cpDefinitionDefaultLanguageId);
						}

						localizedOptionValues.add(localizedOptionValue);
					}

					document.addText(
						StringBundler.concat(
							languageId, "_ATTRIBUTE_", cpOption.getKey(),
							"_VALUES_NAMES"),
						ArrayUtil.toStringArray(localizedOptionValues));
				}

				document.addText(
					"ATTRIBUTE_" + cpOption.getKey() + "_VALUES_IDS",
					ArrayUtil.toStringArray(optionValueIds));
			}

			for (CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue :
						cpDefinitionSpecificationOptionValues) {

				CPSpecificationOption cpSpecificationOption =
					cpDefinitionSpecificationOptionValue.
						getCPSpecificationOption();

				String specificationOptionValue =
					cpDefinitionSpecificationOptionValue.getValue(
						cpDefinitionDefaultLanguageId);

				Set<Locale> availableLocales = _language.getAvailableLocales(
					cpDefinitionSpecificationOptionValue.getGroupId());

				for (Locale locale : availableLocales) {
					String languageId = _language.getLanguageId(locale);

					String localizedSpecificationOptionValue =
						cpDefinitionSpecificationOptionValue.getValue(
							languageId);

					if (Validator.isBlank(localizedSpecificationOptionValue)) {
						localizedSpecificationOptionValue =
							specificationOptionValue;
					}

					String localeSpecificationValueName = StringBundler.concat(
						languageId, "_SPECIFICATION_",
						cpSpecificationOption.getKey(), "_VALUE_NAME");

					Field field = document.getField(
						localeSpecificationValueName);

					if (field != null) {
						String[] currentValues = field.getValues();

						List<String> valuesArrayList = new ArrayList<>(
							Arrays.asList(currentValues));

						valuesArrayList.add(localizedSpecificationOptionValue);

						String[] valuesArray = valuesArrayList.toArray(
							new String[0]);

						document.addText(
							localeSpecificationValueName, valuesArray);
					}
					else {
						document.addText(
							localeSpecificationValueName,
							localizedSpecificationOptionValue);
					}
				}

				String specificationValueName =
					"SPECIFICATION_" + cpSpecificationOption.getKey() +
						"_VALUE_NAME";

				Field field = document.getField(specificationValueName);

				if (field != null) {
					String[] currentValues = field.getValues();

					List<String> valuesArrayList = new ArrayList<>(
						Arrays.asList(currentValues));

					valuesArrayList.add(specificationOptionValue);

					String[] valuesArray = valuesArrayList.toArray(
						new String[0]);

					document.addText(specificationValueName, valuesArray);
				}
				else {
					document.addText(
						specificationValueName, specificationOptionValue);
				}

				String specificationValueId =
					"SPECIFICATION_" + cpSpecificationOption.getKey() +
						"_VALUE_ID";

				long cpDefinitionSpecificationOptionValueId =
					cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId();

				field = document.getField(specificationValueId);

				if (field != null) {
					String[] currentValues = field.getValues();

					List<String> valuesArrayList = new ArrayList<>(
						Arrays.asList(currentValues));

					valuesArrayList.add(
						String.valueOf(cpDefinitionSpecificationOptionValueId));

					String[] valuesArray = valuesArrayList.toArray(
						new String[0]);

					document.addNumber(specificationValueId, valuesArray);
				}
				else {
					document.addNumber(
						specificationValueId,
						cpDefinitionSpecificationOptionValueId);
				}
			}

			List<String> types = _cpDefinitionLinkTypeRegistry.getTypes();

			for (String type : types) {
				if (Validator.isNull(type)) {
					continue;
				}

				String[] linkedProductIds = _getReverseCPDefinitionIds(
					cProduct.getCProductId(), type);

				document.addKeyword(type, linkedProductIds);
			}

			_expandoBridgeIndexer.addAttributes(
				document, cpDefinition.getExpandoBridge());

			for (CPInstance cpInstance : cpDefinition.getCPInstances()) {
				_expandoBridgeIndexer.addAttributes(
					document, cpInstance.getExpandoBridge());
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Commerce product definition " + cpDefinition +
						" indexed successfully");
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index commerce product definition" +
						cpDefinition,
					exception);
			}
		}
	}

	private BigDecimal _getBasePrice(List<CPInstance> cpInstances) {
		if (cpInstances.isEmpty()) {
			return null;
		}

		BigDecimal lowestPrice = null;

		CommercePriceEntry commercePriceEntry = null;

		for (CPInstance cpInstance : cpInstances) {
			if (!cpInstance.isApproved()) {
				continue;
			}

			commercePriceEntry =
				_commercePriceEntryLocalService.
					getInstanceBaseCommercePriceEntry(
						cpInstance.getCPInstanceUuid(),
						CommercePriceListConstants.TYPE_PRICE_LIST,
						StringPool.BLANK);

			if ((commercePriceEntry == null) ||
				commercePriceEntry.isPriceOnApplication()) {

				continue;
			}

			BigDecimal price = commercePriceEntry.getPrice();

			if (lowestPrice == null) {
				lowestPrice = price;
			}

			BigDecimal promoPrice = cpInstance.getPromoPrice();

			if ((promoPrice.compareTo(BigDecimal.ZERO) > 0) &&
				BigDecimalUtil.lt(promoPrice, price)) {

				price = promoPrice;
			}

			if (BigDecimalUtil.lt(price, lowestPrice)) {
				lowestPrice = price;
			}
		}

		return lowestPrice;
	}

	private String[] _getCPConfigurationListIds(long cpDefinitionId) {
		List<String> cpConfigurationListIds = new ArrayList<>();

		for (CPConfigurationEntry cpConfigurationEntry :
				_cpConfigurationEntryLocalService.getCPConfigurationEntries(
					_classNameLocalService.getClassNameId(CPDefinition.class),
					cpDefinitionId, true)) {

			cpConfigurationListIds.add(
				String.valueOf(
					cpConfigurationEntry.getCPConfigurationListId()));

			CPConfigurationEntrySetting cpConfigurationEntrySetting =
				_cpConfigurationEntrySettingLocalService.
					fetchCPConfigurationEntrySetting(
						cpConfigurationEntry.getCPConfigurationEntryId(),
						CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

			if (cpConfigurationEntrySetting == null) {
				continue;
			}

			cpConfigurationListIds.addAll(
				StringUtil.split(cpConfigurationEntrySetting.getValue()));
		}

		return cpConfigurationListIds.toArray(new String[0]);
	}

	private List<CPOption> _getCPOptions(
			List<CPDefinitionOptionRel> cpDefinitionOptionRels)
		throws Exception {

		return TransformUtil.transform(
			cpDefinitionOptionRels,
			cpDefinitionOptionRel -> {
				if (!cpDefinitionOptionRel.isFacetable()) {
					return null;
				}

				return cpDefinitionOptionRel.getCPOption();
			});
	}

	private String _getCPSpecificationOptionKey(
		CPSpecificationOption cpSpecificationOption) {

		return cpSpecificationOption.getKey();
	}

	private List<CPDefinitionSpecificationOptionValue>
			_getFilteredCPDefinitionSpecificationOptionValues(
				List<CPDefinitionSpecificationOptionValue>
					cpDefinitionSpecificationOptionValues)
		throws Exception {

		return TransformUtil.transform(
			cpDefinitionSpecificationOptionValues,
			cpDefinitionSpecificationOptionValue -> {
				CPSpecificationOption cpSpecificationOption =
					cpDefinitionSpecificationOptionValue.
						getCPSpecificationOption();

				if (!cpDefinitionSpecificationOptionValue.isVisible() ||
					!cpSpecificationOption.isFacetable() ||
					!cpSpecificationOption.isVisible()) {

					return null;
				}

				return cpDefinitionSpecificationOptionValue;
			});
	}

	private String[] _getReverseCPDefinitionIds(long cProductId, String type) {
		List<CPDefinitionLink> cpDefinitionLinks =
			_cpDefinitionLinkLocalService.getReverseCPDefinitionLinks(
				cProductId, type, WorkflowConstants.STATUS_APPROVED);

		String[] reverseCPDefinitionIdsArray =
			new String[cpDefinitionLinks.size()];

		List<String> reverseCPDefinitionIds = new ArrayList<>();

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			reverseCPDefinitionIds.add(
				String.valueOf(cpDefinitionLink.getCPDefinitionId()));
		}

		return reverseCPDefinitionIds.toArray(reverseCPDefinitionIdsArray);
	}

	private boolean _isHidden(CPDefinition cpDefinition, CProduct cProduct) {
		if ((cpDefinition.getCPDefinitionId() !=
				cProduct.getPublishedCPDefinitionId()) &&
			_cpDefinitionLocalService.isVersionable(
				cpDefinition.getCPDefinitionId())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionModelDocumentContributor.class);

	@Reference
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Reference
	private CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;

	@Reference
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Reference
	private CPDefinitionLinkTypeRegistry _cpDefinitionLinkTypeRegistry;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

}