/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class JournalArticleModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			HashMapBuilder.put(
				LocaleUtil.ENGLISH, "english title"
			).put(
				LocaleUtil.SPAIN, "spanish title"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.ENGLISH, "english description"
			).put(
				LocaleUtil.SPAIN, "spanish description"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.ENGLISH, "english content"
			).put(
				LocaleUtil.SPAIN, "spanish content"
			).build(),
			LocaleUtil.SPAIN, false, true,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAvailableLanguageIds() throws Exception {
		Assert.assertEquals(
			SetUtil.fromArray(
				LocalizationUtil.getAvailableLanguageIds(
					_journalArticle.getDocument())),
			SetUtil.fromArray(_journalArticle.getAvailableLanguageIds()));
	}

	@Test
	public void testContributeArticleWithSeparatorContentField()
		throws Exception {

		JournalArticle journalArticle = _addArticleWithSeparatorContentField();

		DocumentImpl documentImpl = new DocumentImpl();

		_modelDocumentContributor.contribute(documentImpl, journalArticle);

		Assert.assertEquals(
			journalArticle.getArticleId(), documentImpl.get(Field.ARTICLE_ID));
		Assert.assertNotNull(documentImpl.getField(Field.UID));
		Assert.assertEquals(
			StringPool.BLANK, documentImpl.get(LocaleUtil.US, Field.CONTENT));
	}

	@Test
	public void testFieldContent() throws Exception {
		Document document = _getDocument();

		for (String languageId : _journalArticle.getAvailableLanguageIds()) {
			Assert.assertEquals(
				_getDDMIndexerContent(languageId),
				document.get(
					LocaleUtil.fromLanguageId(languageId), Field.CONTENT));
		}
	}

	@Test
	public void testFieldDefaultLanguageId() throws Exception {
		DDMStructure ddmStructure = _journalArticle.getDDMStructure();

		DDMFormValues ddmFormValues = _ddmFieldLocalService.getDDMFormValues(
			ddmStructure.getDDMForm(), _journalArticle.getId());

		Document document = _getDocument();

		Assert.assertEquals(
			LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale()),
			document.get("defaultLanguageId"));
	}

	private JournalArticle _addArticleWithSeparatorContentField()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createLocalizableTextDDMFormField("name"));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createSeparatorDDMFormField("content", false));

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), ddmForm);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, "${name.getData()}",
			LocaleUtil.US);

		return JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), _getSeparatorContent(),
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());
	}

	private String _getDDMIndexerContent(String languageId) throws Exception {
		com.liferay.portal.kernel.xml.Document document =
			_journalArticle.getDocument();

		DDMStructure ddmStructure = _journalArticle.getDDMStructure();

		return _ddmIndexer.extractIndexableAttributes(
			ddmStructure,
			_fieldsToDDMFormValuesConverter.convert(
				ddmStructure,
				_journalConverter.getDDMFields(ddmStructure, document.asXML())),
			LocaleUtil.fromLanguageId(languageId));
	}

	private Document _getDocument() {
		DocumentImpl documentImpl = new DocumentImpl();

		Assert.assertNotNull(_journalArticle.getDDMFormValues());

		_modelDocumentContributor.contribute(documentImpl, _journalArticle);

		return documentImpl;
	}

	private String _getSeparatorContent() {
		com.liferay.portal.kernel.xml.Document document =
			SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("available-locales", "en_US");
		rootElement.addAttribute("default-locale", "en_US");

		rootElement.addElement("request");

		Element nameElement = rootElement.addElement("dynamic-element");

		nameElement.addAttribute("index-type", "keyword");
		nameElement.addAttribute("instance-id", RandomTestUtil.randomString());
		nameElement.addAttribute("name", "name");
		nameElement.addAttribute("type", "text");

		Element dynamicContentElement = nameElement.addElement(
			"dynamic-content");

		dynamicContentElement.addAttribute("language-id", "en_US");
		dynamicContentElement.addCDATA("english content");

		Element contentElement = rootElement.addElement("dynamic-element");

		contentElement.addAttribute(
			"instance-id", RandomTestUtil.randomString());
		contentElement.addAttribute("name", "content");
		contentElement.addAttribute("type", "separator");

		return document.asXML();
	}

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMIndexer _ddmIndexer;

	@Inject
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private JournalArticle _journalArticle;

	@Inject
	private JournalConverter _journalConverter;

	@Inject(
		filter = "indexer.class.name=com.liferay.journal.model.JournalArticle"
	)
	private ModelDocumentContributor<JournalArticle> _modelDocumentContributor;

}