/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2024-09
 */

package com.liferay.site.cms.site.initializer.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class TranslateObjectEntryCMSServletTest extends BaseCMSServletTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", group.getCompanyId());
		_blogObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BLOG", group.getCompanyId());
		_contentsObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());
	}

	@Test
	public void testTranslateBulkAction() throws Exception {
		_testTranslateBulkActionWithBulkActionItems();
		_testTranslateBulkActionWithSelectAll();
	}

	private ObjectEntry _addObjectEntry(
			long objectDefinitionId, long objectEntryFolderId,
			ServiceContext serviceContext)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(), objectDefinitionId,
			objectEntryFolderId, "en_US",
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(byte[] content)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL,
			"http://localhost:" + PortalUtil.getPortalServerPort(false) + "/");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));
		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());

		if (content != null) {
			mockHttpServletRequest.setContent(content);
		}

		mockHttpServletRequest.setContextPath("/o");
		mockHttpServletRequest.setMethod(HttpMethods.GET);
		mockHttpServletRequest.setServletPath("/cms/translations");

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setRequest(mockHttpServletRequest);

		return themeDisplay;
	}

	private void _testTranslateBulkActionWithBulkActionItems()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntry objectEntry1 = _addObjectEntry(
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);
		ObjectEntry objectEntry2 = _addObjectEntry(
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);

		_addObjectEntry(
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				depotEntry.getGroupId(),
				_contentsObjectEntryFolder.getObjectEntryFolderId());

		_addObjectEntry(
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), serviceContext);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				JSONUtil.put(
					"bulkActionItems",
					JSONUtil.putAll(
						JSONUtil.put(
							"classExternalReferenceCode",
							objectEntry1.getExternalReferenceCode()
						).put(
							"className", objectEntry1.getModelClassName()
						).put(
							"classPK", objectEntry1.getObjectEntryId()
						).put(
							"name", objectEntry1.getTitleValue()
						),
						JSONUtil.put(
							"classExternalReferenceCode",
							objectEntry2.getExternalReferenceCode()
						).put(
							"className", objectEntry2.getModelClassName()
						).put(
							"classPK", objectEntry2.getObjectEntryId()
						).put(
							"name", objectEntry2.getTitleValue()
						),
						JSONUtil.put(
							"classExternalReferenceCode",
							objectEntryFolder.getExternalReferenceCode()
						).put(
							"className", objectEntryFolder.getModelClassName()
						).put(
							"classPK",
							objectEntryFolder.getObjectEntryFolderId()
						).put(
							"name", objectEntryFolder.getName()
						))
				).put(
					"selectionScope", JSONUtil.put("selectAll", false)
				).put(
					"sourceLanguageId", "en_US"
				).put(
					"targetLanguageIds", new String[] {"nl_NL", "de_DE"}
				).put(
					"type", "ExportTranslationBulkAction"
				).put(
					"xliffMimeType", "application/x-xliff+xml"
				).toString(
				).getBytes());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private void _testTranslateBulkActionWithSelectAll() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		_addObjectEntry(
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);
		_addObjectEntry(
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);
		_addObjectEntry(
			_blogObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);
		_addObjectEntry(
			_blogObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(),
			serviceContext);

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				depotEntry.getGroupId(),
				_contentsObjectEntryFolder.getObjectEntryFolderId());

		_addObjectEntry(
			_blogObjectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), serviceContext);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				JSONUtil.put(
					"selectionScope", JSONUtil.put("selectAll", true)
				).put(
					"sourceLanguageId", "en_US"
				).put(
					"targetLanguageIds", new String[] {"nl_NL", "de_DE"}
				).put(
					"type", "ExportTranslationBulkAction"
				).put(
					"xliffMimeType", "application/x-xliff+xml"
				).toString(
				).getBytes());

		mockHttpServletRequest.setParameter(
			"filter",
			"cmsRoot eq true and cmsSection eq 'contents' and status in (0, " +
				"2, 3)");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		ZipReader zipReader = _zipReaderFactory.getZipReader(
			new ByteArrayInputStream(
				mockHttpServletResponse.getContentAsByteArray()));

		List<String> entries = zipReader.getEntries();

		Assert.assertEquals(entries.toString(), 2, entries.size());
		Assert.assertTrue(
			entries.contains("Basic Web Content Translations-en_US.zip"));
		Assert.assertTrue(entries.contains("Blog Translations-en_US.zip"));
	}

	private ObjectDefinition _basicWebContentObjectDefinition;
	private ObjectDefinition _blogObjectDefinition;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectEntryFolder _contentsObjectEntryFolder;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.TranslateObjectEntryCMSServlet"
	)
	private Servlet _servlet;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}