/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.internal.ProvisioningClientImpl;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Marcos Martins
 */
public class ProjectFaroControllerTest {

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(
			_projectFaroController, "_provisionBackoffPortalCache",
			new TestPortalCache());
		ReflectionTestUtils.setField(
			_projectFaroController, "_provisioningClient",
			new ProvisioningClientImpl());
	}

	@Test
	public void testCheckProvisionBackoffWhenFailureRecordedWithinWindow() {
		String corpProjectUuid = RandomTestUtil.randomString();

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_putProvisionBackoff", corpProjectUuid,
			new Exception());

		try {
			ReflectionTestUtils.invokeMethod(
				_projectFaroController, "_checkProvisionBackoff",
				corpProjectUuid);

			Assert.fail();
		}
		catch (FaroException faroException) {
			Response response = faroException.getResponse();

			Assert.assertEquals(
				Response.Status.TOO_MANY_REQUESTS.getStatusCode(),
				response.getStatus());
			Assert.assertNotNull(response.getHeaderString("Retry-After"));
		}
	}

	@Test
	public void testCheckProvisionBackoffWhenFailureWasCleared() {
		String corpProjectUuid = RandomTestUtil.randomString();

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_putProvisionBackoff", corpProjectUuid,
			new Exception());

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_clearProvisionBackoff", corpProjectUuid);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_checkProvisionBackoff", corpProjectUuid);
	}

	@Test
	public void testCheckProvisionBackoffWhenNoFailureWasRecorded() {
		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_checkProvisionBackoff",
			RandomTestUtil.randomString());
	}

	@Test
	public void testCreateOSBAccountEntry1() throws Exception {
		OSBAccountEntry osbAccountEntry =
			_projectFaroController.createOSBAccountEntry(false);

		List<OSBOfferingEntry> offeringEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			offeringEntries.toString(), 1, offeringEntries.size());

		OSBOfferingEntry osbOfferingEntry = offeringEntries.get(0);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			osbOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, osbOfferingEntry.getQuantity());
		Assert.assertNotNull(osbOfferingEntry.getStartDate());
	}

	@Test
	public void testCreateOSBAccountEntry2() throws Exception {
		_assert("1-BusinessTest", ProductConstants.PRODUCT_ENTRY_ID_BUSINESS);
		_assert(
			"2-BusinessLXCTest",
			ProductConstants.PRODUCT_ENTRY_ID_LXC_BUSINESS);
		_assert(
			"3-EnterpriseTest", ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		_assert(
			"4-EnterpriseLXCTest",
			ProductConstants.PRODUCT_ENTRY_ID_LXC_ENTERPRISE);
		_assert("5-ProLXCTest", ProductConstants.PRODUCT_ENTRY_ID_LXC_PRO);
	}

	@Test
	public void testCreateWithSalesforceAccountId() throws Exception {
		String salesforceAccountId = RandomTestUtil.randomString();

		FaroProject faroProject = _createFaroProject(
			RandomTestUtil.randomString(), salesforceAccountId);

		_assertSalesforceAccountId(faroProject, salesforceAccountId);
	}

	@Test
	public void testCreateWithoutSalesforceAccountId() throws Exception {
		String salesforceAccountId = RandomTestUtil.randomString();

		FaroProject faroProject = _createFaroProject(salesforceAccountId, null);

		_assertSalesforceAccountId(faroProject, salesforceAccountId);
	}

	@Test
	public void testUpdateFriendlyURLWhenFriendlyURLIsBlank() throws Exception {
		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		ReflectionTestUtils.setField(
			_projectFaroController, "_groupLocalService", groupLocalService);

		long groupId = RandomTestUtil.randomLong();

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			groupLocalService.getGroup(groupId)
		).thenReturn(
			group
		);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_updateFriendlyURL", "", groupId);

		Mockito.verify(
			group
		).setFriendlyURL(
			null
		);

		Mockito.verify(
			groupLocalService
		).updateGroup(
			group
		);

		Mockito.verify(
			groupLocalService, Mockito.never()
		).updateFriendlyURL(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	@Test
	public void testUpdateFriendlyURLWhenFriendlyURLIsChanged()
		throws Exception {

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		ReflectionTestUtils.setField(
			_projectFaroController, "_groupLocalService", groupLocalService);

		long groupId = RandomTestUtil.randomLong();

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/old-url"
		);

		Mockito.when(
			groupLocalService.getGroup(groupId)
		).thenReturn(
			group
		);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_updateFriendlyURL", "/new-url", groupId);

		Mockito.verify(
			groupLocalService
		).updateFriendlyURL(
			groupId, "/new-url"
		);
	}

	@Test
	public void testUpdateFriendlyURLWhenFriendlyURLIsUnchanged()
		throws Exception {

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		ReflectionTestUtils.setField(
			_projectFaroController, "_groupLocalService", groupLocalService);

		long groupId = RandomTestUtil.randomLong();

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/same-url"
		);

		Mockito.when(
			groupLocalService.getGroup(groupId)
		).thenReturn(
			group
		);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_updateFriendlyURL", "/same-url", groupId);

		Mockito.verify(
			groupLocalService, Mockito.never()
		).updateFriendlyURL(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	private void _assert(String corpProjectUuid, String productEntryId)
		throws Exception {

		OSBAccountEntry osbAccountEntry =
			_projectFaroController.getOSBAccountEntry(corpProjectUuid);

		List<OSBOfferingEntry> offeringEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			offeringEntries.toString(), 1, offeringEntries.size());

		OSBOfferingEntry osbOfferingEntry = offeringEntries.get(0);

		Assert.assertEquals(
			productEntryId, osbOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, osbOfferingEntry.getQuantity());
		Assert.assertNull(osbOfferingEntry.getStartDate());
		Assert.assertEquals(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE,
			osbOfferingEntry.getStatus());
	}

	private void _assertSalesforceAccountId(
			FaroProject faroProject, String salesforceAccountId)
		throws Exception {

		Mockito.verify(
			_faroProjectLocalService
		).addFaroProject(
			Mockito.anyLong(), Mockito.any(), Mockito.eq(salesforceAccountId),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
		);

		Mockito.verify(
			faroProject
		).setSalesforceAccountId(
			salesforceAccountId
		);
	}

	private FaroProject _createFaroProject(
			String osbAccountEntrySalesforceAccountId,
			String salesforceAccountId)
		throws Exception {

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.getUser()
		).thenReturn(
			Mockito.mock(User.class)
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		ProvisioningClient provisioningClient = Mockito.mock(
			ProvisioningClient.class);

		OSBAccountEntry osbAccountEntry = new OSBAccountEntry();

		osbAccountEntry.setSalesforceAccountId(
			osbAccountEntrySalesforceAccountId);

		String corpProjectUuid = RandomTestUtil.randomString();

		Mockito.when(
			provisioningClient.getOSBAccountEntry(corpProjectUuid)
		).thenReturn(
			osbAccountEntry
		);

		ReflectionTestUtils.setField(
			_projectFaroController, "_provisioningClient", provisioningClient);

		Mockito.when(
			_faroProjectLocalService.addFaroProject(
				Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			faroProject
		);

		ReflectionTestUtils.setField(
			_projectFaroController, "_faroProjectLocalService",
			_faroProjectLocalService);

		ReflectionTestUtils.setField(
			_projectFaroController, "contactsEngineClient",
			Mockito.mock(ContactsEngineClient.class));

		try {
			ReflectionTestUtils.invokeMethod(
				_projectFaroController, "_create",
				RandomTestUtil.randomString(), corpProjectUuid,
				Collections.emptyList(), null, Collections.emptyList(),
				RandomTestUtil.randomString(), Collections.emptyList(),
				salesforceAccountId, RandomTestUtil.randomString(),
				FaroProjectConstants.STATE_UNCONFIGURED, "UTC");
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(null);
		}

		Mockito.verify(
			_faroProjectLocalService
		).updateFaroProject(
			faroProject
		);

		return faroProject;
	}

	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final ProjectFaroController _projectFaroController =
		new ProjectFaroController();

	private static class TestPortalCache extends BasePortalCache<String, Long> {

		public TestPortalCache() {
			super(null);
		}

		@Override
		public List<String> getKeys() {
			return new ArrayList<>(_map.keySet());
		}

		@Override
		public String getPortalCacheName() {
			return TestPortalCache.class.getName();
		}

		@Override
		public void removeAll() {
			_map.clear();
		}

		@Override
		protected Long doGet(String key) {
			return _map.get(key);
		}

		@Override
		protected void doPut(String key, Long value, int timeToLive) {
			_map.put(key, value);
		}

		@Override
		protected Long doPutIfAbsent(String key, Long value, int timeToLive) {
			return _map.putIfAbsent(key, value);
		}

		@Override
		protected void doRemove(String key) {
			_map.remove(key);
		}

		@Override
		protected boolean doRemove(String key, Long value) {
			return _map.remove(key, value);
		}

		@Override
		protected Long doReplace(String key, Long value, int timeToLive) {
			return _map.replace(key, value);
		}

		@Override
		protected boolean doReplace(
			String key, Long oldValue, Long newValue, int timeToLive) {

			return _map.replace(key, oldValue, newValue);
		}

		private final Map<String, Long> _map = new ConcurrentHashMap<>();

	}

}