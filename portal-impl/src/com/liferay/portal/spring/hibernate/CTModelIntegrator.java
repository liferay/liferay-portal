/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.change.tracking.registry.CTModelRegistration;
import com.liferay.portal.change.tracking.registry.CTModelRegistry;
import com.liferay.portal.internal.change.tracking.hibernate.CTSQLInterceptor;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * @author Tina Tian
 */
public class CTModelIntegrator implements Integrator {

	@Override
	public void disintegrate(
		SessionFactoryImplementor sessionFactoryImplementor,
		SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {

		for (String tableName : _tableNames) {
			CTModelRegistry.unregisterCTModel(tableName);
		}
	}

	@Override
	public void integrate(
		Metadata metadata, BootstrapContext bootstrapContext,
		SessionFactoryImplementor sessionFactoryImplementor) {

		Collection<PersistentClass> persistentClasses =
			metadata.getEntityBindings();

		boolean containCTModel = false;

		for (PersistentClass persistentClass : persistentClasses) {
			Class<?> mappedClass = persistentClass.getMappedClass();

			if (!CTModel.class.isAssignableFrom(mappedClass)) {
				continue;
			}

			CTModelRegistration ctModelRegistration =
				_createCTModelRegistration(
					persistentClass, mappedClass.getSuperclass());

			if (ctModelRegistration != null) {
				containCTModel = true;

				CTModelRegistry.registerCTModel(ctModelRegistration);

				_tableNames.add(ctModelRegistration.getTableName());
			}
			else {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to find change tracking model class for " +
							mappedClass);
				}
			}
		}

		SessionFactoryOptions sessionFactoryOptions =
			sessionFactoryImplementor.getSessionFactoryOptions();

		CTSQLInterceptor ctSQLInterceptor =
			(CTSQLInterceptor)sessionFactoryOptions.getStatementInspector();

		ctSQLInterceptor.setEnabled(containCTModel);

		ServiceRegistryImplementor serviceRegistryImplementor =
			sessionFactoryImplementor.getServiceRegistry();

		EventListenerRegistry eventListenerRegistry =
			serviceRegistryImplementor.getService(EventListenerRegistry.class);

		eventListenerRegistry.setListeners(
			EventType.PRE_DELETE,
			preDeleteEvent -> {
				Object entity = preDeleteEvent.getEntity();

				if (entity instanceof CTModel) {
					CTModel<?> ctModel = (CTModel<?>)entity;

					long ctCollectionId =
						CTCollectionThreadLocal.getCTCollectionId();

					if ((ctCollectionId ==
							CTCollectionThreadLocal.
								CT_COLLECTION_ID_PRODUCTION) &&
						(ctCollectionId != ctModel.getCtCollectionId())) {

						return true;
					}
				}

				return false;
			});
		eventListenerRegistry.setListeners(
			EventType.PRE_UPDATE,
			preUpdateEvent -> {
				Object entity = preUpdateEvent.getEntity();

				if (entity instanceof CTModel) {
					CTModel<?> ctModel = (CTModel<?>)entity;

					if (ctModel.getCtCollectionId() !=
							CTCollectionThreadLocal.getCTCollectionId()) {

						return true;
					}
				}

				return false;
			});
	}

	private CTModelRegistration _createCTModelRegistration(
		PersistentClass persistentClass, Class<?> modelClass) {

		while (BaseModelImpl.class != modelClass) {
			for (Class<?> interfaceClazz : modelClass.getInterfaces()) {
				if (BaseModel.class.isAssignableFrom(interfaceClazz)) {
					Table table = persistentClass.getTable();

					PrimaryKey primaryKey = table.getPrimaryKey();

					Column column = primaryKey.getColumn(0);

					return new CTModelRegistration(
						interfaceClazz, table.getName(), column.getName());
				}
			}

			modelClass = modelClass.getSuperclass();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTModelIntegrator.class);

	private final Set<String> _tableNames = Collections.newSetFromMap(
		new ConcurrentHashMap<>());

}