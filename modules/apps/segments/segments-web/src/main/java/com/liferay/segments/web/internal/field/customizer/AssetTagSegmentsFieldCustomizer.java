/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.field.customizer;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
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
		"segments.field.customizer.key=" + AssetTagSegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class AssetTagSegmentsFieldCustomizer
	extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "tag";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getAssetTag(fieldValue);
	}

	@Override
	public String getClassName() {
		return AssetTag.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		AssetTag assetTag = _getAssetTag(fieldValue);

		if (assetTag == null) {
			return fieldValue;
		}

		return assetTag.getName();
	}

	@Override
	public String getIcon() {
		return "tag";
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			Group companyGroup = _groupLocalService.getCompanyGroup(
				CompanyThreadLocal.getCompanyId());

			AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
				new AssetTagsItemSelectorCriterion();

			assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new AssetTagsItemSelectorReturnType());
			assetTagsItemSelectorCriterion.setGroupIds(
				new long[] {
					companyGroup.getGroupId(),
					_portal.getScopeGroupId(portletRequest)
				});

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest),
					AssetTag.class.getName()),
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						"selectEntity", assetTagsItemSelectorCriterion)),
				false);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", exception);
			}

			return null;
		}
	}

	private AssetTag _getAssetTag(String fieldValue) {
		long assetTagId = GetterUtil.getLong(fieldValue);

		if (assetTagId == 0) {
			return null;
		}

		return _assetTagLocalService.fetchAssetTag(assetTagId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetTagSegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(
		"assetTagIds");

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}