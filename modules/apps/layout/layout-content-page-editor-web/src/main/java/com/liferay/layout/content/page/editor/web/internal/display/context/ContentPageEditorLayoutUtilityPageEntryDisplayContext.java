/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.layout.content.page.editor.web.internal.configuration.PageEditorConfiguration;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class ContentPageEditorLayoutUtilityPageEntryDisplayContext
	extends ContentPageEditorDisplayContext {

	public ContentPageEditorLayoutUtilityPageEntryDisplayContext(
		List<ContentPageEditorSidebarPanel> contentPageEditorSidebarPanels,
		ContentManager contentManager,
		FragmentCollectionManager fragmentCollectionManager,
		FragmentEntryLinkManager fragmentEntryLinkManager,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ItemSelector itemSelector, JSONFactory jsonFactory, Language language,
		LayoutLocalService layoutLocalService,
		LayoutLockManager layoutLockManager,
		LayoutSetLocalService layoutSetLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateEntryService layoutPageTemplateEntryService,
		LayoutPermission layoutPermission,
		PageEditorConfiguration pageEditorConfiguration, Portal portal,
		PortletRequest portletRequest,
		PortletResourcePermission portletResourcePermission,
		PortletURLFactory portletURLFactory, RenderResponse renderResponse,
		SegmentsConfigurationProvider segmentsConfigurationProvider,
		SegmentsExperienceManager segmentsExperienceManager,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		SegmentsExperimentRelLocalService segmentsExperimentRelLocalService,
		SegmentsEntryService segmentsEntryService, Staging staging,
		StagingGroupHelper stagingGroupHelper,
		StyleBookEntryLocalService styleBookEntryLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		super(
			contentPageEditorSidebarPanels, contentManager,
			fragmentCollectionManager, fragmentEntryLinkManager,
			fragmentEntryLinkLocalService, frontendTokenDefinitionRegistry,
			httpServletRequest, infoItemServiceRegistry,
			infoSearchClassMapperRegistry, itemSelector, jsonFactory, language,
			layoutLocalService, layoutLockManager,
			layoutPageTemplateEntryLocalService, layoutPageTemplateEntryService,
			layoutPermission, layoutSetLocalService, pageEditorConfiguration,
			portal, portletRequest, portletResourcePermission,
			portletURLFactory, renderResponse, segmentsConfigurationProvider,
			segmentsExperienceManager, segmentsExperienceLocalService,
			segmentsExperimentRelLocalService, segmentsEntryService, staging,
			stagingGroupHelper, styleBookEntryLocalService,
			workflowDefinitionLinkLocalService);
	}

	@Override
	public boolean isWorkflowEnabled() {
		return false;
	}

	@Override
	protected String getLookAndFeelURL() {
		return null;
	}

}