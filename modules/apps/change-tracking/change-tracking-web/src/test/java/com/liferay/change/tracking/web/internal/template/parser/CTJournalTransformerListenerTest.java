/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.template.parser;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Brooke Dalton
 */
public class CTJournalTransformerListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnOutput() throws Exception {
		String src = StringBundler.concat(
			"<img src=\"", RandomTestUtil.randomString(),
			"previewCTCollectionId=", RandomTestUtil.randomLong(), "\"></img>");

		String output = _ctJournalTransformerListener.onOutput(
			src, "en",
			HashMapBuilder.put(
				"ct_collection_id",
				String.valueOf(CTConstants.CT_COLLECTION_ID_PRODUCTION)
			).build());

		Assert.assertTrue(
			output.contains(
				"previewCTCollectionId=" +
					CTConstants.CT_COLLECTION_ID_PRODUCTION));
	}

	private final CTJournalTransformerListener _ctJournalTransformerListener =
		new CTJournalTransformerListener();

}