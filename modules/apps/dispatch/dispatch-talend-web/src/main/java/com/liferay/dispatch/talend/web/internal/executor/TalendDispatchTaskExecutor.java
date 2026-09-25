/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.executor;

import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.repository.DispatchFileRepository;
import com.liferay.dispatch.repository.exception.DispatchRepositoryException;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.talend.archive.TalendArchive;
import com.liferay.dispatch.talend.archive.TalendArchiveParserUtil;
import com.liferay.dispatch.talend.web.internal.process.TalendProcess;
import com.liferay.dispatch.talend.web.internal.process.TalendProcessCallable;
import com.liferay.dispatch.talend.web.internal.process.TalendProcessOutput;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.File;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Igor Beslic
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + TalendDispatchTaskExecutor.TALEND,
		"dispatch.task.executor.type=" + TalendDispatchTaskExecutor.TALEND
	},
	service = DispatchTaskExecutor.class
)
public class TalendDispatchTaskExecutor extends BaseDispatchTaskExecutor {

	public static final String TALEND = "talend";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws PortalException {

		TalendArchive talendArchive = _fetchTalendArchive(
			dispatchTrigger.getDispatchTriggerId());

		if (talendArchive == null) {
			throw new PortalException("Unable to fetch Talend archive");
		}

		if (_log.isTraceEnabled()) {
			_log.trace(
				"Execute Talend process for dispatch trigger ID " +
					dispatchTrigger.getDispatchTriggerId());
		}

		try {
			TalendProcess talendProcess = _getTalendProcess(
				dispatchTrigger, talendArchive);

			ProcessChannel<TalendProcessOutput> processChannel =
				_processExecutor.execute(
					talendProcess.getProcessConfig(),
					new TalendProcessCallable(
						talendProcess.getMainMethodArguments(),
						talendArchive.getJobMainClassFQN()));

			NoticeableFuture<TalendProcessOutput> future =
				processChannel.getProcessNoticeableFuture();

			TalendProcessOutput talendProcessOutput = future.get();

			_checkTalendProcessOutput(
				talendProcessOutput, dispatchTaskExecutorOutput);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Completed job for dispatch trigger ID " +
						dispatchTrigger.getDispatchTriggerId());
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
		finally {
			FileUtil.deltree(new File(talendArchive.getJobDirectory()));
		}
	}

	@Override
	public String getName() {
		return TALEND;
	}

	private void _checkTalendProcessOutput(
			TalendProcessOutput talendProcessOutput,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws ProcessException {

		dispatchTaskExecutorOutput.setError(talendProcessOutput.getStdErr());
		dispatchTaskExecutorOutput.setOutput(talendProcessOutput.getStdOut());

		if (talendProcessOutput.hasException()) {
			throw new ProcessException(
				"Subprocess terminated with exit code " +
					talendProcessOutput.getExitCode());
		}
	}

	private TalendArchive _fetchTalendArchive(long dispatchTriggerId)
		throws PortalException {

		FileEntry fileEntry = _dispatchFileRepository.fetchFileEntry(
			dispatchTriggerId);

		if (fileEntry == null) {
			throw new DispatchRepositoryException(
				"Unable to get file entry for dispatch trigger ID " +
					dispatchTriggerId);
		}

		return TalendArchiveParserUtil.parse(fileEntry.getContentStream());
	}

	private TalendProcess _getTalendProcess(
		DispatchTrigger dispatchTrigger, TalendArchive talendArchive) {

		TalendProcess.Builder talendProcessBuilder =
			new TalendProcess.Builder();

		talendProcessBuilder.companyId(dispatchTrigger.getCompanyId());

		Date lastRunStateDate =
			_dispatchTriggerLocalService.fetchPreviousFireDate(
				dispatchTrigger.getDispatchTriggerId());

		talendProcessBuilder.lastRunStartDate(lastRunStateDate);

		talendProcessBuilder.talendArchive(talendArchive);

		UnicodeProperties dispatchTaskSettingsUnicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		if (dispatchTaskSettingsUnicodeProperties != null) {
			for (Map.Entry<String, String> propEntry :
					dispatchTaskSettingsUnicodeProperties.entrySet()) {

				talendProcessBuilder.contextParam(
					propEntry.getKey(),
					TalendContextParamUtil.getValue(
						dispatchTrigger.getCompanyId(),
						dispatchTrigger.getDispatchTriggerId(),
						propEntry.getKey(), propEntry.getValue()));
			}
		}

		return talendProcessBuilder.build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TalendDispatchTaskExecutor.class);

	@Reference
	private DispatchFileRepository _dispatchFileRepository;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private ProcessExecutor _processExecutor;

}