/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ResourceRequest;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class ExportDisplayPagesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	public void testGetFile() throws Exception {
		String name = "Display Page Template One";

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				name, WorkflowConstants.STATUS_APPROVED);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), layoutPageTemplateEntry.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateEntry.getPlid()),
				_read("layout_data.json"));

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_group.getGroupId(), RandomTestUtil.randomString(),
			_serviceContext);

		Class<?> clazz = getClass();

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			fileEntry.getFileEntryId());

		File file = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFile", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
			});

		try (ZipFile zipFile = new ZipFile(file)) {
			int count = 0;

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				if (!zipEntry.isDirectory()) {
					_validateZipEntry(new String[] {name}, zipEntry, zipFile);

					count++;
				}
			}

			Assert.assertEquals(3, count);
		}
	}

	@Test
	public void testGetFileDraft() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				StringUtil.randomString(), WorkflowConstants.STATUS_DRAFT);

		File file = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFile", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
			});

		try (ZipFile zipFile = new ZipFile(file)) {
			Assert.assertEquals(0, zipFile.size());
		}
	}

	@Test
	public void testGetFileMultipleDisplayPageTemplates() throws Exception {
		String name1 = "Display Page Template One";

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(
				name1, WorkflowConstants.STATUS_APPROVED);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), layoutPageTemplateEntry1.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateEntry1.getPlid()),
				_read("layout_data.json"));

		String name2 = "Display Page Template Two";

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(
				name2, WorkflowConstants.STATUS_APPROVED);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), layoutPageTemplateEntry2.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateEntry2.getPlid()),
				_read("layout_data.json"));

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_group.getGroupId(), RandomTestUtil.randomString(),
			_serviceContext);

		Class<?> clazz = getClass();

		FileEntry fileEntry1 = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		FileEntry fileEntry2 = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry2.getLayoutPageTemplateEntryId(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			fileEntry1.getFileEntryId());
		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry2.getLayoutPageTemplateEntryId(),
			fileEntry2.getFileEntryId());

		File file = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFile", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId()
			});

		try (ZipFile zipFile = new ZipFile(file)) {
			int count = 0;

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				if (!zipEntry.isDirectory()) {
					_validateZipEntry(
						new String[] {name1, name2}, zipEntry, zipFile);

					count++;
				}
			}

			Assert.assertEquals(6, count);
		}
	}

	@Test
	public void testGetFileNameMultipleDisplayPageTemplates() {
		String fileName = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFileName", new Class<?>[] {long[].class},
			new long[] {
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
			});

		Assert.assertTrue(fileName.startsWith("display-page-templates-"));
		Assert.assertTrue(fileName.endsWith(".zip"));
	}

	@Test
	public void testGetFileNameSingleDisplayPageTemplate() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				"Display Page Template One", WorkflowConstants.STATUS_APPROVED);

		String fileName = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFileName", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
			});

		Assert.assertTrue(
			fileName.startsWith(
				"display-page-template-" +
					layoutPageTemplateEntry.getLayoutPageTemplateEntryKey() +
						"-"));
		Assert.assertTrue(fileName.endsWith(".zip"));
	}

	@Test
	public void testGetLayoutPageTemplateEntryIdsSingleDisplayPageTemplate() {
		long expectedLayoutPageTemplateEntryId = RandomTestUtil.randomLong();

		long[] actualLayoutPageTemplateEntryIds = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getLayoutPageTemplateEntryIds",
			new Class<?>[] {ResourceRequest.class},
			_getMockLiferayResourceRequest(expectedLayoutPageTemplateEntryId));

		Assert.assertEquals(
			Arrays.toString(actualLayoutPageTemplateEntryIds), 1,
			actualLayoutPageTemplateEntryIds.length);
		Assert.assertEquals(
			expectedLayoutPageTemplateEntryId,
			actualLayoutPageTemplateEntryIds[0]);
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(
			String name, int status)
		throws Exception {

		String className = "com.liferay.journal.model.JournalArticle";

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			null, _serviceContext.getUserId(),
			_serviceContext.getScopeGroupId(), 0, null,
			_portal.getClassNameId(className), _getClassTypeId(className), name,
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, status,
			_serviceContext);
	}

	private long _getClassTypeId(String className) {
		InfoItemFormVariation infoItemFormVariation =
			_getFirstInfoItemFormVariation(className);

		return GetterUtil.getLong(infoItemFormVariation.getKey());
	}

	private InfoItemFormVariation _getFirstInfoItemFormVariation(
		String className) {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>(
			infoItemFormVariationsProvider.getInfoItemFormVariations(
				_group.getGroupId()));

		Assert.assertFalse(infoItemFormVariations.isEmpty());

		infoItemFormVariations.sort(
			Comparator.comparing(InfoItemFormVariation::getKey));

		return infoItemFormVariations.get(0);
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
		long layoutPageTemplateEntryId) {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter(
			"layoutPageTemplateEntryId",
			String.valueOf(layoutPageTemplateEntryId));

		return mockLiferayResourceRequest;
	}

	private boolean _isDisplayPageFile(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		if ((pathParts.length == 3) &&
			Objects.equals(pathParts[0], "display-page-templates") &&
			Objects.equals(pathParts[2], "display-page-template.json")) {

			return true;
		}

		return false;
	}

	private boolean _isDisplayPageThumbnailFile(String fileName) {
		String[] pathParts = StringUtil.split(fileName, CharPool.SLASH);

		if ((pathParts.length == 3) &&
			Objects.equals(pathParts[0], "display-page-templates") &&
			Objects.equals(pathParts[2], "thumbnail.png")) {

			return true;
		}

		return false;
	}

	private boolean _isPageDefinitionFile(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		if ((pathParts.length == 3) &&
			Objects.equals(pathParts[0], "display-pages") &&
			Objects.equals(pathParts[2], "page-definition.json")) {

			return true;
		}

		return false;
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _validateContent(String content, String expectedContent)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(content);

		JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject(
			expectedContent);

		Assert.assertEquals(
			expectedJSONObject.toString(), jsonObject.toString());
	}

	private void _validateContent(
			String content, String expectedFileName,
			String[] expectedDisplayPageTemplateNames,
			HashMap<String, String> inputValuesMap)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(content);

		boolean equals = false;

		for (String expectedDisplayPageTemplateName :
				expectedDisplayPageTemplateNames) {

			String expectedJSON = String.valueOf(
				JSONFactoryUtil.createJSONObject(
					StringUtil.replace(
						_read(expectedFileName), "\"${", "}\"",
						HashMapBuilder.putAll(
							inputValuesMap
						).put(
							"DISPLAY_PAGE_TEMPLATE_NAME",
							StringPool.QUOTE + expectedDisplayPageTemplateName +
								StringPool.QUOTE
						).build())));

			equals = expectedJSON.equals(jsonObject.toString());

			if (equals) {
				break;
			}
		}

		Assert.assertTrue(equals);
	}

	private void _validateZipEntry(
			String[] expectedDisplayPageTemplateNames, ZipEntry zipEntry,
			ZipFile zipFile)
		throws Exception {

		if (_isPageDefinitionFile(zipEntry.getName())) {
			_validateContent(
				StringUtil.read(zipFile.getInputStream(zipEntry)),
				_read("expected_display_page_page_template_definition.json"));
		}

		if (_isDisplayPageFile(zipEntry.getName())) {
			InfoItemFormVariation infoItemFormVariation =
				_getFirstInfoItemFormVariation(
					"com.liferay.journal.model.JournalArticle");

			_validateContent(
				StringUtil.read(zipFile.getInputStream(zipEntry)),
				"expected_display_page_template.json",
				expectedDisplayPageTemplateNames,
				HashMapBuilder.put(
					"CONTENT_SUBTYPE_SUBTYPE_ID", infoItemFormVariation.getKey()
				).put(
					"CONTENT_SUBTYPE_SUBTYPE_KEY",
					infoItemFormVariation.getExternalReferenceCode()
				).build());
		}

		if (_isDisplayPageThumbnailFile(zipEntry.getName())) {
			Assert.assertArrayEquals(
				FileUtil.getBytes(getClass(), "dependencies/thumbnail.png"),
				FileUtil.getBytes(zipFile.getInputStream(zipEntry)));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/export_display_pages"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}