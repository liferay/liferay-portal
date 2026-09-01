/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/site_sitemap/save_company_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveCompanyConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		long companyId = themeDisplay.getCompanyId();

		if (!permissionChecker.isCompanyAdmin(companyId)) {
			PrincipalException principalException =
				new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}

		boolean cachedGenerationEnabled = ParamUtil.getBoolean(
			actionRequest, "cachedGenerationEnabled",
			_sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
				companyId));
		boolean xmlSitemapIndexEnabled = ParamUtil.getBoolean(
			actionRequest, "xmlSitemapIndexEnabled");
		String xmlSitemapIndexMode = ParamUtil.getString(
			actionRequest, "xmlSitemapIndexMode",
			_sitemapConfigurationManager.getXMLSitemapIndexMode(companyId));

		_sitemapConfigurationManager.saveSitemapCompanyConfiguration(
			cachedGenerationEnabled, companyId,
			_getCompanySitemapGroupIds(actionRequest),
			_getCompanySitemapObjectDefinitionIds(actionRequest, companyId),
			ParamUtil.getBoolean(actionRequest, "includeCategories"),
			ParamUtil.getBoolean(actionRequest, "includePages"),
			ParamUtil.getBoolean(actionRequest, "includeWebContent"),
			xmlSitemapIndexEnabled, xmlSitemapIndexMode);

		String successMessageKey = "xml-sitemap-settings-have-been-saved";

		if (cachedGenerationEnabled && xmlSitemapIndexEnabled &&
			StringUtil.equals(
				xmlSitemapIndexMode, SitemapConstants.INDEX_MODE_ASSET_TYPE)) {

			boolean saveAndGenerate = ParamUtil.getBoolean(
				actionRequest, "saveAndGenerate");

			if (saveAndGenerate ||
				!_sitemapStorageHelper.hasSitemapFiles(companyId)) {

				Map<Long, String> assetTypeKeys =
					_sitemapManager.getAssetTypeKeys();

				for (String assetTypeKey : assetTypeKeys.values()) {
					_sitemapManager.scheduleRegenerateSitemap(
						assetTypeKey, companyId, 0, new Date());
				}

				successMessageKey =
					"xml-sitemap-has-been-cached-and-settings-have-been-saved";
			}
		}
		else {
			_sitemapStorageHelper.deleteSitemaps(companyId);

			_sitemapManager.deleteRegenerateSitemapScheduledJobs(companyId);
		}

		SessionMessages.add(
			actionRequest, "requestProcessed",
			_language.get(themeDisplay.getLocale(), successMessageKey));

		sendRedirect(actionRequest, actionResponse);
	}

	private long[] _getCompanySitemapGroupIds(ActionRequest actionRequest) {
		return ArrayUtil.filter(
			ArrayUtil.unique(
				ParamUtil.getLongValues(
					actionRequest, "groupsSearchContainerPrimaryKeys")),
			groupId -> {
				Group group = _groupLocalService.fetchGroup(groupId);

				return (group != null) && !group.isGuest();
			});
	}

	private long[] _getCompanySitemapObjectDefinitionIds(
		ActionRequest actionRequest, long companyId) {

		Map<Long, ObjectDefinitionSetting> objectDefinitionSettingsMap =
			_objectDefinitionSettingLocalService.getObjectDefinitionSettingsMap(
				companyId, ObjectDefinitionSettingConstants.NAME_SITEMAPABLE);

		return ArrayUtil.filter(
			ArrayUtil.unique(
				ParamUtil.getLongValues(
					actionRequest,
					"objectDefinitionsSearchContainerPrimaryKeys")),
			objectDefinitionId -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectDefinitionId);

				return (objectDefinition != null) &&
					   objectDefinition.isActive() &&
					   ObjectDefinitionSettingUtil.isSitemapable(
						   objectDefinition, objectDefinitionSettingsMap);
			});
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapStorageHelper _sitemapStorageHelper;

}