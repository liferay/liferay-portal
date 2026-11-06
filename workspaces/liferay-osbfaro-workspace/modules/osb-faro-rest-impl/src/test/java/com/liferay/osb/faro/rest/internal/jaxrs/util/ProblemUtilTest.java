/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marcos Martins
 */
public class ProblemUtilTest {

	@Test
	public void testGetForbiddenException() {
		String title = RandomTestUtil.randomString();

		ForbiddenException forbiddenException =
			ProblemUtil.getForbiddenException(title);

		Assert.assertEquals(title, forbiddenException.getMessage());

		_assertResponse(
			forbiddenException.getResponse(), Response.Status.FORBIDDEN, title);
	}

	@Test
	public void testGetNotFoundException() {
		String title = RandomTestUtil.randomString();

		NotFoundException notFoundException = ProblemUtil.getNotFoundException(
			title);

		Assert.assertEquals(title, notFoundException.getMessage());

		_assertResponse(
			notFoundException.getResponse(), Response.Status.NOT_FOUND, title);
	}

	@Test
	public void testToResponse() {
		String title = RandomTestUtil.randomString();

		_assertResponse(
			ProblemUtil.toResponse(Response.Status.BAD_REQUEST, title),
			Response.Status.BAD_REQUEST, title);
	}

	private void _assertResponse(
		Response response, Response.Status status, String title) {

		Assert.assertEquals(
			MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		Assert.assertEquals(status.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(status, problem.getStatus());
		Assert.assertEquals(title, problem.getTitle());
		Assert.assertNull(problem.getType());
	}

}