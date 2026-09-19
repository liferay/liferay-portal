/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.http;

import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AssetTagServiceUtil</code> service
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
public class AssetTagServiceHttp {

	public static com.liferay.asset.kernel.model.AssetTag addTag(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "addTag", _addTagParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, name,
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

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteTag(HttpPrincipal httpPrincipal, long tagId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "deleteTag",
				_deleteTagParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(methodKey, tagId);

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

	public static void deleteTags(HttpPrincipal httpPrincipal, long[] tagIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "deleteTags",
				_deleteTagsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(methodKey, tagIds);

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

	public static com.liferay.asset.kernel.model.AssetTag
		fetchAssetTagByExternalReferenceCode(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class,
				"fetchAssetTagByExternalReferenceCode",
				_fetchAssetTagByExternalReferenceCodeParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.kernel.model.AssetTag fetchTag(
			HttpPrincipal httpPrincipal, long groupId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "fetchTag",
				_fetchTagParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name);

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

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.kernel.model.AssetTag
			getAssetTagByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getAssetTagByExternalReferenceCode",
				_getAssetTagByExternalReferenceCodeParameterTypes5);

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

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getGroupTags(HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getGroupTags",
				_getGroupTagsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getGroupTags(
			HttpPrincipal httpPrincipal, long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.kernel.model.AssetTag> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getGroupTags",
				_getGroupTagsParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupTagsCount(
		HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getGroupTagsCount",
				_getGroupTagsCountParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

	public static com.liferay.asset.kernel.model.AssetTagDisplay
		getGroupTagsDisplay(
			HttpPrincipal httpPrincipal, long groupId, String name, int start,
			int end) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getGroupTagsDisplay",
				_getGroupTagsDisplayParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.asset.kernel.model.AssetTagDisplay)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getGroupsTags(HttpPrincipal httpPrincipal, long[] groupIds) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getGroupsTags",
				_getGroupsTagsParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.kernel.model.AssetTag getTag(
			HttpPrincipal httpPrincipal, long tagId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTag", _getTagParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(methodKey, tagId);

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

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.kernel.model.AssetTag getTag(
			HttpPrincipal httpPrincipal, long groupId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTag", _getTagParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name);

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

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			String name) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, name);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.kernel.model.AssetTag> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, name, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(
			HttpPrincipal httpPrincipal, long groupId, String name, int start,
			int end) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(
			HttpPrincipal httpPrincipal, long groupId, String name, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.kernel.model.AssetTag> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(
			HttpPrincipal httpPrincipal, long[] groupIds, String name,
			int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(
			HttpPrincipal httpPrincipal, long[] groupIds, String name,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.kernel.model.AssetTag> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetTag>
		getTags(HttpPrincipal httpPrincipal, String className, long classPK) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTags", _getTagsParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.kernel.model.AssetTag>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getTagsCount(
		HttpPrincipal httpPrincipal, long groupId, String name) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTagsCount",
				_getTagsCountParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name);

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

	public static int getTagsCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String name) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getTagsCount",
				_getTagsCountParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name);

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

	public static int getVisibleAssetsTagsCount(
		HttpPrincipal httpPrincipal, long groupId, long classNameId,
		String name) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "getVisibleAssetsTagsCount",
				_getVisibleAssetsTagsCountParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, name);

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

	public static void mergeTags(
			HttpPrincipal httpPrincipal, long fromTagId, long toTagId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "mergeTags",
				_mergeTagsParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fromTagId, toTagId);

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

	public static void mergeTags(
			HttpPrincipal httpPrincipal, long[] fromTagIds, long toTagId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "mergeTags",
				_mergeTagsParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fromTagIds, toTagId);

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

	public static com.liferay.portal.kernel.json.JSONArray search(
		HttpPrincipal httpPrincipal, long groupId, String name, int start,
		int end) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "search", _searchParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.json.JSONArray)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.json.JSONArray search(
		HttpPrincipal httpPrincipal, long[] groupIds, String name, int start,
		int end) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "search", _searchParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.json.JSONArray)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void subscribeTag(
			HttpPrincipal httpPrincipal, long userId, long groupId, long tagId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "subscribeTag",
				_subscribeTagParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, groupId, tagId);

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

	public static void unsubscribeTag(
			HttpPrincipal httpPrincipal, long userId, long tagId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "unsubscribeTag",
				_unsubscribeTagParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, tagId);

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

	public static com.liferay.asset.kernel.model.AssetTag updateTag(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long tagId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetTagServiceUtil.class, "updateTag",
				_updateTagParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, tagId, name, serviceContext);

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

			return (com.liferay.asset.kernel.model.AssetTag)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(AssetTagServiceHttp.class);

	private static final Class<?>[] _addTagParameterTypes0 = new Class[] {
		String.class, long.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteTagParameterTypes1 = new Class[] {
		long.class
	};
	private static final Class<?>[] _deleteTagsParameterTypes2 = new Class[] {
		long[].class
	};
	private static final Class<?>[]
		_fetchAssetTagByExternalReferenceCodeParameterTypes3 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _fetchTagParameterTypes4 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[]
		_getAssetTagByExternalReferenceCodeParameterTypes5 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getGroupTagsParameterTypes6 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getGroupTagsParameterTypes7 = new Class[] {
		long.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getGroupTagsCountParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[] _getGroupTagsDisplayParameterTypes9 =
		new Class[] {long.class, String.class, int.class, int.class};
	private static final Class<?>[] _getGroupsTagsParameterTypes10 =
		new Class[] {long[].class};
	private static final Class<?>[] _getTagParameterTypes11 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getTagParameterTypes12 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[] _getTagsParameterTypes13 = new Class[] {
		long.class, long.class, String.class
	};
	private static final Class<?>[] _getTagsParameterTypes14 = new Class[] {
		long.class, long.class, String.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getTagsParameterTypes15 = new Class[] {
		long.class, String.class, int.class, int.class
	};
	private static final Class<?>[] _getTagsParameterTypes16 = new Class[] {
		long.class, String.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getTagsParameterTypes17 = new Class[] {
		long[].class, String.class, int.class, int.class
	};
	private static final Class<?>[] _getTagsParameterTypes18 = new Class[] {
		long[].class, String.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getTagsParameterTypes19 = new Class[] {
		String.class, long.class
	};
	private static final Class<?>[] _getTagsCountParameterTypes20 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getTagsCountParameterTypes21 =
		new Class[] {long[].class, String.class};
	private static final Class<?>[] _getVisibleAssetsTagsCountParameterTypes22 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _mergeTagsParameterTypes23 = new Class[] {
		long.class, long.class
	};
	private static final Class<?>[] _mergeTagsParameterTypes24 = new Class[] {
		long[].class, long.class
	};
	private static final Class<?>[] _searchParameterTypes25 = new Class[] {
		long.class, String.class, int.class, int.class
	};
	private static final Class<?>[] _searchParameterTypes26 = new Class[] {
		long[].class, String.class, int.class, int.class
	};
	private static final Class<?>[] _subscribeTagParameterTypes27 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _unsubscribeTagParameterTypes28 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _updateTagParameterTypes29 = new Class[] {
		String.class, long.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};

}
// LIFERAY-SERVICE-BUILDER-HASH:-517731067