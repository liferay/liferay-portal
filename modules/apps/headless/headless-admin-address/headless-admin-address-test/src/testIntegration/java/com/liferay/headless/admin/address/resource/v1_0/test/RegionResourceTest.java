/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.address.client.dto.v1_0.Creator;
import com.liferay.headless.admin.address.client.dto.v1_0.Region;
import com.liferay.headless.admin.address.client.http.HttpInvoker;
import com.liferay.headless.admin.address.client.pagination.Page;
import com.liferay.headless.admin.address.client.pagination.Pagination;
import com.liferay.headless.admin.address.client.resource.v1_0.RegionResource;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.portal.kernel.exception.DuplicateRegionException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;

import jakarta.ws.rs.core.Response;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class RegionResourceTest extends BaseRegionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_country = _countryLocalService.addCountry(
			null, "a1", "a11", true, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Override
	@Test
	public void testGetRegion() throws Exception {
		super.testGetRegion();

		_testGetRegionWithNestedFields();
	}

	@Override
	@Test
	public void testGetRegionsPage() throws Exception {
		String keywords = RandomTestUtil.randomString();

		Page<Region> page = regionResource.getRegionsPage(
			null, keywords, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Region region1 = _addRegion(keywords);
		Region region2 = _addRegion(keywords);

		page = regionResource.getRegionsPage(
			null, keywords, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(region1, (List<Region>)page.getItems());
		assertContains(region2, (List<Region>)page.getItems());
		assertValid(page);

		_testGetRegionsPageWithFilter();
	}

	@Override
	@Test
	public void testGetRegionsPageWithPagination() throws Exception {
		String keywords = RandomTestUtil.randomString();

		Region region1 = _addRegion(keywords);
		Region region2 = _addRegion(keywords);
		Region region3 = _addRegion(keywords);

		Page<Region> page1 = regionResource.getRegionsPage(
			null, keywords, null, Pagination.of(1, 2), null);

		Assert.assertEquals(3, page1.getTotalCount());

		List<Region> page1Items = (List<Region>)page1.getItems();

		Assert.assertEquals(page1Items.toString(), 2, page1Items.size());

		Page<Region> page2 = regionResource.getRegionsPage(
			null, keywords, null, Pagination.of(2, 2), null);

		List<Region> page2Items = (List<Region>)page2.getItems();

		Assert.assertEquals(page2Items.toString(), 1, page2Items.size());

		Page<Region> page3 = regionResource.getRegionsPage(
			null, keywords, null, Pagination.of(1, 3), null);

		List<Region> page3Items = (List<Region>)page3.getItems();

		assertContains(region1, page3Items);
		assertContains(region2, page3Items);
		assertContains(region3, page3Items);
	}

	@Override
	@Test
	public void testPostCountryRegion() throws Exception {
		super.testPostCountryRegion();

		Region existingRegion = _addRegion(randomRegion());

		Region region = randomRegion();

		region.setName((String)null);

		_testPostCountryRegionProblem(region, null);

		region.setName("");

		_testPostCountryRegionProblem(region, null);

		region = randomRegion();

		region.setRegionCode((String)null);

		_testPostCountryRegionProblem(region, null);

		region.setRegionCode("");

		_testPostCountryRegionProblem(region, null);

		region.setRegionCode(existingRegion.getRegionCode());

		_testPostCountryRegionProblem(region, DuplicateRegionException.class);

		region = randomRegion();

		region.setPosition((Double)null);

		Region postRegion = regionResource.postCountryRegion(
			_country.getCountryId(), region);

		Assert.assertEquals(
			postRegion.getPosition(), GetterUtil.DEFAULT_DOUBLE, 0);
	}

	@Override
	@Test
	public void testPutRegion() throws Exception {
		super.testPutRegion();

		Region region1 = _addRegion(randomRegion());

		Region randomRegion = randomRegion();

		randomRegion.setName((String)null);

		_testPutRegionProblem(region1.getId(), randomRegion, null);

		randomRegion.setName("");

		_testPutRegionProblem(region1.getId(), randomRegion, null);

		randomRegion = randomRegion();

		randomRegion.setRegionCode((String)null);

		_testPutRegionProblem(region1.getId(), randomRegion, null);

		randomRegion.setRegionCode("");

		_testPutRegionProblem(region1.getId(), randomRegion, null);

		Region region2 = _addRegion(randomRegion());

		randomRegion.setRegionCode(region2.getRegionCode());

		_testPutRegionProblem(
			region1.getId(), randomRegion, DuplicateRegionException.class);

		randomRegion = randomRegion();

		randomRegion.setPosition((Double)null);

		Region region3 = regionResource.putRegion(
			region1.getId(), randomRegion);

		Assert.assertEquals(
			region3.getPosition(), GetterUtil.DEFAULT_DOUBLE, 0);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "position", "regionCode"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"dateCreated", "dateModified"};
	}

	@Override
	protected Region randomRegion() throws Exception {
		Region region = super.randomRegion();

		region.setCountryId(_country.getCountryId());

		return region;
	}

	@Override
	protected Region testDeleteRegion_addRegion() throws Exception {
		return _addRegion(randomRegion());
	}

	@Override
	protected Region testDeleteRegionByExternalReferenceCode_addRegion()
		throws Exception {

		return _addRegion(randomRegion());
	}

	@Override
	protected Region testGetCountryRegionByRegionCode_addRegion()
		throws Exception {

		return _addRegion(randomRegion());
	}

	@Override
	protected Long testGetCountryRegionByRegionCode_getCountryId(Region region)
		throws Exception {

		return region.getCountryId();
	}

	@Override
	protected Long testGetCountryRegionsPage_getCountryId() throws Exception {
		return _country.getCountryId();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetCountryRegionsPage_getExpectedActions(Long countryId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected Long testGetCountryRegionsPage_getIrrelevantCountryId()
		throws Exception {

		Country country = _countryLocalService.addCountry(
			null, "a2", "a22", true, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());

		return country.getCountryId();
	}

	@Override
	protected Region testGetRegion_addRegion() throws Exception {
		return _addRegion(randomRegion());
	}

	@Override
	protected Region testGetRegionByExternalReferenceCode_addRegion()
		throws Exception {

		return _addRegion(randomRegion());
	}

	@Override
	protected Region testGetRegionsPage_addRegion(Region region)
		throws Exception {

		return _addRegion(region);
	}

	@Override
	protected void testGetRegionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Region, Region, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Region region1 = randomRegion();
		Region region2 = randomRegion();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, region1, region2);
		}

		String keywords = RandomTestUtil.randomString();

		region1.setName(keywords + region1.getName());

		region1 = testGetRegionsPage_addRegion(region1);

		region2.setName(keywords + region2.getName());

		region2 = testGetRegionsPage_addRegion(region2);

		for (EntityField entityField : entityFields) {
			Page<Region> ascPage = regionResource.getRegionsPage(
				null, keywords, null, Pagination.of(1, 2),
				entityField.getName() + ":asc");

			assertEquals(
				Arrays.asList(region1, region2),
				(List<Region>)ascPage.getItems());

			Page<Region> descPage = regionResource.getRegionsPage(
				null, keywords, null, Pagination.of(1, 2),
				entityField.getName() + ":desc");

			assertEquals(
				Arrays.asList(region2, region1),
				(List<Region>)descPage.getItems());
		}
	}

	@Override
	protected Long testGraphQLGetCountryRegionByRegionCode_getCountryId(
			Region region)
		throws Exception {

		return region.getCountryId();
	}

	@Override
	protected Long testGraphQLPostCountryRegion_getCountryId(Region region)
		throws Exception {

		return _country.getCountryId();
	}

	@Override
	protected Region testGraphQLRegion_addRegion() throws Exception {
		Region region = randomRegion();

		region.setPosition((double)Integer.MAX_VALUE);

		return _addRegion(region);
	}

	@Override
	protected Region testPatchRegion_addRegion() throws Exception {
		return _addRegion(randomRegion());
	}

	@Override
	protected Region testPatchRegionByExternalReferenceCode_addRegion()
		throws Exception {

		return _addRegion(randomRegion());
	}

	@Override
	protected Region testPutRegion_addRegion() throws Exception {
		return _addRegion(randomRegion());
	}

	@Override
	protected Region testPutRegionByExternalReferenceCode_addRegion()
		throws Exception {

		return _addRegion(randomRegion());
	}

	private Region _addRegion(Region region) throws Exception {
		return regionResource.postCountryRegion(
			_country.getCountryId(), region);
	}

	private Region _addRegion(String keyword) throws Exception {
		Region region = randomRegion();

		region.setName(keyword + RandomTestUtil.randomString());

		return _addRegion(region);
	}

	private <T extends Exception> void _assertProblem(
			Class<T> exceptionClass,
			UnsafeSupplier<HttpInvoker.HttpResponse, Exception>
				httpResponseUnsafeSupplier)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseExceptionMapper.class.getName(), LoggerTestUtil.OFF)) {

			HttpInvoker.HttpResponse httpResponse =
				httpResponseUnsafeSupplier.get();

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
	}

	private void _testGetRegionWithNestedFields() throws Exception {
		Region postRegion = _addRegion(randomRegion());

		RegionResource regionResource = RegionResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "creator"
		).build();

		Region getRegion = regionResource.getRegion(postRegion.getId());

		Creator creator = getRegion.getCreator();

		User user = TestPropsValues.getUser();

		Assert.assertEquals(creator.getId(), Long.valueOf(user.getUserId()));
		Assert.assertTrue(
			Objects.equals(
				creator.getExternalReferenceCode(),
				user.getExternalReferenceCode()));
	}

	private void _testGetRegionsPageWithFilter() throws Exception {
		String keywords = RandomTestUtil.randomString();

		// Sleep for 1 second to ensure that region 1 and existing
		// regions are created 1 second apart

		Thread.sleep(1000);

		Region region1 = _addRegion(keywords);

		// Sleep for 1 second to ensure that region 1 and region 2 are created
		// 1 second apart

		Thread.sleep(1000);

		Region region2 = _addRegion(keywords);

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Page<Region> page = regionResource.getRegionsPage(
			null, keywords,
			"dateCreated lt " + dateFormat.format(region1.getDateCreated()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		page = regionResource.getRegionsPage(
			null, keywords,
			"dateCreated ge " + dateFormat.format(region1.getDateCreated()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(2, page.getTotalCount());

		// Sleep for 1 second to ensure that region 1 and region 2 are modified
		// 1 second apart

		Thread.sleep(1000);

		region1.setName(
			keywords + StringUtil.toLowerCase(RandomTestUtil.randomString()));

		region1 = regionResource.patchRegion(region1.getId(), region1);

		page = regionResource.getRegionsPage(
			null, keywords,
			"dateModified ge " + dateFormat.format(region1.getDateModified()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(region1, (List<Region>)page.getItems());

		page = regionResource.getRegionsPage(
			null, keywords,
			"dateModified lt " + dateFormat.format(region1.getDateModified()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(region2, (List<Region>)page.getItems());
	}

	private <T extends Exception> void _testPostCountryRegionProblem(
			Region region, Class<T> exceptionClass)
		throws Exception {

		_assertProblem(
			exceptionClass,
			() -> regionResource.postCountryRegionHttpResponse(
				_country.getCountryId(), region));
	}

	private <T extends Exception> void _testPutRegionProblem(
			Long regionId, Region region, Class<T> exceptionClass)
		throws Exception {

		_assertProblem(
			exceptionClass,
			() -> regionResource.putRegionHttpResponse(regionId, region));
	}

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private RegionLocalService _regionLocalService;

}