/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.field.customizer;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.field.customizer.entity.name=User",
		"segments.field.customizer.key=" + GroupSegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class GroupSegmentsFieldCustomizer extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "group";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getGroup(fieldValue);
	}

	@Override
	public String getClassName() {
		return Group.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		Group group = _getGroup(fieldValue);

		if (group == null) {
			return fieldValue;
		}

		try {
			return group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get name for group " + fieldValue,
					portalException);
			}

			return fieldValue;
		}
	}

	@Override
	public String getIcon() {
		return "sites";
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			SiteItemSelectorCriterion siteItemSelectorCriterion =
				new SiteItemSelectorCriterion();

			siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new GroupItemSelectorReturnType());
			siteItemSelectorCriterion.setIncludeCompany(false);
			siteItemSelectorCriterion.setIncludeRecentSites(false);

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest), Group.class.getName()),
				PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						"selectSite", siteItemSelectorCriterion)
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

	private Group _getGroup(String fieldValue) {
		long groupId = GetterUtil.getLong(fieldValue);

		if (groupId == 0) {
			return null;
		}

		return _groupLocalService.fetchGroup(groupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupSegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(
		"groupIds");

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}