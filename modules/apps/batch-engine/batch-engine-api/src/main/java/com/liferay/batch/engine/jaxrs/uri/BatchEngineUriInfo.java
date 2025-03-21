/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.jaxrs.uri;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import java.util.Collections;
import java.util.List;

/**
 * @author Igor Beslic
 */
public class BatchEngineUriInfo implements UriInfo {

	@Override
	public URI getAbsolutePath() {
		return null;
	}

	@Override
	public UriBuilder getAbsolutePathBuilder() {
		return null;
	}

	@Override
	public URI getBaseUri() {
		return null;
	}

	@Override
	public UriBuilder getBaseUriBuilder() {
		return UriBuilder.fromPath("/headless-batch-engine");
	}

	@Override
	public List<Object> getMatchedResources() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getMatchedURIs() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getMatchedURIs(boolean decode) {
		return Collections.emptyList();
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public String getPath(boolean decode) {
		return null;
	}

	@Override
	public MultivaluedMap<String, String> getPathParameters() {
		return _pathParameters;
	}

	@Override
	public MultivaluedMap<String, String> getPathParameters(boolean decode) {
		return _pathParameters;
	}

	@Override
	public List<PathSegment> getPathSegments() {
		return Collections.emptyList();
	}

	@Override
	public List<PathSegment> getPathSegments(boolean decode) {
		return Collections.emptyList();
	}

	@Override
	public MultivaluedMap<String, String> getQueryParameters() {
		return _queryParameters;
	}

	@Override
	public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
		return _queryParameters;
	}

	@Override
	public URI getRequestUri() {
		return null;
	}

	@Override
	public UriBuilder getRequestUriBuilder() {
		return null;
	}

	@Override
	public URI relativize(URI uri) {
		return null;
	}

	@Override
	public URI resolve(URI uri) {
		return null;
	}

	public static class Builder {

		public BatchEngineUriInfo build() {
			return new BatchEngineUriInfo(this);
		}

		public Builder pathParameter(String name, String value) {
			_pathParameters.putSingle(name, value);

			return this;
		}

		public Builder queryParameter(String name, String value) {
			_queryParameters.putSingle(name, value);

			return this;
		}

		public Builder taskItemDelegateName(String taskItemDelegateName) {
			_pathParameters.putSingle(
				"taskItemDelegateName", taskItemDelegateName);

			return this;
		}

		private final MultivaluedHashMap<String, String> _pathParameters =
			new MultivaluedHashMap<>();
		private final MultivaluedHashMap<String, String> _queryParameters =
			new MultivaluedHashMap<>();

	}

	private BatchEngineUriInfo(Builder builder) {
		_pathParameters = builder._pathParameters;
		_queryParameters = builder._queryParameters;
	}

	private final MultivaluedHashMap<String, String> _pathParameters;
	private final MultivaluedHashMap<String, String> _queryParameters;

}