/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Daniel Raposo
 */
public class DefaultExportImportBackgroundTaskStatusMessageTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-75919")
	public void testTranslateBatchProgressMessageWhenTotalsAreKnown() {
		BackgroundTaskStatus backgroundTaskStatus = new BackgroundTaskStatus();

		backgroundTaskStatus.setAttribute(
			"allModelAdditionCountersTotal", 200L);

		_defaultExportImportBackgroundTaskStatusMessageTranslator.translate(
			backgroundTaskStatus, _createMessage(50));

		Assert.assertEquals(
			Integer.valueOf(25),
			backgroundTaskStatus.getAttribute("percentage"));
	}

	@Test
	@TestInfo("LPD-75919")
	public void testTranslateBatchProgressMessageWhenTotalsAreUnknown() {
		BackgroundTaskStatus backgroundTaskStatus = new BackgroundTaskStatus();

		_defaultExportImportBackgroundTaskStatusMessageTranslator.translate(
			backgroundTaskStatus, _createMessage(1));

		Assert.assertNull(backgroundTaskStatus.getAttribute("percentage"));
	}

	private Message _createMessage(int batchEngineProcessedItemsCount) {
		Message message = new Message();

		message.put(
			"batchEngineProcessedItemsCount", batchEngineProcessedItemsCount);
		message.put("messageType", "batchProgress");

		return message;
	}

	private final DefaultExportImportBackgroundTaskStatusMessageTranslator
		_defaultExportImportBackgroundTaskStatusMessageTranslator =
			new DefaultExportImportBackgroundTaskStatusMessageTranslator();

}