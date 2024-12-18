/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>WorkflowDefinitionLinkServiceUtil</code> service
 * utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it requires an additional
 * <code>HttpPrincipal</code> parameter.
 *
 * <p>
 * The benefits of using the HTTP utility is that it is fast and allows for
 * tunneling without the cost of serializing to text. The drawback is that it
 * only works with Java.
 * </p>
 *
 * <p>
 * Set the property <b>tunnel.servlet.hosts.allowed</b> in portal.properties to
 * configure security.
 * </p>
 *
 * <p>
 * The HTTP utility is only generated for remote services.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WorkflowDefinitionLinkServiceHttp {

	public static com.liferay.portal.kernel.model.WorkflowDefinitionLink
			addWorkflowDefinitionLink(
				HttpPrincipal httpPrincipal, long userId, long companyId,
				long groupId, String className, long classPK, long typePK,
				String workflowDefinitionName, int workflowDefinitionVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				WorkflowDefinitionLinkServiceUtil.class,
				"addWorkflowDefinitionLink",
				_addWorkflowDefinitionLinkParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, companyId, groupId, className, classPK,
				typePK, workflowDefinitionName, workflowDefinitionVersion);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.WorkflowDefinitionLink)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.WorkflowDefinitionLink
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				WorkflowDefinitionLinkServiceUtil.class,
				"fetchWorkflowDefinitionLinkByExternalReferenceCode",
				_fetchWorkflowDefinitionLinkByExternalReferenceCodeParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.WorkflowDefinitionLink)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
				getWorkflowDefinitionLinks(
					HttpPrincipal httpPrincipal, long companyId,
					String workflowDefinitionName,
					int workflowDefinitionVersion)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				WorkflowDefinitionLinkServiceUtil.class,
				"getWorkflowDefinitionLinks",
				_getWorkflowDefinitionLinksParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, workflowDefinitionName,
				workflowDefinitionVersion);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.WorkflowDefinitionLink>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.WorkflowDefinitionLink
			updateWorkflowDefinitionLink(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long userId, long companyId, long groupId, String className,
				long classPK, long typePK, String workflowDefinitionName,
				int workflowDefinitionVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				WorkflowDefinitionLinkServiceUtil.class,
				"updateWorkflowDefinitionLink",
				_updateWorkflowDefinitionLinkParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, userId, companyId, groupId,
				className, classPK, typePK, workflowDefinitionName,
				workflowDefinitionVersion);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.WorkflowDefinitionLink)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		WorkflowDefinitionLinkServiceHttp.class);

	private static final Class<?>[] _addWorkflowDefinitionLinkParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class, String.class, long.class,
			long.class, String.class, int.class
		};
	private static final Class<?>[]
		_fetchWorkflowDefinitionLinkByExternalReferenceCodeParameterTypes1 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getWorkflowDefinitionLinksParameterTypes2 =
		new Class[] {long.class, String.class, int.class};
	private static final Class<?>[]
		_updateWorkflowDefinitionLinkParameterTypes3 = new Class[] {
			String.class, long.class, long.class, long.class, String.class,
			long.class, long.class, String.class, int.class
		};

}