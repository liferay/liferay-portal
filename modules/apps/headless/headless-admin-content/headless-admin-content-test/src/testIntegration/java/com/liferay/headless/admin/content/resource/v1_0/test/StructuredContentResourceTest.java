/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.headless.admin.content.client.dto.v1_0.ContentField;
import com.liferay.headless.admin.content.client.dto.v1_0.ContentFieldValue;
import com.liferay.headless.admin.content.client.dto.v1_0.StructuredContent;
import com.liferay.headless.admin.content.client.pagination.Page;
import com.liferay.headless.admin.content.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.StructuredContentResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class StructuredContentResourceTest
	extends BaseStructuredContentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_ddmStructure = _addDDMStructure(testGroup, "test-ddm-structure.json");

		_addDDMTemplate(_ddmStructure);

		_irrelevantDDMStructure = _addDDMStructure(
			irrelevantGroup, "test-ddm-structure.json");
		_localizedDDMStructure = _addDDMStructure(
			testGroup, "test-localized-ddm-structure.json");

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		_depotDDMStructure = _addDDMStructure(
			_depotEntry.getGroup(), "test-ddm-structure.json");

		StructuredContentResource.Builder builder =
			StructuredContentResource.builder();

		_structuredContentResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	public void testGetSiteStructuredContentsPage() throws Exception {
		super.testGetSiteStructuredContentsPage();

		_addDraftStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(),
			RandomTestUtil.randomDouble());

		StructuredContent structuredContent = _addDraftStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(), Double.valueOf(1));

		com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
			patchStructuredContent =
				_structuredContentResource.patchStructuredContent(
					structuredContent.getId(),
					new com.liferay.headless.delivery.client.dto.v1_0.
						StructuredContent() {

						{
							priority = Double.valueOf(3);
						}
					});

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				testGroup.getGroupId(), null, null, null, "priority eq 3.0",
				Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(_toStructuredContent(patchStructuredContent)),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		page = structuredContentResource.getSiteStructuredContentsPage(
			testGroup.getGroupId(), null, null, null, null,
			Pagination.of(1, 10), null);

		List<StructuredContent> structuredContents =
			(List<StructuredContent>)page.getItems();

		Iterator<StructuredContent> iterator = structuredContents.iterator();

		while (iterator.hasNext()) {
			structuredContent = iterator.next();

			_structuredContentResource.deleteStructuredContent(
				structuredContent.getId());
		}

		StructuredContent draftStructuredContent1 = _addDraftStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(),
			Double.valueOf(0.99));

		StructuredContent draftStructuredContent2 = _addDraftStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(), Double.valueOf(1));

		StructuredContent draftStructuredContent3 = _addDraftStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(), Double.valueOf(1.1));

		StructuredContent draftStructuredContent4 = _addDraftStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(),
			Double.valueOf(2.99));

		page = structuredContentResource.getSiteStructuredContentsPage(
			testGroup.getGroupId(), null, null, null, "priority ne 1.0",
			Pagination.of(1, 10), "priority:asc");

		Assert.assertEquals(3, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				draftStructuredContent1, draftStructuredContent3,
				draftStructuredContent4),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		page = structuredContentResource.getSiteStructuredContentsPage(
			testGroup.getGroupId(), null, null, null, "priority gt 0.99",
			Pagination.of(1, 10), "priority:desc");

		Assert.assertEquals(3, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				draftStructuredContent4, draftStructuredContent3,
				draftStructuredContent2),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		page = structuredContentResource.getSiteStructuredContentsPage(
			testGroup.getGroupId(), null, null, null, "priority ge 0.99",
			Pagination.of(1, 10), null);

		Assert.assertEquals(4, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(
				draftStructuredContent1, draftStructuredContent2,
				draftStructuredContent3, draftStructuredContent4),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		page = structuredContentResource.getSiteStructuredContentsPage(
			testGroup.getGroupId(), null, null, null, "priority gt 1.0",
			Pagination.of(1, 10), "priority");

		Assert.assertEquals(2, page.getTotalCount());

		assertEquals(
			Arrays.asList(draftStructuredContent3, draftStructuredContent4),
			(List<StructuredContent>)page.getItems());

		assertValid(page);
	}

	@Override
	@Test
	public void testGetStructuredContentByVersion() throws Exception {
		super.testGetStructuredContentByVersion();

		Locale locale = LocaleUtil.getDefault();

		StructuredContent randomStructuredContent1 = _randomStructuredContent(
			locale);

		randomStructuredContent1.setPriority(Double.valueOf(1));

		com.liferay.headless.admin.content.client.resource.v1_0.
			StructuredContentResource structuredContentResource =
				_buildStructureContentResource(locale);

		StructuredContent postStructuredContent1 =
			structuredContentResource.postSiteStructuredContentDraft(
				testGetSiteStructuredContentsPage_getSiteId(),
				randomStructuredContent1);

		StructuredContent getStructuredContent1 =
			structuredContentResource.getStructuredContentByVersion(
				postStructuredContent1.getId(), 1.0);

		assertEquals(postStructuredContent1, getStructuredContent1);
		Assert.assertEquals(
			Double.valueOf(1.0), getStructuredContent1.getPriority());

		StructuredContent postStructuredContent2 =
			structuredContentResource.postSiteStructuredContentDraft(
				testGetSiteStructuredContentsPage_getSiteId(),
				randomStructuredContent1);

		structuredContentResource =
			com.liferay.headless.admin.content.client.resource.v1_0.
				StructuredContentResource.builder(
				).authentication(
					"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).parameters(
					"restrictFields",
					StringBundler.concat(
						"actions,availableLanguages,contentFields,",
						"contentStructureId,creator,customFields,",
						"dateCreated,dateModified,datePublished,",
						"description,externalReferenceCode,friendlyUrlPath,",
						"id,key,keywords,numberOfComments,priority,",
						"relatedContents,renderedContents,siteId,",
						"structuredContentFolderId,subscribed,",
						"taxonomyCategoryBriefs,uuid,version")
				).build();

		assertEquals(
			new StructuredContent() {
				{
					title = postStructuredContent2.getTitle();
				}
			},
			structuredContentResource.getStructuredContentByVersion(
				postStructuredContent2.getId(), 1.0));

		structuredContentResource =
			com.liferay.headless.admin.content.client.resource.v1_0.
				StructuredContentResource.builder(
				).authentication(
					"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).parameters(
					"fields", "id"
				).build();

		assertEquals(
			new StructuredContent() {
				{
					id = postStructuredContent2.getId();
				}
			},
			structuredContentResource.getStructuredContentByVersion(
				postStructuredContent2.getId(), 1.0));

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			_depotEntry.getGroupId(), RandomTestUtil.randomString());

		StructuredContent randomStructuredContent2 = randomStructuredContent();

		randomStructuredContent2.setContentStructureId(
			_depotDDMStructure.getStructureId());

		com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
			postStructuredContent3 =
				_structuredContentResource.
					postStructuredContentFolderStructuredContent(
						journalFolder.getFolderId(),
						_toStructuredContent(randomStructuredContent2));

		structuredContentResource =
			com.liferay.headless.admin.content.client.resource.v1_0.
				StructuredContentResource.builder(
				).authentication(
					"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).build();

		StructuredContent getStructuredContent2 =
			structuredContentResource.getStructuredContentByVersion(
				postStructuredContent3.getId(), 1.0);

		Assert.assertEquals(
			postStructuredContent3.getId(), getStructuredContent2.getId());
		Assert.assertEquals(
			journalFolder.getFolderId(),
			GetterUtil.getLong(
				getStructuredContent2.getStructuredContentFolderId()));
	}

	@Override
	@Test
	public void testGetStructuredContentsVersionsPage() throws Exception {
		StructuredContent structuredContent1 = _postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());

		Long id = structuredContent1.getId();

		Page<StructuredContent> structuredContentsVersionsPage =
			structuredContentResource.getStructuredContentsVersionsPage(id);

		Assert.assertEquals(1L, structuredContentsVersionsPage.getTotalCount());

		com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
			putStructuredContent =
				_structuredContentResource.putStructuredContent(
					id, _toStructuredContent(structuredContent1));

		structuredContentsVersionsPage =
			structuredContentResource.getStructuredContentsVersionsPage(id);

		Assert.assertEquals(2L, structuredContentsVersionsPage.getTotalCount());

		structuredContentResource =
			com.liferay.headless.admin.content.client.resource.v1_0.
				StructuredContentResource.builder(
				).authentication(
					"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).parameters(
					"fields", "id"
				).build();

		structuredContentsVersionsPage =
			structuredContentResource.getStructuredContentsVersionsPage(id);

		Assert.assertEquals(2L, structuredContentsVersionsPage.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new StructuredContent() {
					{
						id = structuredContent1.getId();
					}
				},
				new StructuredContent() {
					{
						id = putStructuredContent.getId();
					}
				}),
			(List<StructuredContent>)structuredContentsVersionsPage.getItems());

		JournalFolder journalFolder1 = JournalTestUtil.addFolder(
			_depotEntry.getGroupId(), RandomTestUtil.randomString());

		StructuredContent randomStructuredContent1 = randomStructuredContent();

		randomStructuredContent1.setContentStructureId(
			_depotDDMStructure.getStructureId());

		com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
			structuredContent2 =
				_structuredContentResource.
					postStructuredContentFolderStructuredContent(
						journalFolder1.getFolderId(),
						_toStructuredContent(randomStructuredContent1));

		structuredContentResource =
			com.liferay.headless.admin.content.client.resource.v1_0.
				StructuredContentResource.builder(
				).authentication(
					"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).build();

		structuredContentsVersionsPage =
			structuredContentResource.getStructuredContentsVersionsPage(
				structuredContent2.getId());

		Assert.assertEquals(1L, structuredContentsVersionsPage.getTotalCount());
		assertValid(structuredContentsVersionsPage);

		StructuredContent getStructuredContent1 =
			structuredContentsVersionsPage.fetchFirstItem();

		Assert.assertEquals(
			journalFolder1.getFolderId(),
			GetterUtil.getLong(
				getStructuredContent1.getStructuredContentFolderId()));

		JournalFolder journalFolder2 = JournalTestUtil.addFolder(
			_depotEntry.getGroupId(), RandomTestUtil.randomString());
		JournalFolder journalFolder3 = JournalTestUtil.addFolder(
			_depotEntry.getGroupId(), RandomTestUtil.randomString());

		StructuredContent randomStructuredContent2 = randomStructuredContent();

		randomStructuredContent2.setContentStructureId(
			_depotDDMStructure.getStructureId());

		com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
			structuredContent3 =
				_structuredContentResource.
					postStructuredContentFolderStructuredContent(
						journalFolder2.getFolderId(),
						_toStructuredContent(randomStructuredContent2));

		structuredContentResource =
			com.liferay.headless.admin.content.client.resource.v1_0.
				StructuredContentResource.builder(
				).authentication(
					"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).build();

		_journalArticleLocalService.moveArticle(
			_depotEntry.getGroupId(), structuredContent3.getKey(),
			journalFolder3.getFolderId(),
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		structuredContentsVersionsPage =
			structuredContentResource.getStructuredContentsVersionsPage(
				structuredContent3.getId());

		Assert.assertEquals(1L, structuredContentsVersionsPage.getTotalCount());
		assertValid(structuredContentsVersionsPage);

		StructuredContent getStructuredContent2 =
			structuredContentsVersionsPage.fetchFirstItem();

		Assert.assertEquals(
			journalFolder3.getFolderId(),
			GetterUtil.getLong(
				getStructuredContent2.getStructuredContentFolderId()));
	}

	@Override
	@Test
	public void testPostSiteStructuredContentDraft() throws Exception {
		super.testPostSiteStructuredContentDraft();

		// Localized structured content with the default language

		_testPostSiteStructuredContentDraft(
			LocaleUtil.getDefault(), RandomTestUtil.randomDouble());

		// Localized structured content with a different language from the
		// default language

		_testPostSiteStructuredContentDraft(
			LocaleUtil.fromLanguageId("es-ES"), RandomTestUtil.randomDouble());

		// Structured content with a priority

		StructuredContent structuredContent1 =
			_testPostSiteStructuredContentDraft(
				LocaleUtil.getDefault(), Double.valueOf(1));

		Assert.assertEquals(
			Double.valueOf(1.0), structuredContent1.getPriority());

		// Structured content with the default priority

		StructuredContent structuredContent2 =
			_testPostSiteStructuredContentDraft(LocaleUtil.getDefault(), null);

		Assert.assertEquals(
			Double.valueOf(0.0), structuredContent2.getPriority());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"priority", "title"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"contentStructureId", "creatorId", "dateCreated", "dateModified",
			"datePublished", "friendlyUrlPath"
		};
	}

	@Override
	protected StructuredContent randomIrrelevantStructuredContent()
		throws Exception {

		StructuredContent structuredContent = super.randomStructuredContent();

		structuredContent.setContentStructureId(
			_irrelevantDDMStructure.getStructureId());
		structuredContent.setSiteId(irrelevantGroup.getGroupId());

		return structuredContent;
	}

	@Override
	protected StructuredContent randomStructuredContent() throws Exception {
		StructuredContent structuredContent = super.randomStructuredContent();

		structuredContent.setContentStructureId(_ddmStructure.getStructureId());

		return structuredContent;
	}

	@Override
	protected StructuredContent
			testDeleteStructuredContentByVersion_addStructuredContent()
		throws Exception {

		return _postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Override
	protected Double testDeleteStructuredContentByVersion_getVersion()
		throws Exception {

		return 1.0D;
	}

	@Override
	protected StructuredContent
			testGetSiteStructuredContentsPage_addStructuredContent(
				Long siteId, StructuredContent structuredContent)
		throws Exception {

		return _postSiteStructuredContent(siteId, structuredContent);
	}

	@Override
	protected StructuredContent
			testGetStructuredContentByVersion_addStructuredContent()
		throws Exception {

		return _postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Override
	protected Double testGetStructuredContentByVersion_getVersion()
		throws Exception {

		return 1.0D;
	}

	@Override
	protected Double testGraphQLDeleteStructuredContentByVersion_getVersion()
		throws Exception {

		return 1.0D;
	}

	@Override
	protected Double testGraphQLGetStructuredContentByVersion_getVersion()
		throws Exception {

		return 1.0D;
	}

	@Override
	protected StructuredContent
			testGraphQLStructuredContent_addStructuredContent()
		throws Exception {

		return _postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Override
	protected StructuredContent
			testPostSiteStructuredContentDraft_addStructuredContent(
				StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.postSiteStructuredContentDraft(
			testGroup.getGroupId(), structuredContent);
	}

	private DDMStructure _addDDMStructure(Group group, String fileName)
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(JournalArticle.class), group);

		return ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(_read(fileName)), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);
	}

	private DDMTemplate _addDDMTemplate(DDMStructure ddmStructure)
		throws Exception {

		return DDMTemplateTestUtil.addTemplate(
			ddmStructure.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM,
			_read("test-structured-content-template.vm"), LocaleUtil.US);
	}

	private StructuredContent _addDraftStructuredContent(
			Long siteId, Double priority)
		throws Exception {

		Locale locale = LocaleUtil.getDefault();

		StructuredContent structuredContent = _randomStructuredContent(locale);

		structuredContent.setPriority(priority);

		com.liferay.headless.admin.content.client.resource.v1_0.
			StructuredContentResource structuredContentResource =
				_buildStructureContentResource(locale);

		return structuredContentResource.postSiteStructuredContentDraft(
			siteId, structuredContent);
	}

	private void _assertLocalizedValue(
		Map<String, String> localizedValues, String value, String w3cLanguageId,
		Set<String> w3cLanguageIds) {

		Assert.assertEquals(w3cLanguageIds, localizedValues.keySet());
		Assert.assertEquals(value, localizedValues.get(w3cLanguageId));
	}

	private void _assertLocalizedValues(
		StructuredContent structuredContent, String w3cLanguageId) {

		Set<String> w3cLanguageIds = SetUtil.fromArray("es-ES", "en-US");

		Assert.assertEquals(
			w3cLanguageIds,
			SetUtil.fromArray(structuredContent.getAvailableLanguages()));

		_assertLocalizedValue(
			structuredContent.getDescription_i18n(),
			structuredContent.getDescription(), w3cLanguageId, w3cLanguageIds);
		_assertLocalizedValue(
			structuredContent.getTitle_i18n(), structuredContent.getTitle(),
			w3cLanguageId, w3cLanguageIds);
		_assertLocalizedValue(
			structuredContent.getFriendlyUrlPath_i18n(),
			structuredContent.getFriendlyUrlPath(), w3cLanguageId,
			w3cLanguageIds);
		_assertLocalizedValue(
			structuredContent.getDescription_i18n(),
			structuredContent.getDescription(), w3cLanguageId, w3cLanguageIds);

		for (ContentField contentField : structuredContent.getContentFields()) {
			Map<String, ContentFieldValue> contentFieldValue_i18n =
				contentField.getContentFieldValue_i18n();

			Assert.assertEquals(
				w3cLanguageIds, contentFieldValue_i18n.keySet());
		}
	}

	private com.liferay.headless.admin.content.client.resource.v1_0.
		StructuredContentResource _buildStructureContentResource(
			Locale locale) {

		com.liferay.headless.admin.content.client.resource.v1_0.
			StructuredContentResource.Builder builder =
				com.liferay.headless.admin.content.client.resource.v1_0.
					StructuredContentResource.builder();

		return builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			locale
		).header(
			"X-Accept-All-Languages", "true"
		).build();
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private StructuredContent _postSiteStructuredContent(
			Long siteId, StructuredContent structuredContent)
		throws Exception {

		return _toStructuredContent(
			_structuredContentResource.postSiteStructuredContent(
				siteId,
				new com.liferay.headless.delivery.client.dto.v1_0.
					StructuredContent() {

					{
						contentStructureId =
							structuredContent.getContentStructureId();
						priority = structuredContent.getPriority();
						siteId = structuredContent.getSiteId();
						title = structuredContent.getTitle();
					}
				}));
	}

	private StructuredContent _randomStructuredContent(Locale locale)
		throws Exception {

		StructuredContent structuredContent = super.randomStructuredContent();

		String w3cLanguageId = LocaleUtil.toW3cLanguageId(locale);

		Map<String, ContentFieldValue> contentFieldValues = HashMapBuilder.put(
			"en-US",
			(ContentFieldValue)new ContentFieldValue() {

				{
					data = RandomTestUtil.randomString(10);
				}
			}
		).put(
			"es-ES",
			(ContentFieldValue)new ContentFieldValue() {

				{
					data = RandomTestUtil.randomString(10);
				}
			}
		).build();

		structuredContent.setContentFields(
			new ContentField[] {
				new ContentField() {
					{
						contentFieldValue = contentFieldValues.get(
							w3cLanguageId);
						contentFieldValue_i18n = contentFieldValues;
						name = "MyText";
					}
				}
			});

		structuredContent.setContentStructureId(
			_localizedDDMStructure.getStructureId());

		Map<String, String> description_i18n = HashMapBuilder.put(
			"en-US", RandomTestUtil.randomString()
		).put(
			"es-ES", RandomTestUtil.randomString()
		).build();

		structuredContent.setDescription(description_i18n.get(w3cLanguageId));
		structuredContent.setDescription_i18n(description_i18n);

		Map<String, String> friendlyUrlPath_i18n = HashMapBuilder.put(
			"en-US", RandomTestUtil.randomString()
		).put(
			"es-ES", RandomTestUtil.randomString()
		).build();

		structuredContent.setFriendlyUrlPath(
			friendlyUrlPath_i18n.get(w3cLanguageId));
		structuredContent.setFriendlyUrlPath_i18n(friendlyUrlPath_i18n);

		Map<String, String> title_i18n = HashMapBuilder.put(
			"en-US", RandomTestUtil.randomString()
		).put(
			"es-ES", RandomTestUtil.randomString()
		).build();

		structuredContent.setTitle(title_i18n.get(w3cLanguageId));
		structuredContent.setTitle_i18n(title_i18n);

		return structuredContent;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private StructuredContent _testPostSiteStructuredContentDraft(
			Locale locale, Double priority)
		throws Exception {

		StructuredContent randomStructuredContent = _randomStructuredContent(
			locale);

		randomStructuredContent.setPriority(priority);

		com.liferay.headless.admin.content.client.resource.v1_0.
			StructuredContentResource structuredContentResource =
				_buildStructureContentResource(locale);

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContentDraft(
				testGetSiteStructuredContentsPage_getSiteId(),
				randomStructuredContent);

		_assertLocalizedValues(
			postStructuredContent, LocaleUtil.toW3cLanguageId(locale));

		if (priority != null) {
			assertEquals(randomStructuredContent, postStructuredContent);
		}

		assertValid(postStructuredContent);

		return postStructuredContent;
	}

	private com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
		_toStructuredContent(StructuredContent structuredContent) {

		return new com.liferay.headless.delivery.client.dto.v1_0.
			StructuredContent() {

			{
				contentStructureId = structuredContent.getContentStructureId();
				siteId = structuredContent.getSiteId();
				title = structuredContent.getTitle();
			}
		};
	}

	private StructuredContent _toStructuredContent(
		com.liferay.headless.delivery.client.dto.v1_0.StructuredContent
			structuredContent) {

		return new StructuredContent() {
			{
				contentStructureId = structuredContent.getContentStructureId();
				dateCreated = structuredContent.getDateCreated();
				dateModified = structuredContent.getDateModified();
				id = structuredContent.getId();
				priority = structuredContent.getPriority();
				siteId = structuredContent.getSiteId();
				title = structuredContent.getTitle();
			}
		};
	}

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

	private DDMStructure _ddmStructure;
	private DDMStructure _depotDDMStructure;
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private DDMStructure _irrelevantDDMStructure;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private DDMStructure _localizedDDMStructure;
	private StructuredContentResource _structuredContentResource;

}