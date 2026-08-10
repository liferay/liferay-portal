/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class InfoItemUtilTest extends BaseUtilTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetMappedItemJSONObject() throws Exception {
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier = Mockito.mock(
			ClassPKInfoItemIdentifier.class);

		Mockito.when(
			classPKInfoItemIdentifier.getClassPK()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		InfoItemReference infoItemReference = mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			classPKInfoItemIdentifier
		);

		String externalReferenceCode = RandomTestUtil.randomString();
		String fieldKey = RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"className", JournalArticle.class.getName()
			).put(
				"classNameId", CLASS_NAME_ID
			).put(
				"classPK",
				String.valueOf(classPKInfoItemIdentifier.getClassPK())
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"fieldId", fieldKey
			).toString(),
			InfoItemUtil.getMappedItemJSONObject(
				JournalArticle.class.getName(), externalReferenceCode, fieldKey,
				infoItemServiceRegistry, null, SCOPE_GROUP_ID
			).toString());
	}

}