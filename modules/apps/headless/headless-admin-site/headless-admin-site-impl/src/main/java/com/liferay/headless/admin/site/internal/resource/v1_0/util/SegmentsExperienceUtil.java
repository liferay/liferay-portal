/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperienceServiceUtil;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class SegmentsExperienceUtil {

	public static SegmentsExperience addSegmentsExperience(
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience pageExperience, ServiceContext serviceContext)
		throws Exception {

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTENT)) {
			throw new UnsupportedOperationException();
		}

		SegmentsExperience segmentsExperience =
			SegmentsExperienceServiceUtil.addSegmentsExperience(
				pageExperience.getExternalReferenceCode(), layout.getGroupId(),
				_getSegmentsEntryId(
					layout.getGroupId(),
					pageExperience.getSegmentExternalReferenceCode()),
				pageExperience.getKey(), layout.getPlid(),
				LocalizedMapUtil.getLocalizedMap(pageExperience.getName_i18n()),
				GetterUtil.getInteger(pageExperience.getPriority()), true,
				UnicodePropertiesBuilder.create(
					true
				).build(),
				serviceContext);

		LayoutLocalServiceUtil.updateLayoutContent(
			_getData(
				infoItemServiceRegistry, layout, pageExperience,
				segmentsExperience.getSegmentsExperienceId(), serviceContext),
			layout, segmentsExperience.getSegmentsExperienceId());

		return segmentsExperience;
	}

	public static String getDefaultSegmentsExperienceExternalReferenceCode(
		PageExperience[] pageExperiences) {

		if (ArrayUtil.isEmpty(pageExperiences)) {
			throw new UnsupportedOperationException();
		}

		for (PageExperience pageExperience : pageExperiences) {
			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				return pageExperience.getExternalReferenceCode();
			}
		}

		throw new UnsupportedOperationException();
	}

	public static SegmentsExperience updateSegmentsExperience(
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience pageExperience,
			SegmentsExperience segmentsExperience,
			ServiceContext serviceContext)
		throws Exception {

		LayoutLocalServiceUtil.updateLayoutContent(
			_getData(
				infoItemServiceRegistry, layout, pageExperience,
				segmentsExperience.getSegmentsExperienceId(), serviceContext),
			layout, segmentsExperience.getSegmentsExperienceId());

		if ((pageExperience.getPriority() != null) &&
			(segmentsExperience.getPriority() !=
				pageExperience.getPriority())) {

			segmentsExperience =
				SegmentsExperienceServiceUtil.updateSegmentsExperiencePriority(
					segmentsExperience.getSegmentsExperienceId(),
					GetterUtil.getInteger(pageExperience.getPriority()));
		}

		return SegmentsExperienceServiceUtil.updateSegmentsExperience(
			segmentsExperience.getSegmentsExperienceId(),
			_getSegmentsEntryId(
				segmentsExperience.getGroupId(),
				pageExperience.getSegmentExternalReferenceCode()),
			LocalizedMapUtil.getLocalizedMap(pageExperience.getName_i18n()),
			true,
			UnicodePropertiesBuilder.create(
				true
			).build());
	}

	private static String _getData(
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience pageExperience, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItemImporterContext layoutStructureItemImporterContext =
			new LayoutStructureItemImporterContext(
				serviceContext.getCompanyId(), layout.getGroupId(),
				infoItemServiceRegistry, layout, segmentsExperienceId,
				serviceContext.getUserId());

		for (PageElement pageElement : pageExperience.getPageElements()) {
			LayoutStructureUtil.addLayoutStructureItem(
				layoutStructure, layoutStructureItemImporterContext,
				pageElement);
		}

		return layoutStructure.toString();
	}

	private static long _getSegmentsEntryId(
		long groupId, String segmentExternalReferenceCode) {

		if (Validator.isNull(segmentExternalReferenceCode)) {
			return 0;
		}

		SegmentsEntry segmentsEntry =
			SegmentsEntryLocalServiceUtil.fetchSegmentsEntry(
				groupId, segmentExternalReferenceCode);

		if (segmentsEntry == null) {
			throw new UnsupportedOperationException();
		}

		return segmentsEntry.getSegmentsEntryId();
	}

}