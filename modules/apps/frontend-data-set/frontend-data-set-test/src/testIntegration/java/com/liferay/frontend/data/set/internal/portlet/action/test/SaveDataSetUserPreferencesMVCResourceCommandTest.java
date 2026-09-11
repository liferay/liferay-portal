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
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
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

import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

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
			ObjectEntry objectEntry = _fetchDataSetUserPreferencesObjectEntry(
				externalReferenceCode);

			if (objectEntry != null) {
				_objectEntryLocalService.deleteObjectEntry(
					objectEntry.getObjectEntryId());
			}
		}

		_externalReferenceCodes.clear();
	}

	@Test
	public void testBadRequestIsReturnedForBlankFDSName() throws Exception {
		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertStatusCode(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(
				_user, _dataSetSnapshotObjectEntry, StringPool.BLANK));

		_assertInitialDataSetSnapshotERCIsNotUpdated();
	}

	@Test
	public void testBadRequestIsReturnedForBlankPreferences() throws Exception {
		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertStatusCode(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(_user, StringPool.BLANK, _fdsName));

		_assertInitialDataSetSnapshotERCIsNotUpdated();
	}

	@Test
	public void testBadRequestIsReturnedForMalformedPreferences()
		throws Exception {

		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertStatusCode(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(_user, RandomTestUtil.randomString(), _fdsName));

		_assertInitialDataSetSnapshotERCIsNotUpdated();
	}

	@Test
	public void testBadRequestIsReturnedForMissingPreferences()
		throws Exception {

		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertStatusCode(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(_user, (String)null, _fdsName));

		_assertInitialDataSetSnapshotERCIsNotUpdated();
	}

	@Test
	public void testBadRequestIsReturnedForNullPreferences() throws Exception {
		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertStatusCode(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(_user, StringPool.NULL, _fdsName));

		_assertInitialDataSetSnapshotERCIsNotUpdated();
	}

	@Test
	public void testForbiddenIsReturnedForGuestUser() throws Exception {
		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertStatusCode(
			HttpServletResponse.SC_FORBIDDEN,
			_serveResource(
				_userLocalService.getGuestUser(TestPropsValues.getCompanyId()),
				_dataSetSnapshotObjectEntry, _fdsName));

		_assertInitialDataSetSnapshotERCIsNotUpdated();
	}

	@Test
	public void testForbiddenIsReturnedForNonowners() throws Exception {
		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, TestPropsValues.getUserId());

		TestMockLiferayResourceResponse testMockLiferayResourceResponse =
			_serveResource(_user, objectEntry, _fdsName);

		_assertStatusCode(
			HttpServletResponse.SC_FORBIDDEN, testMockLiferayResourceResponse);

		JSONObject jsonObject = _getResponseJSONObject(
			testMockLiferayResourceResponse);

		Assert.assertEquals(
			StringPool.BLANK,
			jsonObject.getString("initialDataSetSnapshotERC"));

		_assertInitialDataSetSnapshotERCIsNotUpdated();

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testInitialDataSetSnapshotERCIsCleared() throws Exception {

		// empty JSON object

		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertNotSaved(
			_serveResource(_user, JSONFactoryUtil.createJSONObject(), _fdsName),
			"initialDataSetSnapshotERC");

		// null value

		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		_assertNotSaved(
			_serveResource(
				_user, "{\"initialDataSetSnapshotERC\": null}", _fdsName),
			"initialDataSetSnapshotERC");
	}

	@Test
	public void testInitialDataSetSnapshotERCIsSavedForSharedSnapshot()
		throws Exception {

		ObjectEntry dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, TestPropsValues.getUserId());

		_shareDataSetSnapshot(dataSetSnapshotObjectEntry, _user.getUserId());

		_assertInitialDataSetSnapshotERCIsSaved(
			dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, dataSetSnapshotObjectEntry, _fdsName), _user);

		_objectEntryLocalService.deleteObjectEntry(
			dataSetSnapshotObjectEntry.getObjectEntryId());
	}

	@Test
	public void testInitialDataSetSnapshotERCIsUpdated() throws Exception {
		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, _dataSetSnapshotObjectEntry, _fdsName),
			_user);

		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			_fdsName, _user.getUserId());

		_assertInitialDataSetSnapshotERCIsSaved(
			objectEntry.getExternalReferenceCode(), _fdsName,
			_serveResource(_user, objectEntry, _fdsName), _user);

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testUnknownKeyIsNotSaved() throws Exception {
		String unknownKey = StringPool.AT + RandomTestUtil.randomString();

		TestMockLiferayResourceResponse testMockLiferayResourceResponse =
			_serveResource(
				_user,
				JSONUtil.put(
					unknownKey, RandomTestUtil.randomString()
				).put(
					"initialDataSetSnapshotERC",
					_dataSetSnapshotObjectEntry.getExternalReferenceCode()
				),
				_fdsName);

		_assertInitialDataSetSnapshotERCIsSaved(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(), _fdsName,
			testMockLiferayResourceResponse, _user);

		_assertNotSaved(testMockLiferayResourceResponse, unknownKey);
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

	private void _assertInitialDataSetSnapshotERCIsNotUpdated()
		throws Exception {

		JSONObject dataSetUserPreferencesJSONObject =
			_getDataSetUserPreferencesJSONObject(
				_getDataSetUserPreferencesObjectEntryERC(_fdsName, _user));

		Assert.assertEquals(
			_dataSetSnapshotObjectEntry.getExternalReferenceCode(),
			dataSetUserPreferencesJSONObject.getString(
				"initialDataSetSnapshotERC"));
	}

	private void _assertInitialDataSetSnapshotERCIsSaved(
			String externalReferenceCode, String fdsName,
			TestMockLiferayResourceResponse testMockLiferayResourceResponse,
			User user)
		throws Exception {

		_assertStatusCode(
			HttpServletResponse.SC_OK, testMockLiferayResourceResponse);

		JSONObject jsonObject = _getResponseJSONObject(
			testMockLiferayResourceResponse);

		Assert.assertEquals(
			externalReferenceCode,
			jsonObject.getString("initialDataSetSnapshotERC"));

		JSONObject dataSetUserPreferencesJSONObject =
			_getDataSetUserPreferencesJSONObject(
				_getDataSetUserPreferencesObjectEntryERC(fdsName, user));

		Assert.assertEquals(
			externalReferenceCode,
			dataSetUserPreferencesJSONObject.getString(
				"initialDataSetSnapshotERC"));
	}

	private void _assertNotSaved(
			TestMockLiferayResourceResponse testMockLiferayResourceResponse,
			String key)
		throws Exception {

		_assertStatusCode(
			HttpServletResponse.SC_OK, testMockLiferayResourceResponse);

		JSONObject jsonObject = _getResponseJSONObject(
			testMockLiferayResourceResponse);

		Assert.assertEquals(StringPool.BLANK, jsonObject.getString(key));

		JSONObject dataSetUserPreferencesJSONObject =
			_getDataSetUserPreferencesJSONObject(
				_getDataSetUserPreferencesObjectEntryERC(_fdsName, _user));

		Assert.assertEquals(
			StringPool.BLANK, dataSetUserPreferencesJSONObject.getString(key));
	}

	private void _assertStatusCode(
		int statusCode,
		TestMockLiferayResourceResponse testMockLiferayResourceResponse) {

		Assert.assertEquals(
			String.valueOf(statusCode),
			testMockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));
	}

	private ObjectEntry _fetchDataSetUserPreferencesObjectEntry(
		String externalReferenceCode) {

		return _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0,
			_dataSetUserPreferencesObjectDefinition.getObjectDefinitionId());
	}

	private JSONObject _getDataSetUserPreferencesJSONObject(
			String externalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = _fetchDataSetUserPreferencesObjectEntry(
			externalReferenceCode);

		Assert.assertNotNull(objectEntry);

		Map<String, Serializable> values = objectEntry.getValues();

		return JSONFactoryUtil.createJSONObject(
			GetterUtil.getString(values.get("preferences")));
	}

	private String _getDataSetUserPreferencesObjectEntryERC(
		String fdsName, User user) {

		return user.getExternalReferenceCode() + StringPool.UNDERLINE + fdsName;
	}

	private JSONObject _getResponseJSONObject(
			TestMockLiferayResourceResponse testMockLiferayResourceResponse)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				testMockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());
	}

	private TestMockLiferayResourceResponse _serveResource(
			User user, JSONObject dataSetUserPreferencesJSONObject,
			String fdsName)
		throws Exception {

		return _serveResource(
			user, dataSetUserPreferencesJSONObject.toString(), fdsName);
	}

	private TestMockLiferayResourceResponse _serveResource(
			User user, ObjectEntry dataSetSnapshotObjectEntry, String fdsName)
		throws Exception {

		return _serveResource(
			user,
			JSONUtil.put(
				"initialDataSetSnapshotERC",
				dataSetSnapshotObjectEntry.getExternalReferenceCode()),
			fdsName);
	}

	private TestMockLiferayResourceResponse _serveResource(
			User user, String preferences, String fdsName)
		throws Exception {

		_externalReferenceCodes.add(
			_getDataSetUserPreferencesObjectEntryERC(fdsName, user));

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

		if (preferences != null) {
			mockHttpServletRequest.setParameter("preferences", preferences);
		}

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		TestMockLiferayResourceResponse testMockLiferayResourceResponse =
			new TestMockLiferayResourceResponse();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, testMockLiferayResourceResponse);

			return testMockLiferayResourceResponse;
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

	@Inject
	private UserLocalService _userLocalService;

	private static class TestMockLiferayResourceResponse
		extends MockLiferayResourceResponse {

		@Override
		public String getProperty(String name) {
			return _properties.get(name);
		}

		@Override
		public void setProperty(String name, String value) {
			_properties.put(name, value);
		}

		private final Map<String, String> _properties = HashMapBuilder.put(
			ResourceResponse.HTTP_STATUS_CODE,
			String.valueOf(HttpServletResponse.SC_OK)
		).build();

	}

}