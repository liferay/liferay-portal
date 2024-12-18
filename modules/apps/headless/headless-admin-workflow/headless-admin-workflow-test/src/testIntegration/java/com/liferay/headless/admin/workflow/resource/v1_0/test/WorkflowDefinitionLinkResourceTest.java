/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalServiceUtil;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class WorkflowDefinitionLinkResourceTest
	extends BaseWorkflowDefinitionLinkResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(
				UserLocalServiceUtil.getUser(TestPropsValues.getUserId())));

		String name = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_kaleoDefinition = KaleoDefinitionLocalServiceUtil.addKaleoDefinition(
			RandomTestUtil.randomString(), name, RandomTestUtil.randomString(),
			StringPool.BLANK,
			WorkflowDefinitionTestUtil.getContent(
				RandomTestUtil.randomString(), "workflow-definition.xml", name),
			StringPool.BLANK, 1, ServiceContextTestUtil.getServiceContext());
	}

	@Override
	protected WorkflowDefinitionLink randomWorkflowDefinitionLink()
		throws Exception {

		Group group = _groupService.getGroup(_kaleoDefinition.getGroupId());

		return new WorkflowDefinitionLink() {
			{
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				groupExternalReferenceCode = group.getExternalReferenceCode();
				groupId = _kaleoDefinition.getGroupId();
				workflowDefinitionName = _kaleoDefinition.getName();
				workflowDefinitionVersion = _kaleoDefinition.getVersion();
			}
		};
	}

	@Override
	protected WorkflowDefinitionLink
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				String externalReferenceCode,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		return workflowDefinitionLinkResource.
			postWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink(
				externalReferenceCode, workflowDefinitionLink);
	}

	@Override
	protected String
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExternalReferenceCode()
		throws Exception {

		return _kaleoDefinition.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getWorkflowDefinitionId()
		throws Exception {

		return _kaleoDefinition.getKaleoDefinitionId();
	}

	@Override
	protected WorkflowDefinitionLink
			testPostWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink_addWorkflowDefinitionLink(
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		return workflowDefinitionLinkResource.
			postWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink(
				_kaleoDefinition.getExternalReferenceCode(),
				workflowDefinitionLink);
	}

	@Override
	protected WorkflowDefinitionLink
			testPutWorkflowDefinitionLinkByExternalReferenceCode_addWorkflowDefinitionLink()
		throws Exception {

		return workflowDefinitionLinkResource.
			postWorkflowDefinitionWorkflowDefinitionLink(
				_kaleoDefinition.getKaleoDefinitionId(),
				randomWorkflowDefinitionLink());
	}

	@Override
	protected WorkflowDefinitionLink
			testPutWorkflowDefinitionLinkByExternalReferenceCode_createWorkflowDefinitionLink()
		throws Exception {

		return testPutWorkflowDefinitionLinkByExternalReferenceCode_addWorkflowDefinitionLink();
	}

	@Override
	protected WorkflowDefinitionLink
		testPutWorkflowDefinitionLinkByExternalReferenceCode_getWorkflowDefinitionLink(
			String externalReferenceCode) {

		try {
			return _fetchWorkflowDefinitionLinkByExternalReferenceCode(
				externalReferenceCode, _kaleoDefinition.getGroupId());
		}
		catch (PortalException portalException) {
			return null;
		}
	}

	private WorkflowDefinitionLink
			_fetchWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkLocalService.
				fetchWorkflowDefinitionLinkByExternalReferenceCode(
					externalReferenceCode, groupId));
	}

	private WorkflowDefinitionLink _toWorkflowDefinitionLink(
			com.liferay.portal.kernel.model.WorkflowDefinitionLink
				workflowDefinitionLink)
		throws PortalException {

		return new WorkflowDefinitionLink() {
			{
				setClassName(workflowDefinitionLink::getClassName);
				setExternalReferenceCode(
					workflowDefinitionLink::getExternalReferenceCode);
				setGroupExternalReferenceCode(
					_groupLocalService.fetchGroup(
						workflowDefinitionLink.getGroupId()
					).getExternalReferenceCode());
				setGroupId(workflowDefinitionLink::getGroupId);
				setId(workflowDefinitionLink::getWorkflowDefinitionLinkId);
				setWorkflowDefinitionName(
					workflowDefinitionLink::getWorkflowDefinitionName);
				setWorkflowDefinitionVersion(
					workflowDefinitionLink::getWorkflowDefinitionVersion);
			}
		};
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private GroupService _groupService;

	@DeleteAfterTestRun
	private KaleoDefinition _kaleoDefinition;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}