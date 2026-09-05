/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class JSONWebServiceActionImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testInjectInnerParametersIntoValueWithIndexedName()
		throws Exception {

		Parent parent = new Parent();

		List<String> values = parent.getValues();

		String value = RandomTestUtil.randomString();

		values.add(value);

		_invokeInjectInnerParametersIntoValue(
			parent, "parent.values[0]", RandomTestUtil.randomString());

		Assert.assertEquals(value, values.get(0));
	}

	@Test
	public void testInjectInnerParametersIntoValueWithNestedName()
		throws Exception {

		Parent parent = new Parent();

		_invokeInjectInnerParametersIntoValue(
			parent, "parent.child.value", RandomTestUtil.randomString());

		Child child = parent.getChild();

		Assert.assertNull(child.getValue());
	}

	@Test
	public void testInjectInnerParametersIntoValueWithSimpleName()
		throws Exception {

		Parent parent = new Parent();
		String value = RandomTestUtil.randomString();

		_invokeInjectInnerParametersIntoValue(parent, "parent.name", value);

		Assert.assertEquals(value, parent.getName());
	}

	public static class Child {

		public String getValue() {
			return _value;
		}

		public void setValue(String value) {
			_value = value;
		}

		private String _value;

	}

	public static class Parent {

		public Child getChild() {
			return _child;
		}

		public String getName() {
			return _name;
		}

		public List<String> getValues() {
			return _values;
		}

		public void setName(String name) {
			_name = name;
		}

		private final Child _child = new Child();
		private String _name;
		private final List<String> _values = new ArrayList<>();

	}

	private void _invokeInjectInnerParametersIntoValue(
		Object object, String parameterName, Object value) {

		JSONWebServiceActionParameters jsonWebServiceActionParameters =
			new JSONWebServiceActionParameters();

		JSONWebServiceActionParametersMap jsonWebServiceActionParametersMap =
			ReflectionTestUtil.getFieldValue(
				jsonWebServiceActionParameters,
				"_jsonWebServiceActionParameters");

		jsonWebServiceActionParametersMap.put(parameterName, value);

		JSONWebServiceActionImpl jsonWebServiceActionImpl =
			new JSONWebServiceActionImpl(null, jsonWebServiceActionParameters);

		ReflectionTestUtil.invoke(
			jsonWebServiceActionImpl, "_injectInnerParametersIntoValue",
			new Class<?>[] {String.class, Object.class}, "parent", object);
	}

}