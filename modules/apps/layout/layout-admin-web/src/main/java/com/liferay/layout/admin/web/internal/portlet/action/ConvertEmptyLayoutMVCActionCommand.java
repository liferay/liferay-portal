/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.handler.LayoutExceptionRequestHandlerUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/convert_empty_layout"
	},
	service = MVCActionCommand.class
)
public class ConvertEmptyLayoutMVCActionCommand
	extends BaseAddLayoutMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			String type = ParamUtil.getString(actionRequest, "type");

			if (type.equals(LayoutConstants.TYPE_EMBEDDED) ||
				type.equals(LayoutConstants.TYPE_LINK_TO_LAYOUT)) {

				SessionErrors.add(actionRequest, PortalException.class);

				return;
			}

			long selPlid = ParamUtil.getLong(actionRequest, "selPlid");

			Layout layout = _layoutLocalService.fetchLayout(selPlid);

			long layoutPageTemplateEntryId = ParamUtil.getLong(
				actionRequest, "layoutPageTemplateEntryId");
			long masterLayoutPlid = ParamUtil.getLong(
				actionRequest, "masterLayoutPlid",
				LayoutConstants.DEFAULT_PLID);

			Layout layoutPageTemplateEntryLayout = null;

			if (layoutPageTemplateEntryId > 0) {
				LayoutPageTemplateEntry layoutPageTemplateEntry =
					_layoutPageTemplateEntryLocalService.
						fetchLayoutPageTemplateEntry(layoutPageTemplateEntryId);

				if (layoutPageTemplateEntry.getLayoutPrototypeId() == 0) {
					type = LayoutConstants.TYPE_CONTENT;
				}
				else {
					type = LayoutConstants.TYPE_PORTLET;
				}

				layoutPageTemplateEntryLayout = _layoutLocalService.fetchLayout(
					layoutPageTemplateEntry.getPlid());

				if (layoutPageTemplateEntryLayout != null) {
					masterLayoutPlid =
						layoutPageTemplateEntryLayout.getMasterLayoutPlid();
				}
			}

			Layout draftLayout = layout.fetchDraftLayout();

			Map<Locale, String> nameMap = HashMapBuilder.put(
				LocaleUtil.getSiteDefault(),
				ParamUtil.getString(actionRequest, "name")
			).build();

			String redirect = null;

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Layout.class.getName(), actionRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			if (!Objects.equals(type, LayoutConstants.TYPE_CONTENT)) {
				layout = _layoutLocalService.updateLayout(
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getLayoutId(), layout.getParentLayoutId(), nameMap,
					layout.getTitleMap(), layout.getDescriptionMap(),
					layout.getKeywordsMap(), layout.getRobotsMap(), type, false,
					layout.getFriendlyURLMap(), layout.isIconImage(), null,
					layout.getStyleBookEntryId(),
					layout.getFaviconFileEntryId(), masterLayoutPlid,
					serviceContext);

				if (layoutPageTemplateEntryLayout != null) {
					_layoutLocalService.copyLayoutContent(
						layoutPageTemplateEntryLayout, layout);
				}

				redirect = PortletURLBuilder.createRenderURL(
					_portal.getLiferayPortletResponse(actionResponse)
				).setMVCRenderCommandName(
					"/layout_admin/edit_layout"
				).setBackURL(
					_getBackURL(actionRequest)
				).setParameter(
					"backURLTitle", layout.getName()
				).setParameter(
					"groupId", themeDisplay.getScopeGroupId()
				).setParameter(
					"privateLayout", layout.isPrivateLayout()
				).setParameter(
					"selPlid", selPlid
				).buildString();
			}
			else {
				if (draftLayout == null) {
					draftLayout = _layoutLocalService.addLayout(
						null, layout.getUserId(), layout.getGroupId(),
						layout.isPrivateLayout(), layout.getParentLayoutId(),
						_classNameLocalService.getClassNameId(Layout.class),
						layout.getPlid(), nameMap, layout.getTitleMap(),
						layout.getDescriptionMap(), layout.getKeywordsMap(),
						layout.getRobotsMap(), type, layout.getTypeSettings(),
						true, true, Collections.emptyMap(), masterLayoutPlid,
						serviceContext);
				}

				if (layoutPageTemplateEntryLayout != null) {
					_layoutLocalService.copyLayoutContent(
						layoutPageTemplateEntryLayout, draftLayout);
				}
				else {
					String externalReferenceCode = GetterUtil.getString(
						serviceContext.getAttribute(
							"defaultSegmentsExperienceExternalReferenceCode"),
						null);

					SegmentsExperience segmentsExperience =
						_segmentsExperienceLocalService.
							addDefaultSegmentsExperience(
								externalReferenceCode, layout.getUserId(),
								layout.getPlid(), serviceContext);

					_layoutPageTemplateStructureLocalService.
						addLayoutPageTemplateStructure(
							layout.getUserId(), layout.getGroupId(),
							layout.getPlid(),
							segmentsExperience.getSegmentsExperienceId(),
							_generateContentLayoutStructure(), serviceContext);
				}

				layout = _layoutLocalService.updateLayout(
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getLayoutId(), layout.getParentLayoutId(), nameMap,
					layout.getTitleMap(), layout.getDescriptionMap(),
					layout.getKeywordsMap(), layout.getRobotsMap(), type, false,
					layout.getFriendlyURLMap(), layout.isIconImage(), null,
					layout.getStyleBookEntryId(),
					layout.getFaviconFileEntryId(), masterLayoutPlid,
					serviceContext);

				_layoutLocalService.updateStatus(
					layout.getUserId(), layout.getPlid(),
					WorkflowConstants.STATUS_DRAFT, serviceContext);

				redirect = HttpComponentsUtil.addParameters(
					PortalUtil.getLayoutFullURL(draftLayout, themeDisplay),
					"p_l_back_url", _getBackURL(actionRequest),
					"p_l_back_url_title",
					_language.get(themeDisplay.getLocale(), "pages"),
					"p_l_mode", Constants.EDIT);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("redirectURL", redirect));
		}
		catch (Exception exception) {
			LayoutExceptionRequestHandlerUtil.handleException(
				actionRequest, actionResponse, exception);
		}
	}

	private String _generateContentLayoutStructure() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		int layoutPageTemplateEntryType = GetterUtil.getInteger(
			serviceContext.getAttribute("layout.page.template.entry.type"),
			LayoutPageTemplateEntryTypeConstants.BASIC);

		if (!Objects.equals(
				layoutPageTemplateEntryType,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return layoutStructure.toString();
		}

		layoutStructure.addDropZoneLayoutStructureItem(
			rootLayoutStructureItem.getItemId(), 0);

		return layoutStructure.toString();
	}

	private String _getBackURL(ActionRequest actionRequest) {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				actionRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).buildString();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}