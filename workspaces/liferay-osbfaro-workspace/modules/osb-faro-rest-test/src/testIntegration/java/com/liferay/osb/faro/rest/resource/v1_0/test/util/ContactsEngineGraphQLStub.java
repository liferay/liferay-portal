/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test.util;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.GraphQLRequest;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.Collections;
import java.util.Map;

/**
 * @author Leslie Wong
 */
public class ContactsEngineGraphQLStub implements AutoCloseable {

	public ContactsEngineGraphQLStub(Object resource) {
		Object faroGraphQLClient = ReflectionTestUtil.getFieldValue(
			resource, "_faroGraphQLClient");

		_contactsEngineClient = ReflectionTestUtil.getAndSetFieldValue(
			faroGraphQLClient, "_contactsEngineClient",
			(ContactsEngineClient)ProxyUtil.newProxyInstance(
				ContactsEngineClient.class.getClassLoader(),
				new Class<?>[] {ContactsEngineClient.class},
				(proxy, method, arguments) -> {
					_graphQLRequest = (GraphQLRequest)arguments[4];

					return Collections.singletonMap("data", _data);
				}));

		_faroGraphQLClient = faroGraphQLClient;
	}

	@Override
	public void close() {
		ReflectionTestUtil.setFieldValue(
			_faroGraphQLClient, "_contactsEngineClient", _contactsEngineClient);
	}

	public GraphQLRequest getGraphQLRequest() {
		return _graphQLRequest;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	private final ContactsEngineClient _contactsEngineClient;
	private volatile Map<String, Object> _data = Collections.emptyMap();
	private final Object _faroGraphQLClient;
	private volatile GraphQLRequest _graphQLRequest;

}