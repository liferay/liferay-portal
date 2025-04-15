/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/edit_layout_design"
	},
	service = MVCActionCommand.class
)
public class EditLayoutDesignMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_updateLayout(
			actionRequest, actionResponse,
			_portal.getUploadPortletRequest(actionRequest));
	}

	private void _addClientExtensionEntryRel(
			String cetExternalReferenceCode, Layout layout, String type,
			long userId, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(cetExternalReferenceCode)) {
			_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
				_portal.getClassNameId(Layout.class), layout.getPlid(), type);

			return;
		}

		ClientExtensionEntryRel clientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.
				fetchClientExtensionEntryRelByExternalReferenceCode(
					cetExternalReferenceCode, layout.getCompanyId());

		if (clientExtensionEntryRel != null) {
			return;
		}

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(Layout.class), layout.getPlid(), type);

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			userId, layout.getGroupId(), _portal.getClassNameId(Layout.class),
			layout.getPlid(), cetExternalReferenceCode, type, StringPool.BLANK,
			serviceContext);
	}

	private void _updateClientExtensionEntryRels(
			ActionRequest actionRequest, Layout layout, long userId)
		throws PortalException {

		String themeFaviconCETExternalReferenceCode = ParamUtil.getString(
			actionRequest, "themeFaviconCETExternalReferenceCode");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_addClientExtensionEntryRel(
			themeFaviconCETExternalReferenceCode, layout,
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON, userId,
			serviceContext);

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(Layout.class), layout.getPlid(),
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);

		String[] globalCSSCETExternalReferenceCodes = ParamUtil.getStringValues(
			actionRequest, "globalCSSCETExternalReferenceCodes");

		for (String globalCSSCETExternalReferenceCode :
				globalCSSCETExternalReferenceCodes) {

			_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
				userId, layout.getGroupId(),
				_portal.getClassNameId(Layout.class), layout.getPlid(),
				globalCSSCETExternalReferenceCode,
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS, StringPool.BLANK,
				serviceContext);
		}

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(Layout.class), layout.getPlid(),
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

		String[] globalJSCETExternalReferenceCodes = ParamUtil.getStringValues(
			actionRequest, "globalJSCETExternalReferenceCodes");

		for (String globalJSCETExternalReferenceCode :
				globalJSCETExternalReferenceCodes) {

			String[] typeSettings = StringUtil.split(
				globalJSCETExternalReferenceCode, StringPool.UNDERLINE);

			UnicodeProperties typeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).put(
					"loadType", typeSettings[1]
				).put(
					"scriptLocation", typeSettings[2]
				).build();

			_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
				userId, layout.getGroupId(),
				_portal.getClassNameId(Layout.class), layout.getPlid(),
				typeSettings[0], ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				typeSettingsUnicodeProperties.toString(), serviceContext);
		}

		String themeCSSCETExternalReferenceCode = ParamUtil.getString(
			actionRequest, "themeCSSCETExternalReferenceCode");

		_addClientExtensionEntryRel(
			themeCSSCETExternalReferenceCode, layout,
			ClientExtensionEntryConstants.TYPE_THEME_CSS, userId,
			serviceContext);

		String themeSpritemapCETExternalReferenceCode = ParamUtil.getString(
			actionRequest, "themeSpritemapCETExternalReferenceCode");

		_addClientExtensionEntryRel(
			themeSpritemapCETExternalReferenceCode, layout,
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP, userId,
			serviceContext);
	}

	private void _updateLayout(
			ActionRequest actionRequest, ActionResponse actionResponse,
			UploadPortletRequest uploadPortletRequest)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long groupId = ParamUtil.getLong(actionRequest, "groupId");
			long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");
			long stagingGroupId = ParamUtil.getLong(
				actionRequest, "stagingGroupId");
			long selPlid = ParamUtil.getLong(actionRequest, "selPlid");
			boolean deleteLogo = ParamUtil.getBoolean(
				actionRequest, "deleteLogo");

			byte[] iconBytes = null;

			long fileEntryId = ParamUtil.getLong(
				uploadPortletRequest, "fileEntryId");

			if (fileEntryId > 0) {
				FileEntry fileEntry = _dlAppLocalService.getFileEntry(
					fileEntryId);

				iconBytes = FileUtil.getBytes(fileEntry.getContentStream());
			}

			Layout layout = _layoutLocalService.getLayout(selPlid);

			long styleBookEntryId = ParamUtil.getLong(
				uploadPortletRequest, "styleBookEntryId",
				layout.getStyleBookEntryId());
			long faviconFileEntryId = ParamUtil.getLong(
				uploadPortletRequest, "faviconFileEntryId",
				layout.getFaviconFileEntryId());
			long masterLayoutPlid = ParamUtil.getLong(
				uploadPortletRequest, "masterLayoutPlid",
				layout.getMasterLayoutPlid());

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Layout.class.getName(), actionRequest);

			if (layout.fetchDraftLayout() == null) {
				AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
					Layout.class.getName(), layout.getPlid());

				serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
				serviceContext.setAssetTagNames(assetEntry.getTagNames());
			}

			if (layout.isTypeAssetDisplay() || layout.isTypeUtility()) {
				serviceContext.setAttribute(
					"layout.instanceable.allowed", Boolean.TRUE);
			}

			if (layout.isDraftLayout()) {
				UnicodeProperties layoutTypeSettingsUnicodeProperties =
					layout.getTypeSettingsProperties();

				serviceContext.setAttribute(
					Sites.LAYOUT_UPDATEABLE,
					layoutTypeSettingsUnicodeProperties.get(
						Sites.LAYOUT_UPDATEABLE));
			}

			layout = _layoutService.updateLayout(
				groupId, layout.isPrivateLayout(), layout.getLayoutId(),
				layout.getParentLayoutId(), layout.getNameMap(),
				layout.getTitleMap(), layout.getDescriptionMap(),
				layout.getKeywordsMap(), layout.getRobotsMap(),
				layout.getType(), layout.isHidden(), layout.getFriendlyURLMap(),
				!deleteLogo, iconBytes, styleBookEntryId, faviconFileEntryId,
				masterLayoutPlid, serviceContext);

			_updateClientExtensionEntryRels(
				actionRequest, layout, themeDisplay.getUserId());

			UnicodeProperties formTypeSettingsUnicodeProperties =
				PropertiesParamUtil.getProperties(
					actionRequest, "TypeSettingsProperties--");

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout != null) {
				serviceContext.setAttribute(
					Sites.LAYOUT_UPDATEABLE,
					formTypeSettingsUnicodeProperties.get(
						Sites.LAYOUT_UPDATEABLE));

				draftLayout = _layoutService.updateLayout(
					groupId, draftLayout.isPrivateLayout(),
					draftLayout.getLayoutId(), draftLayout.getParentLayoutId(),
					draftLayout.getNameMap(), draftLayout.getTitleMap(),
					draftLayout.getDescriptionMap(),
					draftLayout.getKeywordsMap(), draftLayout.getRobotsMap(),
					draftLayout.getType(), draftLayout.isHidden(),
					draftLayout.getFriendlyURLMap(), !deleteLogo, iconBytes,
					styleBookEntryId, faviconFileEntryId,
					draftLayout.getMasterLayoutPlid(), serviceContext);

				_updateClientExtensionEntryRels(
					actionRequest, draftLayout, themeDisplay.getUserId());
			}

			UnicodeProperties layoutTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			layoutTypeSettingsUnicodeProperties.putAll(
				formTypeSettingsUnicodeProperties);

			layoutTypeSettingsUnicodeProperties.putAll(
				layout.getTypeSettingsProperties());

			if (layout.isDraftLayout()) {
				layoutTypeSettingsUnicodeProperties.put(
					LayoutTypeSettingsConstants.
						KEY_DESIGN_CONFIGURATION_MODIFIED,
					Boolean.TRUE.toString());
			}

			layout = _layoutService.updateLayout(
				groupId, layout.isPrivateLayout(), layout.getLayoutId(),
				layoutTypeSettingsUnicodeProperties.toString());

			ActionUtil.updateLookAndFeel(
				actionRequest, themeDisplay.getCompanyId(), liveGroupId,
				stagingGroupId, layout.isPrivateLayout(), layout.getLayoutId(),
				layout.getTypeSettingsProperties());

			if (layout.isDraftLayout()) {
				_layoutLocalService.updateStatus(
					themeDisplay.getUserId(), layout.getPlid(),
					WorkflowConstants.STATUS_DRAFT, serviceContext);
			}

			String redirect = ParamUtil.getString(
				actionRequest, "redirect",
				_portal.getLayoutFullURL(layout, themeDisplay));

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			MultiSessionMessages.add(
				actionRequest, portletResource + "layoutUpdated", layout);

			actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
		}
		catch (LayoutJavaScriptException | LayoutNameException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			SessionErrors.add(actionRequest, exception.getClass());

			hideDefaultSuccessMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
		catch (ModelListenerException modelListenerException) {
			if (modelListenerException.getCause() instanceof PortalException) {
				throw (PortalException)modelListenerException.getCause();
			}

			throw modelListenerException;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditLayoutDesignMVCActionCommand.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

}