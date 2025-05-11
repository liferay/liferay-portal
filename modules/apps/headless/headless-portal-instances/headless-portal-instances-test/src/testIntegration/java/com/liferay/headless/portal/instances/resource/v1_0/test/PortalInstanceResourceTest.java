/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.portal.instances.client.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.pagination.Page;
import com.liferay.headless.portal.instances.client.problem.Problem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

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

	@Override
	@Test
	public void testDeletePortalInstance() throws Exception {
		_testDeletePortalInstanceExisting();
		_testDeletePortalInstanceNonexistent();
	}

	@Override
	@Test
	public void testGetPortalInstance() throws Exception {
		assertEquals(
			portalInstanceResource.getPortalInstance(
				_portalInstance.getPortalInstanceId()),
			_portalInstance);
	}

	@Override
	@Test
	public void testGetPortalInstancesPage() throws Exception {
		Page<PortalInstance> page =
			portalInstanceResource.getPortalInstancesPage(null);

		assertContains(_portalInstance, (List<PortalInstance>)page.getItems());
	}

	@Override
	@Test
	public void testPatchPortalInstance() throws Exception {
		_testPatchPortalInstanceUpdateActive();
		_testPatchPortalInstanceUpdateCompanyId();
		_testPatchPortalInstanceUpdateDomain();
		_testPatchPortalInstanceUpdatePortletInstanceId();
		_testPatchPortalInstanceUpdateVirtualHost();
	}

	@Override
	@Test
	public void testPostPortalInstance() throws Exception {
		_testPostPortalInstanceWithoutAdmin();
		_testPostPortalInstanceWithAdmin();
		_testPostPortalInstanceWithAdminAndCompanyStrangers();
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
			Assert.assertNotNull(
				_userLocalService.getUserByEmailAddress(
					postPortalInstance.getCompanyId(), emailAddress));

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

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static PortalInstance _portalInstance;

	@Inject
	private UserLocalService _userLocalService;

}