/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.DuplicateDDMTemplateExternalReferenceCodeException;
import com.liferay.dynamic.data.mapping.exception.TemplateCreationDisabledException;
import com.liferay.dynamic.data.mapping.exception.TemplateDuplicateTemplateKeyException;
import com.liferay.dynamic.data.mapping.exception.TemplateNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateScriptException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.util.comparator.TemplateIdComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class DDMTemplateLocalServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_classNameId = PortalUtil.getClassNameId(DDL_RECORD_CLASS_NAME);
		_resourceClassNameId = PortalUtil.getClassNameId(
			DDL_RECORD_SET_CLASS_NAME);
	}

	@Test
	public void testAddTemplate() throws Exception {
		DDMTemplate template = _ddmTemplateLocalService.addTemplate(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			group.getGroupId(), _classNameId, 0, _resourceClassNameId,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(
			Validator.isNotNull(template.getExternalReferenceCode()));
	}

	@Test(expected = TemplateDuplicateTemplateKeyException.class)
	public void testAddTemplateWithDuplicateKey() throws Exception {
		String templateKey = RandomTestUtil.randomString();
		String language = TemplateConstants.LANG_TYPE_VM;

		addTemplate(
			_classNameId, 0, templateKey, "Test Template 1",
			DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE, language,
			getTestTemplateScript(language), WorkflowConstants.STATUS_APPROVED);
		addTemplate(
			_classNameId, 0, templateKey, "Test Template 2",
			DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE, language,
			getTestTemplateScript(language), WorkflowConstants.STATUS_APPROVED);
	}

	@Test(expected = DuplicateDDMTemplateExternalReferenceCodeException.class)
	public void testAddTemplateWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_ddmTemplateLocalService.addTemplate(
			externalReferenceCode, TestPropsValues.getUserId(),
			group.getGroupId(), _classNameId, 0, _resourceClassNameId,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			ServiceContextTestUtil.getServiceContext());
		_ddmTemplateLocalService.addTemplate(
			externalReferenceCode, TestPropsValues.getUserId(),
			group.getGroupId(), _classNameId, 0, _resourceClassNameId,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = TemplateCreationDisabledException.class)
	public void testAddTemplateWithTemplateCreationDisabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.dynamic.data.mapping.configuration." +
						"DDMWebConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enableTemplateCreation", false
					).build())) {

			addTemplate(
				_classNameId, 0, null, "Test Template",
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE,
				TemplateConstants.LANG_TYPE_VM,
				getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
				WorkflowConstants.STATUS_APPROVED);
		}
	}

	@Test(expected = TemplateNameException.class)
	public void testAddTemplateWithoutName() throws Exception {
		addTemplate(
			_classNameId, 0, null, StringPool.BLANK,
			DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test(expected = TemplateScriptException.class)
	public void testAddTemplateWithoutScript() throws Exception {
		addTemplate(
			_classNameId, 0, null, "Test Template",
			DDMTemplateConstants.TEMPLATE_TYPE_FORM,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM, StringPool.BLANK,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testCopyTemplates() throws Exception {
		int initialCount = DDMTemplateLocalServiceUtil.getTemplatesCount(
			group.getGroupId(), _classNameId, 0);

		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Test Template",
			"Test Template", WorkflowConstants.STATUS_APPROVED);

		copyTemplate(template);

		int count = DDMTemplateLocalServiceUtil.getTemplatesCount(
			group.getGroupId(), _classNameId, 0);

		Assert.assertEquals(initialCount + 2, count);
	}

	@Test
	public void testDeleteTemplate() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template",
			WorkflowConstants.STATUS_APPROVED);

		DDMTemplateLocalServiceUtil.deleteTemplate(template.getTemplateId());

		Assert.assertNull(
			DDMTemplateLocalServiceUtil.fetchDDMTemplate(
				template.getTemplateId()));
	}

	@Test
	public void testDeleteTemplateByExternalReferenceCode() throws Exception {
		DDMTemplate template = _ddmTemplateLocalService.addTemplate(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			group.getGroupId(), _classNameId, 0, _resourceClassNameId,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			ServiceContextTestUtil.getServiceContext());

		_ddmTemplateLocalService.deleteTemplate(
			template.getExternalReferenceCode(), template.getGroupId());

		Assert.assertNull(
			_ddmTemplateLocalService.fetchTemplate(template.getTemplateId()));
	}

	@Test
	public void testFetchDDMTemplateByExternalReferenceCode() throws Exception {
		Company company = _companyLocalService.getCompany(group.getCompanyId());

		DDMTemplate template = _ddmTemplateLocalService.addTemplate(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			company.getGroupId(), _classNameId, 0, _resourceClassNameId,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(
			_ddmTemplateLocalService.fetchDDMTemplateByExternalReferenceCode(
				template.getExternalReferenceCode(), group.getGroupId(),
				false));
		Assert.assertNotNull(
			_ddmTemplateLocalService.fetchDDMTemplateByExternalReferenceCode(
				template.getExternalReferenceCode(), group.getGroupId(), true));

		_ddmTemplateLocalService.deleteTemplate(template);
	}

	@Test
	public void testFetchTemplate() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, _resourceClassNameId, "Test Template",
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertNotNull(
			DDMTemplateLocalServiceUtil.fetchTemplate(
				template.getGroupId(), _classNameId,
				template.getTemplateKey()));
	}

	@Test
	public void testGetTemplates() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, _resourceClassNameId, "Test Template",
			WorkflowConstants.STATUS_APPROVED);

		List<DDMTemplate> ddmTemplates =
			DDMTemplateLocalServiceUtil.getTemplates(
				template.getGroupId(), template.getClassNameId());

		Assert.assertTrue(
			ddmTemplates.toString(), ddmTemplates.contains(template));
	}

	@Test
	public void testSearchByAnyStatus() throws Exception {
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Event", null,
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Contact", null,
			WorkflowConstants.STATUS_DRAFT);

		List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, null, null, null, null, null,
			WorkflowConstants.STATUS_ANY, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmTemplates.toString(), 2, ddmTemplates.size());
	}

	@Test
	public void testSearchByDescription() throws Exception {
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Event", "Event",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Contact", "Contact",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Meeting", "Meeting",
			WorkflowConstants.STATUS_APPROVED);

		List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, null, "Meeting", null, null, null,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmTemplates.toString(), 1, ddmTemplates.size());

		DDMTemplate template = ddmTemplates.get(0);

		Assert.assertEquals(
			"Meeting", template.getDescription(group.getDefaultLanguageId()));
	}

	@Test
	public void testSearchByDraftStatus() throws Exception {
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Event", null,
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Contact", null,
			WorkflowConstants.STATUS_DRAFT);

		List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, null, null, null, null, null,
			WorkflowConstants.STATUS_DRAFT, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmTemplates.toString(), 1, ddmTemplates.size());
	}

	@Test
	public void testSearchByName() throws Exception {
		addDisplayTemplate(
			_classNameId, _resourceClassNameId, "Event",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, _resourceClassNameId, "Contact",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, _resourceClassNameId, "Meeting",
			WorkflowConstants.STATUS_APPROVED);

		List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, "Event", null, null, null, null,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmTemplates.toString(), 1, ddmTemplates.size());
		Assert.assertEquals("Event", getTemplateName(ddmTemplates.get(0)));
	}

	@Test
	public void testSearchByNameAndDescription1() throws Exception {
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Event", "Event",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Contact", "Contact",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Meeting", "Meeting",
			WorkflowConstants.STATUS_APPROVED);

		List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, 0, "Event", "Meeting", null, null, null,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmTemplates.toString(), 2, ddmTemplates.size());
	}

	@Test
	public void testSearchByNameAndDescription2() throws Exception {
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Event", "Event",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Contact", "Contact",
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Meeting", "Meeting",
			WorkflowConstants.STATUS_APPROVED);

		List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, "Event", "Meeting", null, null,
			null, WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, TemplateIdComparator.getInstance(true));

		Assert.assertEquals("Event", getTemplateName(ddmTemplates.get(0)));
		Assert.assertEquals("Meeting", getTemplateName(ddmTemplates.get(1)));
	}

	@Test
	public void testSearchCount() throws Exception {
		int initialCount = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			0, "Test Template", null, null, null, null,
			WorkflowConstants.STATUS_APPROVED, false);

		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Test Template",
			"Test Template", WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			_resourceClassNameId, "Test Template", null, null, null, null,
			WorkflowConstants.STATUS_APPROVED, false);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchCountByClassNameIdAndClassPK() throws Exception {
		long classNameId1 = RandomTestUtil.randomLong();
		long classPK1 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			classNameId1, classPK1, _resourceClassNameId,
			StringUtil.randomString(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		addDisplayTemplate(
			classNameId1, RandomTestUtil.randomLong(), _resourceClassNameId,
			StringUtil.randomString(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		long classNameId2 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			classNameId2, RandomTestUtil.randomLong(), _resourceClassNameId,
			StringUtil.randomString(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			new long[] {classNameId1}, new long[] {classPK1},
			_resourceClassNameId, null, null, null,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(1, count);

		count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			new long[] {classNameId2}, new long[] {classPK1},
			_resourceClassNameId, null, null, null,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(0, count);
	}

	@Test
	public void testSearchCountByClassNameIds() throws Exception {
		long classNameId1 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			classNameId1, _resourceClassNameId, StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			classNameId1, _resourceClassNameId, StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			classNameId1, _resourceClassNameId, StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		long classNameId2 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			classNameId2, _resourceClassNameId, StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			classNameId2, _resourceClassNameId, StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			new long[] {classNameId1}, null, _resourceClassNameId, null, null,
			null, WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(3, count);

		count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			new long[] {classNameId1, classNameId2}, null, _resourceClassNameId,
			null, null, null, WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(5, count);
	}

	@Test
	public void testSearchCountByClassPKs() throws Exception {
		long classPK1 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			_classNameId, classPK1, _resourceClassNameId,
			StringUtil.randomString(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		long classPK2 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			_classNameId, classPK2, _resourceClassNameId,
			StringUtil.randomString(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		long classPK3 = RandomTestUtil.randomLong();

		addDisplayTemplate(
			_classNameId, classPK3, _resourceClassNameId,
			StringUtil.randomString(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, new long[] {classPK1, classPK2, classPK3},
			_resourceClassNameId, null, null, null,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(3, count);
	}

	@Test
	public void testSearchCountByKeywords() throws Exception {
		int initialCount = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			0, null, null, null, WorkflowConstants.STATUS_APPROVED);

		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, "Test Template",
			"Test Template", WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			_resourceClassNameId, "Test", null, null,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchCountByLanguage() throws Exception {
		String velocityLanguage = TemplateConstants.LANG_TYPE_VM;

		addTemplate(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			velocityLanguage, getTestTemplateScript(velocityLanguage),
			WorkflowConstants.STATUS_APPROVED);
		addTemplate(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			velocityLanguage, getTestTemplateScript(velocityLanguage),
			WorkflowConstants.STATUS_APPROVED);

		String freeMarkerLanguage = TemplateConstants.LANG_TYPE_FTL;

		addTemplate(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			freeMarkerLanguage, getTestTemplateScript(freeMarkerLanguage),
			WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, null, null, null, null,
			TemplateConstants.LANG_TYPE_VM, WorkflowConstants.STATUS_APPROVED,
			true);

		Assert.assertEquals(2, count);
	}

	@Test
	public void testSearchCountByResourceClassNameId() throws Exception {
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, StringUtil.randomString(),
			StringUtil.randomString(), WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, StringUtil.randomString(),
			StringUtil.randomString(), WorkflowConstants.STATUS_APPROVED);
		addDisplayTemplate(
			_classNameId, 0, _resourceClassNameId, StringUtil.randomString(),
			StringUtil.randomString(), WorkflowConstants.STATUS_APPROVED);

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			null, null, _resourceClassNameId, null, null, null,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(3, count);
	}

	@Test
	public void testSmallImageWithInvalidURL() throws Exception {
		DDMTemplate template = addTemplate(
			_classNameId, 0, _resourceClassNameId, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			DDMTemplateConstants.TEMPLATE_TYPE_FORM,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			WorkflowConstants.STATUS_APPROVED, true, "foo");

		Assert.assertFalse(template.isSmallImage());
	}

	@Test
	public void testSmallImageWithValidURL() throws Exception {
		DDMTemplate template = addTemplate(
			_classNameId, 0, _resourceClassNameId, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			DDMTemplateConstants.TEMPLATE_TYPE_FORM,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			TemplateConstants.LANG_TYPE_VM,
			getTestTemplateScript(TemplateConstants.LANG_TYPE_VM),
			WorkflowConstants.STATUS_APPROVED, true,
			"http://foo.com/example.png");

		Assert.assertTrue(template.isSmallImage());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected DDMTemplate copyTemplate(DDMTemplate template) throws Exception {
		return DDMTemplateLocalServiceUtil.copyTemplate(
			template.getUserId(), template.getTemplateId(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	protected String getTemplateName(DDMTemplate template) {
		return template.getName(group.getDefaultLanguageId());
	}

	private static long _classNameId;
	private static long _resourceClassNameId;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

}