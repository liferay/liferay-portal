/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.http;

import com.liferay.account.service.AccountEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AccountEntryServiceUtil</code> service
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
public class AccountEntryServiceHttp {

	public static void activateAccountEntries(
			HttpPrincipal httpPrincipal, long[] accountEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "activateAccountEntries",
				_activateAccountEntriesParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryIds);

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

	public static com.liferay.account.model.AccountEntry activateAccountEntry(
			HttpPrincipal httpPrincipal, long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "activateAccountEntry",
				_activateAccountEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry addAccountEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long userId, long parentAccountEntryId, String name,
			String description, String[] domains, String email,
			byte[] logoBytes, String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "addAccountEntry",
				_addAccountEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, userId, parentAccountEntryId,
				name, description, domains, email, logoBytes, taxIdNumber, type,
				status, serviceContext);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			addOrUpdateAccountEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long userId, long parentAccountEntryId, String name,
				String description, String[] domains, String emailAddress,
				byte[] logoBytes, String taxIdNumber, String type, int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "addOrUpdateAccountEntry",
				_addOrUpdateAccountEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, userId, parentAccountEntryId,
				name, description, domains, emailAddress, logoBytes,
				taxIdNumber, type, status, serviceContext);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deactivateAccountEntries(
			HttpPrincipal httpPrincipal, long[] accountEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "deactivateAccountEntries",
				_deactivateAccountEntriesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryIds);

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

	public static com.liferay.account.model.AccountEntry deactivateAccountEntry(
			HttpPrincipal httpPrincipal, long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "deactivateAccountEntry",
				_deactivateAccountEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteAccountEntries(
			HttpPrincipal httpPrincipal, long[] accountEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "deleteAccountEntries",
				_deleteAccountEntriesParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryIds);

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

	public static void deleteAccountEntry(
			HttpPrincipal httpPrincipal, long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "deleteAccountEntry",
				_deleteAccountEntryParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId);

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

	public static com.liferay.account.model.AccountEntry fetchAccountEntry(
			HttpPrincipal httpPrincipal, long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "fetchAccountEntry",
				_fetchAccountEntryParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			fetchAccountEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class,
				"fetchAccountEntryByExternalReferenceCode",
				_fetchAccountEntryByExternalReferenceCodeParameterTypes9);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.account.model.AccountEntry>
			getAccountEntries(
				HttpPrincipal httpPrincipal, long companyId, int status,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.account.model.AccountEntry> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "getAccountEntries",
				_getAccountEntriesParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, status, start, end, orderByComparator);

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

			return (java.util.List<com.liferay.account.model.AccountEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry getAccountEntry(
			HttpPrincipal httpPrincipal, long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "getAccountEntry",
				_getAccountEntryParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			getAccountEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class,
				"getAccountEntryByExternalReferenceCode",
				_getAccountEntryByExternalReferenceCodeParameterTypes12);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			getOrAddEmptyAccountEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				String name, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "getOrAddEmptyAccountEntry",
				_getOrAddEmptyAccountEntryParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, name, type);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountEntry> searchAccountEntries(
				HttpPrincipal httpPrincipal, String keywords,
				java.util.LinkedHashMap<String, Object> params, int cur,
				int delta, String orderByField, boolean reverse)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "searchAccountEntries",
				_searchAccountEntriesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, keywords, params, cur, delta, orderByField, reverse);

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

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.account.model.AccountEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry updateAccountEntry(
			HttpPrincipal httpPrincipal,
			com.liferay.account.model.AccountEntry accountEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateAccountEntry",
				_updateAccountEntryParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntry);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry updateAccountEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long accountEntryId, long parentAccountEntryId, String name,
			String description, boolean deleteLogo, String[] domains,
			String emailAddress, byte[] logoBytes, String taxIdNumber,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateAccountEntry",
				_updateAccountEntryParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, accountEntryId,
				parentAccountEntryId, name, description, deleteLogo, domains,
				emailAddress, logoBytes, taxIdNumber, status, serviceContext);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			updateDefaultBillingAddressId(
				HttpPrincipal httpPrincipal, long accountEntryId,
				long addressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateDefaultBillingAddressId",
				_updateDefaultBillingAddressIdParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, addressId);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			updateDefaultShippingAddressId(
				HttpPrincipal httpPrincipal, long accountEntryId,
				long addressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateDefaultShippingAddressId",
				_updateDefaultShippingAddressIdParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, addressId);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry updateDomains(
			HttpPrincipal httpPrincipal, long accountEntryId, String[] domains)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateDomains",
				_updateDomainsParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, domains);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, long accountEntryId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, externalReferenceCode);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntry
			updateRestrictMembership(
				HttpPrincipal httpPrincipal, long accountEntryId,
				boolean restrictMembership)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryServiceUtil.class, "updateRestrictMembership",
				_updateRestrictMembershipParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, restrictMembership);

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

			return (com.liferay.account.model.AccountEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		AccountEntryServiceHttp.class);

	private static final Class<?>[] _activateAccountEntriesParameterTypes0 =
		new Class[] {long[].class};
	private static final Class<?>[] _activateAccountEntryParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _addAccountEntryParameterTypes2 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			String[].class, String.class, byte[].class, String.class,
			String.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addOrUpdateAccountEntryParameterTypes3 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			String[].class, String.class, byte[].class, String.class,
			String.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deactivateAccountEntriesParameterTypes4 =
		new Class[] {long[].class};
	private static final Class<?>[] _deactivateAccountEntryParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteAccountEntriesParameterTypes6 =
		new Class[] {long[].class};
	private static final Class<?>[] _deleteAccountEntryParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchAccountEntryParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchAccountEntryByExternalReferenceCodeParameterTypes9 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getAccountEntriesParameterTypes10 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAccountEntryParameterTypes11 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getAccountEntryByExternalReferenceCodeParameterTypes12 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getOrAddEmptyAccountEntryParameterTypes13 =
		new Class[] {String.class, String.class, String.class};
	private static final Class<?>[] _searchAccountEntriesParameterTypes14 =
		new Class[] {
			String.class, java.util.LinkedHashMap.class, int.class, int.class,
			String.class, boolean.class
		};
	private static final Class<?>[] _updateAccountEntryParameterTypes15 =
		new Class[] {com.liferay.account.model.AccountEntry.class};
	private static final Class<?>[] _updateAccountEntryParameterTypes16 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			boolean.class, String[].class, String.class, byte[].class,
			String.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateDefaultBillingAddressIdParameterTypes17 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_updateDefaultShippingAddressIdParameterTypes18 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[] _updateDomainsParameterTypes19 =
		new Class[] {long.class, String[].class};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes20 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updateRestrictMembershipParameterTypes21 =
		new Class[] {long.class, boolean.class};

}