/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.test.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@LanguageIds(availableLanguageIds = "en_US", defaultLanguageId = "en_US")
@RunWith(Arquillian.class)
public class JournalTestUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddArticleWithDDMStructureAndDDMTemplate()
		throws Exception {

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL,
			JournalTestUtil.getSampleTemplateFTL());

		Assert.assertNotNull(
			JournalTestUtil.addArticleWithXMLContent(
				content, ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey()));
	}

	@Test
	public void testAddArticleWithFolder() throws Exception {
		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), 0, "Test Folder");

		Assert.assertNotNull(
			JournalTestUtil.addArticle(
				_group.getGroupId(), folder.getFolderId(), "Test Article",
				"This is a test article."));
	}

	@Test
	public void testAddArticleWithoutFolder() throws Exception {
		Assert.assertNotNull(
			JournalTestUtil.addArticle(
				_group.getGroupId(), "Test Article",
				"This is a test article."));
	}

	@Test
	public void testAddDDMStructure() throws Exception {
		Assert.assertNotNull(
			DDMStructureTestUtil.addStructure(JournalArticle.class.getName()));
	}

	@Test
	public void testAddDDMStructureWithDefinition() throws Exception {
		Assert.assertNotNull(
			DDMStructureTestUtil.addStructure(JournalArticle.class.getName()));
	}

	@Test
	public void testAddDDMStructureWithDefinitionAndLocale() throws Exception {
		Assert.assertNotNull(
			DDMStructureTestUtil.addStructure(
				JournalArticle.class.getName(), LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testAddDDMStructureWithLocale() throws Exception {
		Assert.assertNotNull(
			DDMStructureTestUtil.addStructure(
				JournalArticle.class.getName(), LocaleUtil.getSiteDefault()));
	}

	@Test(expected = LocaleException.class)
	public void testAddDDMStructureWithNonexistingLocale() throws Exception {
		DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName(), LocaleUtil.CANADA);
	}

	@Test
	public void testAddDDMTemplateToDDMStructure() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName());

		Assert.assertNotNull(
			DDMTemplateTestUtil.addTemplate(
				ddmStructure.getStructureId(),
				PortalUtil.getClassNameId(JournalArticle.class)));
	}

	@Test
	public void testAddDDMTemplateToDDMStructureWithLanguage()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName());

		Assert.assertNotNull(
			DDMTemplateTestUtil.addTemplate(
				ddmStructure.getStructureId(),
				PortalUtil.getClassNameId(JournalArticle.class),
				TemplateConstants.LANG_TYPE_FTL,
				JournalTestUtil.getSampleTemplateFTL()));
	}

	@Test
	public void testAddFolder() throws Exception {
		Assert.assertNotNull(
			JournalTestUtil.addFolder(_group.getGroupId(), 0, "Test Folder"));
	}

	@Test
	public void testDeleteDDMStructure() throws Exception {
		String content = DDMStructureTestUtil.getSampleStructuredContent();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName());

		Assert.assertNotNull(
			JournalTestUtil.addArticleWithXMLContent(
				TestPropsValues.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				PortalUtil.getClassNameId(DDMStructure.class),
				ddmStructure.getStructureId(), content,
				ddmStructure.getStructureKey(), null,
				LocaleUtil.getSiteDefault()));

		DDMStructureLocalServiceUtil.deleteStructure(ddmStructure);

		try {
			Assert.assertNull(
				JournalArticleLocalServiceUtil.getArticle(
					ddmStructure.getGroupId(), DDMStructure.class.getName(),
					ddmStructure.getStructureId()));
		}
		catch (NoSuchArticleException noSuchArticleException) {
		}
	}

	@Test
	public void testGetSampleStructureDefinition() {
		Assert.assertNotNull(DDMStructureTestUtil.getSampleDDMForm());
	}

	@Test
	public void testGetSampleTemplateVM() {
		Assert.assertEquals(
			"$name.getData()", JournalTestUtil.getSampleTemplateVM());
	}

	@Test
	public void testUpdateArticle() throws Exception {
		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(), "Test Article", "This is a test article.");

		Map<Locale, String> contents = HashMapBuilder.put(
			LocaleUtil.US, "This is an updated test article."
		).build();

		String defaultLanguageId = LanguageUtil.getLanguageId(
			LocaleUtil.getSiteDefault());

		String localizedContent =
			DDMStructureTestUtil.getSampleStructuredContent(
				contents, defaultLanguageId);

		Assert.assertNotNull(
			JournalTestUtil.updateArticle(
				article, article.getTitle(), localizedContent));
	}

	@DeleteAfterTestRun
	private DDMStructure _ddmStructure;

	@DeleteAfterTestRun
	private Group _group;

}