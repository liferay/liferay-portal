/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.display.context.EditElementVariationsDisplayContext;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelLocalService;
import com.liferay.segments.service.SegmentsExperienceService;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/edit_element_variations"
	},
	service = MVCRenderCommand.class
)
public class EditElementVariationsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			EditElementVariationsDisplayContext.class.getName(),
			new EditElementVariationsDisplayContext(
				_audiencesEntryService, _fragmentEntryLinkLocalService,
				_portal.getHttpServletRequest(renderRequest),
				_layoutLocalService,
				_layoutPageTemplateStructureRelElementVariationService, _portal,
				_segmentsExperienceAudienceEntryRelLocalService,
				_segmentsExperienceService));

		return "/edit_element_variations.jsp";
	}

	@Reference
	private AudiencesEntryService _audiencesEntryService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureRelElementVariationService
		_layoutPageTemplateStructureRelElementVariationService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceAudienceEntryRelLocalService
		_segmentsExperienceAudienceEntryRelLocalService;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

}