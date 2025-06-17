/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.definition.groovy.script.use.ObjectDefinitionGroovyScriptUseSourceURLFactory;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.definition.groovy.script.use.WorkflowDefinitionGroovyScriptUseSourceURLFactory;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTabRegistry;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class GetGroovyScriptUsesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			ScriptManagementConfigurationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_mockLiferayResourceRequest = new MockLiferayResourceRequest();

		_mockLiferayResourceRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					ConfigurationAdminPortletKeys.SYSTEM_SETTINGS),
				null));
	}

	@Test
	public void testGetGroovyScriptUses() throws Exception {
		Company company1 = CompanyTestUtil.addCompany("company1.com");

		User user1 = UserTestUtil.addCompanyAdminUser(company1);

		ObjectDefinition objectDefinition1 = _createObjectDefinition(
			user1.getUserId());

		_createObjectDefinitionGroovyScriptUses(
			"company1", objectDefinition1, user1.getUserId());

		_createWorkflowDefinitions(
			company1.getCompanyId(), "company1", user1.getUserId());

		Company company2 = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User user2 = TestPropsValues.getUser();

		ObjectDefinition objectDefinition2 = _createObjectDefinition(
			user2.getUserId());

		_createObjectDefinitionGroovyScriptUses(
			"liferay", objectDefinition2, user2.getUserId());

		_createWorkflowDefinitions(
			company2.getCompanyId(), "liferay", user2.getUserId());

		Assert.assertEquals(
			JSONFactoryUtil.createJSONArray(
			).put(
				JSONUtil.put(
					"companyWebId", "company1.com"
				).put(
					"sourceName", "company1ActiveGroovyObjectAction"
				).put(
					"sourceURL",
					ObjectDefinitionGroovyScriptUseSourceURLFactory.create(
						company1, objectDefinition1.getObjectDefinitionId(),
						_portal, "actions")
				)
			).put(
				JSONUtil.put(
					"companyWebId", "company1.com"
				).put(
					"sourceName", "company1ActiveGroovyObjectValidation"
				).put(
					"sourceURL",
					ObjectDefinitionGroovyScriptUseSourceURLFactory.create(
						company1, objectDefinition1.getObjectDefinitionId(),
						_portal, "validations")
				)
			).put(
				JSONUtil.put(
					"companyWebId", "company1.com"
				).put(
					"sourceName", "company1PublishedGroovyWorkflowDefinition"
				).put(
					"sourceURL",
					WorkflowDefinitionGroovyScriptUseSourceURLFactory.create(
						company1, _portal,
						"company1PublishedGroovyWorkflowDefinition", 1,
						_workflowPortletTabRegistry)
				)
			).put(
				JSONUtil.put(
					"companyWebId", "company1.com"
				).put(
					"sourceName", "company1PublishedJavaWorkflowDefinition"
				).put(
					"sourceURL",
					WorkflowDefinitionGroovyScriptUseSourceURLFactory.create(
						company1, _portal,
						"company1PublishedJavaWorkflowDefinition", 1,
						_workflowPortletTabRegistry)
				)
			).put(
				JSONUtil.put(
					"companyWebId", "liferay.com"
				).put(
					"sourceName", "liferayActiveGroovyObjectAction"
				).put(
					"sourceURL",
					ObjectDefinitionGroovyScriptUseSourceURLFactory.create(
						company2, objectDefinition2.getObjectDefinitionId(),
						_portal, "actions")
				)
			).put(
				JSONUtil.put(
					"companyWebId", "liferay.com"
				).put(
					"sourceName", "liferayActiveGroovyObjectValidation"
				).put(
					"sourceURL",
					ObjectDefinitionGroovyScriptUseSourceURLFactory.create(
						company2, objectDefinition2.getObjectDefinitionId(),
						_portal, "validations")
				)
			).put(
				JSONUtil.put(
					"companyWebId", "liferay.com"
				).put(
					"sourceName", "liferayPublishedGroovyWorkflowDefinition"
				).put(
					"sourceURL",
					WorkflowDefinitionGroovyScriptUseSourceURLFactory.create(
						company2, _portal,
						"liferayPublishedGroovyWorkflowDefinition", 1,
						_workflowPortletTabRegistry)
				)
			).put(
				JSONUtil.put(
					"companyWebId", "liferay.com"
				).put(
					"sourceName", "liferayPublishedJavaWorkflowDefinition"
				).put(
					"sourceURL",
					WorkflowDefinitionGroovyScriptUseSourceURLFactory.create(
						company2, _portal,
						"liferayPublishedJavaWorkflowDefinition", 1,
						_workflowPortletTabRegistry)
				)
			).toString(),
			_getGroovyScriptUsesJSONArrayString());
	}

	private void _addObjectAction(
			boolean active, String label, String objectActionExecutorKey,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"secret", "onafteradd"
		).put(
			"url", "https://onafteradd.com"
		).build();

		if (Objects.equals(
				ObjectActionExecutorConstants.KEY_GROOVY,
				objectActionExecutorKey)) {

			unicodeProperties = UnicodePropertiesBuilder.put(
				"script", "println \"Hello World \""
			).build();
		}

		_objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), userId,
			objectDefinition.getObjectDefinitionId(), active, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(label),
			RandomTestUtil.randomString(), objectActionExecutorKey,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, unicodeProperties,
			false);
	}

	private void _addObjectValidationRule(
			boolean active, String engine, String label,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		String script = "isEmailAddress(textObjectField)";

		if (Objects.equals(
				ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY, engine)) {

			script = "println \"Hello World \"";
		}

		_objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, userId, objectDefinition.getObjectDefinitionId(),
			active, engine,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(label),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			false, Collections.emptyList());
	}

	private ObjectDefinition _createObjectDefinition(long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				userId, 0, null, false, false, true, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _createObjectDefinitionGroovyScriptUses(
			String companyName, ObjectDefinition objectDefinition, long userId)
		throws Exception {

		_addObjectAction(
			true, companyName + "ActiveGroovyObjectAction",
			ObjectActionExecutorConstants.KEY_GROOVY, objectDefinition, userId);
		_addObjectAction(
			true, companyName + "ActiveWebhookObjectAction",
			ObjectActionExecutorConstants.KEY_WEBHOOK, objectDefinition,
			userId);
		_addObjectAction(
			false, companyName + "InactiveGroovyObjectAction",
			ObjectActionExecutorConstants.KEY_GROOVY, objectDefinition, userId);

		_addObjectValidationRule(
			true, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
			companyName + "ActiveGroovyObjectValidation", objectDefinition,
			userId);
		_addObjectValidationRule(
			true, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			companyName + "ActiveDDMObjectValidation", objectDefinition,
			userId);
		_addObjectValidationRule(
			false, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
			companyName + "InactiveGroovyObjectValidation", objectDefinition,
			userId);
	}

	private void _createWorkflowDefinitions(
			long companyId, String companyName, long userId)
		throws Exception {

		_workflowDefinitionManager.deployWorkflowDefinition(
			null, companyId, userId,
			companyName + "PublishedGroovyWorkflowDefinition",
			companyName + "PublishedGroovyWorkflowDefinition",
			_getContentBytes("workflow-definition-2.json"));
		_workflowDefinitionManager.deployWorkflowDefinition(
			null, companyId, userId,
			companyName + "PublishedJavaWorkflowDefinition",
			companyName + "PublishedJavaWorkflowDefinition",
			_getContentBytes("workflow-definition-3.json"));
		_workflowDefinitionManager.deployWorkflowDefinition(
			null, companyId, userId,
			companyName + "PublishedWorkflowDefinition", StringUtil.randomId(),
			_getContentBytes("workflow-definition-1.json"));

		_workflowDefinitionManager.saveWorkflowDefinition(
			null, companyId, userId,
			companyName + "UnpublishedGroovyWorkflowDefinition",
			StringUtil.randomId(),
			_getContentBytes("workflow-definition-2.json"));
		_workflowDefinitionManager.saveWorkflowDefinition(
			null, companyId, userId,
			companyName + "UnpublishedJavaWorkflowDefinition",
			StringUtil.randomId(),
			_getContentBytes("workflow-definition-3.json"));
	}

	private byte[] _getContentBytes(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			StringBundler.concat(
				"com/liferay/portal/security/script/management/web/internal",
				"/portlet/action/test/dependencies/", fileName));

		String content = StringUtil.read(inputStream);

		return content.getBytes();
	}

	private String _getGroovyScriptUsesJSONArrayString() throws Exception {
		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return byteArrayOutputStream.toString();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private MockLiferayResourceRequest _mockLiferayResourceRequest;

	@Inject(filter = "mvc.command.name=/system_settings/get_groovy_script_uses")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowPortletTabRegistry _workflowPortletTabRegistry;

}