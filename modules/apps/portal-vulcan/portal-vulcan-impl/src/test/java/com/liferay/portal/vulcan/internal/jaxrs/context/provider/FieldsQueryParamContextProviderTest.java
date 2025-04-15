/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.fields.FieldsQueryParam;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Hernández
 */
public class FieldsQueryParamContextProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test() {

		// Null

		MatcherAssert.assertThat(
			_getFieldNames(null), Matchers.is(Matchers.nullValue()));

		// Empty

		MatcherAssert.assertThat(
			_getFieldNames(""), Matchers.is(Matchers.empty()));

		// Expanded

		MatcherAssert.assertThat(
			_getFieldNames("hello.hi.hello,potato"),
			Matchers.containsInAnyOrder(
				"hello", "hello.hi", "hello.hi.hello", "potato"));

		// No duplicates

		MatcherAssert.assertThat(
			_getFieldNames("hello,hi,hello"),
			Matchers.containsInAnyOrder("hello", "hi"));
	}

	private Set<String> _getFieldNames(String fieldNames) {
		ContextProvider<FieldsQueryParam> contextProvider =
			new FieldsQueryParamContextProvider();

		FieldsQueryParam fieldsQueryParam = contextProvider.createContext(
			_getMessage(fieldNames));

		return fieldsQueryParam.getFieldNames();
	}

	private Message _getMessage(String fieldNames) {
		Message message = Mockito.mock(Message.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			message.getContextualProperty("HTTP.REQUEST")
		).thenReturn(
			httpServletRequest
		);

		Mockito.when(
			httpServletRequest.getParameter("fields")
		).thenReturn(
			fieldNames
		);

		return message;
	}

}