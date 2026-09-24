/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 */
public class SaveFormInstanceMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpSaveFormInstanceMVCResourceCommand();
	}

	@Test
	public void testFormatDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, 3);
		calendar.set(Calendar.DAY_OF_MONTH, 18);
		calendar.set(Calendar.HOUR_OF_DAY, 14);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Date date = calendar.getTime();

		Assert.assertEquals(
			_getExpectedFormattedDate("AM", "11:00"),
			_saveFormInstanceMVCResourceCommand.formatDate(
				date, LocaleUtil.US, "America/Sao_Paulo"));
		Assert.assertEquals(
			_getExpectedFormattedDate("PM", "2:00"),
			_saveFormInstanceMVCResourceCommand.formatDate(
				date, LocaleUtil.US, "UTC"));
	}

	private String _getExpectedFormattedDate(String amPm, String time) {
		if (JavaDetector.getJavaSpecificationVersion() >= 20) {
			return StringBundler.concat("Apr 18, 2018, ", time, "\u202F", amPm);
		}

		return StringBundler.concat("Apr 18, 2018, ", time, " ", amPm);
	}

	private void _setUpSaveFormInstanceMVCResourceCommand() throws Exception {
		_saveFormInstanceMVCResourceCommand =
			new SaveFormInstanceMVCResourceCommand();
	}

	private SaveFormInstanceMVCResourceCommand
		_saveFormInstanceMVCResourceCommand;

}