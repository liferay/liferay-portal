/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ben Demetrius
 */
@RunWith(Arquillian.class)
public class ObjectEntryAuditModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_auditRouter = (AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
			_objectEntryModelListener, "_auditRouter",
			ProxyUtil.newProxyInstance(
				AuditRouter.class.getClassLoader(),
				new Class<?>[] {AuditRouter.class},
				(proxy, method, arguments) -> {
					_auditMessages.add((AuditMessage)arguments[0]);

					return null;
				}));

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectDefinition.setEnableObjectEntryHistory(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		_objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"description"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_objectEntryModelListener, "_auditRouter", _auditRouter);

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());
	}

	@Test
	public void testOnAfterCreateWithLocalizedValues() throws Exception {
		_addObjectEntry("en_US", "Description", "Descrição");

		_assertLocalizedValues(
			TreeMapBuilder.put(
				"description", "Description"
			).put(
				"description[pt_BR]", "Descrição"
			).build());
	}

	@Test
	public void testOnAfterCreateWithNondefaultEntryLanguageId()
		throws Exception {

		_addObjectEntry("pt_BR", "Description", "Descrição");

		_assertLocalizedValues(
			TreeMapBuilder.put(
				"description", "Descrição"
			).put(
				"description[en_US]", "Description"
			).build());
	}

	@Test
	public void testOnAfterRemoveWithLocalizedValues() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			"en_US", "Description", "Descrição");

		_auditMessages.clear();

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_assertLocalizedValues(
			TreeMapBuilder.put(
				"description", "Description"
			).put(
				"description[pt_BR]", "Descrição"
			).build());
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		_testOnAfterUpdateWithDefaultLocaleValue();
		_testOnAfterUpdateWithMultipleLocaleValues();
		_testOnAfterUpdateWithNondefaultLocaleValue();
	}

	private ObjectEntry _addObjectEntry(
			String defaultLanguageId, String enUSValue, String ptBRValue)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			defaultLanguageId, _getValues(enUSValue, ptBRValue),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertLocalizedValues(
		Map<String, String> expectedLocalizedValues) {

		Map<String, String> localizedValues = new TreeMap<>();

		JSONObject additionalInfoJSONObject = _pollAdditionalInfoJSONObject();

		for (String key : additionalInfoJSONObject.keySet()) {
			if (key.startsWith(_objectField.getName())) {
				localizedValues.put(
					key, additionalInfoJSONObject.getString(key));
			}
		}

		Assert.assertEquals(expectedLocalizedValues, localizedValues);
	}

	private void _assertModifiedAttributes(String... expectedAttributes) {
		List<String> modifiedAttributes = new ArrayList<>();

		JSONObject additionalInfoJSONObject = _pollAdditionalInfoJSONObject();

		JSONArray jsonArray = additionalInfoJSONObject.getJSONArray(
			"attributes");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			modifiedAttributes.add(
				StringBundler.concat(
					jsonObject.getString("name"), "=",
					jsonObject.getString("oldValue"), ">",
					jsonObject.getString("newValue")));
		}

		Assert.assertEquals(
			Arrays.asList(expectedAttributes), modifiedAttributes);
	}

	private Map<String, Serializable> _getValues(
		String enUSValue, String ptBRValue) {

		return HashMapBuilder.<String, Serializable>put(
			_objectField.getI18nObjectFieldName(),
			HashMapBuilder.put(
				"en_US", enUSValue
			).put(
				"pt_BR", ptBRValue
			).build()
		).build();
	}

	private JSONObject _pollAdditionalInfoJSONObject() {
		AuditMessage auditMessage = _auditMessages.poll();

		return auditMessage.getAdditionalInfo();
	}

	private void _testOnAfterUpdateWithDefaultLocaleValue() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			"en_US", "Description", "Descrição");

		_auditMessages.clear();

		_updateObjectEntry("Description 2", objectEntry, "Descrição");

		_assertModifiedAttributes("description=Description>Description 2");
	}

	private void _testOnAfterUpdateWithMultipleLocaleValues() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			"en_US", "Description", "Descrição");

		_auditMessages.clear();

		_updateObjectEntry("Description 2", objectEntry, "Descrição 2");

		_assertModifiedAttributes(
			"description=Description>Description 2",
			"description[pt_BR]=Descrição>Descrição 2");
	}

	private void _testOnAfterUpdateWithNondefaultLocaleValue()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			"en_US", "Description", "Descrição");

		_auditMessages.clear();

		_updateObjectEntry("Description", objectEntry, "Descrição 2");

		_assertModifiedAttributes("description[pt_BR]=Descrição>Descrição 2");
	}

	private void _updateObjectEntry(
			String enUSValue, ObjectEntry objectEntry, String ptBRValue)
		throws Exception {

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			_getValues(enUSValue, ptBRValue),
			ServiceContextTestUtil.getServiceContext());
	}

	private final Queue<AuditMessage> _auditMessages = new LinkedList<>();
	private AuditRouter _auditRouter;
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.model.listener.ObjectEntryModelListener"
	)
	private ModelListener<ObjectEntry> _objectEntryModelListener;

	private ObjectField _objectField;

}