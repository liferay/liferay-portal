/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.bean;

import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public class BeanParamUtilTest {

	@Test
	public void testGetBigDecimalAsDouble() {
		double defaultValue = RandomTestUtil.randomDouble();
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getBigDecimalAsDouble(
				null, mockHttpServletRequest, "value", Bean::getBigDecimal,
				defaultValue),
			0);

		Bean bean = new Bean();

		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getBigDecimalAsDouble(
				bean, mockHttpServletRequest, "value", Bean::getNullBigDecimal,
				defaultValue),
			0);

		BigDecimal bigDecimal = bean.getBigDecimal();

		Assert.assertEquals(
			bigDecimal.doubleValue(),
			BeanParamUtil.getBigDecimalAsDouble(
				bean, mockHttpServletRequest, "value", Bean::getBigDecimal,
				defaultValue),
			0);

		double value = RandomTestUtil.randomDouble();

		mockHttpServletRequest.addParameter("value", String.valueOf(value));

		Assert.assertEquals(
			value,
			BeanParamUtil.getBigDecimalAsDouble(
				bean, mockHttpServletRequest, "value", Bean::getBigDecimal,
				defaultValue),
			0);
	}

	@Test
	public void testGetBoolean() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertFalse(
			BeanParamUtil.getBoolean(
				null, mockHttpServletRequest, "value", Bean::getBoolean));
		Assert.assertTrue(
			BeanParamUtil.getBoolean(
				null, mockHttpServletRequest, "value", Bean::getBoolean, true));

		Bean bean = new Bean();

		Assert.assertEquals(
			bean.getBoolean(),
			BeanParamUtil.getBoolean(
				bean, mockHttpServletRequest, "value", Bean::getBoolean,
				!bean.getBoolean()));

		MockPortletRequest mockPortletRequest = new MockPortletRequest();

		Assert.assertFalse(
			BeanParamUtil.getBoolean(
				null, mockPortletRequest, "value", Bean::getBoolean));
		Assert.assertTrue(
			BeanParamUtil.getBoolean(
				null, mockPortletRequest, "value", Bean::getBoolean, true));
		Assert.assertEquals(
			bean.getBoolean(),
			BeanParamUtil.getBoolean(
				bean, mockPortletRequest, "value", Bean::getBoolean,
				!bean.getBoolean()));

		mockHttpServletRequest.addParameter(
			"value", String.valueOf(!bean.getBoolean()));
		mockPortletRequest.addParameter(
			"value", String.valueOf(!bean.getBoolean()));

		Assert.assertEquals(
			!bean.getBoolean(),
			BeanParamUtil.getBoolean(
				bean, mockHttpServletRequest, "value", Bean::getBoolean));
		Assert.assertEquals(
			!bean.getBoolean(),
			BeanParamUtil.getBoolean(
				bean, mockPortletRequest, "value", Bean::getBoolean));
	}

	@Test
	public void testGetDouble() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertEquals(
			0,
			BeanParamUtil.getDouble(
				null, mockHttpServletRequest, "value", Bean::getDouble),
			0);

		double defaultValue = RandomTestUtil.randomDouble();

		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getDouble(
				null, mockHttpServletRequest, "value", Bean::getDouble,
				defaultValue),
			0);

		Bean bean = new Bean();

		Assert.assertEquals(
			bean.getDouble(),
			BeanParamUtil.getDouble(
				bean, mockHttpServletRequest, "value", Bean::getDouble,
				defaultValue),
			0);

		double value = RandomTestUtil.randomDouble();

		mockHttpServletRequest.addParameter("value", String.valueOf(value));

		Assert.assertEquals(
			value,
			BeanParamUtil.getDouble(
				bean, mockHttpServletRequest, "value", Bean::getDouble),
			0);
	}

	@Test
	public void testGetInteger() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertEquals(
			0,
			BeanParamUtil.getInteger(
				null, mockHttpServletRequest, "value", Bean::getInt));

		int defaultValue = RandomTestUtil.randomInt();

		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getInteger(
				null, mockHttpServletRequest, "value", Bean::getInt,
				defaultValue));

		Bean bean = new Bean();

		Assert.assertEquals(
			bean.getInt(),
			BeanParamUtil.getInteger(
				bean, mockHttpServletRequest, "value", Bean::getInt,
				defaultValue));

		int value = RandomTestUtil.randomInt();

		mockHttpServletRequest.addParameter("value", String.valueOf(value));

		Assert.assertEquals(
			value,
			BeanParamUtil.getInteger(
				bean, mockHttpServletRequest, "value", Bean::getInt));
	}

	@Test
	public void testGetLong() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertEquals(
			0,
			BeanParamUtil.getLong(
				null, mockHttpServletRequest, "value", Bean::getLong));

		long defaultValue = RandomTestUtil.randomLong();

		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getLong(
				null, mockHttpServletRequest, "value", Bean::getLong,
				defaultValue));

		Bean bean = new Bean();

		Assert.assertEquals(
			bean.getLong(),
			BeanParamUtil.getLong(
				bean, mockHttpServletRequest, "value", Bean::getLong,
				defaultValue));

		MockPortletRequest mockPortletRequest = new MockPortletRequest();

		Assert.assertEquals(
			0,
			BeanParamUtil.getLong(
				null, mockPortletRequest, "value", Bean::getLong));
		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getLong(
				null, mockPortletRequest, "value", Bean::getLong,
				defaultValue));
		Assert.assertEquals(
			bean.getLong(),
			BeanParamUtil.getLong(
				bean, mockPortletRequest, "value", Bean::getLong,
				defaultValue));

		long value = RandomTestUtil.randomLong();

		mockHttpServletRequest.addParameter("value", String.valueOf(value));
		mockPortletRequest.addParameter("value", String.valueOf(value));

		Assert.assertEquals(
			value,
			BeanParamUtil.getLong(
				bean, mockHttpServletRequest, "value", Bean::getLong));
		Assert.assertEquals(
			value,
			BeanParamUtil.getLong(
				bean, mockPortletRequest, "value", Bean::getLong));
	}

	@Test
	public void testGetString() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertEquals(
			"",
			BeanParamUtil.getString(
				null, mockHttpServletRequest, "value", Bean::getString));

		String defaultValue = RandomTestUtil.randomString();

		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getString(
				null, mockHttpServletRequest, "value", Bean::getString,
				defaultValue));

		Bean bean = new Bean();

		Assert.assertEquals(
			bean.getString(),
			BeanParamUtil.getString(
				bean, mockHttpServletRequest, "value", Bean::getString,
				defaultValue));

		MockPortletRequest mockPortletRequest = new MockPortletRequest();

		Assert.assertEquals(
			"",
			BeanParamUtil.getString(
				null, mockPortletRequest, "value", Bean::getString));
		Assert.assertEquals(
			defaultValue,
			BeanParamUtil.getString(
				null, mockPortletRequest, "value", Bean::getString,
				defaultValue));
		Assert.assertEquals(
			bean.getString(),
			BeanParamUtil.getString(
				bean, mockPortletRequest, "value", Bean::getString,
				defaultValue));

		String value = RandomTestUtil.randomString();

		mockHttpServletRequest.addParameter("value", value);
		mockPortletRequest.addParameter("value", value);

		Assert.assertEquals(
			value,
			BeanParamUtil.getString(
				bean, mockHttpServletRequest, "value", Bean::getString));
		Assert.assertEquals(
			value,
			BeanParamUtil.getString(
				bean, mockPortletRequest, "value", Bean::getString));
	}

	private static class Bean {

		public BigDecimal getBigDecimal() {
			return _bigDecimal;
		}

		public boolean getBoolean() {
			return _boolean;
		}

		public double getDouble() {
			return _double;
		}

		public int getInt() {
			return _int;
		}

		public long getLong() {
			return _long;
		}

		public BigDecimal getNullBigDecimal() {
			return null;
		}

		public String getString() {
			return _string;
		}

		private final BigDecimal _bigDecimal = BigDecimal.valueOf(
			RandomTestUtil.randomDouble());
		private final boolean _boolean = RandomTestUtil.randomBoolean();
		private final double _double = RandomTestUtil.randomDouble();
		private final int _int = RandomTestUtil.randomInt();
		private final long _long = RandomTestUtil.randomLong();
		private final String _string = RandomTestUtil.randomString();

	}

}