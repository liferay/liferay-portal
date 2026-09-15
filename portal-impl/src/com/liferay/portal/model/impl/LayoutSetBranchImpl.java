/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetStagingHandler;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

/**
 * @author Raymond Augé
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutSetBranchImpl extends LayoutSetBranchBaseImpl {

	@Override
	public ColorScheme getColorScheme() {
		return ThemeLocalServiceUtil.getColorScheme(
			getCompanyId(), getTheme().getThemeId(), getColorSchemeId());
	}

	@Override
	public Group getGroup() throws PortalException {
		return GroupLocalServiceUtil.getGroup(getGroupId());
	}

	@Override
	public LayoutSet getLayoutSet() {
		if (_layoutSet != null) {
			return _layoutSet;
		}

		try {
			_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
				getGroupId(), getPrivateLayout());

			LayoutSetStagingHandler layoutSetStagingHandler =
				LayoutStagingUtil.getLayoutSetStagingHandler(_layoutSet);

			if (layoutSetStagingHandler == null) {
				return _layoutSet;
			}

			_layoutSet = layoutSetStagingHandler.getLayoutSet();

			return _layoutSet;
		}
		catch (PortalException | SystemException exception) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _layoutSet;
	}

	@Override
	public long getLiveLogoId() {
		long logoId = getLayoutSet().getLogoId();

		if (logoId == 0) {
			logoId = getLayoutSet().getLiveLogoId();
		}

		return logoId;
	}

	@Override
	public boolean getLogo() {
		if (getLogoId() > 0) {
			return true;
		}

		return false;
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
		UnicodeProperties settingsUnicodeProperties = getSettingsProperties();

		String value = settingsUnicodeProperties.getProperty(
			ThemeSettingImpl.namespaceProperty(device, key));

		if (value != null) {
			return value;
		}

		Theme theme = null;

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

			theme = ThemeLocalServiceUtil.getTheme(getCompanyId(), themeId);
		}
		else {
			theme = getTheme();
		}

		return theme.getSetting(key);
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
	public boolean isLogo() {
		return getLogo();
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

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetBranchImpl.class);

	private LayoutSet _layoutSet;
	private UnicodeProperties _settingsUnicodeProperties;

}