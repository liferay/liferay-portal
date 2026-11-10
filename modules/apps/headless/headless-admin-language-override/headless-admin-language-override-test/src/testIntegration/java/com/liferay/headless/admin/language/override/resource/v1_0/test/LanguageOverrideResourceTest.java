/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.language.override.client.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.language.override.client.pagination.Page;
import com.liferay.headless.admin.language.override.client.pagination.Pagination;
import com.liferay.headless.admin.language.override.client.problem.Problem;
import com.liferay.headless.admin.language.override.client.resource.v1_0.LanguageOverrideResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49852"))
@RunWith(Arquillian.class)
public class LanguageOverrideResourceTest
	extends BaseLanguageOverrideResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		_nestedFieldsLanguageOverrideResource =
			LanguageOverrideResource.builder(
			).authentication(
				adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).parameter(
				"nestedFields", "creator"
			).build();
	}

	@Test
	public void testGetLanguageOverrideByExternalReferenceCode()
		throws Exception {

		super.testGetLanguageOverrideByExternalReferenceCode();

		String externalReferenceCode = RandomTestUtil.randomString();

		_nestedFieldsLanguageOverrideResource.
			putLanguageOverrideByExternalReferenceCode(
				externalReferenceCode,
				_toLanguageOverride(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString()));

		LanguageOverride languageOverride =
			_nestedFieldsLanguageOverrideResource.
				getLanguageOverrideByExternalReferenceCode(
					externalReferenceCode);

		Assert.assertNotNull(languageOverride.getCreator());
		Assert.assertNotNull(languageOverride.getDateCreated());

		_nestedFieldsLanguageOverrideResource.
			deleteLanguageOverrideByExternalReferenceCode(
				externalReferenceCode);
	}

	@Test
	public void testGetLanguageOverridesPage() throws Exception {
		super.testGetLanguageOverridesPage();

		_testGetLanguageOverridesPageWithInFilter();
		_testGetLanguageOverridesPageWithInvalidFilter();
		_testGetLanguageOverridesPageWithOrFilter();
		_testGetLanguageOverridesPageWithoutPermission();
		_testGetLanguageOverridesPageWithSearch();
		_testGetLanguageOverridesPageWithSearchAndSort();
	}

	@Override
	@Test
	public void testGetLanguageOverridesPageWithFilterDateTimeEquals()
		throws Exception {

		LanguageOverride languageOverride =
			testGetLanguageOverridesPage_addLanguageOverride(
				randomLanguageOverride());

		LanguageOverride pastLanguageOverride = _getPastLanguageOverride(
			languageOverride);

		for (EntityField entityField :
				getEntityFields(EntityField.Type.DATE_TIME)) {

			Page<LanguageOverride> page =
				languageOverrideResource.getLanguageOverridesPage(
					languageOverride.getKey(),
					getFilterString(entityField, "between", languageOverride),
					null, null);

			Assert.assertEquals(1, page.getTotalCount());

			page = languageOverrideResource.getLanguageOverridesPage(
				languageOverride.getKey(),
				getFilterString(entityField, "between", pastLanguageOverride),
				null, null);

			Assert.assertEquals(0, page.getTotalCount());
		}
	}

	@Test
	public void testPutLanguageOverrideByExternalReferenceCode()
		throws Exception {

		super.testPutLanguageOverrideByExternalReferenceCode();

		String externalReferenceCode = RandomTestUtil.randomString();
		String key = RandomTestUtil.randomString();

		String value1 = RandomTestUtil.randomString();

		LanguageOverride languageOverride =
			languageOverrideResource.putLanguageOverrideByExternalReferenceCode(
				externalReferenceCode, _toLanguageOverride(key, value1));

		Assert.assertEquals(value1, languageOverride.getValue());

		String value2 = RandomTestUtil.randomString();

		LanguageOverride updatedLanguageOverride =
			languageOverrideResource.putLanguageOverrideByExternalReferenceCode(
				externalReferenceCode, _toLanguageOverride(key, value2));

		Assert.assertEquals(
			languageOverride.getId(), updatedLanguageOverride.getId());
		Assert.assertEquals(value2, updatedLanguageOverride.getValue());
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"languageId"};
	}

	@Override
	protected LanguageOverride randomLanguageOverride() throws Exception {
		LanguageOverride languageOverride = super.randomLanguageOverride();

		languageOverride.setLanguageId("en_US");

		return languageOverride;
	}

	@Override
	protected LanguageOverride
			testBatchEngineDeleteImportTask_addLanguageOverride()
		throws Exception {

		return _addLanguageOverride(randomLanguageOverride());
	}

	@Override
	protected LanguageOverride
			testDeleteLanguageOverrideByExternalReferenceCode_addLanguageOverride()
		throws Exception {

		return _addLanguageOverride(randomLanguageOverride());
	}

	@Override
	protected LanguageOverride
			testGetLanguageOverrideByExternalReferenceCode_addLanguageOverride()
		throws Exception {

		return _addLanguageOverride(randomLanguageOverride());
	}

	@Override
	protected LanguageOverride testGetLanguageOverridesPage_addLanguageOverride(
			LanguageOverride languageOverride)
		throws Exception {

		return _addLanguageOverride(languageOverride);
	}

	@Override
	protected LanguageOverride testPostLanguageOverride_addLanguageOverride(
			LanguageOverride languageOverride)
		throws Exception {

		return languageOverrideResource.postLanguageOverride(languageOverride);
	}

	@Override
	protected LanguageOverride
			testPutLanguageOverrideByExternalReferenceCode_addLanguageOverride()
		throws Exception {

		return _addLanguageOverride(randomLanguageOverride());
	}

	private LanguageOverride _addLanguageOverride(
			LanguageOverride languageOverride)
		throws Exception {

		return languageOverrideResource.
			putLanguageOverrideByExternalReferenceCode(
				languageOverride.getExternalReferenceCode(), languageOverride);
	}

	private void _addLanguageOverride(String keyString) throws Exception {
		LanguageOverride languageOverride = _toLanguageOverride(
			keyString, RandomTestUtil.randomString());

		languageOverride.setExternalReferenceCode(
			RandomTestUtil.randomString());

		_addLanguageOverride(languageOverride);
	}

	private LanguageOverride _getPastLanguageOverride(
			LanguageOverride languageOverride)
		throws Exception {

		LanguageOverride pastLanguageOverride = randomLanguageOverride();

		Date dateCreated = languageOverride.getDateCreated();

		pastLanguageOverride.setDateCreated(
			new Date(dateCreated.getTime() - Time.HOUR));

		Date dateModified = languageOverride.getDateModified();

		pastLanguageOverride.setDateModified(
			new Date(dateModified.getTime() - Time.HOUR));

		return pastLanguageOverride;
	}

	private void _testGetLanguageOverridesPageWithInFilter() throws Exception {
		LanguageOverride languageOverride = _addLanguageOverride(
			randomLanguageOverride());

		for (EntityField entityField :
				getEntityFields(EntityField.Type.DATE_TIME)) {

			Page<LanguageOverride> page =
				languageOverrideResource.getLanguageOverridesPage(
					null,
					StringBundler.concat(
						getFilterString(
							entityField, "between", languageOverride),
						" and externalReferenceCode in ('', '",
						languageOverride.getExternalReferenceCode(), "')"),
					null, null);

			Assert.assertEquals(
				Arrays.asList(languageOverride.getKey()), _toKeys(page));
		}
	}

	private void _testGetLanguageOverridesPageWithInvalidFilter()
		throws Exception {

		try {
			languageOverrideResource.getLanguageOverridesPage(
				null, "dateModified le now()", null, null);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}
	}

	private void _testGetLanguageOverridesPageWithOrFilter() throws Exception {
		LanguageOverride languageOverride = _addLanguageOverride(
			randomLanguageOverride());

		_addLanguageOverride(randomLanguageOverride());

		LanguageOverride pastLanguageOverride = _getPastLanguageOverride(
			languageOverride);

		for (EntityField entityField :
				getEntityFields(EntityField.Type.DATE_TIME)) {

			Page<LanguageOverride> page =
				languageOverrideResource.getLanguageOverridesPage(
					languageOverride.getKey(),
					StringBundler.concat(
						getFilterString(
							entityField, "between", pastLanguageOverride),
						" or ",
						getFilterString(
							entityField, "between", languageOverride)),
					null, null);

			Assert.assertEquals(
				Arrays.asList(languageOverride.getKey()), _toKeys(page));
		}
	}

	private void _testGetLanguageOverridesPageWithSearch() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		String key = RandomTestUtil.randomString();

		languageOverrideResource.putLanguageOverrideByExternalReferenceCode(
			externalReferenceCode,
			_toLanguageOverride(key, RandomTestUtil.randomString()));

		Page<LanguageOverride> page =
			languageOverrideResource.getLanguageOverridesPage(
				key, null, null, null);

		Assert.assertEquals(1, page.getTotalCount());

		List<LanguageOverride> items = new ArrayList<>(page.getItems());

		LanguageOverride languageOverride = items.get(0);

		Assert.assertEquals(key, languageOverride.getKey());
	}

	private void _testGetLanguageOverridesPageWithSearchAndSort()
		throws Exception {

		String keyPrefix = RandomTestUtil.randomString();

		_addLanguageOverride(keyPrefix + "-b");

		_addLanguageOverride(keyPrefix + "-a");

		Page<LanguageOverride> page =
			languageOverrideResource.getLanguageOverridesPage(
				keyPrefix, null, Pagination.of(1, 10), "key:asc");

		Assert.assertEquals(
			Arrays.asList(keyPrefix + "-a", keyPrefix + "-b"), _toKeys(page));

		page = languageOverrideResource.getLanguageOverridesPage(
			keyPrefix, null, Pagination.of(1, 10), "key:desc");

		Assert.assertEquals(
			Arrays.asList(keyPrefix + "-b", keyPrefix + "-a"), _toKeys(page));
	}

	private void _testGetLanguageOverridesPageWithoutPermission()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		LanguageOverrideResource userLanguageOverrideResource =
			LanguageOverrideResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		try {
			userLanguageOverrideResource.getLanguageOverridesPage(
				null,
				getFilterString(
					entityFields.get(0), "between", randomLanguageOverride()),
				null, null);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private List<String> _toKeys(Page<LanguageOverride> page) {
		List<String> keys = new ArrayList<>();

		for (LanguageOverride languageOverride : page.getItems()) {
			keys.add(languageOverride.getKey());
		}

		return keys;
	}

	private LanguageOverride _toLanguageOverride(
		String keyString, String valueString) {

		LanguageOverride languageOverride = new LanguageOverride();

		languageOverride.setKey(keyString);
		languageOverride.setLanguageId("en_US");
		languageOverride.setValue(valueString);

		return languageOverride;
	}

	private LanguageOverrideResource _nestedFieldsLanguageOverrideResource;

}