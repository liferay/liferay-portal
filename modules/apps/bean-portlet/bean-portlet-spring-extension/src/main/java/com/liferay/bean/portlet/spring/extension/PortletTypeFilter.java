/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;

import jakarta.portlet.EventPortlet;
import jakarta.portlet.HeaderPortlet;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletAsyncListener;
import jakarta.portlet.ResourceServingPortlet;
import jakarta.portlet.annotations.ActionMethod;
import jakarta.portlet.annotations.DestroyMethod;
import jakarta.portlet.annotations.EventMethod;
import jakarta.portlet.annotations.HeaderMethod;
import jakarta.portlet.annotations.InitMethod;
import jakarta.portlet.annotations.PortletLifecycleFilter;
import jakarta.portlet.annotations.PortletRequestScoped;
import jakarta.portlet.annotations.PortletSessionScoped;
import jakarta.portlet.annotations.RenderMethod;
import jakarta.portlet.annotations.RenderStateScoped;
import jakarta.portlet.annotations.ServeResourceMethod;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.IOException;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * @author Neil Griffin
 */
public class PortletTypeFilter implements TypeFilter {

	@Override
	public boolean match(
			MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory)
		throws IOException {

		AnnotationMetadata metaData = metadataReader.getAnnotationMetadata();

		if (metaData.hasAnnotation(ApplicationScoped.class.getName()) ||
			metaData.hasAnnotation(Dependent.class.getName()) ||
			metaData.hasAnnotation(PortletLifecycleFilter.class.getName()) ||
			metaData.hasAnnotation(PortletRequestScoped.class.getName()) ||
			metaData.hasAnnotation(PortletSessionScoped.class.getName()) ||
			metaData.hasAnnotation(RenderStateScoped.class.getName()) ||
			metaData.hasAnnotation(RequestScoped.class.getName()) ||
			metaData.hasAnnotation(SessionScoped.class.getName()) ||
			metaData.hasAnnotatedMethods(ActionMethod.class.getName()) ||
			metaData.hasAnnotatedMethods(DestroyMethod.class.getName()) ||
			metaData.hasAnnotatedMethods(EventMethod.class.getName()) ||
			metaData.hasAnnotatedMethods(HeaderMethod.class.getName()) ||
			metaData.hasAnnotatedMethods(InitMethod.class.getName()) ||
			metaData.hasAnnotatedMethods(RenderMethod.class.getName()) ||
			metaData.hasAnnotatedMethods(ServeResourceMethod.class.getName())) {

			return true;
		}

		return _matchInterfacesRecurse(metadataReader, metadataReaderFactory);
	}

	private boolean _matchInterfacesRecurse(
			MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory)
		throws IOException {

		ClassMetadata classMetadata = metadataReader.getClassMetadata();

		if (classMetadata.isAnnotation() || classMetadata.isInterface() ||
			!classMetadata.isIndependent()) {

			return false;
		}

		String[] interfaceNames = classMetadata.getInterfaceNames();

		for (String interfaceName : interfaceNames) {
			if (interfaceName.equals(ActionFilter.class.getName()) ||
				interfaceName.equals(EventFilter.class.getName()) ||
				interfaceName.equals(EventPortlet.class.getName()) ||
				interfaceName.equals(HeaderFilter.class.getName()) ||
				interfaceName.equals(HeaderPortlet.class.getName()) ||
				interfaceName.equals(Portlet.class.getName()) ||
				interfaceName.equals(PortletAsyncListener.class.getName()) ||
				interfaceName.equals(RenderFilter.class.getName()) ||
				interfaceName.equals(ResourceFilter.class.getName()) ||
				interfaceName.equals(ResourceServingPortlet.class.getName())) {

				return true;
			}
		}

		String superClassName = classMetadata.getSuperClassName();

		if (superClassName.equals(Object.class.getName())) {
			return false;
		}

		return _matchInterfacesRecurse(
			metadataReaderFactory.getMetadataReader(superClassName),
			metadataReaderFactory);
	}

}