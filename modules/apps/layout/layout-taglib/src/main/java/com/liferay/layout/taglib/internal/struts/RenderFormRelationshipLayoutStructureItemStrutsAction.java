/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.struts;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.taglib.constants.LayoutStructureRendererConstants;
import com.liferay.layout.taglib.servlet.taglib.renderer.LayoutStructureRenderer;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "path=/portal/render_form_relationship_layout_structure_item",
	service = StrutsAction.class
)
public class RenderFormRelationshipLayoutStructureItemStrutsAction
	implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(
			ParamUtil.getLong(httpServletRequest, "p_l_id"));

		if ((layout == null) ||
			(!layout.isTypeContent() && !layout.isTypeAssetDisplay())) {

			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(),
				_getSegmentsExperienceId(httpServletRequest, layout));

		if (layoutStructure == null) {
			return null;
		}

		String formRelationshipLayoutStructureItemId = ParamUtil.getString(
			httpServletRequest, "formRelationshipLayoutStructureItemId");

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formRelationshipLayoutStructureItemId);

		if (layoutStructureItem == null) {
			return null;
		}

		LayoutStructureItem ancestorLayoutStructureItem =
			LayoutStructureItemUtil.getAncestor(
				layoutStructureItem.getItemId(),
				LayoutDataItemTypeConstants.TYPE_FORM, layoutStructure);

		if (ancestorLayoutStructureItem == null) {
			return null;
		}

		themeDisplay.setIsolated(true);

		httpServletRequest.setAttribute(
			LayoutStructureRendererConstants.
				LAYOUT_PARENT_ITEM_EXTERNAL_REFERENCE_CODE +
					formRelationshipLayoutStructureItemId,
			ParamUtil.getString(
				httpServletRequest, "parentItemExternalReferenceCode"));

		httpServletRequest.setAttribute(
			LayoutStructureRendererConstants.
				LAYOUT_RELATED_ITEM_EXTERNAL_REFERENCE_CODE +
					formRelationshipLayoutStructureItemId,
			ParamUtil.getString(
				httpServletRequest, "relatedItemExternalReferenceCode"));

		LayoutStructureRenderer layoutStructureRenderer =
			new LayoutStructureRenderer(
				httpServletRequest, layoutStructure,
				ancestorLayoutStructureItem.getParentItemId(),
				FragmentEntryLinkConstants.VIEW,
				PageContextFactoryUtil.create(
					httpServletRequest, httpServletResponse),
				false, false);

		layoutStructureRenderer.render();

		return null;
	}

	private long _getSegmentsExperienceId(
		HttpServletRequest httpServletRequest, Layout layout) {

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId");

		if (segmentsExperienceId != 0) {
			return segmentsExperienceId;
		}

		return _segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
			layout.getPlid());
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutStructureProvider _layoutStructureProvider;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}