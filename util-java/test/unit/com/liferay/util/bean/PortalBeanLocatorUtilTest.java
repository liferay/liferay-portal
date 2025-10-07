/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.bean;

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Miguel Pastor
 */
public class PortalBeanLocatorUtilTest {

	@After
	public void tearDown() {
		PortalBeanLocatorUtil.setBeanLocator(null);
	}

	@Test
	public void testBeanLocatorHasNotBeenSet() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalBeanLocatorUtil.class.getName(), LoggerTestUtil.ERROR)) {

			try {
				PortalBeanLocatorUtil.locate("beanName");

				Assert.fail();
			}
			catch (BeanLocatorException beanLocatorException) {
				Assert.assertEquals(
					"BeanLocator is not set",
					beanLocatorException.getMessage());

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					"BeanLocator is null", logEntry.getMessage());
			}
		}
	}

	@Test
	public void testLocateExistingBean() {
		Mockito.when(
			_beanLocator.locate("existingBean")
		).thenReturn(
			new String("existingBean")
		);

		PortalBeanLocatorUtil.setBeanLocator(_beanLocator);

		String bean = (String)PortalBeanLocatorUtil.locate("existingBean");

		Assert.assertNotNull(bean);
		Assert.assertEquals("existingBean", bean);

		Mockito.verify(
			_beanLocator, Mockito.times(1)
		).locate(
			"existingBean"
		);
	}

	@Test
	public void testLocateNonexistingBean() {
		Mockito.when(
			_beanLocator.locate("nonExistingBean")
		).thenReturn(
			null
		);

		PortalBeanLocatorUtil.setBeanLocator(_beanLocator);

		String bean = (String)PortalBeanLocatorUtil.locate("nonExistingBean");

		Assert.assertNull(bean);

		Mockito.verify(
			_beanLocator, Mockito.times(1)
		).locate(
			"nonExistingBean"
		);
	}

	private final BeanLocator _beanLocator = Mockito.mock(BeanLocator.class);

}