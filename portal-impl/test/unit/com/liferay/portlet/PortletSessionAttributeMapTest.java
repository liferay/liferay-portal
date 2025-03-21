/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpSession;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpSession;

/**
 * @author Shuyang Zhou
 */
public class PortletSessionAttributeMapTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() {
		_httpSession.setAttribute(_SCOPE_PREFIX.concat(_KEY_1), _value1);
		_httpSession.setAttribute(_SCOPE_PREFIX.concat(_KEY_2), _value2);
		_httpSession.setAttribute(_SCOPE_PREFIX.concat(_KEY_3), _value3);
	}

	@Test
	public void testConstructor() {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(_httpSession);

		Assert.assertSame(_httpSession, portletSessionAttributeMap.httpSession);
		Assert.assertNull(portletSessionAttributeMap.scopePrefix);

		portletSessionAttributeMap = new PortletSessionAttributeMap(
			_httpSession, null);

		Assert.assertSame(_httpSession, portletSessionAttributeMap.httpSession);
		Assert.assertNull(portletSessionAttributeMap.scopePrefix);

		String scopePrefix = "scopePrefix";

		portletSessionAttributeMap = new PortletSessionAttributeMap(
			_httpSession, scopePrefix);

		Assert.assertSame(_httpSession, portletSessionAttributeMap.httpSession);
		Assert.assertSame(scopePrefix, portletSessionAttributeMap.scopePrefix);
	}

	@Test
	public void testContainsKey() {
		testContainsKey(true);
		testContainsKey(false);
	}

	@Test
	public void testContainsValue() {
		testContainsValue(true);
		testContainsValue(false);
	}

	@Test
	public void testEntrySet() {
		testEntrySet(true);
		testEntrySet(false);
	}

	@Test
	public void testEqualsAndHashCode() {
		testEqualsAndHashCode(true);
		testEqualsAndHashCode(false);
	}

	@Test
	public void testGet() {
		testGet(true);
		testGet(false);
	}

	@Test
	public void testIsEmpty() {
		testIsEmpty(true);
		testIsEmpty(false);
	}

	@Test
	public void testKeySet() {
		testKeySet(true);
		testKeySet(false);
	}

	@Test
	public void testPortletScopeFiltering() {
		_httpSession.setAttribute(_KEY_4, _value4);
		_httpSession.setAttribute(_KEY_5, _value5);

		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(_httpSession, _SCOPE_PREFIX);

		Set<Map.Entry<String, Object>> entrySet =
			portletSessionAttributeMap.entrySet();

		Assert.assertEquals(entrySet.toString(), 3, entrySet.size());
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(new AbstractMap.SimpleEntry<>(_KEY_1, _value1)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(new AbstractMap.SimpleEntry<>(_KEY_2, _value2)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(new AbstractMap.SimpleEntry<>(_KEY_3, _value3)));

		portletSessionAttributeMap = new PortletSessionAttributeMap(
			_httpSession);

		entrySet = portletSessionAttributeMap.entrySet();

		Assert.assertEquals(entrySet.toString(), 5, entrySet.size());
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(
				new AbstractMap.SimpleEntry<>(
					_SCOPE_PREFIX.concat(_KEY_1), _value1)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(
				new AbstractMap.SimpleEntry<>(
					_SCOPE_PREFIX.concat(_KEY_2), _value2)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(
				new AbstractMap.SimpleEntry<>(
					_SCOPE_PREFIX.concat(_KEY_3), _value3)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(new AbstractMap.SimpleEntry<>(_KEY_4, _value4)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(new AbstractMap.SimpleEntry<>(_KEY_5, _value5)));
	}

	@Test
	public void testSize() {
		testSize(true);
		testSize(false);
	}

	@Test
	public void testUnsupportedMethods() {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(_httpSession);

		try {
			portletSessionAttributeMap.clear();

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}

		try {
			portletSessionAttributeMap.put(null, null);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}

		try {
			portletSessionAttributeMap.putAll(null);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}

		try {
			portletSessionAttributeMap.remove(null);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}

		Set<String> keySet = portletSessionAttributeMap.keySet();

		try {
			keySet.clear();

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}

		Set<Map.Entry<String, Object>> entrySet =
			portletSessionAttributeMap.entrySet();

		try {
			entrySet.clear();

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testValues() {
		testValues(true);
		testValues(false);
	}

	protected String encodeKey(boolean portletScope, String key) {
		if (portletScope) {
			return key;
		}

		return _SCOPE_PREFIX.concat(key);
	}

	protected String getScopePrefix(boolean portletScope) {
		if (portletScope) {
			return _SCOPE_PREFIX;
		}

		return null;
	}

	protected void testContainsKey(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Assert.assertFalse(portletSessionAttributeMap.containsKey(null));
		Assert.assertTrue(
			portletSessionAttributeMap.containsKey(
				encodeKey(portletScope, _KEY_1)));
		Assert.assertTrue(
			portletSessionAttributeMap.containsKey(
				encodeKey(portletScope, _KEY_2)));
		Assert.assertTrue(
			portletSessionAttributeMap.containsKey(
				encodeKey(portletScope, _KEY_3)));
		Assert.assertFalse(
			portletSessionAttributeMap.containsKey(
				encodeKey(portletScope, _KEY_4)));
		Assert.assertFalse(
			portletSessionAttributeMap.containsKey(
				encodeKey(portletScope, _KEY_5)));
	}

	protected void testContainsValue(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Assert.assertFalse(portletSessionAttributeMap.containsValue(null));
		Assert.assertTrue(portletSessionAttributeMap.containsValue(_value1));
		Assert.assertTrue(portletSessionAttributeMap.containsValue(_value2));
		Assert.assertTrue(portletSessionAttributeMap.containsValue(_value3));
		Assert.assertFalse(portletSessionAttributeMap.containsValue(_value4));
		Assert.assertFalse(portletSessionAttributeMap.containsValue(_value5));
	}

	protected void testEntrySet(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Set<Map.Entry<String, Object>> entrySet =
			portletSessionAttributeMap.entrySet();

		Assert.assertEquals(entrySet.toString(), 3, entrySet.size());
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(
				new AbstractMap.SimpleEntry<>(
					encodeKey(portletScope, _KEY_1), _value1)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(
				new AbstractMap.SimpleEntry<>(
					encodeKey(portletScope, _KEY_2), _value2)));
		Assert.assertTrue(
			entrySet.toString(),
			entrySet.contains(
				new AbstractMap.SimpleEntry<>(
					encodeKey(portletScope, _KEY_3), _value3)));
	}

	protected void testEqualsAndHashCode(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Map<String, Object> map = HashMapBuilder.<String, Object>put(
			encodeKey(portletScope, _KEY_1), _value1
		).put(
			encodeKey(portletScope, _KEY_2), _value2
		).put(
			encodeKey(portletScope, _KEY_3), _value3
		).build();

		Assert.assertEquals(map, portletSessionAttributeMap);
		Assert.assertEquals(
			map.hashCode(), portletSessionAttributeMap.hashCode());
	}

	protected void testGet(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Assert.assertNull(portletSessionAttributeMap.get(null));
		Assert.assertSame(
			_value1,
			portletSessionAttributeMap.get(encodeKey(portletScope, _KEY_1)));
		Assert.assertSame(
			_value2,
			portletSessionAttributeMap.get(encodeKey(portletScope, _KEY_2)));
		Assert.assertSame(
			_value3,
			portletSessionAttributeMap.get(encodeKey(portletScope, _KEY_3)));
		Assert.assertNull(
			portletSessionAttributeMap.get(encodeKey(portletScope, _KEY_4)));
		Assert.assertNull(
			portletSessionAttributeMap.get(encodeKey(portletScope, _KEY_5)));
	}

	protected void testIsEmpty(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Assert.assertFalse(portletSessionAttributeMap.isEmpty());

		portletSessionAttributeMap = new PortletSessionAttributeMap(
			new MockHttpSession(), getScopePrefix(portletScope));

		Assert.assertTrue(portletSessionAttributeMap.isEmpty());
	}

	protected void testKeySet(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Set<String> keySet = portletSessionAttributeMap.keySet();

		Assert.assertEquals(keySet.toString(), 3, keySet.size());
		Assert.assertTrue(
			keySet.toString(),
			keySet.contains(encodeKey(portletScope, _KEY_1)));
		Assert.assertTrue(
			keySet.toString(),
			keySet.contains(encodeKey(portletScope, _KEY_2)));
		Assert.assertTrue(
			keySet.toString(),
			keySet.contains(encodeKey(portletScope, _KEY_3)));
	}

	protected void testSize(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Assert.assertEquals(3, portletSessionAttributeMap.size());

		portletSessionAttributeMap = new PortletSessionAttributeMap(
			new MockHttpSession(), getScopePrefix(portletScope));

		Assert.assertEquals(0, portletSessionAttributeMap.size());
	}

	protected void testValues(boolean portletScope) {
		PortletSessionAttributeMap portletSessionAttributeMap =
			new PortletSessionAttributeMap(
				_httpSession, getScopePrefix(portletScope));

		Collection<Object> values = portletSessionAttributeMap.values();

		Assert.assertEquals(values.toString(), 3, values.size());
		Assert.assertTrue(values.toString(), values.contains(_value1));
		Assert.assertTrue(values.toString(), values.contains(_value2));
		Assert.assertTrue(values.toString(), values.contains(_value3));
	}

	private static final String _KEY_1 = "key1";

	private static final String _KEY_2 = "key2";

	private static final String _KEY_3 = "key3";

	private static final String _KEY_4 = "key4";

	private static final String _KEY_5 = "key5";

	private static final String _SCOPE_PREFIX = "scopePrefix?";

	private static final Object _value1 = new Object();
	private static final Object _value2 = new Object();
	private static final Object _value3 = new Object();
	private static final Object _value4 = new Object();
	private static final Object _value5 = new Object();

	private final HttpSession _httpSession = new MockHttpSession();

}