/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/edit_layout"
	},
	service = MVCActionCommand.class
)
public class EditLayoutMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(actionRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long groupId = ParamUtil.getLong(actionRequest, "groupId");
			Map<Locale, String> nameMap = _localization.getLocalizationMap(
				actionRequest, "nameMapAsXML");
			long selPlid = ParamUtil.getLong(actionRequest, "selPlid");
			String type = ParamUtil.getString(uploadPortletRequest, "type");
			boolean hidden = ParamUtil.getBoolean(
				uploadPortletRequest, "hidden");
			Map<Locale, String> friendlyURLMap =
				_localization.getLocalizationMap(actionRequest, "friendlyURL");

			Layout layout = _layoutLocalService.getLayout(selPlid);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Layout.class.getName(), actionRequest);

			if ((layout.isTypeAssetDisplay() || layout.isTypeContent()) &&
				(layout.fetchDraftLayout() == null)) {

				AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
					Layout.class.getName(), layout.getPlid());

				serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
			}

			String oldFriendlyURL = layout.getFriendlyURL();

			Collection<String> values = friendlyURLMap.values();

			values.removeIf(Validator::isNull);

			if (friendlyURLMap.isEmpty()) {
				friendlyURLMap = layout.getFriendlyURLMap();
			}

			if (layout.isTypeAssetDisplay()) {
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
				layout.getParentLayoutId(), nameMap, layout.getTitleMap(),
				layout.getDescriptionMap(), layout.getKeywordsMap(),
				layout.getRobotsMap(), type, hidden, friendlyURLMap,
				layout.isIconImage(), null, layout.getStyleBookEntryId(),
				layout.getFaviconFileEntryId(), layout.getMasterLayoutPlid(),
				serviceContext);

			UnicodeProperties formTypeSettingsUnicodeProperties =
				PropertiesParamUtil.getProperties(
					actionRequest, "TypeSettingsProperties--");

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout != null) {
				serviceContext.setAttribute(
					Sites.LAYOUT_UPDATEABLE,
					formTypeSettingsUnicodeProperties.get(
						Sites.LAYOUT_UPDATEABLE));

				_layoutService.updateLayout(
					groupId, draftLayout.isPrivateLayout(),
					draftLayout.getLayoutId(), draftLayout.getParentLayoutId(),
					nameMap, draftLayout.getTitleMap(),
					draftLayout.getDescriptionMap(),
					draftLayout.getKeywordsMap(), draftLayout.getRobotsMap(),
					type, draftLayout.isHidden(),
					draftLayout.getFriendlyURLMap(), draftLayout.isIconImage(),
					null, draftLayout.getStyleBookEntryId(),
					draftLayout.getFaviconFileEntryId(),
					draftLayout.getMasterLayoutPlid(), serviceContext);
			}

			themeDisplay.clearLayoutFriendlyURL(layout);

			UnicodeProperties layoutTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			String linkToLayoutUuid = ParamUtil.getString(
				actionRequest, "linkToLayoutUuid");

			if (Validator.isNotNull(linkToLayoutUuid)) {
				Layout linkToLayout = _layoutService.getLayoutByUuidAndGroupId(
					linkToLayoutUuid, groupId, layout.isPrivateLayout());

				formTypeSettingsUnicodeProperties.put(
					"linkToLayoutId",
					String.valueOf(linkToLayout.getLayoutId()));
			}

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			if (type.equals(LayoutConstants.TYPE_PORTLET)) {
				String layoutTemplateId = ParamUtil.getString(
					uploadPortletRequest, "layoutTemplateId",
					GetterUtil.getString(
						layoutTypePortlet.getLayoutTemplateId(),
						PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID));

				layoutTypePortlet.setLayoutTemplateId(
					themeDisplay.getUserId(), layoutTemplateId);

				layoutTypeSettingsUnicodeProperties.putAll(
					formTypeSettingsUnicodeProperties);

				boolean layoutCustomizable = GetterUtil.getBoolean(
					layoutTypeSettingsUnicodeProperties.get(
						LayoutConstants.CUSTOMIZABLE_LAYOUT));

				if (!layoutCustomizable) {
					layoutTypePortlet.removeCustomization(
						layoutTypeSettingsUnicodeProperties);
				}
			}
			else {
				layoutTypeSettingsUnicodeProperties.putAll(
					formTypeSettingsUnicodeProperties);

				layoutTypeSettingsUnicodeProperties.putAll(
					layout.getTypeSettingsProperties());
			}

			layout = _layoutService.updateLayout(
				groupId, layout.isPrivateLayout(), layout.getLayoutId(),
				layoutTypeSettingsUnicodeProperties.toString());

			EventsProcessorUtil.process(
				PropsKeys.LAYOUT_CONFIGURATION_ACTION_UPDATE,
				layoutTypePortlet.getConfigurationActionUpdate(),
				uploadPortletRequest,
				_portal.getHttpServletResponse(actionResponse));

			if (layout.isDraftLayout()) {
				_layoutLocalService.updateStatus(
					themeDisplay.getUserId(), layout.getPlid(),
					WorkflowConstants.STATUS_DRAFT, serviceContext);
			}

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isNull(redirect) ||
				(redirect.contains(oldFriendlyURL) &&
				 !Objects.equals(layout.getFriendlyURL(), oldFriendlyURL))) {

				redirect = _portal.getLayoutFullURL(layout, themeDisplay);
			}

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			MultiSessionMessages.add(
				actionRequest, portletResource + "layoutUpdated", layout);

			ActionUtil.addFriendlyURLWarningSessionMessages(
				_portal.getHttpServletRequest(actionRequest), layout,
				_layoutSetPrototypeHelper);

			actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
		}
		catch (ModelListenerException modelListenerException) {
			if (modelListenerException.getCause() instanceof PortalException) {
				throw (PortalException)modelListenerException.getCause();
			}

			throw modelListenerException;
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}