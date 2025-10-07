/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.OrganizationServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>OrganizationServiceUtil</code> service
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
public class OrganizationServiceHttp {

	public static void addGroupOrganizations(
			HttpPrincipal httpPrincipal, long groupId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "addGroupOrganizations",
				_addGroupOrganizationsParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, organizationIds);

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

	public static com.liferay.portal.kernel.model.Organization addOrganization(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "addOrganization",
				_addOrganizationParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, parentOrganizationId, name,
				type, regionId, countryId, statusListTypeId, comments, site,
				addresses, emailAddresses, orgLabors, phones, websites,
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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization addOrganization(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "addOrganization",
				_addOrganizationParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, parentOrganizationId, name,
				type, regionId, countryId, statusListTypeId, comments, site,
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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.User
			addOrganizationUserByEmailAddress(
				HttpPrincipal httpPrincipal, String emailAddress,
				long organizationId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class,
				"addOrganizationUserByEmailAddress",
				_addOrganizationUserByEmailAddressParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, emailAddress, organizationId, serviceContext);

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

			return (com.liferay.portal.kernel.model.User)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization
			addOrUpdateOrganization(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long parentOrganizationId, String name, String type,
				long regionId, long countryId, long statusListTypeId,
				String comments, boolean hasLogo, byte[] logoBytes,
				boolean site,
				java.util.List<com.liferay.portal.kernel.model.Address>
					addresses,
				java.util.List<com.liferay.portal.kernel.model.EmailAddress>
					emailAddresses,
				java.util.List<com.liferay.portal.kernel.model.OrgLabor>
					orgLabors,
				java.util.List<com.liferay.portal.kernel.model.Phone> phones,
				java.util.List<com.liferay.portal.kernel.model.Website>
					websites,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "addOrUpdateOrganization",
				_addOrUpdateOrganizationParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, parentOrganizationId, name,
				type, regionId, countryId, statusListTypeId, comments, hasLogo,
				logoBytes, site, addresses, emailAddresses, orgLabors, phones,
				websites, serviceContext);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void addPasswordPolicyOrganizations(
			HttpPrincipal httpPrincipal, long passwordPolicyId,
			long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "addPasswordPolicyOrganizations",
				_addPasswordPolicyOrganizationsParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, passwordPolicyId, organizationIds);

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

	public static void addUserOrganizationByEmailAddress(
			HttpPrincipal httpPrincipal, String emailAddress,
			long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class,
				"addUserOrganizationByEmailAddress",
				_addUserOrganizationByEmailAddressParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, emailAddress, organizationId);

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

	public static void deleteLogo(
			HttpPrincipal httpPrincipal, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "deleteLogo",
				_deleteLogoParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId);

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

	public static void deleteOrganization(
			HttpPrincipal httpPrincipal, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "deleteOrganization",
				_deleteOrganizationParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId);

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

	public static void deleteUserOrganizationByEmailAddress(
			HttpPrincipal httpPrincipal, String emailAddress,
			long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class,
				"deleteUserOrganizationByEmailAddress",
				_deleteUserOrganizationByEmailAddressParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, emailAddress, organizationId);

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

	public static com.liferay.portal.kernel.model.Organization
			fetchOrganization(HttpPrincipal httpPrincipal, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "fetchOrganization",
				_fetchOrganizationParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization
			fetchOrganizationByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class,
				"fetchOrganizationByExternalReferenceCode",
				_fetchOrganizationByExternalReferenceCodeParameterTypes11);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		getGtOrganizations(
			HttpPrincipal httpPrincipal, long gtOrganizationId, long companyId,
			long parentOrganizationId, int size) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getGtOrganizations",
				_getGtOrganizationsParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, gtOrganizationId, companyId, parentOrganizationId,
				size);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization
			getOrAddEmptyOrganization(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrAddEmptyOrganization",
				_getOrAddEmptyOrganizationParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, name);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization getOrganization(
			HttpPrincipal httpPrincipal, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganization",
				_getOrganizationParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization
			getOrganizationByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class,
				"getOrganizationByExternalReferenceCode",
				_getOrganizationByExternalReferenceCodeParameterTypes15);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getOrganizationId(
			HttpPrincipal httpPrincipal, long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizationId",
				_getOrganizationIdParameterTypes16);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			HttpPrincipal httpPrincipal, long companyId,
			long parentOrganizationId) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizations",
				_getOrganizationsParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			HttpPrincipal httpPrincipal, long companyId,
			long parentOrganizationId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizations",
				_getOrganizationsParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			HttpPrincipal httpPrincipal, long companyId,
			long parentOrganizationId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizations",
				_getOrganizationsParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			HttpPrincipal httpPrincipal, long companyId,
			long parentOrganizationId, String name, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizations",
				_getOrganizationsParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			HttpPrincipal httpPrincipal, long companyId,
			long parentOrganizationId, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizations",
				_getOrganizationsParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId, name, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getOrganizationsCount(
		HttpPrincipal httpPrincipal, long companyId,
		long parentOrganizationId) {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizationsCount",
				_getOrganizationsCountParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId);

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

	public static int getOrganizationsCount(
			HttpPrincipal httpPrincipal, long companyId,
			long parentOrganizationId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getOrganizationsCount",
				_getOrganizationsCountParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, parentOrganizationId, name);

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

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
			getUserOrganizations(HttpPrincipal httpPrincipal, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "getUserOrganizations",
				_getUserOrganizationsParameterTypes24);

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

