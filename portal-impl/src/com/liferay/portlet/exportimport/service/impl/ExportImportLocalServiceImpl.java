/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.impl;

import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportControllerRegistryUtil;
import com.liferay.exportimport.kernel.controller.ImportController;
import com.liferay.exportimport.kernel.exception.ExportImportIOException;
import com.liferay.exportimport.kernel.exception.ExportImportRuntimeException;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portlet.exportimport.service.base.ExportImportLocalServiceBaseImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author Daniel Kocsis
 */
public class ExportImportLocalServiceImpl
	extends ExportImportLocalServiceBaseImpl {

	@CTAware
	@Override
	public File exportLayoutsAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		try {
			ExportController layoutExportController =
				ExportImportControllerRegistryUtil.getExportController(
					Layout.class.getName());

			return layoutExportController.export(exportImportConfiguration);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public long exportLayoutsAsFileInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		if (!DLValidatorUtil.isValidName(exportImportConfiguration.getName())) {
			throw new LARFileNameException(exportImportConfiguration.getName());
		}

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		return backgroundTask.getBackgroundTaskId();
	}

	@CTAware
	@Override
	public long exportLayoutsAsFileInBackground(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return exportLayoutsAsFileInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId));
	}

	@CTAware
	@Override
	public File exportPortletInfoAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		try {
			ExportController portletExportController =
				ExportImportControllerRegistryUtil.getExportController(
					Portlet.class.getName());

			return portletExportController.export(exportImportConfiguration);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public long exportPortletInfoAsFileInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		String fileName = MapUtil.getString(
			exportImportConfiguration.getSettingsMap(), "fileName");

		if (!DLValidatorUtil.isValidName(fileName)) {
			throw new LARFileNameException(fileName);
		}

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		return backgroundTask.getBackgroundTaskId();
	}

	@CTAware
	@Override
	public long exportPortletInfoAsFileInBackground(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return exportPortletInfoAsFileInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId));
	}

	@CTAware
	@Override
	public void importLayouts(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			ImportController layoutImportController =
				ExportImportControllerRegistryUtil.getImportController(
					Layout.class.getName());

			layoutImportController.importFile(exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public void importLayouts(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			importLayouts(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportLocalServiceImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@CTAware
	@Override
	public void importLayoutsDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController layoutImportController =
				ExportImportControllerRegistryUtil.getImportController(
					Layout.class.getName());

			layoutImportController.importDataDeletions(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public long importLayoutSetPrototypeInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_SET_PROTOTYPE_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
				).put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		backgroundTask.addAttachment(userId, file.getName(), file);

		return backgroundTask.getBackgroundTaskId();
	}

	@CTAware
	@Override
	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		backgroundTask.addAttachment(userId, file.getName(), file);

		return backgroundTask.getBackgroundTaskId();
	}

	@CTAware
	@Override
	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return importLayoutsInBackground(
				userId, exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportLocalServiceImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@CTAware
	@Override
	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId, File file)
		throws PortalException {

		return importPortletInfoInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			file);
	}

	@CTAware
	@Override
	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException {

		return importLayoutsInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			inputStream);
	}

	@CTAware
	@Override
	public void importPortletDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController portletImportController =
				ExportImportControllerRegistryUtil.getImportController(
					Portlet.class.getName());

			portletImportController.importDataDeletions(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			ImportController portletImportController =
				ExportImportControllerRegistryUtil.getImportController(
					Portlet.class.getName());

			portletImportController.importFile(exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			while (true) {
				if (throwable == null) {
					break;
				}

				if (throwable instanceof LocaleException) {
					throw (PortalException)throwable;
				}

				if (throwable instanceof PortletDataException) {
					throwable = throwable.getCause();
				}
				else {
					break;
				}
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			importPortletInfo(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportLocalServiceImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@CTAware
	@Override
	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		backgroundTask.addAttachment(userId, file.getName(), file);

		return backgroundTask.getBackgroundTaskId();
	}

	@CTAware
	@Override
	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return importPortletInfoInBackground(
				userId, exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportLocalServiceImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@CTAware
	@Override
	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId, File file)
		throws PortalException {

		return importPortletInfoInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			file);
	}

	@CTAware
	@Override
	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException {

		return importPortletInfoInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			inputStream);
	}

	@CTAware
	@Override
	public long mergeLayoutSetPrototypeInBackground(
			long userId, long groupId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.addBackgroundTask(
				userId, groupId, exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
				).put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		return backgroundTask.getBackgroundTaskId();
	}

	@CTAware
	@Override
	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController layoutImportController =
				ExportImportControllerRegistryUtil.getImportController(
					Layout.class.getName());

			return layoutImportController.validateFile(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return validateImportLayoutsFile(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportLocalServiceImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_VALIDATE_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_VALIDATE);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@CTAware
	@Override
	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController portletImportController =
				ExportImportControllerRegistryUtil.getImportController(
					Portlet.class.getName());

			return portletImportController.validateFile(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportLocalServiceImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@CTAware
	@Override
	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return validateImportPortletInfo(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportLocalServiceImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_VALIDATE_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_VALIDATE);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@BeanReference(type = ExportImportConfigurationLocalService.class)
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

}