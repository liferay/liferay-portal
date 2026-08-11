/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryPersistence;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
public class ClobEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Test
	public void test() {
		ClobEntry clobEntry1 = null;
		ClobEntry clobEntry2 = null;

		try {
			clobEntry1 = _addClobEntry("aaa");
			clobEntry2 = _addClobEntry("bbb");

			Assert.assertEquals(
				Arrays.asList(clobEntry2, clobEntry1),
				_clobEntryPersistence.findAll(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					OrderByComparatorFactoryUtil.create(
						"ClobEntry", "content", false)));
		}
		finally {
			if (clobEntry1 != null) {
				_clobEntryPersistence.remove(clobEntry1);
			}

			if (clobEntry2 != null) {
				_clobEntryPersistence.remove(clobEntry2);
			}
		}
	}

	private ClobEntry _addClobEntry(String content) {
		ClobEntry clobEntry = _clobEntryPersistence.create(
			RandomTestUtil.nextLong());

		clobEntry.setContent(content);

		return _clobEntryPersistence.update(clobEntry);
	}

	@Inject
	private ClobEntryPersistence _clobEntryPersistence;

}