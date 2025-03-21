/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.changeset.web.internal.portlet.action;

import com.liferay.exportimport.changeset.constants.ChangesetConstants;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.changeset.exception.ExportImportEntityException;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.kernel.staging.StagingURLHelper;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.http.LayoutServiceHttp;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ChangesetPortletKeys.CHANGESET,
		"mvc.command.name=/export_import_changeset/export_import_changeset"
	},
	service = MVCActionCommand.class
)
public class ExportImportChangesetMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, "cmd");

		if (cmd.equals(Constants.EXPORT) || cmd.equals(Constants.PUBLISH) ||
			cmd.equals(ChangesetConstants.PUBLISH_CHANGESET)) {

			processExportAndPublishAction(
				actionRequest, actionResponse, cmd, null);
		}
		else {
			SessionErrors.add(
				actionRequest, ExportImportEntityException.class,
				new ExportImportEntityException(
					ExportImportEntityException.TYPE_INVALID_COMMAND));
		}
	}

	protected void processExportAndPublishAction(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String cmd, String changesetUuid)
		throws Exception {

		if (Validator.isNotNull(actionRequest.getParameter("changesetUuid"))) {
			changesetUuid = ParamUtil.getString(actionRequest, "changesetUuid");
		}
		else if (Validator.isNull(changesetUuid)) {
			SessionErrors.add(
				actionRequest, ExportImportEntityException.class,
				new ExportImportEntityException(
					ExportImportEntityException.TYPE_NO_DATA_FOUND));

			return;
		}

		Map<String, String[]> parameterMap =
			exportImportConfigurationParameterMapFactory.buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.FALSE.toString()});
		parameterMap.put("changesetUuid", new String[] {changesetUuid});

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletId = MapUtil.getString(
			actionRequest.getParameterMap(), "portletId");

		if (Validator.isNull(portletId)) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			portletId = portletDisplay.getId();
		}

		long backgroundTaskId = 0;

		if (cmd.equals(Constants.EXPORT)) {
			Portlet portlet = portletLocalService.getPortletById(portletId);

			Map<String, Serializable> settingsMap =
				exportImportConfigurationSettingsMapFactory.
					buildExportPortletSettingsMap(
						themeDisplay.getUser(), themeDisplay.getPlid(),
						themeDisplay.getScopeGroupId(),
						ChangesetPortletKeys.CHANGESET, parameterMap,
						exportImportHelper.getPortletExportFileName(portlet));

			ExportImportConfiguration exportImportConfiguration =
				exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						themeDisplay.getUserId(), portletId,
						ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
						settingsMap);

			backgroundTaskId =
				exportImportLocalService.exportPortletInfoAsFileInBackground(
					themeDisplay.getUserId(), exportImportConfiguration);
		}
		else if (cmd.equals(Constants.PUBLISH) ||
				 cmd.equals(ChangesetConstants.PUBLISH_CHANGESET)) {

			Group scopeGroup = groupLocalService.fetchGroup(
				ParamUtil.getLong(actionRequest, "groupId"));

			if (scopeGroup == null) {
				scopeGroup = themeDisplay.getScopeGroup();
			}

			if (!scopeGroup.isStagingGroup() &&
				!scopeGroup.isStagedRemotely()) {

				SessionErrors.add(
					actionRequest, ExportImportEntityException.class,
					new ExportImportEntityException(
						ExportImportEntityException.TYPE_GROUP_NOT_STAGED));

				return;
			}

			if (!scopeGroup.isStagedPortlet(portletId)) {
				SessionErrors.add(
					actionRequest, ExportImportEntityException.class,
					new ExportImportEntityException(
						ExportImportEntityException.TYPE_PORTLET_NOT_STAGED));

				return;
			}

			int type =
				ExportImportConfigurationConstants.TYPE_PUBLISH_PORTLET_LOCAL;

			if (scopeGroup.isStagedRemotely()) {
				type =
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_REMOTE;
			}

			long liveGroupId = 0;
			long targetPlid = themeDisplay.getPlid();

			if (scopeGroup.isStagingGroup()) {
				liveGroupId = scopeGroup.getLiveGroupId();
			}
			else if (scopeGroup.isStagedRemotely()) {
				liveGroupId = scopeGroup.getRemoteLiveGroupId();

				PermissionChecker permissionChecker =
					PermissionThreadLocal.getPermissionChecker();

				User user = permissionChecker.getUser();

				Group stagingGroup = scopeGroup;

				if (scopeGroup.isLayout()) {
					stagingGroup = scopeGroup.getParentGroup();
				}

				HttpPrincipal httpPrincipal = new HttpPrincipal(
					stagingURLHelper.buildRemoteURL(
						stagingGroup.getTypeSettingsProperties()),
					user.getLogin(), user.getPassword(),
					user.isPasswordEncrypted());

				try (SafeCloseable safeCloseable =
						ThreadContextClassLoaderUtil.swap(
							PortalClassLoaderUtil.getClassLoader())) {

					targetPlid = LayoutServiceHttp.getControlPanelLayoutPlid(
						httpPrincipal);
				}
			}

			Map<String, Serializable> settingsMap =
				exportImportConfigurationSettingsMapFactory.
					buildPublishPortletSettingsMap(
						themeDisplay.getUser(), scopeGroup.getGroupId(),
						themeDisplay.getPlid(), liveGroupId, targetPlid,
						ChangesetPortletKeys.CHANGESET, parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						themeDisplay.getUserId(), portletId, type, settingsMap);

			backgroundTaskId = staging.publishPortlet(
				themeDisplay.getUserId(), exportImportConfiguration);
		}

		_sendRedirect(actionRequest, actionResponse, backgroundTaskId);
	}

	@Reference
	protected ExportImportConfigurationLocalService
		exportImportConfigurationLocalService;

	@Reference
	protected ExportImportConfigurationParameterMapFactory
		exportImportConfigurationParameterMapFactory;

	@Reference
	protected ExportImportConfigurationSettingsMapFactory
		exportImportConfigurationSettingsMapFactory;

	@Reference
	protected ExportImportHelper exportImportHelper;

	@Reference
	protected ExportImportLocalService exportImportLocalService;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletLocalService portletLocalService;

	@Reference
	protected Staging staging;

	@Reference
	protected StagingURLHelper stagingURLHelper;

	private void _sendRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse,
			long backgroundTaskId)
		throws Exception {

		actionRequest.setAttribute(
			WebKeys.REDIRECT,
			PortletURLBuilder.createRenderURL(
				portal.getLiferayPortletResponse(actionResponse),
				ExportImportPortletKeys.EXPORT_IMPORT
			).setMVCPath(
				"/view_export_import.jsp"
			).setBackURL(
				actionRequest.getParameter("backURL")
			).setParameter(
				"backgroundTaskId", backgroundTaskId
			).buildString());

		hideDefaultSuccessMessage(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

}