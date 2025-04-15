/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webdav.test.rule;

import com.liferay.document.library.webdav.test.BaseWebDAVTestCase;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.webdav.methods.Method;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.runner.Description;

/**
 * @author Miguel Pastor
 * @author Shuyang Zhou
 */
public class WebDAVEnvironmentConfigClassTestRule
	extends ClassTestRule<Object> {

	public static final WebDAVEnvironmentConfigClassTestRule INSTANCE =
		new WebDAVEnvironmentConfigClassTestRule();

	@Override
	public void afterClass(Description description, Object object) {
		_baseWebDAVTestCase.service(Method.DELETE, "", null, null);
	}

	@Override
	public Object beforeClass(Description description) {
		Tuple tuple = _baseWebDAVTestCase.service(Method.MKCOL, "", null, null);

		int statusCode = BaseWebDAVTestCase.getStatusCode(tuple);

		if (statusCode == HttpServletResponse.SC_METHOD_NOT_ALLOWED) {
			_baseWebDAVTestCase.service(Method.DELETE, "", null, null);

			tuple = _baseWebDAVTestCase.service(Method.MKCOL, "", null, null);

			Assert.assertEquals(
				HttpServletResponse.SC_CREATED,
				BaseWebDAVTestCase.getStatusCode(tuple));
		}

		return null;
	}

	private WebDAVEnvironmentConfigClassTestRule() {
	}

	private static final BaseWebDAVTestCase _baseWebDAVTestCase =
		new BaseWebDAVTestCase();

}