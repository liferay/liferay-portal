/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.osgi.util.tracker;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourcedModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Alejandro Tardín
 */
@Component(service = {})
public class LaunchEntryManagerServiceTrackerCustomizer
	implements ServiceTrackerCustomizer
		<LaunchEntryManager, ServiceRegistration<?>> {

	@Override
	public ServiceRegistration<?> addingService(
		ServiceReference<LaunchEntryManager> serviceReference) {

		return _bundleContext.registerService(
			ModelListener.class,
			new LaunchEntryModelListener<>(
				GetterUtil.getString(
					serviceReference.getProperty(
						"launch.entry.manager.class.name")),
				_bundleContext.getService(serviceReference)),
			null);
	}

	@Override
	public void modifiedService(
		ServiceReference<LaunchEntryManager> serviceReference,
		ServiceRegistration<?> serviceRegistration) {
	}

	@Override
	public void removedService(
		ServiceReference<LaunchEntryManager> serviceReference,
		ServiceRegistration<?> serviceRegistration) {

		serviceRegistration.unregister();

		_bundleContext.ungetService(serviceReference);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, LaunchEntryManager.class, this);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private void _deleteLaunchEntries(String className, long classPK)
		throws PortalException {

		long companyId = CompanyThreadLocal.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "LaunchEntry");

		if (objectDefinition == null) {
			return;
		}

		Group companyGroup = _groupLocalService.getCompanyGroup(companyId);

		long objectDefinitionId = objectDefinition.getObjectDefinitionId();

		Predicate predicate = _getEqualsPredicate(
			"className", objectDefinitionId, className);

		for (long objectEntryId :
				_objectEntryLocalService.getPrimaryKeys(
					new Long[] {companyGroup.getGroupId()}, companyId,
					objectDefinition.getUserId(), objectDefinitionId,
					predicate.and(
						_getEqualsPredicate(
							"classPK", objectDefinitionId, classPK)),
					false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			_objectEntryLocalService.deleteObjectEntry(objectEntryId);
		}
	}

	private Predicate _getEqualsPredicate(
			String fieldName, long objectDefinitionId, Object value)
		throws PortalException {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, fieldName);

		Table<?> table = _objectFieldLocalService.getTable(
			objectDefinitionId, objectField.getName());

		Column<?, Object> column = (Column<?, Object>)table.getColumn(
			objectField.getDBColumnName());

		return column.eq(value);
	}

	private BundleContext _bundleContext;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	private ServiceTracker<LaunchEntryManager, ServiceRegistration<?>>
		_serviceTracker;

	private class LaunchEntryModelListener<T extends BaseModel<T>>
		extends BaseModelListener<T> {

		@Override
		public Class<?> getModelClass() {
			return _launchEntryManager.getModelClass();
		}

		@Override
		public void onAfterRemove(T baseModel) throws ModelListenerException {
			if (baseModel == null) {
				return;
			}

			long classPK = GetterUtil.getLong(baseModel.getPrimaryKeyObj());

			if (baseModel instanceof ResourcedModel) {
				ResourcedModel resourcedModel = (ResourcedModel)baseModel;

				classPK = resourcedModel.getResourcePrimKey();
			}

			if (_launchEntryManager.fetchItem(classPK) != null) {
				return;
			}

			try {
				_deleteLaunchEntries(_className, classPK);
			}
			catch (PortalException portalException) {
				throw new ModelListenerException(portalException);
			}
		}

		private LaunchEntryModelListener(
			String className, LaunchEntryManager launchEntryManager) {

			_className = className;
			_launchEntryManager = launchEntryManager;
		}

		private final String _className;
		private final LaunchEntryManager _launchEntryManager;

	}

}