/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface TestrayFactor {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name", "factorCategoryToFactors",
		"factorOptionToFactors"
	};

	public Category getCategory();

	public Long getId();

	public JSONObject getJSONObject();

	public Option getOption();

	public TestrayServer getTestrayServer();

	public static class Category {

		public Long getId() {
			if (_id <= 0) {
				JSONObject jsonObject = _getJSONObject();

				if (jsonObject == null) {
					return _id;
				}

				_id = jsonObject.getLong("id");
			}

			return _id;
		}

		public String getName() {
			if (JenkinsResultsParserUtil.isNullOrEmpty(_name)) {
				JSONObject jsonObject = _getJSONObject();

				if (jsonObject == null) {
					return _name;
				}

				_name = jsonObject.getString("name");
			}

			return _name;
		}

		public TestrayServer getTestrayServer() {
			return _testrayServer;
		}

		protected Category(JSONObject jsonObject, TestrayServer testrayServer) {
			_jsonObject = jsonObject;
			_testrayServer = testrayServer;

			_cached = true;

			_id = _jsonObject.getLong("id");
			_name = _jsonObject.getString("name");
		}

		protected Category(long id, TestrayServer testrayServer) {
			_id = id;
			_testrayServer = testrayServer;
		}

		protected Category(String name, TestrayServer testrayServer) {
			_name = name;
			_testrayServer = testrayServer;
		}

		private synchronized JSONObject _getJSONObject() {
			if (_cached || (_jsonObject != null)) {
				return _jsonObject;
			}

			String filterString = null;

			if (_id > 0) {
				filterString = JenkinsResultsParserUtil.combine(
					"id eq '", String.valueOf(_id), "'");
			}
			else if (!JenkinsResultsParserUtil.isNullOrEmpty(_name)) {
				filterString = JenkinsResultsParserUtil.combine(
					"name eq '", _name, "'");
			}

			if (filterString == null) {
				_cached = true;

				return null;
			}

			try {
				JSONObject jsonObject = new JSONObject(
					_testrayServer.requestGet(
						"/o/c/factorcategories?filter=" +
							URLEncoder.encode(filterString, "UTF-8")));

				JSONArray itemsJSONArray = jsonObject.optJSONArray("items");

				if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
					_cached = true;

					return null;
				}

				_jsonObject = itemsJSONArray.getJSONObject(0);

				_cached = true;

				return _jsonObject;
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private boolean _cached;
		private long _id;
		private JSONObject _jsonObject;
		private String _name;
		private final TestrayServer _testrayServer;

	}

	public static class Option {

		public Category getCategory() {
			if (_category != null) {
				return _category;
			}

			JSONObject jsonObject = _getJSONObject();

			if (jsonObject == null) {
				return null;
			}

			_category = TestrayFactory.newTestrayFactorCategory(
				jsonObject.getLong(
					"r_factorCategoryToOptions_c_factorCategoryId"),
				getTestrayServer());

			return _category;
		}

		public Long getId() {
			if (_cached || (_id > 0)) {
				return _id;
			}

			JSONObject jsonObject = _getJSONObject();

			if (jsonObject == null) {
				return _id;
			}

			_id = jsonObject.getLong("id");

			return _id;
		}

		public String getName() {
			if (_cached || (_name != null)) {
				return _name;
			}

			JSONObject jsonObject = _getJSONObject();

			if (jsonObject == null) {
				return _name;
			}

			_name = jsonObject.getString("name");

			return _name;
		}

		public TestrayServer getTestrayServer() {
			return _testrayServer;
		}

		protected Option(JSONObject jsonObject, TestrayServer testrayServer) {
			_jsonObject = jsonObject;
			_testrayServer = testrayServer;

			_id = jsonObject.getLong("id");
			_name = jsonObject.getString("name");
		}

		protected Option(long id, TestrayServer testrayServer) {
			_id = id;
			_testrayServer = testrayServer;
		}

		protected Option(String name, TestrayServer testrayServer) {
			_name = name;
			_testrayServer = testrayServer;
		}

		private synchronized JSONObject _getJSONObject() {
			if (_cached) {
				return _jsonObject;
			}

			String filterString = null;

			if (_id > 0) {
				filterString = JenkinsResultsParserUtil.combine(
					"id eq '", String.valueOf(_id), "'");
			}
			else if (!JenkinsResultsParserUtil.isNullOrEmpty(_name)) {
				filterString = JenkinsResultsParserUtil.combine(
					"name eq '", _name, "'");
			}

			if (filterString == null) {
				_cached = true;

				return null;
			}

			try {
				JSONObject jsonObject = new JSONObject(
					_testrayServer.requestGet(
						"/o/c/factoroptions?filter=" +
							URLEncoder.encode(filterString, "UTF-8")));

				JSONArray itemsJSONArray = jsonObject.optJSONArray("items");

				if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
					_cached = true;

					return null;
				}

				_jsonObject = itemsJSONArray.getJSONObject(0);

				_cached = true;

				return _jsonObject;
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private boolean _cached;
		private Category _category;
		private long _id;
		private JSONObject _jsonObject;
		private String _name;
		private final TestrayServer _testrayServer;

	}

}