/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Arrays;

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
 * @author Juanjo Fernandez
 */
@FeatureFlag("LPS-164563")
@RunWith(Arquillian.class)
public class SystemFDSSerializerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(SystemFDSSerializerTest.class);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", TestPropsValues.getCompanyId());

		_dataSetSnapshotObjectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"fdsName", _FDS_NAME
			).put(
				"label", _LABEL
			).put(
				"viewConfig", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_memberUser = UserTestUtil.addUser();

		_userGroup = UserGroupTestUtil.addUserGroup();

		_userLocalService.addUserGroupUser(
			_userGroup.getUserGroupId(), _memberUser.getUserId());

		_sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _userGroup.getUserGroupId(),
			0,
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			_dataSetSnapshotObjectEntry.getObjectEntryId(), 0, true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testSerializeSnapshots() throws Exception {
		JSONArray jsonArray = _fdsSerializer.serializeSnapshots(
			_FDS_NAME, _getHttpServletRequest(_memberUser.getUserId()));

		JSONObject itemJSONObject = _getItemJSONObject(jsonArray);

		Assert.assertEquals(_LABEL, itemJSONObject.getString("label"));

		_nonmemberUser = UserTestUtil.addUser();

		jsonArray = _fdsSerializer.serializeSnapshots(
			_FDS_NAME, _getHttpServletRequest(_nonmemberUser.getUserId()));

		Assert.assertNull(_getItemJSONObject(jsonArray));
	}

	@Test
	public void testSerializeUserConfiguration() throws Exception {
		HttpServletRequest httpServletRequest = _getHttpServletRequest(
			_memberUser.getUserId());

		Assert.assertNull(
			_fdsSerializer.serializeUserConfiguration(
				_FDS_NAME, httpServletRequest));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_USER_CONFIGURATION",
					TestPropsValues.getCompanyId());

		// Malformed data set user configuration

		_dataSetUserConfigurationObjectEntry =
			_objectEntryLocalService.addOrUpdateObjectEntry(
				_memberUser.getExternalReferenceCode() + StringPool.UNDERLINE +
					_FDS_NAME,
				0, _memberUser.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"configuration", "{\"initialDataSetSnapshotERC\": "
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _memberUser.getUserId()));

		Assert.assertNull(
			_fdsSerializer.serializeUserConfiguration(
				_FDS_NAME, httpServletRequest));

		// Random entry in data set user configuration

		_dataSetUserConfigurationObjectEntry =
			_objectEntryLocalService.addOrUpdateObjectEntry(
				_memberUser.getExternalReferenceCode() + StringPool.UNDERLINE +
					_FDS_NAME,
				0, _memberUser.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"configuration",
					StringBundler.concat(
						"{\"", StringPool.AT + RandomTestUtil.randomString(),
						": ", RandomTestUtil.randomString())
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _memberUser.getUserId()));

		Assert.assertNull(
			_fdsSerializer.serializeUserConfiguration(
				_FDS_NAME, httpServletRequest));

		// Valid data set user configuration

		_dataSetUserConfigurationObjectEntry =
			_objectEntryLocalService.updateObjectEntry(
				_memberUser.getUserId(),
				_dataSetUserConfigurationObjectEntry.getObjectEntryId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"configuration",
					JSONUtil.put(
						"initialDataSetSnapshotERC",
						_dataSetSnapshotObjectEntry.getExternalReferenceCode()
					).toString()
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _memberUser.getUserId()));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"initialDataSetSnapshotERC",
				_dataSetSnapshotObjectEntry.getExternalReferenceCode()
			).toString(),
			_fdsSerializer.serializeUserConfiguration(
				_FDS_NAME, httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);
	}

	private HttpServletRequest _getHttpServletRequest(long userId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockHttpServletRequest.setAttribute(WebKeys.LOCALE, LocaleUtil.US);
		mockHttpServletRequest.setAttribute(WebKeys.USER_ID, userId);

		return mockHttpServletRequest;
	}

	private JSONObject _getItemJSONObject(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject groupJSONObject = jsonArray.getJSONObject(i);

			if (!groupJSONObject.getBoolean("headerVisible")) {
				continue;
			}

			JSONArray itemsJSONArray = groupJSONObject.getJSONArray("items");

			for (int j = 0; j < itemsJSONArray.length(); j++) {
				JSONObject itemJSONObject = itemsJSONArray.getJSONObject(j);

				if (itemJSONObject.getLong("id") ==
						_dataSetSnapshotObjectEntry.getObjectEntryId()) {

					return itemJSONObject;
				}
			}
		}

		return null;
	}

	private static final String _FDS_NAME = RandomTestUtil.randomString();

	private static final String _LABEL = RandomTestUtil.randomString();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _dataSetSnapshotObjectEntry;

	@DeleteAfterTestRun
	private ObjectEntry _dataSetUserConfigurationObjectEntry;

	@Inject(filter = "frontend.data.set.serializer.type=system")
	private FDSSerializer _fdsSerializer;

	@DeleteAfterTestRun
	private User _memberUser;

	@DeleteAfterTestRun
	private User _nonmemberUser;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private SharingEntry _sharingEntry;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserLocalService _userLocalService;

}