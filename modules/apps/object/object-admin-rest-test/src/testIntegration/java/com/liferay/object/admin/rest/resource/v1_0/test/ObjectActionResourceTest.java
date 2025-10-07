/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.resource.v1_0.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;

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
public class ObjectActionResourceTest extends BaseObjectActionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition.getObjectDefinitionId());
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectAction() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectActionNotFound() {
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"label"};
	}

	@Override
	protected ObjectAction randomObjectAction() throws Exception {
		return new ObjectAction() {
			{
				active = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = RandomTestUtil.randomString();
				errorMessage = Collections.singletonMap(
					"en_US", RandomTestUtil.randomString());
				externalReferenceCode = RandomTestUtil.randomString();
				id = RandomTestUtil.randomLong();
				label = Collections.singletonMap(
					"en_US", RandomTestUtil.randomString());
				name = RandomTestUtil.randomString();
				objectActionExecutorKey =
					ObjectActionExecutorConstants.KEY_WEBHOOK;
				objectActionTriggerKey =
					ObjectActionTriggerConstants.KEY_STANDALONE;
				parameters = UnicodePropertiesBuilder.put(
					"secret", "standalone"
				).put(
					"url", "https://standalone.com"
				).build();
			}
		};
	}

	@Override
	protected ObjectAction testDeleteObjectAction_addObjectAction()
		throws Exception {

		return _addObjectAction();
	}

	@Override
	protected ObjectAction testGetObjectAction_addObjectAction()
		throws Exception {

		return _addObjectAction();
	}

	@Override
	protected ObjectAction
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				String externalReferenceCode, ObjectAction objectAction)
		throws Exception {

		return objectActionResource.
			postObjectDefinitionByExternalReferenceCodeObjectAction(
				externalReferenceCode, objectAction);
	}

	@Override
	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExternalReferenceCode()
		throws Exception {

		return _objectDefinition.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId()
		throws Exception {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected ObjectAction
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectActionsPageObjectDefinitionObjectAction_addObjectAction(
				String externalReferenceCode, ObjectAction objectAction)
		throws Exception {

		return objectActionResource.
			postObjectDefinitionByExternalReferenceCodeObjectAction(
				externalReferenceCode, objectAction);
	}

	@Override
	protected ObjectAction testGraphQLObjectAction_addObjectAction()
		throws Exception {

		return _addObjectAction();
	}

	@Override
	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectAction_getObjectDefinitionId()
		throws Exception {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected Long
			testGraphQLPostObjectDefinitionObjectAction_getObjectDefinitionId()
		throws Exception {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected ObjectAction testPatchObjectAction_addObjectAction()
		throws Exception {

		return _addObjectAction();
	}

	@Override
	protected ObjectAction
			testPostObjectDefinitionByExternalReferenceCodeObjectAction_addObjectAction(
				ObjectAction objectAction)
		throws Exception {

		return objectActionResource.
			postObjectDefinitionByExternalReferenceCodeObjectAction(
				_objectDefinition.getExternalReferenceCode(), objectAction);
	}

	@Override
	protected ObjectAction testPutObjectAction_addObjectAction()
		throws Exception {

		return _addObjectAction();
	}

	private ObjectAction _addObjectAction() throws Exception {
		return objectActionResource.postObjectDefinitionObjectAction(
			_objectDefinition.getObjectDefinitionId(), randomObjectAction());
	}

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}