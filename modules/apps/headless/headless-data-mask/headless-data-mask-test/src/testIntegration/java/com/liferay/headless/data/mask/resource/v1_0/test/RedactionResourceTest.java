/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.mask.client.dto.v1_0.Redaction;
import com.liferay.headless.data.mask.client.problem.Problem;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class RedactionResourceTest extends BaseRedactionResourceTestCase {

	@Override
	@Test
	public void testGetRedaction() throws Exception {
		Redaction redaction = redactionResource.getRedaction(
			"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b",
			"(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}", "$1.0/24",
			"Connected from 192.168.1.42");

		Assert.assertNull(redaction.getError());
		Assert.assertEquals(
			"Connected from 192.168.1.0/24", redaction.getOutput());

		redaction = redactionResource.getRedaction(
			"[unclosed", null, "[REDACTED]", "anything");

		Assert.assertNotNull(redaction.getError());
		Assert.assertEquals("anything", redaction.getOutput());

		redaction = redactionResource.getRedaction(
			"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b",
			"[unclosed", "[REDACTED]", "example@example.com");

		Assert.assertNotNull(redaction.getError());
		Assert.assertEquals("example@example.com", redaction.getOutput());

		String catastrophicText = "a".repeat(40);

		redaction = redactionResource.getRedaction(
			"(.*a){40}", null, "R", catastrophicText);

		Assert.assertNotNull(redaction.getError());
		Assert.assertEquals(catastrophicText, redaction.getOutput());

		Problem.ProblemException problemException = Assert.assertThrows(
			Problem.ProblemException.class,
			() -> redactionResource.getRedaction(
				null, null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString()));

		Problem problem = problemException.getProblem();

		Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		Assert.assertEquals(
			"getRedaction.arg0 must not be null\n", problem.getTitle());

		problemException = Assert.assertThrows(
			Problem.ProblemException.class,
			() -> redactionResource.getRedaction(
				RandomTestUtil.randomString(), null, null,
				RandomTestUtil.randomString()));

		problem = problemException.getProblem();

		Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		Assert.assertEquals(
			"getRedaction.arg2 must not be null\n", problem.getTitle());

		problemException = Assert.assertThrows(
			Problem.ProblemException.class,
			() -> redactionResource.getRedaction(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), null));

		problem = problemException.getProblem();

		Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		Assert.assertEquals(
			"getRedaction.arg3 must not be null\n", problem.getTitle());
	}

}