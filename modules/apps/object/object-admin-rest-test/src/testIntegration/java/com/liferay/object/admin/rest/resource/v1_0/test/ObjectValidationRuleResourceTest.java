/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRuleSetting;
import com.liferay.object.admin.rest.resource.v1_0.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ObjectValidationRuleResourceTest
	extends BaseObjectValidationRuleResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition();

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Able")
			).name(
				"able"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_objectDefinitionLocalService.deleteObjectDefinition(
			_modifiableSystemObjectDefinition.getObjectDefinitionId());

		if (_objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition.getObjectDefinitionId());
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectValidationRule() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectValidationRuleNotFound() {
	}

	@Override
	@Test
	public void testPostObjectDefinitionObjectValidationRule()
		throws Exception {

		super.testPostObjectDefinitionObjectValidationRule();

		ObjectValidationRule randomSystemObjectValidationRule =
			randomObjectValidationRule();

		randomSystemObjectValidationRule.setObjectValidationRuleSettings(
			new ObjectValidationRuleSetting[] {
				new ObjectValidationRuleSetting() {
					{
						name =
							ObjectValidationRuleSettingConstants.
								NAME_ALLOW_ACTIVE_STATUS_UPDATE;
						value = "true";
					}
				}
			});
		randomSystemObjectValidationRule.setSystem(true);

		ObjectValidationRule postObjectValidationRule =
			objectValidationRuleResource.
				postObjectDefinitionObjectValidationRule(
					_modifiableSystemObjectDefinition.getObjectDefinitionId(),
					randomSystemObjectValidationRule);

		assertEquals(
			randomSystemObjectValidationRule, postObjectValidationRule);
		assertValid(postObjectValidationRule);
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected ObjectValidationRule randomObjectValidationRule()
		throws Exception {

		ObjectValidationRule objectValidationRule =
			super.randomObjectValidationRule();

		objectValidationRule.setActive(true);
		objectValidationRule.setEngine(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM);
		objectValidationRule.setErrorLabel(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));
		objectValidationRule.setName(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));
		objectValidationRule.setObjectDefinitionId(
			_objectDefinition.getObjectDefinitionId());
		objectValidationRule.setOutputType(
			ObjectValidationRule.OutputType.FULL_VALIDATION);
		objectValidationRule.setScript("isEmailAddress(able)");
		objectValidationRule.setSystem(false);

		return objectValidationRule;
	}

	@Override
	protected ObjectValidationRule
			testDeleteObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				_objectDefinition.getObjectDefinitionId(),
				randomObjectValidationRule());
	}

	@Override
	protected ObjectValidationRule
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				String objectDefinitionExternalReferenceCode,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				objectDefinitionExternalReferenceCode, objectValidationRule);
	}

	@Override
	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExternalReferenceCode()
		throws Exception {

		return _objectDefinition.getExternalReferenceCode();
	}

	@Override
	protected Long
		testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId() {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected ObjectValidationRule
			testGetObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				_objectDefinition.getObjectDefinitionId(),
				randomObjectValidationRule());
	}

	@Override
	protected ObjectValidationRule
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageObjectDefinitionObjectValidationRule_addObjectValidationRule(
				String objectDefinitionExternalReferenceCode,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				objectDefinitionExternalReferenceCode, objectValidationRule);
	}

	@Override
	protected ObjectValidationRule
			testGraphQLObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				_objectDefinition.getObjectDefinitionId(),
				randomObjectValidationRule());
	}

	@Override
	protected Long
		testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectValidationRule_getObjectDefinitionId(
			ObjectValidationRule objectValidationRule) {

		return objectValidationRule.getObjectDefinitionId();
	}

	@Override
	protected Long
		testGraphQLPostObjectDefinitionObjectValidationRule_getObjectDefinitionId(
			ObjectValidationRule objectValidationRule) {

		return objectValidationRule.getObjectDefinitionId();
	}

	@Override
	protected ObjectValidationRule
			testPatchObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				_objectDefinition.getObjectDefinitionId(),
				randomObjectValidationRule());
	}

	@Override
	protected ObjectValidationRule
			testPostObjectDefinitionByExternalReferenceCodeObjectValidationRule_addObjectValidationRule(
				ObjectValidationRule objectValidationRule)
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				_objectDefinition.getExternalReferenceCode(),
				objectValidationRule);
	}

	@Override
	protected ObjectValidationRule
			testPutObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				_objectDefinition.getObjectDefinitionId(),
				randomObjectValidationRule());
	}

	private ObjectDefinition _modifiableSystemObjectDefinition;
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}