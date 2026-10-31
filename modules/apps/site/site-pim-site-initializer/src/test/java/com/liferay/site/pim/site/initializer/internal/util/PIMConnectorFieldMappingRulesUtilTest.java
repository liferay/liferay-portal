/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Balazs Breier
 */
public class PIMConnectorFieldMappingRulesUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_htmlParserUtilMockedStatic.close();
	}

	@Test
	public void testApplyRules() {
		_htmlParserUtilMockedStatic.when(
			() -> HtmlParserUtil.extractText("<p> Red <b>Shirt</b> </p>")
		).thenReturn(
			" Red Shirt "
		);

		Assert.assertEquals(
			"RED SHIRT",
			PIMConnectorFieldMappingRulesUtil.applyRules(
				_mockObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"stripHTMLTags", true
					).put(
						"textCase", "upper"
					).put(
						"trimWhitespace", true
					).build()),
				"<p> Red <b>Shirt</b> </p>"));
		Assert.assertEquals(
			"caf%C3%A9",
			PIMConnectorFieldMappingRulesUtil.applyRules(
				_mockObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"textCase", "lower"
					).put(
						"urlEncode", true
					).build()),
				"CafÉ"));
		Assert.assertEquals(
			"Fjord Lounge Chair with Solid Oak...",
			PIMConnectorFieldMappingRulesUtil.applyRules(
				_mockObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"truncateLength", 40
					).build()),
				"Fjord Lounge Chair with Solid Oak Frame and Matching " +
					"Footstool"));
		Assert.assertEquals(
			"abcdefghijklmnopqrstuvwxyz0123456789...",
			PIMConnectorFieldMappingRulesUtil.applyRules(
				_mockObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"truncateLength", 40
					).put(
						"urlEncode", true
					).build()),
				"abcdefghijklmnopqrstuvwxyz0123456789ééé"));
		Assert.assertNull(
			PIMConnectorFieldMappingRulesUtil.applyRules(
				_mockObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"stripHTMLTags", true
					).build()),
				null));
	}

	private ObjectEntry _mockObjectEntry(Map<String, Serializable> values) {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			values
		);

		return objectEntry;
	}

	private final MockedStatic<HtmlParserUtil> _htmlParserUtilMockedStatic =
		Mockito.mockStatic(HtmlParserUtil.class);

}