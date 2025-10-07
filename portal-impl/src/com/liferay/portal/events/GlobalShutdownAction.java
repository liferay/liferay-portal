/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.deploy.auto.AutoDeployDir;
import com.liferay.portal.kernel.deploy.auto.AutoDeployUtil;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.struts.AuthPublicPathRegistry;

/**
 * @author Brian Wing Shun Chan
 */
public class GlobalShutdownAction extends SimpleAction {

	@Override
	public void run(String[] ids) {

		// Lower shutdown levels have dependences on higher levels, therefore
		// lower ones need to shutdown before higher ones. Components within the
		// same shutdown level should not depend on each other.

		shutdownLevel1();
		shutdownLevel2();
		shutdownLevel3();
		shutdownLevel4();
		shutdownLevel5();
		shutdownLevel6();
		shutdownLevel7();
	}

	protected ThreadGroup getThreadGroup() {
		Thread currentThread = Thread.currentThread();

		ThreadGroup threadGroup = currentThread.getThreadGroup();

		for (int i = 0; i < 10; i++) {
			if (threadGroup.getParent() == null) {
				break;
			}

			threadGroup = threadGroup.getParent();
		}

		return threadGroup;
	}

	protected Thread[] getThreads(ThreadGroup threadGroup) {
		Thread[] threads = new Thread[threadGroup.activeCount() * 2];

		threadGroup.enumerate(threads);

		return threads;
	}

	protected void shutdownLevel1() {

		// Authentication

		AuthPublicPathRegistry.unregister(PropsValues.AUTH_PUBLIC_PATHS);
	}

	protected void shutdownLevel2() {

		// Auto deploy

		AutoDeployUtil.unregisterDir(AutoDeployDir.DEFAULT_NAME);

		// Hot deploy

		HotDeployUtil.unregisterListeners();
	}

	protected void shutdownLevel3() {
	}

	protected void shutdownLevel4() {
	}

	protected void shutdownLevel5() {
	}

	protected void shutdownLevel6() {

		// Thread local registry

		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

	protected void shutdownLevel7() {

		// Programmatically exit

		if (GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.SHUTDOWN_PROGRAMMATICALLY_EXIT))) {

			Thread currentThread = Thread.currentThread();

			ThreadGroup threadGroup = getThreadGroup();

			Thread[] threads = getThreads(threadGroup);

			for (Thread thread : threads) {
				if ((thread == null) || (thread == currentThread)) {
					continue;
				}

				try {
					thread.interrupt();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}

			threadGroup.destroy();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GlobalShutdownAction.class);

}