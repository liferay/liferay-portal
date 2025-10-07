/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class DateTimeObjectFieldBusinessTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectFieldBusinessType =
			_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME);
	}

	@Test
	public void testGetDisplayContextValue() throws Exception {
		Assert.assertEquals(
			"2025-08-04 09:10",
			_getDisplayContextValue("2025-08-04 09:10:11.1"));
		Assert.assertEquals(
			"2025-08-04 09:10",
			_getDisplayContextValue("2025-08-04 09:10:11.12"));
		Assert.assertEquals(
			"2025-08-04 09:10",
			_getDisplayContextValue("2025-08-04 09:10:11.123"));
	}

	private String _getDisplayContextValue(String value) throws Exception {
		return String.valueOf(
			_objectFieldBusinessType.getDisplayContextValue(
				new DateTimeObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					_OBJECT_FIELD_NAME
				).build(),
				TestPropsValues.getUserId(),
				Collections.singletonMap(_OBJECT_FIELD_NAME, value)));
	}

	private static final String _OBJECT_FIELD_NAME =
		RandomTestUtil.randomString();

	private ObjectFieldBusinessType _objectFieldBusinessType;

	@Inject
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

}