/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.search.experiences.rest.client.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.client.dto.v1_0.SearchHits;
import com.liferay.search.experiences.rest.client.dto.v1_0.SearchResponse;
import com.liferay.search.experiences.rest.client.dto.v1_0.util.SXPBlueprintUtil;
import com.liferay.search.experiences.rest.client.pagination.Pagination;
import com.liferay.search.experiences.rest.client.problem.Problem;
import com.liferay.search.experiences.rest.client.resource.v1_0.SearchResponseResource;

import java.util.Collections;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class SearchResponseResourceTest
	extends BaseSearchResponseResourceTestCase {

	@Override
	@Test
	public void testPostSearch() throws Exception {
		super.testPostSearch();

		_testPostSearch();
		_testPostSearchWithoutPermission();
		_testPostSearchZeroResults();

		if (false) {

			// TODO Tests pass with remote Elastic but sidecar does not play
			// well with ConfigurationTemporarySwapper

			_testPostSearchThrowsElasticsearchStatusException();
			_testPostSearchThrowsInvalidQueryEntryExceptionAndUnresolvedTemplateVariableException();

			// TODO SXPBlueprint.toDTO with "{ ... }" freezes and never returns

			_testPostSearchThrowsJsonParseException();

			// TODO SXPBlueprint.toDTO with bad JSON returns a half empty DTO

			_testPostSearchThrowsUnrecognizedPropertyException();
		}
	}

	@Override
	protected SearchResponse testPostSearch_addSearchResponse(
			SearchResponse searchResponse)
		throws Exception {

		return searchResponse;
	}

	private SearchResponse _postSearch(String sxpBlueprintJSON)
		throws Exception {

		SXPBlueprint sxpBlueprint = SXPBlueprintUtil.toSXPBlueprint(
			sxpBlueprintJSON);

		Assert.assertNotNull(sxpBlueprint);

		return searchResponseResource.postSearch(
			null, _PAGINATION, sxpBlueprint);
	}

	private String _read() throws Exception {
		Class<?> clazz = getClass();

		Thread currentThread = Thread.currentThread();

		StackTraceElement[] stackTraceElements = currentThread.getStackTrace();

		return StringUtil.read(
			clazz.getResourceAsStream(
				StringBundler.concat(
					"dependencies/", clazz.getSimpleName(), StringPool.PERIOD,
					stackTraceElements[2].getMethodName(), ".json")));
	}

	private void _testPostSearch() throws Exception {
		_postSearch("{}");
		_postSearch(_read());
	}

	private void _testPostSearchThrowsElasticsearchStatusException()
		throws Exception {

		String message = StringBundler.concat(
			"org.elasticsearch.ElasticsearchStatusException: ",
			"ElasticsearchStatusException[Elasticsearch exception [type=",
			"parsing_exception, reason=[INVALID] query malformed, no ",
			"start_object after query name]]");

		try {
			try (ConfigurationTemporarySwapper configurationTemporarySwapper =
					new ConfigurationTemporarySwapper(
						_CONFIGURATION_PID_ELASTICSEARCH,
						new HashMapDictionary<>(
							Collections.singletonMap(
								"logExceptionsOnly", false)))) {

				try (LogCapture logCapture =
						LoggerTestUtil.configureLog4JLogger(
							_CLASS_NAME_EXCEPTION_MAPPER,
							LoggerTestUtil.ERROR)) {

					_postSearch(_read());
				}
			}

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertThat(
				problemException.getMessage(),
				CoreMatchers.containsString(message));
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_CONFIGURATION_PID_ELASTICSEARCH,
					new HashMapDictionary<>(
						Collections.singletonMap("logExceptionsOnly", true)))) {

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME_ELASTICSEARCH_INDEX_SEARCHER,
					LoggerTestUtil.ERROR)) {

				SearchResponse searchResponse = _postSearch(_read());

				Assert.assertNull(searchResponse.getResponse());
				Assert.assertThat(
					searchResponse.getResponseString(),
					CoreMatchers.containsString(message));
			}
		}
	}

	private void _testPostSearchThrowsInvalidQueryEntryExceptionAndUnresolvedTemplateVariableException()
		throws Exception {

		try {
			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

				_postSearch(_read());

				Assert.fail();
			}
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertThat(
				problemException.getMessage(),
				CoreMatchers.allOf(
					CoreMatchers.containsString("Invalid query entry at: 0"),
					CoreMatchers.containsString("Invalid query entry at: 1"),
					CoreMatchers.containsString("The key \"value\" is not set"),
					CoreMatchers.containsString(
						"Unresolved template variables: [ipstack.latitude, " +
							"ipstack.longitude]")));
		}
	}

	private void _testPostSearchThrowsJsonParseException() throws Exception {
		try {
			_postSearch("{ ... }");

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertThat(
				problemException.getMessage(),
				CoreMatchers.containsString("Input is invalid JSON"));
		}
	}

	private void _testPostSearchThrowsUnrecognizedPropertyException()
		throws Exception {

		try {
			_postSearch(_read());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {

			// TODO The property "INVALID_2" should be in the message

			Assert.assertThat(
				problemException.getMessage(),
				CoreMatchers.containsString(
					StringBundler.concat(
						"The property \"configuration\" is not defined in ",
						"SXPBlueprint. The property \"general\" is not ",
						"defined in Configuration. The property \"INVALID_1\" ",
						"is not defined in General.")));
		}
	}

	private void _testPostSearchWithoutPermission() throws Exception {
		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		SearchResponseResource searchResponseResource =
			SearchResponseResource.builder(
			).authentication(
				_user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		try {
			searchResponseResource.postSearch(
				null, _PAGINATION, SXPBlueprintUtil.toSXPBlueprint("{}"));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPostSearchZeroResults() throws Exception {
		SearchResponse searchResponse = _postSearch(_read());

		SearchHits searchHits = searchResponse.getSearchHits();

		Assert.assertEquals(Long.valueOf(0), searchHits.getTotalHits());

		String response = String.valueOf(searchResponse.getResponse());

		Assert.assertThat(response, CoreMatchers.containsString("hits"));
		Assert.assertThat(
			response,
			CoreMatchers.not(CoreMatchers.containsString("max_score")));
	}

	private static final String _CLASS_NAME_ELASTICSEARCH_INDEX_SEARCHER =
		"com.liferay.portal.search.elasticsearch8.internal." +
			"ElasticsearchIndexSearcher";

	private static final String _CLASS_NAME_EXCEPTION_MAPPER =
		"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
			"ExceptionMapper";

	private static final String _CONFIGURATION_PID_ELASTICSEARCH =
		"com.liferay.portal.search.elasticsearch8.configuration." +
			"ElasticsearchConfiguration";

	private static final Pagination _PAGINATION = Pagination.of(1, 1);

	@DeleteAfterTestRun
	private User _user;

}