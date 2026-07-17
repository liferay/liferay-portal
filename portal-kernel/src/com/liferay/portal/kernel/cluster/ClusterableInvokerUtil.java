/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cluster;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiServiceInvokerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Shuyang Zhou
 */
public class ClusterableInvokerUtil {

	public static MethodHandler createMethodHandler(
		Class<? extends ClusterInvokeAcceptor> clusterInvokeAcceptorClass,
		Object targetObject, Method method, Object[] arguments) {

		MethodHandler methodHandler =
			IdentifiableOSGiServiceInvokerUtil.createMethodHandler(
				targetObject, method, arguments);

		Map<String, Serializable> context =
			ClusterableContextThreadLocal.collectThreadLocalContext();

		_populateContextFromThreadLocals(context);

		String clusterInvokeAcceptorClassName =
			clusterInvokeAcceptorClass.getName();

		if (clusterInvokeAcceptorClass == ClusterInvokeAcceptor.class) {
			clusterInvokeAcceptorClassName = null;
		}

		return new MethodHandler(
			_invokeMethodKey, methodHandler, clusterInvokeAcceptorClassName,
			context);
	}

	public static void invokeOnCluster(
			Class<? extends ClusterInvokeAcceptor> clusterInvokeAcceptorClass,
			Object targetObject, Method method, Object[] arguments)
		throws Throwable {

		MethodHandler methodHandler = createMethodHandler(
			clusterInvokeAcceptorClass, targetObject, method, arguments);

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			methodHandler, true);

		clusterRequest.setFireAndForget(true);

		ClusterExecutorUtil.execute(clusterRequest);
	}

	public static Object invokeOnMaster(
			Class<? extends ClusterInvokeAcceptor> clusterInvokeAcceptorClass,
			Object targetObject, Method method, Object[] arguments)
		throws Throwable {

		MethodHandler methodHandler = createMethodHandler(
			clusterInvokeAcceptorClass, targetObject, method, arguments);

		Future<Object> futureResult = ClusterMasterExecutorUtil.executeOnMaster(
			methodHandler);

		if (_CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT <= 0) {
			return futureResult.get();
		}

		return futureResult.get(
			_CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT, TimeUnit.SECONDS);
	}

	@SuppressWarnings("unused")
	private static Object _invoke(
			MethodHandler methodHandler, String clusterInvokeAcceptorClassName,
			Map<String, Serializable> context)
		throws Exception {

		if (Validator.isNotNull(clusterInvokeAcceptorClassName)) {
			ClusterInvokeAcceptor clusterInvokeAcceptor =
				ClusterInvokeAcceptorUtil.getClusterInvokeAcceptor(
					clusterInvokeAcceptorClassName);

			if (!clusterInvokeAcceptor.accept(context)) {
				return null;
			}
		}

		_populateThreadLocalsFromContext(context);

		return methodHandler.invoke();
	}

	private static void _populateContextFromThreadLocals(
		Map<String, Serializable> context) {

		context.putIfAbsent("companyId", CompanyThreadLocal.getCompanyId());
		context.putIfAbsent(
			"defaultLocale", LocaleThreadLocal.getDefaultLocale());
		context.putIfAbsent("groupId", GroupThreadLocal.getGroupId());
		context.putIfAbsent("principalName", PrincipalThreadLocal.getName());
		context.putIfAbsent(
			"principalPassword", PrincipalThreadLocal.getPassword());
		context.putIfAbsent(
			"siteDefaultLocale", LocaleThreadLocal.getSiteDefaultLocale());
		context.putIfAbsent(
			"themeDisplayLocale", LocaleThreadLocal.getThemeDisplayLocale());
	}

	private static void _populateThreadLocalsFromContext(
		Map<String, Serializable> context) {

		long companyId = GetterUtil.getLong(context.get("companyId"));

		if (companyId > 0) {
			CompanyThreadLocal.setCompanyId(companyId);
		}

		Locale defaultLocale = (Locale)context.get("defaultLocale");

		if (defaultLocale != null) {
			LocaleThreadLocal.setDefaultLocale(defaultLocale);
		}

		long groupId = GetterUtil.getLong(context.get("groupId"));

		if (groupId > 0) {
			GroupThreadLocal.setGroupId(groupId);
		}

		String principalName = GetterUtil.getString(
			context.get("principalName"));

		if (Validator.isNotNull(principalName)) {
			PrincipalThreadLocal.setName(principalName);
		}

		PermissionChecker permissionChecker = null;

		if (Validator.isNotNull(principalName)) {
			try {
				User user = UserLocalServiceUtil.fetchUser(
					PrincipalThreadLocal.getUserId());

				permissionChecker = PermissionCheckerFactoryUtil.create(user);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		if (permissionChecker != null) {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		String principalPassword = GetterUtil.getString(
			context.get("principalPassword"));

		if (Validator.isNotNull(principalPassword)) {
			PrincipalThreadLocal.setPassword(principalPassword);
		}

		Locale siteDefaultLocale = (Locale)context.get("siteDefaultLocale");

		if (siteDefaultLocale != null) {
			LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
		}

		Locale themeDisplayLocale = (Locale)context.get("themeDisplayLocale");

		if (themeDisplayLocale != null) {
			LocaleThreadLocal.setThemeDisplayLocale(themeDisplayLocale);
		}
	}

	private static final long _CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT =
		GetterUtil.getLong(
			PropsUtil.get(PropsKeys.CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT));

	private static final MethodKey _invokeMethodKey = new MethodKey(
		ClusterableInvokerUtil.class, "_invoke", MethodHandler.class,
		String.class, Map.class);

}