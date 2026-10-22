/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.jaxrs.container.response.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import java.io.Serializable;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@FeatureFlag("LPD-96666")
@RunWith(Arquillian.class)
public class PIMConnectorContainerResponseFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR,
					TestPropsValues.getCompanyId());

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"active", true
			).put(
				"key", "liferay-commerce"
			).put(
				"name", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		String restContextPath = _objectDefinition.getRESTContextPath();

		_restContextPath = restContextPath.substring(1);

		_endpoint = StringBundler.concat(
			_restContextPath, "/", _objectEntry.getObjectEntryId());
	}

	@After
	public void tearDown() throws Exception {
		_objectEntryLocalService.deleteObjectEntry(_objectEntry);
	}

	@Test
	public void testFilter() throws Exception {
		JSONObject jsonObject = _getJSONObject(_endpoint);

		Assert.assertEquals(
			"never-executed", jsonObject.getString("lastExecution"));
		Assert.assertFalse(jsonObject.has("lastExecutionDate"));
		Assert.assertFalse(jsonObject.getBoolean("mappingComplete"));

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.
				getDispatchTriggerByExternalReferenceCode(
					"L_PIM_CONNECTOR_" +
						_objectEntry.getExternalReferenceCode(),
					TestPropsValues.getCompanyId());

		_dispatchLogLocalService.addDispatchLog(
			TestPropsValues.getUserId(), dispatchTrigger.getDispatchTriggerId(),
			null, null, null, new Date(System.currentTimeMillis() - 1000),
			DispatchTaskStatus.SUCCESSFUL);

		jsonObject = _getJSONObject(_endpoint);

		Assert.assertEquals("success", jsonObject.getString("lastExecution"));
		Assert.assertTrue(jsonObject.has("lastExecutionDate"));

		_dispatchLogLocalService.addDispatchLog(
			TestPropsValues.getUserId(), dispatchTrigger.getDispatchTriggerId(),
			null, null, null, new Date(), DispatchTaskStatus.FAILED);

		Assert.assertEquals(
			"failed",
			_getJSONObject(
				_endpoint
			).getString(
				"lastExecution"
			));

		for (String channelFieldName : _REQUIRED_CHANNEL_FIELD_NAMES) {
			_addPIMConnectorFieldMappingObjectEntry(channelFieldName);
		}

		Assert.assertTrue(
			_getJSONObject(
				_endpoint
			).getBoolean(
				"mappingComplete"
			));

		jsonObject = _getJSONObject(_endpoint + "?fields=id,name");

		Assert.assertFalse(jsonObject.has("lastExecution"));
		Assert.assertFalse(jsonObject.has("mappingComplete"));

		jsonObject = _getJSONObject(
			StringBundler.concat(
				_restContextPath, "?filter=",
				URLCodec.encodeURL(
					StringBundler.concat(
						"id eq '", _objectEntry.getObjectEntryId(), "'"))));

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("failed", jsonObject.getString("lastExecution"));
		Assert.assertTrue(jsonObject.getBoolean("mappingComplete"));

		jsonObject = _getJSONObject(
			_endpoint + "/pimConnectorToPIMConnectorFieldMappings");

		jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(4, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertFalse(jsonObject.has("lastExecution"));
		Assert.assertFalse(jsonObject.has("mappingComplete"));
	}

	private ObjectEntry _addPIMConnectorFieldMappingObjectEntry(
			String channelFieldName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
					TestPropsValues.getCompanyId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
					_objectDefinition.getObjectDefinitionId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"channelFieldName", channelFieldName
			).put(
				objectField.getName(), _objectEntry.getObjectEntryId()
			).put(
				"priority", 0
			).put(
				"type", "fixedValue"
			).put(
				"value", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private JSONObject _getJSONObject(String endpoint) throws Exception {
		return HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.GET);
	}

	private static final String[] _REQUIRED_CHANNEL_FIELD_NAMES = {
		"catalogId", "name", "productType", "skus[].sku"
	};

	@Inject
	private DispatchLogLocalService _dispatchLogLocalService;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	private String _endpoint;
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private String _restContextPath;

}