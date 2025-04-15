/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;
import com.liferay.template.test.util.TemplateTestUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
@Sync
public class DeleteTemplateEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setCompanyId(TestPropsValues.getCompanyId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_templateEntry = TemplateTestUtil.addAnyTemplateEntry(
			_infoItemServiceRegistry, _serviceContext);
	}

	@Test
	public void testBulkDeleteTemplateEntry() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		TemplateEntry templateEntry = TemplateTestUtil.addAnyTemplateEntry(
			_infoItemServiceRegistry, _serviceContext);

		mockLiferayPortletActionRequest.addParameter(
			"rowIds",
			new String[] {
				String.valueOf(_templateEntry.getTemplateEntryId()),
				String.valueOf(templateEntry.getTemplateEntryId())
			});

		_assertTemplateExists(_templateEntry);
		_assertTemplateExists(templateEntry);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		_assertTemplateNotExists(_templateEntry);
		_assertTemplateNotExists(templateEntry);
	}

	@Test
	public void testDeleteTemplateEntry() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"templateEntryId",
			String.valueOf(_templateEntry.getTemplateEntryId()));

		_assertTemplateExists(_templateEntry);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		_assertTemplateNotExists(_templateEntry);
	}

	private void _assertTemplateExists(TemplateEntry templateEntry) {
		Assert.assertNotNull(
			_ddmTemplateLocalService.fetchDDMTemplate(
				templateEntry.getDDMTemplateId()));
		Assert.assertNotNull(
			_templateEntryLocalService.fetchTemplateEntry(
				templateEntry.getTemplateEntryId()));
	}

	private void _assertTemplateNotExists(TemplateEntry templateEntry) {
		Assert.assertNull(
			_ddmTemplateLocalService.fetchDDMTemplate(
				templateEntry.getDDMTemplateId()));
		Assert.assertNull(
			_templateEntryLocalService.fetchTemplateEntry(
				templateEntry.getTemplateEntryId()));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DDMTemplate _ddmTemplate;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject(filter = "mvc.command.name=/template/delete_template_entry")
	private MVCActionCommand _mvcActionCommand;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private TemplateEntry _templateEntry;

	@Inject
	private TemplateEntryLocalService _templateEntryLocalService;

}