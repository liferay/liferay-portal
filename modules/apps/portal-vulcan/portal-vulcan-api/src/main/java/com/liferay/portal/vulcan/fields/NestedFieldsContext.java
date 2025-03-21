/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.fields;

import com.liferay.portal.kernel.util.ListUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;

import org.apache.cxf.message.Message;

/**
 * @author Ivica Cardic
 */
public class NestedFieldsContext implements Cloneable {

	public NestedFieldsContext(int depth, List<String> nestedFields) {
		this(depth, null, nestedFields, null, null, null);
	}

	public NestedFieldsContext(
		int depth, Message message, List<String> nestedFields,
		MultivaluedMap<String, String> pathParameters,
		MultivaluedMap<String, String> queryParameters,
		String resourceVersion) {

		_depth = depth;
		_message = message;
		_nestedFields = ListUtil.copy(nestedFields);
		_pathParameters = pathParameters;
		_queryParameters = queryParameters;
		_resourceVersion = resourceVersion;
	}

	public void addNestedField(String nestedField) {
		_nestedFields.add(nestedField);
	}

	@Override
	public NestedFieldsContext clone() throws CloneNotSupportedException {
		return (NestedFieldsContext)super.clone();
	}

	public void decrementCurrentDepth() {
		_currentDepth--;
	}

	public int getCurrentDepth() {
		return _currentDepth;
	}

	public int getDepth() {
		return _depth;
	}

	public Message getMessage() {
		return _message;
	}

	public List<String> getNestedFields() {
		return _nestedFields;
	}

	public MultivaluedMap<String, String> getPathParameters() {
		return _pathParameters;
	}

	public MultivaluedMap<String, String> getQueryParameters() {
		return _queryParameters;
	}

	public String getResourceVersion() {
		return _resourceVersion;
	}

	public void incrementCurrentDepth() {
		_currentDepth++;
	}

	private int _currentDepth;
	private final int _depth;
	private final Message _message;
	private final List<String> _nestedFields;
	private final MultivaluedMap<String, String> _pathParameters;
	private final MultivaluedMap<String, String> _queryParameters;
	private final String _resourceVersion;

}