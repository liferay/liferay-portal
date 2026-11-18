/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal;

import com.liferay.change.tracking.internal.helper.CTTableMapperHelper;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.spi.exception.CTEventException;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = CTServiceRegistry.class)
public class CTServiceRegistry {

	public CTService<?> getCTService(long classNameId) {
		ClassName className = _classNameLocalService.fetchByClassNameId(
			classNameId);

		if (className == null) {
			return null;
		}

		return _serviceTrackerMap.getService(className.getValue());
	}

	public Collection<CTTableMapperHelper> getCTTableMapperHelpers() {
		Map<String, CTTableMapperHelper> ctMappingTableHelpers =
			new HashMap<>();

		for (CTService<?> ctService : _serviceTrackerMap.values()) {
			CTPersistence<?> ctPersistence = ctService.getCTPersistence();

			List<String> mappingTableNames =
				ctPersistence.getMappingTableNames();

			if (mappingTableNames.isEmpty()) {
				continue;
			}

			Set<String> primaryKeyNames = ctPersistence.getCTColumnNames(
				CTColumnResolutionType.PK);

			if (primaryKeyNames.size() != 1) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"{primaryKeyNames=", primaryKeyNames, ", tableName=",
						ctPersistence.getTableName(), "}"));
			}

			Iterator<String> iterator = primaryKeyNames.iterator();

			String primaryKeyName = iterator.next();

			for (String mappingTableName : mappingTableNames) {
				ctMappingTableHelpers.compute(
					mappingTableName,
					(key, ctTableMapperHelper) -> {
						if (ctTableMapperHelper == null) {
							return new CTTableMapperHelper(
								ctService, mappingTableName, primaryKeyName,
								ctService.getModelClass());
						}

						ctTableMapperHelper.setRightColumnName(primaryKeyName);
						ctTableMapperHelper.setRightModelClass(
							ctService.getModelClass());

						return ctTableMapperHelper;
					});
			}
		}

		return ctMappingTableHelpers.values();
	}

	public boolean hasCTService(String className) {
		return _serviceTrackerMap.containsKey(className);
	}

	public void onAfterCopy(
		CTCollection sourceCTCollection, CTCollection targetCTCollection) {

		for (CTEventListener ctEventListener : _serviceTrackerList) {
			try {
				ctEventListener.onAfterCopy(
					sourceCTCollection.getCtCollectionId(),
					targetCTCollection.getCtCollectionId());
			}
			catch (CTEventException ctEventException) {
				_log.error(
					StringBundler.concat(
						"On after copy callback failure for change tracking ",
						"collection ", sourceCTCollection, " by ",
						ctEventListener),
					ctEventException);
			}
		}
	}

	public void onAfterPublish(long ctCollectionId) {
		for (CTEventListener ctEventListener : _serviceTrackerList) {
			try {
				ctEventListener.onAfterPublish(ctCollectionId);
			}
			catch (CTEventException ctEventException) {
				_log.error(
					StringBundler.concat(
						"On after publish callback failure for change ",
						"tracking collection ", ctCollectionId, " by ",
						ctEventListener),
					ctEventException);
			}
		}
	}

	public void onBeforePublish(long ctCollectionId) {
		for (CTEventListener ctEventListener : _serviceTrackerList) {
			try {
				ctEventListener.onBeforePublish(ctCollectionId);
			}
			catch (CTEventException ctEventException) {
				_log.error(
					StringBundler.concat(
						"On before publish callback failure for change ",
						"tracking collection ", ctCollectionId, " by ",
						ctEventListener),
					ctEventException);
			}
		}
	}

	public void onBeforeRemove(long ctCollectionId) {
		for (CTEventListener ctEventListener : _serviceTrackerList) {
			try {
				ctEventListener.onBeforeRemove(ctCollectionId);
			}
			catch (CTEventException ctEventException) {
				_log.error(
					StringBundler.concat(
						"On before remove callback failure for change ",
						"tracking collection ", ctCollectionId, " by ",
						ctEventListener),
					ctEventException);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, CTEventListener.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, (Class<CTService<?>>)(Class<?>)CTService.class, null,
			(serviceReference, emitter) -> {
				CTService<?> ctService = bundleContext.getService(
					serviceReference);

				Class<?> modelClass = ctService.getModelClass();

				emitter.emit(modelClass.getName());

				bundleContext.ungetService(serviceReference);
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();

		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTServiceRegistry.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private ServiceTrackerList<CTEventListener> _serviceTrackerList;
	private ServiceTrackerMap<String, CTService<?>> _serviceTrackerMap;

}