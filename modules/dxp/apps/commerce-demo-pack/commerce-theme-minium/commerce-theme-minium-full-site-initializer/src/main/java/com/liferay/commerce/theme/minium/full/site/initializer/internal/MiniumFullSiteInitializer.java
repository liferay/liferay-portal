/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.theme.minium.full.site.initializer.internal;

import com.liferay.commerce.theme.minium.SiteInitializerDependencyResolver;
import com.liferay.commerce.theme.minium.SiteInitializerDependencyResolverThreadLocal;
import com.liferay.commerce.theme.minium.full.site.initializer.internal.importer.CommerceMLForecastImporter;
import com.liferay.commerce.theme.minium.full.site.initializer.internal.importer.CommerceMLRecommendationImporter;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.site.exception.InitializationException;
import com.liferay.site.initializer.SiteInitializer;

import jakarta.servlet.ServletContext;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = "site.initializer.key=" + MiniumFullSiteInitializer.KEY,
	service = SiteInitializer.class
)
public class MiniumFullSiteInitializer implements SiteInitializer {

	public static final String KEY = "minium-full-initializer";

	@Override
	public String getDescription(Locale locale) {
		return _siteInitializer.getDescription(locale);
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return "Minium Demo";
	}

	@Override
	public String getThumbnailSrc() {
		return _servletContext.getContextPath() + "/images/thumbnail.png";
	}

	@Override
	public void initialize(long groupId) throws InitializationException {
		try {
			SiteInitializerDependencyResolverThreadLocal.
				setSiteInitializerDependencyResolver(
					_fullSiteInitializerDependencyResolver);

			_siteInitializer.initialize(groupId);

			_importCommerceMLForecasts(groupId);

			_importCommerceMLRecommendations(groupId);

			_fixDLFileEntryPermissions(groupId);
		}
		catch (InitializationException initializationException) {
			throw initializationException;
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new InitializationException(exception);
		}
		finally {
			SiteInitializerDependencyResolverThreadLocal.
				removeSiteInitializerDependencyResolver();
		}
	}

	@Override
	public boolean isActive(long companyId) {
		return _siteInitializer.isActive(companyId);
	}

	private void _fixDLFileEntryPermissions(long groupId)
		throws PortalException {

		List<DLFileEntry> dlFileEntries =
			_dlFileEntryLocalService.getFileEntries(
				groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		if (dlFileEntries.isEmpty()) {
			return;
		}

		Group group = _groupLocalService.getGroup(groupId);

		long companyId = group.getCompanyId();

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			Role role = _roleLocalService.getRole(
				companyId, RoleConstants.SITE_MEMBER);

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, dlFileEntry.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFileEntry.getPrimaryKey()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});
		}
	}

	private JSONArray _getJSONArray(String name) throws Exception {
		return _jsonFactory.createJSONArray(
			_fullSiteInitializerDependencyResolver.getJSON(name));
	}

	private void _importCommerceMLForecasts(long groupId) throws Exception {
		JSONArray jsonArray = _getJSONArray("forecasts.json");

		User user = _userLocalService.getUser(PrincipalThreadLocal.getUserId());

		_commerceMLForecastImporter.importCommerceMLForecasts(
			jsonArray, groupId, user.getUserId());
	}

	private void _importCommerceMLRecommendations(long groupId)
		throws Exception {

		JSONArray jsonArray = _getJSONArray("recommendations.json");

		User user = _userLocalService.getUser(PrincipalThreadLocal.getUserId());

		_commerceMLRecommendationImporter.importCommerceMLRecommendations(
			jsonArray, "MIN", groupId, user.getUserId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MiniumFullSiteInitializer.class);

	@Reference
	private CommerceMLForecastImporter _commerceMLForecastImporter;

	@Reference
	private CommerceMLRecommendationImporter _commerceMLRecommendationImporter;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference(target = "(site.initializer.key=minium-full-initializer)")
	private SiteInitializerDependencyResolver
		_fullSiteInitializerDependencyResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.theme.minium.full.site.initializer)"
	)
	private ServletContext _servletContext;

	@Reference(target = "(site.initializer.key=minium-initializer)")
	private SiteInitializer _siteInitializer;

	@Reference
	private UserLocalService _userLocalService;

}