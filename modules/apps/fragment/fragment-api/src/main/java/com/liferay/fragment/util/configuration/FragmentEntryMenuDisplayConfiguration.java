/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.NavigationMenuMode;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class FragmentEntryMenuDisplayConfiguration {

	public FragmentEntryMenuDisplayConfiguration(
		long companyId, String json, long scopeGroupId) {

		if (!JSONUtil.isJSONObject(json)) {
			_source = _DEFAULT_SOURCE;

			return;
		}

		JSONObject jsonObject = _createJSONObject(json);

		if (jsonObject.has("contextualMenu")) {
			_source = ContextualMenu.parse(
				jsonObject.getString("contextualMenu"));

			return;
		}

		String parentSiteNavigationMenuItemExternalReferenceCode =
			jsonObject.getString(
				"parentSiteNavigationMenuItemExternalReferenceCode");
		long parentSiteNavigationMenuItemId = jsonObject.getLong(
			"parentSiteNavigationMenuItemId");
		String siteNavigationMenuExternalReferenceCode = jsonObject.getString(
			"siteNavigationMenuExternalReferenceCode");
		long siteNavigationMenuId = jsonObject.getLong("siteNavigationMenuId");

		if (Validator.isNull(
				parentSiteNavigationMenuItemExternalReferenceCode) &&
			(parentSiteNavigationMenuItemId <= 0) &&
			Validator.isNull(siteNavigationMenuExternalReferenceCode) &&
			(siteNavigationMenuId <= 0)) {

			if (jsonObject.has("privateLayout")) {
				_source = new SiteNavigationMenuSource(
					jsonObject.getBoolean("privateLayout"), StringPool.BLANK,
					siteNavigationMenuId);
			}
			else {
				_source = _DEFAULT_SOURCE;
			}

			return;
		}

		long groupId = _getGroupId(
			companyId, scopeGroupId,
			jsonObject.getString(
				"siteNavigationMenuScopeExternalReferenceCode"));

		siteNavigationMenuId = _getSiteNavigationMenuId(
			groupId, siteNavigationMenuExternalReferenceCode,
			siteNavigationMenuId);

		_source = new SiteNavigationMenuSource(
			jsonObject.getBoolean("privateLayout"),
			_getRootItemId(
				parentSiteNavigationMenuItemExternalReferenceCode, groupId,
				parentSiteNavigationMenuItemId, siteNavigationMenuId),
			siteNavigationMenuId);
	}

	public NavigationMenuMode getNavigationMenuMode() {
		if (_source instanceof SiteNavigationMenuSource) {
			SiteNavigationMenuSource siteNavigationMenuSource =
				(SiteNavigationMenuSource)_source;

			if (siteNavigationMenuSource.isPrivateLayout()) {
				return NavigationMenuMode.PRIVATE_PAGES;
			}

			return NavigationMenuMode.PUBLIC_PAGES;
		}

		return NavigationMenuMode.PUBLIC_PAGES;
	}

	public String getRootItemId() {
		if (!(_source instanceof SiteNavigationMenuSource)) {
			return null;
		}

		SiteNavigationMenuSource siteNavigationMenuSource =
			(SiteNavigationMenuSource)_source;

		return siteNavigationMenuSource.getRootItemId();
	}

	public int getRootItemLevel() {
		if (_source instanceof ContextualMenu) {
			ContextualMenu contextualMenu = (ContextualMenu)_source;

			if (contextualMenu == ContextualMenu.CHILDREN) {
				return 0;
			}
			else if (contextualMenu == ContextualMenu.PARENT_AND_ITS_SIBLINGS) {
				return 2;
			}
			else if (contextualMenu == ContextualMenu.SELF_AND_SIBLINGS) {
				return 1;
			}
		}

		return 1;
	}

	public String getRootItemType() {
		if (_source instanceof ContextualMenu) {
			return "relative";
		}

		return "select";
	}

	public long getSiteNavigationMenuId() {
		if (_source instanceof
				SiteNavigationMenuSource siteNavigationMenuSource) {

			return siteNavigationMenuSource.getSiteNavigationMenuId();
		}

		return 0;
	}

	private JSONObject _createJSONObject(String value) {
		try {
			return JSONFactoryUtil.createJSONObject(value);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	private long _getGroupId(
		long companyId, long scopeGroupId, String externalReferenceCode) {

		if (Validator.isNull(externalReferenceCode)) {
			return scopeGroupId;
		}

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group == null) {
			return scopeGroupId;
		}

		return group.getGroupId();
	}

	private String _getRootItemId(
		String externalReferenceCode, long groupId, long itemId,
		long siteNavigationMenuId) {

		if (Validator.isNull(externalReferenceCode) && (itemId <= 0)) {
			return null;
		}

		if (siteNavigationMenuId == 0) {
			Layout layout = LayoutLocalServiceUtil.fetchLayout(itemId);

			if (layout == null) {
				return null;
			}

			return layout.getUuid();
		}

		if (itemId > 0) {
			return String.valueOf(itemId);
		}

		SiteNavigationMenuItem siteNavigationMenuItem =
			SiteNavigationMenuItemLocalServiceUtil.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (siteNavigationMenuItem == null) {
			return null;
		}

		return String.valueOf(
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	private long _getSiteNavigationMenuId(
		long groupId, String siteNavigationMenuExternalReferenceCode,
		long siteNavigationMenuId) {

		if (siteNavigationMenuId > 0) {
			return siteNavigationMenuId;
		}

		if (Validator.isNull(siteNavigationMenuExternalReferenceCode)) {
			return 0;
		}

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenuExternalReferenceCode, groupId);

		if (siteNavigationMenu == null) {
			return 0;
		}

		return siteNavigationMenu.getSiteNavigationMenuId();
	}

	private static final Source _DEFAULT_SOURCE = new DefaultSource();

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryMenuDisplayConfiguration.class);

	private final Source _source;

	private static class DefaultSource implements Source {
	}

	private static class SiteNavigationMenuSource implements Source {

		public SiteNavigationMenuSource(
			boolean privateLayout, String rootItemId,
			long siteNavigationMenuId) {

			_privateLayout = privateLayout;
			_rootItemId = rootItemId;
			_siteNavigationMenuId = siteNavigationMenuId;
		}

		public String getRootItemId() {
			return _rootItemId;
		}

		public long getSiteNavigationMenuId() {
			return _siteNavigationMenuId;
		}

		public boolean isPrivateLayout() {
			return _privateLayout;
		}

		private final boolean _privateLayout;
		private final String _rootItemId;
		private final long _siteNavigationMenuId;

	}

	private enum ContextualMenu implements Source {

		CHILDREN("children"),
		PARENT_AND_ITS_SIBLINGS("parent-and-its-siblings"),
		SELF_AND_SIBLINGS("self-and-siblings");

		public static ContextualMenu parse(String stringValue) {
			for (ContextualMenu contextualMenu : values()) {
				if (Objects.equals(contextualMenu.getValue(), stringValue)) {
					return contextualMenu;
				}
			}

			return SELF_AND_SIBLINGS;
		}

		public String getValue() {
			return _value;
		}

		private ContextualMenu(String value) {
			_value = value;
		}

		private final String _value;

	}

	private interface Source {
	}

}