/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Juanjo Fernández
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-34594"), @FeatureFlag("LPS-164563")}
)
@RunWith(Arquillian.class)
public class SaveDataSetStartupSnapshotMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			SaveDataSetStartupSnapshotMVCResourceCommandTest.class);

		_dataSetSnapshotObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", TestPropsValues.getCompanyId());

		_dataSetStartupSnapshotObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_STARTUP_SNAPSHOT",
					TestPropsValues.getCompanyId());
	}

	@Test
	public void testServeResourceSavesStartupSnapshotForOwner()
		throws Exception {

		User user = TestPropsValues.getUser();

		String fdsName = RandomTestUtil.randomString();

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			fdsName, user.getUserId());

		JSONObject jsonObject = _serveResource(
			user, dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			fdsName);

		Assert.assertEquals(
			dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			jsonObject.getString("erc"));

		_assertStartupSnapshot(user, fdsName, dataSetSnapshotObjectEntry);
	}

	@Test
	public void testServeResourceSavesStartupSnapshotForSharedSnapshot()
		throws Exception {

		User user = UserTestUtil.addUser();

		_users.add(user);

		String fdsName = RandomTestUtil.randomString();

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			fdsName, TestPropsValues.getUserId());

		_shareDataSetSnapshot(dataSetSnapshotObjectEntry, user.getUserId());

		JSONObject jsonObject = _serveResource(
			user, dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			fdsName);

		Assert.assertEquals(
			dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			jsonObject.getString("erc"));

		_assertStartupSnapshot(user, fdsName, dataSetSnapshotObjectEntry);
	}

	@Test
	public void testServeResourceThrowsPrincipalExceptionWithoutAccess()
		throws Exception {

		User user = UserTestUtil.addUser();

		_users.add(user);

		String fdsName = RandomTestUtil.randomString();

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			fdsName, TestPropsValues.getUserId());

		try {
			_serveResource(
				user, dataSetSnapshotObjectEntry.getExternalReferenceCode(),
				fdsName);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(_hasCause(exception, PrincipalException.class));
		}

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				user.getExternalReferenceCode() + StringPool.UNDERLINE +
					fdsName,
				0,
				_dataSetStartupSnapshotObjectDefinition.
					getObjectDefinitionId()));
	}

	private ObjectEntry _addDataSetSnapshotObjectEntry(
			String fdsName, long userId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, userId, _dataSetSnapshotObjectDefinition.getObjectDefinitionId(),
			0, null,
			HashMapBuilder.<String, Serializable>put(
				"fdsName", fdsName
			).put(
				"label", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), userId));
	}

	private void _assertStartupSnapshot(
			User user, String fdsName, ObjectEntry dataSetSnapshotObjectEntry)
		throws Exception {

		ObjectEntry dataSetStartupSnapshotObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				user.getExternalReferenceCode() + StringPool.UNDERLINE +
					fdsName,
				0,
				_dataSetStartupSnapshotObjectDefinition.
					getObjectDefinitionId());

		Assert.assertNotNull(dataSetStartupSnapshotObjectEntry);

		Map<String, Serializable> values =
			dataSetStartupSnapshotObjectEntry.getValues();

		Assert.assertEquals(
			dataSetSnapshotObjectEntry.getObjectEntryId(),
			GetterUtil.getLong(
				values.get(_DATA_SET_SNAPSHOT_ID_OBJECT_FIELD_NAME)));
	}

	private boolean _hasCause(Throwable throwable, Class<?> clazz) {
		while (throwable != null) {
			if (clazz.isInstance(throwable)) {
				return true;
			}

			throwable = throwable.getCause();
		}

		return false;
	}

	private JSONObject _serveResource(
			User user, String dataSetSnapshotExternalReferenceCode,
			String fdsName)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"dataSetSnapshotExternalReferenceCode",
			dataSetSnapshotExternalReferenceCode);
		mockHttpServletRequest.setParameter("fdsName", fdsName);

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, mockLiferayResourceResponse);

			ByteArrayOutputStream byteArrayOutputStream =
				(ByteArrayOutputStream)
					mockLiferayResourceResponse.getPortletOutputStream();

			return JSONFactoryUtil.createJSONObject(
				byteArrayOutputStream.toString());
		}
	}

	private void _shareDataSetSnapshot(
			ObjectEntry dataSetSnapshotObjectEntry, long toUserId)
		throws Exception {

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, toUserId,
			_classNameLocalService.getClassNameId(
				_dataSetSnapshotObjectDefinition.getClassName()),
			dataSetSnapshotObjectEntry.getObjectEntryId(),
			TestPropsValues.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	private static final String _DATA_SET_SNAPSHOT_ID_OBJECT_FIELD_NAME =
		"r_dataSetSnapshotToStartupSnapshots_l_dataSetSnapshotId";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _dataSetSnapshotObjectDefinition;
	private ObjectDefinition _dataSetStartupSnapshotObjectDefinition;

	@Inject(
		filter = "mvc.command.name=/frontend_data_set_admin/save_data_set_startup_snapshot"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}