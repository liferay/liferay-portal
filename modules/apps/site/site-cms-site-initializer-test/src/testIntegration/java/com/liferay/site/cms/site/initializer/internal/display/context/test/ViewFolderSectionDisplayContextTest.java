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
import com.liferay.object.constants.ObjectEntryFolderConstants;
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

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
			null, httpServletRequest, new MockHttpServletResponse());

		Object viewFolderSectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewFolderSectionDisplayContext");

		Assert.assertNotNull(viewFolderSectionDisplayContext);

		return viewFolderSectionDisplayContext;
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

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewFolderJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private SharingEntryService _sharingEntryService;

	@Inject
	private UserLocalService _userLocalService;

}