/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.batch.engine.client.http.HttpInvoker;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.portal.instances.client.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.portal.instances.client.pagination.Page;
import com.liferay.headless.portal.instances.client.problem.Problem;
import com.liferay.headless.portal.instances.client.resource.v1_0.PortalInstanceResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PortalInstanceResourceTest
	extends BasePortalInstanceResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_portalInstance = _toPortalInstance(_company);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_deletePortalInstance(_portalInstance);
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

			importTaskResource = ImportTaskResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				company.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();
			portalInstanceResource = PortalInstanceResource.builder(
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
	public void testDeletePortalInstance() throws Exception {
		_testDeletePortalInstanceExisting();
		_testDeletePortalInstanceNonexistent();
		_testDeletePortalInstanceWithoutOmniadminPermission();
	}

	@Override
	@Test
	public void testGetPortalInstance() throws Exception {
		assertEquals(
			portalInstanceResource.getPortalInstance(
				_portalInstance.getPortalInstanceId()),
			_portalInstance);

		_testGetPortalInstanceWithoutOmniadminPermission();
	}

	@Override
	@Test
	public void testGetPortalInstancesPage() throws Exception {
		Page<PortalInstance> page =
			portalInstanceResource.getPortalInstancesPage(null);

		assertContains(_portalInstance, (List<PortalInstance>)page.getItems());

		_testGetPortalInstancesPageWithoutOmniadminPermission();
	}

	@Override
	@Test
	public void testPatchPortalInstance() throws Exception {
		_testPatchPortalInstanceUpdateActive();
		_testPatchPortalInstanceUpdateCompanyId();
		_testPatchPortalInstanceUpdateDomain();
		_testPatchPortalInstanceUpdatePortletInstanceId();
		_testPatchPortalInstanceUpdateVirtualHost();
		_testPatchPortalInstanceWithoutOmniadminPermission();
	}

	@Override
	@Test
	public void testPostPortalInstance() throws Exception {
		_testPostPortalInstanceBatchWithSeveralPortalInstances();
		_testPostPortalInstanceWithoutAdmin();
		_testPostPortalInstanceWithAdmin();
		_testPostPortalInstanceWithAdminAndCompanyStrangers();
		_testPostPortalInstanceWithAdminCredentials();
		_testPostPortalInstanceWithoutOmniadminPermission();
	}

	@Override
	@Test
	public void testPutPortalInstanceActivate() throws Exception {
		_companyLocalService.updateCompany(
			_company.getCompanyId(), _company.getVirtualHostname(),
			_company.getMx(), _company.getMaxUsers(), false);

		Company company = _companyLocalService.fetchCompany(
			_portalInstance.getCompanyId());

		Assert.assertFalse(company.isActive());

		portalInstanceResource.putPortalInstanceActivate(
			_portalInstance.getPortalInstanceId());

		company = _companyLocalService.fetchCompany(
			_portalInstance.getCompanyId());

		Assert.assertTrue(company.isActive());

		_testPutPortalInstanceActivateWithoutOmniadminPermission();
	}

	@Override
	@Test
	public void testPutPortalInstanceDeactivate() throws Exception {
		_companyLocalService.updateCompany(
			_company.getCompanyId(), _company.getVirtualHostname(),
			_company.getMx(), _company.getMaxUsers(), true);

		Company company = _companyLocalService.fetchCompany(
			_portalInstance.getCompanyId());

		Assert.assertTrue(company.isActive());

		portalInstanceResource.putPortalInstanceDeactivate(
			_portalInstance.getPortalInstanceId());

		company = _companyLocalService.fetchCompany(
			_portalInstance.getCompanyId());

		Assert.assertFalse(company.isActive());

		_testPutPortalInstanceDeactivateWithoutOmniadminPermission();
	}

	@Override
	protected void assertValid(PortalInstance portalInstance) throws Exception {
		boolean valid = true;

		if (Validator.isNull(portalInstance.getActive()) ||
			Validator.isNull(portalInstance.getCompanyId()) ||
			Validator.isNull(portalInstance.getDomain()) ||
			Validator.isNull(portalInstance.getPortalInstanceId()) ||
			Validator.isNull(portalInstance.getVirtualHost())) {

			valid = false;
		}

		Assert.assertTrue(valid);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "companyId", "domain", "portalInstanceId", "virtualHost"
		};
	}

	@Override
	protected PortalInstance randomPortalInstance() throws Exception {
		String randomPortalInstanceId = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		String randomDomain =
			randomPortalInstanceId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3));

		return new PortalInstance() {
			{
				active = true;
				companyId = RandomTestUtil.randomLong();
				domain = randomDomain;
				portalInstanceId = randomPortalInstanceId;
				virtualHost = randomDomain;
			}
		};
	}

	@Override
	protected void testBatchEngineDeleteImportTask_deletePortalInstance(
			int expectedStatusCode, String externalReferenceCode, String id,
			String... parameters)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.portal.instances.dto.v1_0.PortalInstance",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"portalInstanceId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Override
	protected PortalInstance testDeletePortalInstance_addPortalInstance()
		throws Exception {

		return portalInstanceResource.postPortalInstance(
			randomPortalInstance());
	}

	@Override
	protected PortalInstance testPostPortalInstance_addPortalInstance(
			PortalInstance portalInstance)
		throws Exception {

		return portalInstanceResource.postPortalInstance(portalInstance);
	}

	private static void _deletePortalInstance(PortalInstance portalInstance)
		throws Exception {

		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		try {
			_companyLocalService.deleteCompany(portalInstance.getCompanyId());
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	private static PortalInstance _toPortalInstance(Company company) {
		return new PortalInstance() {
			{
				setActive(company::isActive);
				setCompanyId(company::getCompanyId);
				setDomain(company::getMx);
				setPortalInstanceId(company::getWebId);
				setVirtualHost(company::getVirtualHostname);
			}
		};
	}

	private void _assertProblemExceptionProblemStatus(
			String status, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
		}
	}

	private PortalInstance _copyPortalInstance(
			boolean updateActive, boolean updateCompanyId, boolean updateDomain,
			boolean updatePortletInstanceId, boolean updateVirtualHost)
		throws Exception {

		String randomPortalInstanceId = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		String randomDomain =
			randomPortalInstanceId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3));

		PortalInstance copyPortalInstance = _portalInstance.clone();

		if (updateActive) {
			copyPortalInstance.setActive(!copyPortalInstance.getActive());
		}

		if (updateCompanyId) {
			copyPortalInstance.setCompanyId(RandomTestUtil.randomLong());
		}

		if (updateDomain) {
			copyPortalInstance.setDomain(randomDomain);
		}

		if (updatePortletInstanceId) {
			copyPortalInstance.setPortalInstanceId(randomPortalInstanceId);
		}

		if (updateVirtualHost) {
			copyPortalInstance.setVirtualHost(randomDomain);
		}

		return copyPortalInstance;
	}

	private PortalInstanceResource _createUserPortalInstanceResource()
		throws Exception {

		User user = UserTestUtil.addUser(testCompany, "test");

		return PortalInstanceResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testDeletePortalInstanceExisting() throws Exception {
		PortalInstance randomPortalInstance = randomPortalInstance();

		PortalInstance portalInstance =
			portalInstanceResource.postPortalInstance(randomPortalInstance);

		assertValid(portalInstance);

		Assert.assertNotNull(
			_companyLocalService.fetchCompany(portalInstance.getCompanyId()));

		portalInstanceResource.deletePortalInstance(
			portalInstance.getPortalInstanceId());

		Assert.assertNull(
			_companyLocalService.fetchCompany(portalInstance.getCompanyId()));
	}

	private void _testDeletePortalInstanceNonexistent() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		try {
			portalInstanceResource.deletePortalInstance(portalInstanceId);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testDeletePortalInstanceWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		_assertProblemExceptionProblemStatus(
			"FORBIDDEN",
			() -> userPortalInstanceResource.deletePortalInstance(
				_portalInstance.getPortalInstanceId()));
	}

	private void _testGetPortalInstanceWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		// PrincipalExceptionMapper converts a denied GET request to a
		// 404 to avoid disclosing the portal instance's existence

		_assertProblemExceptionProblemStatus(
			"NOT_FOUND",
			() -> userPortalInstanceResource.getPortalInstance(
				_portalInstance.getPortalInstanceId()));
	}

	private void _testGetPortalInstancesPageWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		// PrincipalExceptionMapper converts a denied GET request to a
		// 404 to avoid disclosing the portal instance's existence

		_assertProblemExceptionProblemStatus(
			"NOT_FOUND",
			() -> userPortalInstanceResource.getPortalInstancesPage(null));
	}

	private void _testPatchPortalInstace(
			PortalInstance portalInstance, boolean updateActive,
			boolean updateCompanyId, boolean updatePortletInstanceId)
		throws Exception {

		PortalInstance patchPortalInstance =
			portalInstanceResource.patchPortalInstance(
				_portalInstance.getPortalInstanceId(), portalInstance);

		if (updateActive) {
			Assert.assertNotEquals(
				portalInstance.getActive(), patchPortalInstance.getActive());
		}
		else {
			Assert.assertEquals(
				portalInstance.getActive(), patchPortalInstance.getActive());
		}

		if (updateCompanyId) {
			Assert.assertNotEquals(
				portalInstance.getCompanyId(),
				patchPortalInstance.getCompanyId());
		}
		else {
			Assert.assertEquals(
				portalInstance.getCompanyId(),
				patchPortalInstance.getCompanyId());
		}

		Assert.assertEquals(
			portalInstance.getDomain(), patchPortalInstance.getDomain());

		if (updatePortletInstanceId) {
			Assert.assertNotEquals(
				portalInstance.getPortalInstanceId(),
				patchPortalInstance.getPortalInstanceId());
		}
		else {
			Assert.assertEquals(
				portalInstance.getPortalInstanceId(),
				patchPortalInstance.getPortalInstanceId());
		}

		Assert.assertEquals(
			portalInstance.getVirtualHost(),
			patchPortalInstance.getVirtualHost());
	}

	private void _testPatchPortalInstanceUpdateActive() throws Exception {
		PortalInstance portalInstance = _copyPortalInstance(
			true, false, false, false, false);

		_testPatchPortalInstace(portalInstance, true, false, false);
	}

	private void _testPatchPortalInstanceUpdateCompanyId() throws Exception {
		PortalInstance portalInstance = _copyPortalInstance(
			false, true, false, false, false);

		_testPatchPortalInstace(portalInstance, false, true, false);
	}

	private void _testPatchPortalInstanceUpdateDomain() throws Exception {
		PortalInstance portalInstance = _copyPortalInstance(
			false, false, true, false, false);

		_testPatchPortalInstace(portalInstance, false, false, false);
	}

	private void _testPatchPortalInstanceUpdatePortletInstanceId()
		throws Exception {

		PortalInstance portalInstance = _copyPortalInstance(
			false, false, false, true, false);

		_testPatchPortalInstace(portalInstance, false, false, true);
	}

	private void _testPatchPortalInstanceUpdateVirtualHost() throws Exception {
		PortalInstance portalInstance = _copyPortalInstance(
			false, false, false, false, true);

		_testPatchPortalInstace(portalInstance, false, false, false);
	}

	private void _testPatchPortalInstanceWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		_assertProblemExceptionProblemStatus(
			"FORBIDDEN",
			() -> userPortalInstanceResource.patchPortalInstance(
				_portalInstance.getPortalInstanceId(), randomPortalInstance()));
	}

	private void _testPostPortalInstanceBatchWithSeveralPortalInstances()
		throws Exception {

		List<PortalInstance> portalInstances = Arrays.asList(
			randomPortalInstance(), randomPortalInstance());

		try {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			for (PortalInstance portalInstance : portalInstances) {
				jsonArray.put(
					JSONFactoryUtil.createJSONObject(
						portalInstance.toString()));
			}

			HttpResponse httpResponse =
				portalInstanceResource.postPortalInstanceBatchHttpResponse(
					null, jsonArray);

			Assert.assertEquals(202, httpResponse.getStatusCode());

			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));

			for (PortalInstance portalInstance : portalInstances) {
				PortalInstance getPortalInstance =
					portalInstanceResource.getPortalInstance(
						portalInstance.getPortalInstanceId());

				assertEquals(portalInstance, getPortalInstance);
				assertValid(getPortalInstance);
			}

			jsonArray = JSONFactoryUtil.createJSONArray();

			for (PortalInstance portalInstance : portalInstances) {
				jsonArray.put(
					JSONUtil.put(
						"portalInstanceId",
						portalInstance.getPortalInstanceId()));
			}

			httpResponse =
				portalInstanceResource.deletePortalInstanceBatchHttpResponse(
					null, jsonArray);

			Assert.assertEquals(202, httpResponse.getStatusCode());

			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));

			for (PortalInstance portalInstance : portalInstances) {
				assertHttpResponseStatusCode(
					404,
					portalInstanceResource.getPortalInstanceHttpResponse(
						portalInstance.getPortalInstanceId()));
			}
		}
		finally {
			for (PortalInstance portalInstance : portalInstances) {
				Company company =
					_companyLocalService.fetchCompanyByVirtualHost(
						portalInstance.getVirtualHost());

				if (company != null) {
					_deletePortalInstance(_toPortalInstance(company));
				}
			}
		}
	}

	private void _testPostPortalInstanceWithAdmin() throws Exception {
		PortalInstance randomPortalInstance = randomPortalInstance();

		String firstName = RandomTestUtil.randomString();

		String emailAddress = StringUtil.toLowerCase(
			firstName + "@liferay.com");

		randomPortalInstance.setAdmin(
			Admin.toDTO(
				JSONUtil.put(
					"emailAddress", emailAddress
				).put(
					"familyName", RandomTestUtil.randomString()
				).put(
					"givenName", firstName
				).toString()));

		PortalInstance postPortalInstance =
			testPostPortalInstance_addPortalInstance(randomPortalInstance);

		try {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						postPortalInstance.getCompanyId())) {

				Assert.assertNotNull(
					_userLocalService.getUserByEmailAddress(
						postPortalInstance.getCompanyId(), emailAddress));
			}

			assertEquals(randomPortalInstance, postPortalInstance);
			assertValid(postPortalInstance);
		}
		finally {
			if (postPortalInstance != null) {
				_deletePortalInstance(postPortalInstance);
			}
		}
	}

	private void _testPostPortalInstanceWithAdminAndCompanyStrangers()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.COMPANY_SECURITY_STRANGERS,
					Boolean.TRUE.toString())) {

			_testPostPortalInstanceWithAdmin();
		}
	}

	private void _testPostPortalInstanceWithAdminCredentials()
		throws Exception {

		PortalInstance randomPortalInstance = randomPortalInstance();

		String givenName = RandomTestUtil.randomString();

		String emailAddress = StringUtil.toLowerCase(
			givenName + "@liferay.com");

		String middleName = RandomTestUtil.randomString();
		String password = RandomTestUtil.randomString();
		String screenName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		randomPortalInstance.setAdmin(
			Admin.toDTO(
				JSONUtil.put(
					"emailAddress", emailAddress
				).put(
					"familyName", RandomTestUtil.randomString()
				).put(
					"givenName", givenName
				).put(
					"middleName", middleName
				).put(
					"password", password
				).put(
					"screenName", screenName
				).toString()));

		PortalInstance postPortalInstance =
			testPostPortalInstance_addPortalInstance(randomPortalInstance);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					postPortalInstance.getCompanyId())) {

			User user = _userLocalService.getUserByEmailAddress(
				postPortalInstance.getCompanyId(), emailAddress);

			Assert.assertEquals(middleName, user.getMiddleName());
			Assert.assertEquals(screenName, user.getScreenName());

			Assert.assertEquals(
				Authenticator.SUCCESS,
				_userLocalService.authenticateByEmailAddress(
					postPortalInstance.getCompanyId(), emailAddress, password,
					new HashMap<>(), new HashMap<>(), new HashMap<>()));
		}
		finally {
			_deletePortalInstance(postPortalInstance);
		}
	}

	private void _testPostPortalInstanceWithoutAdmin() throws Exception {
		PortalInstance randomPortalInstance = randomPortalInstance();

		PortalInstance postPortalInstance =
			testPostPortalInstance_addPortalInstance(randomPortalInstance);

		try {
			assertEquals(randomPortalInstance, postPortalInstance);
			assertValid(postPortalInstance);
		}
		finally {
			if (postPortalInstance != null) {
				_deletePortalInstance(postPortalInstance);
			}
		}
	}

	private void _testPostPortalInstanceWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		_assertProblemExceptionProblemStatus(
			"FORBIDDEN",
			() -> userPortalInstanceResource.postPortalInstance(
				randomPortalInstance()));
	}

	private void _testPutPortalInstanceActivateWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		_assertProblemExceptionProblemStatus(
			"FORBIDDEN",
			() -> userPortalInstanceResource.putPortalInstanceActivate(
				_portalInstance.getPortalInstanceId()));
	}

	private void _testPutPortalInstanceDeactivateWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceResource userPortalInstanceResource =
			_createUserPortalInstanceResource();

		_assertProblemExceptionProblemStatus(
			"FORBIDDEN",
			() -> userPortalInstanceResource.putPortalInstanceDeactivate(
				_portalInstance.getPortalInstanceId()));
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static PortalInstance _portalInstance;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}