/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.collection.provider;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.filter.KeywordsInfoFilter;
import com.liferay.info.form.InfoForm;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.localized.SingleValueInfoLocalizedValue;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "item.class.name=com.liferay.commerce.product.model.CPDefinition",
	service = RelatedInfoItemCollectionProvider.class
)
public class CPDefinitionOptionValueRelRelatedInfoItemCollectionProvider
	implements ConfigurableInfoCollectionProvider<CPDefinitionOptionValueRel>,
			   RelatedInfoItemCollectionProvider
				   <CPDefinition, CPDefinitionOptionValueRel> {

	@Override
	public InfoPage<CPDefinitionOptionValueRel> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			Object relatedItem = collectionQuery.getRelatedItem();

			if (!(relatedItem instanceof CPDefinition)) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			CPDefinition cpDefinition = (CPDefinition)relatedItem;

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			long[] channelGroupIds = _getChannelGroupIds(
				serviceContext.getScopeGroupId(),
				cpDefinition.getCPDefinitionId());

			long[] groupIds = _getGroupIds(collectionQuery);

			if ((groupIds.length > 0) && (groupIds[0] > 0)) {
				channelGroupIds = groupIds;
			}

			SearchContext searchContext = new SearchContext();

			searchContext.setAttributes(
				HashMapBuilder.<String, Serializable>put(
					Field.STATUS, WorkflowConstants.STATUS_APPROVED
				).put(
					"excludedCPDefinitionId", cpDefinition.getCPDefinitionId()
				).build());

			long companyId = _getCompanyId(collectionQuery);

			if (companyId > 0) {
				searchContext.setCompanyId(companyId);
			}
			else {
				searchContext.setCompanyId(serviceContext.getCompanyId());
			}

			KeywordsInfoFilter keywordsInfoFilter =
				collectionQuery.getInfoFilter(KeywordsInfoFilter.class);

			if (keywordsInfoFilter != null) {
				searchContext.setKeywords(keywordsInfoFilter.getKeywords());
			}

			CPQuery cpQuery = new CPQuery();

			cpQuery.setAnyCategoryIds(_getCategoryIds(collectionQuery));
			cpQuery.setOrderByCol1(Field.NAME);
			cpQuery.setOrderByType1("ASC");

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_getCPDefinitionOptionValueRels(
					_cpDefinitionHelper.searchCPDefinitions(
						channelGroupIds, searchContext, cpQuery,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS));

			Pagination pagination = collectionQuery.getPagination();

			if (pagination.getStart() >= 0) {
				int end = pagination.getEnd();

				if (end > cpDefinitionOptionValueRels.size()) {
					end = cpDefinitionOptionValueRels.size();
				}

				return InfoPage.of(
					cpDefinitionOptionValueRels.subList(
						pagination.getStart(), end),
					collectionQuery.getPagination(),
					cpDefinitionOptionValueRels.size());
			}

			return InfoPage.of(
				cpDefinitionOptionValueRels, collectionQuery.getPagination(),
				cpDefinitionOptionValueRels.size());
		}
		catch (Exception exception) {
			_log.error("Unable to get cpDefinitionOptionValueRel", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getCollectionItemClassName() {
		return CPDefinitionOptionRel.class.getName();
	}

	@Override
	public InfoForm getConfigurationInfoForm() {
		return InfoForm.builder(
		).infoFieldSetEntry(
			_getItemTypesInfoField()
		).build();
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "skus-by-categories");
	}

	private List<AssetCategory> _getAssetCategories() {
		List<AssetCategory> assetCategories = new ArrayList<>();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(
				new long[] {themeDisplay.getCompanyGroupId()});

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			List<AssetCategory> companyAssetCategories =
				_assetCategoryLocalService.getVocabularyCategories(
					assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			if (!companyAssetCategories.isEmpty()) {
				assetCategories.addAll(companyAssetCategories);
			}
		}

		return assetCategories;
	}

	private List<CPDefinitionOptionValueRel> _getCPDefinitionOptionValueRels(
		CPDefinition cpDefinition) {

		return TransformUtil.transform(
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinition.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			cpInstance -> {
				CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
					_cpDefinitionOptionValueRelLocalService.
						createCPDefinitionOptionValueRel(0);

				cpDefinitionOptionValueRel.setCPInstanceUuid(
					cpInstance.getCPInstanceUuid());

				cpDefinitionOptionValueRel.setCProductId(
					cpDefinition.getCProductId());
				cpDefinitionOptionValueRel.setKey(
					FriendlyURLNormalizerUtil.normalize(
						cpDefinition.getName() + StringPool.DASH +
							cpInstance.getSku()));
				cpDefinitionOptionValueRel.setName(cpDefinition.getName());
				cpDefinitionOptionValueRel.setQuantity(BigDecimal.ONE);

				return cpDefinitionOptionValueRel;
			});
	}

	private List<CPDefinitionOptionValueRel> _getCPDefinitionOptionValueRels(
		List<CPDefinition> cpDefinitions) {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			new ArrayList<>();

		for (CPDefinition cpDefinition : cpDefinitions) {
			cpDefinitionOptionValueRels.addAll(
				_getCPDefinitionOptionValueRels(cpDefinition));
		}

		return cpDefinitionOptionValueRels;
	}

	private long[] _getCategoryIds(CollectionQuery collectionQuery) {
		List<Long> categoryIds = new ArrayList<>();

		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (MapUtil.isNotEmpty(configuration) &&
			ArrayUtil.isNotEmpty(configuration.get("categoryIds"))) {

			String[] categoryIdsArray = configuration.get("categoryIds");

			if (ArrayUtil.isNotEmpty(categoryIdsArray)) {
				for (String categoryId : categoryIdsArray) {
					categoryIds.add(GetterUtil.getLong(categoryId));
				}
			}
		}

		return ArrayUtil.toLongArray(categoryIds);
	}

	private long[] _getChannelGroupIds(long groupId, long cpDefinitionId)
		throws PortalException {

		if (_isChannelGroup(groupId)) {
			return new long[] {groupId};
		}

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		if (cpDefinition.isChannelFilterEnabled()) {
			List<CommerceChannelRel> commerceChannelRels =
				_commerceChannelRelLocalService.getCommerceChannelRels(
					CPDefinition.class.getName(), cpDefinitionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			if (!commerceChannelRels.isEmpty()) {
				return TransformUtil.transformToLongArray(
					commerceChannelRels,
					commerceChannelRel -> {
						CommerceChannel commerceChannel =
							_commerceChannelLocalService.getCommerceChannel(
								commerceChannelRel.getCommerceChannelId());

						return commerceChannel.getSiteGroupId();
					});
			}
		}
		else {
			return TransformUtil.transformToLongArray(
				_commerceChannelLocalService.getCommerceChannels(
					cpDefinition.getCompanyId()),
				commerceChannelRel -> {
					CommerceChannel commerceChannel =
						_commerceChannelLocalService.getCommerceChannel(
							commerceChannelRel.getCommerceChannelId());

					return commerceChannel.getSiteGroupId();
				});
		}

		return new long[] {groupId};
	}

	private long _getCompanyId(CollectionQuery collectionQuery) {
		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (MapUtil.isNotEmpty(configuration) &&
			ArrayUtil.isNotEmpty(configuration.get("companyIds"))) {

			String[] companyIds = configuration.get("companyIds");

			for (String companyId : companyIds) {
				return GetterUtil.getLong(companyId);
			}
		}

		return 0;
	}

	private long[] _getGroupIds(CollectionQuery collectionQuery) {
		List<Long> groupIds = new ArrayList<>();

		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (MapUtil.isNotEmpty(configuration)) {
			String[] groupIdsArray = configuration.get("groupIds");

			if (ArrayUtil.isNotEmpty(groupIdsArray)) {
				for (String groupId : groupIdsArray) {
					groupIds.add(GetterUtil.getLong(groupId));
				}
			}
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	private InfoField _getItemTypesInfoField() {
		return InfoField.builder(
		).infoFieldType(
			MultiselectInfoFieldType.INSTANCE
		).namespace(
			StringPool.BLANK
		).name(
			"category"
		).attribute(
			MultiselectInfoFieldType.OPTIONS,
			TransformUtil.transform(
				_getAssetCategories(),
				assetCategory -> new OptionInfoFieldType(
					new SingleValueInfoLocalizedValue<>(
						assetCategory.getName()),
					String.valueOf(assetCategory.getCategoryId())))
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(getClass(), "category")
		).localizable(
			true
		).build();
	}

	private boolean _isChannelGroup(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		String className = group.getClassName();

		if (className.equals(CommerceChannel.class.getName())) {
			return true;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				groupId);

		if (commerceChannel != null) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionOptionValueRelRelatedInfoItemCollectionProvider.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

}