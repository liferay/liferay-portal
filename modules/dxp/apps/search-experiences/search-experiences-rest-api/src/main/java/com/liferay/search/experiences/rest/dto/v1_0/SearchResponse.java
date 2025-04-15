/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("SearchResponse")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SearchResponse")
public class SearchResponse implements Serializable {

	public static SearchResponse toDTO(String json) {
		return ObjectMapperUtil.readValue(SearchResponse.class, json);
	}

	public static SearchResponse unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SearchResponse.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map[] getErrors() {
		if (_errorsSupplier != null) {
			errors = _errorsSupplier.get();

			_errorsSupplier = null;
		}

		return errors;
	}

	public void setErrors(Map[] errors) {
		this.errors = errors;

		_errorsSupplier = null;
	}

	@JsonIgnore
	public void setErrors(
		UnsafeSupplier<Map[], Exception> errorsUnsafeSupplier) {

		_errorsSupplier = () -> {
			try {
				return errorsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map[] errors;

	@JsonIgnore
	private Supplier<Map[]> _errorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getPage() {
		if (_pageSupplier != null) {
			page = _pageSupplier.get();

			_pageSupplier = null;
		}

		return page;
	}

	public void setPage(Integer page) {
		this.page = page;

		_pageSupplier = null;
	}

	@JsonIgnore
	public void setPage(UnsafeSupplier<Integer, Exception> pageUnsafeSupplier) {
		_pageSupplier = () -> {
			try {
				return pageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer page;

	@JsonIgnore
	private Supplier<Integer> _pageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getPageSize() {
		if (_pageSizeSupplier != null) {
			pageSize = _pageSizeSupplier.get();

			_pageSizeSupplier = null;
		}

		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;

		_pageSizeSupplier = null;
	}

	@JsonIgnore
	public void setPageSize(
		UnsafeSupplier<Integer, Exception> pageSizeUnsafeSupplier) {

		_pageSizeSupplier = () -> {
			try {
				return pageSizeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer pageSize;

	@JsonIgnore
	private Supplier<Integer> _pageSizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getRequest() {
		if (_requestSupplier != null) {
			request = _requestSupplier.get();

			_requestSupplier = null;
		}

		return request;
	}

	public void setRequest(Object request) {
		this.request = request;

		_requestSupplier = null;
	}

	@JsonIgnore
	public void setRequest(
		UnsafeSupplier<Object, Exception> requestUnsafeSupplier) {

		_requestSupplier = () -> {
			try {
				return requestUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object request;

	@JsonIgnore
	private Supplier<Object> _requestSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRequestString() {
		if (_requestStringSupplier != null) {
			requestString = _requestStringSupplier.get();

			_requestStringSupplier = null;
		}

		return requestString;
	}

	public void setRequestString(String requestString) {
		this.requestString = requestString;

		_requestStringSupplier = null;
	}

	@JsonIgnore
	public void setRequestString(
		UnsafeSupplier<String, Exception> requestStringUnsafeSupplier) {

		_requestStringSupplier = () -> {
			try {
				return requestStringUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String requestString;

	@JsonIgnore
	private Supplier<String> _requestStringSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getResponse() {
		if (_responseSupplier != null) {
			response = _responseSupplier.get();

			_responseSupplier = null;
		}

		return response;
	}

	public void setResponse(Object response) {
		this.response = response;

		_responseSupplier = null;
	}

	@JsonIgnore
	public void setResponse(
		UnsafeSupplier<Object, Exception> responseUnsafeSupplier) {

		_responseSupplier = () -> {
			try {
				return responseUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object response;

	@JsonIgnore
	private Supplier<Object> _responseSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getResponseString() {
		if (_responseStringSupplier != null) {
			responseString = _responseStringSupplier.get();

			_responseStringSupplier = null;
		}

		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;

		_responseStringSupplier = null;
	}

	@JsonIgnore
	public void setResponseString(
		UnsafeSupplier<String, Exception> responseStringUnsafeSupplier) {

		_responseStringSupplier = () -> {
			try {
				return responseStringUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String responseString;

	@JsonIgnore
	private Supplier<String> _responseStringSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SearchHits getSearchHits() {
		if (_searchHitsSupplier != null) {
			searchHits = _searchHitsSupplier.get();

			_searchHitsSupplier = null;
		}

		return searchHits;
	}

	public void setSearchHits(SearchHits searchHits) {
		this.searchHits = searchHits;

		_searchHitsSupplier = null;
	}

	@JsonIgnore
	public void setSearchHits(
		UnsafeSupplier<SearchHits, Exception> searchHitsUnsafeSupplier) {

		_searchHitsSupplier = () -> {
			try {
				return searchHitsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SearchHits searchHits;

	@JsonIgnore
	private Supplier<SearchHits> _searchHitsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SearchRequest getSearchRequest() {
		if (_searchRequestSupplier != null) {
			searchRequest = _searchRequestSupplier.get();

			_searchRequestSupplier = null;
		}

		return searchRequest;
	}

	public void setSearchRequest(SearchRequest searchRequest) {
		this.searchRequest = searchRequest;

		_searchRequestSupplier = null;
	}

	@JsonIgnore
	public void setSearchRequest(
		UnsafeSupplier<SearchRequest, Exception> searchRequestUnsafeSupplier) {

		_searchRequestSupplier = () -> {
			try {
				return searchRequestUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SearchRequest searchRequest;

	@JsonIgnore
	private Supplier<SearchRequest> _searchRequestSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SearchResponse)) {
			return false;
		}

		SearchResponse searchResponse = (SearchResponse)object;

		return Objects.equals(toString(), searchResponse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map[] errors = getErrors();

		if (errors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errors\": ");

			sb.append("[");

			for (int i = 0; i < errors.length; i++) {
				sb.append(errors[i]);

				if ((i + 1) < errors.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer page = getPage();

		if (page != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"page\": ");

			sb.append(page);
		}

		Integer pageSize = getPageSize();

		if (pageSize != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSize\": ");

			sb.append(pageSize);
		}

		Object request = getRequest();

		if (request != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"request\": ");

			if (request instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)request));
			}
			else if (request instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)request));
				sb.append("\"");
			}
			else {
				sb.append(request);
			}
		}

		String requestString = getRequestString();

		if (requestString != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestString\": ");

			sb.append("\"");

			sb.append(_escape(requestString));

			sb.append("\"");
		}

		Object response = getResponse();

		if (response != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"response\": ");

			if (response instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)response));
			}
			else if (response instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)response));
				sb.append("\"");
			}
			else {
				sb.append(response);
			}
		}

		String responseString = getResponseString();

		if (responseString != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"responseString\": ");

			sb.append("\"");

			sb.append(_escape(responseString));

			sb.append("\"");
		}

		SearchHits searchHits = getSearchHits();

		if (searchHits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchHits\": ");

			sb.append(String.valueOf(searchHits));
		}

		SearchRequest searchRequest = getSearchRequest();

		if (searchRequest != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchRequest\": ");

			sb.append(String.valueOf(searchRequest));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.SearchResponse",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}