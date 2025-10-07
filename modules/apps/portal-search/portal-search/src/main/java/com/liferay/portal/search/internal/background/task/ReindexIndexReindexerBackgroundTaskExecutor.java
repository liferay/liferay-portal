/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.background.task;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.spi.reindexer.IndexReindexerRegistry;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gustavo Lima
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.portal.search.internal.background.task.ReindexIndexReindexerBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class ReindexIndexReindexerBackgroundTaskExecutor
	extends BaseReindexBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	protected void reindex(
			String className, long[] companyIds, String executionMode)
		throws Exception {

		if (Validator.isBlank(className)) {
			Collection<IndexReindexer> indexReindexers =
				_indexReindexerRegistry.getIndexReindexers();

			for (IndexReindexer indexReindexer : indexReindexers) {
				Class<?> clazz = indexReindexer.getClass();

				_reindex(
					clazz.getName(), companyIds, executionMode, indexReindexer,
					null, null);
			}
		}
		else {
			IndexReindexer indexReindexer =
				_indexReindexerRegistry.getIndexReindexer(className);

			if (indexReindexer == null) {
				return;
			}

			_reindex(
				className, companyIds, executionMode, indexReindexer,
				ReindexBackgroundTaskConstants.SINGLE_START,
				ReindexBackgroundTaskConstants.SINGLE_END);
		}
	}

	private void _reindex(
			String className, long[] companyIds, String executionMode,
			IndexReindexer indexReindexer, String startPhase, String endPhase)
		throws Exception {

		for (long companyId : companyIds) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				if (startPhase != null) {
					_reindexStatusMessageSender.sendStatusMessage(
						startPhase, companyId, companyIds);
				}

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Start reindexing company ", companyId,
							" for class name ", className,
							" with execution mode ", executionMode));
				}

				indexReindexer.reindex(companyId, executionMode);

				if (endPhase != null) {
					_reindexStatusMessageSender.sendStatusMessage(
						endPhase, companyId, companyIds);
				}

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Finished reindexing company ", companyId,
							" for class name ", className,
							" with execution mode ", executionMode));
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexIndexReindexerBackgroundTaskExecutor.class);

	@Reference
	private IndexReindexerRegistry _indexReindexerRegistry;

	@Reference
	private ReindexStatusMessageSender _reindexStatusMessageSender;

}