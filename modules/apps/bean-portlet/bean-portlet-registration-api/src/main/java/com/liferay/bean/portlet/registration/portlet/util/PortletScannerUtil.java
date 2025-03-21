/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.util;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodFactory;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventPortlet;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderPortlet;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceServingPortlet;
import jakarta.portlet.annotations.ActionMethod;
import jakarta.portlet.annotations.DestroyMethod;
import jakarta.portlet.annotations.EventMethod;
import jakarta.portlet.annotations.HeaderMethod;
import jakarta.portlet.annotations.InitMethod;
import jakarta.portlet.annotations.RenderMethod;
import jakarta.portlet.annotations.ServeResourceMethod;

import java.lang.reflect.Method;

import java.util.Set;

/**
 * @author Neil Griffin
 */
public class PortletScannerUtil {

	public static void scanNonannotatedBeanMethods(
		Class<?> beanPortletClass,
		BeanPortletMethodFactory beanPortletMethodFactory,
		Set<BeanPortletMethod> beanPortletMethods) {

		if (Portlet.class.isAssignableFrom(beanPortletClass)) {
			try {
				Method processActionMethod = beanPortletClass.getMethod(
					"processAction", ActionRequest.class, ActionResponse.class);

				if (!processActionMethod.isAnnotationPresent(
						ActionMethod.class)) {

					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass, BeanPortletMethodType.ACTION,
							processActionMethod));
				}

				Method destroyMethod = beanPortletClass.getMethod("destroy");

				if (!destroyMethod.isAnnotationPresent(DestroyMethod.class)) {
					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass, BeanPortletMethodType.DESTROY,
							destroyMethod));
				}

				Method initMethod = beanPortletClass.getMethod(
					"init", PortletConfig.class);

				if (!initMethod.isAnnotationPresent(InitMethod.class)) {
					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass, BeanPortletMethodType.INIT,
							initMethod));
				}

				Method renderMethod = beanPortletClass.getMethod(
					"render", RenderRequest.class, RenderResponse.class);

				if (!renderMethod.isAnnotationPresent(RenderMethod.class)) {
					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass, BeanPortletMethodType.RENDER,
							renderMethod));
				}
			}
			catch (NoSuchMethodException noSuchMethodException) {
				_log.error(noSuchMethodException);
			}
		}

		if (EventPortlet.class.isAssignableFrom(beanPortletClass)) {
			try {
				Method eventMethod = beanPortletClass.getMethod(
					"processEvent", EventRequest.class, EventResponse.class);

				if (!eventMethod.isAnnotationPresent(EventMethod.class)) {
					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass, BeanPortletMethodType.EVENT,
							eventMethod));
				}
			}
			catch (NoSuchMethodException noSuchMethodException) {
				_log.error(noSuchMethodException);
			}
		}

		if (HeaderPortlet.class.isAssignableFrom(beanPortletClass)) {
			try {
				Method renderHeadersMethod = beanPortletClass.getMethod(
					"renderHeaders", HeaderRequest.class, HeaderResponse.class);

				if (!renderHeadersMethod.isAnnotationPresent(
						HeaderMethod.class)) {

					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass, BeanPortletMethodType.HEADER,
							renderHeadersMethod));
				}
			}
			catch (NoSuchMethodException noSuchMethodException) {
				_log.error(noSuchMethodException);
			}
		}

		if (ResourceServingPortlet.class.isAssignableFrom(beanPortletClass)) {
			try {
				Method serveResourceMethod = beanPortletClass.getMethod(
					"serveResource", ResourceRequest.class,
					ResourceResponse.class);

				if (!serveResourceMethod.isAnnotationPresent(
						ServeResourceMethod.class)) {

					beanPortletMethods.add(
						beanPortletMethodFactory.create(
							beanPortletClass,
							BeanPortletMethodType.SERVE_RESOURCE,
							serveResourceMethod));
				}
			}
			catch (NoSuchMethodException noSuchMethodException) {
				_log.error(noSuchMethodException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletScannerUtil.class);

}