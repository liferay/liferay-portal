/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.depot.web.internal.constants.DepotScreenNavigationEntryConstants;
import com.liferay.depot.web.internal.constants.SharingWebKeys;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.sharing.configuration.SharingConfigurationFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseDepotScreenNavigationEntry
	implements ScreenNavigationEntry<DepotEntry> {

	public abstract String getActionCommandName();

	@Override
	public String getCategoryKey() {
		return DepotScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL;
	}

	public abstract String getJspPath();

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(getResourceBundle(locale), getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return DepotScreenNavigationEntryConstants.SCREEN_NAVIGATION_KEY_DEPOT;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		Group group = _getGroup(httpServletRequest);

		httpServletRequest.setAttribute(
			AssetAutoTaggerConfiguration.class.getName(),
			assetAutoTaggerConfigurationFactory.
				getGroupAssetAutoTaggerConfiguration(group));

		httpServletRequest.setAttribute(
			DepotAdminWebKeys.ACTION_COMMAND_NAME, getActionCommandName());
		httpServletRequest.setAttribute(
			DepotAdminWebKeys.FORM_DESCRIPTION,
			getDescription(httpServletRequest.getLocale()));
		httpServletRequest.setAttribute(
			DepotAdminWebKeys.FORM_LABEL,
			getLabel(httpServletRequest.getLocale()));
		httpServletRequest.setAttribute(
			DepotAdminWebKeys.JSP_PATH, getJspPath());
		httpServletRequest.setAttribute(
			DepotAdminWebKeys.SHOW_CONTROLS, isShowControls());
		httpServletRequest.setAttribute(
			SharingWebKeys.GROUP_SHARING_CONFIGURATION,
			sharingConfigurationFactory.getGroupSharingConfiguration(group));

		jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/screen/navigation/edit_depot_entry_navigation.jsp");
	}

	protected String getDescription(Locale locale) {
		return null;
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			resourceBundle, PortalUtil.getResourceBundle(locale));
	}

	protected boolean isShowControls() {
		return true;
	}

	@Reference
	protected AssetAutoTaggerConfigurationFactory
		assetAutoTaggerConfigurationFactory;

	@Reference
	protected JSPRenderer jspRenderer;

	@Reference
	protected Portal portal;

	@Reference
	protected SharingConfigurationFactory sharingConfigurationFactory;

	private Group _getGroup(HttpServletRequest httpServletRequest)
		throws IOException {

		try {
			DepotEntry depotEntry = (DepotEntry)httpServletRequest.getAttribute(
				DepotAdminWebKeys.DEPOT_ENTRY);

			return depotEntry.getGroup();
		}
		catch (PortalException portalException) {
			throw new IOException(portalException);
		}
	}

}