/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.BaseDBProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This abstract class should be extended for startup processes that verify the
 * integrity of the database.
 *
 * @author Alexander Chow
 * @author Hugo Huijser
 */
public abstract class VerifyProcess extends BaseDBProcess {

	public void verify() throws VerifyException {
		long start = System.currentTimeMillis();

		try (Connection connection = getConnection()) {
			this.connection = connection;

			process(
				companyId -> {
					if (_log.isInfoEnabled()) {
						String info =
							"Verifying " + ClassUtil.getClassName(this);

						if (Validator.isNotNull(companyId)) {
							info += "#" + companyId;
						}

						_log.info(info);
					}

					try {
						doVerify();
					}
					finally {
						closeConnections();
					}
				});
		}
		catch (VerifyException verifyException) {
			throw verifyException;
		}
		catch (Exception exception) {
			throw new VerifyException(exception);
		}
		finally {
			this.connection = null;

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Completed verification process ",
						ClassUtil.getClassName(this), " in ",
						System.currentTimeMillis() - start, " ms"));
			}
		}
	}

	public void verify(VerifyProcess verifyProcess) throws VerifyException {
		verifyProcess.verify();
	}

	protected void doVerify() throws Exception {
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #processConcurrently(Object[], UnsafeConsumer, String)}
	 */
	@Deprecated
	protected void doVerify(Collection<? extends Callable<Void>> callables)
		throws Exception {

		try {
			ExecutorService executorService = Executors.newFixedThreadPool(
				callables.size());

			List<Future<Void>> futures = executorService.invokeAll(callables);

			executorService.shutdown();

			UnsafeConsumer.accept(futures, Future::get);
		}
		catch (Throwable throwable) {
			Class<?> clazz = getClass();

			throw new Exception(
				"Verification error: " + clazz.getName(), throwable);
		}
	}

	protected boolean isForceConcurrent(
		Collection<? extends Callable<Void>> callables) {

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(VerifyProcess.class);

}