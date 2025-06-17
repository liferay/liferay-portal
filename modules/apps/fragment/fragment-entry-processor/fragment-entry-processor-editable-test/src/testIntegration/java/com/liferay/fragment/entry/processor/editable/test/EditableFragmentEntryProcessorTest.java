/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.util.AnalyticsAttributesUtil;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class EditableFragmentEntryProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_processedHTML = _getProcessedHTML("processed_fragment_entry.html");

		_originalSiteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();

		_originalThemeDisplayDefaultLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.US);

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

		ServiceContextThreadLocal.pushServiceContext(
			new MockServiceContext(_layout, _getThemeDisplay(LocaleUtil.US)));
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setSiteDefaultLocale(_originalSiteDefaultLocale);
		LocaleThreadLocal.setThemeDisplayLocale(
			_originalThemeDisplayDefaultLocale);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testFragmentEntryProcessorEditable() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString("fragment_entry_link_editable_values.json"));

		Assert.assertEquals(
			_processedHTML,
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.getMostRelevantLocale(),
					FragmentEntryLinkConstants.EDIT)));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionDefaultText()
		throws Exception {

		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			_portal.getClassNameId(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
						RandomTestUtil.randomLong()),
			classPK, fieldId,
			"action/editable_values_action_default_text.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		TextNode textNode = (TextNode)element.childNode(0);

		Assert.assertEquals("Default Action Text", textNode.text());

		element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);

		textNode = (TextNode)element.childNode(0);

		Assert.assertEquals("Default Action Text", textNode.text());
	}

	@Test
	public void testFragmentEntryProcessorEditableActionInlineText()
		throws Exception {

		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			_portal.getClassNameId(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
						RandomTestUtil.randomLong()),
			classPK, fieldId, "action/editable_values_action_inline_text.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		TextNode textNode = (TextNode)element.childNode(0);

		Assert.assertEquals("Custom Action Text en_US", textNode.text());

		element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);

		textNode = (TextNode)element.childNode(0);

		Assert.assertEquals("Custom Action Text es_ES", textNode.text());
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionEditMode()
		throws Exception {

		String editableValues = _getEditableFieldValues(
			_portal.getClassNameId(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
						RandomTestUtil.randomLong()),
			RandomTestUtil.randomLong(),
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong(),
			"action/editable_values_action_mapped_action.json");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", editableValues,
			"action/fragment_entry_action.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.EDIT);

		Assert.assertEquals(
			StringPool.BLANK, element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			StringPool.BLANK, element.attr("data-lfr-class-pk"));
		Assert.assertEquals(
			StringPool.BLANK, element.attr("data-lfr-field-id"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnErrorNone()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_error_none.json");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", editableValues,
			"action/fragment_entry_action.html", LocaleUtil.US,
			FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"none", element.attr("data-lfr-on-error-interaction"));
		Assert.assertEquals("true", element.attr("data-lfr-on-error-reload"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnErrorNotification()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_error_" +
				"notification.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"notification", element.attr("data-lfr-on-error-interaction"));
		Assert.assertEquals("true", element.attr("data-lfr-on-error-reload"));
		Assert.assertEquals(
			"Error Text en_US", element.attr("data-lfr-on-error-text"));

		_originalThemeDisplayDefaultLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

		ServiceContextThreadLocal.pushServiceContext(
			new MockServiceContext(
				_layout, _getThemeDisplay(LocaleUtil.SPAIN)));

		try {
			element = _getElement(
				"data-lfr-editable-id", "editable_action", fragmentEntryLink,
				LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(
				_originalThemeDisplayDefaultLocale);

			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertEquals(
			"Error Text es_ES", element.attr("data-lfr-on-error-text"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnErrorPage()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_error_page.json",
			_group.getGroupId(), _layout.getLayoutId());

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", editableValues,
			"action/fragment_entry_action.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"page", element.attr("data-lfr-on-error-interaction"));
		Assert.assertEquals(
			_portal.getLayoutURL(
				_layout, _getThemeDisplay(LocaleUtil.getSiteDefault())),
			element.attr("data-lfr-on-error-page-url"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnErrorURL()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_error_url.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"url", element.attr("data-lfr-on-error-interaction"));
		Assert.assertEquals(
			"http://www.example.com",
			element.attr("data-lfr-on-error-page-url"));

		_originalThemeDisplayDefaultLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

		ServiceContextThreadLocal.pushServiceContext(
			new MockServiceContext(
				_layout, _getThemeDisplay(LocaleUtil.SPAIN)));

		try {
			element = _getElement(
				"data-lfr-editable-id", "editable_action", fragmentEntryLink,
				LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(
				_originalThemeDisplayDefaultLocale);

			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertEquals(
			"http://www.example.es",
			element.attr("data-lfr-on-error-page-url"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnSuccessNone()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_success_none.json");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", editableValues,
			"action/fragment_entry_action.html", LocaleUtil.US,
			FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"none", element.attr("data-lfr-on-success-interaction"));
		Assert.assertEquals("true", element.attr("data-lfr-on-success-reload"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnSuccessNotification()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_success_" +
				"notification.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"notification", element.attr("data-lfr-on-success-interaction"));
		Assert.assertEquals("true", element.attr("data-lfr-on-success-reload"));
		Assert.assertEquals(
			"Success Text en_US", element.attr("data-lfr-on-success-text"));

		_originalThemeDisplayDefaultLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

		ServiceContextThreadLocal.pushServiceContext(
			new MockServiceContext(
				_layout, _getThemeDisplay(LocaleUtil.SPAIN)));

		try {
			element = _getElement(
				"data-lfr-editable-id", "editable_action", fragmentEntryLink,
				LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(
				_originalThemeDisplayDefaultLocale);

			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertEquals(
			"Success Text es_ES", element.attr("data-lfr-on-success-text"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnSuccessPage()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_success_page.json",
			_group.getGroupId(), _layout.getLayoutId());

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", editableValues,
			"action/fragment_entry_action.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"page", element.attr("data-lfr-on-success-interaction"));
		Assert.assertEquals(
			_portal.getLayoutURL(
				_layout, _getThemeDisplay(LocaleUtil.getSiteDefault())),
			element.attr("data-lfr-on-success-page-url"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionOnSuccessURL()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action_on_success_url.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
		Assert.assertEquals(
			"url", element.attr("data-lfr-on-success-interaction"));
		Assert.assertEquals(
			"http://www.example.com",
			element.attr("data-lfr-on-success-page-url"));

		_originalThemeDisplayDefaultLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

		ServiceContextThreadLocal.pushServiceContext(
			new MockServiceContext(
				_layout, _getThemeDisplay(LocaleUtil.SPAIN)));

		try {
			element = _getElement(
				"data-lfr-editable-id", "editable_action", fragmentEntryLink,
				LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(
				_originalThemeDisplayDefaultLocale);

			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertEquals(
			"http://www.example.es",
			element.attr("data-lfr-on-success-page-url"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedActionViewMode()
		throws Exception {

		long classNameId = _portal.getClassNameId(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomLong());
		long classPK = RandomTestUtil.randomLong();
		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				RandomTestUtil.randomLong();

		String editableValues = _getEditableFieldValues(
			classNameId, classPK, fieldId,
			"action/editable_values_action_mapped_action.json");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", editableValues,
			"action/fragment_entry_action.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			String.valueOf(classNameId),
			element.attr("data-lfr-class-name-id"));
		Assert.assertEquals(
			String.valueOf(classPK), element.attr("data-lfr-class-pk"));
		Assert.assertEquals(fieldId, element.attr("data-lfr-field-id"));
	}

	@Test
	public void testFragmentEntryProcessorEditableActionMappedText()
		throws Exception {

		ObjectDefinition objectDefinition = _addObjectDefinition();

		Map<Locale, String> labelMap = HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			labelMap, RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		String fieldId =
			ObjectAction.class.getSimpleName() + StringPool.UNDERLINE +
				objectAction.getName();

		String editableValues = _getEditableFieldValues(
			classNameId, objectEntry.getPrimaryKey(), fieldId,
			"action/editable_values_action_mapped_text.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "action/fragment_entry_action.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		TextNode textNode = (TextNode)element.childNode(0);

		Assert.assertEquals(labelMap.get(LocaleUtil.US), textNode.text());

		element = _getElement(
			"data-lfr-editable-id", "editable_action", fragmentEntryLink,
			LocaleUtil.SPAIN, FragmentEntryLinkConstants.VIEW);

		textNode = (TextNode)element.childNode(0);

		Assert.assertEquals(labelMap.get(LocaleUtil.SPAIN), textNode.text());
	}

	@FeatureFlag("LPD-39437")
	@Test
	public void testFragmentEntryProcessorEditableAssertAnalyticsAttributesWithMappedImageInViewMode()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_image.html");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				TestPropsValues.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				StringPool.BLANK,
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"image-square",
						JSONUtil.put(
							"classNameId",
							_portal.getClassNameId(JournalArticle.class)
						).put(
							"classPK", journalArticle.getResourcePrimKey()
						).put(
							"defaultValue", "test"
						).put(
							"fieldId", "smallImage"
						))
				).toString(),
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext());

		Element element = _getElement(
			"data-lfr-editable-id", "image-square", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			AnalyticsAttributesUtil.ACTION_IMPRESSION,
			element.attr("data-analytics-asset-action"));
		Assert.assertEquals(
			"smallImage", element.attr("data-analytics-asset-field"));
		Assert.assertEquals(
			String.valueOf(journalArticle.getResourcePrimKey()),
			element.attr("data-analytics-asset-id"));
		Assert.assertEquals(
			String.valueOf(journalArticle.getDDMStructureId()),
			element.attr("data-analytics-asset-subtype"));
		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			element.attr("data-analytics-asset-title"));
		Assert.assertEquals(
			JournalArticle.class.getName(),
			element.attr("data-analytics-asset-type"));
	}

	@FeatureFlag("LPD-39437")
	@Test
	public void testFragmentEntryProcessorEditableAssertAnalyticsAttributesWithMappedTextInEditMode()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_editable_text.html");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				TestPropsValues.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				StringPool.BLANK,
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"editable_text",
						JSONUtil.put(
							"classNameId",
							_portal.getClassNameId(JournalArticle.class)
						).put(
							"classPK", journalArticle.getResourcePrimKey()
						).put(
							"defaultValue", "test"
						).put(
							"fieldId", "title"
						))
				).toString(),
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext());

		Element element = _getElement(
			"data-lfr-editable-id", "editable_text", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.EDIT);

		String attribute = element.attr("data-analytics-asset-action");

		Assert.assertTrue(attribute.isEmpty());
	}

	@FeatureFlag("LPD-39437")
	@Test
	public void testFragmentEntryProcessorEditableAssertAnalyticsAttributesWithMappedTextInViewMode()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			"fragment_entry_editable_text.html");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				TestPropsValues.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				StringPool.BLANK,
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"editable_text",
						JSONUtil.put(
							"classNameId",
							_portal.getClassNameId(JournalArticle.class)
						).put(
							"classPK", journalArticle.getResourcePrimKey()
						).put(
							"defaultValue", "test"
						).put(
							"fieldId", "title"
						))
				).toString(),
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext());

		Element element = _getElement(
			"data-lfr-editable-id", "editable_text", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(
			AnalyticsAttributesUtil.ACTION_IMPRESSION,
			element.attr("data-analytics-asset-action"));
		Assert.assertEquals(
			"title", element.attr("data-analytics-asset-field"));
		Assert.assertEquals(
			String.valueOf(journalArticle.getResourcePrimKey()),
			element.attr("data-analytics-asset-id"));
		Assert.assertEquals(
			String.valueOf(journalArticle.getDDMStructureId()),
			element.attr("data-analytics-asset-subtype"));
		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			element.attr("data-analytics-asset-title"));
		Assert.assertEquals(
			JournalArticle.class.getName(),
			element.attr("data-analytics-asset-type"));
	}

	@Test
	@TestInfo("LPD-34747")
	public void testFragmentEntryProcessorEditableLinkInlineValueEditMode()
		throws Exception {

		_testFragmentEntryProcessorEditableLinkInlineValue(
			"#", FragmentEntryLinkConstants.EDIT);
	}

	@Test
	@TestInfo("LPD-34747")
	public void testFragmentEntryProcessorEditableLinkInlineValueViewMode()
		throws Exception {

		_testFragmentEntryProcessorEditableLinkInlineValue(
			"https://www.liferay.com", FragmentEntryLinkConstants.VIEW);
	}

	@Test
	public void testFragmentEntryProcessorEditableLinkWithNestedEditablesInHtml()
		throws Exception {

		_addFragmentEntry(
			"fragment_entry_with_editable_link_with_nested_editable_in_html." +
				"html");
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedAssetField()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				TestPropsValues.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String editableValues = _readJSONFileToString(
			"fragment_entry_link_mapped_asset_field.json");

		editableValues = StringUtil.replace(
			editableValues, new String[] {"CLASS_NAME_ID", "CLASS_PK"},
			new String[] {
				String.valueOf(_portal.getClassNameId(JournalArticle.class)),
				String.valueOf(journalArticle.getResourcePrimKey())
			});

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(), editableValues);

		int count =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(
					_portal.getClassNameId(JournalArticle.class),
					journalArticle.getResourcePrimKey());

		Assert.assertEquals(1, count);

		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);

		count =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(
					_portal.getClassNameId(JournalArticle.class),
					journalArticle.getResourcePrimKey());

		Assert.assertEquals(0, count);
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedDLImageBackgroundImageFileEntryId()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry();

		String editableValues = _getEditableFieldValues(
			_portal.getClassNameId(FileEntry.class), fileEntry.getFileEntryId(),
			"fileURL",
			"fragment_entry_link_mapped_asset_field_background_image.json");

		Element element = _getElement(
			"data-lfr-background-image-id", "background-image", editableValues,
			"fragment_entry_background_image.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.EDIT);

		String style = element.attr("style");

		Assert.assertTrue(
			style.contains(
				"--background-image-file-entry-id: " +
					fileEntry.getFileEntryId() + ";"));
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedDLImageFileEntryId()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry();

		String editableValues = _getEditableFieldValues(
			_portal.getClassNameId(FileEntry.class), fileEntry.getFileEntryId(),
			"fileURL", "fragment_entry_link_mapped_asset_field_image.json");

		Element element = _getElement(
			"data-lfr-editable-id", "image-square", editableValues,
			"fragment_entry_image.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.EDIT);

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(element.attr("data-fileentryid")));
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedDLPreview()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry();

		String editableValues = _getEditableFieldValues(
			_portal.getClassNameId(FileEntry.class), fileEntry.getFileEntryId(),
			"fileURL", "fragment_entry_link_mapped_asset_field_image.json");

		Element element = _getElement(
			"data-lfr-editable-id", "image-square", editableValues,
			"fragment_entry_image.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.EDIT);

		String src = element.attr("src");

		Assert.assertFalse(src.contains("imagePreview=1"));
		Assert.assertEquals(
			_dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK),
			src);
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedJournalArticleBackgroundImageFileEntryId()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry();

		String editableValues = _getJournalArticleEditableFieldValues(
			"fragment_entry_link_mapped_asset_field_background_image.json",
			"fileURL", fileEntry);

		Element element = _getElement(
			"data-lfr-background-image-id", "background-image", editableValues,
			"fragment_entry_background_image.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.EDIT);

		String style = element.attr("style");

		Assert.assertTrue(
			style.contains(
				"--background-image-file-entry-id: " +
					fileEntry.getFileEntryId() + ";"));
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedJournalArticleImageFileEntryId()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry();

		String editableValues = _getJournalArticleEditableFieldValues(
			"fragment_entry_link_mapped_asset_field_image.json",
			"ImageFieldName", fileEntry);

		Element element = _getElement(
			"data-lfr-editable-id", "image-square", editableValues,
			"fragment_entry_image.html", LocaleUtil.getSiteDefault(),
			FragmentEntryLinkConstants.EDIT);

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(element.attr("data-fileentryid")));
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithDuplicateIds()
		throws Exception {

		_addFragmentEntry("fragment_entry_with_duplicate_editable_ids.html");
	}

	@Test
	public void testFragmentEntryProcessorEditableWithEmptyString()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_empty_string.json"));

		Assert.assertEquals(
			_getProcessedHTML("processed_fragment_entry_empty_string.html"),
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.US, FragmentEntryLinkConstants.EDIT)));
	}

	@Test
	@TestInfo("LPS-124056")
	public void testFragmentEntryProcessorEditableWithImageLazyLoading()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_without_image_lazy_" +
					"loading.json"));

		String content =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.US, FragmentEntryLinkConstants.EDIT));

		Assert.assertFalse(content.contains("loading=\"lazy\""));

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_with_image_lazy_loading." +
					"json"));

		content = _fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
			fragmentEntryLink,
			_getFragmentEntryProcessorContext(
				LocaleUtil.US, FragmentEntryLinkConstants.EDIT));

		Assert.assertTrue(content.contains("loading=\"lazy\""));
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithInvalidTypeAttribute()
		throws Exception {

		_addFragmentEntry(
			"fragment_entry_with_invalid_editable_type_attribute.html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithInvalidTypeAttributeInImageTag()
		throws Exception {

		_addFragmentEntry(
			"fragment_entry_with_invalid_editable_type_attribute_in_image_" +
				"tag.html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithInvalidTypeAttributeInLinkTag()
		throws Exception {

		_addFragmentEntry(
			"fragment_entry_with_invalid_editable_type_attribute_in_link_tag." +
				"html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithInvalidTypeAttributeInTextTag()
		throws Exception {

		_addFragmentEntry(
			"fragment_entry_with_invalid_editable_type_attribute_in_text_tag." +
				"html");
	}

	@Test
	public void testFragmentEntryProcessorEditableWithLocalizableImageAlt()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_localizable_image_alt." +
					"json"));

		Assert.assertThat(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.US, FragmentEntryLinkConstants.EDIT)),
			CoreMatchers.containsString("en_US-alt"));

		Locale currentLocale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(
			LocaleUtil.fromLanguageId("es_ES"));

		Assert.assertThat(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.fromLanguageId("es_ES"),
					FragmentEntryLinkConstants.EDIT)),
			CoreMatchers.containsString("es_ES-alt"));

		LocaleThreadLocal.setThemeDisplayLocale(currentLocale);
	}

	@Test
	public void testFragmentEntryProcessorEditableWithMatchedLanguage()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_matching_language.json"));

		Assert.assertEquals(
			_processedHTML,
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.US, FragmentEntryLinkConstants.EDIT)));
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithMissingAttributes()
		throws Exception {

		_addFragmentEntry(
			"fragment_entry_with_missing_editable_attributes.html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithNestedDropZonesInHTML()
		throws Exception {

		_addFragmentEntry("fragment_entry_with_nested_drop_zones_in_html.html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithNestedEditablesInHTML1()
		throws Exception {

		_addFragmentEntry("fragment_entry_with_nested_editable_in_html_1.html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithNestedEditablesInHTML2()
		throws Exception {

		_addFragmentEntry("fragment_entry_with_nested_editable_in_html_2.html");
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testFragmentEntryProcessorEditableWithNestedWidgetsInHTML()
		throws Exception {

		_addFragmentEntry("fragment_entry_with_nested_widgets_in_html.html");
	}

	@Test
	public void testFragmentEntryProcessorEditableWithUnmatchedLanguage()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FragmentEntry fragmentEntry = _addFragmentEntry("fragment_entry.html");

		fragmentEntryLink.setHtml(fragmentEntry.getHtml());

		fragmentEntryLink.setEditableValues(
			_readJSONFileToString(
				"fragment_entry_link_editable_values_unmatching_language." +
					"json"));

		Assert.assertEquals(
			_processedHTML,
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.CHINESE, FragmentEntryLinkConstants.EDIT)));
	}

	@Test
	public void testGetEmptyStringFromEmptyFieldValue() throws Exception {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.TEXT);

		ddmFormField.setDataType("text");

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter, StringPool.BLANK,
			_group.getGroupId(), _journalConverter);

		String editableValues = _readJSONFileToString(
			"fragment_entry_link_mapped_empty_field_value.json");

		editableValues = StringUtil.replace(
			editableValues,
			new String[] {"CLASS_NAME_ID", "CLASS_PK", "FIELD_ID"},
			new String[] {
				String.valueOf(_portal.getClassNameId(JournalArticle.class)),
				String.valueOf(journalArticle.getResourcePrimKey()),
				"DDMStructure_" + ddmFormField.getName()
			});

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "fragment_entry_editable_text.html");

		Element element = _getElement(
			"data-lfr-editable-id", "editable_text", fragmentEntryLink,
			LocaleUtil.US, FragmentEntryLinkConstants.VIEW);

		Assert.assertEquals(StringPool.BLANK, element.text());
	}

	private DDMStructure _addDDMStructure(Group group, String content)
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				_portal.getClassNameId(JournalArticle.class), group);

		return ddmStructureTestHelper.addStructure(
			_portal.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(content), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);
	}

	private FragmentEntry _addFragmentEntry(String htmlFile) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), "Fragment Collection",
				StringPool.BLANK, serviceContext);

		return _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, _readFileToString(htmlFile), null, false,
			null, null, 0, false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String editableValues, String htmlFileName)
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(htmlFileName);

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			fragmentEntry.getFragmentEntryId(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			TestPropsValues.getPlid(), fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(), StringPool.BLANK,
			editableValues, StringPool.BLANK, 0, null, fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private FileEntry _addImageFileEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = FileUtil.getBytes(
			EditableFragmentEntryProcessorTest.class,
			"/com/liferay/fragment/entry/processor/editable/test/dependencies" +
				"/image.jpg");

		InputStream inputStream = new UnsyncByteArrayInputStream(bytes);

		LocalRepository localRepository =
			RepositoryProviderUtil.getLocalRepository(_group.getGroupId());

		return localRepository.addFileEntry(
			null, TestPropsValues.getUserId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, inputStream, bytes.length, null,
			null, null, serviceContext);
	}

	private JournalArticle _addJournalArticle(
			DDMStructure ddmStructure, String fieldId, FileEntry fileEntry)
		throws Exception {

		User user = TestPropsValues.getUser();

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		return _journalArticleLocalService.addArticle(
			null, user.getUserId(), _group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT,
			HashMapBuilder.put(
				defaultLocale, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				defaultLocale, defaultLocale.toString()
			).build(),
			HashMapBuilder.put(
				defaultLocale, RandomTestUtil.randomString()
			).build(),
			StringUtil.replace(
				_readFileToString("dynamic_content.xml"),
				new String[] {"[$FIELD_ID$]", "[$IMAGE_JSON$]"},
				new String[] {fieldId, _toJSON(fileEntry)}),
			ddmStructure.getStructureId(), ddmTemplate.getTemplateKey(), null,
			displayCalendar.get(Calendar.MONTH),
			displayCalendar.get(Calendar.DATE),
			displayCalendar.get(Calendar.YEAR),
			displayCalendar.get(Calendar.HOUR_OF_DAY),
			displayCalendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0, 0, 0,
			0, 0, true, true, false, 0, 0, null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null,
				"control_panel.sites",
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), null);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private Document _getDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);

		document.outputSettings(outputSettings);

		return document;
	}

	private String _getEditableFieldValues(
			long classNameId, long classPK, String fieldId, String fileName)
		throws Exception {

		String editableValues = _readJSONFileToString(fileName);

		return StringUtil.replace(
			editableValues,
			new String[] {"CLASS_NAME_ID", "CLASS_PK", "FIELD_ID"},
			new String[] {
				String.valueOf(classNameId), String.valueOf(classPK), fieldId
			});
	}

	private String _getEditableFieldValues(
			long classNameId, long classPK, String fieldId, String fileName,
			long groupId, long layoutId)
		throws Exception {

		String editableValues = _readJSONFileToString(fileName);

		return StringUtil.replace(
			editableValues,
			new String[] {
				"CLASS_NAME_ID", "CLASS_PK", "FIELD_ID", "GROUP_ID", "LAYOUT_ID"
			},
			new String[] {
				String.valueOf(classNameId), String.valueOf(classPK), fieldId,
				String.valueOf(groupId), String.valueOf(layoutId)
			});
	}

	private Element _getElement(
			String dataAttributeName, String editableId,
			FragmentEntryLink fragmentEntryLink, Locale locale, String mode)
		throws Exception {

		String processedFragmentEntryLinkHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(locale, mode));

		Document document = _getDocument(processedFragmentEntryLinkHTML);

		Element body = document.body();

		Elements elements = body.getElementsByAttributeValue(
			dataAttributeName, editableId);

		return elements.get(0);
	}

	private Element _getElement(
			String dataAttributeName, String editableId, String editableValues,
			String htmlFileName, Locale locale, String mode)
		throws Exception {

		return _getElement(
			dataAttributeName, editableId,
			_addFragmentEntryLink(editableValues, htmlFileName), locale, mode);
	}

	private FragmentEntryProcessorContext _getFragmentEntryProcessorContext(
			Locale locale, String mode)
		throws Exception {

		return new DefaultFragmentEntryProcessorContext(
			_getHttpServletRequest(locale), null, mode, locale);
	}

	private HttpServletRequest _getHttpServletRequest(Locale locale)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private String _getJournalArticleEditableFieldValues(
			String editableValuesFileName, String fieldId, FileEntry fileEntry)
		throws Exception {

		String ddmStructureContent = _readJSONFileToString(
			"ddm_structure.json");

		ddmStructureContent = StringUtil.replace(
			ddmStructureContent, "FIELD_NAME", fieldId);

		DDMStructure ddmStructure = _addDDMStructure(
			_group, ddmStructureContent);

		JournalArticle journalArticle = _addJournalArticle(
			ddmStructure, fieldId, fileEntry);

		return _getEditableFieldValues(
			_portal.getClassNameId(JournalArticle.class),
			journalArticle.getResourcePrimKey(), fieldId,
			editableValuesFileName);
	}

	private String _getProcessedHTML(String fileName) throws Exception {
		Document document = Jsoup.parseBodyFragment(
			_readFileToString(fileName));

		document.outputSettings(
			new Document.OutputSettings() {
				{
					prettyPrint(false);
				}
			});

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private ThemeDisplay _getThemeDisplay(Locale locale) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLocale(locale);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLookAndFeel(
			_themeLocalService.getTheme(
				_company.getCompanyId(), layoutSet.getThemeId()),
			null);
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(_getHttpServletRequest(locale));
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _readFileToString(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/fragment/entry/processor/editable/test/dependencies/" +
				fileName);
	}

	private String _readJSONFileToString(String jsonFileName) throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_readFileToString(jsonFileName));

		return jsonObject.toString();
	}

	private void _testFragmentEntryProcessorEditableLinkInlineValue(
			String expectedHref, String mode)
		throws Exception {

		String editableValues = _readJSONFileToString(
			"link/editable_values_link_inline.json");

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValues, "link/fragment_entry_link.html");

		String processedFragmentEntryLinkHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				_getFragmentEntryProcessorContext(
					LocaleUtil.getSiteDefault(), mode));

		Document document = _getDocument(processedFragmentEntryLinkHTML);

		Element body = document.body();

		Elements elements = body.getElementsByTag("a");

		Element element = elements.get(0);

		Assert.assertEquals(expectedHref, element.attr("href"));
		Assert.assertEquals(
			"Default Editable Values Link Text", element.text());
	}

	private String _toJSON(FileEntry fileEntry) {
		return JSONUtil.put(
			"alt", StringPool.BLANK
		).put(
			"description", StringPool.BLANK
		).put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getFileName()
		).put(
			"title", fileEntry.getTitle()
		).put(
			"type", "journal"
		).put(
			"uuid", fileEntry.getUuid()
		).toString();
	}

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalConverter _journalConverter;

	private Layout _layout;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private Locale _originalSiteDefaultLocale;
	private Locale _originalThemeDisplayDefaultLocale;

	@Inject
	private Portal _portal;

	private String _processedHTML;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private ThemeLocalService _themeLocalService;

	private static class MockServiceContext extends ServiceContext {

		public MockServiceContext(Layout layout, ThemeDisplay themeDisplay) {
			_layout = layout;
			_themeDisplay = themeDisplay;
		}

		@Override
		public HttpServletRequest getRequest() {
			HttpServletRequest httpServletRequest =
				new MockHttpServletRequest();

			httpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _themeDisplay);

			return httpServletRequest;
		}

		@Override
		public HttpServletResponse getResponse() {
			return new MockHttpServletResponse();
		}

		@Override
		public ThemeDisplay getThemeDisplay() {
			return _themeDisplay;
		}

		private final Layout _layout;
		private final ThemeDisplay _themeDisplay;

	}

}