/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.categorization.inputs.internal;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.AlertTag;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseInputFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "INPUTS";
	}

	@Override
	public int getType() {
		return FragmentConstants.TYPE_INPUT;
	}

	@Override
	public String getTypeOptions() {
		return JSONUtil.put(
			"fieldTypes", JSONUtil.putAll("categorization")
		).toString();
	}

	protected long getClassPK(
		String className, HttpServletRequest httpServletRequest) {

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if ((infoItemDetails == null) ||
			!Objects.equals(className, infoItemDetails.getClassName())) {

			return 0;
		}

		InfoItemReference infoItemReference =
			infoItemDetails.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			return classPKInfoItemIdentifier.getClassPK();
		}

		return 0;
	}

	protected FormStyledLayoutStructureItem getFormStyledLayoutStructureItem(
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest) {

		LayoutStructure layoutStructure = getLayoutStructure(
			fragmentEntryLink, httpServletRequest);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
					fragmentEntryLink.getFragmentEntryLinkId());

		if (fragmentStyledLayoutStructureItem == null) {
			return null;
		}

		LayoutStructureItem layoutStructureItem =
			LayoutStructureItemUtil.getAncestor(
				fragmentStyledLayoutStructureItem.getItemId(),
				LayoutDataItemTypeConstants.TYPE_FORM, layoutStructure);

		if (!(layoutStructureItem instanceof FormStyledLayoutStructureItem)) {
			return null;
		}

		return (FormStyledLayoutStructureItem)layoutStructureItem;
	}

	protected LayoutStructure getLayoutStructure(
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest) {

		LayoutStructure layoutStructure = null;

		if (httpServletRequest != null) {
			layoutStructure = (LayoutStructure)httpServletRequest.getAttribute(
				LayoutWebKeys.LAYOUT_STRUCTURE);
		}

		if (layoutStructure == null) {
			LayoutPageTemplateStructure layoutPageTemplateStructure =
				layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						fragmentEntryLink.getGroupId(),
						fragmentEntryLink.getPlid());

			layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getData(
					fragmentEntryLink.getSegmentsExperienceId()));
		}

		return layoutStructure;
	}

	protected void writeDisabledCategorizationAlert(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, PrintWriter printWriter)
		throws Exception {

		if (!fragmentRendererContext.isEditMode()) {
			return;
		}

		AlertTag alertTag = new AlertTag();

		alertTag.setMessage(
			language.get(
				fragmentRendererContext.getLocale(),
				"categorization-is-disabled-for-the-selected-content"));
		alertTag.setTitle(
			language.get(fragmentRendererContext.getLocale(), "info"));

		printWriter.write(
			alertTag.doTagAsString(httpServletRequest, httpServletResponse));
	}

	@Reference
	protected Language language;

	@Reference
	protected LayoutPageTemplateStructureLocalService
		layoutPageTemplateStructureLocalService;

}