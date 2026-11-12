/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.scheduler;

import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncConstants;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.internal.event.AntivirusAsyncEventListenerManager;
import com.liferay.antivirus.async.store.util.AntivirusAsyncUtil;
import com.liferay.document.library.kernel.antivirus.AntivirusScanner;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Date;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Christopher Kian
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {"osgi.command.function=scan", "osgi.command.scope=antivirus"},
	service = SchedulerJobConfiguration.class
)
public class AntivirusAsyncFileStoreSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public String getDestinationName() {
		return AntivirusAsyncDestinationNames.ANTIVIRUS_BATCH;
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		if (!_antivirusScanner.isActive()) {
			return () -> {
			};
		}

		if (StringUtil.startsWith(
				PropsUtil.get(PropsKeys.DL_STORE_IMPL),
				"com.liferay.portal.store.file.system")) {

			java.io.File file =
				(java.io.File)_storeServiceReference.getProperty("rootDir");

			return () -> _scan((String)file.getAbsolutePath());
		}

		return this::_scanDLFileEntries;
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		AntivirusAsyncConfiguration antivirusAsyncConfiguration =
			ConfigurableUtil.createConfigurable(
				AntivirusAsyncConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			antivirusAsyncConfiguration.batchScanCronExpression());

		_triggerConfiguration.setStartDate(
			new Date(
				System.currentTimeMillis() + TimeUnit.SECOND.toMillis(30)));
	}

	private void _scan(String rootDirAbsolutePathString) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("Scanning " + rootDirAbsolutePathString);
		}

		Path rootPath = Paths.get(rootDirAbsolutePathString);

		Files.walkFileTree(
			rootPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
						Path dirPath, IOException ioException)
					throws IOException {

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					try {
						_scheduleAntivirusScan(rootPath, filePath);
					}
					catch (Throwable throwable) {
						_log.error(
							"Unable to schedule antivirus scan for " + filePath,
							throwable);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _scanDLFileEntries() {
		try {
			ActionableDynamicQuery actionableDynamicQuery =
				_dlFileVersionLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setCompanyId(
				CompanyThreadLocal.getCompanyId());
			actionableDynamicQuery.setPerformActionMethod(
				(DLFileVersion dlFileVersion) -> {
					DLFileEntry dlFileEntry = dlFileVersion.getFileEntry();

					_scheduleAntivirusScan(
						dlFileEntry.getModelClassName(),
						dlFileVersion.getFileEntryId(),
						dlFileVersion.getCompanyId(),
						dlFileVersion.getExtension(), dlFileEntry.getName(),
						AntivirusAsyncUtil.getJobName(
							dlFileEntry.getCompanyId(),
							dlFileEntry.getDataRepositoryId(),
							dlFileEntry.getFileName(),
							dlFileVersion.getStoreFileName()),
						dlFileEntry.getDataRepositoryId(),
						dlFileEntry.getSize(), dlFileEntry.getFileName(),
						dlFileEntry.getUserId(),
						dlFileVersion.getStoreFileName());
				});

			actionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}
	}

	private void _scheduleAntivirusScan(Path rootPath, Path filePath) {
		Path relativePath = rootPath.relativize(filePath);

		if (relativePath.getNameCount() <= 1) {
			return;
		}

		// Company ID

		Path companyIdPath = relativePath.getName(0);

		long companyId = GetterUtil.getLong(companyIdPath.toString());

		relativePath = companyIdPath.relativize(relativePath);

		// Repository ID

		Path repositoryIdPath = relativePath.getName(0);

		long repositoryId = GetterUtil.getLong(repositoryIdPath.toString());

		if (repositoryId == AntivirusAsyncConstants.REPOSITORY_ID_QUARANTINE) {
			return;
		}

		relativePath = repositoryIdPath.relativize(relativePath);

		// Version label

		String versionLabel = String.valueOf(relativePath.getFileName());

		relativePath = relativePath.subpath(0, relativePath.getNameCount() - 1);

		String fileNameFragment = StringPool.BLANK;

		int x = versionLabel.lastIndexOf(CharPool.UNDERLINE);

		if (x > -1) {
			if (x > 0) {
				fileNameFragment = versionLabel.substring(0, x);
			}

			int y = versionLabel.lastIndexOf(CharPool.PERIOD);

			versionLabel = versionLabel.substring(x + 1, y);
		}

		// File name

		String fileName = String.valueOf(relativePath.getFileName());

		// Directory name

		String fileDirectory = StringPool.BLANK;

		if (relativePath.getNameCount() > 1) {
			fileDirectory = String.valueOf(
				relativePath.subpath(0, relativePath.getNameCount() - 1));
		}

		String fileExtension = _file.getExtension(fileName);

		if (fileExtension.equals("afsh")) {
			fileExtension = StringPool.BLANK;
			fileName = _file.stripExtension(fileName);
		}

		if (!fileNameFragment.isEmpty()) {
			String fileDirectoryParts = fileDirectory.replaceAll(
				StringPool.SLASH, StringPool.BLANK);

			if (fileName.startsWith(fileDirectoryParts)) {
				fileDirectory = StringPool.BLANK;
			}
		}

		if (!fileDirectory.isEmpty()) {
			fileName = StringBundler.concat(
				fileDirectory, StringPool.SLASH, fileName);

			if (fileNameFragment.isEmpty()) {
				fileName += StringPool.SLASH;
			}
		}

		long size = -1;

		try {
			if (Files.exists(filePath)) {
				size = Files.size(filePath);
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		_scheduleAntivirusScan(
			null, 0, companyId, fileExtension, fileName,
			AntivirusAsyncUtil.getJobName(
				companyId, repositoryId, fileName, versionLabel),
			repositoryId, size, null, 0L, versionLabel);
	}

	private void _scheduleAntivirusScan(
		String className, long classPK, long companyId, String fileExtension,
		String fileName, String jobName, long repositoryId, long size,
		String sourceFileName, long userId, String versionLabel) {

		Message message = new Message();

		message.put("className", className);
		message.put("classPK", classPK);
		message.put("companyId", companyId);
		message.put("fileExtension", fileExtension);
		message.put("fileName", fileName);
		message.put("jobName", jobName);
		message.put("repositoryId", repositoryId);
		message.put("size", size);
		message.put("sourceFileName", sourceFileName);
		message.put("userId", userId);
		message.put("versionLabel", versionLabel);

		_antivirusAsyncEventListenerManager.onPrepare(message);

		_messageBus.sendMessage(
			AntivirusAsyncDestinationNames.ANTIVIRUS, message);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AntivirusAsyncFileStoreSchedulerJobConfiguration.class);

	@Reference
	private AntivirusAsyncEventListenerManager
		_antivirusAsyncEventListenerManager;

	@Reference
	private AntivirusScanner _antivirusScanner;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private File _file;

	@Reference
	private MessageBus _messageBus;

	@Reference(target = "(rootDir=*)")
	private ServiceReference<Store> _storeServiceReference;

	private TriggerConfiguration _triggerConfiguration;

}