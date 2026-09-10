/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerProfileToolObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		MCPServerTestUtil.processBatchEngineUnits();

		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_mcpServerProfileExternalReferenceCode =
			mcpServerProfileObjectEntry.getExternalReferenceCode();
	}

	@Test
	public void testOnBeforeCreate() throws Exception {

		// Every level below a restricted ancestor

		_assertRestrictFieldsFailure("actions,actions.get", "actions.get");
		_assertRestrictFieldsFailure(
			"actions,actions.get.method", "actions.get.method");
		_assertRestrictFieldsFailure(
			"actions.get.method,actions", "actions.get.method");

		// Blank names and surrounding whitespace never reach Vulcan intact

		_assertBlankRestrictFieldFailure("actions,,description", "");
		_assertBlankRestrictFieldFailure(
			"actions, description", " description");
		_assertBlankRestrictFieldFailure("actions ,description", "actions ");

		// A translator splitting on dots needs every segment to be a name

		_assertMalformedRestrictFieldFailure(
			"actions.,description", "actions.");
		_assertMalformedRestrictFieldFailure(
			".actions,description", ".actions");
		_assertMalformedRestrictFieldFailure(
			"actions..get,description", "actions..get");
		_assertMalformedRestrictFieldFailure(
			"actions get,description", "actions get");
		_assertMalformedRestrictFieldFailure(
			"actions;get,description", "actions;get");

		// The same field twice is not a canonical value

		AssertUtils.assertFailure(
			ModelListenerException.class,
			"jakarta.validation.ValidationException: Unable to restrict " +
				"field \"actions\" more than once",
			() -> _addMCPServerProfileToolObjectEntry(
				"actions,description,actions"));

		// Fields outside of the restricted subtree

		_addMCPServerProfileToolObjectEntry(
			"actions,actionsCount,creator.id,description");

		// The same tool twice in one profile, whatever it restricts

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				"jakarta.validation.ValidationException: Unable to add tool ",
				"\"getMCPServerProfilesPage\" from tool set ",
				"\"mcp-server-profiles\" to MCP server profile \"",
				_mcpServerProfileExternalReferenceCode, "\" more than once"),
			() -> _addMCPServerProfileToolObjectEntry("description"));

		// The same tool in another profile

		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

		MCPServerTestUtil.addMCPServerProfileToolObjectEntry(
			mcpServerProfileObjectEntry.getExternalReferenceCode(), null,
			"getMCPServerProfilesPage", "mcp-server-profiles");

		// Relationship alias keys are valid names

		MCPServerTestUtil.addMCPServerProfileToolObjectEntry(
			_mcpServerProfileExternalReferenceCode,
			"r_universityStudents_c_university.budget,name_i18n",
			"postMCPServerProfile", "mcp-server-profiles");
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		ObjectEntry mcpServerProfileToolObjectEntry =
			_addMCPServerProfileToolObjectEntry("creator.id");

		AssertUtils.assertFailure(
			ModelListenerException.class,
			"jakarta.validation.ValidationException: Unable to restrict " +
				"field \"creator.id\" because restricted field \"creator\" " +
					"already hides it",
			() -> MCPServerTestUtil.updateMCPServerProfileToolRestrictFields(
				mcpServerProfileToolObjectEntry, "creator,creator.id"));

		MCPServerTestUtil.updateMCPServerProfileToolRestrictFields(
			mcpServerProfileToolObjectEntry, "creator");

		Assert.assertEquals(
			"creator",
			MapUtil.getString(
				_objectEntryLocalService.getValues(
					mcpServerProfileToolObjectEntry.getObjectEntryId()),
				"restrictFields"));

		// Renaming a tool into one the profile already has

		ObjectEntry postMCPServerProfileToolObjectEntry =
			MCPServerTestUtil.addMCPServerProfileToolObjectEntry(
				_mcpServerProfileExternalReferenceCode, null,
				"postMCPServerProfile", "mcp-server-profiles");

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				"jakarta.validation.ValidationException: Unable to add tool ",
				"\"getMCPServerProfilesPage\" from tool set ",
				"\"mcp-server-profiles\" to MCP server profile \"",
				_mcpServerProfileExternalReferenceCode, "\" more than once"),
			() -> MCPServerTestUtil.updateMCPServerProfileToolObjectEntry(
				postMCPServerProfileToolObjectEntry,
				HashMapBuilder.<String, Serializable>put(
					"toolName", "getMCPServerProfilesPage"
				).build()));

		Assert.assertEquals(
			"postMCPServerProfile",
			MapUtil.getString(
				_objectEntryLocalService.getValues(
					postMCPServerProfileToolObjectEntry.getObjectEntryId()),
				"toolName"));
	}

	@Test
	public void testRestrictFieldsOnPatch() throws Exception {
		ObjectEntry mcpServerProfileToolObjectEntry =
			_addMCPServerProfileToolObjectEntry("creator.id,description");

		long objectEntryId = mcpServerProfileToolObjectEntry.getObjectEntryId();

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"restrictFields", "creator,creator.id"
				).toString(),
				"mcp/server-profile-tools/" + objectEntryId,
				Http.Method.PATCH));

		// Removing a restriction re-exposes the field

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"restrictFields", "description"
			).toString(),
			"mcp/server-profile-tools/" + objectEntryId, Http.Method.PATCH);

		Assert.assertEquals(
			"description", jsonObject.getString("restrictFields"));

		Assert.assertEquals(
			"description",
			MapUtil.getString(
				_objectEntryLocalService.getValues(objectEntryId),
				"restrictFields"));
	}

	@Test
	public void testRestrictFieldsOnPost() throws Exception {
		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				_getMCPServerProfileToolJSONObject(
					"creator,creator.id"
				).toString(),
				"mcp/server-profile-tools", Http.Method.POST));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getMCPServerProfileToolJSONObject(
				"creator,description"
			).toString(),
			"mcp/server-profile-tools", Http.Method.POST);

		Assert.assertEquals(
			"creator,description",
			MapUtil.getString(
				_objectEntryLocalService.getValues(jsonObject.getLong("id")),
				"restrictFields"));
	}

	private ObjectEntry _addMCPServerProfileToolObjectEntry(
			String restrictFields)
		throws Exception {

		return MCPServerTestUtil.addMCPServerProfileToolObjectEntry(
			_mcpServerProfileExternalReferenceCode, restrictFields,
			"getMCPServerProfilesPage", "mcp-server-profiles");
	}

	private void _assertBlankRestrictFieldFailure(
		String restrictFields, String restrictFieldName) {

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				"jakarta.validation.ValidationException: Unable to restrict ",
				"field \"", restrictFieldName, "\" because the name is blank ",
				"or has surrounding whitespace"),
			() -> _addMCPServerProfileToolObjectEntry(restrictFields));
	}

	private void _assertMalformedRestrictFieldFailure(
		String restrictFields, String restrictFieldName) {

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				"jakarta.validation.ValidationException: Unable to restrict ",
				"field \"", restrictFieldName, "\" because the name is not a ",
				"dotted path of letters, digits, and underscores"),
			() -> _addMCPServerProfileToolObjectEntry(restrictFields));
	}

	private void _assertRestrictFieldsFailure(
		String restrictFields, String restrictFieldName) {

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				"jakarta.validation.ValidationException: Unable to restrict ",
				"field \"", restrictFieldName, "\" because restricted field ",
				"\"actions\" already hides it"),
			() -> _addMCPServerProfileToolObjectEntry(restrictFields));
	}

	private JSONObject _getMCPServerProfileToolJSONObject(
		String restrictFields) {

		return JSONUtil.put(
			"r_mcpServerProfileToTools_l_mcpServerProfileERC",
			_mcpServerProfileExternalReferenceCode
		).put(
			"restrictFields", restrictFields
		).put(
			"toolName", "getMCPServerProfilesPage"
		).put(
			"toolSetName", "mcp-server-profiles"
		);
	}

	private String _mcpServerProfileExternalReferenceCode;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}