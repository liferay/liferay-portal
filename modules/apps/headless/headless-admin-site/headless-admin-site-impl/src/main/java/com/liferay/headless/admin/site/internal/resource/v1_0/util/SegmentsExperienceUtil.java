/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.exception.SegmentsExperienceLayoutException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class SegmentsExperienceUtil {

	public static SegmentsExperience addSegmentsExperience(
			FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry,
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience pageExperience, Integer priority,
			ServiceContext serviceContext)
		throws Exception {

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTENT)) {
			throw new SegmentsExperienceLayoutException();
		}

		SegmentsEntryReference segmentsEntryReference =
			_getSegmentsEntryReference(
				layout.getCompanyId(),
				pageExperience.getSegmentItemExternalReference(),
				layout.getGroupId());

		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.addSegmentsExperience(
				pageExperience.getExternalReferenceCode(),
				serviceContext.getUserId(), layout.getGroupId(),
				segmentsEntryReference.getExternalReferenceCode(),
				segmentsEntryReference.getScopeExternalReferenceCode(),
				pageExperience.getKey(), layout.getPlid(),
				LocalizedMapUtil.getLocalizedMap(pageExperience.getName_i18n()),
				getPriority(pageExperience.getKey(), layout, priority), true,
				UnicodePropertiesBuilder.create(
					true
				).build(),
				ServiceContextUtil.createServiceContext(
					serviceContext.getScopeGroupId(),
					serviceContext.getRequest(), serviceContext.getUserId(),
					pageExperience.getUuid()));

		LayoutLocalServiceUtil.updateLayoutContent(
			_getData(
				fragmentEntryProcessorRegistry, infoItemServiceRegistry, layout,
				pageExperience, segmentsExperience.getSegmentsExperienceId(),
				serviceContext),
			layout, segmentsExperience.getSegmentsExperienceId());

		return segmentsExperience;
	}

	public static int getPriority(String key, Layout layout, Integer priority) {
		if (Objects.equals(key, SegmentsExperienceConstants.KEY_DEFAULT)) {
			return 0;
		}

		if (priority != null) {
			return priority;
		}

		int lowestPriority =
			SegmentsExperienceLocalServiceUtil.getLowestPriority(
				layout.getGroupId(), layout.getPlid());

		return lowestPriority - 1;
	}

	public static SegmentsExperience updateSegmentsExperience(
			FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry,
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience pageExperience, Integer priority,
			SegmentsExperience segmentsExperience,
			ServiceContext serviceContext)
		throws Exception {

		LayoutLocalServiceUtil.updateLayoutContent(
			_getData(
				fragmentEntryProcessorRegistry, infoItemServiceRegistry, layout,
				pageExperience, segmentsExperience.getSegmentsExperienceId(),
				serviceContext),
			layout, segmentsExperience.getSegmentsExperienceId());

		int segmentsExperiencePriority = getPriority(
			pageExperience.getKey(), layout, priority);

		if (segmentsExperiencePriority != segmentsExperience.getPriority()) {
			segmentsExperience =
				SegmentsExperienceLocalServiceUtil.
					updateSegmentsExperiencePriority(
						serviceContext.getUserId(),
						segmentsExperience.getSegmentsExperienceId(),
						segmentsExperiencePriority);
		}

		SegmentsEntryReference segmentsEntryReference =
			_getSegmentsEntryReference(
				layout.getCompanyId(),
				pageExperience.getSegmentItemExternalReference(),
				layout.getGroupId());

		return SegmentsExperienceLocalServiceUtil.updateSegmentsExperience(
			serviceContext.getUserId(),
			segmentsExperience.getSegmentsExperienceId(),
			segmentsEntryReference.getExternalReferenceCode(),
			segmentsEntryReference.getScopeExternalReferenceCode(),
			LocalizedMapUtil.getLocalizedMap(pageExperience.getName_i18n()),
			true,
			UnicodePropertiesBuilder.create(
				true
			).build());
	}

	public static void validateSegmentsExperienceLayout(Layout layout)
		throws Exception {

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTENT)) {
			throw new SegmentsExperienceLayoutException();
		}

		long plid = layout.getPlid();

		if (layout.getClassPK() > 0) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry != null) {
			throw new SegmentsExperienceLayoutException();
		}
	}

	private static String _getData(
			FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry,
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience pageExperience, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		if (ArrayUtil.isEmpty(pageExperience.getPageElements())) {
			return layoutStructure.toString();
		}

		LayoutStructureItemImporterContext layoutStructureItemImporterContext =
			new LayoutStructureItemImporterContext(
				serviceContext.getCompanyId(), fragmentEntryProcessorRegistry,
				layout.getGroupId(), infoItemServiceRegistry, layout,
				segmentsExperienceId,
				UserLocalServiceUtil.getUser(serviceContext.getUserId()));

		for (PageElement pageElement : pageExperience.getPageElements()) {
			LayoutStructureUtil.addLayoutStructureItem(
				layoutStructure, layoutStructureItemImporterContext,
				pageElement);
		}

		return layoutStructure.toString();
	}

	private static SegmentsEntryReference _getSegmentsEntryReference(
			long companyId, ItemExternalReference itemExternalReference,
			long scopeGroupId)
		throws Exception {

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return new SegmentsEntryReference(null, null);
		}

		SegmentsEntryReference segmentsEntryReference =
			new SegmentsEntryReference(
				itemExternalReference.getExternalReferenceCode(),
				ItemScopeUtil.getItemScopeExternalReferenceCode(
					itemExternalReference.getScope(), scopeGroupId));

		SegmentsEntry segmentsEntry = null;
		Long groupId = ItemScopeUtil.getItemGroupId(
			companyId, itemExternalReference.getScope(), scopeGroupId);

		if (groupId != null) {
			segmentsEntry =
				SegmentsEntryLocalServiceUtil.
					fetchSegmentsEntryByExternalReferenceCode(
						segmentsEntryReference.getExternalReferenceCode(),
						groupId);
		}

		if (segmentsEntry == null) {
			LogUtil.logOptionalReference(itemExternalReference, scopeGroupId);
		}

		return segmentsEntryReference;
	}

	private static class SegmentsEntryReference {

		protected String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		protected String getScopeExternalReferenceCode() {
			return _scopeExternalReferenceCode;
		}

		private SegmentsEntryReference(
			String externalReferenceCode, String scopeExternalReferenceCode) {

			_externalReferenceCode = externalReferenceCode;
			_scopeExternalReferenceCode = scopeExternalReferenceCode;
		}

		private final String _externalReferenceCode;
		private final String _scopeExternalReferenceCode;

	}

}