/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.image;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Victor Kammerer
 */
public class ImageDDMFormFieldValueRequestParameterRetrieverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGet() {
		ImageDDMFormFieldValueRequestParameterRetriever
			imageDDMFormFieldValueRequestParameterRetriever =
				new ImageDDMFormFieldValueRequestParameterRetriever();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String ddmFormFieldParameterName = RandomTestUtil.randomString();

		mockHttpServletRequest.setParameter(ddmFormFieldParameterName, "{}");

		Assert.assertEquals(
			StringPool.BLANK,
			imageDDMFormFieldValueRequestParameterRetriever.get(
				mockHttpServletRequest, ddmFormFieldParameterName, null));

		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		mockHttpServletRequest.setParameter(
			ddmFormFieldParameterName, jsonObject.toString());

		Assert.assertEquals(
			jsonObject.toString(),
			imageDDMFormFieldValueRequestParameterRetriever.get(
				mockHttpServletRequest, ddmFormFieldParameterName, null));
	}

}