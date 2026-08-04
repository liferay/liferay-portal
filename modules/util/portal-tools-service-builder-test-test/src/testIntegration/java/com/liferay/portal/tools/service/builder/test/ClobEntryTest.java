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
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryUtil;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() {
		_persistence = ClobEntryUtil.getPersistence();

		_clobEntry1 = _addClobEntry("aaa");
		_clobEntry2 = _addClobEntry("bbb");
	}

	@After
	public void tearDown() {
		_persistence.remove(_clobEntry1);
		_persistence.remove(_clobEntry2);
	}

	@Test
	public void test() {
		Assert.assertEquals(
			Arrays.asList(_clobEntry2, _clobEntry1),
			_persistence.findAll(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create(
					"ClobEntry", "content", false)));
	}

	private ClobEntry _addClobEntry(String content) {
		ClobEntry clobEntry = _persistence.create(RandomTestUtil.nextLong());

		clobEntry.setContent(content);

		return _persistence.update(clobEntry);
	}

	private ClobEntry _clobEntry1;
	private ClobEntry _clobEntry2;
	private ClobEntryPersistence _persistence;

}