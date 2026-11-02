/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.template.exception.DuplicateTemplateEntryExternalReferenceCodeException;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class TemplateEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test(expected = DuplicateTemplateEntryExternalReferenceCodeException.class)
	public void testAddTemplateEntryWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_templateEntryLocalService.addTemplateEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), 0, StringPool.BLANK, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		_templateEntryLocalService.addTemplateEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), 0, StringPool.BLANK, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testDeleteGroup() throws Exception {
		long classNameId = PortalUtil.getClassNameId(TemplateEntry.class);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), classNameId, 0, classNameId);

		TemplateEntry templateEntry =
			_templateEntryLocalService.addTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				ddmTemplate.getTemplateId(), StringPool.BLANK, StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_groupLocalService.deleteGroup(_group);

		Assert.assertNull(
			_ddmTemplateLocalService.fetchDDMTemplate(
				ddmTemplate.getTemplateId()));
		Assert.assertNull(
			_templateEntryLocalService.fetchTemplateEntry(
				templateEntry.getTemplateEntryId()));
	}

	@Test
	public void testFetchTemplateEntryByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_templateEntryLocalService.addTemplateEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), 0, StringPool.BLANK, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotNull(
			_templateEntryLocalService.
				fetchTemplateEntryByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));
	}

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private TemplateEntryLocalService _templateEntryLocalService;

}