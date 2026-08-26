/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.verify.VerifyException;
import com.liferay.portal.verify.VerifyProcess;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(
	property = "run.on.portal.upgrade=true", service = VerifyProcess.class
)
public class PostUpgradeDataCleanupVerifyProcess extends VerifyProcess {

	@Override
	public void verify() throws VerifyException {
		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				try {
					super.verify();
				}
				finally {
					DBUpgrader.stopUpgradeLogAppender();
				}

				return null;
			});
	}

	@Override
	protected void doVerify() throws Exception {
		for (PostUpgradeDataCleanupProcess postUpgradeDataCleanupProcess :
				_getPostUpgradeDataCleanupProcesses()) {

			try (LoggingTimer loggingTimer = new LoggingTimer(
					ClassUtil.getClassName(postUpgradeDataCleanupProcess))) {

				postUpgradeDataCleanupProcess.cleanUp();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private List<PostUpgradeDataCleanupProcess>
		_getPostUpgradeDataCleanupProcesses() {

		List<PostUpgradeDataCleanupProcess> postUpgradeDataCleanupProcesses =
			ListUtil.fromArray(
				new ClassNamePostUpgradeDataCleanupProcess(
					_classNameLocalService, _companyLocalService, connection,
					_objectDefinitionLocalService),
				new ResourceActionPostUpgradeDataCleanupProcess(
					_companyLocalService, connection,
					_objectDefinitionLocalService, _resourceActionLocalService),
				new ServiceComponentPostUpgradeDataCleanupProcess(
					connection, _serviceComponentLocalService),
				new StorePostUpgradeDataCleanupProcess(_store));

		IndexInformation indexInformation = _indexInformationSnapshot.get();
		IndexNameBuilder indexNameBuilder = _indexNameBuilderSnapshot.get();

		if ((indexInformation == null) || (indexNameBuilder == null)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Skipping search index cleanup: search engine unavailable");
			}

			return postUpgradeDataCleanupProcesses;
		}

		postUpgradeDataCleanupProcesses.add(
			new SearchIndexPostUpgradeDataCleanupProcess(
				indexInformation, indexNameBuilder));

		return postUpgradeDataCleanupProcesses;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PostUpgradeDataCleanupVerifyProcess.class);

	private static final Snapshot<IndexInformation> _indexInformationSnapshot =
		new Snapshot<>(
			PostUpgradeDataCleanupVerifyProcess.class, IndexInformation.class);
	private static final Snapshot<IndexNameBuilder> _indexNameBuilderSnapshot =
		new Snapshot<>(
			PostUpgradeDataCleanupVerifyProcess.class, IndexNameBuilder.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTLETS_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ServiceComponentLocalService _serviceComponentLocalService;

	@Reference(target = "(default=true)")
	private Store _store;

}