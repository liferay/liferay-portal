/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
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
import com.liferay.portal.kernel.util.Validator;
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

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Juanjo Fernández
 * @author Daniel Sanz
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-34594"), @FeatureFlag("LPS-164563")}
)
@RunWith(Arquillian.class)
public class SaveDataSetUserConfigurationMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			SaveDataSetUserConfigurationMVCResourceCommandTest.class);

		_dataSetSnapshotObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", TestPropsValues.getCompanyId());

		_user = UserTestUtil.addUser();

		_dataSetSnapshotObjectEntry = _addDataSetSnapshotObjectEntry(
			_user.getUserId());

		_dataSetUserConfigurationObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_USER_CONFIGURATION",
					TestPropsValues.getCompanyId());

		_dataSetUserConfigurationJSONObject = JSONUtil.put(
			"initialDataSetSnapshotERC",
			_dataSetSnapshotObjectEntry.getExternalReferenceCode());
	}

	@After
	public void tearDown() throws Exception {
		for (String externalReferenceCode : _externalReferenceCodes) {
			ObjectEntry objectEntry = _fetchDataSetUserConfigurationObjectEntry(
				externalReferenceCode);

			if (objectEntry != null) {
				_objectEntryLocalService.deleteObjectEntry(
					objectEntry.getObjectEntryId());
			}
		}

		_externalReferenceCodes.clear();

		_objectEntryLocalService.deleteObjectEntry(
			_dataSetSnapshotObjectEntry.getObjectEntryId());
	}

	@Test
	public void testServeResource() throws Exception {

		// Empty configuration JSON

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_OK,
			_serveResource(_jsonFactory.createJSONObject()));

		// Null value in configuration JSON

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_OK,
			_serveResource("{\"initialDataSetSnapshotERC\": null}"));

		// Owned data set snapshot

		_assertResponse(_serveResource());

		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			_user.getUserId());

		_assertResponse(
			JSONUtil.put(
				"initialDataSetSnapshotERC",
				objectEntry.getExternalReferenceCode()),
			HttpServletResponse.SC_OK, _serveResource(objectEntry));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());

		// Random entry in configuration JSON

		_assertResponse(
			_serveResource(
				JSONUtil.merge(
					_dataSetUserConfigurationJSONObject,
					JSONUtil.put(
						StringPool.AT + RandomTestUtil.randomString(),
						RandomTestUtil.randomString()))));

		// Shared data set snapshot

		objectEntry = _addDataSetSnapshotObjectEntry(
			TestPropsValues.getUserId());

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, _user.getUserId(),
			_classNameLocalService.getClassNameId(
				_dataSetSnapshotObjectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), TestPropsValues.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_assertResponse(
			JSONUtil.put(
				"initialDataSetSnapshotERC",
				objectEntry.getExternalReferenceCode()),
			HttpServletResponse.SC_OK, _serveResource(objectEntry));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testServeResourceWithError() throws Exception {

		// Blank configuration JSON

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(StringPool.BLANK));

		// Blank frontend data set name

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(
				StringPool.BLANK, _dataSetSnapshotObjectEntry, _user));

		// Guest user

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_FORBIDDEN,
			_serveResource(
				_FDS_NAME, _dataSetSnapshotObjectEntry,
				_userLocalService.getGuestUser(
					TestPropsValues.getCompanyId())));

		// Malformed configuration JSON

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(RandomTestUtil.randomString()));

		// Null configuration JSON

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_BAD_REQUEST, _serveResource((String)null));

		// Unknown data set snapshot

		_assertResponse(_serveResource());

		_assertResponse(
			HttpServletResponse.SC_BAD_REQUEST,
			_serveResource(
				JSONUtil.put(
					"initialDataSetSnapshotERC",
					RandomTestUtil.randomString())));

		// User cannot access the data set snapshot

		_assertResponse(_serveResource());

		ObjectEntry objectEntry = _addDataSetSnapshotObjectEntry(
			TestPropsValues.getUserId());

		_assertResponse(
			HttpServletResponse.SC_FORBIDDEN, _serveResource(objectEntry));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	private ObjectEntry _addDataSetSnapshotObjectEntry(long userId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, userId, _dataSetSnapshotObjectDefinition.getObjectDefinitionId(),
			0, null,
			HashMapBuilder.<String, Serializable>put(
				"fdsName", _FDS_NAME
			).put(
				"label", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), userId));
	}

	private void _assertResponse(
			int statusCode,
			TestMockLiferayResourceResponse testMockLiferayResourceResponse)
		throws Exception {

		_assertResponse(
			_jsonFactory.createJSONObject(), statusCode,
			testMockLiferayResourceResponse);
	}

	private void _assertResponse(
			JSONObject jsonObject, int statusCode,
			TestMockLiferayResourceResponse testMockLiferayResourceResponse)
		throws Exception {

		Assert.assertEquals(
			String.valueOf(statusCode),
			testMockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				testMockLiferayResourceResponse.getPortletOutputStream();

		JSONAssert.assertEquals(
			_jsonFactory.createJSONObject(
				byteArrayOutputStream.toString()
			).toString(),
			jsonObject.toString(), JSONCompareMode.STRICT);

		if (jsonObject.length() != 0) {
			ObjectEntry objectEntry = _fetchDataSetUserConfigurationObjectEntry(
				_getDataSetUserConfigurationObjectEntryERC(_FDS_NAME, _user));

			Map<String, Serializable> values = objectEntry.getValues();

			JSONAssert.assertEquals(
				_jsonFactory.createJSONObject(
					GetterUtil.getString(values.get("configuration"))
				).toString(),
				jsonObject.toString(), JSONCompareMode.STRICT);
		}
	}

	private void _assertResponse(
			TestMockLiferayResourceResponse testMockLiferayResourceResponse)
		throws Exception {

		_assertResponse(
			_dataSetUserConfigurationJSONObject, HttpServletResponse.SC_OK,
			testMockLiferayResourceResponse);
	}

	private ObjectEntry _fetchDataSetUserConfigurationObjectEntry(
		String externalReferenceCode) {

		return _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0,
			_dataSetUserConfigurationObjectDefinition.getObjectDefinitionId());
	}

	private String _getDataSetUserConfigurationObjectEntryERC(
		String fdsName, User user) {

		return user.getExternalReferenceCode() + StringPool.UNDERLINE + fdsName;
	}

	private TestMockLiferayResourceResponse _serveResource() throws Exception {
		return _serveResource(_FDS_NAME, _dataSetSnapshotObjectEntry, _user);
	}

	private TestMockLiferayResourceResponse _serveResource(
			JSONObject jsonObject)
		throws Exception {

		return _serveResource(jsonObject.toString());
	}

	private TestMockLiferayResourceResponse _serveResource(
			ObjectEntry objectEntry)
		throws Exception {

		return _serveResource(_FDS_NAME, objectEntry, _user);
	}

	private TestMockLiferayResourceResponse _serveResource(
			String configurationJSON)
		throws Exception {

		return _serveResource(configurationJSON, _FDS_NAME, _user);
	}

	private TestMockLiferayResourceResponse _serveResource(
			String fdsName, ObjectEntry objectEntry, User user)
		throws Exception {

		return _serveResource(
			JSONUtil.put(
				"initialDataSetSnapshotERC",
				objectEntry.getExternalReferenceCode()
			).toString(),
			fdsName, user);
	}

	private TestMockLiferayResourceResponse _serveResource(
			String configurationJSON, String fdsName, User user)
		throws Exception {

		TestMockLiferayResourceResponse testMockLiferayResourceResponse =
			new TestMockLiferayResourceResponse();

		if (!Validator.isBlank(fdsName)) {
			_externalReferenceCodes.add(
				_getDataSetUserConfigurationObjectEntryERC(fdsName, user));
		}

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

		if (configurationJSON != null) {
			mockHttpServletRequest.setParameter(
				"configuration", configurationJSON);
		}

		mockHttpServletRequest.setParameter("fdsName", fdsName);

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, testMockLiferayResourceResponse);

			return testMockLiferayResourceResponse;
		}
	}

	private static final String _FDS_NAME = RandomTestUtil.randomString();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _dataSetSnapshotObjectDefinition;

	@DeleteAfterTestRun
	private ObjectEntry _dataSetSnapshotObjectEntry;

	private JSONObject _dataSetUserConfigurationJSONObject;
	private ObjectDefinition _dataSetUserConfigurationObjectDefinition;
	private final Set<String> _externalReferenceCodes = new LinkedHashSet<>();

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "mvc.command.name=/frontend_data_set_admin/save_data_set_user_configuration"
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