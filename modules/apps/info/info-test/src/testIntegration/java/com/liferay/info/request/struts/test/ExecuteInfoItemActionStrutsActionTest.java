/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.request.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class ExecuteInfoItemActionStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			ScriptManagementConfigurationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.getAdminUser(_group.getCompanyId());

		ObjectDefinition objectDefinition = _addObjectDefinition();

		_classNameId = String.valueOf(
			_portal.getClassNameId(objectDefinition.getClassName()));

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_user.getUserId(), _group.getGroupId(),
			objectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_classPK = String.valueOf(objectEntry.getPrimaryKey());

		_errorMessageMap = LocalizedMapUtil.getLocalizedMap(
			RandomTestUtil.randomString());
		_fileName = _file.createTempFileName("action-executed-", "txt");
		_layout = _layoutLocalService.addLayout(
			null, _user.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT,
			UnicodePropertiesBuilder.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
			).buildString(),
			false, false, Collections.emptyMap(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), _user.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(), _errorMessageMap,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"script",
				"File file = new File('" + _fileName +
					"'); file.append('true');"
			).build(),
			false);
	}

	@After
	public void tearDown() {
		if (_fileName != null) {
			java.io.File file = new java.io.File(_fileName);

			file.delete();
		}
	}

	@Test
	public void testExecuteInfoItemActionFailureGuest() throws Exception {
		_user = _userLocalService.getGuestUser(_group.getCompanyId());

		_testExecuteInfoItemAction(false);
	}

	@Test
	public void testExecuteInfoItemActionSuccess() throws Exception {
		_testExecuteInfoItemAction(true);
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				_user.getUserId(), 0, null, false, false, true, false, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null,
				"control_panel.sites",
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), null);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				_user.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			_user.getUserId(), objectDefinition.getObjectDefinitionId());
	}

	private void _processEvents(
			UploadPortletRequest uploadPortletRequest,
			MockHttpServletResponse mockHttpServletResponse, User user)
		throws Exception {

		uploadPortletRequest.setAttribute(
			WebKeys.CURRENT_URL, "/portal/execute_info_item_action");
		uploadPortletRequest.setAttribute(WebKeys.USER, user);

		EventsProcessorUtil.process(
			PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
			PropsValues.SERVLET_SERVICE_EVENTS_PRE, uploadPortletRequest,
			mockHttpServletResponse);
	}

	private void _testExecuteInfoItemAction(boolean success) throws Exception {
		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null,
					HashMapBuilder.put(
						"classNameId", Collections.singletonList(_classNameId)
					).put(
						"classPK", Collections.singletonList(_classPK)
					).put(
						"fieldId",
						Collections.singletonList(
							ObjectAction.class.getSimpleName() +
								StringPool.UNDERLINE + _objectAction.getName())
					).put(
						"p_l_mode", Collections.singletonList(Constants.VIEW)
					).put(
						"plid",
						Collections.singletonList(
							String.valueOf(_layout.getPlid()))
					).build()),
				null, RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		_processEvents(uploadPortletRequest, mockHttpServletResponse, _user);

		_executeInfoItemActionStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		String responseBody = unsyncStringWriter.toString();

		if (success) {
			Assert.assertTrue(_file.exists(_fileName));
			Assert.assertEquals("{}", responseBody);
		}
		else {
			Assert.assertFalse(_file.exists(_fileName));

			JSONObject jsonObject = _jsonFactory.createJSONObject(responseBody);

			Assert.assertEquals(
				_errorMessageMap.get(LocaleUtil.getDefault()),
				jsonObject.get("error"));
		}
	}

	private String _classNameId;
	private String _classPK;
	private Map<Locale, String> _errorMessageMap;

	@Inject(
		filter = "component.name=com.liferay.info.internal.request.struts.ExecuteInfoItemActionStrutsAction"
	)
	private StrutsAction _executeInfoItemActionStrutsAction;

	@Inject
	private File _file;

	private String _fileName;

	@DeleteAfterTestRun
	private Group _group;

	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ObjectAction _objectAction;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}