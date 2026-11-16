/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryService;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class ViewFolderSectionDisplayContextTest
	extends BaseDisplayContextTestCase {

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

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_contentsObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), depotEntry.getGroupId(),
				TestPropsValues.getUserId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					depotEntry.getGroupId()));

		_objectEntryFolder = _addObjectEntryFolder(
			depotEntry, rootObjectEntryFolder.getObjectEntryFolderId());

		_testCMSStructureObjectFolderContributor =
			new TestCMSStructureObjectFolderContributor(
				rootObjectEntryFolder.getExternalReferenceCode());

		Bundle bundle = FrameworkUtil.getBundle(
			ViewFolderSectionDisplayContextTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			CMSStructureObjectFolderContributor.class,
			_testCMSStructureObjectFolderContributor, null);
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetAdditionalProps() throws Exception {

		// No object entry folder in context

		Map<String, Object> additionalProps = _getAdditionalProps(
			getMockHttpServletRequest());

		Assert.assertFalse(
			additionalProps.toString(),
			additionalProps.containsKey("trashEnabled"));

		// Recycle Bin is enabled

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				null, depotEntry.getGroupId(), TestPropsValues.getUserId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), StringUtil.randomString()
				).build(),
				StringUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		_setTrashEnabled(depotEntry, true);

		additionalProps = _getAdditionalProps(
			getMockHttpServletRequest(objectEntryFolder));

		Assert.assertEquals(Boolean.TRUE, additionalProps.get("trashEnabled"));

		// Recycle Bin is not enabled

		_setTrashEnabled(depotEntry, false);

		additionalProps = _getAdditionalProps(
			getMockHttpServletRequest(objectEntryFolder));

		Assert.assertEquals(Boolean.FALSE, additionalProps.get("trashEnabled"));

		// Object entry folder with a CMS structure object folder contributor

		additionalProps = _getAdditionalProps(
			getMockHttpServletRequest(_objectEntryFolder));

		Assert.assertEquals(
			Boolean.FALSE, additionalProps.get("galleryViewEnabled"));
	}

	@Test
	public void testGetBreadcrumbProps() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		ObjectEntryFolder parentObjectEntryFolder = _addObjectEntryFolder(
			depotEntry, rootObjectEntryFolder.getObjectEntryFolderId());

		ObjectEntryFolder sharedObjectEntryFolder = _addObjectEntryFolder(
			depotEntry, parentObjectEntryFolder.getObjectEntryFolderId());

		ObjectEntryFolder childObjectEntryFolder = _addObjectEntryFolder(
			depotEntry, sharedObjectEntryFolder.getObjectEntryFolderId());

		Map<String, Object> breadcrumbProps = _getBreadcrumbProps(
			getMockHttpServletRequest(childObjectEntryFolder));

		Assert.assertEquals(
			breadcrumbProps.toString(), Boolean.FALSE,
			breadcrumbProps.get("hideSpace"));

		_assertBreadcrumbLabels(
			breadcrumbProps, group.getName(LocaleUtil.getDefault()),
			rootObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
			parentObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
			sharedObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
			childObjectEntryFolder.getLabel(LocaleUtil.getDefault()));

		User memberUser = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			depotEntry.getGroupId(), memberUser.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				memberUser)) {

			breadcrumbProps = _getBreadcrumbProps(
				getMockHttpServletRequest(childObjectEntryFolder, memberUser));

			Assert.assertEquals(
				breadcrumbProps.toString(), Boolean.FALSE,
				breadcrumbProps.get("hideSpace"));

			_assertBreadcrumbLabels(
				breadcrumbProps, group.getName(LocaleUtil.getDefault()),
				rootObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				parentObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				sharedObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				childObjectEntryFolder.getLabel(LocaleUtil.getDefault()));
		}

		User user = UserTestUtil.addUser();

		_sharingEntryService.addSharingEntry(
			null, 0, 0, user.getUserId(),
			portal.getClassNameId(ObjectEntryFolder.class.getName()),
			sharedObjectEntryFolder.getObjectEntryFolderId(),
			depotEntry.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			breadcrumbProps = _getBreadcrumbProps(
				getMockHttpServletRequest(childObjectEntryFolder, user));

			Assert.assertEquals(
				breadcrumbProps.toString(), Boolean.TRUE,
				breadcrumbProps.get("hideSpace"));

			_assertBreadcrumbLabels(
				breadcrumbProps,
				sharedObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				childObjectEntryFolder.getLabel(LocaleUtil.getDefault()));

			breadcrumbProps = _getBreadcrumbProps(
				getMockHttpServletRequest(sharedObjectEntryFolder, user));

			Assert.assertEquals(
				breadcrumbProps.toString(), Boolean.TRUE,
				breadcrumbProps.get("hideSpace"));

			_assertBreadcrumbLabels(
				breadcrumbProps,
				sharedObjectEntryFolder.getLabel(LocaleUtil.getDefault()));
		}

		_sharingEntryService.addSharingEntry(
			null, 0, 0, user.getUserId(),
			portal.getClassNameId(ObjectEntryFolder.class.getName()),
			parentObjectEntryFolder.getObjectEntryFolderId(),
			depotEntry.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			breadcrumbProps = _getBreadcrumbProps(
				getMockHttpServletRequest(childObjectEntryFolder, user));

			Assert.assertEquals(
				breadcrumbProps.toString(), Boolean.TRUE,
				breadcrumbProps.get("hideSpace"));

			_assertBreadcrumbLabels(
				breadcrumbProps,
				parentObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				sharedObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				childObjectEntryFolder.getLabel(LocaleUtil.getDefault()));
		}

		User cmsAdministratorUser = UserTestUtil.addCompanyUser(
			companyLocalService.getCompany(TestPropsValues.getCompanyId()),
			RoleConstants.CMS_ADMINISTRATOR);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				cmsAdministratorUser)) {

			breadcrumbProps = _getBreadcrumbProps(
				getMockHttpServletRequest(
					childObjectEntryFolder, cmsAdministratorUser));

			Assert.assertEquals(
				breadcrumbProps.toString(), Boolean.FALSE,
				breadcrumbProps.get("hideSpace"));

			_assertBreadcrumbLabels(
				breadcrumbProps, group.getName(LocaleUtil.getDefault()),
				rootObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				parentObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				sharedObjectEntryFolder.getLabel(LocaleUtil.getDefault()),
				childObjectEntryFolder.getLabel(LocaleUtil.getDefault()));
		}
	}

	@Test
	public void testGetBulkActionDropdownItems() throws Exception {
		Assert.assertNotSame(
			_testCMSStructureObjectFolderContributor.getBulkActionDropdownItems(
				null),
			_invoke("getBulkActionDropdownItems", _contentsObjectEntryFolder));
		Assert.assertSame(
			_testCMSStructureObjectFolderContributor.getBulkActionDropdownItems(
				null),
			_invoke("getBulkActionDropdownItems", _objectEntryFolder));
	}

	@Test
	public void testGetCMSSiteInitializerFDSName() throws Exception {
		Assert.assertEquals(
			"com.liferay.site.cms.site.initializer-viewContentsFolder",
			_invoke(
				"getCMSSiteInitializerFDSName", _contentsObjectEntryFolder));
		Assert.assertEquals(
			_testCMSStructureObjectFolderContributor.getFDSName(),
			_invoke("getCMSSiteInitializerFDSName", _objectEntryFolder));
	}

	@Test
	public void testGetCreationMenuDropdownItems() throws Exception {
		Assert.assertNotSame(
			_testCMSStructureObjectFolderContributor.
				getCreationMenuDropdownItems(null, null),
			_invoke(
				"getCreationMenuDropdownItems", _contentsObjectEntryFolder));
		Assert.assertSame(
			_testCMSStructureObjectFolderContributor.
				getCreationMenuDropdownItems(null, null),
			_invoke("getCreationMenuDropdownItems", _objectEntryFolder));
	}

	@Test
	public void testGetEmptyState() throws Exception {
		Map<String, Object> emptyState = _invoke(
			"getEmptyState", _contentsObjectEntryFolder);

		Assert.assertEquals(
			"/states/cms_empty_state_content.svg", emptyState.get("image"));

		Assert.assertSame(
			_testCMSStructureObjectFolderContributor.getEmptyState(null),
			_invoke("getEmptyState", _objectEntryFolder));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		Assert.assertNotSame(
			_testCMSStructureObjectFolderContributor.getFDSActionDropdownItems(
				null),
			_invoke("getFDSActionDropdownItems", _contentsObjectEntryFolder));
		Assert.assertSame(
			_testCMSStructureObjectFolderContributor.getFDSActionDropdownItems(
				null),
			_invoke("getFDSActionDropdownItems", _objectEntryFolder));
	}

	@Test
	public void testGetObjectFolderExternalReferenceCodes() throws Exception {
		Assert.assertArrayEquals(
			new String[] {
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
			},
			_invoke(
				"getObjectFolderExternalReferenceCodes",
				_contentsObjectEntryFolder));
		Assert.assertArrayEquals(
			new String[] {
				_testCMSStructureObjectFolderContributor.
					getObjectFolderExternalReferenceCode()
			},
			_invoke(
				"getObjectFolderExternalReferenceCodes", _objectEntryFolder));
	}

	@Test
	public void testGetPropsTransformerModule() throws Exception {
		Assert.assertEquals(
			"{AssetsFDSPropsTransformer} from site-cms-site-initializer",
			_invoke("getPropsTransformerModule", _contentsObjectEntryFolder));
		Assert.assertEquals(
			_testCMSStructureObjectFolderContributor.
				getPropsTransformerModule(),
			_invoke("getPropsTransformerModule", _objectEntryFolder));
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			DepotEntry depotEntry, long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, depotEntry.getGroupId(), TestPropsValues.getUserId(),
			parentObjectEntryFolderId, RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId()));
	}

	private void _assertBreadcrumbLabels(
		Map<String, Object> breadcrumbProps, String... expectedLabels) {

		JSONArray jsonArray = (JSONArray)breadcrumbProps.get("breadcrumbItems");

		Assert.assertEquals(
			jsonArray.toString(), expectedLabels.length, jsonArray.length());

		for (int i = 0; i < expectedLabels.length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Assert.assertEquals(
				jsonArray.toString(), expectedLabels[i],
				jsonObject.getString("label"));
		}

		JSONObject lastJSONObject = jsonArray.getJSONObject(
			expectedLabels.length - 1);

		Assert.assertTrue(
			jsonArray.toString(), lastJSONObject.getBoolean("active"));
	}

	private Map<String, Object> _getAdditionalProps(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getDisplayContext(httpServletRequest), "getAdditionalProps",
			new Class<?>[0]);
	}

	private Map<String, Object> _getBreadcrumbProps(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getDisplayContext(httpServletRequest), "getBreadcrumbProps",
			new Class<?>[0]);
	}

	private Object _getDisplayContext(HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			fragmentRendererContext, httpServletRequest,
			new MockHttpServletResponse());

		Object viewFolderSectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewFolderSectionDisplayContext");

		Assert.assertNotNull(viewFolderSectionDisplayContext);

		return viewFolderSectionDisplayContext;
	}

	private <T> T _invoke(
			String methodName, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getDisplayContext(getMockHttpServletRequest(objectEntryFolder)),
			methodName, new Class<?>[0]);
	}

	private void _setTrashEnabled(DepotEntry depotEntry, boolean trashEnabled)
		throws Exception {

		Group group = depotEntry.getGroup();

		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			"trashEnabled", String.valueOf(trashEnabled));

		_groupLocalService.updateGroup(
			group.getGroupId(), unicodeProperties.toString());
	}

	private ObjectEntryFolder _contentsObjectEntryFolder;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewFolderJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private GroupLocalService _groupLocalService;

	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	private ServiceRegistration<CMSStructureObjectFolderContributor>
		_serviceRegistration;

	@Inject
	private SharingEntryService _sharingEntryService;

	private TestCMSStructureObjectFolderContributor
		_testCMSStructureObjectFolderContributor;

	@Inject
	private UserLocalService _userLocalService;

	private static class TestCMSStructureObjectFolderContributor
		implements CMSStructureObjectFolderContributor {

		@Override
		public List<DropdownItem> getBulkActionDropdownItems(
			HttpServletRequest httpServletRequest) {

			return _bulkActionDropdownItems;
		}

		@Override
		public List<DropdownItem> getCreationMenuDropdownItems(
			HttpServletRequest httpServletRequest,
			ObjectEntryFolder objectEntryFolder) {

			return _creationMenuDropdownItems;
		}

		@Override
		public Map<String, Object> getEmptyState(
			HttpServletRequest httpServletRequest) {

			return _emptyState;
		}

		@Override
		public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest) {

			return _fdsActionDropdownItems;
		}

		@Override
		public String getFDSName() {
			return _fdsName;
		}

		@Override
		public String getLabel() {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getObjectEntryFolderExternalReferenceCode() {
			return _objectEntryFolderExternalReferenceCode;
		}

		@Override
		public String getObjectFolderExternalReferenceCode() {
			return _objectFolderExternalReferenceCode;
		}

		@Override
		public String getPropsTransformerModule() {
			return _propsTransformerModule;
		}

		@Override
		public Map<String, List<String>> getSystemObjectFieldNames() {
			return Collections.emptyMap();
		}

		@Override
		public boolean isGalleryViewEnabled() {
			return false;
		}

		private TestCMSStructureObjectFolderContributor(
			String objectEntryFolderExternalReferenceCode) {

			_objectEntryFolderExternalReferenceCode =
				objectEntryFolderExternalReferenceCode;
		}

		private final List<DropdownItem> _bulkActionDropdownItems =
			new ArrayList<>();
		private final List<DropdownItem> _creationMenuDropdownItems =
			new ArrayList<>();
		private final Map<String, Object> _emptyState =
			HashMapBuilder.<String, Object>put(
				"title", RandomTestUtil.randomString()
			).build();
		private final List<FDSActionDropdownItem> _fdsActionDropdownItems =
			new ArrayList<>();
		private final String _fdsName = RandomTestUtil.randomString();
		private final String _objectEntryFolderExternalReferenceCode;
		private final String _objectFolderExternalReferenceCode =
			RandomTestUtil.randomString();
		private final String _propsTransformerModule =
			RandomTestUtil.randomString();

	}

}