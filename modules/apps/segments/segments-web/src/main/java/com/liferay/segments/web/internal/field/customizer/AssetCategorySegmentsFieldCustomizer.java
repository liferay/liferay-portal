/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.field.customizer;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	property = {
		"segments.field.customizer.entity.name=Organization",
		"segments.field.customizer.entity.name=User",
		"segments.field.customizer.key=" + AssetCategorySegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class AssetCategorySegmentsFieldCustomizer
	extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "category";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getAssetCategory(fieldValue);
	}

	@Override
	public String getClassName() {
		return AssetCategory.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		AssetCategory assetCategory = _getAssetCategory(fieldValue);

		if (assetCategory == null) {
			return fieldValue;
		}

		return assetCategory.getName();
	}

	@Override
	public String getIcon() {
		return "categories";
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			InfoItemItemSelectorCriterion itemSelectorCriterion =
				new InfoItemItemSelectorCriterion();

			itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new InfoItemItemSelectorReturnType());
			itemSelectorCriterion.setItemType(AssetCategory.class.getName());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest),
					AssetCategory.class.getName()),
				PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						themeDisplay.getScopeGroup(),
						themeDisplay.getScopeGroupId(), "selectEntity",
						itemSelectorCriterion)
				).setParameter(
					"vocabularyIds",
					() -> ListUtil.toString(
						_assetVocabularyLocalService.getGroupVocabularies(
							themeDisplay.getCompanyGroupId()),
						AssetVocabulary.VOCABULARY_ID_ACCESSOR)
				).buildString(),
				false);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", exception);
			}

			return null;
		}
	}

	private AssetCategory _getAssetCategory(String fieldValue) {
		long assetCategoryId = GetterUtil.getLong(fieldValue);

		if (assetCategoryId == 0) {
			return null;
		}

		return _assetCategoryLocalService.fetchAssetCategory(assetCategoryId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategorySegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(
		"assetCategoryIds");

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}