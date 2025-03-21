/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.handler.LayoutPageTemplateEntryExceptionRequestHandlerUtil;
import com.liferay.layout.page.template.exception.RequiredLayoutPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sandro Chinea
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/update_display_page_entry_content_type"
	},
	service = MVCActionCommand.class
)
public class UpdateDisplayPageEntryContentTypeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Layout draftLayout = null;
		LayoutPageTemplateEntry layoutPageTemplateEntry = null;
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
					ParamUtil.getLong(
						actionRequest, "layoutPageTemplateEntryId"));

			draftLayout = _layoutLocalService.fetchDraftLayout(
				layoutPageTemplateEntry.getPlid());

			_layoutLockManager.getLock(draftLayout, themeDisplay.getUserId());

			_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				ParamUtil.getLong(actionRequest, "classNameId"),
				ParamUtil.getLong(actionRequest, "classTypeId"));

			hideDefaultSuccessMessage(actionRequest);

			SessionMessages.add(
				actionRequest, "displayPageContentTypeChanged",
				_language.format(
					themeDisplay.getLocale(),
					"the-content-type-of-x-was-successfully-changed",
					new String[] {
						HtmlUtil.escape(layoutPageTemplateEntry.getName())
					}));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					ParamUtil.getString(actionRequest, "redirect")));
		}
		catch (ModelListenerException modelListenerException) {
			if (modelListenerException.getCause() instanceof
					RequiredLayoutPageTemplateEntryException) {

				hideDefaultSuccessMessage(actionRequest);

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"error",
						JSONUtil.put(
							"assetType",
							_getTypeLabel(layoutPageTemplateEntry, themeDisplay)
						).put(
							"hasUsages", true
						).put(
							"viewUsagesURL",
							_getViewUsagesURL(
								actionRequest, actionResponse,
								layoutPageTemplateEntry)
						)));

				return;
			}

			throw modelListenerException;
		}
		catch (PortalException portalException) {
			hideDefaultSuccessMessage(actionRequest);

			if (portalException instanceof LockedLayoutException) {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put("error", JSONUtil.put("isLocked", true)));

				return;
			}

			LayoutPageTemplateEntryExceptionRequestHandlerUtil.
				handlePortalException(
					actionRequest, actionResponse, portalException);
		}
		finally {
			_layoutLockManager.unlock(draftLayout, themeDisplay.getUserId());
		}
	}

	private String _getTypeLabel(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		ThemeDisplay themeDisplay) {

		if (layoutPageTemplateEntry == null) {
			return StringPool.BLANK;
		}

		InfoItemDetailsProvider<?> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				layoutPageTemplateEntry.getClassName());

		if (infoItemDetailsProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemClassDetails infoItemClassDetails =
			infoItemDetailsProvider.getInfoItemClassDetails();

		return infoItemClassDetails.getLabel(themeDisplay.getLocale());
	}

	private String _getViewUsagesURL(
		ActionRequest actionRequest, ActionResponse actionResponse,
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return RenderURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCRenderCommandName(
			"/layout_page_template_admin/view_asset_display_page_usages"
		).setRedirect(
			ParamUtil.getString(actionRequest, "redirect")
		).setParameter(
			"classNameId", layoutPageTemplateEntry.getClassNameId()
		).setParameter(
			"classTypeId", layoutPageTemplateEntry.getClassTypeId()
		).setParameter(
			"layoutPageTemplateEntryId",
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"defaultTemplate", layoutPageTemplateEntry.isDefaultTemplate()
		).buildString();
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLockManager _layoutLockManager;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}