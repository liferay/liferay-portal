/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.util;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;

import jakarta.portlet.annotations.ActionMethod;
import jakarta.portlet.annotations.EventMethod;
import jakarta.portlet.annotations.PortletQName;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * @author Shuyang Zhou
 */
public class BeanMethodIndexUtil {

	public static Map<BeanPortletMethodType, List<BeanPortletMethod>>
		indexBeanMethods(Set<BeanPortletMethod> beanPortletMethods) {

		EnumMap<BeanPortletMethodType, List<BeanPortletMethod>>
			beanMethodsEnumMap = new EnumMap<>(BeanPortletMethodType.class);

		for (BeanPortletMethod beanPortletMethod : beanPortletMethods) {
			beanMethodsEnumMap.compute(
				beanPortletMethod.getBeanPortletMethodType(),
				(methodType, valueBeanMethods) -> {
					if (valueBeanMethods == null) {
						valueBeanMethods = new ArrayList<>();
					}

					valueBeanMethods.add(beanPortletMethod);

					return valueBeanMethods;
				});
		}

		beanMethodsEnumMap.forEach(
			(methodType, valueBeanMethods) -> Collections.sort(
				valueBeanMethods));

		return beanMethodsEnumMap;
	}

	public static void scanSupportedEvents(
		Map<BeanPortletMethodType, List<BeanPortletMethod>> beanMethodsMap,
		Set<QName> supportedProcessingEvents,
		Set<QName> supportedPublishingEvents) {

		List<BeanPortletMethod> eventBeanPortletMethods = beanMethodsMap.get(
			BeanPortletMethodType.EVENT);

		if (eventBeanPortletMethods != null) {
			for (BeanPortletMethod beanPortletMethod :
					eventBeanPortletMethods) {

				Method beanEventMethod = beanPortletMethod.getMethod();

				EventMethod eventMethod = beanEventMethod.getAnnotation(
					EventMethod.class);

				if (eventMethod == null) {
					continue;
				}

				for (PortletQName portletQName :
						eventMethod.processingEvents()) {

					supportedProcessingEvents.add(
						new QName(
							portletQName.namespaceURI(),
							portletQName.localPart()));
				}

				for (PortletQName portletQName :
						eventMethod.publishingEvents()) {

					supportedPublishingEvents.add(
						new QName(
							portletQName.namespaceURI(),
							portletQName.localPart()));
				}
			}
		}

		List<BeanPortletMethod> actionBeanPortletMethods = beanMethodsMap.get(
			BeanPortletMethodType.ACTION);

		if (actionBeanPortletMethods != null) {
			for (BeanPortletMethod beanPortletMethod :
					actionBeanPortletMethods) {

				Method method = beanPortletMethod.getMethod();

				ActionMethod actionMethod = method.getAnnotation(
					ActionMethod.class);

				if (actionMethod == null) {
					continue;
				}

				for (PortletQName portletQName :
						actionMethod.publishingEvents()) {

					supportedPublishingEvents.add(
						new QName(
							portletQName.namespaceURI(),
							portletQName.localPart()));
				}
			}
		}
	}

}