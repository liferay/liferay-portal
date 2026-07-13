/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.rest.client.dto.v1_0.Choice;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSection;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportPreviewResource;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
public class ExportPreviewResourceTest
	extends BaseExportPreviewResourceTestCase {

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

		_companyObjectDefinition = _publishObjectDefinitionWithEntries(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_depotObjectDefinition = _publishObjectDefinitionWithEntries(
			testDepotEntryGroup.getGroupId(),
			ObjectDefinitionConstants.SCOPE_DEPOT);
		_siteObjectDefinition = _publishObjectDefinitionWithEntries(
			testGroup.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE);

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_exportPreviewResource = ExportPreviewResource.builder(
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

		_objectDefinitionLocalService.deleteObjectDefinition(
			_companyObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			_depotObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			_siteObjectDefinition);

		_userLocalService.deleteUser(_user);
	}

	@FeatureFlag("LPD-17564")
	@Override
	@Test
	public void testGetAssetLibraryExportPreview() throws Exception {
		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getAssetLibraryExportPreviewHttpResponse(
				testDepotEntryGroup.getExternalReferenceCode(), null, null));

		_testGetExportPreviewWithDateFilter(
			_depotObjectDefinition,
			(startDate, endDate) ->
				exportPreviewResource.getAssetLibraryExportPreview(
					testDepotEntryGroup.getExternalReferenceCode(), endDate,
					startDate));
		_testGetExportPreviewWithDifferentScope(
			exportPreviewResource.getAssetLibraryExportPreview(
				testDepotEntryGroup.getExternalReferenceCode(), null, null),
			_companyObjectDefinition, _siteObjectDefinition);
	}

	@FeatureFlag("LPD-17564")
	@Override
	@Test
	public void testGetAssetLibraryPortletExportPreview() throws Exception {
		String portletId = _depotObjectDefinition.getPortletId();

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.
				getAssetLibraryPortletExportPreviewHttpResponse(
					testDepotEntryGroup.getExternalReferenceCode(), portletId,
					null, 0L, null));

		_testGetExportPreviewWithDateFilter(
			_depotObjectDefinition,
			(startDate, endDate) ->
				exportPreviewResource.getAssetLibraryPortletExportPreview(
					testDepotEntryGroup.getExternalReferenceCode(), portletId,
					endDate, 0L, startDate));

		long plid = _addLayoutWithPortlet(testDepotEntryGroup, portletId);

		_testGetPortletExportPreview(
			exportPreviewResource.getAssetLibraryPortletExportPreview(
				testDepotEntryGroup.getExternalReferenceCode(), portletId, null,
				plid, null),
			portletId);
	}

	@Override
	@Test
	public void testGetExportPreview() throws Exception {
		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getExportPreviewHttpResponse(null, null));

		_testGetExportPreviewWithDateFilter(
			_companyObjectDefinition,
			(startDate, endDate) -> exportPreviewResource.getExportPreview(
				endDate, startDate));
		_testGetExportPreviewWithDifferentScope(
			exportPreviewResource.getExportPreview(null, null),
			_depotObjectDefinition, _siteObjectDefinition);
		_testGetExportPreviewWithDeletions();
	}

	@FeatureFlag("LPD-38869")
	@Override
	@Test
	public void testGetSiteExportPreview() throws Exception {
		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getSiteExportPreviewHttpResponse(
				testGroup.getExternalReferenceCode(), null, null));

		_testGetExportPreviewWithDateFilter(
			_siteObjectDefinition,
			(startDate, endDate) -> exportPreviewResource.getSiteExportPreview(
				testGroup.getExternalReferenceCode(), endDate, startDate));
		_testGetExportPreviewWithDifferentScope(
			exportPreviewResource.getSiteExportPreview(
				testGroup.getExternalReferenceCode(), null, null),
			_companyObjectDefinition, _depotObjectDefinition);
		_testGetExportPreviewWithLayoutSet(
			(startDate, endDate) -> exportPreviewResource.getSiteExportPreview(
				testGroup.getExternalReferenceCode(), endDate, startDate));
	}

	@Override
	@Test
	public void testGetSitePortletExportPreview() throws Exception {
		String portletId = _siteObjectDefinition.getPortletId();

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getSitePortletExportPreviewHttpResponse(
				testGroup.getExternalReferenceCode(), portletId, null, 0L,
				null));

		_testGetExportPreviewWithDateFilter(
			_siteObjectDefinition,
			(startDate, endDate) ->
				exportPreviewResource.getSitePortletExportPreview(
					testGroup.getExternalReferenceCode(), portletId, endDate,
					0L, startDate));

		long plid = _addLayoutWithPortlet(testGroup, portletId);

		_testGetPortletExportPreview(
			exportPreviewResource.getSitePortletExportPreview(
				testGroup.getExternalReferenceCode(), portletId, null, plid,
				null),
			portletId);
	}

	private long _addLayoutWithPortlet(Group group, String portletId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(
			layout, portletId,
			HashMapBuilder.put(
				RandomTestUtil.randomString(),
				new String[] {RandomTestUtil.randomString()}
			).build());

		return layout.getPlid();
	}

	private void _addObjectEntry(
			long groupId, Date modifiedDate, ObjectDefinition objectDefinition)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setModifiedDate(modifiedDate);

		_objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textField", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	private long _getAdditionCount(
		ExportPreview exportPreview, String portletId) {

		PreviewPortletDataHandler previewPortletDataHandler =
			_getPreviewPortletDataHandler(
				exportPreview, "PORTLET_DATA_" + portletId);

		if (previewPortletDataHandler == null) {
			return 0L;
		}

		return previewPortletDataHandler.getAdditionCount();
	}

	private Choice _getChoice(
		PreviewPortletDataHandler previewPortletDataHandler, String name) {

		for (PreviewPortletDataHandlerControl previewPortletDataHandlerControl :
				previewPortletDataHandler.
					getPreviewPortletDataHandlerControls()) {

			if (!(previewPortletDataHandlerControl instanceof
					PreviewPortletDataHandlerChoice)) {

				continue;
			}

			PreviewPortletDataHandlerChoice previewPortletDataHandlerChoice =
				(PreviewPortletDataHandlerChoice)
					previewPortletDataHandlerControl;

			for (Choice choice : previewPortletDataHandlerChoice.getChoices()) {
				if (name.equals(choice.getName())) {
					return choice;
				}
			}
		}

		return null;
	}

	private PreviewPortletDataHandler _getPreviewPortletDataHandler(
		ExportPreview exportPreview, String name) {

		for (PreviewPortletDataHandlerSection previewPortletDataHandlerSection :
				exportPreview.getPreviewPortletDataHandlerSections()) {

			for (PreviewPortletDataHandler previewPortletDataHandler :
					previewPortletDataHandlerSection.
						getPreviewPortletDataHandlers()) {

				if (name.equals(previewPortletDataHandler.getName())) {
					return previewPortletDataHandler;
				}
			}
		}

		return null;
	}

	private PreviewPortletDataHandlerSection
		_getPreviewPortletDataHandlerSection(
			ExportPreview exportPreview, String name) {

		for (PreviewPortletDataHandlerSection previewPortletDataHandlerSection :
				exportPreview.getPreviewPortletDataHandlerSections()) {

			if (name.equals(previewPortletDataHandlerSection.getName())) {
				return previewPortletDataHandlerSection;
			}
		}

		return null;
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

		_addObjectEntry(groupId, new Date(), objectDefinition);
		_addObjectEntry(
			groupId, new Date(System.currentTimeMillis() - (25 * Time.HOUR)),
			objectDefinition);

		return objectDefinition;
	}

	private void _testGetExportPreviewWithDateFilter(
			ObjectDefinition objectDefinition,
			UnsafeBiFunction<Date, Date, ExportPreview, Exception>
				unsafeBiFunction)
		throws Exception {

		long now = System.currentTimeMillis();

		String portletId = objectDefinition.getPortletId();

		Assert.assertEquals(
			2L,
			_getAdditionCount(unsafeBiFunction.apply(null, null), portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(new Date(now - (24 * Time.HOUR)), null),
				portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(null, new Date(now - (24 * Time.HOUR))),
				portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(now - (24 * Time.HOUR)), new Date(now)),
				portletId));
		Assert.assertEquals(
			2L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(now - (48 * Time.HOUR)), new Date(now)),
				portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(now - (26 * Time.HOUR)),
					new Date(now - (24 * Time.HOUR))),
				portletId));
		Assert.assertEquals(
			0L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(now - (3 * Time.DAY)),
					new Date(now - (2 * Time.DAY))),
				portletId));
	}

	@TestInfo({"LPD-37317", "LPD-90359"})
	private void _testGetExportPreviewWithDeletions() throws Exception {
		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			List<ObjectEntry> objectEntries =
				_objectEntryLocalService.getObjectEntries(
					GroupConstants.DEFAULT_PARENT_GROUP_ID,
					objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				objectEntries.toString(), 2, objectEntries.size());

			_objectEntryLocalService.deleteObjectEntry(objectEntries.get(0));

			PreviewPortletDataHandler previewPortletDataHandler =
				_getPreviewPortletDataHandler(
					exportPreviewResource.getExportPreview(null, null),
					"PORTLET_DATA_" + objectDefinition.getPortletId());

			Assert.assertEquals(
				Long.valueOf(1), previewPortletDataHandler.getAdditionCount());
			Assert.assertEquals(
				Long.valueOf(1), previewPortletDataHandler.getDeletionCount());
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	private void _testGetExportPreviewWithDifferentScope(
		ExportPreview exportPreview, ObjectDefinition... objectDefinitions) {

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			Assert.assertNull(
				_getPreviewPortletDataHandler(
					exportPreview,
					"PORTLET_DATA_" + objectDefinition.getPortletId()));
		}
	}

	private void _testGetExportPreviewWithLayoutSet(
			UnsafeBiFunction<Date, Date, ExportPreview, Exception>
				unsafeBiFunction)
		throws Exception {

		LayoutTestUtil.addTypeContentLayout(testGroup, false, false);
		LayoutTestUtil.addTypeContentLayout(testGroup, true, false);
		LayoutTestUtil.addTypeContentLayout(testGroup, true, false);

		_layoutLocalService.deleteLayout(
			LayoutTestUtil.addTypeContentLayout(testGroup, false, false),
			ServiceContextTestUtil.getServiceContext());

		PreviewPortletDataHandler previewPortletDataHandler =
			_getPreviewPortletDataHandler(
				unsafeBiFunction.apply(null, null),
				"PORTLET_DATA_com_liferay_layout_admin_web_portlet_" +
					"LayoutSetLayoutsPortlet");

		Assert.assertNotNull(previewPortletDataHandler);

		Choice choice = _getChoice(
			previewPortletDataHandler, "publicLayoutPages");

		Assert.assertEquals(1L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(1L, GetterUtil.getLong(choice.getDeletionCount()));

		choice = _getChoice(previewPortletDataHandler, "privateLayoutPages");

		Assert.assertEquals(2L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(0L, GetterUtil.getLong(choice.getDeletionCount()));
	}

	private void _testGetPortletExportPreview(
		ExportPreview exportPreview, String portletId) {

		PreviewPortletDataHandlerSection previewPortletDataHandlerSection =
			_getPreviewPortletDataHandlerSection(
				exportPreview, ExportImportConstants.SECTION_KEY_CONTENT);

		PreviewPortletDataHandler[] previewPortletDataHandlers =
			previewPortletDataHandlerSection.getPreviewPortletDataHandlers();

		Assert.assertEquals(
			Arrays.toString(previewPortletDataHandlers), 1,
			previewPortletDataHandlers.length);
		Assert.assertEquals(
			"PORTLET_DATA_" + portletId,
			previewPortletDataHandlers[0].getName());

		previewPortletDataHandlerSection = _getPreviewPortletDataHandlerSection(
			exportPreview, ExportImportConstants.SECTION_KEY_CONFIGURATION);

		previewPortletDataHandlers =
			previewPortletDataHandlerSection.getPreviewPortletDataHandlers();

		Assert.assertEquals(
			Arrays.toString(previewPortletDataHandlers), 1,
			previewPortletDataHandlers.length);
		Assert.assertEquals(
			"PORTLET_CONFIGURATION_" + portletId,
			previewPortletDataHandlers[0].getName());
		Assert.assertTrue(
			ArrayUtil.isNotEmpty(
				previewPortletDataHandlers[0].
					getPreviewPortletDataHandlerControls()));
	}

	private ObjectDefinition _companyObjectDefinition;
	private ObjectDefinition _depotObjectDefinition;
	private ExportPreviewResource _exportPreviewResource;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectDefinition _siteObjectDefinition;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}