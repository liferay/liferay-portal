/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.sites.kernel.util.Sites;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Represents a portal layout set, providing access to the layout set's color
 * schemes, groups, prototypes, themes, and more.
 *
 * <p>
 * Each {@link Group} in Liferay can have a public and a private layout set.
 * This keeps information common to all layouts (pages) in the layout set.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class LayoutSetImpl extends LayoutSetBaseImpl {

	/**
	 * Returns the layout set's color scheme.
	 *
	 * <p>
	 * Just like themes, color schemes can be configured on the layout set
	 * level. The layout set's color scheme can be overridden on the layout
	 * level.
	 * </p>
	 *
	 * @return the layout set's color scheme
	 */
	@Override
	public ColorScheme getColorScheme() {
		return ThemeLocalServiceUtil.getColorScheme(
			getCompanyId(), getThemeId(), getColorSchemeId());
	}

	@Override
	public String getCompanyFallbackVirtualHostname() {
		if (_companyFallbackVirtualHostname != null) {
			return _companyFallbackVirtualHostname;
		}

		_companyFallbackVirtualHostname = StringPool.BLANK;

		if (Validator.isNotNull(PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME) &&
			!isPrivateLayout()) {

			Group group = GroupLocalServiceUtil.fetchGroup(
				getCompanyId(), PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

			if ((group != null) && (getGroupId() == group.getGroupId())) {
				Company company = CompanyLocalServiceUtil.fetchCompany(
					getCompanyId());

				if (company != null) {
					_companyFallbackVirtualHostname =
						company.getVirtualHostname();
				}
			}
		}

		companyFallbackVirtualHostnameUpdateEntityCacheConsumer.accept(
			_companyFallbackVirtualHostname);

		return _companyFallbackVirtualHostname;
	}

	@Override
	public String getFaviconURL() {
		if (_faviconURL != null) {
			return _faviconURL;
		}

		if (getFaviconFileEntryId() == 0) {
			return null;
		}

		FileEntry fileEntry = null;

		try {
			fileEntry = DLAppLocalServiceUtil.getFileEntry(
				getFaviconFileEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (fileEntry == null) {
			return null;
		}

		_faviconURL = HtmlUtil.escape(
			StringBundler.concat(
				PortalUtil.getPathContext(), "/documents/",
				fileEntry.getRepositoryId(), StringPool.SLASH,
				fileEntry.getFolderId(), StringPool.SLASH,
				URLCodec.encodeURL(HtmlUtil.unescape(fileEntry.getTitle())),
				StringPool.SLASH, URLCodec.encodeURL(fileEntry.getUuid())));

		return _faviconURL;
	}

	/**
	 * Returns the layout set's group.
	 *
	 * @return the layout set's group
	 */
	@Override
	public Group getGroup() throws PortalException {
		return GroupLocalServiceUtil.getGroup(getGroupId());
	}

	/**
	 * Returns the layout set prototype's ID, or <code>0</code> if it has no
	 * layout set prototype.
	 *
	 * <p>
	 * Prototype is Liferay's technical name for a site template.
	 * </p>
	 *
	 * @return the layout set prototype's ID, or <code>0</code> if it has no
	 *         layout set prototype
	 */
	@Override
	public long getLayoutSetPrototypeId() throws PortalException {
		String layoutSetPrototypeUuid = getLayoutSetPrototypeUuid();

		if (Validator.isNull(layoutSetPrototypeUuid)) {
			return 0;
		}

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSetPrototypeUuid, getCompanyId());

		return layoutSetPrototype.getLayoutSetPrototypeId();
	}

	@Override
	public long getLiveLogoId() {
		long logoId = 0;

		Group group = null;

		try {
			group = getGroup();

			if (!group.isStagingGroup()) {
				return logoId;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return logoId;
		}

		Group liveGroup = group.getLiveGroup();

		LayoutSet liveLayoutSet = null;

		if (isPrivateLayout()) {
			liveLayoutSet = liveGroup.getPrivateLayoutSet();
		}
		else {
			liveLayoutSet = liveGroup.getPublicLayoutSet();
		}

		return liveLayoutSet.getLogoId();
	}

	@Override
	public boolean getLogo() {
		if (getLogoId() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public List<Layout> getMergeFailFriendlyURLLayouts() {
		UnicodeProperties settingsUnicodeProperties = getSettingsProperties();

		String uuids = settingsUnicodeProperties.getProperty(
			Sites.MERGE_FAIL_FRIENDLY_URL_LAYOUTS);

		if (Validator.isNotNull(uuids)) {
			List<Layout> layouts = new ArrayList<>();

			for (String uuid : StringUtil.split(uuids)) {
				Layout layout =
					LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
						uuid, getGroupId(), isPrivateLayout());

				if (layout != null) {
					layouts.add(layout);
				}
			}

			return layouts;
		}

		return Collections.emptyList();
	}

	@Override
	public int getPageCount() {
		return LayoutSetLocalServiceUtil.getPageCount(
			getGroupId(), getPrivateLayout());
	}

	@Override
	public String getSettings() {
		if (_settingsUnicodeProperties == null) {
			return super.getSettings();
		}

		return _settingsUnicodeProperties.toString();
	}

	@Override
	public UnicodeProperties getSettingsProperties() {
		if (_settingsUnicodeProperties == null) {
			_settingsUnicodeProperties = new UnicodeProperties(true);

			try {
				_settingsUnicodeProperties.load(super.getSettings());
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}

		return _settingsUnicodeProperties;
	}

	@Override
	public String getSettingsProperty(String key) {
		UnicodeProperties settingsUnicodeProperties = getSettingsProperties();

		return settingsUnicodeProperties.getProperty(key);
	}

	@Override
	public Theme getTheme() {
		return ThemeLocalServiceUtil.getTheme(getCompanyId(), getThemeId());
	}

	@Override
	public String getThemeSetting(String key, String device) {
		if (!Validator.isBlank(super.getSettings())) {
			UnicodeProperties settingsUnicodeProperties =
				getSettingsProperties();

			String value = settingsUnicodeProperties.getProperty(
				ThemeSettingImpl.namespaceProperty(device, key));

			if (value != null) {
				return value;
			}
		}

		Theme theme = getTheme(device);

		return theme.getSetting(key);
	}

	/**
	 * Returns the names of the layout set's virtual hosts.
	 *
	 * <p>
	 * When accessing a layout set that has a virtual host, the URL elements
	 * "/web/sitename" or "/group/sitename" can be omitted.
	 * </p>
	 *
	 * @return a map from the layout set's virtual host names to the language
	 *         ids configured for them. If the virtual host is configured
	 *         for the default language, it will map to the empty string instead
	 *         of a language id. If the layout set has no virtual hosts
	 *         configured, the returned map will be empty.
	 */
	@Override
	public TreeMap<String, String> getVirtualHostnames() {
		if (_virtualHostnames != null) {
			return new TreeMap<>(_virtualHostnames);
		}

		List<VirtualHost> virtualHosts =
			VirtualHostLocalServiceUtil.getVirtualHosts(
				getCompanyId(), getLayoutSetId());

		if (ListUtil.isEmpty(virtualHosts)) {
			_virtualHostnames = new TreeMap<>();
		}
		else {
			TreeMap<String, String> virtualHostnames = new TreeMap<>();

			for (VirtualHost virtualHost : virtualHosts) {
				virtualHostnames.put(
					virtualHost.getHostname(), virtualHost.getLanguageId());
			}

			_virtualHostnames = virtualHostnames;
		}

		virtualHostnamesUpdateEntityCacheConsumer.accept(_virtualHostnames);

		return new TreeMap<>(_virtualHostnames);
	}

	@Override
	public boolean hasSetModifiedDate() {
		return true;
	}

	@Override
	public boolean isLayoutSetPrototypeLinkActive() {
		if (isLayoutSetPrototypeLinkEnabled() &&
			Validator.isNotNull(getLayoutSetPrototypeUuid())) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isLayoutSetPrototypeUpdateable() {
		if (!isLayoutSetPrototypeLinkActive()) {
			return true;
		}

		try {
			LayoutSetPrototype layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.
					getLayoutSetPrototypeByUuidAndCompanyId(
						getLayoutSetPrototypeUuid(), getCompanyId());

			String layoutsUpdateable = layoutSetPrototype.getSettingsProperty(
				"layoutsUpdateable");

			if (Validator.isNotNull(layoutsUpdateable)) {
				return GetterUtil.getBoolean(layoutsUpdateable, true);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	@Override
	public boolean isLayoutSetReadyForPropagation() {
		if (!isLayoutSetPrototypeLinkActive()) {
			return false;
		}

		try {
			LayoutSetPrototype layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.
					getLayoutSetPrototypeByUuidAndCompanyId(
						getLayoutSetPrototypeUuid(), getCompanyId());

			String readyForPropagation = layoutSetPrototype.getSettingsProperty(
				"readyForPropagation");

			if (Validator.isNotNull(readyForPropagation)) {
				return GetterUtil.getBoolean(readyForPropagation, true);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	@Override
	public boolean isLogo() {
		return getLogo();
	}

	@Override
	public void setCompanyFallbackVirtualHostname(
		String companyFallbackVirtualHostname) {

		_companyFallbackVirtualHostname = companyFallbackVirtualHostname;
	}

	@Override
	public void setSettings(String settings) {
		_settingsUnicodeProperties = null;

		super.setSettings(settings);
	}

	@Override
	public void setSettingsProperties(
		UnicodeProperties settingsUnicodeProperties) {

		_settingsUnicodeProperties = settingsUnicodeProperties;

		super.setSettings(_settingsUnicodeProperties.toString());
	}

	/**
	 * Sets the names of the layout set's virtual host name and language IDs.
	 *
	 * @param virtualHostnames the map of the layout set's virtual host name and
	 *        language IDs
	 * @see   #getVirtualHostnames()
	 */
	@Override
	public void setVirtualHostnames(TreeMap<String, String> virtualHostnames) {
		_virtualHostnames = virtualHostnames;
	}

	protected Theme getTheme(String device) {
		boolean controlPanel = false;

		try {
			Group group = getGroup();

			controlPanel = group.isControlPanel();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (controlPanel) {
			String themeId = PrefsPropsUtil.getString(
				getCompanyId(),
				PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID);

			return ThemeLocalServiceUtil.getTheme(getCompanyId(), themeId);
		}

		return getTheme();
	}

	private static final Log _log = LogFactoryUtil.getLog(LayoutSetImpl.class);

	@CacheField(propagateToInterface = true)
	private String _companyFallbackVirtualHostname;

	private String _faviconURL;
	private UnicodeProperties _settingsUnicodeProperties;

	@CacheField(propagateToInterface = true)
	private TreeMap<String, String> _virtualHostnames;

}