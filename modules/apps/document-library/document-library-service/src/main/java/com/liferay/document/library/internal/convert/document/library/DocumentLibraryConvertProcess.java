/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.convert.document.library;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.comparator.FileVersionVersionComparator;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.convert.BaseConvertProcess;
import com.liferay.portal.convert.ConvertProcess;
import com.liferay.portal.convert.documentlibrary.DLStoreConvertProcess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.MaintenanceUtil;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Minhchau Dang
 * @author Alexander Chow
 * @author László Csontos
 */
@Component(service = ConvertProcess.class)
public class DocumentLibraryConvertProcess extends BaseConvertProcess {

	@Override
	public String getConfigurationErrorMessage() {
		return "there-are-no-stores-configured";
	}

	@Override
	public String getDescription() {
		return "migrate-documents-from-one-repository-to-another";
	}

	@Override
	public String getParameterDescription() {
		return "please-select-a-new-repository-hook";
	}

	@Override
	public String[] getParameterNames() {
		Set<String> storeTypes = _serviceTrackerMap.keySet();

		StringBundler sb = new StringBundler((storeTypes.size() * 2) + 2);

		sb.append(PropsKeys.DL_STORE_IMPL);
		sb.append(StringPool.EQUAL);

		for (String storeType : storeTypes) {
			Class<?> clazz = _store.getClass();

			if (!storeType.equals(clazz.getName())) {
				sb.append(storeType);
				sb.append(StringPool.SEMICOLON);
			}
		}

		return new String[] {
			sb.toString(), "delete-files-from-previous-repository=checkbox"
		};
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void validate() {
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, Store.class, "(ct.aware=true)",
			(serviceReference1, emitter) -> emitter.emit(
				String.valueOf(serviceReference1.getProperty("store.type"))));

		_dlStoreConvertProcesses = ServiceTrackerListFactory.open(
			bundleContext, DLStoreConvertProcess.class);
	}

	@Deactivate
	protected void deactivate() {
		_dlStoreConvertProcesses.close();

		_serviceTrackerMap.close();
	}

	@Override
	protected void doConvert() throws Exception {
		String targetStoreClassName = getTargetStoreClassName();

		migrateDLStoreConvertProcesses(
			_store, _serviceTrackerMap.getService(targetStoreClassName));

		MaintenanceUtil.appendStatus(
			StringBundler.concat(
				"Please set ", PropsKeys.DL_STORE_IMPL,
				" in your portal-ext.properties to use ",
				targetStoreClassName));

		PropsValues.DL_STORE_IMPL = targetStoreClassName;
	}

	protected List<FileVersion> getFileVersions(FileEntry fileEntry) {
		return ListUtil.sort(
			fileEntry.getFileVersions(WorkflowConstants.STATUS_ANY),
			new FileVersionVersionComparator(true));
	}

	protected String getTargetStoreClassName() {
		String[] values = getParameterValues();

		return values[0];
	}

	protected boolean isDeleteFilesFromSourceStore() {
		String[] values = getParameterValues();

		return GetterUtil.getBoolean(values[1]);
	}

	protected void migrateDLStoreConvertProcesses(
			Store sourceStore, Store targetStore)
		throws PortalException {

		Collection<DLStoreConvertProcess> dlStoreConvertProcesses =
			_getDLStoreConvertProcesses();

		for (DLStoreConvertProcess dlStoreConvertProcess :
				dlStoreConvertProcesses) {

			if (isDeleteFilesFromSourceStore()) {
				dlStoreConvertProcess.move(sourceStore, targetStore);
			}
			else {
				dlStoreConvertProcess.copy(sourceStore, targetStore);
			}
		}
	}

	private Collection<DLStoreConvertProcess> _getDLStoreConvertProcesses() {
		return _dlStoreConvertProcesses.toList();
	}

	private ServiceTrackerList<DLStoreConvertProcess> _dlStoreConvertProcesses;
	private ServiceTrackerMap<String, Store> _serviceTrackerMap;

	@Reference(target = "(default=true)")
	private Store _store;

}