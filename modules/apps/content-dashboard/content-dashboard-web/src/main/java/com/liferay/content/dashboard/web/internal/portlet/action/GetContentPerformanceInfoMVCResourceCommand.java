/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/content_dashboard/get_content_performance_info"
	},
	service = MVCResourceCommand.class
)
public class GetContentPerformanceInfoMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			AnalyticsConfiguration analyticsConfiguration =
				_analyticsSettingsManager.getAnalyticsConfiguration(
					themeDisplay.getCompanyId());

			boolean connectedToAnalyticsCloud = false;

			if (!Validator.isBlank(analyticsConfiguration.token())) {
				connectedToAnalyticsCloud = true;
			}

			boolean assetLibrary = false;

			String className = _getClassName(resourceRequest);

			AssetEntry assetEntry = _assetEntryLocalService.getEntry(
				className,
				GetterUtil.getLong(
					ParamUtil.getLong(resourceRequest, "classPK")));

			DepotEntry depotEntry =
				_depotEntryLocalService.fetchGroupDepotEntry(
					assetEntry.getGroupId());

			List<Long> groupIds = new ArrayList<>();

			boolean connectedToAssetLibrary = false;

			if (depotEntry != null) {
				assetLibrary = true;

				groupIds = _getDepotEntryGroupRelToGroupId(depotEntry);

				if (!groupIds.isEmpty()) {
					connectedToAssetLibrary = true;
				}
			}
			else {
				groupIds = Collections.singletonList(assetEntry.getGroupId());
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"analyticsSettingsPortletURL",
					PortletURLBuilder.create(
						_portal.getControlPanelPortletURL(
							httpServletRequest,
							ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
							PortletRequest.RENDER_PHASE)
					).setMVCRenderCommandName(
						"/configuration_admin/view_configuration_screen"
					).setParameter(
						"configurationScreenKey", "analytics-cloud-connection"
					).buildString()
				).put(
					"assetId", _getClassPK(className, assetEntry.getClassPK())
				).put(
					"assetLibrary", assetLibrary
				).put(
					"assetType", _getAssetType(className)
				).put(
					"connectedToAnalyticsCloud", connectedToAnalyticsCloud
				).put(
					"connectedToAssetLibrary", connectedToAssetLibrary
				).put(
					"groupId", assetEntry.getGroupId()
				).put(
					"isAdmin",
					_roleLocalService.hasUserRole(
						themeDisplay.getUserId(), themeDisplay.getCompanyId(),
						RoleConstants.ADMINISTRATOR, true)
				).put(
					"siteEditDepotEntryDepotAdminPortletURL",
					() -> {
						if (depotEntry == null) {
							return PortletURLBuilder.create(
								_portletURLFactory.create(
									httpServletRequest, _DEPOT_ADMIN_PORTLET_ID,
									PortletRequest.RENDER_PHASE)
							).buildString();
						}

						return PortletURLBuilder.create(
							_portletURLFactory.create(
								httpServletRequest, _DEPOT_ADMIN_PORTLET_ID,
								PortletRequest.RENDER_PHASE)
						).setMVCRenderCommandName(
							"/depot/edit_depot_entry"
						).setParameter(
							"depotEntryId", depotEntry.getDepotEntryId()
						).setParameter(
							"screenNavigationEntryKey", "sites"
						).buildString();
					}
				).put(
					"siteSyncedToAnalyticsCloud",
					_hasSiteSyncedToAnalyticsCloud(
						analyticsConfiguration.syncedGroupIds(), groupIds)
				));
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						ResourceBundleUtil.getBundle(
							_portal.getLocale(resourceRequest), getClass()),
						"an-unexpected-error-occurred")));
		}
	}

	private String _getAssetType(String className) {
		if (StringUtil.endsWith(className, "BlogsEntry")) {
			return "blog";
		}
		else if (StringUtil.endsWith(className, "DLFileEntry")) {
			return "document";
		}
		else if (StringUtil.endsWith(className, "JournalArticle")) {
			return "journal";
		}

		return null;
	}

	private String _getClassName(ResourceRequest resourceRequest) {
		String className = ParamUtil.getString(resourceRequest, "className");

		if (StringUtil.equals(className, FileEntry.class.getName())) {
			className = DLFileEntry.class.getName();
		}

		return className;
	}

	private String _getClassPK(String className, long classPK)
		throws Exception {

		if (!StringUtil.equals(className, JournalArticle.class.getName())) {
			return String.valueOf(classPK);
		}

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(classPK);

		return journalArticle.getArticleId();
	}

	private List<Long> _getDepotEntryGroupRelToGroupId(DepotEntry depotEntry)
		throws PortalException {

		return TransformUtil.transform(
			_depotEntryGroupRelLocalService.getDepotEntryGroupRels(depotEntry),
			depotEntryGroupRel -> depotEntryGroupRel.getToGroupId());
	}

	private boolean _hasSiteSyncedToAnalyticsCloud(
		String[] analyticsCloudSyncedGroupIds, List<Long> groupIds) {

		for (long groupId : groupIds) {
			if (ArrayUtil.contains(
					analyticsCloudSyncedGroupIds, String.valueOf(groupId))) {

				return true;
			}
		}

		return false;
	}

	private static final String _DEPOT_ADMIN_PORTLET_ID =
		"com_liferay_depot_web_portlet_DepotAdminPortlet";

	private static final Log _log = LogFactoryUtil.getLog(
		GetContentPerformanceInfoMVCResourceCommand.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private RoleLocalService _roleLocalService;

}