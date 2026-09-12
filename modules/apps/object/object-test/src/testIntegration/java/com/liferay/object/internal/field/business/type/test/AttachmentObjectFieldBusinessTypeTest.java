/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class AttachmentObjectFieldBusinessTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetValueWithFileEntry() throws Exception {
		ObjectField objectField = _objectFieldLocalService.createObjectField(0);

		objectField.setName("attachment");

		long fileEntryId = RandomTestUtil.randomLong();

		FileEntry fileEntry = new FileEntry() {
			{
				setId(() -> fileEntryId);
				setMetadata(
					() -> {
						throw new AssertionError(
							"The metadata of the attachment was resolved");
					});
			}
		};

		ObjectFieldBusinessType objectFieldBusinessType =
			_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

		Assert.assertEquals(
			fileEntryId,
			objectFieldBusinessType.getValue(
				null, objectField, TestPropsValues.getUserId(),
				Collections.singletonMap("attachment", fileEntry)));
	}

	@Inject
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}