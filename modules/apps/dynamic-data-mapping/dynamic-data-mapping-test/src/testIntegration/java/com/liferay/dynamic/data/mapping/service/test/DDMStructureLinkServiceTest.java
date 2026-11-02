/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkService;
import com.liferay.dynamic.data.mapping.util.comparator.StructureLinkStructureNameComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DDMStructureLinkServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetStructureLinks() throws Exception {
		long classNameId = PortalUtil.getClassNameId(DDL_RECORD_SET_CLASS_NAME);
		long classPK = RandomTestUtil.randomLong();
		String keyword = StringUtil.toLowerCase(RandomTestUtil.randomString());

		List<DDMStructureLink> ddmStructureLinks = TransformUtil.transform(
			Arrays.asList(
				RandomTestUtil.randomString(),
				RandomTestUtil.randomString(4001)),
			name -> {
				DDMStructure ddmStructure = addStructure(
					classNameId, keyword + StringUtil.toLowerCase(name));

				return _ddmStructureLinkLocalService.addStructureLink(
					classNameId, classPK, ddmStructure.getStructureId());
			});

		StructureLinkStructureNameComparator
			structureLinkStructureNameComparator =
				new StructureLinkStructureNameComparator(true);

		ddmStructureLinks.sort(structureLinkStructureNameComparator);

		Assert.assertEquals(
			ddmStructureLinks,
			_ddmStructureLinkService.getStructureLinks(
				classNameId, classPK, new long[] {group.getGroupId()}, keyword,
				DDL_RECORD_SET_CLASS_NAME, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				structureLinkStructureNameComparator));
	}

	@Inject
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

	@Inject
	private DDMStructureLinkService _ddmStructureLinkService;

}