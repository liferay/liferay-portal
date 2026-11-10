/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Shuyang Zhou
 */
public class SelectTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSetAttributes() {
		SelectTag selectTag = new SelectTag();

		selectTag.setListType(ListTypeConstants.ORGANIZATION_SERVICE);
		selectTag.setName("orgLaborListTypeId0");
		selectTag.setPageContext(new MockPageContext());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		selectTag.setAttributes(mockHttpServletRequest);

		Assert.assertEquals(
			"listTypeId",
			mockHttpServletRequest.getAttribute("listTypeFieldName"));
	}

}