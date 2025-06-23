/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateExportImportConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class ExportImportLayoutPageTemplateEntriesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(getClass());

		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group1.getCompanyId());

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			}
		};
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryCollectionDisplayBeforePaginationImprovements()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				AssetListEntry assetListEntry = _addAssetListEntry(
					_group1.getGroupId());

				return String.valueOf(assetListEntry.getAssetListEntryId());
			}
		).build();

		File expectedFile = _generateZipFile(
			"collection_display/before_pagination_improvements/expected",
			numberValuesMap, null);
		File inputFile = _generateZipFile(
			"collection_display/before_pagination_improvements/input",
			numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryCollectionDisplayComplete()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				AssetListEntry assetListEntry = _addAssetListEntry(
					_group1.getGroupId());

				return String.valueOf(assetListEntry.getAssetListEntryId());
			}
		).build();

		File expectedFile = _generateZipFile(
			"collection_display/complete/expected", numberValuesMap, null);
		File inputFile = _generateZipFile(
			"collection_display/complete/input", numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryCollectionDisplayDefault()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				AssetListEntry assetListEntry = _addAssetListEntry(
					_group1.getGroupId());

				return String.valueOf(assetListEntry.getAssetListEntryId());
			}
		).build();

		File expectedFile = _generateZipFile(
			"collection_display/default/expected", numberValuesMap, null);
		File inputFile = _generateZipFile(
			"collection_display/default/input", numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryCollectionDisplayName()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				AssetListEntry assetListEntry = _addAssetListEntry(
					_group1.getGroupId());

				return String.valueOf(assetListEntry.getAssetListEntryId());
			}
		).build();

		File expectedFile = _generateZipFile(
			"collection_display/name/expected", numberValuesMap, null);
		File inputFile = _generateZipFile(
			"collection_display/name/input", numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerBackgroundFragmentImage()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();

		File expectedFile = _generateZipFile(
			"container/background_fragment_image/expected", numberValuesMap,
			null);
		File inputFile = _generateZipFile(
			"container/background_fragment_image/input", numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerBackgroundImage()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();

		File expectedFile = _generateZipFile(
			"container/background_image/expected", numberValuesMap, null);
		File inputFile = _generateZipFile(
			"container/background_image/input", numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerComplete()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/complete/expected", null, null);
		File inputFile = _generateZipFile(
			"container/complete/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerContentVisibility()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/content_visibility/expected", null, null);
		File inputFile = _generateZipFile(
			"container/content_visibility/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerCssClasses()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"container/css_classes/expected", null, null);
		File inputFile = _generateZipFile(
			"container/css_classes/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerCustomCSS()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"container/custom_css/expected", null, null);
		File inputFile = _generateZipFile(
			"container/custom_css/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerDefault()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/default/expected", null, null);
		File inputFile = _generateZipFile(
			"container/default/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerEmpty()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/empty/expected", null, null);
		File inputFile = _generateZipFile("container/empty/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerLayout()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/layout/expected", null, null);
		File inputFile = _generateZipFile("container/layout/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerLink()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/link/expected", null, null);
		File inputFile = _generateZipFile("container/link/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerLinkMappedToJournalArticleDisplayPageURL()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle(
			_group1.getGroupId());

		_addDisplayPageTemplate(journalArticle);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(journalArticle.getResourcePrimKey())
		).put(
			"DISPLAY_PAGE_URL",
			StringBundler.concat(
				"\"http://localhost:8080/web", _group1.getFriendlyURL(),
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
				journalArticle.getUrlTitle(), "\"")
		).build();

		File expectedFile = _generateZipFile(
			"container/link_mapped_journal_article_display_page_url/expected",
			numberValuesMap, null);
		File inputFile = _generateZipFile(
			"container/link_mapped_journal_article_display_page_url/input",
			numberValuesMap, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerLinkMappedToLayoutWithFriendlyURL()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"FRIENDLY_URL", layout.getFriendlyURL()
		).put(
			"SITE_KEY", String.valueOf(_group1.getGroupKey())
		).build();

		File expectedFile = _generateZipFile(
			"container/link_mapped_layout/friendly_url/expected", null,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"container/link_mapped_layout/friendly_url/input", null,
			stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerLinkMappedToLayoutWithFriendlyURLSiteKeyDifferentGroup()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"FRIENDLY_URL", layout.getFriendlyURL()
		).put(
			"SITE_KEY", String.valueOf(_group1.getGroupKey())
		).build();

		File expectedFile = _generateZipFile(
			"container/link_mapped_layout/friendly_url_site_key/expected", null,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"container/link_mapped_layout/friendly_url_site_key/input", null,
			stringValuesMap);

		_validateImportExport(
			expectedFile, inputFile, _group1.getGroupId(),
			_group2.getGroupId());
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerLinkMappedToLayoutWithPlid()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"FRIENDLY_URL", layout.getFriendlyURL()
		).put(
			"PLID", String.valueOf(layout.getPlid())
		).put(
			"SITE_KEY", String.valueOf(_group1.getGroupKey())
		).build();

		File expectedFile = _generateZipFile(
			"container/link_mapped_layout/plid/expected", null,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"container/link_mapped_layout/plid/input", null, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryContainerName()
		throws Exception {

		File expectedFile = _generateZipFile(
			"container/name/expected", null, null);
		File inputFile = _generateZipFile("container/name/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFormContainerName()
		throws Exception {

		File expectedFile = _generateZipFile("form/name/expected", null, null);
		File inputFile = _generateZipFile("form/name/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFormContainerWithEmbeddedSuccessMessage()
		throws Exception {

		File expectedFile = _generateZipFile(
			"form/success_message_embedded/expected", null, null);
		File inputFile = _generateZipFile(
			"form/success_message_embedded/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFormContainerWithLayoutSuccessMessage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"FRIENDLY_URL", layout.getFriendlyURL()
		).put(
			"SITE_KEY", String.valueOf(_group1.getGroupKey())
		).build();

		File expectedFile = _generateZipFile(
			"form/success_message_layout/expected", null, stringValuesMap);
		File inputFile = _generateZipFile(
			"form/success_message_layout/input", null, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFormContainerWithNoneSuccessMessage()
		throws Exception {

		File expectedFile = _generateZipFile(
			"form/success_message_none/expected", null, null);
		File inputFile = _generateZipFile(
			"form/success_message_none/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFormContainerWithURLSuccessMessage()
		throws Exception {

		File expectedFile = _generateZipFile(
			"form/success_message_url/expected", null, null);
		File inputFile = _generateZipFile(
			"form/success_message_url/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFormSteps()
		throws Exception {

		File expectedFile = _generateZipFile("form/steps/expected", null, null);
		File inputFile = _generateZipFile("form/steps/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentActionFieldDisplayPage()
		throws Exception {

		_addActionFragmentEntry();

		ObjectEntry objectEntry = _addObjectEntry();

		ObjectAction objectAction = _addObjectAction(objectEntry);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(objectEntry.getObjectEntryId())
		).build();

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"ACTION_NAME",
			ObjectAction.class.getSimpleName() + StringPool.DASH +
				objectAction.getName()
		).put(
			"CLASS_NAME", objectEntry.getModelClassName()
		).put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/action_field/display_page/expected", numberValuesMap,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/action_field/display_page/input", numberValuesMap,
			stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentActionFieldExternalURL()
		throws Exception {

		_addActionFragmentEntry();

		ObjectEntry objectEntry = _addObjectEntry();

		ObjectAction objectAction = _addObjectAction(objectEntry);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(objectEntry.getObjectEntryId())
		).build();

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"ACTION_NAME",
			ObjectAction.class.getSimpleName() + StringPool.DASH +
				objectAction.getName()
		).put(
			"CLASS_NAME", objectEntry.getModelClassName()
		).put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/action_field/external_url/expected", numberValuesMap,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/action_field/external_url/input", numberValuesMap,
			stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentActionFieldMappedAction()
		throws Exception {

		_addActionFragmentEntry();

		ObjectEntry objectEntry = _addObjectEntry();

		ObjectAction objectAction = _addObjectAction(objectEntry);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(objectEntry.getObjectEntryId())
		).build();

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"ACTION_NAME",
			ObjectAction.class.getSimpleName() + StringPool.DASH +
				objectAction.getName()
		).put(
			"CLASS_NAME", objectEntry.getModelClassName()
		).put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/action_field/mapped_value/expected", numberValuesMap,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/action_field/mapped_value/input", numberValuesMap,
			stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentActionFieldNotification()
		throws Exception {

		_addActionFragmentEntry();

		ObjectEntry objectEntry = _addObjectEntry();

		ObjectAction objectAction = _addObjectAction(objectEntry);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(objectEntry.getObjectEntryId())
		).build();

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"ACTION_NAME",
			ObjectAction.class.getSimpleName() + StringPool.DASH +
				objectAction.getName()
		).put(
			"CLASS_NAME", objectEntry.getModelClassName()
		).put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/action_field/notification/expected", numberValuesMap,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/action_field/notification/input", numberValuesMap,
			stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentActionFieldPage()
		throws Exception {

		_addActionFragmentEntry();

		ObjectEntry objectEntry = _addObjectEntry();

		ObjectAction objectAction = _addObjectAction(objectEntry);

		Layout successLayout = LayoutTestUtil.addTypeContentLayout(_group1);

		Layout errorLayout = LayoutTestUtil.addTypeContentLayout(_group1);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(objectEntry.getObjectEntryId())
		).build();

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"ACTION_NAME",
			ObjectAction.class.getSimpleName() + StringPool.DASH +
				objectAction.getName()
		).put(
			"CLASS_NAME", objectEntry.getModelClassName()
		).put(
			"ERROR_LAYOUT_FRIENDLY_URL", errorLayout.getFriendlyURL()
		).put(
			"SITE_KEY", _group1.getGroupKey()
		).put(
			"SUCCESS_LAYOUT_FRIENDLY_URL", successLayout.getFriendlyURL()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/action_field/page/expected", numberValuesMap,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/action_field/page/input", numberValuesMap,
			stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentCssClasses()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/css_classes/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());
		File inputFile = _generateZipFile(
			"fragment/css_classes/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentCustomCSS()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/custom_css/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());
		File inputFile = _generateZipFile(
			"fragment/custom_css/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentHidden()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/hidden/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());
		File inputFile = _generateZipFile("fragment/hidden/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentImageFieldImageMappedAndLinkMapped()
		throws Exception {

		_addImageFragmentEntry();

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/image_field/image_mapped_and_link_mapped/expected",
			numberValuesMap, stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/image_field/image_mapped_and_link_mapped/input",
			numberValuesMap, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentImageFieldImageMappedAndLinkPage()
		throws Exception {

		_addImageFragmentEntry();

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"FRIENDLY_URL",
			() -> {
				Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

				return layout.getFriendlyURL();
			}
		).put(
			"SITE_KEY", String.valueOf(_group1.getGroupKey())
		).build();

		File expectedFile = _generateZipFile(
			"fragment/image_field/image_mapped_and_link_page/expected",
			numberValuesMap, stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/image_field/image_mapped_and_link_page/input",
			numberValuesMap, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentImageFieldImageURLAndLinkURL()
		throws Exception {

		_addImageFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/image_field/image_url_and_link_url/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());

		File inputFile = _generateZipFile(
			"fragment/image_field/image_url_and_link_url/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentName()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/name/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());

		File inputFile = _generateZipFile("fragment/name/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentResponsiveStyles()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/responsive/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());

		File inputFile = _generateZipFile(
			"fragment/responsive/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentAvailableMappedContentAvailable()
		throws Exception {

		_addTextFragmentEntry();

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			numberValuesMap, stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentAvailableMappedContentAvailableOverwriteFalsePageTemplateEntryDoesNotExist()
		throws Exception {

		_addTextFragmentEntry();

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			numberValuesMap, stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentAvailableMappedContentAvailableOverwriteFalsePageTemplateEntryExists()
		throws Exception {

		_addTextFragmentEntry();

		Map<String, String> numberValuesMap1 = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			numberValuesMap1, stringValuesMap);

		File inputFile1 = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap1, stringValuesMap);

		_getImportLayoutPageTemplateEntry(
			inputFile1, _group1.getGroupId(),
			LayoutsImporterResultEntry.Status.IMPORTED,
			LayoutsImportStrategy.DO_NOT_OVERWRITE);

		File inputFile2 = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			HashMapBuilder.put(
				"CLASS_PK",
				() -> {
					JournalArticle journalArticle = _addJournalArticle(
						_group1.getGroupId());

					return String.valueOf(journalArticle.getResourcePrimKey());
				}
			).build(),
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());

		File outputFile = _importExportLayoutPageTemplateEntry(
			inputFile2, _group1.getGroupId(),
			LayoutsImporterResultEntry.Status.IGNORED,
			LayoutsImportStrategy.DO_NOT_OVERWRITE);

		_validateFile(expectedFile, outputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentAvailableMappedContentAvailableOverwriteTruePageTemplateEntryDoesNotExist()
		throws Exception {

		_addTextFragmentEntry();

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			numberValuesMap, stringValuesMap);

		File inputFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap, stringValuesMap);

		File outputFile = _importExportLayoutPageTemplateEntry(
			inputFile, _group1.getGroupId(),
			LayoutsImporterResultEntry.Status.IMPORTED,
			LayoutsImportStrategy.OVERWRITE);

		_validateFile(expectedFile, outputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentAvailableMappedContentAvailableOverwriteTruePageTemplateEntryExists()
		throws Exception {

		_addTextFragmentEntry();

		File inputFile1 = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			HashMapBuilder.put(
				"CLASS_PK",
				() -> {
					JournalArticle journalArticle = _addJournalArticle(
						_group1.getGroupId());

					return String.valueOf(journalArticle.getResourcePrimKey());
				}
			).build(),
			null);

		_getImportLayoutPageTemplateEntry(
			inputFile1, _group1.getGroupId(),
			LayoutsImporterResultEntry.Status.IMPORTED,
			LayoutsImportStrategy.DO_NOT_OVERWRITE);

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			numberValuesMap, stringValuesMap);

		File inputFile2 = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap, stringValuesMap);

		File outputFile = _importExportLayoutPageTemplateEntry(
			inputFile2, _group1.getGroupId(),
			LayoutsImporterResultEntry.Status.IMPORTED,
			LayoutsImportStrategy.OVERWRITE);

		_validateFile(expectedFile, outputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentAvailableMappedContentNotAvailable()
		throws Exception {

		_addTextFragmentEntry();

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK", String.valueOf(RandomTestUtil.randomLong())
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_available",
			numberValuesMap, stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryFragmentTextFieldFragmentNotAvailable()
		throws Exception {

		Map<String, String> numberValuesMap = HashMapBuilder.put(
			"CLASS_PK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_group1.getGroupId());

				return String.valueOf(journalArticle.getResourcePrimKey());
			}
		).build();
		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"SITE_KEY", _group1.getGroupKey()
		).build();

		File expectedFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/expected" +
				"/fragment_not_available",
			numberValuesMap, stringValuesMap);
		File inputFile = _generateZipFile(
			"fragment/text_field/mapped_value/class_pk_reference/input",
			numberValuesMap, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryLocalizationConfig()
		throws Exception {

		File expectedFile = _generateZipFile(
			"form/localization_config/expected", null, null);
		File inputFile = _generateZipFile(
			"form/localization_config/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryRowContainer()
		throws Exception {

		File expectedFile = _generateZipFile(
			"row/container/expected", null, null);
		File inputFile = _generateZipFile("row/container/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryRowCssClasses()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"container/css_classes/expected", null, null);
		File inputFile = _generateZipFile(
			"container/css_classes/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryRowCustomCSS()
		throws Exception {

		File expectedFile = _generateZipFile(
			"row/custom_css/expected", null, null);
		File inputFile = _generateZipFile("row/custom_css/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryRowName()
		throws Exception {

		File expectedFile = _generateZipFile("row/name/expected", null, null);
		File inputFile = _generateZipFile("row/name/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryRules()
		throws Exception {

		_addTextFragmentEntry();

		File expectedFile = _generateZipFile(
			"fragment/rules/expected", null,
			HashMapBuilder.put(
				"SITE_KEY", _group1.getGroupKey()
			).build());

		File inputFile = _generateZipFile("fragment/rules/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryThemeSpritemapClientExtension()
		throws Exception {

		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"url", "http://" + RandomTestUtil.randomString() + ".com"
				).buildString());

		Map<String, String> stringValuesMap = HashMapBuilder.put(
			"EXTERNAL_REFERENCE_CODE",
			clientExtensionEntry.getExternalReferenceCode()
		).put(
			"NAME", clientExtensionEntry.getName(LocaleUtil.getDefault())
		).build();

		File expectedFile = _generateZipFile(
			"client_extensions/theme_spritemap/expected", null,
			stringValuesMap);
		File inputFile = _generateZipFile(
			"client_extensions/theme_spritemap/input", null, stringValuesMap);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryWidgetCssClasses()
		throws Exception {

		File expectedFile = _generateZipFile(
			"widget/css_classes/expected", null, null);
		File inputFile = _generateZipFile(
			"widget/css_classes/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryWidgetCustomCSS()
		throws Exception {

		File expectedFile = _generateZipFile(
			"widget/custom_css/expected", null, null);
		File inputFile = _generateZipFile(
			"widget/custom_css/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryWidgetHidden()
		throws Exception {

		File expectedFile = _generateZipFile(
			"widget/hidden/expected", null, null);
		File inputFile = _generateZipFile("widget/hidden/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	@Test
	public void testImportExportLayoutPageTemplateEntryWidgetName()
		throws Exception {

		File expectedFile = _generateZipFile(
			"widget/name/expected", null, null);
		File inputFile = _generateZipFile("widget/name/input", null, null);

		_validateImportExport(expectedFile, inputFile);
	}

	private void _addActionFragmentEntry() throws Exception {
		String html =
			"<button data-lfr-editable-id=\"action\" data-lfr-editable-type" +
				"=\"action\">Action Button Fragment</button>";

		_addFragmentEntry(
			_group1.getGroupId(), "test-action-fragment",
			"Test Action Fragment", html);
	}

	private AssetListEntry _addAssetListEntry(long groupId)
		throws PortalException {

		return _assetListEntryLocalService.addAssetListEntry(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_MANUAL,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private void _addDisplayPageTemplate(JournalArticle journalArticle)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group1.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			ServiceContextTestUtil.getServiceContext(_group1.getGroupId()));

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), true);
	}

	private FragmentEntry _addFragmentEntry(
			long groupId, String key, String name, String html)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), groupId, "Test Collection",
				StringPool.BLANK, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), groupId,
			fragmentCollection.getFragmentCollectionId(), key, name,
			StringPool.BLANK, html, StringPool.BLANK, false, StringPool.BLANK,
			null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _addImageFragmentEntry() throws Exception {
		String html =
			"<img data-lfr-editable-id=\"image-id\" " +
				"data-lfr-editable-type=\"image\" " +
					"src=\"https://example.com/image.jpeg\"/>";

		_addFragmentEntry(
			_group1.getGroupId(), "test-image-fragment", "Test Image Fragment",
			html);
	}

	private JournalArticle _addJournalArticle(long groupId) throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			groupId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		journalArticle.setSmallImage(true);
		journalArticle.setSmallImageURL(
			"https://avatars1.githubusercontent.com/u/131436");

		return JournalTestUtil.updateArticle(journalArticle);
	}

	private ObjectAction _addObjectAction(ObjectEntry objectEntry)
		throws Exception {

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectEntry.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);
	}

	private ObjectEntry _addObjectEntry() throws Exception {
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

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		return _objectEntryLocalService.addObjectEntry(
			_group1.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addTextFragmentEntry() throws Exception {
		String html =
			"<lfr-editable id=\"element-text\" type=\"text\">Test Text " +
				"Fragment</lfr-editable>";

		_addFragmentEntry(
			_group1.getGroupId(), "test-text-fragment", "Test Text Fragment",
			html);
	}

	private void _addZipWriterEntry(
			ZipWriter zipWriter, URL url, Map<String, String> numberValuesMap,
			Map<String, String> stringValuesMap)
		throws IOException {

		String entryPath = url.getPath();

		String zipPath = StringUtil.removeSubstring(
			entryPath, _LAYOUT_PATE_TEMPLATES_PATH);

		String content = URLUtil.toString(url);

		content = StringUtil.replace(content, "\"${", "}\"", numberValuesMap);
		content = StringUtil.replace(content, "£{", "}", stringValuesMap);

		zipWriter.addEntry(zipPath, content);
	}

	private File _generateZipFile(
			String testPath, Map<String, String> numberValuesMap,
			Map<String, String> stringValuesMap)
		throws Exception {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		Enumeration<URL> enumeration = _bundle.findEntries(
			StringBundler.concat(
				_LAYOUT_PATE_TEMPLATES_PATH + testPath,
				StringPool.FORWARD_SLASH + _ROOT_FOLDER,
				StringPool.FORWARD_SLASH),
			LayoutPageTemplateExportImportConstants.
				FILE_NAME_PAGE_TEMPLATE_COLLECTION,
			true);

		try {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				_populateZipWriter(
					zipWriter, url, numberValuesMap, stringValuesMap);
			}

			return zipWriter.getFile();
		}
		catch (Exception exception) {
			throw new Exception(exception);
		}
	}

	private LayoutPageTemplateEntry _getImportLayoutPageTemplateEntry(
			File file, long groupId, LayoutsImporterResultEntry.Status status,
			LayoutsImportStrategy layoutsImportStrategy)
		throws Exception {

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries = null;

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(_group1, TestPropsValues.getUserId()));

		try {
			layoutsImporterResultEntries = _layoutsImporter.importFile(
				TestPropsValues.getUserId(), groupId, 0, file,
				layoutsImportStrategy, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertNotNull(layoutsImporterResultEntries);

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutsImporterResultEntry layoutPageTemplateImportEntry =
			layoutsImporterResultEntries.get(0);

		Assert.assertEquals(status, layoutPageTemplateImportEntry.getStatus());

		String layoutPageTemplateEntryKey = StringUtil.toLowerCase(
			layoutPageTemplateImportEntry.getName());

		layoutPageTemplateEntryKey = StringUtil.replace(
			layoutPageTemplateEntryKey, CharPool.SPACE, CharPool.DASH);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				groupId, layoutPageTemplateEntryKey);

		Assert.assertNotNull(layoutPageTemplateEntry);

		return layoutPageTemplateEntry;
	}

	private ServiceContext _getServiceContext(Group group, long userId)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(httpServletRequest));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group, userId);

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(
			_layoutLocalService.getLayout(TestPropsValues.getPlid()));

		LayoutSet layoutSet = _group1.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group1.getGroupId());
		themeDisplay.setSiteGroupId(_group1.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private File _importExportLayoutPageTemplateEntry(
			File file, long groupId, LayoutsImporterResultEntry.Status status,
			LayoutsImportStrategy layoutsImportStrategy)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry(
				file, groupId, status, layoutsImportStrategy);

		return ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFile", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
			});
	}

	private void _populateZipWriter(
			ZipWriter zipWriter, URL url, Map<String, String> numberValuesMap,
			Map<String, String> stringValuesMap)
		throws IOException {

		String zipPath = StringUtil.removeSubstring(
			url.getFile(), _LAYOUT_PATE_TEMPLATES_PATH);

		try (InputStream inputStream = url.openStream()) {
			zipWriter.addEntry(zipPath, inputStream);
		}

		String path = FileUtil.getPath(url.getPath());

		Enumeration<URL> enumeration = _bundle.findEntries(
			path,
			LayoutPageTemplateExportImportConstants.FILE_NAME_PAGE_TEMPLATE,
			true);

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(
				zipWriter, elementURL, numberValuesMap, stringValuesMap);
		}

		enumeration = _bundle.findEntries(
			path,
			LayoutPageTemplateExportImportConstants.FILE_NAME_PAGE_DEFINITION,
			true);

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(
				zipWriter, elementURL, numberValuesMap, stringValuesMap);
		}

		enumeration = _bundle.findEntries(path, "thumbnail.png", true);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(
				zipWriter, elementURL, numberValuesMap, stringValuesMap);
		}
	}

	private void _validateFile(File inputFile, File outputFile)
		throws Exception {

		ZipFile inputZipFile = new ZipFile(inputFile);

		Enumeration<? extends ZipEntry> inputEnumeration =
			inputZipFile.entries();

		Map<String, String> fileNameFileContentMap = new HashMap<>();

		int numberOfInputFiles = 0;

		while (inputEnumeration.hasMoreElements()) {
			ZipEntry zipEntry = inputEnumeration.nextElement();

			if (!zipEntry.isDirectory()) {
				numberOfInputFiles++;

				String content = StringUtil.read(
					inputZipFile.getInputStream(zipEntry));

				String name = zipEntry.getName();

				String[] parts = name.split("/");

				fileNameFileContentMap.put(parts[parts.length - 1], content);
			}
		}

		ZipFile outputZipFile = new ZipFile(outputFile);

		Enumeration<? extends ZipEntry> outputEnumeration =
			outputZipFile.entries();

		int numberOfOutputFiles = 0;

		while (outputEnumeration.hasMoreElements()) {
			ZipEntry zipEntry = outputEnumeration.nextElement();

			if (!zipEntry.isDirectory()) {
				numberOfOutputFiles++;

				String name = zipEntry.getName();

				String[] parts = name.split("/");

				Assert.assertEquals(
					_objectMapper.readTree(
						fileNameFileContentMap.get(parts[parts.length - 1])),
					_objectMapper.readTree(
						StringUtil.read(
							outputZipFile.getInputStream(zipEntry))));
			}
		}

		Assert.assertEquals(numberOfInputFiles, numberOfOutputFiles);
		Assert.assertTrue(numberOfInputFiles > 0);
	}

	private void _validateImportExport(File expectedFile, File inputFile)
		throws Exception {

		_validateImportExport(
			expectedFile, inputFile, _group1.getGroupId(),
			_group1.getGroupId());
	}

	private void _validateImportExport(
			File expectedFile, File inputFile, long groupId1, long groupId2)
		throws Exception {

		File outputFile1 = _importExportLayoutPageTemplateEntry(
			inputFile, groupId1, LayoutsImporterResultEntry.Status.IMPORTED,
			LayoutsImportStrategy.DO_NOT_OVERWRITE);

		_validateFile(expectedFile, outputFile1);

		File outputFile2 = _importExportLayoutPageTemplateEntry(
			outputFile1, groupId2, LayoutsImporterResultEntry.Status.IMPORTED,
			LayoutsImportStrategy.OVERWRITE);

		_validateFile(expectedFile, outputFile2);
	}

	private static final String _LAYOUT_PATE_TEMPLATES_PATH =
		"com/liferay/layout/page/template/admin/web/internal/portlet/action" +
			"/test/dependencies/import_export/page_templates/";

	private static final String _ROOT_FOLDER = "page-templates";

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	private Bundle _bundle;

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutsImporter _layoutsImporter;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/export_layout_page_template_entries"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectMapper _objectMapper;

	@Inject
	private Portal _portal;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}