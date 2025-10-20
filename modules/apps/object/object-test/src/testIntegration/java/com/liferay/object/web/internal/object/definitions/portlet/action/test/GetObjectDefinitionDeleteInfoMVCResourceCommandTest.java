/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Aquiles Duarte
 */
@RunWith(Arquillian.class)
public class GetObjectDefinitionDeleteInfoMVCResourceCommandTest
	extends BaseMVCResourceCommandTestCase {

	@Test
	public void testGetObjectDefinitionDeleteInfo() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(),
			objectDefinition.getObjectDefinitionId(), Collections.emptyMap());

		JSONObject jsonObject = getJSONObject(
			"objectDefinitionId",
			String.valueOf(objectDefinition.getObjectDefinitionId()));

		Assert.assertEquals(1, jsonObject.getInt("objectEntriesCount"));
	}

	@Override
	protected MVCResourceCommand getMVCResourceCommand() {
		return _mvcResourceCommand;
	}

	@Inject(
		filter = "mvc.command.name=/object_definitions/get_object_definition_delete_info"
	)
	private MVCResourceCommand _mvcResourceCommand;

}