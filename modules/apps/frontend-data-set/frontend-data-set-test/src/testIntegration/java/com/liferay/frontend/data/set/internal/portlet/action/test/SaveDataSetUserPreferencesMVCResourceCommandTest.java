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
import com.liferay.portal.kernel.json.JSONUtil;
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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Juanjo Fernández
 * @author Daniel Sanz
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-34594"), @FeatureFlag("LPS-164563")}
)
@RunWith(Arquillian.class)
public class SaveDataSetUserPreferencesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			SaveDataSetUserPreferencesMVCResourceCommandTest.class);

		_dataSetSnapshotObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", TestPropsValues.getCompanyId());

		_dataSetUserPreferencesObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_USER_PREFERENCES",
					TestPropsValues.getCompanyId());

		_fdsName = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser();

		_dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		for (String externalReferenceCode : _externalReferenceCodes) {
			ObjectEntry objectEntry = _fetchUserPreferencesObjectEntry(
				externalReferenceCode);

			if (objectEntry != null) {
				_objectEntryLocalService.deleteObjectEntry(
					objectEntry.getObjectEntryId());
			}
		}

		_externalReferenceCodes.clear();
	}

	@Test
	public void testPrincipalExceptionIsThrownForNonowners() throws Exception {
		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, TestPropsValues.getUserId());

		try {
			_serveResource(
				_user, objectEntry.getExternalReferenceCode(), _fdsName);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(_hasCause(exception, PrincipalException.class));
		}

		Assert.assertNull(
			_fetchUserPreferencesObjectEntry(
				_getUserPreferencesObjectEntryExternalReferenceCode(
					_fdsName, _user)));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testStartupSnapshotERCUserPreferenceIsCleared()
		throws Exception {

		// empty JSON object

		_assertStartupSnapshotERCUserPreference(
			_user, _fdsName, _dataSetSnapshotObjectEntry,
			_serveResource(
				_user, _dataSetSnapshotObjectEntry.getExternalReferenceCode(),
				_fdsName));

		_assertNoUserPreference(
			_serveResource(_user, JSONFactoryUtil.createJSONObject(), _fdsName),
			"startupSnapshotERC");

		// null value. Requires creating JSONObject from string representation

		_assertStartupSnapshotERCUserPreference(
			_user, _fdsName, _dataSetSnapshotObjectEntry,
			_serveResource(
				_user, _dataSetSnapshotObjectEntry.getExternalReferenceCode(),
				_fdsName));

		_assertNoUserPreference(
			_serveResource(
				_user,
				JSONFactoryUtil.createJSONObject(
					"{\"startupSnapshotERC\": null}"),
				_fdsName),
			"startupSnapshotERC");
	}

	@Test
	public void testStartupSnapshotERCUserPreferenceIsReplaced()
		throws Exception {

		_assertStartupSnapshotERCUserPreference(
			_user, _fdsName, _dataSetSnapshotObjectEntry,
			_serveResource(
				_user, _dataSetSnapshotObjectEntry.getExternalReferenceCode(),
				_fdsName));

		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, _user.getUserId());

		_assertStartupSnapshotERCUserPreference(
			_user, _fdsName, objectEntry,
			_serveResource(
				_user, objectEntry.getExternalReferenceCode(), _fdsName));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testStartupSnapshotERCUserPreferenceIsSavedForOwner()
		throws Exception {

		User user = TestPropsValues.getUser();

		String fdsName = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			fdsName, user.getUserId());

		_assertStartupSnapshotERCUserPreference(
			user, fdsName, objectEntry,
			_serveResource(
				user, objectEntry.getExternalReferenceCode(), fdsName));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testStartupSnapshotERCUserPreferenceIsSavedForSharedSnapshot()
		throws Exception {

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, TestPropsValues.getUserId());

		_shareDataSetSnapshot(dataSetSnapshotObjectEntry, _user.getUserId());

		_assertStartupSnapshotERCUserPreference(
			_user, _fdsName, dataSetSnapshotObjectEntry,
			_serveResource(
				_user, dataSetSnapshotObjectEntry.getExternalReferenceCode(),
				_fdsName));

		_objectEntryLocalService.deleteObjectEntry(
			dataSetSnapshotObjectEntry.getObjectEntryId());
	}

	@Test
	public void testUnknownUserPreferencesAreNotSaved() throws Exception {
		JSONObject jsonObject = _serveResource(
			_user,
			JSONUtil.put(
				"favoriteSnapshotERCs",
				JSONUtil.putAll(
					_dataSetSnapshotObjectEntry.getExternalReferenceCode())
			).put(
				"startupSnapshotERC",
				_dataSetSnapshotObjectEntry.getExternalReferenceCode()
			),
			_fdsName);

		_assertStartupSnapshotERCUserPreference(
			_user, _fdsName, _dataSetSnapshotObjectEntry, jsonObject);

		_assertNoUserPreference(jsonObject, "favoriteSnapshotERCs");
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

	private void _assertNoUserPreference(JSONObject jsonObject, String key)
		throws Exception {

		Assert.assertEquals(StringPool.BLANK, jsonObject.getString(key));

		JSONObject preferencesJSONObject = _getUserPreferencesJSONObject(
			_getUserPreferencesObjectEntryExternalReferenceCode(
				_fdsName, _user));

		Assert.assertEquals(
			StringPool.BLANK, preferencesJSONObject.getString(key));
	}

	private void _assertStartupSnapshotERCUserPreference(
			User user, String fdsName, ObjectEntry dataSetSnapshotObjectEntry,
			JSONObject jsonObject)
		throws Exception {

		Assert.assertEquals(
			dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			jsonObject.getString("startupSnapshotERC"));

		JSONObject preferencesJSONObject = _getUserPreferencesJSONObject(
			_getUserPreferencesObjectEntryExternalReferenceCode(fdsName, user));

		Assert.assertEquals(
			dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			preferencesJSONObject.getString("startupSnapshotERC"));
	}

	private ObjectEntry _fetchUserPreferencesObjectEntry(
		String externalReferenceCode) {

		return _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0,
			_dataSetUserPreferencesObjectDefinition.getObjectDefinitionId());
	}

	private JSONObject _getUserPreferencesJSONObject(
			String externalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = _fetchUserPreferencesObjectEntry(
			externalReferenceCode);

		Assert.assertNotNull(objectEntry);

		Map<String, Serializable> values = objectEntry.getValues();

		return JSONFactoryUtil.createJSONObject(
			GetterUtil.getString(values.get("preferences")));
	}

	private String _getUserPreferencesObjectEntryExternalReferenceCode(
		String fdsName, User user) {

		return user.getExternalReferenceCode() + StringPool.UNDERLINE + fdsName;
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
			User user, JSONObject preferencesJSONObject, String fdsName)
		throws Exception {

		_externalReferenceCodes.add(
			_getUserPreferencesObjectEntryExternalReferenceCode(fdsName, user));

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

		mockHttpServletRequest.setParameter("fdsName", fdsName);
		mockHttpServletRequest.setParameter(
			"preferences", preferencesJSONObject.toString());

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

	private JSONObject _serveResource(
			User user, String startupSnapshotERC, String fdsName)
		throws Exception {

		return _serveResource(
			user, JSONUtil.put("startupSnapshotERC", startupSnapshotERC),
			fdsName);
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

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _dataSetSnapshotObjectDefinition;

	@DeleteAfterTestRun
	private ObjectEntry _dataSetSnapshotObjectEntry;

	private ObjectDefinition _dataSetUserPreferencesObjectDefinition;
	private final Set<String> _externalReferenceCodes = new LinkedHashSet<>();
	private String _fdsName;

	@Inject(
		filter = "mvc.command.name=/frontend_data_set_admin/save_data_set_user_preferences"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}