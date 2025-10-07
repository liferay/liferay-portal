/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class PageExperiencesTestUtil {

	public static void assertPageExperiences(
		PageExperience[] expectedPageExperiences, Layout layout,
		PageExperience[] pageExperiences) {

		PageExperience expectedPageExperience = expectedPageExperiences[0];

		Assert.assertEquals(
			pageExperiences.toString(), 1, pageExperiences.length);

		PageExperience pageExperience = pageExperiences[0];

		Assert.assertEquals(
			expectedPageExperience.getExternalReferenceCode(),
			pageExperience.getExternalReferenceCode());
		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			pageExperience.getPageSpecificationExternalReferenceCode());

		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperience(
				layout.getPlid());

		Assert.assertEquals(
			segmentsExperience.getExternalReferenceCode(),
			pageExperience.getExternalReferenceCode());
	}

	public static PageExperience[] getPageExperiences(
		String contentPageSpecificationExternalReferenceCode) {

		return new PageExperience[] {
			new PageExperience() {
				{
					setExternalReferenceCode(RandomTestUtil::randomString);
					setKey(SegmentsExperienceConstants.KEY_DEFAULT);
					setName_i18n(
						Collections.singletonMap(
							"en-US", RandomTestUtil.randomString()));
					setPageElements(
						PageElementsTestUtil.getPageElements(
							RandomTestUtil.randomInt(1, 3), StringPool.BLANK));
					setPageSpecificationExternalReferenceCode(
						contentPageSpecificationExternalReferenceCode);
				}
			}
		};
	}

	public static void modifyPageExperiences(PageExperience[] pageExperiences) {
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
							RandomTestUtil.randomInt(1, 3), StringPool.BLANK);

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

}