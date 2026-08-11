/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.PublishProcessResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ScheduledPublishProcessResourceTest
	extends BaseScheduledPublishProcessResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());

		GroupTestUtil.enableLocalStaging(testGroup);

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		_publishProcessResource = PublishProcessResource.builder(
		).authentication(
			adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	public void testDeleteSiteScheduledPublishProcess() throws Exception {
		ScheduledPublishProcess scheduledPublishProcess =
			_addScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString());

		assertHttpResponseStatusCode(
			204,
			scheduledPublishProcessResource.
				deleteSiteScheduledPublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(),
					scheduledPublishProcess.getId()));

		Page<ScheduledPublishProcess> page =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					testGroup.getExternalReferenceCode(), null,
					Pagination.of(1, 100), null);

		for (ScheduledPublishProcess remainingScheduledPublishProcess :
				page.getItems()) {

			Assert.assertNotEquals(
				scheduledPublishProcess.getId(),
				remainingScheduledPublishProcess.getId());
		}
	}

	@Override
	@Test
	public void testGetSiteScheduledPublishProcess() throws Exception {
		assertHttpResponseStatusCode(
			404,
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(),
					RandomTestUtil.randomLong()));

		ScheduledPublishProcess scheduledPublishProcess =
			_addScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString());

		ScheduledPublishProcess getScheduledPublishProcess =
			scheduledPublishProcessResource.getSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				scheduledPublishProcess.getId());

		Assert.assertEquals(
			_CRON_EXPRESSION, getScheduledPublishProcess.getCronExpression());
		Assert.assertEquals(
			scheduledPublishProcess.getName(),
			getScheduledPublishProcess.getName());
		Assert.assertNotNull(getScheduledPublishProcess.getDateCreated());
		Assert.assertNotNull(getScheduledPublishProcess.getPublishParameters());
		Assert.assertNotNull(getScheduledPublishProcess.getScheduleStartDate());
	}

	@Override
	protected ScheduledPublishProcess
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				String siteExternalReferenceCode,
				ScheduledPublishProcess scheduledPublishProcess)
		throws Exception {

		return _addScheduledPublishProcess(
			siteExternalReferenceCode, scheduledPublishProcess.getName());
	}

	@Override
	protected String
			testGetSiteScheduledPublishProcessesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return null;
	}

	private ScheduledPublishProcess _addScheduledPublishProcess(
			String siteExternalReferenceCode, String publishProcessName)
		throws Exception {

		_publishProcessResource.postSitePublishProcess(
			siteExternalReferenceCode,
			new PublishProcessRequest() {
				{
					cronExpression = _CRON_EXPRESSION;
					name = publishProcessName;
					timeZoneId = "UTC";
				}
			});

		Page<ScheduledPublishProcess> page =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					siteExternalReferenceCode, publishProcessName,
					Pagination.of(1, 1), null);

		return page.fetchFirstItem();
	}

	private static final String _CRON_EXPRESSION = "0 0 3 * * ?";

	private PublishProcessResource _publishProcessResource;

}