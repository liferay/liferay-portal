/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstanceCopy;
import com.liferay.headless.portal.instances.client.problem.Problem;
import com.liferay.headless.portal.instances.client.resource.v1_0.PortalInstanceCopyResource;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PortalInstanceCopyResourceTest
	extends BasePortalInstanceCopyResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BasePortalInstanceCopyResourceTestCase.setUpClass();

		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_deleteCompany(_company.getCompanyId());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Company company = _companyLocalService.getCompany(
				PortalInstancePool.getDefaultCompanyId());

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			portalInstanceCopyResource = PortalInstanceCopyResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				company.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();
		}
	}

	@Override
	@Test
	public void testPostPortalInstanceCopy() throws Exception {
		super.testPostPortalInstanceCopy();

		DB db = DBManagerUtil.getDB();

		Assume.assumeTrue(db.isSupportsDBPartition());

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			_testPostPortalInstanceCopyWithDBPartitionDisabled();

			return;
		}

		_testPostPortalInstanceCopyDefaultCompany();
		_testPostPortalInstanceCopyMissingRequiredFields();
		_testPostPortalInstanceCopySuccess();
		_testPostPortalInstanceCopySuccessWithDestinationCompanyId();
		_testPostPortalInstanceCopyWithNonexistentPortalInstance();
		_testPostPortalInstanceCopyWithNonpositiveDestinationCompanyId();
		_testPostPortalInstanceCopyWithoutOmniadminPermission();
	}

	private static void _deleteCompany(long companyId) throws Exception {
		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		try {
			_companyLocalService.deleteCompany(companyId);
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	private void _assertPostPortalInstanceCopyBadRequest(
			PortalInstanceCopy portalInstanceCopy)
		throws Exception {

		try {
			portalInstanceCopy.setSourcePortalInstanceId(_company::getWebId);

			portalInstanceCopyResource.postPortalInstanceCopy(
				portalInstanceCopy);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}
	}

	private void _assertPostPortalInstanceCopySuccess(
			PortalInstanceCopy portalInstanceCopy, Long expectedCompanyId)
		throws Exception {

		portalInstanceCopy.setSourcePortalInstanceId(_company::getWebId);

		PortalInstance copiedPortalInstance =
			portalInstanceCopyResource.postPortalInstanceCopy(
				portalInstanceCopy);

		try {
			Assert.assertNotEquals(
				Long.valueOf(_company.getCompanyId()),
				copiedPortalInstance.getCompanyId());
			Assert.assertEquals(
				portalInstanceCopy.getWebId(),
				copiedPortalInstance.getPortalInstanceId());
			Assert.assertEquals(
				portalInstanceCopy.getVirtualHost(),
				copiedPortalInstance.getVirtualHost());

			if (expectedCompanyId != null) {
				Assert.assertEquals(
					expectedCompanyId, copiedPortalInstance.getCompanyId());
			}
		}
		finally {
			if (copiedPortalInstance != null) {
				_deleteCompany(copiedPortalInstance.getCompanyId());
			}
		}
	}

	private PortalInstanceCopyResource _createUserPortalInstanceCopyResource()
		throws Exception {

		User user = UserTestUtil.addUser(testCompany, "test");

		return PortalInstanceCopyResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testPostPortalInstanceCopyDefaultCompany() throws Exception {
		Company defaultCompany = _companyLocalService.getCompany(
			PortalInstancePool.getDefaultCompanyId());

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(RandomTestUtil.randomString());
		portalInstanceCopy.setVirtualHost(RandomTestUtil.randomString());
		portalInstanceCopy.setWebId(RandomTestUtil.randomString());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_COPY_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceCopy.setSourcePortalInstanceId(
				defaultCompany::getWebId);

			portalInstanceCopyResource.postPortalInstanceCopy(
				portalInstanceCopy);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Company ID " + defaultCompany.getCompanyId() +
					" is the default company ID",
				problem.getTitle());
		}
	}

	private void _testPostPortalInstanceCopyMissingRequiredFields()
		throws Exception {

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setVirtualHost(RandomTestUtil.randomString());
		portalInstanceCopy.setWebId(RandomTestUtil.randomString());

		_assertPostPortalInstanceCopyBadRequest(portalInstanceCopy);

		portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(RandomTestUtil.randomString());
		portalInstanceCopy.setWebId(RandomTestUtil.randomString());

		_assertPostPortalInstanceCopyBadRequest(portalInstanceCopy);

		portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(RandomTestUtil.randomString());
		portalInstanceCopy.setVirtualHost(RandomTestUtil.randomString());

		_assertPostPortalInstanceCopyBadRequest(portalInstanceCopy);
	}

	private void _testPostPortalInstanceCopySuccess() throws Exception {
		String randomId = StringUtil.toLowerCase(RandomTestUtil.randomString());

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(randomId);
		portalInstanceCopy.setVirtualHost(
			randomId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3)));
		portalInstanceCopy.setWebId(randomId);

		_assertPostPortalInstanceCopySuccess(portalInstanceCopy, null);
	}

	private void _testPostPortalInstanceCopySuccessWithDestinationCompanyId()
		throws Exception {

		long destinationCompanyId = CounterLocalServiceUtil.increment(
			Company.class.getName());

		String randomId = StringUtil.toLowerCase(RandomTestUtil.randomString());

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setDestinationCompanyId(destinationCompanyId);
		portalInstanceCopy.setName(randomId);
		portalInstanceCopy.setVirtualHost(
			randomId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3)));
		portalInstanceCopy.setWebId(randomId);

		_assertPostPortalInstanceCopySuccess(
			portalInstanceCopy, destinationCompanyId);
	}

	private void _testPostPortalInstanceCopyWithDBPartitionDisabled()
		throws Exception {

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(RandomTestUtil.randomString());
		portalInstanceCopy.setVirtualHost(RandomTestUtil.randomString());
		portalInstanceCopy.setWebId(RandomTestUtil.randomString());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_COPY_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceCopy.setSourcePortalInstanceId(_company::getWebId);

			portalInstanceCopyResource.postPortalInstanceCopy(
				portalInstanceCopy);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Database partitioning must be enabled", problem.getTitle());
		}
	}

	private void _testPostPortalInstanceCopyWithNonexistentPortalInstance()
		throws Exception {

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(RandomTestUtil.randomString());
		portalInstanceCopy.setVirtualHost(RandomTestUtil.randomString());
		portalInstanceCopy.setWebId(RandomTestUtil.randomString());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			portalInstanceCopy.setSourcePortalInstanceId(
				RandomTestUtil::randomString);

			portalInstanceCopyResource.postPortalInstanceCopy(
				portalInstanceCopy);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPostPortalInstanceCopyWithNonpositiveDestinationCompanyId()
		throws Exception {

		String randomId = StringUtil.toLowerCase(RandomTestUtil.randomString());

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setDestinationCompanyId(0L);
		portalInstanceCopy.setName(randomId);
		portalInstanceCopy.setVirtualHost(
			randomId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3)));
		portalInstanceCopy.setWebId(randomId);

		_assertPostPortalInstanceCopySuccess(portalInstanceCopy, null);
	}

	private void _testPostPortalInstanceCopyWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceCopyResource userPortalInstanceCopyResource =
			_createUserPortalInstanceCopyResource();

		PortalInstanceCopy portalInstanceCopy = new PortalInstanceCopy();

		portalInstanceCopy.setName(RandomTestUtil.randomString());
		portalInstanceCopy.setSourcePortalInstanceId(_company::getWebId);
		portalInstanceCopy.setVirtualHost(RandomTestUtil.randomString());
		portalInstanceCopy.setWebId(RandomTestUtil.randomString());

		try {
			userPortalInstanceCopyResource.postPortalInstanceCopy(
				portalInstanceCopy);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private static final String _CLASS_NAME_PORTAL_INSTANCE_COPY_RESOURCE_IMPL =
		"com.liferay.headless.portal.instances.internal.resource.v1_0." +
			"PortalInstanceCopyResourceImpl";

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

}