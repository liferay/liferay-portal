/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/publish_layout"
	},
	service = MVCActionCommand.class
)
public class PublishLayoutMVCActionCommand
	extends BaseContentPageEditorMVCActionCommand {

	@Override
	protected void doCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout draftLayout = _layoutLocalService.getLayout(
			themeDisplay.getPlid());

		if (!draftLayout.isDraftLayout()) {
			return;
		}

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			themeDisplay.getPermissionChecker(), draftLayout);

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_publishLayout(
			draftLayout, layout, serviceContext, themeDisplay.getUserId());

		String portletId = _portal.getPortletId(actionRequest);

		if (SessionMessages.contains(
				actionRequest,
				portletId.concat(
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE))) {

			SessionMessages.clear(actionRequest);
		}

		MultiSessionMessages.add(actionRequest, "layoutPublished");
	}

	private void _cleanWidgetLayoutTypeSettings(
		UnicodeProperties typeSettingsUnicodeProperties) {

		typeSettingsUnicodeProperties.remove(
			LayoutConstants.CUSTOMIZABLE_LAYOUT);
		typeSettingsUnicodeProperties.remove(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID);

		Set<Map.Entry<String, String>> entrySet =
			typeSettingsUnicodeProperties.entrySet();

		entrySet.removeIf(
			entry -> {
				String key = entry.getKey();

				return key.startsWith("column-");
			});
	}

	private void _publishLayout(
			Layout draftLayout, Layout layout, ServiceContext serviceContext,
			long userId)
		throws Exception {

		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				layout.getCompanyId(), layout.getGroupId(),
				Layout.class.getName())) {

			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				layout.getCompanyId(), layout.getGroupId(), userId,
				Layout.class.getName(), layout.getPlid(), layout,
				serviceContext, Collections.emptyMap());

			layoutLockManager.unlock(draftLayout, userId);
		}
		else {
			UnicodeProperties originalTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			boolean indexReadOnly =
				IndexStatusManagerThreadLocal.isIndexReadOnly();

			IndexStatusManagerThreadLocal.setIndexReadOnly(true);

			try {
				_layoutLocalService.copyLayoutContent(draftLayout, layout);
			}
			finally {
				IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);
			}

			layout = _layoutLocalService.getLayout(layout.getPlid());

			LayoutStructureUtil.deleteMarkedForDeletionItems(
				draftLayout.getGroupId(), draftLayout.getPlid());

			draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

			UnicodeProperties typeSettingsUnicodeProperties =
				draftLayout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.remove(
				LayoutTypeSettingsConstants.KEY_DESIGN_CONFIGURATION_MODIFIED);

			String layoutPrototypeUuid = layout.getLayoutPrototypeUuid();

			if (Validator.isNotNull(layoutPrototypeUuid)) {
				typeSettingsUnicodeProperties.setProperty(
					"layoutPrototypeUuid", layoutPrototypeUuid);
			}

			typeSettingsUnicodeProperties.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED,
				Boolean.TRUE.toString());

			_cleanWidgetLayoutTypeSettings(typeSettingsUnicodeProperties);

			draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

			draftLayout = _layoutLocalService.updateLayout(draftLayout);

			LayoutSet layoutSet = layout.getLayoutSet();

			UnicodeProperties updatedTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			if (originalTypeSettingsUnicodeProperties.containsKey(
					LayoutTypePortletConstants.SITEMAP_CHANGEFREQ)) {

				updatedTypeSettingsUnicodeProperties.put(
					LayoutTypePortletConstants.SITEMAP_CHANGEFREQ,
					originalTypeSettingsUnicodeProperties.get(
						LayoutTypePortletConstants.SITEMAP_CHANGEFREQ));
			}

			if (originalTypeSettingsUnicodeProperties.containsKey(
					LayoutTypePortletConstants.SITEMAP_INCLUDE)) {

				updatedTypeSettingsUnicodeProperties.put(
					LayoutTypePortletConstants.SITEMAP_INCLUDE,
					originalTypeSettingsUnicodeProperties.get(
						LayoutTypePortletConstants.SITEMAP_INCLUDE));
			}

			if (originalTypeSettingsUnicodeProperties.containsKey(
					LayoutTypePortletConstants.SITEMAP_PRIORITY)) {

				updatedTypeSettingsUnicodeProperties.put(
					LayoutTypePortletConstants.SITEMAP_PRIORITY,
					originalTypeSettingsUnicodeProperties.get(
						LayoutTypePortletConstants.SITEMAP_PRIORITY));
			}

			if (layoutSet.isLayoutSetPrototypeLinkActive()) {
				if (originalTypeSettingsUnicodeProperties.containsKey(
						Sites.LAST_MERGE_LAYOUT_MODIFIED_TIME)) {

					updatedTypeSettingsUnicodeProperties.put(
						Sites.LAST_MERGE_LAYOUT_MODIFIED_TIME,
						originalTypeSettingsUnicodeProperties.getProperty(
							Sites.LAST_MERGE_LAYOUT_MODIFIED_TIME));
				}

				if (originalTypeSettingsUnicodeProperties.containsKey(
						Sites.LAST_MERGE_TIME)) {

					updatedTypeSettingsUnicodeProperties.put(
						Sites.LAST_MERGE_TIME,
						originalTypeSettingsUnicodeProperties.getProperty(
							Sites.LAST_MERGE_TIME));
				}
			}

			_cleanWidgetLayoutTypeSettings(
				updatedTypeSettingsUnicodeProperties);

			layout.setType(draftLayout.getType());
			layout.setLayoutPrototypeUuid(null);
			layout.setStatus(WorkflowConstants.STATUS_APPROVED);

			layout = _layoutLocalService.updateLayout(layout);

			_updateLayoutRevision(layout, serviceContext);
		}
	}

	private void _updateLayoutRevision(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (layoutRevision == null) {
			return;
		}

		_layoutRevisionLocalService.updateLayoutRevision(
			serviceContext.getUserId(), layoutRevision.getLayoutRevisionId(),
			layoutRevision.getLayoutBranchId(), layoutRevision.getName(),
			layoutRevision.getTitle(), layoutRevision.getDescription(),
			layoutRevision.getKeywords(), layoutRevision.getRobots(),
			layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
			layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
			layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
			serviceContext);
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}