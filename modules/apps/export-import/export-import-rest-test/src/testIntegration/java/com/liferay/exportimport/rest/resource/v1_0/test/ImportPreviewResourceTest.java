/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSection;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportPreviewResource;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;
import java.io.Serializable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ImportPreviewResourceTest
	extends BaseImportPreviewResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_importPreviewResource = ImportPreviewResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_userLocalService.deleteUser(_user);
	}

	@Override
	@Test
	public void testPostAssetLibraryImportPreview() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_importPreviewResource.postAssetLibraryImportPreviewHttpResponse(
				testDepotEntryGroup.getExternalReferenceCode(), null,
				HashMapBuilder.put(
					"file",
					_exportLayoutAsFile(testDepotEntryGroup.getGroupId())
				).build()));

		_testPostImportPreviewWithInvalidFile(
			file ->
				importPreviewResource.postAssetLibraryImportPreviewHttpResponse(
					testDepotEntryGroup.getExternalReferenceCode(), null,
					HashMapBuilder.put(
						"file", file
					).build()));

		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			testDepotEntryGroup.getGroupId(),
			ObjectDefinitionConstants.SCOPE_DEPOT);

		try {
			_testPostImportPreviewWithObjectEntries(
				testDepotEntryGroup.getGroupId(), objectDefinition,
				file -> importPreviewResource.postAssetLibraryImportPreview(
					testDepotEntryGroup.getExternalReferenceCode(), null,
					HashMapBuilder.put(
						"file", file
					).build()));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Override
	@Test
	public void testPostAssetLibraryPortletImportPreview() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			testDepotEntryGroup);

		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			testDepotEntryGroup.getGroupId(),
			ObjectDefinitionConstants.SCOPE_DEPOT);

		String portletId = objectDefinition.getPortletId();

		LayoutTestUtil.addPortletToLayout(layout, portletId);

		assertHttpResponseStatusCode(
			403,
			_importPreviewResource.
				postAssetLibraryPortletImportPreviewHttpResponse(
					testDepotEntryGroup.getExternalReferenceCode(), portletId,
					layout.getPlid(), null,
					HashMapBuilder.put(
						"file",
						_exportPortletAsFile(
							testDepotEntryGroup.getGroupId(), layout.getPlid(),
							portletId)
					).build()));

		_testPostImportPreviewWithInvalidFile(
			file ->
				importPreviewResource.
					postAssetLibraryPortletImportPreviewHttpResponse(
						testDepotEntryGroup.getExternalReferenceCode(),
						portletId, layout.getPlid(), null,
						HashMapBuilder.put(
							"file", file
						).build()));
		_testPostPortletImportPreviewWithObjectEntries(
			testDepotEntryGroup.getGroupId(), objectDefinition,
			layout.getPlid(),
			file -> importPreviewResource.postAssetLibraryPortletImportPreview(
				testDepotEntryGroup.getExternalReferenceCode(), portletId,
				layout.getPlid(), null,
				HashMapBuilder.put(
					"file", file
				).build()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Override
	@Test
	public void testPostImportPreview() throws Exception {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			testCompany.getCompanyId());

		assertHttpResponseStatusCode(
			403,
			_importPreviewResource.postImportPreviewHttpResponse(
				null,
				HashMapBuilder.put(
					"file", _exportLayoutAsFile(group.getGroupId())
				).build()));

		_testPostImportPreviewWithInvalidFile(
			file -> importPreviewResource.postImportPreviewHttpResponse(
				null,
				HashMapBuilder.put(
					"file", file
				).build()));

		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			_testPostImportPreviewWithObjectEntries(
				group.getGroupId(), objectDefinition,
				file -> importPreviewResource.postImportPreview(
					null,
					HashMapBuilder.put(
						"file", file
					).build()));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Override
	@Test
	public void testPostSiteImportPreview() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_importPreviewResource.postSiteImportPreviewHttpResponse(
				testGroup.getExternalReferenceCode(), null,
				HashMapBuilder.put(
					"file", _exportLayoutAsFile(testGroup.getGroupId())
				).build()));

		_testPostImportPreviewWithInvalidFile(
			file -> importPreviewResource.postSiteImportPreviewHttpResponse(
				testGroup.getExternalReferenceCode(), null,
				HashMapBuilder.put(
					"file", file
				).build()));

		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			testGroup.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE);

		try {
			_testPostImportPreviewWithObjectEntries(
				testGroup.getGroupId(), objectDefinition,
				file -> importPreviewResource.postSiteImportPreview(
					testGroup.getExternalReferenceCode(), null,
					HashMapBuilder.put(
						"file", file
					).build()));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Override
	@Test
	public void testPostSitePortletImportPreview() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			testGroup.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE);

		String portletId = objectDefinition.getPortletId();

		LayoutTestUtil.addPortletToLayout(layout, portletId);

		assertHttpResponseStatusCode(
			403,
			_importPreviewResource.postSitePortletImportPreviewHttpResponse(
				testGroup.getExternalReferenceCode(), portletId,
				layout.getPlid(), null,
				HashMapBuilder.put(
					"file",
					_exportPortletAsFile(
						testGroup.getGroupId(), layout.getPlid(), portletId)
				).build()));

		_testPostImportPreviewWithInvalidFile(
			file ->
				importPreviewResource.postSitePortletImportPreviewHttpResponse(
					testGroup.getExternalReferenceCode(), portletId,
					layout.getPlid(), null,
					HashMapBuilder.put(
						"file", file
					).build()));
		_testPostPortletImportPreviewWithObjectEntries(
			testGroup.getGroupId(), objectDefinition, layout.getPlid(),
			file -> importPreviewResource.postSitePortletImportPreview(
				testGroup.getExternalReferenceCode(), portletId,
				layout.getPlid(), null,
				HashMapBuilder.put(
					"file", file
				).build()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private void _addObjectEntry(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		_objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textField", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private File _exportLayoutAsFile(long groupId) throws Exception {
		Map<String, Serializable> parameterMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					TestPropsValues.getUser(), groupId, false, null,
					HashMapBuilder.put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_ALL,
						new String[] {Boolean.TRUE.toString()}
					).build());

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					TestPropsValues.getUserId(), groupId,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					parameterMap, new ServiceContext());

		return ExportImportLocalServiceUtil.exportLayoutsAsFile(
			exportImportConfiguration);
	}

	private File _exportPortletAsFile(long groupId, long plid, String portletId)
		throws Exception {

		Map<String, Serializable> parameterMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportPortletSettingsMap(
					TestPropsValues.getUser(), plid, groupId, portletId,
					HashMapBuilder.put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_ALL,
						new String[] {Boolean.TRUE.toString()}
					).build(),
					StringPool.BLANK);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					TestPropsValues.getUserId(), groupId,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					parameterMap, new ServiceContext());

		return ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);
	}

	private long _getAdditionCount(
		ImportPreview importPreview, String portletId) {

		PreviewPortletDataHandlerSection[] previewPortletDataHandlerSections =
			importPreview.getPreviewPortletDataHandlerSections();

		if (previewPortletDataHandlerSections == null) {
			return 0L;
		}

		String handlerName = "PORTLET_DATA_" + portletId;

		for (PreviewPortletDataHandlerSection previewPortletDataHandlerSection :
				previewPortletDataHandlerSections) {

			for (PreviewPortletDataHandler previewPortletDataHandler :
					previewPortletDataHandlerSection.
						getPreviewPortletDataHandlers()) {

				if (handlerName.equals(previewPortletDataHandler.getName())) {
					return previewPortletDataHandler.getAdditionCount();
				}
			}
		}

		return 0;
	}

	private ObjectDefinition _publishObjectDefinitionWithEntries(
			long groupId, String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), "textField", false)),
				scope);

		if (Objects.equals(scope, ObjectDefinitionConstants.SCOPE_DEPOT)) {
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE);
		}

		_addObjectEntry(groupId, objectDefinition);

		return objectDefinition;
	}

	private void _testPostImportPreviewWithInvalidFile(
			UnsafeFunction<File, HttpInvoker.HttpResponse, Exception>
				unsafeFunction)
		throws Exception {

		File file = File.createTempFile(RandomTestUtil.randomString(), ".lar");

		FileUtil.write(file, RandomTestUtil.randomBytes());

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.zip.internal.reader.BaseZipReader",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(400, unsafeFunction.apply(file));
		}
	}

	private void _testPostImportPreviewWithObjectEntries(
			long groupId, ObjectDefinition objectDefinition,
			UnsafeFunction<File, ImportPreview, Exception> unsafeFunction)
		throws Exception {

		ImportPreview importPreview = unsafeFunction.apply(
			_exportLayoutAsFile(groupId));

		long additionCount = _getAdditionCount(
			importPreview, objectDefinition.getPortletId());

		Assert.assertTrue(additionCount > 0);
	}

	private void _testPostPortletImportPreviewWithObjectEntries(
			long groupId, ObjectDefinition objectDefinition, long plid,
			UnsafeFunction<File, ImportPreview, Exception> unsafeFunction)
		throws Exception {

		ImportPreview importPreview = unsafeFunction.apply(
			_exportPortletAsFile(
				groupId, plid, objectDefinition.getPortletId()));

		long additionCount = _getAdditionCount(
			importPreview, objectDefinition.getPortletId());

		Assert.assertTrue(additionCount > 0);
	}

	private ImportPreviewResource _importPreviewResource;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}