/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.model.listener;

import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.internal.notifications.PortalInstanceNotificationUtil;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class BatchEngineImportTaskModelListenerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_cacheEnabled = ReflectionTestUtil.getAndSetFieldValue(
			PortalInstancePool.class, "_cacheEnabled", true);

		ReflectionTestUtil.setFieldValue(
			_batchEngineImportTaskModelListener, "_companyLocalService",
			_companyLocalService);

		UserNotificationEventLocalServiceUtil.setService(
			_userNotificationEventLocalService);

		Mockito.when(
			_batchEngineImportTask.getBatchEngineImportTaskId()
		).thenReturn(
			_BATCH_ENGINE_IMPORT_TASK_ID
		);

		Mockito.when(
			_batchEngineImportTask.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_company.getWebId()
		).thenReturn(
			_WEB_ID
		);

		Mockito.when(
			_companyLocalService.fetchCompany(_COMPANY_ID)
		).thenReturn(
			_company
		);

		PortalInstance portalInstance = new PortalInstance();

		portalInstance.setCompanyId(() -> _COMPANY_ID);

		PortalInstanceNotificationUtil.addPortalInstance(
			_BATCH_ENGINE_IMPORT_TASK_ID, new Object(), portalInstance);
	}

	@After
	public void tearDown() {
		PortalInstanceNotificationUtil.removePortalInstances(
			_BATCH_ENGINE_IMPORT_TASK_ID);

		PortalInstancePool.remove(_COMPANY_ID);

		ReflectionTestUtil.setFieldValue(
			PortalInstancePool.class, "_cacheEnabled", _cacheEnabled);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		PortalInstancePool.add(_company);

		_onAfterUpdate(
			BatchEngineTaskExecuteStatus.COMPLETED,
			RandomTestUtil.randomString());

		Mockito.verifyNoInteractions(_userNotificationEventLocalService);

		_onAfterUpdate(
			BatchEngineTaskExecuteStatus.STARTED,
			PortalInstance.class.getName());

		Mockito.verifyNoInteractions(_userNotificationEventLocalService);

		_onAfterUpdate(
			BatchEngineTaskExecuteStatus.COMPLETED,
			PortalInstance.class.getName());

		JSONObject payloadJSONObject = _getPayloadJSONObject();

		Assert.assertEquals(
			PortalInstancesNotificationConstants.OPERATION_TYPE_ADD,
			payloadJSONObject.getString("operationType"));
		Assert.assertEquals(
			_WEB_ID, payloadJSONObject.getString("portalInstanceId"));
		Assert.assertEquals(
			PortalInstancesNotificationConstants.STATUS_SUCCESS,
			payloadJSONObject.getString("status"));
	}

	@Test
	public void testOnAfterUpdateWhenCompanyIsNotInPortalInstancePool()
		throws Exception {

		_onAfterUpdate(
			BatchEngineTaskExecuteStatus.COMPLETED,
			PortalInstance.class.getName());

		JSONObject payloadJSONObject = _getPayloadJSONObject();

		Assert.assertEquals(
			"the-instance-was-created-but-its-initialization-failed-check-" +
				"the-server-logs",
			payloadJSONObject.getString("errorMessageKey"));
		Assert.assertEquals(
			PortalInstancesNotificationConstants.STATUS_FAILED,
			payloadJSONObject.getString("status"));
	}

	private JSONObject _getPayloadJSONObject() throws Exception {
		ArgumentCaptor<JSONObject> argumentCaptor = ArgumentCaptor.forClass(
			JSONObject.class);

		Mockito.verify(
			_userNotificationEventLocalService
		).sendUserNotificationEvents(
			Mockito.eq(_USER_ID),
			Mockito.eq(PortalInstancesPortletKeys.PORTAL_INSTANCES),
			Mockito.eq(UserNotificationDeliveryConstants.TYPE_WEBSITE),
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private void _onAfterUpdate(
		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus,
		String className) {

		Mockito.when(
			_batchEngineImportTask.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_batchEngineImportTask.getExecuteStatus()
		).thenReturn(
			batchEngineTaskExecuteStatus.name()
		);

		_batchEngineImportTaskModelListener.onAfterUpdate(
			null, _batchEngineImportTask);
	}

	private static final long _BATCH_ENGINE_IMPORT_TASK_ID =
		RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private static final String _WEB_ID = RandomTestUtil.randomString();

	private final BatchEngineImportTask _batchEngineImportTask = Mockito.mock(
		BatchEngineImportTask.class);
	private final BatchEngineImportTaskModelListener
		_batchEngineImportTaskModelListener =
			new BatchEngineImportTaskModelListener();
	private boolean _cacheEnabled;
	private final Company _company = Mockito.mock(Company.class);
	private final CompanyLocalService _companyLocalService = Mockito.mock(
		CompanyLocalService.class);
	private final UserNotificationEventLocalService
		_userNotificationEventLocalService = Mockito.mock(
			UserNotificationEventLocalService.class);

}