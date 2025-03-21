/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpSession;

/**
 * @author Shuyang Zhou
 */
public class PortletSessionImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testConstructor() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		Assert.assertSame(_mockHttpSession, portletSessionImpl.httpSession);
		Assert.assertSame(_portletContext, portletSessionImpl.portletContext);

		Assert.assertEquals(
			StringBundler.concat(
				LiferayPortletSession.PORTLET_SCOPE_NAMESPACE, _PORTLET_NAME,
				LiferayPortletSession.LAYOUT_SEPARATOR, _PLID,
				StringPool.QUESTION),
			portletSessionImpl.scopePrefix);
	}

	@Test
	public void testDirectDelegateMethods() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		Assert.assertEquals(
			_mockHttpSession.getCreationTime(),
			portletSessionImpl.getCreationTime());
		Assert.assertSame(
			_mockHttpSession, portletSessionImpl.getHttpSession());
		Assert.assertEquals(
			_mockHttpSession.getId(), portletSessionImpl.getId());
		Assert.assertEquals(
			_mockHttpSession.getLastAccessedTime(),
			portletSessionImpl.getLastAccessedTime());
		Assert.assertEquals(
			_mockHttpSession.getMaxInactiveInterval(),
			portletSessionImpl.getMaxInactiveInterval());
		Assert.assertSame(
			_portletContext, portletSessionImpl.getPortletContext());
		Assert.assertEquals(
			_mockHttpSession.isNew(), portletSessionImpl.isNew());

		Assert.assertFalse(_mockHttpSession.isInvalid());

		portletSessionImpl.invalidate();

		Assert.assertTrue(_mockHttpSession.isInvalid());

		portletSessionImpl.setMaxInactiveInterval(Integer.MAX_VALUE);

		Assert.assertEquals(
			Integer.MAX_VALUE, _mockHttpSession.getMaxInactiveInterval());

		HttpSession httpSession = new MockHttpSession();

		portletSessionImpl.setHttpSession(httpSession);

		Assert.assertSame(httpSession, portletSessionImpl.httpSession);
	}

	@Test
	public void testGetAttribute() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		try {
			Assert.assertNull(portletSessionImpl.getAttribute(null));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		try {
			Assert.assertNull(
				portletSessionImpl.getAttribute(
					null, PortletSession.APPLICATION_SCOPE));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		Assert.assertSame(_value1, portletSessionImpl.getAttribute(_KEY_1));
		Assert.assertSame(_value2, portletSessionImpl.getAttribute(_KEY_2));
		Assert.assertSame(_value3, portletSessionImpl.getAttribute(_KEY_3));
		Assert.assertNull(portletSessionImpl.getAttribute(_KEY_4));
		Assert.assertNull(portletSessionImpl.getAttribute(_KEY_5));
		Assert.assertNull(portletSessionImpl.getAttribute(_KEY_6));
		Assert.assertNull(
			portletSessionImpl.getAttribute(
				_KEY_1, PortletSession.APPLICATION_SCOPE));
		Assert.assertNull(
			portletSessionImpl.getAttribute(
				_KEY_2, PortletSession.APPLICATION_SCOPE));
		Assert.assertNull(
			portletSessionImpl.getAttribute(
				_KEY_3, PortletSession.APPLICATION_SCOPE));
		Assert.assertSame(
			_value4,
			portletSessionImpl.getAttribute(
				_KEY_4, PortletSession.APPLICATION_SCOPE));
		Assert.assertSame(
			_value5,
			portletSessionImpl.getAttribute(
				_KEY_5, PortletSession.APPLICATION_SCOPE));
		Assert.assertNull(
			portletSessionImpl.getAttribute(
				_KEY_6, PortletSession.APPLICATION_SCOPE));
	}

	@Test
	public void testGetAttributeMap() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		String scopePrefix = portletSessionImpl.scopePrefix;

		Map<String, Object> attributeMap = portletSessionImpl.getAttributeMap();

		Assert.assertEquals(attributeMap.toString(), 3, attributeMap.size());
		Assert.assertSame(_value1, attributeMap.get(_KEY_1));
		Assert.assertSame(_value2, attributeMap.get(_KEY_2));
		Assert.assertSame(_value3, attributeMap.get(_KEY_3));
		Assert.assertEquals(
			attributeMap,
			portletSessionImpl.getAttributeMap(PortletSession.PORTLET_SCOPE));

		attributeMap = portletSessionImpl.getAttributeMap(
			PortletSession.APPLICATION_SCOPE);

		Assert.assertEquals(attributeMap.toString(), 5, attributeMap.size());
		Assert.assertSame(
			_value1, attributeMap.get(scopePrefix.concat(_KEY_1)));
		Assert.assertSame(
			_value2, attributeMap.get(scopePrefix.concat(_KEY_2)));
		Assert.assertSame(
			_value3, attributeMap.get(scopePrefix.concat(_KEY_3)));
		Assert.assertSame(_value4, attributeMap.get(_KEY_4));
		Assert.assertSame(_value5, attributeMap.get(_KEY_5));
	}

	@Test
	public void testGetAttributeNames() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		String scopePrefix = portletSessionImpl.scopePrefix;

		Enumeration<String> enumeration =
			portletSessionImpl.getAttributeNames();

		Assert.assertEquals(_KEY_1, enumeration.nextElement());
		Assert.assertEquals(_KEY_2, enumeration.nextElement());
		Assert.assertEquals(_KEY_3, enumeration.nextElement());
		Assert.assertFalse(enumeration.hasMoreElements());

		enumeration = portletSessionImpl.getAttributeNames(
			PortletSession.APPLICATION_SCOPE);

		Assert.assertEquals(
			scopePrefix.concat(_KEY_1), enumeration.nextElement());
		Assert.assertEquals(
			scopePrefix.concat(_KEY_2), enumeration.nextElement());
		Assert.assertEquals(
			scopePrefix.concat(_KEY_3), enumeration.nextElement());
		Assert.assertEquals(_KEY_4, enumeration.nextElement());
		Assert.assertEquals(_KEY_5, enumeration.nextElement());
		Assert.assertFalse(enumeration.hasMoreElements());
	}

	@Test
	public void testInvalidate() {
		PortletSessionImpl portletSessionImpl = new PortletSessionImpl(
			new MockHttpSession(), _portletContext, _PORTLET_NAME, _PLID);

		Assert.assertFalse(portletSessionImpl.isInvalidated());

		portletSessionImpl.invalidate();

		try {
			Assert.assertSame(_value1, portletSessionImpl.getAttribute(_KEY_1));

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		try {
			Assert.assertEquals(
				_mockHttpSession.getCreationTime(),
				portletSessionImpl.getCreationTime());

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		Assert.assertTrue(portletSessionImpl.isInvalidated());
	}

	@Test
	public void testRemoveAttribute() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		String scopePrefix = portletSessionImpl.scopePrefix;

		try {
			portletSessionImpl.removeAttribute(null);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		portletSessionImpl.removeAttribute(_KEY_1);

		Assert.assertNull(
			_mockHttpSession.getAttribute(scopePrefix.concat(_KEY_1)));

		portletSessionImpl.removeAttribute(_KEY_2);

		Assert.assertNull(
			_mockHttpSession.getAttribute(scopePrefix.concat(_KEY_2)));

		portletSessionImpl.removeAttribute(_KEY_3);

		Assert.assertNull(
			_mockHttpSession.getAttribute(scopePrefix.concat(_KEY_3)));

		portletSessionImpl.removeAttribute(
			_KEY_4, PortletSession.APPLICATION_SCOPE);

		Assert.assertNull(_mockHttpSession.getAttribute(_KEY_4));

		portletSessionImpl.removeAttribute(
			_KEY_5, PortletSession.APPLICATION_SCOPE);

		Assert.assertNull(_mockHttpSession.getAttribute(_KEY_5));

		Enumeration<String> enumeration = _mockHttpSession.getAttributeNames();

		Assert.assertFalse(enumeration.hasMoreElements());
	}

	@Test
	public void testSetAttribute() {
		PortletSessionImpl portletSessionImpl = _getPortletSessionImpl();

		String scopePrefix = portletSessionImpl.scopePrefix;

		try {
			portletSessionImpl.setAttribute(null, null);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		String key7 = "key7";
		Object value7 = new Object();

		portletSessionImpl.setAttribute(key7, value7);

		Assert.assertSame(
			value7, _mockHttpSession.getAttribute(scopePrefix.concat(key7)));

		String key8 = "key8";
		Object value8 = new Object();

		portletSessionImpl.setAttribute(
			key8, value8, PortletSession.APPLICATION_SCOPE);

		Assert.assertSame(value8, _mockHttpSession.getAttribute(key8));
	}

	private PortletSessionImpl _getPortletSessionImpl() {
		PortletSessionImpl portletSessionImpl = new PortletSessionImpl(
			_mockHttpSession, _portletContext, _PORTLET_NAME, _PLID);

		String scopePrefix = portletSessionImpl.scopePrefix;

		_mockHttpSession.setAttribute(scopePrefix.concat(_KEY_1), _value1);
		_mockHttpSession.setAttribute(scopePrefix.concat(_KEY_2), _value2);
		_mockHttpSession.setAttribute(scopePrefix.concat(_KEY_3), _value3);

		_mockHttpSession.setAttribute(_KEY_4, _value4);
		_mockHttpSession.setAttribute(_KEY_5, _value5);

		return portletSessionImpl;
	}

	private static final String _KEY_1 = "key1";

	private static final String _KEY_2 = "key2";

	private static final String _KEY_3 = "key3";

	private static final String _KEY_4 = "key4";

	private static final String _KEY_5 = "key5";

	private static final String _KEY_6 = "key6";

	private static final long _PLID = 100;

	private static final String _PORTLET_NAME = "portletName";

	private static final PortletContext _portletContext =
		(PortletContext)ProxyUtil.newProxyInstance(
			PortletContext.class.getClassLoader(),
			new Class<?>[] {PortletContext.class},
			new InvocationHandler() {

				@Override
				public Object invoke(
					Object proxy, Method method, Object[] args) {

					throw new UnsupportedOperationException();
				}

			});

	private final MockHttpSession _mockHttpSession = new MockHttpSession();
	private final Object _value1 = new Object();
	private final Object _value2 = new Object();
	private final Object _value3 = new Object();
	private final Object _value4 = new Object();
	private final Object _value5 = new Object();

}