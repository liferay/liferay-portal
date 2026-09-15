/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutSetStagingHandler
	implements InvocationHandler, Serializable {

	public static LayoutSet newProxyInstance(
		InvocationHandler invocationHandler) {

		return _proxyProviderFunction.apply(invocationHandler);
	}

	public LayoutSetStagingHandler(LayoutSet layoutSet) {
		_layoutSet = layoutSet;

		try {
			_layoutSetBranch = _getLayoutSetBranch(layoutSet);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new IllegalStateException(exception);
		}
	}

	public LayoutSet getLayoutSet() {
		return _layoutSet;
	}

	public LayoutSetBranch getLayoutSetBranch() {
		return _layoutSetBranch;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
		throws Throwable {

		try {
			String methodName = method.getName();

			if (methodName.equals("getWrappedModel")) {
				return _layoutSet;
			}

			if (_layoutSetBranch == null) {
				return method.invoke(_layoutSet, arguments);
			}

			if (methodName.equals("clone")) {
				return _clone();
			}

			if (methodName.equals("toEscapedModel")) {
				if (_layoutSet.isEscapedModel()) {
					return this;
				}

				return _toEscapedModel();
			}

			Object bean = _layoutSet;

			if (_layoutSetBranchMethodNames.contains(methodName)) {
				try {
					Class<?> layoutSetBranchClass = _layoutSetBranch.getClass();

					method = layoutSetBranchClass.getMethod(
						methodName, method.getParameterTypes());

					bean = _layoutSetBranch;
				}
				catch (NoSuchMethodException noSuchMethodException) {
					_log.error(noSuchMethodException);
				}
			}

			return method.invoke(bean, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
	}

	public void setLayoutSetBranch(LayoutSetBranch layoutSetBranch) {
		_layoutSetBranch = layoutSetBranch;
	}

	private Object _clone() {
		return newProxyInstance(
			new LayoutSetStagingHandler((LayoutSet)_layoutSet.clone()));
	}

	private LayoutSetBranch _getLayoutSetBranch(LayoutSet layoutSet)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		long layoutSetBranchId = ParamUtil.getLong(
			serviceContext, "layoutSetBranchId");

		if (layoutSetBranchId > 0) {
			return LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(
				layoutSetBranchId);
		}

		if (serviceContext.isSignedIn()) {
			return LayoutSetBranchLocalServiceUtil.getUserLayoutSetBranch(
				serviceContext.getUserId(), layoutSet.getGroupId(),
				layoutSet.isPrivateLayout(), layoutSet.getLayoutSetId(),
				layoutSetBranchId);
		}

		return LayoutSetBranchLocalServiceUtil.getMasterLayoutSetBranch(
			layoutSet.getGroupId(), layoutSet.isPrivateLayout());
	}

	private Object _toEscapedModel() {
		return newProxyInstance(
			new LayoutSetStagingHandler(_layoutSet.toEscapedModel()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetStagingHandler.class);

	private static final Set<String> _layoutSetBranchMethodNames =
		new HashSet<>(
			Arrays.asList(
				"getColorScheme", "getColorSchemeId", "getCss",
				"getLayoutSetPrototypeLinkEnabled", "getLayoutSetPrototypeUuid",
				"getLogo", "getLogoId", "getSettings", "getSettingsProperties",
				"getSettingsProperty", "getTheme", "getThemeId",
				"getThemeSetting", "isEscapedModel",
				"isLayoutSetPrototypeLinkActive", "isLogo", "setColorSchemeId",
				"setCss", "setLayoutSetPrototypeLinkEnabled",
				"setLayoutSetPrototypeUuid", "setLogoId", "setSettings",
				"setSettingsProperties", "setThemeId"));
	private static final Function<InvocationHandler, LayoutSet>
		_proxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			LayoutSet.class, ModelWrapper.class);

	private final LayoutSet _layoutSet;
	private LayoutSetBranch _layoutSetBranch;

}