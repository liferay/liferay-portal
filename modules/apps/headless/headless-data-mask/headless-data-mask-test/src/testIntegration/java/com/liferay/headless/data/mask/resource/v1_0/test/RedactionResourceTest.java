/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.mask.client.dto.v1_0.Redaction;
import com.liferay.headless.data.mask.client.problem.Problem;

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
		_testRedactsWithReplacementRegex();
		_testReturnsErrorForInvalidRegex();
		_testThrowsForMissingRequiredFields();
	}

	private Redaction _getRedaction(
			String detectionRegex, String replacementRegex,
			String replacementValue, String text)
		throws Exception {

		return redactionResource.getRedaction(
			detectionRegex, replacementRegex, replacementValue, text);
	}

	private void _testRedactsWithReplacementRegex() throws Exception {
		Redaction redaction = _getRedaction(
			"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b",
			"(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}", "$1.0/24",
			"Connected from 192.168.1.42");

		Assert.assertNull(redaction.getError());
		Assert.assertEquals(
			"Connected from 192.168.1.0/24", redaction.getOutput());
	}

	private void _testReturnsErrorForInvalidRegex() throws Exception {
		Redaction redaction = _getRedaction(
			"[unclosed", null, "[REDACTED]", "anything");

		Assert.assertNotNull(redaction.getError());
		Assert.assertEquals("anything", redaction.getOutput());

		redaction = _getRedaction(
			_EMAIL_DETECTION_REGEX, "[unclosed", "[REDACTED]",
			"alice@example.com");

		Assert.assertNotNull(redaction.getError());
		Assert.assertEquals("alice@example.com", redaction.getOutput());
	}

	private void _testThrowsForMissingRequiredFields() {
		Assert.assertThrows(
			Problem.ProblemException.class,
			() -> _getRedaction(null, null, "[REDACTED]", "anything"));
		Assert.assertThrows(
			Problem.ProblemException.class,
			() -> _getRedaction(
				_EMAIL_DETECTION_REGEX, null, null, "anything"));
		Assert.assertThrows(
			Problem.ProblemException.class,
			() -> _getRedaction(_EMAIL_DETECTION_REGEX, null, "[EMAIL]", null));
	}

	private static final String _EMAIL_DETECTION_REGEX =
		"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b";

}