/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.configuration;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.NavigationMenuMode;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class FragmentEntryMenuDisplayConfiguration {

	public FragmentEntryMenuDisplayConfiguration(String json) {
		Source source = _DEFAULT_SOURCE;

		if (JSONUtil.isJSONObject(json)) {
			JSONObject jsonObject = _createJSONObject(json);

			if (jsonObject.has("contextualMenu")) {
				source = ContextualMenu.parse(
					jsonObject.getString("contextualMenu"));
			}
			else if (jsonObject.has(
						"siteNavigationMenuExternalReferenceCode") ||
					 jsonObject.has("siteNavigationMenuId")) {

				source = new SiteNavigationMenuSource(
					jsonObject.getLong("parentSiteNavigationMenuItemId"),
					jsonObject.getBoolean("privateLayout"),
					jsonObject.getString(
						"siteNavigationMenuExternalReferenceCode"),
					jsonObject.getLong("siteNavigationMenuId"));
			}
		}

		_source = source;
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

		long parentSiteNavigationMenuItemId =
			siteNavigationMenuSource.getParentSiteNavigationMenuItemId();

		if (parentSiteNavigationMenuItemId <= 0) {
			return null;
		}

		if (siteNavigationMenuSource.getSiteNavigationMenuId() == 0) {
			Layout layout = LayoutLocalServiceUtil.fetchLayout(
				parentSiteNavigationMenuItemId);

			return layout.getUuid();
		}

		return String.valueOf(parentSiteNavigationMenuItemId);
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

	public String getSiteNavigationMenuExternalReferenceCode() {
		SiteNavigationMenuSource siteNavigationMenuSource =
			_getSiteNavigationMenuSource();

		if (siteNavigationMenuSource == null) {
			return null;
		}

		return siteNavigationMenuSource.
			getSiteNavigationMenuExternalReferenceCode();
	}

	public long getSiteNavigationMenuId(long groupId) {
		SiteNavigationMenuSource siteNavigationMenuSource =
			_getSiteNavigationMenuSource();

		if (siteNavigationMenuSource == null) {
			return 0;
		}

		long siteNavigationMenuId =
			siteNavigationMenuSource.getSiteNavigationMenuId();

		if (siteNavigationMenuId > 0) {
			return siteNavigationMenuId;
		}

		String siteNavigationMenuExternalReferenceCode =
			siteNavigationMenuSource.
				getSiteNavigationMenuExternalReferenceCode();

		if (Validator.isNull(siteNavigationMenuExternalReferenceCode)) {
			return 0;
		}

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenuExternalReferenceCode, groupId);

		if (siteNavigationMenu != null) {
			return siteNavigationMenu.getSiteNavigationMenuId();
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

	private SiteNavigationMenuSource _getSiteNavigationMenuSource() {
		if (_source instanceof SiteNavigationMenuSource) {
			return (SiteNavigationMenuSource)_source;
		}

		return null;
	}

	private static final Source _DEFAULT_SOURCE = new DefaultSource();

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryMenuDisplayConfiguration.class);

	private final Source _source;

	private static class DefaultSource implements Source {
	}

	private static class SiteNavigationMenuSource implements Source {

		public SiteNavigationMenuSource(
			long parentSiteNavigationMenuItemId, boolean privateLayout,
			String siteNavigationMenuExternalReferenceCode,
			long siteNavigationMenuId) {

			_parentSiteNavigationMenuItemId = parentSiteNavigationMenuItemId;
			_privateLayout = privateLayout;
			_siteNavigationMenuExternalReferenceCode =
				siteNavigationMenuExternalReferenceCode;
			_siteNavigationMenuId = siteNavigationMenuId;
		}

		public long getParentSiteNavigationMenuItemId() {
			return _parentSiteNavigationMenuItemId;
		}

		public String getSiteNavigationMenuExternalReferenceCode() {
			return _siteNavigationMenuExternalReferenceCode;
		}

		public long getSiteNavigationMenuId() {
			return _siteNavigationMenuId;
		}

		public boolean isPrivateLayout() {
			return _privateLayout;
		}

		private final long _parentSiteNavigationMenuItemId;
		private final boolean _privateLayout;
		private final String _siteNavigationMenuExternalReferenceCode;
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