/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.internal.service;

import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetStagingHandler;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

import java.io.Closeable;
import java.io.IOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
@Component(service = {})
public class LayoutSetLocalServiceStagingAdvice {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_closeable = StagingAdviceUtil.register(
			bundleContext, LayoutSetLocalServiceStagingInvocationHandler::new,
			_layoutSetLocalService, LayoutSetLocalService.class);
	}

	@Deactivate
	protected void deactivate() throws IOException {
		_closeable.close();
	}

	protected LayoutSet wrapLayoutSet(LayoutSet layoutSet) {
		try {
			if (!LayoutStagingUtil.isBranchingLayoutSet(
					layoutSet.getGroup(), layoutSet.isPrivateLayout())) {

				return layoutSet;
			}
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return layoutSet;
		}

		return LayoutSetStagingHandler.newProxyInstance(
			new LayoutSetStagingHandler(layoutSet));
	}

	protected List<LayoutSet> wrapLayoutSets(List<LayoutSet> layoutSets) {
		return TransformUtil.transform(
			layoutSets, layoutSet -> wrapLayoutSet(layoutSet));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetLocalServiceStagingAdvice.class);

	private Closeable _closeable;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	private class LayoutSetLocalServiceStagingInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("getWrappedService")) {
				return _targetObject;
			}

			if (methodName.equals("setWrappedService")) {
				_targetObject = arguments[0];

				return null;
			}

			try {
				Object returnValue = method.invoke(_targetObject, arguments);

				if (!StagingAdvicesThreadLocal.isEnabled()) {
					return returnValue;
				}

				if (returnValue instanceof LayoutSet) {
					return wrapLayoutSet((LayoutSet)returnValue);
				}

				if (returnValue instanceof List<?>) {
					List<?> list = (List<?>)returnValue;

					if (!list.isEmpty() && (list.get(0) instanceof LayoutSet)) {
						returnValue = wrapLayoutSets(
							(List<LayoutSet>)returnValue);
					}
				}

				return returnValue;
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getCause();
			}
		}

		private LayoutSetLocalServiceStagingInvocationHandler(
			Object targetObject) {

			_targetObject = targetObject;
		}

		private volatile Object _targetObject;

	}

}