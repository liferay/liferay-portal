/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionTemplateService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noor Najjar
 */
@RunWith(Arquillian.class)
public class CTCollectionTemplateServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_name = RandomTestUtil.randomString();

		_ctCollectionTemplateService.addCTCollectionTemplate(
			_name, RandomTestUtil.randomString(),
			JSONUtil.put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"publicationsUserRoleUserIds", Collections.emptyList()
			).put(
				"roleValues", Collections.emptyList()
			).put(
				"userIds", new long[] {RandomTestUtil.randomLong()}
			).toString());
	}

	@Test
	public void testGetCTCollectionTemplates() {
		_testGetCTCollectionTemplates(StringPool.BLANK);
		_testGetCTCollectionTemplates(_name);
	}

	@Test
	public void testGetCTCollectionTemplatesCount() {
		_testGetCTCollectionTemplatesCount(StringPool.BLANK);
		_testGetCTCollectionTemplatesCount(_name);
	}

	private void _testGetCTCollectionTemplates(String keywords) {
		List<CTCollectionTemplate> ctCollectionTemplates =
			_ctCollectionTemplateService.getCTCollectionTemplates(
				keywords, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			ctCollectionTemplates.toString(), 1, ctCollectionTemplates.size());

		CTCollectionTemplate ctCollectionTemplate = ctCollectionTemplates.get(
			0);

		Assert.assertEquals(_name, ctCollectionTemplate.getName());
	}

	private void _testGetCTCollectionTemplatesCount(String keywords) {
		long count = _ctCollectionTemplateService.getCTCollectionTemplatesCount(
			keywords);

		Assert.assertEquals(1, count);
	}

	@Inject
	private static CTCollectionTemplateService _ctCollectionTemplateService;

	private String _name;

}