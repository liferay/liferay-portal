/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectDefinitionServiceUtil</code> service
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
 * @author Marco Leo
 * @generated
 */
public class ObjectDefinitionServiceHttp {

	public static com.liferay.object.model.ObjectDefinition
			addCustomObjectDefinition(
				HttpPrincipal httpPrincipal, long objectFolderId,
				String className, boolean enableComments,
				boolean enableFormContainer,
				boolean enableFriendlyURLCustomization,
				boolean enableIndexSearch, boolean enableLocalization,
				boolean enableObjectEntryDraft,
				boolean enableObjectEntrySchedule,
				boolean enableObjectEntrySubscription,
				boolean enableObjectEntryVersioning,
				String friendlyURLSeparator,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				String panelAppOrder, String panelCategoryKey,
				java.util.Map<java.util.Locale, String> pluralLabelMap,
				boolean portlet, String scope, String storageType,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings,
				java.util.List<com.liferay.object.model.ObjectField>
					objectFields,
				java.util.List
					<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
						workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "addCustomObjectDefinition",
				_addCustomObjectDefinitionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectFolderId, className, enableComments,
				enableFormContainer, enableFriendlyURLCustomization,
				enableIndexSearch, enableLocalization, enableObjectEntryDraft,
				enableObjectEntrySchedule, enableObjectEntrySubscription,
				enableObjectEntryVersioning, friendlyURLSeparator, labelMap,
				name, panelAppOrder, panelCategoryKey, pluralLabelMap, portlet,
				scope, storageType, objectDefinitionSettings, objectFields,
				workflowDefinitionLinks);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition addObjectDefinition(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long objectFolderId, boolean modifiable, String scope,
			boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "addObjectDefinition",
				_addObjectDefinitionParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectFolderId, modifiable,
				scope, system);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			addSystemObjectDefinition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long userId, long objectFolderId, String className,
				boolean enableComments, boolean enableFormContainer,
				boolean enableFriendlyURLCustomization,
				boolean enableIndexSearch, boolean enableLocalization,
				boolean enableObjectEntryDraft,
				boolean enableObjectEntrySchedule,
				boolean enableObjectEntrySubscription,
				boolean enableObjectEntryVersioning,
				String friendlyURLSeparator,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				String panelAppOrder, String panelCategoryKey,
				java.util.Map<java.util.Locale, String> pluralLabelMap,
				boolean portlet, String scope,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings,
				java.util.List<com.liferay.object.model.ObjectField>
					objectFields,
				java.util.List
					<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
						workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "addSystemObjectDefinition",
				_addSystemObjectDefinitionParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, userId, objectFolderId,
				className, enableComments, enableFormContainer,
				enableFriendlyURLCustomization, enableIndexSearch,
				enableLocalization, enableObjectEntryDraft,
				enableObjectEntrySchedule, enableObjectEntrySubscription,
				enableObjectEntryVersioning, friendlyURLSeparator, labelMap,
				name, panelAppOrder, panelCategoryKey, pluralLabelMap, portlet,
				scope, objectDefinitionSettings, objectFields,
				workflowDefinitionLinks);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			deleteObjectDefinition(
				HttpPrincipal httpPrincipal, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "deleteObjectDefinition",
				_deleteObjectDefinitionParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			fetchObjectDefinitionByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"fetchObjectDefinitionByExternalReferenceCode",
				_fetchObjectDefinitionByExternalReferenceCodeParameterTypes4);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectDefinition>
		getCMSObjectDefinitions(
			HttpPrincipal httpPrincipal, long companyId,
			String[] objectFolderExternalReferenceCodes) {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "getCMSObjectDefinitions",
				_getCMSObjectDefinitionsParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, objectFolderExternalReferenceCodes);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.object.model.ObjectDefinition>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition getObjectDefinition(
			HttpPrincipal httpPrincipal, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "getObjectDefinition",
				_getObjectDefinitionParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			getObjectDefinitionByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"getObjectDefinitionByExternalReferenceCode",
				_getObjectDefinitionByExternalReferenceCodeParameterTypes7);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(HttpPrincipal httpPrincipal, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "getObjectDefinitions",
				_getObjectDefinitionsParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.object.model.ObjectDefinition>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(
			HttpPrincipal httpPrincipal, long companyId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "getObjectDefinitions",
				_getObjectDefinitionsParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.object.model.ObjectDefinition>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getObjectDefinitionsCount(HttpPrincipal httpPrincipal)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "getObjectDefinitionsCount",
				_getObjectDefinitionsCountParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(methodKey);

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

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getObjectDefinitionsCount(
			HttpPrincipal httpPrincipal, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "getObjectDefinitionsCount",
				_getObjectDefinitionsCountParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId);

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

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			publishCustomObjectDefinition(
				HttpPrincipal httpPrincipal, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"publishCustomObjectDefinition",
				_publishCustomObjectDefinitionParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			publishSystemObjectDefinition(
				HttpPrincipal httpPrincipal, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"publishSystemObjectDefinition",
				_publishSystemObjectDefinitionParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			updateCustomObjectDefinition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long objectDefinitionId,
				long accountEntryRestrictedObjectFieldId,
				long descriptionObjectFieldId, long objectFolderId,
				long titleObjectFieldId, boolean accountEntryRestricted,
				boolean active, String className, boolean enableCategorization,
				boolean enableComments, boolean enableFormContainer,
				boolean enableFriendlyURLCustomization,
				boolean enableIndexSearch, boolean enableLocalization,
				boolean enableObjectEntryDraft,
				boolean enableObjectEntryHistory,
				boolean enableObjectEntrySchedule,
				boolean enableObjectEntrySubscription,
				boolean enableObjectEntryVersioning,
				String friendlyURLSeparator,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				String panelAppOrder, String panelCategoryKey, boolean portlet,
				java.util.Map<java.util.Locale, String> pluralLabelMap,
				String scope, int status,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings,
				java.util.List
					<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
						workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"updateCustomObjectDefinition",
				_updateCustomObjectDefinitionParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectDefinitionId,
				accountEntryRestrictedObjectFieldId, descriptionObjectFieldId,
				objectFolderId, titleObjectFieldId, accountEntryRestricted,
				active, className, enableCategorization, enableComments,
				enableFormContainer, enableFriendlyURLCustomization,
				enableIndexSearch, enableLocalization, enableObjectEntryDraft,
				enableObjectEntryHistory, enableObjectEntrySchedule,
				enableObjectEntrySubscription, enableObjectEntryVersioning,
				friendlyURLSeparator, labelMap, name, panelAppOrder,
				panelCategoryKey, portlet, pluralLabelMap, scope, status,
				objectDefinitionSettings, workflowDefinitionLinks);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, long objectDefinitionId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId, externalReferenceCode);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			updateSystemObjectDefinition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long objectDefinitionId, long objectFolderId,
				long titleObjectFieldId,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings,
				java.util.List
					<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
						workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class,
				"updateSystemObjectDefinition",
				_updateSystemObjectDefinitionParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectDefinitionId,
				objectFolderId, titleObjectFieldId, objectDefinitionSettings,
				workflowDefinitionLinks);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectDefinition
			updateTitleObjectFieldId(
				HttpPrincipal httpPrincipal, long objectDefinitionId,
				long titleObjectFieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectDefinitionServiceUtil.class, "updateTitleObjectFieldId",
				_updateTitleObjectFieldIdParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId, titleObjectFieldId);

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

			return (com.liferay.object.model.ObjectDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionServiceHttp.class);

	private static final Class<?>[] _addCustomObjectDefinitionParameterTypes0 =
		new Class[] {
			long.class, String.class, boolean.class, boolean.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			boolean.class, boolean.class, boolean.class, String.class,
			java.util.Map.class, String.class, String.class, String.class,
			java.util.Map.class, boolean.class, String.class, String.class,
			java.util.List.class, java.util.List.class, java.util.List.class
		};
	private static final Class<?>[] _addObjectDefinitionParameterTypes1 =
		new Class[] {
			String.class, long.class, boolean.class, String.class, boolean.class
		};
	private static final Class<?>[] _addSystemObjectDefinitionParameterTypes2 =
		new Class[] {
			String.class, long.class, long.class, String.class, boolean.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			String.class, java.util.Map.class, String.class, String.class,
			String.class, java.util.Map.class, boolean.class, String.class,
			java.util.List.class, java.util.List.class, java.util.List.class
		};
	private static final Class<?>[] _deleteObjectDefinitionParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchObjectDefinitionByExternalReferenceCodeParameterTypes4 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCMSObjectDefinitionsParameterTypes5 =
		new Class[] {long.class, String[].class};
	private static final Class<?>[] _getObjectDefinitionParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getObjectDefinitionByExternalReferenceCodeParameterTypes7 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getObjectDefinitionsParameterTypes8 =
		new Class[] {int.class, int.class};
	private static final Class<?>[] _getObjectDefinitionsParameterTypes9 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getObjectDefinitionsCountParameterTypes10 =
		new Class[] {};
	private static final Class<?>[] _getObjectDefinitionsCountParameterTypes11 =
		new Class[] {long.class};
	private static final Class<?>[]
		_publishCustomObjectDefinitionParameterTypes12 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_publishSystemObjectDefinitionParameterTypes13 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_updateCustomObjectDefinitionParameterTypes14 = new Class[] {
			String.class, long.class, long.class, long.class, long.class,
			long.class, boolean.class, boolean.class, String.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			boolean.class, boolean.class, boolean.class, String.class,
			java.util.Map.class, String.class, String.class, String.class,
			boolean.class, java.util.Map.class, String.class, int.class,
			java.util.List.class, java.util.List.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes15 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_updateSystemObjectDefinitionParameterTypes16 = new Class[] {
			String.class, long.class, long.class, long.class,
			java.util.List.class, java.util.List.class
		};
	private static final Class<?>[] _updateTitleObjectFieldIdParameterTypes17 =
		new Class[] {long.class, long.class};

}