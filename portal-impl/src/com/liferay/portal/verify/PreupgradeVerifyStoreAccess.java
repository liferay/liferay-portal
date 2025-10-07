/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Random;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author István András Dézsi
 */
public class PreupgradeVerifyStoreAccess extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (PropsValues.UPGRADE_DATABASE_DL_STORAGE_CHECK_DISABLED ||
			StartupHelperUtil.isDBNew() ||
			StringUtil.equals(
				PropsValues.DL_STORE_IMPL,
				"com.liferay.portal.store.db.DBStore")) {

			return;
		}

		Store store = null;

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (ServiceReference<Store> serviceReference :
				bundleContext.getServiceReferences(
					Store.class,
					"(store.type=" + PropsValues.DL_STORE_IMPL + ")")) {

			store = bundleContext.getService(serviceReference);

			break;
		}

		if (store == null) {
			throw new VerifyException(
				PropsValues.DL_STORE_IMPL + " is not available");
		}

		long randomCompanyId = _getRandomCompanyId();
		long randomRepositoryId = Math.abs(
			new Random(
			).nextLong(
				1, Long.MAX_VALUE
			));
		String randomFileName = StringUtil.randomString();

		store.addFile(
			randomCompanyId, randomRepositoryId, randomFileName,
			Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(new byte[1024 * 65]));

		if (!store.hasFile(
				randomCompanyId, randomRepositoryId, randomFileName,
				Store.VERSION_DEFAULT)) {

			throw new VerifyException(
				"Unable to create temporary file in store");
		}

		store.deleteFile(
			randomCompanyId, randomRepositoryId, randomFileName,
			Store.VERSION_DEFAULT);
	}

	@Override
	protected boolean isSkipDBPartitions() {
		return true;
	}

	private long _getRandomCompanyId() {
		Set<Long> companyIds = SetUtil.fromArray(
			PortalInstancePool.getCompanyIds());
		Random random = new Random();

		while (true) {
			long randomCompanyId = random.nextLong(1, Long.MAX_VALUE);

			if (!companyIds.contains(randomCompanyId)) {
				return randomCompanyId;
			}
		}
	}

}