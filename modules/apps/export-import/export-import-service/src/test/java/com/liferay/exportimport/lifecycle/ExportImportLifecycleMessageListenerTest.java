/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.lifecycle;

import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 */
public class ExportImportLifecycleMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testDoReceiveWithAsyncListener() throws Exception {
		List<ExportImportLifecycleEvent> exportImportLifecycleEvents =
			_registerExportImportLifecycleListener(true);

		AsyncExportImportLifecycleMessageListener
			asyncExportImportLifecycleMessageListener =
				new AsyncExportImportLifecycleMessageListener();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		asyncExportImportLifecycleMessageListener.activate(bundleContext);

		ExportImportLifecycleEvent exportImportLifecycleEvent =
			new ExportImportLifecycleEventImpl();

		asyncExportImportLifecycleMessageListener.doReceive(
			_createMessage(exportImportLifecycleEvent));

		asyncExportImportLifecycleMessageListener.deactivate();

		Assert.assertSame(
			exportImportLifecycleEvent, exportImportLifecycleEvents.get(0));
		Assert.assertEquals(
			exportImportLifecycleEvents.toString(), 1,
			exportImportLifecycleEvents.size());
	}

	@Test
	public void testDoReceiveWithSyncListener() throws Exception {
		List<ExportImportLifecycleEvent> exportImportLifecycleEvents =
			_registerExportImportLifecycleListener(false);

		SyncExportImportLifecycleMessageListener
			syncExportImportLifecycleMessageListener =
				new SyncExportImportLifecycleMessageListener();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		syncExportImportLifecycleMessageListener.activate(bundleContext);

		ExportImportLifecycleEvent exportImportLifecycleEvent =
			new ExportImportLifecycleEventImpl();

		syncExportImportLifecycleMessageListener.doReceive(
			_createMessage(exportImportLifecycleEvent));

		syncExportImportLifecycleMessageListener.deactivate();

		Assert.assertSame(
			exportImportLifecycleEvent, exportImportLifecycleEvents.get(0));
		Assert.assertEquals(
			exportImportLifecycleEvents.toString(), 1,
			exportImportLifecycleEvents.size());
	}

	private Message _createMessage(
		ExportImportLifecycleEvent exportImportLifecycleEvent) {

		Message message = new Message();

		message.put("exportImportLifecycleEvent", exportImportLifecycleEvent);

		return message;
	}

	private List<ExportImportLifecycleEvent>
		_registerExportImportLifecycleListener(boolean parallel) {

		List<ExportImportLifecycleEvent> exportImportLifecycleEvents =
			new ArrayList<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ExportImportLifecycleListener.class,
			new ExportImportLifecycleListener() {

				@Override
				public boolean isParallel() {
					return parallel;
				}

				@Override
				public void onExportImportLifecycleEvent(
					ExportImportLifecycleEvent exportImportLifecycleEvent) {

					exportImportLifecycleEvents.add(exportImportLifecycleEvent);
				}

			},
			null);

		return exportImportLifecycleEvents;
	}

	private ServiceRegistration<ExportImportLifecycleListener>
		_serviceRegistration;

}