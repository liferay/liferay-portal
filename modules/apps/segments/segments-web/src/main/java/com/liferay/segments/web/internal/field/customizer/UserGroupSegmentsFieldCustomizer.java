/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.field.customizer;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.user.groups.admin.item.selector.UserGroupItemSelectorCriterion;

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
		"segments.field.customizer.key=" + UserGroupSegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class UserGroupSegmentsFieldCustomizer
	extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "userGroup";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getUserGroup(fieldValue);
	}

	@Override
	public String getClassName() {
		return UserGroup.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		UserGroup userGroup = _getUserGroup(fieldValue);

		if (userGroup == null) {
			return fieldValue;
		}

		return userGroup.getName();
	}

	@Override
	public String getIcon() {
		return "users";
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			UserGroupItemSelectorCriterion userGroupItemSelectorCriterion =
				new UserGroupItemSelectorCriterion();

			userGroupItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new UUIDItemSelectorReturnType());

			boolean filterManageableUserGroups = true;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (permissionChecker.hasPermission(
					themeDisplay.getScopeGroup(), UserGroup.class.getName(),
					UserGroup.class.getName(), ActionKeys.VIEW)) {

				filterManageableUserGroups = false;
			}

			userGroupItemSelectorCriterion.setFilterManageableUserGroups(
				filterManageableUserGroups);

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest),
					UserGroup.class.getName()),
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						"selectEntity", userGroupItemSelectorCriterion)),
				false);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", exception);
			}

			return null;
		}
	}

	private UserGroup _getUserGroup(String fieldValue) {
		long userGroupId = GetterUtil.getLong(fieldValue);

		if (userGroupId == 0) {
			return null;
		}

		return _userGroupLocalService.fetchUserGroup(userGroupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupSegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(
		"userGroupIds");

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}