/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.rest.client.dto.v1_0.Choice;
import com.liferay.exportimport.rest.client.dto.v1_0.DateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportPreviewResource;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
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
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
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

	@Override
	@Test
	public void testGetAssetLibraryExportPreview() throws Exception {
		String externalReferenceCode =
			testDepotEntryGroup.getExternalReferenceCode();
		String portletId = _depotObjectDefinition.getPortletId();

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getAssetLibraryExportPreviewHttpResponse(
				externalReferenceCode, null, null, null, null, null));

		_testGetExportPreviewWithDateFilter(
			_depotObjectDefinition,
			(startDate, endDate) ->
				exportPreviewResource.getAssetLibraryExportPreview(
					externalReferenceCode,
					_toDateFilterType(startDate, endDate), endDate, null, null,
					startDate));
		_testGetExportPreviewWithDateFilter(
			_depotObjectDefinition,
			(startDate, endDate) ->
				exportPreviewResource.getAssetLibraryExportPreview(
					externalReferenceCode,
					_toDateFilterType(startDate, endDate), endDate, null,
					portletId, startDate));
		_testGetExportPreviewWithDifferentScope(
			exportPreviewResource.getAssetLibraryExportPreview(
				externalReferenceCode, null, null, null, null, null),
			_companyObjectDefinition, _siteObjectDefinition);

		_addPortletPreferences(
			testDepotEntryGroup.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_GROUP, portletId);

		_testGetPortletExportPreview(
			exportPreviewResource.getAssetLibraryExportPreview(
				externalReferenceCode, null, null, null, portletId, null),
			portletId);
	}

	@Override
	@Test
	public void testGetExportPreview() throws Exception {
		String portletId = _companyObjectDefinition.getPortletId();

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getExportPreviewHttpResponse(
				null, null, null, null, null));

		_testGetExportPreviewWithDateFilter(
			_companyObjectDefinition,
			(startDate, endDate) -> exportPreviewResource.getExportPreview(
				_toDateFilterType(startDate, endDate), endDate, null, null,
				startDate));
		_testGetExportPreviewWithDateFilter(
			_companyObjectDefinition,
			(startDate, endDate) -> exportPreviewResource.getExportPreview(
				_toDateFilterType(startDate, endDate), endDate, null, portletId,
				startDate));
		_testGetExportPreviewWithDeletions(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			() -> exportPreviewResource.getExportPreview(
				null, null, null, null, null));
		_testGetExportPreviewWithDifferentScope(
			exportPreviewResource.getExportPreview(
				null, null, null, null, null),
			_depotObjectDefinition, _siteObjectDefinition);

		_addPortletPreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY, portletId);

		_testGetPortletExportPreview(
			exportPreviewResource.getExportPreview(
				null, null, null, portletId, null),
			portletId);
	}

	@FeatureFlag("LPD-38869")
	@Override
	@Test
	public void testGetSiteExportPreview() throws Exception {
		String externalReferenceCode = testGroup.getExternalReferenceCode();
		String portletId = _siteObjectDefinition.getPortletId();

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getSiteExportPreviewHttpResponse(
				externalReferenceCode, null, null, null, null, null, null));

		_testGetExportPreviewWithDateFilter(
			_siteObjectDefinition,
			(startDate, endDate) -> exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, _toDateFilterType(startDate, endDate),
				endDate, null, null, null, startDate));
		_testGetExportPreviewWithDateFilter(
			_siteObjectDefinition,
			(startDate, endDate) -> exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, _toDateFilterType(startDate, endDate),
				endDate, null, portletId, null, startDate));
		_testGetExportPreviewWithDeletions(
			testGroup.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE,
			() -> exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, null, null, null, null, null, null));
		_testGetExportPreviewWithDifferentScope(
			exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, null, null, null, null, null, null),
			_companyObjectDefinition, _depotObjectDefinition);
		_testGetExportPreviewWithLayoutSet(
			(startDate, endDate) -> exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, _toDateFilterType(startDate, endDate),
				endDate, null, null, null, startDate));
		_testGetSiteExportPreviewWithFromLastPublishDateFilter(
			externalReferenceCode);
		_testGetSiteExportPreviewWithLayoutPageTemplateEntries();

		Layout layout = _addLayoutWithPortlet(testGroup, portletId);

		_testGetPortletExportPreview(
			exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, null, null, null, portletId,
				layout.getExternalReferenceCode(), null),
			portletId);
		_testGetSiteExportPreviewWithGroupPermissions(
			externalReferenceCode, portletId,
			layout.getExternalReferenceCode());

		_testGetSiteExportPreviewWithLayoutSetPrototypeGroup();
		_testGetSiteExportPreviewWithSitePage(
			externalReferenceCode, BlogsPortletKeys.BLOGS);
	}

	private LayoutPageTemplateCollection _addBasicLayoutPageTemplateCollection()
		throws Exception {

		return _layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(), null,
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(
			long layoutPageTemplateCollectionId, int type)
		throws Exception {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			testGroup.getGroupId(), layoutPageTemplateCollectionId, null, 0,
			null, RandomTestUtil.randomString(), type, 0, false, 0, 0, 0,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private Layout _addLayoutWithPortlet(Group group, String portletId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(
			layout, portletId,
			HashMapBuilder.put(
				RandomTestUtil.randomString(),
				new String[] {RandomTestUtil.randomString()}
			).build());

		return layout;
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

	private void _addPortletPreferences(
			long ownerId, int ownerType, String portletId)
		throws Exception {

		_portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				TestPropsValues.getCompanyId(), ownerId, ownerType,
				PortletKeys.PREFS_PLID_SHARED, portletId, null, null);
	}

	private long _getAdditionCount(
		ExportPreview exportPreview, String portletId) {

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			exportPreview, "PORTLET_DATA_" + portletId);

		if (portletDataHandler == null) {
			return 0L;
		}

		return portletDataHandler.getAdditionCount();
	}

	private Choice _getChoice(
		PortletDataHandler portletDataHandler, String name) {

		for (PortletDataHandlerControl portletDataHandlerControl :
				portletDataHandler.getPortletDataHandlerControls()) {

			if (!(portletDataHandlerControl instanceof
					PortletDataHandlerChoice)) {

				continue;
			}

			PortletDataHandlerChoice portletDataHandlerChoice =
				(PortletDataHandlerChoice)portletDataHandlerControl;

			for (Choice choice : portletDataHandlerChoice.getChoices()) {
				if (name.equals(choice.getName())) {
					return choice;
				}
			}
		}

		return null;
	}

	private PortletDataHandler _getPortletDataHandler(
		ExportPreview exportPreview, String name) {

		for (PortletDataHandler portletDataHandler :
				exportPreview.getPortletDataHandlers()) {

			if (name.equals(portletDataHandler.getName())) {
				return portletDataHandler;
			}
		}

		return null;
	}

	private PortletDataHandlerBoolean _getPortletDataHandlerBoolean(
		PortletDataHandler portletDataHandler, String name) {

		for (PortletDataHandlerControl portletDataHandlerControl :
				portletDataHandler.getPortletDataHandlerControls()) {

			if (name.equals(portletDataHandlerControl.getName())) {
				return (PortletDataHandlerBoolean)portletDataHandlerControl;
			}
		}

		return null;
	}

	private PortletDataHandler[] _getPortletDataHandlers(
		ExportPreview exportPreview, String sectionKey) {

		return ArrayUtil.filter(
			exportPreview.getPortletDataHandlers(),
			portletDataHandler -> sectionKey.equals(
				portletDataHandler.getSectionKey()));
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

		long time = System.currentTimeMillis();

		String portletId = objectDefinition.getPortletId();

		Assert.assertEquals(
			2L,
			_getAdditionCount(unsafeBiFunction.apply(null, null), portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(new Date(time - (24 * Time.HOUR)), null),
				portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(null, new Date(time - (24 * Time.HOUR))),
				portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(time - (24 * Time.HOUR)), new Date(time)),
				portletId));
		Assert.assertEquals(
			2L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(time - (48 * Time.HOUR)), new Date(time)),
				portletId));
		Assert.assertEquals(
			1L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(time - (26 * Time.HOUR)),
					new Date(time - (24 * Time.HOUR))),
				portletId));
		Assert.assertEquals(
			0L,
			_getAdditionCount(
				unsafeBiFunction.apply(
					new Date(time - (3 * Time.DAY)),
					new Date(time - (2 * Time.DAY))),
				portletId));
	}

	@TestInfo({"LPD-37317", "LPD-90359"})
	private void _testGetExportPreviewWithDeletions(
			long groupId, String scope,
			UnsafeSupplier<ExportPreview, Exception> unsafeSupplier)
		throws Exception {

		ObjectDefinition objectDefinition = _publishObjectDefinitionWithEntries(
			groupId, scope);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				groupId, objectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 2, objectEntries.size());

		_objectEntryLocalService.deleteObjectEntry(objectEntries.get(0));

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			unsafeSupplier.get(),
			"PORTLET_DATA_" + objectDefinition.getPortletId());

		Assert.assertEquals(
			Long.valueOf(1), portletDataHandler.getAdditionCount());
		Assert.assertEquals(
			Long.valueOf(1), portletDataHandler.getDeletionCount());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private void _testGetExportPreviewWithDifferentScope(
		ExportPreview exportPreview, ObjectDefinition... objectDefinitions) {

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			Assert.assertNull(
				_getPortletDataHandler(
					exportPreview,
					"PORTLET_DATA_" + objectDefinition.getPortletId()));
		}
	}

	private void _testGetExportPreviewWithLayoutSet(
			UnsafeBiFunction<Date, Date, ExportPreview, Exception>
				unsafeBiFunction)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			testGroup, false, false);

		long time = System.currentTimeMillis();

		layout.setModifiedDate(new Date(time - (2 * Time.DAY)));

		_layoutLocalService.updateLayout(layout);

		LayoutTestUtil.addTypeContentLayout(testGroup, false, false);
		LayoutTestUtil.addTypeContentLayout(testGroup, true, false);
		LayoutTestUtil.addTypeContentLayout(testGroup, true, false);

		_layoutLocalService.deleteLayout(
			LayoutTestUtil.addTypeContentLayout(testGroup, false, false),
			ServiceContextTestUtil.getServiceContext());

		String portletId = LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			unsafeBiFunction.apply(null, null), "PORTLET_DATA_" + portletId);

		Choice choice = _getChoice(portletDataHandler, "public-pages");

		Assert.assertEquals(2L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(1L, GetterUtil.getLong(choice.getDeletionCount()));

		choice = _getChoice(portletDataHandler, "private-pages");

		Assert.assertEquals(2L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(0L, GetterUtil.getLong(choice.getDeletionCount()));

		portletDataHandler = _getPortletDataHandler(
			unsafeBiFunction.apply(
				new Date(time - (3 * Time.DAY)), new Date(time - Time.DAY)),
			"PORTLET_DATA_" + portletId);

		choice = _getChoice(portletDataHandler, "public-pages");

		Assert.assertEquals(1L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(0L, GetterUtil.getLong(choice.getDeletionCount()));

		choice = _getChoice(portletDataHandler, "private-pages");

		Assert.assertEquals(0L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(0L, GetterUtil.getLong(choice.getDeletionCount()));

		portletDataHandler = _getPortletDataHandler(
			unsafeBiFunction.apply(new Date(time - Time.DAY), null),
			"PORTLET_DATA_" + portletId);

		choice = _getChoice(portletDataHandler, "public-pages");

		Assert.assertEquals(1L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(1L, GetterUtil.getLong(choice.getDeletionCount()));

		choice = _getChoice(portletDataHandler, "private-pages");

		Assert.assertEquals(2L, GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(0L, GetterUtil.getLong(choice.getDeletionCount()));
	}

	private void _testGetPortletExportPreview(
		ExportPreview exportPreview, String portletId) {

		PortletDataHandler[] portletDataHandlers = _getPortletDataHandlers(
			exportPreview, ExportImportConstants.SECTION_KEY_CONTENT);

		Assert.assertEquals(
			Arrays.toString(portletDataHandlers), 1,
			portletDataHandlers.length);
		Assert.assertEquals(
			"PORTLET_DATA_" + portletId, portletDataHandlers[0].getName());

		portletDataHandlers = _getPortletDataHandlers(
			exportPreview, ExportImportConstants.SECTION_KEY_CONFIGURATION);

		Assert.assertEquals(
			Arrays.toString(portletDataHandlers), 1,
			portletDataHandlers.length);
		Assert.assertEquals(
			"PORTLET_CONFIGURATION_" + portletId,
			portletDataHandlers[0].getName());
		Assert.assertTrue(
			ArrayUtil.isNotEmpty(
				portletDataHandlers[0].getPortletDataHandlerControls()));
	}

	private void _testGetSiteExportPreviewWithFromLastPublishDateFilter(
			String externalReferenceCode)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				exportPreviewResource.getSiteExportPreviewHttpResponse(
					externalReferenceCode, "FromLastPublishDate", null, null,
					null, null, null));
		}
	}

	@TestInfo("LPD-105790")
	private void _testGetSiteExportPreviewWithGroupPermissions(
			String externalReferenceCode, String portletId,
			String sitePageExternalReferenceCode)
		throws Exception {

		_userLocalService.addGroupUsers(
			testGroup.getGroupId(), new long[] {_user.getUserId()});

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getSiteExportPreviewHttpResponse(
				externalReferenceCode, null, null, null, portletId,
				sitePageExternalReferenceCode, null));

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		RoleTestUtil.addResourcePermission(
			_role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()),
			ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		RoleTestUtil.addResourcePermission(
			_role, _siteObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.VIEW);

		_testGetPortletExportPreview(
			_exportPreviewResource.getSiteExportPreview(
				externalReferenceCode, null, null, null, portletId,
				sitePageExternalReferenceCode, null),
			portletId);

		assertHttpResponseStatusCode(
			404,
			_exportPreviewResource.getSiteExportPreviewHttpResponse(
				externalReferenceCode, null, null, null, null, null, null));

		RoleTestUtil.addResourcePermission(
			_role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()),
			ActionKeys.EXPORT_IMPORT_LAYOUTS);

		assertHttpResponseStatusCode(
			200,
			_exportPreviewResource.getSiteExportPreviewHttpResponse(
				externalReferenceCode, null, null, null, null, null, null));
	}

	@TestInfo({"LPD-67433", "LPD-90359"})
	private void _testGetSiteExportPreviewWithLayoutPageTemplateEntries()
		throws Exception {

		LayoutPageTemplateCollection basicLayoutPageTemplateCollection =
			_addBasicLayoutPageTemplateCollection();

		long basicLayoutPageTemplateCollectionId =
			basicLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId();

		LayoutPageTemplateEntry basicLayoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				basicLayoutPageTemplateCollectionId,
				LayoutPageTemplateEntryTypeConstants.BASIC);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			_addLayoutPageTemplateEntry(
				basicLayoutPageTemplateCollectionId,
				LayoutPageTemplateEntryTypeConstants.BASIC));
		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			_addLayoutPageTemplateEntry(
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			exportPreviewResource.getSiteExportPreview(
				testGroup.getExternalReferenceCode(), null, null, null, null,
				null, null),
			"PORTLET_DATA_" +
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES);

		Assert.assertEquals(
			Long.valueOf(3), portletDataHandler.getAdditionCount());
		Assert.assertEquals(
			Long.valueOf(2), portletDataHandler.getDeletionCount());

		PortletDataHandlerBoolean basicPortletDataHandlerBoolean =
			_getPortletDataHandlerBoolean(
				portletDataHandler,
				StringBundler.concat(
					"_",
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
					"_", LayoutPageTemplateEntry.class.getName(), "-",
					LayoutPageTemplateEntryTypeConstants.BASIC));

		Assert.assertEquals(
			Long.valueOf(1), basicPortletDataHandlerBoolean.getAdditionCount());
		Assert.assertEquals(
			Long.valueOf(1), basicPortletDataHandlerBoolean.getDeletionCount());

		PortletDataHandlerBoolean masterLayoutPortletDataHandlerBoolean =
			_getPortletDataHandlerBoolean(
				portletDataHandler,
				StringBundler.concat(
					"_",
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
					"_", LayoutPageTemplateEntry.class.getName(), "-",
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		Assert.assertEquals(
			Long.valueOf(1),
			masterLayoutPortletDataHandlerBoolean.getAdditionCount());
		Assert.assertEquals(
			Long.valueOf(1),
			masterLayoutPortletDataHandlerBoolean.getDeletionCount());

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			basicLayoutPageTemplateEntry);
		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			masterLayoutPageTemplateEntry);
		_layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(
				basicLayoutPageTemplateCollection);
	}

	private void _testGetSiteExportPreviewWithLayoutSetPrototypeGroup()
		throws Exception {

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		Group group = _layoutSetPrototype.getGroup();

		LayoutTestUtil.addTypePortletLayout(group, true);

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			exportPreviewResource.getSiteExportPreview(
				group.getExternalReferenceCode(), null, null, null, null, null,
				null),
			"PORTLET_DATA_" + LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS);

		Assert.assertNull(_getChoice(portletDataHandler, "public-pages"));

		Choice choice = _getChoice(portletDataHandler, "private-pages");

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, true),
			GetterUtil.getLong(choice.getAdditionCount()));
		Assert.assertEquals(
			choice.getAdditionCount(), portletDataHandler.getAdditionCount());
	}

	private void _testGetSiteExportPreviewWithSitePage(
			String externalReferenceCode, String portletId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), testGroup.getGroupId(), layout);

		LayoutTestUtil.addPortletToLayout(
			layout, portletId,
			HashMapBuilder.put(
				"lfrScopeLayoutUuid", new String[] {layout.getUuid()}
			).put(
				"lfrScopeType", new String[] {"layout"}
			).build());

		assertHttpResponseStatusCode(
			200,
			exportPreviewResource.getSiteExportPreviewHttpResponse(
				externalReferenceCode, null, null, null, portletId,
				layout.getExternalReferenceCode(), null));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				exportPreviewResource.getSiteExportPreviewHttpResponse(
					externalReferenceCode, null, null, null, portletId,
					RandomTestUtil.randomString(), null));
		}
	}

	private String _toDateFilterType(Date startDate, Date endDate) {
		if ((startDate == null) && (endDate == null)) {
			return null;
		}

		return String.valueOf(DateFilter.Type.DATE_RANGE);
	}

	private ObjectDefinition _companyObjectDefinition;
	private ObjectDefinition _depotObjectDefinition;
	private ExportPreviewResource _exportPreviewResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private PortletPreferences _portletPreferences;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@DeleteAfterTestRun
	private Role _role;

	private ObjectDefinition _siteObjectDefinition;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}