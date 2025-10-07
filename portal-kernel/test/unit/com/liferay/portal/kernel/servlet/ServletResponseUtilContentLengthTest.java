/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.StringUtil;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hai Yu
 */
public class ServletResponseUtilContentLengthTest {

	@Test
	public void testContentLength() throws Exception {
		_testContentLength(_INPUTSTREAM_LENGTH);
		_testContentLength(_INPUTSTREAM_LENGTH + 1);
		_testContentLength(_INPUTSTREAM_LENGTH - 1);
	}

	private void _testContentLength(int contentLength) throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		String content = StringUtil.randomString(_INPUTSTREAM_LENGTH);

		ServletResponseUtil.write(
			mockHttpServletResponse,
			new ByteArrayInputStream(content.getBytes()), contentLength);

		Assert.assertEquals(
			String.valueOf(contentLength),
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));

		if (contentLength >= _INPUTSTREAM_LENGTH) {
			Assert.assertEquals(
				content, mockHttpServletResponse.getContentAsString());
		}
		else {
			Assert.assertEquals(
				content.substring(0, contentLength),
				mockHttpServletResponse.getContentAsString());
		}
	}

	private static final int _INPUTSTREAM_LENGTH = 10;

}