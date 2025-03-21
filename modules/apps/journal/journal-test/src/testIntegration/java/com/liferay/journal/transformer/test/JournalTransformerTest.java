/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.transformer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.constants.JournalStructureConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.template.TemplateVariableDefinition;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class JournalTransformerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> journalUtilClass = ReflectionTestUtil.getFieldValue(
			JournalTestUtil.class, "_JOURNAL_UTIL_CLASS");

		ClassLoader classLoader = journalUtilClass.getClassLoader();

		Class<?> journalTransformerClass = classLoader.loadClass(
			"com.liferay.journal.internal.transformer.JournalTransformer");

		_journalTransformer = journalTransformerClass.newInstance();
		_transformMethod = ReflectionTestUtil.getMethod(
			journalTransformerClass, "transform", JournalArticle.class,
			DDMTemplate.class, JournalHelper.class, String.class,
			LayoutDisplayPageProviderRegistry.class, List.class,
			PortletRequestModel.class, boolean.class, String.class,
			ThemeDisplay.class, String.class);

		Bundle bundle = FrameworkUtil.getBundle(journalUtilClass);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundle.getBundleContext(), TransformerListener.class,
			"(jakarta.portlet.name=" + JournalPortletKeys.JOURNAL + ")");
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceTrackerList.close();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				StringUtil.replace(
					_read("data_definition.json"),
					new String[] {"[$FIELD_SET_NAME$]"},
					new String[] {"FieldsGroup19507604"}),
				TestPropsValues.getUser());

		_journalArticle = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			StringUtil.replace(
				_read("journal_article_content.xml"),
				new String[] {"[$FIELD_SET_NAME$]"},
				new String[] {"FieldsGroup19507604"}),
			dataDefinition.getDataDefinitionKey(), null);
	}

	@Test
	public void testCreateTemplateNode() {
		_testCreateTemplateNodeDocumentLibraryDDMFormField();
		_testCreateTemplateNodeSelectTypeDDMFormFieldWithNoOptions();
		_testCreateTemplateNodeSelectTypeDDMFormFieldWithOptions();
		_testCreateTemplateNodeSelectTypeDDMFormFieldWithoutOptions();
		_testCreateTemplateNodeTextDDMFormFieldWithHTML();
		_testCreateTemplateNodeTextDDMFormFieldWithPlainText();
	}

	@Test
	public void testFTLTransformation() throws Exception {
		Assert.assertEquals(
			"Joe Bloggs - print",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${name.getData()} - ${viewMode}", null,
				Constants.PRINT));
	}

	@Test
	public void testIncludeNestedFieldBackwardsCompatibility()
		throws Exception {

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				StringUtil.replace(
					_read("data_definition.json"),
					new String[] {"[$FIELD_SET_NAME$]"},
					new String[] {"birthdayFieldSet"}),
				TestPropsValues.getUser());

		_journalArticle = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			StringUtil.replace(
				_read("journal_article_content.xml"),
				new String[] {"[$FIELD_SET_NAME$]"},
				new String[] {"birthdayFieldSet"}),
			dataDefinition.getDataDefinitionKey(), null);

		Assert.assertEquals(
			"2022-11-26",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${birthday.getData()}", null, Constants.VIEW));
	}

	@Test
	public void testJournalReservedVariables() throws Exception {
		TemplateHandler journalTemplateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(
				JournalArticle.class.getName());

		Map<String, TemplateVariableGroup> templateVariableGroups =
			journalTemplateHandler.getTemplateVariableGroups(
				_journalArticle.getDDMStructureId(),
				TemplateConstants.LANG_TYPE_FTL,
				LocaleUtil.getMostRelevantLocale());

		TemplateVariableGroup journalReservedTemplateVariableGroup =
			templateVariableGroups.get("journal-reserved");

		Assert.assertNotNull(journalReservedTemplateVariableGroup);

		User user = TestPropsValues.getUser();
		String languageId = _journalArticle.getDefaultLanguageId();
		List<TransformerListener> transformerListeners = ListUtil.filter(
			_serviceTrackerList.toList(), TransformerListener::isEnabled);

		_assertReservedVariable(
			user.getComments(), "comments", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_COMMENTS,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			user.getEmailAddress(), "author-email-address", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_EMAIL_ADDRESS,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			String.valueOf(user.getUserId()), "author-id", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_ID,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			user.getJobTitle(), "author-job-title", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_JOB_TITLE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			user.getFullName(), "author-name", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_NAME,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			Time.getRFC822(_journalArticle.getCreateDate()), "create-date",
			languageId, JournalStructureConstants.RESERVED_ARTICLE_CREATE_DATE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			_journalArticle.getDescription(languageId), "description",
			languageId, JournalStructureConstants.RESERVED_ARTICLE_DESCRIPTION,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			Time.getRFC822(_journalArticle.getDisplayDate()), "display-date",
			languageId, JournalStructureConstants.RESERVED_ARTICLE_DISPLAY_DATE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			_journalArticle.getExternalReferenceCode(),
			"external-reference-code", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_EXTERNAL_REFERENCE_CODE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			_journalArticle.getArticleId(), "article-id", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_ID,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			String.valueOf(_journalArticle.getId()), "id", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_ID_,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			Time.getRFC822(_journalArticle.getModifiedDate()), "modified-date",
			languageId,
			JournalStructureConstants.RESERVED_ARTICLE_MODIFIED_DATE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			String.valueOf(_journalArticle.getResourcePrimKey()),
			"resource-prim-key", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_RESOURCE_PRIM_KEY,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			StringPool.BLANK, "small-image-url", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_SMALL_IMAGE_URL,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			_journalArticle.getTitle(languageId), "title", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_TITLE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			_journalArticle.getUrlTitle(), "url-title", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_URL_TITLE,
			journalReservedTemplateVariableGroup, transformerListeners);
		_assertReservedVariable(
			String.valueOf(_journalArticle.getVersion()), "version", languageId,
			JournalStructureConstants.RESERVED_ARTICLE_VERSION,
			journalReservedTemplateVariableGroup, transformerListeners);
	}

	@Test
	public void testLocaleTransformerListener() throws Exception {
		Assert.assertEquals(
			"Joe Bloggs",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${name.getData()}", null, Constants.VIEW));

		Assert.assertEquals(
			"João da Silva",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.BRAZIL),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${name.getData()}", null, Constants.VIEW));

		Assert.assertEquals(
			"Joe Bloggs",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.FRENCH),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${name.getData()}", null, Constants.VIEW));
	}

	@Test
	public void testLocaleTransformerListenerNestedFieldWithNoTranslation()
		throws Exception {

		Assert.assertEquals(
			"2022-11-26",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${FieldsGroup19507604.birthday.getData()}", null,
				Constants.VIEW));

		Assert.assertEquals(
			"2022-11-26",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.BRAZIL),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${FieldsGroup19507604.birthday.getData()}", null,
				Constants.VIEW));
	}

	@Test
	public void testRandomNamespaceAssetPublisherTemplate() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		String pid =
			"com.liferay.asset.publisher.web.internal.configuration." +
				"AssetPublisherSelectionStyleConfiguration";

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.internal.transformer.JournalTransformer",
				LoggerTestUtil.WARN)) {

			ConfigurationTestUtil.saveConfiguration(
				pid,
				HashMapDictionaryBuilder.<String, Object>put(
					"defaultSelectionStyle", "dynamic"
				).build());

			DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
				_group.getGroupId(), JournalArticle.class.getName());

			DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
				_group.getGroupId(), ddmStructure.getStructureId(),
				_portal.getClassNameId(JournalArticle.class),
				TemplateConstants.LANG_TYPE_FTL,
				new String(
					FileUtil.getBytes(
						getClass(),
						"dependencies" +
							"/random_namespace_asset_publisher_template.ftl")),
				LocaleUtil.US);

			JournalArticle journalArticle =
				JournalTestUtil.addArticleWithXMLContent(
					_group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
					DDMStructureTestUtil.getSampleStructuredContent(),
					ddmStructure.getStructureKey(),
					ddmTemplate.getTemplateKey(), LocaleUtil.US);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(
				_companyLocalService.getCompany(_group.getCompanyId()));

			Layout layout = LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId());

			themeDisplay.setLayout(layout);

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				_group.getGroupId(), false);

			themeDisplay.setLayoutSet(layoutSet);

			themeDisplay.setLayoutTypePortlet(
				(LayoutTypePortlet)layout.getLayoutType());
			themeDisplay.setLocale(LocaleUtil.US);
			themeDisplay.setLookAndFeel(
				_themeLocalService.getTheme(
					_group.getCompanyId(), layoutSet.getThemeId()),
				null);
			themeDisplay.setPermissionChecker(permissionChecker);
			themeDisplay.setRealUser(TestPropsValues.getUser());

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.CTX, mockHttpServletRequest.getServletContext());
			mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);
			mockHttpServletRequest.setMethod(HttpMethods.GET);
			mockHttpServletRequest.setParameter(
				"currentURL", "http://localhost:8080/currentURL");

			themeDisplay.setRequest(mockHttpServletRequest);

			themeDisplay.setResponse(new MockHttpServletResponse());
			themeDisplay.setScopeGroupId(_group.getGroupId());
			themeDisplay.setSiteGroupId(_group.getGroupId());
			themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
			themeDisplay.setUser(TestPropsValues.getUser());

			_transformMethod.invoke(
				_journalTransformer, journalArticle, ddmTemplate,
				_journalHelper, LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, ddmTemplate.getScript(), themeDisplay,
				Constants.VIEW);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Article " + journalArticle.getArticleId() +
					" cannot include itself",
				logEntry.getMessage());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			ConfigurationTestUtil.deleteConfiguration(pid);
		}
	}

	@Test
	public void testRegexTransformerListener() throws Exception {
		initRegexTransformerListener();

		Assert.assertEquals(
			"Hello Joe Bloggs, Welcome to production.sample.com.",
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false,
				"Hello ${name.getData()}, Welcome to beta.sample.com.", null,
				Constants.VIEW));
	}

	@Test
	public void testTokensTransformerListener() throws Exception {
		Assert.assertEquals(
			String.valueOf(TestPropsValues.getCompanyId()),
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "@company_id@", null, Constants.VIEW));

		Assert.assertEquals(
			String.valueOf(TestPropsValues.getCompanyId()),
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "@@company_id@@", null, Constants.VIEW));
	}

	@Test
	public void testTransformSelectDDMFormFieldType() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_read(
					"data_definition_with_select_field_single_selection.json"),
				TestPropsValues.getUser());

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				_read(
					"journal_article_content" +
						"_with_select_field_single_selection.xml"),
				dataDefinition.getDataDefinitionKey(), null);

		Assert.assertEquals(
			"Option71814087",
			_transformMethod.invoke(
				_journalTransformer, journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${Radio80408512.getData()}", null,
				Constants.VIEW));

		dataDefinition = DataDefinitionTestUtil.addDataDefinition(
			"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
			_read("data_definition_with_select_field_multiple_selection.json"),
			TestPropsValues.getUser());

		journalArticle = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			_read(
				"journal_article_content" +
					"_with_select_field_multiple_selection.xml"),
			dataDefinition.getDataDefinitionKey(), null);

		Assert.assertEquals(
			JSONUtil.putAll(
				"Option81316201", "Option25867365"
			).toString(),
			_transformMethod.invoke(
				_journalTransformer, journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "${CheckboxMultiple94681127.getData()}", null,
				Constants.VIEW));
	}

	@Test
	public void testViewCounterTransformerListener() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				"<script type=\"text/javascript\">",
				"Liferay.Service('/assetentry/increment-view-counter',",
				"{userId:0, className:'",
				"com.liferay.journal.model.JournalArticle', classPK:",
				_journalArticle.getResourcePrimKey(), "});</script>"),
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				LocaleUtil.toLanguageId(LocaleUtil.US),
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				null, false, "@view_counter@", null, Constants.VIEW));
	}

	protected void initRegexTransformerListener() {
		CacheRegistryUtil.setActive(true);

		List<Pattern> patterns = new ArrayList<>();
		List<String> replacements = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			String regex = TestPropsUtil.get(
				"journal.transformer.regex.pattern." + i);
			String replacement = TestPropsUtil.get(
				"journal.transformer.regex.replacement." + i);

			if (Validator.isNull(regex) || Validator.isNull(replacement)) {
				break;
			}

			patterns.add(Pattern.compile(regex));
			replacements.add(replacement);
		}

		ReflectionTestUtil.setFieldValue(
			_transformerListener, "_patterns", patterns);
		ReflectionTestUtil.setFieldValue(
			_transformerListener, "_replacements", replacements);
	}

	private void _assertReservedVariable(
			String expectedValue, String label, String languageId, String name,
			TemplateVariableGroup templateVariableGroup,
			List<TransformerListener> transformerListeners)
		throws Exception {

		TemplateVariableDefinition templateVariableDefinition = null;

		for (TemplateVariableDefinition
				journalReservedTemplateVariableDefinition :
					templateVariableGroup.getTemplateVariableDefinitions()) {

			if (!Objects.equals(
					journalReservedTemplateVariableDefinition.getName(),
					name)) {

				continue;
			}

			templateVariableDefinition =
				journalReservedTemplateVariableDefinition;

			break;
		}

		Assert.assertEquals(
			templateVariableDefinition.getLabel(), label,
			templateVariableDefinition.getLabel());
		Assert.assertEquals(
			expectedValue,
			_transformMethod.invoke(
				_journalTransformer, _journalArticle, null, _journalHelper,
				languageId, _layoutDisplayPageProviderRegistry,
				transformerListeners, null, false,
				"${.vars[\"" + templateVariableDefinition.getName() +
					"\"].data}",
				null, Constants.VIEW));
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _testCreateTemplateNodeDocumentLibraryDDMFormField() {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.DOCUMENT_LIBRARY);

		ddmFormField.setDataType("document_library");

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		Element dynamicContentElement = rootElement.addElement(
			"dynamic-content");

		JSONObject jsonObject = JSONUtil.put(
			"fileEntryId", RandomTestUtil.randomLong()
		).put(
			"groupId", RandomTestUtil.randomLong()
		);

		dynamicContentElement.setText(jsonObject.toString());

		TemplateNode templateNode = ReflectionTestUtil.invoke(
			_journalTransformer, "_createTemplateNode",
			new Class<?>[] {
				DDMFormField.class, Element.class, Locale.class,
				ThemeDisplay.class
			},
			ddmFormField, rootElement, LocaleUtil.getDefault(),
			new ThemeDisplay());

		Assert.assertEquals(
			jsonObject.getString("fileEntryId"),
			templateNode.getAttribute("fileEntryId"));
		Assert.assertEquals(
			jsonObject.getString("groupId"),
			templateNode.getAttribute("groupId"));
	}

	private void _testCreateTemplateNodeSelectTypeDDMFormFieldWithNoOptions() {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.SELECT);

		ddmFormField.setDataType("string");
		ddmFormField.setMultiple(true);

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addElement("dynamic-content");

		TemplateNode templateNode = ReflectionTestUtil.invoke(
			_journalTransformer, "_createTemplateNode",
			new Class<?>[] {
				DDMFormField.class, Element.class, Locale.class,
				ThemeDisplay.class
			},
			ddmFormField, rootElement, LocaleUtil.getDefault(),
			new ThemeDisplay());

		Assert.assertTrue(MapUtil.isEmpty(templateNode.getAttributes()));
		Assert.assertEquals(StringPool.BLANK, templateNode.getData());
		Assert.assertEquals("name", templateNode.getName());
		Assert.assertEquals("select", templateNode.getType());

		List<String> options = templateNode.getOptions();

		Assert.assertEquals(options.toString(), 0, options.size());

		Assert.assertTrue(MapUtil.isEmpty(templateNode.getOptionsMap()));
	}

	private void _testCreateTemplateNodeSelectTypeDDMFormFieldWithOptions() {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.SELECT);

		ddmFormField.setDataType("string");
		ddmFormField.setMultiple(true);

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		Element dynamicContentElement = rootElement.addElement(
			"dynamic-content");

		Element optionElement = dynamicContentElement.addElement("option");

		String json = JSONUtil.putAll(
			"option1", "option2"
		).toString();

		optionElement.setText(json);

		TemplateNode templateNode = ReflectionTestUtil.invoke(
			_journalTransformer, "_createTemplateNode",
			new Class<?>[] {
				DDMFormField.class, Element.class, Locale.class,
				ThemeDisplay.class
			},
			ddmFormField, rootElement, LocaleUtil.getDefault(),
			new ThemeDisplay());

		Assert.assertTrue(MapUtil.isEmpty(templateNode.getAttributes()));
		Assert.assertTrue(
			StringUtil.contains(
				templateNode.getData(), "option1", StringPool.BLANK));
		Assert.assertTrue(
			StringUtil.contains(
				templateNode.getData(), "option2", StringPool.BLANK));
		Assert.assertEquals("name", templateNode.getName());
		Assert.assertEquals("select", templateNode.getType());

		List<String> options = templateNode.getOptions();

		Assert.assertEquals(options.toString(), 1, options.size());
		Assert.assertEquals(json, options.get(0));

		Assert.assertTrue(MapUtil.isEmpty(templateNode.getOptionsMap()));
	}

	private void _testCreateTemplateNodeSelectTypeDDMFormFieldWithoutOptions() {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.SELECT);

		ddmFormField.setDataType("string");
		ddmFormField.setMultiple(true);

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addElement("dynamic-content");

		TemplateNode templateNode = ReflectionTestUtil.invoke(
			_journalTransformer, "_createTemplateNode",
			new Class<?>[] {
				DDMFormField.class, Element.class, Locale.class,
				ThemeDisplay.class
			},
			ddmFormField, rootElement, LocaleUtil.getDefault(),
			new ThemeDisplay());

		Assert.assertTrue(MapUtil.isEmpty(templateNode.getAttributes()));
		Assert.assertEquals("name", templateNode.getName());
		Assert.assertEquals(StringPool.BLANK, templateNode.getData());
		Assert.assertEquals("select", templateNode.getType());
		Assert.assertTrue(ListUtil.isEmpty(templateNode.getOptions()));
		Assert.assertTrue(MapUtil.isEmpty(templateNode.getOptionsMap()));
	}

	private void _testCreateTemplateNodeTextDDMFormField(String text) {
		DDMFormField ddmFormField = new DDMFormField(
			"text", DDMFormFieldTypeConstants.TEXT);

		ddmFormField.setDataType("text");

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		Element dynamicContentElement = rootElement.addElement(
			"dynamic-content");

		dynamicContentElement.setText(text);

		TemplateNode templateNode = ReflectionTestUtil.invoke(
			_journalTransformer, "_createTemplateNode",
			new Class<?>[] {
				DDMFormField.class, Element.class, Locale.class,
				ThemeDisplay.class
			},
			ddmFormField, rootElement, LocaleUtil.getDefault(),
			new ThemeDisplay());

		Assert.assertEquals(HtmlUtil.escape(text), templateNode.getData());
	}

	private void _testCreateTemplateNodeTextDDMFormFieldWithHTML() {
		_testCreateTemplateNodeTextDDMFormField(
			"<img src=\"x\" onerror=alert(document.cookie)>");
	}

	private void _testCreateTemplateNodeTextDDMFormFieldWithPlainText() {
		_testCreateTemplateNodeTextDDMFormField(RandomTestUtil.randomString());
	}

	private static Object _journalTransformer;
	private static ServiceTrackerList<TransformerListener> _serviceTrackerList;
	private static Method _transformMethod;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;

	@Inject
	private JournalConverter _journalConverter;

	@Inject
	private JournalHelper _journalHelper;

	@Inject
	private Language _language;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ThemeLocalService _themeLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.internal.transformer.RegexTransformerListener"
	)
	private TransformerListener _transformerListener;

}