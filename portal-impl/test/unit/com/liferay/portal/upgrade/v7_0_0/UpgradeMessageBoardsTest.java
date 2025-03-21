/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Iván Zaera
 */
public class UpgradeMessageBoardsTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PropsTestUtil.setProps(
			PropsKeys.MESSAGE_BOARDS_EMAIL_HTML_FORMAT, StringPool.FALSE);

		_portletPreferences = new MockPortletPreferences();
		_upgradeMessageBoards = new UpgradeMessageBoards();
	}

	@Test
	public void testUpgradeEmailSignatureWithHtmlFormat() throws Exception {
		_portletPreferences.setValue(
			"emailHtmlFormat", Boolean.TRUE.toString());
		_portletPreferences.setValue("messageBody", "The Body");
		_portletPreferences.setValue("messageSignature", "The Signature");

		_upgradeMessageBoards.upgradeEmailSignature(
			_portletPreferences, "messageBody", "messageSignature");

		String messageBody = _portletPreferences.getValue(
			"messageBody", StringPool.BLANK);

		Assert.assertEquals("The Body<br />--<br />The Signature", messageBody);

		String messageSignature = _portletPreferences.getValue(
			"messageSignature", StringPool.BLANK);

		Assert.assertEquals(messageSignature, StringPool.BLANK);
	}

	@Test
	public void testUpgradeEmailSignatureWithNonhtmlFormat() throws Exception {
		_portletPreferences.setValue(
			"emailHtmlFormat", Boolean.FALSE.toString());
		_portletPreferences.setValue("messageBody", "The Body");
		_portletPreferences.setValue("messageSignature", "The Signature");

		_upgradeMessageBoards.upgradeEmailSignature(
			_portletPreferences, "messageBody", "messageSignature");

		String messageBody = _portletPreferences.getValue(
			"messageBody", StringPool.BLANK);

		Assert.assertEquals("The Body\n--\nThe Signature", messageBody);

		String messageSignature = _portletPreferences.getValue(
			"messageSignature", StringPool.BLANK);

		Assert.assertEquals(messageSignature, StringPool.BLANK);
	}

	@Test
	public void testUpgradeThreadPriorities() throws Exception {
		_portletPreferences.setValues(
			"priorities",
			new String[] {
				"Urgent,/message_boards/priority_urgent.png,3.0",
				"Sticky,/message_boards/priority_sticky.png,2.0",
				"Announcement,/message_boards/priority_announcement.png,1.0"
			});

		_upgradeMessageBoards.upgradeThreadPriorities(_portletPreferences);

		Assert.assertArrayEquals(
			new String[] {
				"Urgent|/message_boards/priority_urgent.png|3.0",
				"Sticky|/message_boards/priority_sticky.png|2.0",
				"Announcement|/message_boards/priority_announcement.png|1.0"
			},
			_portletPreferences.getValues(
				"priorities", StringPool.EMPTY_ARRAY));
	}

	private PortletPreferences _portletPreferences;
	private UpgradeMessageBoards _upgradeMessageBoards;

}