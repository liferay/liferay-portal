/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>RoleServiceUtil</code> service
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
public class RoleServiceHttp {

	public static com.liferay.portal.kernel.model.Role addRole(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			String className, long classPK, String name,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			String subtype,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "addRole", _addRoleParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, className, classPK, name,
				titleMap, descriptionMap, type, subtype, serviceContext);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void addUserRoles(
			HttpPrincipal httpPrincipal, long userId, long[] roleIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "addUserRoles",
				_addUserRolesParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, roleIds);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
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
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role copyRole(
			HttpPrincipal httpPrincipal, long userId, String name,
			long sourceRoleId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "copyRole", _copyRoleParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, name, sourceRoleId, serviceContext);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteRole(HttpPrincipal httpPrincipal, long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "deleteRole",
				_deleteRoleParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(methodKey, roleId);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
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
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role fetchRole(
			HttpPrincipal httpPrincipal, long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "fetchRole", _fetchRoleParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(methodKey, roleId);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role fetchRole(
			HttpPrincipal httpPrincipal, long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "fetchRole", _fetchRoleParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role
			fetchRoleByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "fetchRoleByExternalReferenceCode",
				_fetchRoleByExternalReferenceCodeParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
			getGroupRoles(HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getGroupRoles",
				_getGroupRolesParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		getGroupRolesAndTeamRoles(
			HttpPrincipal httpPrincipal, long companyId, String name,
			java.util.List<String> excludedNames, String title,
			String description, int[] types, long excludedTeamRoleId,
			long teamGroupId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getGroupRolesAndTeamRoles",
				_getGroupRolesAndTeamRolesParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name, excludedNames, title, description,
				types, excludedTeamRoleId, teamGroupId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupRolesAndTeamRolesCount(
		HttpPrincipal httpPrincipal, long companyId, String name,
		java.util.List<String> excludedNames, String title, String description,
		int[] types, long excludedTeamRoleId, long teamGroupId) {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getGroupRolesAndTeamRolesCount",
				_getGroupRolesAndTeamRolesCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name, excludedNames, title, description,
				types, excludedTeamRoleId, teamGroupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role getOrAddEmptyRole(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			String className, long classPK, String name, int type)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getOrAddEmptyRole",
				_getOrAddEmptyRoleParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, className, classPK, name,
				type);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof Exception) {
					throw (Exception)exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role getRole(
			HttpPrincipal httpPrincipal, long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getRole", _getRoleParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(methodKey, roleId);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role getRole(
			HttpPrincipal httpPrincipal, long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getRole", _getRoleParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role
			getRoleByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getRoleByExternalReferenceCode",
				_getRoleByExternalReferenceCodeParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role> getRoles(
			HttpPrincipal httpPrincipal, int type, String subtype)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getRoles", _getRolesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, type, subtype);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role> getRoles(
			HttpPrincipal httpPrincipal, long companyId, int[] types)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getRoles", _getRolesParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, types);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
			getUserGroupGroupRoles(
				HttpPrincipal httpPrincipal, long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getUserGroupGroupRoles",
				_getUserGroupGroupRolesParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, groupId);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
			getUserGroupRoles(
				HttpPrincipal httpPrincipal, long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getUserGroupRoles",
				_getUserGroupRolesParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, groupId);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
			getUserRelatedRoles(
				HttpPrincipal httpPrincipal, long userId,
				java.util.List<com.liferay.portal.kernel.model.Group> groups)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getUserRelatedRoles",
				_getUserRelatedRolesParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, groups);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
			getUserRoles(HttpPrincipal httpPrincipal, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "getUserRoles",
				_getUserRolesParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(methodKey, userId);

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

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static boolean hasUserRole(
			HttpPrincipal httpPrincipal, long userId, long companyId,
			String name, boolean inherited)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "hasUserRole",
				_hasUserRoleParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, companyId, name, inherited);

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

			return ((Boolean)returnObj).booleanValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static boolean hasUserRoles(
			HttpPrincipal httpPrincipal, long userId, long companyId,
			String[] names, boolean inherited)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "hasUserRoles",
				_hasUserRolesParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, companyId, names, inherited);

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

			return ((Boolean)returnObj).booleanValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role> search(
		HttpPrincipal httpPrincipal, long companyId, String keywords,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "search", _searchParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, types, params, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.portal.kernel.model.Role>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int searchCount(
		HttpPrincipal httpPrincipal, long companyId, String keywords,
		Integer[] types, java.util.LinkedHashMap<String, Object> params) {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "searchCount",
				_searchCountParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, types, params);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void unsetUserRoles(
			HttpPrincipal httpPrincipal, long userId, long[] roleIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "unsetUserRoles",
				_unsetUserRolesParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, roleIds);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
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
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, long roleId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, roleId, externalReferenceCode);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal,
				com.liferay.portal.kernel.model.Role role,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, role, externalReferenceCode);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Role updateRole(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long roleId, String name,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String subtype,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				RoleServiceUtil.class, "updateRole",
				_updateRoleParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, roleId, name, titleMap,
				descriptionMap, subtype, serviceContext);

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

			return (com.liferay.portal.kernel.model.Role)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(RoleServiceHttp.class);

	private static final Class<?>[] _addRoleParameterTypes0 = new Class[] {
		String.class, String.class, long.class, String.class,
		java.util.Map.class, java.util.Map.class, int.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addUserRolesParameterTypes1 = new Class[] {
		long.class, long[].class
	};
	private static final Class<?>[] _copyRoleParameterTypes2 = new Class[] {
		long.class, String.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteRoleParameterTypes3 = new Class[] {
		long.class
	};
	private static final Class<?>[] _fetchRoleParameterTypes4 = new Class[] {
		long.class
	};
	private static final Class<?>[] _fetchRoleParameterTypes5 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[]
		_fetchRoleByExternalReferenceCodeParameterTypes6 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getGroupRolesParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[] _getGroupRolesAndTeamRolesParameterTypes8 =
		new Class[] {
			long.class, String.class, java.util.List.class, String.class,
			String.class, int[].class, long.class, long.class, int.class,
			int.class
		};
	private static final Class<?>[]
		_getGroupRolesAndTeamRolesCountParameterTypes9 = new Class[] {
			long.class, String.class, java.util.List.class, String.class,
			String.class, int[].class, long.class, long.class
		};
	private static final Class<?>[] _getOrAddEmptyRoleParameterTypes10 =
		new Class[] {
			String.class, String.class, long.class, String.class, int.class
		};
	private static final Class<?>[] _getRoleParameterTypes11 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getRoleParameterTypes12 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[]
		_getRoleByExternalReferenceCodeParameterTypes13 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getRolesParameterTypes14 = new Class[] {
		int.class, String.class
	};
	private static final Class<?>[] _getRolesParameterTypes15 = new Class[] {
		long.class, int[].class
	};
	private static final Class<?>[] _getUserGroupGroupRolesParameterTypes16 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getUserGroupRolesParameterTypes17 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getUserRelatedRolesParameterTypes18 =
		new Class[] {long.class, java.util.List.class};
	private static final Class<?>[] _getUserRolesParameterTypes19 =
		new Class[] {long.class};
	private static final Class<?>[] _hasUserRoleParameterTypes20 = new Class[] {
		long.class, long.class, String.class, boolean.class
	};
	private static final Class<?>[] _hasUserRolesParameterTypes21 =
		new Class[] {long.class, long.class, String[].class, boolean.class};
	private static final Class<?>[] _searchParameterTypes22 = new Class[] {
		long.class, String.class, Integer[].class,
		java.util.LinkedHashMap.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _searchCountParameterTypes23 = new Class[] {
		long.class, String.class, Integer[].class, java.util.LinkedHashMap.class
	};
	private static final Class<?>[] _unsetUserRolesParameterTypes24 =
		new Class[] {long.class, long[].class};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes25 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes26 = new Class[] {
			com.liferay.portal.kernel.model.Role.class, String.class
		};
	private static final Class<?>[] _updateRoleParameterTypes27 = new Class[] {
		String.class, long.class, String.class, java.util.Map.class,
		java.util.Map.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};

}