			return (java.util.List
				<com.liferay.portal.kernel.model.Organization>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void setGroupOrganizations(
			HttpPrincipal httpPrincipal, long groupId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "setGroupOrganizations",
				_setGroupOrganizationsParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, organizationIds);

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

	public static void unsetGroupOrganizations(
			HttpPrincipal httpPrincipal, long groupId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "unsetGroupOrganizations",
				_unsetGroupOrganizationsParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, organizationIds);

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

	public static void unsetPasswordPolicyOrganizations(
			HttpPrincipal httpPrincipal, long passwordPolicyId,
			long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class,
				"unsetPasswordPolicyOrganizations",
				_unsetPasswordPolicyOrganizationsParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, passwordPolicyId, organizationIds);

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

	public static com.liferay.portal.kernel.model.Organization updateLogo(
			HttpPrincipal httpPrincipal, long organizationId, byte[] logoBytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "updateLogo",
				_updateLogoParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId, logoBytes);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization
			updateOrganization(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long organizationId, long parentOrganizationId, String name,
				String type, long regionId, long countryId,
				long statusListTypeId, String comments, boolean hasLogo,
				byte[] logoBytes, boolean site,
				java.util.List<com.liferay.portal.kernel.model.Address>
					addresses,
				java.util.List<com.liferay.portal.kernel.model.EmailAddress>
					emailAddresses,
				java.util.List<com.liferay.portal.kernel.model.OrgLabor>
					orgLabors,
				java.util.List<com.liferay.portal.kernel.model.Phone> phones,
				java.util.List<com.liferay.portal.kernel.model.Website>
					websites,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "updateOrganization",
				_updateOrganizationParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, organizationId,
				parentOrganizationId, name, type, regionId, countryId,
				statusListTypeId, comments, hasLogo, logoBytes, site, addresses,
				emailAddresses, orgLabors, phones, websites, serviceContext);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Organization
			updateOrganization(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long organizationId, long parentOrganizationId, String name,
				String type, long regionId, long countryId,
				long statusListTypeId, String comments, boolean site,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OrganizationServiceUtil.class, "updateOrganization",
				_updateOrganizationParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, organizationId,
				parentOrganizationId, name, type, regionId, countryId,
				statusListTypeId, comments, site, serviceContext);

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

			return (com.liferay.portal.kernel.model.Organization)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		OrganizationServiceHttp.class);

	private static final Class<?>[] _addGroupOrganizationsParameterTypes0 =
		new Class[] {long.class, long[].class};
	private static final Class<?>[] _addOrganizationParameterTypes1 =
		new Class[] {
			String.class, long.class, String.class, String.class, long.class,
			long.class, long.class, String.class, boolean.class,
			java.util.List.class, java.util.List.class, java.util.List.class,
			java.util.List.class, java.util.List.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addOrganizationParameterTypes2 =
		new Class[] {
			String.class, long.class, String.class, String.class, long.class,
			long.class, long.class, String.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrganizationUserByEmailAddressParameterTypes3 = new Class[] {
			String.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addOrUpdateOrganizationParameterTypes4 =
		new Class[] {
			String.class, long.class, String.class, String.class, long.class,
			long.class, long.class, String.class, boolean.class, byte[].class,
			boolean.class, java.util.List.class, java.util.List.class,
			java.util.List.class, java.util.List.class, java.util.List.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addPasswordPolicyOrganizationsParameterTypes5 = new Class[] {
			long.class, long[].class
		};
	private static final Class<?>[]
		_addUserOrganizationByEmailAddressParameterTypes6 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _deleteLogoParameterTypes7 = new Class[] {
		long.class
	};
	private static final Class<?>[] _deleteOrganizationParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[]
		_deleteUserOrganizationByEmailAddressParameterTypes9 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _fetchOrganizationParameterTypes10 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchOrganizationByExternalReferenceCodeParameterTypes11 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getGtOrganizationsParameterTypes12 =
		new Class[] {long.class, long.class, long.class, int.class};
	private static final Class<?>[] _getOrAddEmptyOrganizationParameterTypes13 =
		new Class[] {String.class, String.class};
	private static final Class<?>[] _getOrganizationParameterTypes14 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getOrganizationByExternalReferenceCodeParameterTypes15 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getOrganizationIdParameterTypes16 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getOrganizationsParameterTypes17 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getOrganizationsParameterTypes18 =
		new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getOrganizationsParameterTypes19 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getOrganizationsParameterTypes20 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class
		};
	private static final Class<?>[] _getOrganizationsParameterTypes21 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getOrganizationsCountParameterTypes22 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getOrganizationsCountParameterTypes23 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _getUserOrganizationsParameterTypes24 =
		new Class[] {long.class};
	private static final Class<?>[] _setGroupOrganizationsParameterTypes25 =
		new Class[] {long.class, long[].class};
	private static final Class<?>[] _unsetGroupOrganizationsParameterTypes26 =
		new Class[] {long.class, long[].class};
	private static final Class<?>[]
		_unsetPasswordPolicyOrganizationsParameterTypes27 = new Class[] {
			long.class, long[].class
		};
	private static final Class<?>[] _updateLogoParameterTypes28 = new Class[] {
		long.class, byte[].class
	};
	private static final Class<?>[] _updateOrganizationParameterTypes29 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			long.class, long.class, long.class, String.class, boolean.class,
			byte[].class, boolean.class, java.util.List.class,
			java.util.List.class, java.util.List.class, java.util.List.class,
			java.util.List.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateOrganizationParameterTypes30 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			long.class, long.class, long.class, String.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}