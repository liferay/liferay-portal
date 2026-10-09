/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class PageExperiencesTestUtil {

	public static void assertPageExperiences(
		PageExperience[] expectedPageExperiences, Layout layout,
		PageExperience[] pageExperiences) {

		if (_isMultiplePageExperiencesSupported(layout)) {
			Assert.assertEquals(
				Arrays.toString(pageExperiences),
				expectedPageExperiences.length, pageExperiences.length);
		}
		else {
			Assert.assertEquals(
				Arrays.toString(expectedPageExperiences), 1,
				expectedPageExperiences.length);
			Assert.assertEquals(
				Arrays.toString(pageExperiences), 1, pageExperiences.length);
		}

		Map<String, PageExperience> pageExperiencesMap = new HashMap<>();

		for (PageExperience pageExperience : pageExperiences) {
			pageExperiencesMap.put(pageExperience.getKey(), pageExperience);
		}

		for (PageExperience expectedPageExperience : expectedPageExperiences) {
			PageExperience pageExperience = pageExperiencesMap.get(
				expectedPageExperience.getKey());

			Assert.assertEquals(
				expectedPageExperience.getExternalReferenceCode(),
				pageExperience.getExternalReferenceCode());
			Assert.assertEquals(
				layout.getExternalReferenceCode(),
				pageExperience.getPageSpecificationExternalReferenceCode());

			if (expectedPageExperience.getUuid() == null) {
				continue;
			}

			Assert.assertEquals(
				expectedPageExperience.getUuid(), pageExperience.getUuid());
		}

		PageExperience defaultPageExperience = pageExperiencesMap.get(
			SegmentsExperienceConstants.KEY_DEFAULT);

		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperience(
				layout.getPlid());

		Assert.assertEquals(
			segmentsExperience.getExternalReferenceCode(),
			defaultPageExperience.getExternalReferenceCode());
	}

	public static PageExperience getDefaultPageExperience(
		PageExperience[] pageExperiences) {

		for (PageExperience pageExperience : pageExperiences) {
			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				return pageExperience;
			}
		}

		return null;
	}

	public static PageExperience getDefaultPageExperience(
		String externalReferenceCode, PageElement[] pageElements,
		String pageSpecificationExternalReferenceCode, String uuid) {

		PageExperience pageExperience = getPageExperience(
			externalReferenceCode, SegmentsExperienceConstants.KEY_DEFAULT, 0,
			uuid);

		pageExperience.setPageElements(pageElements);
		pageExperience.setPageSpecificationExternalReferenceCode(
			pageSpecificationExternalReferenceCode);

		return pageExperience;
	}

	public static PageExperience[] getDefaultPageExperiences(
		PageElement[] pageElements,
		String pageSpecificationExternalReferenceCode) {

		PageExperience pageExperience = new PageExperience();

		pageExperience.setExternalReferenceCode(RandomTestUtil::randomString);
		pageExperience.setKey(SegmentsExperienceConstants.KEY_DEFAULT);
		pageExperience.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		pageExperience.setPageElements(pageElements);
		pageExperience.setPageSpecificationExternalReferenceCode(
			pageSpecificationExternalReferenceCode);
		pageExperience.setUuid(RandomTestUtil::randomString);

		return new PageExperience[] {pageExperience};
	}

	public static PageExperience getPageExperience() {
		PageExperience pageExperience = new PageExperience();

		pageExperience.setExternalReferenceCode(RandomTestUtil::randomString);
		pageExperience.setKey(RandomTestUtil.randomString());
		pageExperience.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		pageExperience.setUuid(RandomTestUtil::randomString);

		return pageExperience;
	}

	public static PageExperience getPageExperience(
			String pageSpecificationExternalReferenceCode, int priority,
			long scopeGroupId, SegmentsEntry segmentsEntry)
		throws Exception {

		PageExperience pageExperience = _getPageExperience(
			pageSpecificationExternalReferenceCode, priority, scopeGroupId);

		if (segmentsEntry != null) {
			pageExperience.setSegmentItemExternalReference(
				() -> ReferencesTestUtil.getItemExternalReference(
					segmentsEntry, scopeGroupId));
		}

		return pageExperience;
	}

	public static PageExperience getPageExperience(
			String pageSpecificationExternalReferenceCode, int priority,
			long scopeGroupId, String segmentsEntryERC,
			String segmentsEntryScopeERC)
		throws Exception {

		PageExperience pageExperience = _getPageExperience(
			pageSpecificationExternalReferenceCode, priority, scopeGroupId);

		if (Validator.isNotNull(segmentsEntryERC)) {
			pageExperience.setSegmentItemExternalReference(
				() -> ReferencesTestUtil.getItemExternalReference(
					SegmentsEntry.class.getName(), segmentsEntryERC,
					segmentsEntryScopeERC));
		}

		return pageExperience;
	}

	public static PageExperience getPageExperience(
		String externalReferenceCode, String key, int priority, String uuid) {

		PageExperience pageExperience = new PageExperience();

		pageExperience.setExternalReferenceCode(externalReferenceCode);
		pageExperience.setKey(key);
		pageExperience.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		pageExperience.setPriority(priority);
		pageExperience.setUuid(uuid);

		return pageExperience;
	}

	public static PageExperience[] getPageExperiences(
			long companyGroupId, long groupId,
			String pageSpecificationExternalReferenceCode)
		throws Exception {

		return new PageExperience[] {
			getPageExperience(
				pageSpecificationExternalReferenceCode, 3, groupId,
				SegmentsTestUtil.addSegmentsEntry(groupId)),
			getPageExperience(
				pageSpecificationExternalReferenceCode, 2, groupId, null),
			getPageExperience(
				pageSpecificationExternalReferenceCode, 1, groupId,
				SegmentsTestUtil.addSegmentsEntry(companyGroupId))
		};
	}

	public static void modifyPageExperiences(
		PageExperience[] pageExperiences, long scopeGroupId) {

		for (PageExperience pageExperience : pageExperiences) {
			List<PageElement> dropZonePageElements =
				TransformUtil.transformToList(
					pageExperience.getPageElements(),
					pageElement -> {
						PageElementDefinition pageElementDefinition =
							pageElement.getPageElementDefinition();

						if (Objects.equals(
								pageElementDefinition.getType(),
								PageElementDefinition.Type.DROP_ZONE)) {

							return pageElement;
						}

						return null;
					});

			pageExperience.setPageElements(
				() -> {
					PageElement[] pageElements =
						PageElementsTestUtil.getPageElements(
							RandomTestUtil.randomInt(1, 3), StringPool.BLANK,
							scopeGroupId);

					if (ListUtil.isEmpty(dropZonePageElements)) {
						return pageElements;
					}

					for (int i = 0; i < dropZonePageElements.size(); i++) {
						PageElement pageElement = dropZonePageElements.get(i);

						pageElement.setPosition(pageElements.length + i);
					}

					return ArrayUtil.append(
						pageElements,
						dropZonePageElements.toArray(new PageElement[0]));
				});
		}
	}

	public static PageExperience toDraftPageExperience(
		String draftContentPageSpecificationExternalReferenceCode,
		PageExperience publishedPageExperience) {

		PageExperience pageExperience = new PageExperience();

		if (SegmentsExperienceConstants.KEY_DEFAULT.equals(
				publishedPageExperience.getKey())) {

			pageExperience.setExternalReferenceCode(
				draftContentPageSpecificationExternalReferenceCode +
					LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT);
		}
		else {
			pageExperience.setExternalReferenceCode(
				publishedPageExperience.getExternalReferenceCode() +
					LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT);
		}

		pageExperience.setKey(publishedPageExperience.getKey());

		return pageExperience;
	}

	public static PageExperience toPublishedPageExperience(
		PageExperience draftPageExperience,
		String publishedContentPageSpecificationExternalReferenceCode) {

		PageExperience pageExperience = new PageExperience();

		String draftExternalReferenceCode =
			draftPageExperience.getExternalReferenceCode();

		if (SegmentsExperienceConstants.KEY_DEFAULT.equals(
				draftPageExperience.getKey())) {

			pageExperience.setExternalReferenceCode(
				publishedContentPageSpecificationExternalReferenceCode +
					LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT);
		}
		else if (draftExternalReferenceCode.endsWith(
					LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT)) {

			int suffixDraftLength =
				LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT.length();

			pageExperience.setExternalReferenceCode(
				draftExternalReferenceCode.substring(
					0,
					draftExternalReferenceCode.length() - suffixDraftLength));
		}
		else {
			pageExperience.setExternalReferenceCode(
				draftExternalReferenceCode +
					LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PUBLISHED);
		}

		pageExperience.setKey(draftPageExperience.getKey());

		return pageExperience;
	}

	private static PageExperience _getPageExperience(
			String pageSpecificationExternalReferenceCode, int priority,
			long scopeGroupId)
		throws Exception {

		PageExperience pageExperience = getPageExperience();

		pageExperience.setPageElements(
			PageElementsTestUtil.getPageElements(scopeGroupId));
		pageExperience.setPageSpecificationExternalReferenceCode(
			pageSpecificationExternalReferenceCode);
		pageExperience.setPriority(priority);

		return pageExperience;
	}

	private static boolean _isMultiplePageExperiencesSupported(Layout layout) {
		if (!layout.isTypeContent()) {
			return false;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			return true;
		}

		return false;
	}

}