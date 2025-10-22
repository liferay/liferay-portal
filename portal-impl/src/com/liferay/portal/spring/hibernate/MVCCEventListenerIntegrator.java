/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.dao.orm.hibernate.event.MVCCSynchronizerPostUpdateEventListener;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * @author Tina Tian
 */
public class MVCCEventListenerIntegrator implements Integrator {

	public static final MVCCEventListenerIntegrator INSTANCE =
		new MVCCEventListenerIntegrator();

	@Override
	public void disintegrate(
		SessionFactoryImplementor sessionFactoryImplementor,
		SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
	}

	@Override
	public void integrate(
		Metadata metadata, BootstrapContext bootstrapContext,
		SessionFactoryImplementor sessionFactoryImplementor) {

		ServiceRegistryImplementor serviceRegistryImplementor =
			sessionFactoryImplementor.getServiceRegistry();

		EventListenerRegistry eventListenerRegistry =
			serviceRegistryImplementor.getService(EventListenerRegistry.class);

		eventListenerRegistry.setListeners(
			EventType.POST_UPDATE,
			MVCCSynchronizerPostUpdateEventListener.INSTANCE);
	}

}