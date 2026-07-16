/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.expando.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.search.test.util.ExpandoTableSearchFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
public class ExpandoBridgeIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		ExpandoTableSearchFixture expandoTableSearchFixture =
			new ExpandoTableSearchFixture(
				_classNameLocalService, _expandoColumnLocalService,
				_expandoTableLocalService);

		expandoTableSearchFixture.addExpandoColumn(
			User.class, ExpandoColumnConstants.INDEX_TYPE_KEYWORD,
			_EXPANDO_COLUMN);

		_expandoColumns = expandoTableSearchFixture.getExpandoColumns();
		_expandoTables = expandoTableSearchFixture.getExpandoTables();

		_user = UserTestUtil.addUser();

		ExpandoBridge expandoBridge = _user.getExpandoBridge();

		String value = RandomTestUtil.randomString();

		expandoBridge.setAttribute(_EXPANDO_COLUMN, value);

		_test(value);

		try (SafeCloseable safeCloseable =
				ReindexCacheThreadLocal.openReindexMode()) {

			_test(value);
		}
	}

	private void _test(String value) {
		Document document = new DocumentImpl();

		_expandoBridgeIndexer.addAttributes(document, _user);

		Assert.assertEquals(
			value,
			document.get(
				"expando__keyword__custom_fields__" + _EXPANDO_COLUMN));
	}

	private static final String _EXPANDO_COLUMN = RandomTestUtil.randomString();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@DeleteAfterTestRun
	private List<ExpandoColumn> _expandoColumns;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@DeleteAfterTestRun
	private List<ExpandoTable> _expandoTables;

	@DeleteAfterTestRun
	private User _user;

}