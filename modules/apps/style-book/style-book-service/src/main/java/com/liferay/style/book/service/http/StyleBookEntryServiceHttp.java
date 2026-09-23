/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.style.book.service.StyleBookEntryServiceUtil;

/**
 * Provides the HTTP utility for the
 * <code>StyleBookEntryServiceUtil</code> service
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
public class StyleBookEntryServiceHttp {

	public static com.liferay.style.book.model.StyleBookEntry addFrontendToken(
			HttpPrincipal httpPrincipal, long styleBookEntryId,
			String cssVariableMappingValue, String defaultValue,
			String editorType, String frontendTokenCategoryLabel,
			String frontendTokenCategoryName, String frontendTokenDescription,
			String frontendTokenLabel, String frontendTokenName,
			String frontendTokenSetDescription, String frontendTokenSetLabel,
			String frontendTokenSetName, String frontendTokenType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "addFrontendToken",
				_addFrontendTokenParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, cssVariableMappingValue,
				defaultValue, editorType, frontendTokenCategoryLabel,
				frontendTokenCategoryName, frontendTokenDescription,
				frontendTokenLabel, frontendTokenName,
				frontendTokenSetDescription, frontendTokenSetLabel,
				frontendTokenSetName, frontendTokenType, serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry addStyleBookEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, boolean defaultStyleBookEntry,
			String frontendTokenDefinition, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "addStyleBookEntry",
				_addStyleBookEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				defaultStyleBookEntry, frontendTokenDefinition,
				frontendTokensValues, name, styleBookEntryKey, themeId,
				serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry addStyleBookEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, String name, String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "addStyleBookEntry",
				_addStyleBookEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, name,
				styleBookEntryKey, themeId, serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry addStyleBookEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, String frontendTokenDefinition,
			String frontendTokensValues, String name, String styleBookEntryKey,
			String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "addStyleBookEntry",
				_addStyleBookEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				frontendTokenDefinition, frontendTokensValues, name,
				styleBookEntryKey, themeId, serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			copyStyleBookEntry(
				HttpPrincipal httpPrincipal, long groupId,
				long sourceStyleBookEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "copyStyleBookEntry",
				_copyStyleBookEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, sourceStyleBookEntryId, serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			deleteStyleBookEntry(
				HttpPrincipal httpPrincipal, long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "deleteStyleBookEntry",
				_deleteStyleBookEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			deleteStyleBookEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "deleteStyleBookEntry",
				_deleteStyleBookEntryParameterTypes6);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			deleteStyleBookEntry(
				HttpPrincipal httpPrincipal,
				com.liferay.style.book.model.StyleBookEntry styleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "deleteStyleBookEntry",
				_deleteStyleBookEntryParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntry);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			discardDraftStyleBookEntry(
				HttpPrincipal httpPrincipal, long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "discardDraftStyleBookEntry",
				_discardDraftStyleBookEntryParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			fetchStyleBookEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class,
				"fetchStyleBookEntryByExternalReferenceCode",
				_fetchStyleBookEntryByExternalReferenceCodeParameterTypes9);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.style.book.model.StyleBookEntry>
			getStyleBookEntries(
				HttpPrincipal httpPrincipal, long groupId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.style.book.model.StyleBookEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "getStyleBookEntries",
				_getStyleBookEntriesParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.style.book.model.StyleBookEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.style.book.model.StyleBookEntry>
			getStyleBookEntries(
				HttpPrincipal httpPrincipal, long groupId, String name,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.style.book.model.StyleBookEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "getStyleBookEntries",
				_getStyleBookEntriesParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.style.book.model.StyleBookEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getStyleBookEntriesCount(
			HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "getStyleBookEntriesCount",
				_getStyleBookEntriesCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
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

	public static int getStyleBookEntriesCount(
			HttpPrincipal httpPrincipal, long groupId, String name)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "getStyleBookEntriesCount",
				_getStyleBookEntriesCountParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
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

	public static com.liferay.style.book.model.StyleBookEntry getStyleBookEntry(
			HttpPrincipal httpPrincipal, long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "getStyleBookEntry",
				_getStyleBookEntryParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			getStyleBookEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class,
				"getStyleBookEntryByExternalReferenceCode",
				_getStyleBookEntryByExternalReferenceCodeParameterTypes15);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry publishDraft(
			HttpPrincipal httpPrincipal, long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "publishDraft",
				_publishDraftParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			updateDefaultStyleBookEntry(
				HttpPrincipal httpPrincipal, long styleBookEntryId,
				boolean defaultStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "updateDefaultStyleBookEntry",
				_updateDefaultStyleBookEntryParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, defaultStyleBookEntry);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			updateFrontendTokenDefinition(
				HttpPrincipal httpPrincipal, long styleBookEntryId,
				String frontendTokenDefinition,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class,
				"updateFrontendTokenDefinition",
				_updateFrontendTokenDefinitionParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, frontendTokenDefinition,
				serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			updateFrontendTokensValues(
				HttpPrincipal httpPrincipal, long styleBookEntryId,
				String frontendTokensValues)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "updateFrontendTokensValues",
				_updateFrontendTokensValuesParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, frontendTokensValues);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry updateName(
			HttpPrincipal httpPrincipal, long styleBookEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "updateName",
				_updateNameParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, name);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			updatePreviewFileEntryId(
				HttpPrincipal httpPrincipal, long styleBookEntryId,
				long previewFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "updatePreviewFileEntryId",
				_updatePreviewFileEntryIdParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, previewFileEntryId,
				serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			updateStyleBookEntry(
				HttpPrincipal httpPrincipal, long styleBookEntryId,
				boolean defaultStyleBookEntry, String frontendTokenDefinition,
				String frontendTokensValues, String name,
				String styleBookEntryKey, long previewFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "updateStyleBookEntry",
				_updateStyleBookEntryParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, defaultStyleBookEntry,
				frontendTokenDefinition, frontendTokensValues, name,
				styleBookEntryKey, previewFileEntryId, serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.style.book.model.StyleBookEntry
			updateStyleBookEntry(
				HttpPrincipal httpPrincipal, long styleBookEntryId,
				String frontendTokenDefinition, String frontendTokensValues,
				String name,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				StyleBookEntryServiceUtil.class, "updateStyleBookEntry",
				_updateStyleBookEntryParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, styleBookEntryId, frontendTokenDefinition,
				frontendTokensValues, name, serviceContext);

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

			return (com.liferay.style.book.model.StyleBookEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		StyleBookEntryServiceHttp.class);

	private static final Class<?>[] _addFrontendTokenParameterTypes0 =
		new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			String.class, String.class, String.class, String.class,
			String.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addStyleBookEntryParameterTypes1 =
		new Class[] {
			String.class, long.class, boolean.class, String.class, String.class,
			String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addStyleBookEntryParameterTypes2 =
		new Class[] {
			String.class, long.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addStyleBookEntryParameterTypes3 =
		new Class[] {
			String.class, long.class, String.class, String.class, String.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyStyleBookEntryParameterTypes4 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteStyleBookEntryParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteStyleBookEntryParameterTypes6 =
		new Class[] {String.class, long.class};
	private static final Class<?>[] _deleteStyleBookEntryParameterTypes7 =
		new Class[] {com.liferay.style.book.model.StyleBookEntry.class};
	private static final Class<?>[] _discardDraftStyleBookEntryParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchStyleBookEntryByExternalReferenceCodeParameterTypes9 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getStyleBookEntriesParameterTypes10 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getStyleBookEntriesParameterTypes11 =
		new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getStyleBookEntriesCountParameterTypes12 =
		new Class[] {long.class};
	private static final Class<?>[] _getStyleBookEntriesCountParameterTypes13 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getStyleBookEntryParameterTypes14 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getStyleBookEntryByExternalReferenceCodeParameterTypes15 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _publishDraftParameterTypes16 =
		new Class[] {long.class};
	private static final Class<?>[]
		_updateDefaultStyleBookEntryParameterTypes17 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[]
		_updateFrontendTokenDefinitionParameterTypes18 = new Class[] {
			long.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateFrontendTokensValuesParameterTypes19 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updateNameParameterTypes20 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[] _updatePreviewFileEntryIdParameterTypes21 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateStyleBookEntryParameterTypes22 =
		new Class[] {
			long.class, boolean.class, String.class, String.class, String.class,
			String.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateStyleBookEntryParameterTypes23 =
		new Class[] {
			long.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-900802160