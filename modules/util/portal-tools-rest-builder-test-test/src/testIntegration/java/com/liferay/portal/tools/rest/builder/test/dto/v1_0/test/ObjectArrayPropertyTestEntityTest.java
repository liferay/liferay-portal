/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.dto.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ObjectArrayPropertyTestEntitySerDes;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ObjectArrayPropertyTestEntity;

import java.util.Arrays;
import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectArrayPropertyTestEntityTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testToJSON() throws Exception {
		com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.
			ObjectArrayPropertyTestEntity objectArrayPropertyTestEntity =
				new com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.
					ObjectArrayPropertyTestEntity();

		objectArrayPropertyTestEntity.setObjectArrayProperty(_ARRAY);

		JSONAssert.assertEquals(
			_ARRAY_JSON,
			ObjectArrayPropertyTestEntitySerDes.toJSON(
				objectArrayPropertyTestEntity),
			true);
	}

	@Test
	public void testToString() throws Exception {
		ObjectArrayPropertyTestEntity objectArrayPropertyTestEntity =
			new ObjectArrayPropertyTestEntity();

		objectArrayPropertyTestEntity.setObjectArrayProperty(_ARRAY);

		JSONAssert.assertEquals(
			_ARRAY_JSON, objectArrayPropertyTestEntity.toString(), true);
	}

	private static final Object[] _ARRAY = {
		Collections.singletonMap("key", "value"), Arrays.asList("a", "b"),
		"quoted \"string\"", 1, true
	};

	private static final String _ARRAY_JSON =
		"{\"objectArrayProperty\": [{\"key\": \"value\"}, [\"a\", \"b\"], " +
			"\"quoted \\\"string\\\"\", 1, true]}";

}