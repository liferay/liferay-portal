/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.internal.ProvisioningClientImpl;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntryBuilder;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.exception.FaroValidationException;
import com.liferay.osb.faro.web.internal.model.display.contacts.ProjectDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Marcos Martins
 */
public class ProjectFaroControllerTest {

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(
			_projectFaroController, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtils.setField(
			_projectFaroController, "_faroUserLocalService",
			_faroUserLocalService);
		ReflectionTestUtils.setField(
			_projectFaroController, "_provisionBackoffPortalCache",
			new TestPortalCache());
		ReflectionTestUtils.setField(
			_projectFaroController, "_provisioningClient",
			new ProvisioningClientImpl());
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", false);
	}

	@Test
	public void testCheckProvisionBackoffWhenFailureRecordedWithinWindow() {
		String provisionKey = RandomTestUtil.randomString();

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_putProvisionBackoff", new Exception(),
			provisionKey);

		try {
			ReflectionTestUtils.invokeMethod(
				_projectFaroController, "_checkProvisionBackoff", provisionKey);

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
		String provisionKey = RandomTestUtil.randomString();

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_putProvisionBackoff", new Exception(),
			provisionKey);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_clearProvisionBackoff", provisionKey);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_checkProvisionBackoff", provisionKey);
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

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		OSBOfferingEntry osbOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			osbOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, osbOfferingEntry.getQuantity());
		Assert.assertNotNull(osbOfferingEntry.getStartDate());
	}

	@Test
	public void testCreateOSBAccountEntry1WithSubscriptionPushEnabled()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		OSBAccountEntry osbAccountEntry =
			_projectFaroController.createOSBAccountEntry(true);

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		OSBOfferingEntry osbOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			ProductConstants.SALESFORCE_PRODUCT_ID_DATA_PLATFORM_PRIVATE_BETA,
			osbOfferingEntry.getProductEntryId());
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

		_createFaroProject(RandomTestUtil.randomString(), salesforceAccountId);

		_assertSalesforceAccountId(salesforceAccountId);
	}

	@Test
	public void testCreateWithoutSalesforceAccountId() throws Exception {
		String salesforceAccountId = RandomTestUtil.randomString();

		_createFaroProject(salesforceAccountId, null);

		_assertSalesforceAccountId(salesforceAccountId);
	}

	@Test
	public void testGetOSBAccountEntryWithSubscriptionPushEnabled()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE);
		osbOfferingEntry.setQuantity(1);

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSalesforceAccountId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			JSONUtil.writeValueAsString(
				new FaroSubscriptionDisplay(
					OSBAccountEntryBuilder.setName(
						RandomTestUtil.randomString()
					).setOfferingEntries(
						Collections.singletonList(osbOfferingEntry)
					).build()))
		);

		OSBAccountEntry osbAccountEntry =
			_projectFaroController.getOSBAccountEntry(faroProject);

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		osbOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE,
			osbOfferingEntry.getProductEntryId());
	}

	@Test
	public void testGetProjectDisplayBySalesforceAccountId() throws Exception {
		String salesforceAccountId = RandomTestUtil.randomString();

		long groupId = _setUpFaroProject(salesforceAccountId);

		_setUpPermissionChecker(false);

		Mockito.when(
			_faroUserLocalService.fetchFaroUser(groupId, _USER_ID)
		).thenReturn(
			Mockito.mock(FaroUser.class)
		);

		_assertGetProjectDisplay(salesforceAccountId);
	}

	@Test
	public void testGetProjectDisplayBySalesforceAccountIdWithOmniadmin()
		throws Exception {

		String salesforceAccountId = RandomTestUtil.randomString();

		_setUpFaroProject(salesforceAccountId);

		_setUpPermissionChecker(true);

		_assertGetProjectDisplay(salesforceAccountId);

		Mockito.verifyNoInteractions(_faroUserLocalService);
	}

	@Test
	public void testGetProjectDisplayBySalesforceAccountIdWithPendingFaroUser()
		throws Exception {

		String salesforceAccountId = RandomTestUtil.randomString();

		long groupId = _setUpFaroProject(salesforceAccountId);

		_setUpPermissionChecker(false);

		Mockito.when(
			_faroUserLocalService.fetchFaroUser(groupId, _EMAIL_ADDRESS)
		).thenReturn(
			Mockito.mock(FaroUser.class)
		);

		_assertGetProjectDisplay(salesforceAccountId);
	}

	@Test
	public void testGetProjectDisplayBySalesforceAccountIdWithoutFaroProject()
		throws Exception {

		_setUpPermissionChecker(true);

		_assertGetProjectDisplayFails(
			RandomTestUtil.randomString(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testGetProjectDisplayBySalesforceAccountIdWithoutFaroUser()
		throws Exception {

		String salesforceAccountId = RandomTestUtil.randomString();

		_setUpFaroProject(salesforceAccountId);

		_setUpPermissionChecker(false);

		_assertGetProjectDisplayFails(
			salesforceAccountId, Response.Status.FORBIDDEN);
	}

	@Test
	public void testGetProvisionKey() {
		String corpProjectUuid = RandomTestUtil.randomString();

		Assert.assertEquals(
			corpProjectUuid,
			ReflectionTestUtils.invokeMethod(
				_projectFaroController, "_getProvisionKey", corpProjectUuid,
				null));

		String salesforceAccountId = RandomTestUtil.randomString();

		Assert.assertEquals(
			salesforceAccountId,
			ReflectionTestUtils.invokeMethod(
				_projectFaroController, "_getProvisionKey", corpProjectUuid,
				salesforceAccountId));
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

	@Test
	public void testUpdateSubscription() throws Exception {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		String salesforceAccountId = RandomTestUtil.randomString();

		try {
			_projectFaroController.updateSubscription(
				salesforceAccountId, null);

			Assert.fail();
		}
		catch (FaroException faroException) {
			Response response = faroException.getResponse();

			Assert.assertEquals(
				Response.Status.NOT_FOUND.getStatusCode(),
				response.getStatus());
		}

		Mockito.verify(
			_faroProjectLocalService, Mockito.never()
		).fetchFaroProjectByCorpProjectUuid(
			Mockito.anyString()
		);

		Mockito.verify(
			_faroProjectLocalService
		).fetchFaroProjectBySalesforceAccountId(
			salesforceAccountId
		);
	}

	@Test
	public void testUpdateSubscriptionWithSubscriptionPushDisabled()
		throws Exception {

		String corpProjectUuid = RandomTestUtil.randomString();

		try {
			_projectFaroController.updateSubscription(corpProjectUuid, null);

			Assert.fail();
		}
		catch (FaroException faroException) {
			Response response = faroException.getResponse();

			Assert.assertEquals(
				Response.Status.NOT_FOUND.getStatusCode(),
				response.getStatus());
		}

		Mockito.verify(
			_faroProjectLocalService
		).fetchFaroProjectByCorpProjectUuid(
			corpProjectUuid
		);

		Mockito.verify(
			_faroProjectLocalService, Mockito.never()
		).fetchFaroProjectBySalesforceAccountId(
			Mockito.anyString()
		);
	}

	@Test
	public void testValidateOfferingEntries() {
		_validateOfferingEntries(ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);

		_assertValidateOfferingEntriesFails(
			ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE);
	}

	@Test
	public void testValidateOfferingEntriesWithSubscriptionPushEnabled() {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		_validateOfferingEntries(
			ProductConstants.PRODUCT_ENTRY_ID_DATA_PLATFORM_ENTERPRISE);
		_validateOfferingEntries(
			ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE);

		_assertValidateOfferingEntriesFails(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		_assertValidateOfferingEntriesFails(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE_CONTACTS);
	}

	private void _assert(String corpProjectUuid, String productEntryId)
		throws Exception {

		OSBAccountEntry osbAccountEntry =
			_projectFaroController.getOSBAccountEntry(corpProjectUuid);

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		OSBOfferingEntry osbOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			productEntryId, osbOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, osbOfferingEntry.getQuantity());
		Assert.assertNull(osbOfferingEntry.getStartDate());
		Assert.assertEquals(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE,
			osbOfferingEntry.getStatus());
	}

	private void _assertGetProjectDisplay(String salesforceAccountId)
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		try (MockedStatic<FaroUserLocalServiceUtil>
				faroUserLocalServiceUtilMockedStatic = Mockito.mockStatic(
					FaroUserLocalServiceUtil.class)) {

			ProjectDisplay projectDisplay =
				_projectFaroController.getProjectDisplay(salesforceAccountId);

			Assert.assertEquals(
				salesforceAccountId, projectDisplay.getSalesforceAccountId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(null);
		}
	}

	private void _assertGetProjectDisplayFails(
			String salesforceAccountId, Response.Status status)
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		try {
			_projectFaroController.getProjectDisplay(salesforceAccountId);

			Assert.fail();
		}
		catch (FaroException faroException) {
			Response response = faroException.getResponse();

			Assert.assertEquals(status.getStatusCode(), response.getStatus());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(null);
		}
	}

	private void _assertSalesforceAccountId(String salesforceAccountId)
		throws Exception {

		Mockito.verify(
			_faroProjectLocalService
		).addFaroProject(
			Mockito.anyLong(), Mockito.any(), Mockito.eq(salesforceAccountId),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.eq(salesforceAccountId),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.any()
		);
	}

	private void _assertValidateOfferingEntriesFails(String productEntryId) {
		try {
			_validateOfferingEntries(productEntryId);

			Assert.fail();
		}
		catch (FaroValidationException faroValidationException) {
			Response response = faroValidationException.getResponse();

			Assert.assertEquals(
				Response.Status.BAD_REQUEST.getStatusCode(),
				response.getStatus());
		}
	}

	private void _createFaroProject(
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
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			faroProject
		);

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
	}

	private long _setUpFaroProject(String salesforceAccountId) {
		long groupId = RandomTestUtil.randomLong();

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			faroProject.getSalesforceAccountId()
		).thenReturn(
			salesforceAccountId
		);

		Mockito.when(
			faroProject.getTimeZoneId()
		).thenReturn(
			"UTC"
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectBySalesforceAccountId(
				salesforceAccountId)
		).thenReturn(
			faroProject
		);

		return groupId;
	}

	private void _setUpPermissionChecker(boolean omniadmin) {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getEmailAddress()
		).thenReturn(
			_EMAIL_ADDRESS
		);

		Mockito.when(
			permissionChecker.getUser()
		).thenReturn(
			user
		);

		Mockito.when(
			permissionChecker.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			permissionChecker.isOmniadmin()
		).thenReturn(
			omniadmin
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private void _validateOfferingEntries(String productEntryId) {
		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(productEntryId);
		osbOfferingEntry.setQuantity(1);

		ReflectionTestUtils.invokeMethod(
			_projectFaroController, "_validateOfferingEntries",
			Collections.singletonList(osbOfferingEntry));
	}

	private static final String _EMAIL_ADDRESS =
		RandomTestUtil.randomString() + "@liferay.com";

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final FaroUserLocalService _faroUserLocalService = Mockito.mock(
		FaroUserLocalService.class);
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