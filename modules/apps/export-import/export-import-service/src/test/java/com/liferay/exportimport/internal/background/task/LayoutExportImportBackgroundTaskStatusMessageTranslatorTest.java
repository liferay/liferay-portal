/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jaime Leon
 */
public class LayoutExportImportBackgroundTaskStatusMessageTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-109895")
	public void testTranslateLayoutMessageKeepsPhases() {
		BackgroundTaskStatus backgroundTaskStatus = new BackgroundTaskStatus();

		backgroundTaskStatus.setAttribute("phasesTotal", 3);

		_layoutExportImportBackgroundTaskStatusMessageTranslator.translate(
			backgroundTaskStatus, _createLayoutMessage(10));

		Assert.assertEquals(0, backgroundTaskStatus.getAttribute("phaseIndex"));
		Assert.assertEquals(
			3, backgroundTaskStatus.getAttribute("phasesTotal"));
		Assert.assertEquals(
			10L,
			backgroundTaskStatus.getAttribute("allModelAdditionCountersTotal"));

		backgroundTaskStatus.setAttribute(
			"currentModelAdditionCountersTotal", 10L);

		_layoutExportImportBackgroundTaskStatusMessageTranslator.translate(
			backgroundTaskStatus, _createLayoutMessage(20));

		Assert.assertEquals(1, backgroundTaskStatus.getAttribute("phaseIndex"));
		Assert.assertEquals(
			3, backgroundTaskStatus.getAttribute("phasesTotal"));
		Assert.assertEquals(
			20L,
			backgroundTaskStatus.getAttribute("allModelAdditionCountersTotal"));
		Assert.assertEquals(
			0L,
			backgroundTaskStatus.getAttribute(
				"currentModelAdditionCountersTotal"));
	}

	@Test
	@TestInfo("LPD-109895")
	public void testTranslateLayoutMessageWithoutPhasesTotal() {
		BackgroundTaskStatus backgroundTaskStatus = new BackgroundTaskStatus();

		_layoutExportImportBackgroundTaskStatusMessageTranslator.translate(
			backgroundTaskStatus, _createLayoutMessage(10));

		Assert.assertEquals(0, backgroundTaskStatus.getAttribute("phaseIndex"));
		Assert.assertNull(backgroundTaskStatus.getAttribute("phasesTotal"));
	}

	private Message _createLayoutMessage(long modelAdditionCountersTotal) {
		Message message = new Message();

		message.put("messageType", "layout");
		message.put(
			"modelAdditionCounters",
			HashMapBuilder.put(
				RandomTestUtil.randomString(),
				new LongWrapper(modelAdditionCountersTotal)
			).build());

		return message;
	}

	private final LayoutExportImportBackgroundTaskStatusMessageTranslator
		_layoutExportImportBackgroundTaskStatusMessageTranslator =
			new LayoutExportImportBackgroundTaskStatusMessageTranslator();

}