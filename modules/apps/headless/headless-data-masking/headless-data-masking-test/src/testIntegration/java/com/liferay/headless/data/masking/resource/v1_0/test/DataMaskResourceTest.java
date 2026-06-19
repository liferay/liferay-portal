/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.masking.client.dto.v1_0.DataMaskPreviewRequest;
import com.liferay.headless.data.masking.client.dto.v1_0.DataMaskPreviewResult;
import com.liferay.headless.data.masking.client.problem.Problem;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class DataMaskResourceTest extends BaseDataMaskResourceTestCase {

	@Override
	@Test
	public void testPostDataMaskPreview() throws Exception {
		super.testPostDataMaskPreview();

		DataMaskPreviewResult dataMaskPreviewResult = _previewDataMask(
			_EMAIL_DETECTION_REGEX, null, "[EMAIL]",
			"From alice@example.com to bob@example.org");

		Assert.assertNull(dataMaskPreviewResult.getError());
		Assert.assertEquals(
			"From [EMAIL] to [EMAIL]", dataMaskPreviewResult.getOutput());

		dataMaskPreviewResult = _previewDataMask(
			"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b",
			"(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}", "$1.0/24",
			"Connected from 192.168.1.42");

		Assert.assertNull(dataMaskPreviewResult.getError());
		Assert.assertEquals(
			"Connected from 192.168.1.0/24", dataMaskPreviewResult.getOutput());

		dataMaskPreviewResult = _previewDataMask(
			_EMAIL_DETECTION_REGEX, null, "[EMAIL]", "No email here at all.");

		Assert.assertNull(dataMaskPreviewResult.getError());
		Assert.assertEquals(
			"No email here at all.", dataMaskPreviewResult.getOutput());

		dataMaskPreviewResult = _previewDataMask(
			"[unclosed", null, "[REDACTED]", "anything");

		Assert.assertNotNull(dataMaskPreviewResult.getError());
		Assert.assertEquals("anything", dataMaskPreviewResult.getOutput());

		dataMaskPreviewResult = _previewDataMask(
			_EMAIL_DETECTION_REGEX, "[unclosed", "[REDACTED]",
			"alice@example.com");

		Assert.assertNotNull(dataMaskPreviewResult.getError());
		Assert.assertEquals(
			"alice@example.com", dataMaskPreviewResult.getOutput());

		Assert.assertThrows(
			Problem.ProblemException.class,
			() -> _previewDataMask(null, null, "[REDACTED]", "anything"));
		Assert.assertThrows(
			Problem.ProblemException.class,
			() -> _previewDataMask(
				_EMAIL_DETECTION_REGEX, null, null, "anything"));
		Assert.assertThrows(
			Problem.ProblemException.class,
			() -> _previewDataMask(
				_EMAIL_DETECTION_REGEX, null, "[EMAIL]", null));
	}

	private DataMaskPreviewResult _previewDataMask(
			String detectionRegex, String replacementRegex,
			String replacementValue, String sampleText)
		throws Exception {

		DataMaskPreviewRequest dataMaskPreviewRequest =
			new DataMaskPreviewRequest();

		dataMaskPreviewRequest.setDetectionRegex(detectionRegex);
		dataMaskPreviewRequest.setReplacementRegex(replacementRegex);
		dataMaskPreviewRequest.setReplacementValue(replacementValue);
		dataMaskPreviewRequest.setSampleText(sampleText);

		return dataMaskResource.postDataMaskPreview(dataMaskPreviewRequest);
	}

	private static final String _EMAIL_DETECTION_REGEX =
		"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b";

}