/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.InfoFormUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel"
	},
	service = DTOConverter.class
)
public class PageExperienceDTOConverter
	implements DTOConverter<LayoutPageTemplateStructureRel, PageExperience> {

	@Override
	public String getContentType() {
		return PageExperience.class.getSimpleName();
	}

	@Override
	public PageExperience toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				layoutPageTemplateStructureRel.getSegmentsExperienceId());

		Layout layout = _layoutLocalService.getLayout(
			segmentsExperience.getPlid());

		return new PageExperience() {
			{
				setExternalReferenceCode(
					segmentsExperience::getExternalReferenceCode);
				setKey(segmentsExperience::getSegmentsExperienceKey);
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, segmentsExperience.getNameMap()));
				setPageElements(
					() -> _getPageElements(
						dtoConverterContext, layoutPageTemplateStructureRel,
						layout.getPlid()));
				setPageSpecificationExternalReferenceCode(
					layout::getExternalReferenceCode);
				setPriority(segmentsExperience::getPriority);
				setSegmentItemExternalReference(
					() -> {
						if (segmentsExperience.hasDefaultSegmentsEntry()) {
							return null;
						}

						return new ItemExternalReference() {
							{
								setClassName(SegmentsEntry.class::getName);
								setExternalReferenceCode(
									segmentsExperience::getSegmentsEntryERC);
								setScope(
									() -> ItemScopeUtil.getItemScope(
										layout.getCompanyId(),
										segmentsExperience.
											getSegmentsEntryScopeERC(),
										layout.getGroupId()));
							}
						};
					});
				setUuid(segmentsExperience::getUuid);
			}
		};
	}

	private PageElement[] _getPageElements(
		DTOConverterContext dtoConverterContext,
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel,
		long layoutPlid) {

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructureRel.getData());

		dtoConverterContext.setAttribute(
			LayoutStructure.class.getName(), layoutStructure);

		dtoConverterContext.setAttribute(
			"companyId", layoutPageTemplateStructureRel.getCompanyId());
		dtoConverterContext.setAttribute(
			"displayPageTemplateInfoForm",
			InfoFormUtil.getDisplayPageTemplateInfoForm(layoutPlid));
		dtoConverterContext.setAttribute("layoutPlid", layoutPlid);
		dtoConverterContext.setAttribute(
			"scopeGroupId", layoutPageTemplateStructureRel.getGroupId());

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		return TransformUtil.transformToArray(
			rootLayoutStructureItem.getChildrenItemIds(),
			childrenItemId -> _pageElementDTOConverter.toDTO(
				dtoConverterContext,
				layoutStructure.getLayoutStructureItem(childrenItemId)),
			PageElement.class);
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageElementDTOConverter)"
	)
	private DTOConverter<LayoutStructureItem, PageElement>
		_pageElementDTOConverter;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}