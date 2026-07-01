/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eric Yan
 */
public class BaseFilterTest {

	@Test
	public void testDoFilter() throws Exception {
		_testDoFilter(new Exception());
		_testDoFilter(new SystemException());
	}

	private void _testDoFilter(Exception exception) throws Exception {
		BaseFilter baseFilter = new BaseFilter() {

			@Override
			protected Log getLog() {
				return LogFactoryUtil.getLog(BaseFilterTest.class);
			}

			@Override
			protected void processFilter(
					HttpServletRequest httpServletRequest,
					HttpServletResponse httpServletResponse,
					FilterChain filterChain)
				throws Exception {

				throw exception;
			}

		};

		try {
			baseFilter.doFilter(
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				new MockFilterChain());

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertSame(exception, runtimeException);
		}
		catch (ServletException servletException) {
			Assert.assertSame(exception, servletException.getRootCause());
		}
	}

}