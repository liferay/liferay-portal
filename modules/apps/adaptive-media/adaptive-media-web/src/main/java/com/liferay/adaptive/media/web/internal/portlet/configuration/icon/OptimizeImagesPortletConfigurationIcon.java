/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.configuration.icon;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.web.internal.background.task.OptimizeImagesAllConfigurationsBackgroundTaskExecutor;
import com.liferay.adaptive.media.web.internal.constants.AMPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
	service = PortletConfigurationIcon.class
)
public class OptimizeImagesPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getCssClass() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (_isDisabled(themeDisplay.getCompanyId())) {
			return "disabled";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "adapt-all-images");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (_isDisabled(themeDisplay.getCompanyId())) {
			return "javascript:void(0);";
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, AMPortletKeys.ADAPTIVE_MEDIA,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/adaptive_media/optimize_images"
		).buildString();
	}

	@Override
	public double getWeight() {
		return 101;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		return permissionChecker.isCompanyAdmin();
	}

	private boolean _isDisabled(long companyId) {
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				companyId);

		if (amImageConfigurationEntries.isEmpty()) {
			return true;
		}

		int backgroundTasksCount =
			_backgroundTaskManager.getBackgroundTasksCount(
				CompanyConstants.SYSTEM,
				OptimizeImagesAllConfigurationsBackgroundTaskExecutor.class.
					getName(),
				false);

		if (backgroundTasksCount != 0) {
			return true;
		}

		return false;
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}