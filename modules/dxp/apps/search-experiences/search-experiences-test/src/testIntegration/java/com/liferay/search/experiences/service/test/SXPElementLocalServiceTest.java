/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.exception.DuplicateSXPElementExternalReferenceCodeException;
import com.liferay.search.experiences.exception.NoSuchSXPElementException;
import com.liferay.search.experiences.exception.SXPElementTitleException;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.service.SXPElementLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class SXPElementLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddSXPElement() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		SXPElement sxpElement = _addSXPElement(
			externalReferenceCode, TestPropsValues.getUserId());

		Assert.assertEquals(
			externalReferenceCode, sxpElement.getExternalReferenceCode());
		Assert.assertEquals("1.0", sxpElement.getVersion());

		// Duplicate external reference code in a different company

		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.getAdminUser(company.getCompanyId());

		SXPElement differentCompanySXPElement = _addSXPElement(
			sxpElement.getExternalReferenceCode(), user.getUserId());

		_companyLocalService.deleteCompany(company);

		Assert.assertEquals(
			sxpElement.getExternalReferenceCode(),
			differentCompanySXPElement.getExternalReferenceCode());

		// Duplicate external reference code in the same company

		try {
			_addSXPElement(
				sxpElement.getExternalReferenceCode(),
				TestPropsValues.getUserId());

			Assert.fail();
		}
		catch (DuplicateSXPElementExternalReferenceCodeException
					duplicateSXPElementExternalReferenceCodeException) {

			Assert.assertNotNull(
				duplicateSXPElementExternalReferenceCodeException);
		}

		// Null external reference code

		sxpElement = _addSXPElement(null, TestPropsValues.getUserId());

		Assert.assertNotNull(sxpElement.getExternalReferenceCode());
		Assert.assertEquals("1.0", sxpElement.getVersion());

		// Fallback description and fallback title

		String fallbackDescription = RandomTestUtil.randomString();
		String fallbackTitle = RandomTestUtil.randomString();

		SXPElement fallbackFieldsSXPElement = _addSXPElement(
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			"{}", RandomTestUtil.randomString(), fallbackDescription,
			fallbackTitle,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			TestPropsValues.getUserId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			fallbackDescription,
			fallbackFieldsSXPElement.getFallbackDescription());
		Assert.assertEquals(
			fallbackTitle, fallbackFieldsSXPElement.getFallbackTitle());

		String description = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		SXPElement noFallbackFieldsSXPElement = _addSXPElement(
			Collections.singletonMap(LocaleUtil.US, description), "{}",
			RandomTestUtil.randomString(), null, null,
			Collections.singletonMap(LocaleUtil.US, title),
			TestPropsValues.getUserId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			description, noFallbackFieldsSXPElement.getFallbackDescription());
		Assert.assertEquals(
			title, noFallbackFieldsSXPElement.getFallbackTitle());

		// Title

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		try {
			_addSXPElement(
				Collections.singletonMap(LocaleUtil.US, StringPool.BLANK), "{}",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), Collections.emptyMap(),
				TestPropsValues.getUserId(), serviceContext);
		}
		catch (SXPElementTitleException sxpElementTitleException) {
			Assert.assertNotNull(sxpElementTitleException);
		}

		// Validate

		String attributeName =
			"com.liferay.search.experiences.service.impl." +
				"SXPElementLocalServiceImpl#_validate";

		serviceContext.setAttribute(attributeName, Boolean.TRUE);

		try {
			_addSXPElement(
				Collections.singletonMap(LocaleUtil.US, StringPool.BLANK), "{}",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), Collections.emptyMap(),
				TestPropsValues.getUserId(), serviceContext);
		}
		catch (SXPElementTitleException sxpElementTitleException) {
			Assert.assertNotNull(sxpElementTitleException);
		}

		serviceContext.setAttribute(attributeName, Boolean.FALSE);

		sxpElement = _addSXPElement(
			Collections.emptyMap(), "{}", RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			Collections.emptyMap(), TestPropsValues.getUserId(),
			serviceContext);

		Assert.assertEquals(Collections.emptyMap(), sxpElement.getTitleMap());
	}

	@Test
	public void testGetSXPElementByExternalReferenceCode() throws Exception {
		SXPElement sxpElement = _addSXPElement(
			RandomTestUtil.randomString(), TestPropsValues.getUserId());

		Assert.assertEquals(
			sxpElement,
			_sxpElementLocalService.getSXPElementByExternalReferenceCode(
				sxpElement.getExternalReferenceCode(),
				TestPropsValues.getCompanyId()));

		try {
			_sxpElementLocalService.getSXPElementByExternalReferenceCode(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId());

			Assert.fail();
		}
		catch (NoSuchSXPElementException noSuchSXPElementException) {
			Assert.assertNotNull(noSuchSXPElementException);
		}
	}

	@Test
	public void testUpdateSXPElement() throws Exception {
		SXPElement sxpElement = _addSXPElement(
			RandomTestUtil.randomString(), TestPropsValues.getUserId());

		String externalReferenceCode = RandomTestUtil.randomString();

		sxpElement.setExternalReferenceCode(externalReferenceCode);

		sxpElement = _sxpElementLocalService.updateSXPElement(sxpElement);

		Assert.assertEquals(
			externalReferenceCode, sxpElement.getExternalReferenceCode());

		sxpElement = _sxpElementLocalService.updateSXPElement(
			sxpElement.getExternalReferenceCode(), sxpElement.getUserId(),
			sxpElement.getSXPElementId(), sxpElement.getDescriptionMap(),
			sxpElement.getElementDefinitionJSON(), sxpElement.isHidden(),
			sxpElement.getSchemaVersion(), sxpElement.getTitleMap(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			externalReferenceCode, sxpElement.getExternalReferenceCode());
		Assert.assertEquals("1.1", sxpElement.getVersion());
	}

	@Test
	public void testUpdateSXPElementWithLegacyRangeQuery() throws Exception {
		String elementDefinitionJSON = _getElementDefinitionJSON(
			JSONUtil.put(
				"from", 1
			).put(
				"include_lower", false
			).put(
				"to", 2
			));

		SXPElement sxpElement = _addSXPElement(
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			elementDefinitionJSON, null, null, null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			TestPropsValues.getUserId(),
			ServiceContextTestUtil.getServiceContext());

		String expectedElementDefinitionJSON = _getElementDefinitionJSON(
			JSONUtil.put(
				"gt", 1
			).put(
				"lte", 2
			));

		JSONAssert.assertEquals(
			expectedElementDefinitionJSON,
			sxpElement.getElementDefinitionJSON(), true);

		sxpElement = _sxpElementLocalService.updateSXPElement(
			sxpElement.getExternalReferenceCode(), sxpElement.getUserId(),
			sxpElement.getSXPElementId(), sxpElement.getDescriptionMap(),
			elementDefinitionJSON, sxpElement.isHidden(),
			sxpElement.getSchemaVersion(), sxpElement.getTitleMap(),
			ServiceContextTestUtil.getServiceContext());

		JSONAssert.assertEquals(
			expectedElementDefinitionJSON,
			sxpElement.getElementDefinitionJSON(), true);
	}

	@Test(expected = DuplicateSXPElementExternalReferenceCodeException.class)
	public void testUpdateSXPElementWithSameExternalReferenceCode()
		throws Exception {

		SXPElement sxpElement1 = _addSXPElement(
			RandomTestUtil.randomString(), TestPropsValues.getUserId());
		SXPElement sxpElement2 = _addSXPElement(
			RandomTestUtil.randomString(), TestPropsValues.getUserId());

		sxpElement2.setExternalReferenceCode(
			sxpElement1.getExternalReferenceCode());

		_sxpElementLocalService.updateSXPElement(sxpElement2);
	}

	private SXPElement _addSXPElement(
			Map<Locale, String> descriptionMap, String elementDefinitionJSON,
			String externalReferenceCode, String fallbackDescription,
			String fallbackTitle, Map<Locale, String> titleMap, long userId,
			ServiceContext serviceContext)
		throws Exception {

		SXPElement sxpElement = _sxpElementLocalService.addSXPElement(
			externalReferenceCode, userId, descriptionMap,
			elementDefinitionJSON, fallbackDescription, fallbackTitle, false,
			RandomTestUtil.randomString(), titleMap, 0, serviceContext);

		_sxpElements.add(sxpElement);

		return sxpElement;
	}

	private SXPElement _addSXPElement(String externalReferenceCode, long userId)
		throws Exception {

		return _addSXPElement(
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			"{}", externalReferenceCode, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			userId, ServiceContextTestUtil.getServiceContext());
	}

	private String _getElementDefinitionJSON(JSONObject fieldJSONObject) {
		return JSONUtil.put(
			"query",
			JSONUtil.put("range", JSONUtil.put("createDate", fieldJSONObject))
		).toString();
	}

	private static String _originalName;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private SXPElementLocalService _sxpElementLocalService;

	@DeleteAfterTestRun
	private List<SXPElement> _sxpElements = new ArrayList<>();

}