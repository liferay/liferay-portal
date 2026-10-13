/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class OrganizationCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_organizationLocalService.getOrAddEmptyOrganization(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		ListType listType = _listTypeLocalService.getListType(
			group.getCompanyId(), ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
			ListTypeConstants.ORGANIZATION_STATUS);

		_organizationLocalService.addOrganization(
			externalReferenceCode, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(),
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0,
			listType.getListTypeId(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_organizationLocalService.deleteOrganization(
			_getOrganization(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Organization organization = _getOrganization(
			externalReferenceCode, groupId);

		return organization.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.headless.admin.user.dto.v1_0.Organization.class.
			getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		Organization organization = _getOrganization(
			externalReferenceCode, groupId);

		return organization.getOrganizationId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		Organization organization = _getOrganization(
			externalReferenceCode, groupId);

		return organization.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		Organization organization =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());

		if (organization == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return true;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		Organization organization = _getOrganization(
			externalReferenceCode, groupId);

		organization.setUserId(userId);

		_organizationLocalService.updateOrganization(organization);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Organization organization = _getOrganization(
			externalReferenceCode, groupId);

		_organizationLocalService.updateOrganization(
			organization.getExternalReferenceCode(),
			organization.getCompanyId(), organization.getOrganizationId(),
			organization.getParentOrganizationId(), organization.getName(),
			organization.getType(), organization.getRegionId(),
			organization.getCountryId(), organization.getStatusListTypeId(),
			RandomTestUtil.randomString(), false, null, false,
			ServiceContextTestUtil.getServiceContext(
				organization.getCompanyId(), groupId,
				organization.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Organization organization = _getOrganization(
			externalReferenceCode, groupId);

		organization.setModifiedDate(modifiedDate);

		_organizationLocalService.updateOrganization(organization);
	}

	private Organization _getOrganization(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _organizationLocalService.getOrganizationByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

}