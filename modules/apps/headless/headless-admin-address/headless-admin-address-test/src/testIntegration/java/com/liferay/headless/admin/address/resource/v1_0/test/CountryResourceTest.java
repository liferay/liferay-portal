/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.address.client.dto.v1_0.Country;
import com.liferay.headless.admin.address.client.http.HttpInvoker;
import com.liferay.headless.admin.address.client.pagination.Page;
import com.liferay.headless.admin.address.client.pagination.Pagination;
import com.liferay.headless.admin.address.client.serdes.v1_0.CountrySerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.portal.kernel.exception.CountryA2Exception;
import com.liferay.portal.kernel.exception.CountryA3Exception;
import com.liferay.portal.kernel.exception.DuplicateCountryException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.RandomizerBumper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class CountryResourceTest extends BaseCountryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Page<Country> countriesPage = countryResource.getCountriesPage(
			null, null, Pagination.of(1, -1), null);

		for (Country country : countriesPage.getItems()) {
			_addCountryA2AndA3(country.getA2(), country.getA3());
		}
	}

	@Override
	@Test
	public void testGetCountriesPage() throws Exception {
		Page<Country> page = countryResource.getCountriesPage(
			null, null, Pagination.of(1, _getPageSize()), null);

		long totalCount = page.getTotalCount();

		Country country1 = testGetCountriesPage_addCountry(randomCountry());
		Country country2 = testGetCountriesPage_addCountry(randomCountry());

		page = countryResource.getCountriesPage(
			null, null, Pagination.of(1, _getPageSize()), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(country1, (List<Country>)page.getItems());
		assertContains(country2, (List<Country>)page.getItems());
		assertValid(page);
	}

	@Override
	@Test
	public void testGraphQLGetCountriesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"countries",
			HashMapBuilder.<String, Object>put(
				"page", 1
			).put(
				"pageSize", _getPageSize()
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject countriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/countries");

		long totalCount = countriesJSONObject.getLong("totalCount");

		Country country1 = testGraphQLGetCountriesPage_addCountry();
		Country country2 = testGraphQLGetCountriesPage_addCountry();

		countriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/countries");

		Assert.assertEquals(
			totalCount + 2, countriesJSONObject.getLong("totalCount"));

		assertContains(
			country1,
			Arrays.asList(
				CountrySerDes.toDTOs(countriesJSONObject.getString("items"))));
		assertContains(
			country2,
			Arrays.asList(
				CountrySerDes.toDTOs(countriesJSONObject.getString("items"))));
	}

	@Override
	@Test
	public void testPostCountry() throws Exception {
		super.testPostCountry();

		Country existingCountry = countryResource.getCountryByA2("US");

		Country country = randomCountry();

		country.setA2((String)null);

		_testPostCountryProblem(country, null);

		country.setA2("");

		_testPostCountryProblem(country, null);

		country.setA2("too long");

		_testPostCountryProblem(country, CountryA2Exception.class);

		country.setA2(existingCountry.getA2());

		_testPostCountryProblem(country, DuplicateCountryException.class);

		country = randomCountry();

		country.setA3((String)null);

		_testPostCountryProblem(country, null);

		country.setA3("");

		_testPostCountryProblem(country, null);

		country.setA3("too long");

		_testPostCountryProblem(country, CountryA3Exception.class);

		country.setA3(existingCountry.getA3());

		_testPostCountryProblem(country, DuplicateCountryException.class);

		country = randomCountry();

		country.setName((String)null);

		_testPostCountryProblem(country, null);

		country.setName("");

		_testPostCountryProblem(country, null);

		country.setName(existingCountry.getName());

		_testPostCountryProblem(country, DuplicateCountryException.class);

		country = randomCountry();

		country.setNumber((Integer)null);

		_testPostCountryProblem(country, null);

		country.setNumber(existingCountry.getNumber());

		_testPostCountryProblem(country, DuplicateCountryException.class);
	}

	@Override
	@Test
	public void testPutCountry() throws Exception {
		super.testPutCountry();

		Country country = _addCountry(randomCountry());
		Country existingCountry = countryResource.getCountryByA2("US");

		Country randomCountry = randomCountry();

		randomCountry.setA2((String)null);

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setA2("");

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setA2("too long");

		_testPutCountryProblem(
			country.getId(), randomCountry, CountryA2Exception.class);

		randomCountry.setA2(existingCountry.getA2());

		_testPutCountryProblem(
			country.getId(), randomCountry, DuplicateCountryException.class);

		randomCountry = randomCountry();

		randomCountry.setA3((String)null);

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setA3("");

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setA3("too long");

		_testPutCountryProblem(
			country.getId(), randomCountry, CountryA3Exception.class);

		randomCountry.setA3(existingCountry.getA3());

		_testPutCountryProblem(
			country.getId(), randomCountry, DuplicateCountryException.class);

		randomCountry = randomCountry();

		randomCountry.setName((String)null);

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setName("");

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setName(existingCountry.getName());

		_testPutCountryProblem(
			country.getId(), randomCountry, DuplicateCountryException.class);

		randomCountry = randomCountry();

		randomCountry.setNumber((Integer)null);

		_testPutCountryProblem(country.getId(), randomCountry, null);

		randomCountry.setNumber(existingCountry.getNumber());

		_testPutCountryProblem(
			country.getId(), randomCountry, DuplicateCountryException.class);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"a2", "a3", "name", "number"};
	}

	@Override
	protected Country randomCountry() throws Exception {
		Country country = super.randomCountry();

		country.setA2(
			RandomTestUtil.randomString(2, _getRandomizerBumper(_countryA2s)));
		country.setA3(
			RandomTestUtil.randomString(3, _getRandomizerBumper(_countryA3s)));
		country.setNumber(RandomTestUtil.nextInt());

		return country;
	}

	@Override
	protected Country testDeleteCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGetCountriesPage_addCountry(Country country)
		throws Exception {

		return _addCountry(country);
	}

	@Override
	protected void testGetCountriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Country, Country, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String keywords = RandomTestUtil.randomString();

		Country country1 = randomCountry();
		Country country2 = randomCountry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, country1, country2);
		}

		country1.setName(keywords + country1.getName());

		country1 = testGetCountriesPage_addCountry(country1);

		country2.setName(keywords + country2.getName());

		country2 = testGetCountriesPage_addCountry(country2);

		for (EntityField entityField : entityFields) {
			Page<Country> ascPage = countryResource.getCountriesPage(
				null, keywords, Pagination.of(1, 2),
				entityField.getName() + ":asc");

			assertEquals(
				Arrays.asList(country1, country2),
				(List<Country>)ascPage.getItems());

			Page<Country> descPage = countryResource.getCountriesPage(
				null, keywords, Pagination.of(1, 2),
				entityField.getName() + ":desc");

			assertEquals(
				Arrays.asList(country2, country1),
				(List<Country>)descPage.getItems());
		}
	}

	@Override
	protected Country testGetCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGetCountryByA2_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGetCountryByA3_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGetCountryByName_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGetCountryByNumber_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLDeleteCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLGetCountriesPage_addCountry()
		throws Exception {

		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLGetCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLGetCountryByA2_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLGetCountryByA3_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLGetCountryByName_addCountry()
		throws Exception {

		return _addCountry(randomCountry());
	}

	@Override
	protected Country testGraphQLGetCountryByNumber_addCountry()
		throws Exception {

		return _addCountry(randomCountry());
	}

	@Override
	protected Country testPatchCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	@Override
	protected Country testPostCountry_addCountry(Country country)
		throws Exception {

		return _addCountry(country);
	}

	@Override
	protected Country testPutCountry_addCountry() throws Exception {
		return _addCountry(randomCountry());
	}

	private Country _addCountry(Country country) throws Exception {
		country = countryResource.postCountry(country);

		_addCountryA2AndA3(country.getA2(), country.getA3());

		return country;
	}

	private void _addCountryA2AndA3(String a2, String a3) {
		_countryA2s.add(StringUtil.toLowerCase(a2));
		_countryA3s.add(StringUtil.toLowerCase(a3));
	}

	private int _getPageSize() {
		return _countryA2s.size() + 10;
	}

	private RandomizerBumper<String> _getRandomizerBumper(
		List<String> existingValues) {

		return randomValue ->
			StringUtil.isLowerCase(randomValue) &&
			!existingValues.contains(randomValue);
	}

	private <T extends Exception> void _testPostCountryProblem(
			Country country, Class<T> exceptionClass)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			countryResource.postCountryHttpResponse(country);

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(),
			httpResponse.getStatusCode());

		if (exceptionClass != null) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				httpResponse.getContent());

			Assert.assertEquals(
				exceptionClass.getSimpleName(), jsonObject.get("type"));
		}
	}

	private <T extends Exception> void _testPutCountryProblem(
			Long countryId, Country country, Class<T> exceptionClass)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			countryResource.putCountryHttpResponse(countryId, country);

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(),
			httpResponse.getStatusCode());

		if (exceptionClass != null) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				httpResponse.getContent());

			Assert.assertEquals(
				exceptionClass.getSimpleName(), jsonObject.get("type"));
		}
	}

	private final List<String> _countryA2s = new ArrayList<>();
	private final List<String> _countryA3s = new ArrayList<>();

	@Inject
	private JSONFactory _jsonFactory;

}