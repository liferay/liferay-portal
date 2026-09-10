/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.http;

import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>KBArticleServiceUtil</code> service
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
public class KBArticleServiceHttp {

	public static com.liferay.knowledge.base.model.KBArticle addKBArticle(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			String portletId, long parentResourceClassNameId,
			long parentResourcePrimKey, String title, String urlTitle,
			String content, String description, String[] sections,
			String sourceURL, java.util.Date displayDate,
			java.util.Date expirationDate, java.util.Date reviewDate,
			String[] selectedFileNames,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "addKBArticle",
				_addKBArticleParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, portletId,
				parentResourceClassNameId, parentResourcePrimKey, title,
				urlTitle, content, description, sections, sourceURL,
				displayDate, expirationDate, reviewDate, selectedFileNames,
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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int addKBArticlesMarkdown(
			HttpPrincipal httpPrincipal, long groupId, long parentKBFolderId,
			String fileName, boolean prioritizeByNumericalPrefix,
			java.io.InputStream inputStream,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "addKBArticlesMarkdown",
				_addKBArticlesMarkdownParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, parentKBFolderId, fileName,
				prioritizeByNumericalPrefix, inputStream, serviceContext);

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

	public static void addTempAttachment(
			HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey,
			String fileName, String tempFolderName,
			java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "addTempAttachment",
				_addTempAttachmentParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey, fileName, tempFolderName,
				inputStream, mimeType);

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

	public static int countKBArticlesByKeywords(
		HttpPrincipal httpPrincipal, long groupId, String keywords,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "countKBArticlesByKeywords",
				_countKBArticlesByKeywordsParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, keywords, status);

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

	public static com.liferay.knowledge.base.model.KBArticle deleteKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "deleteKBArticle",
				_deleteKBArticleParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteKBArticleAttachment(
			HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "deleteKBArticleAttachment",
				_deleteKBArticleAttachmentParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId);

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

	public static void deleteKBArticles(
			HttpPrincipal httpPrincipal, long groupId, long[] resourcePrimKeys)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "deleteKBArticles",
				_deleteKBArticlesParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKeys);

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

	public static void deleteTempAttachment(
			HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey,
			String fileName, String tempFolderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "deleteTempAttachment",
				_deleteTempAttachmentParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey, fileName, tempFolderName);

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

	public static com.liferay.knowledge.base.model.KBArticle expireKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "expireKBArticle",
				_expireKBArticleParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, serviceContext);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
		fetchFirstChildKBArticle(
			HttpPrincipal httpPrincipal, long groupId,
			long parentResourcePrimKey) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "fetchFirstChildKBArticle",
				_fetchFirstChildKBArticleParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, parentResourcePrimKey);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
		fetchFirstChildKBArticle(
			HttpPrincipal httpPrincipal, long groupId,
			long parentResourcePrimKey, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "fetchFirstChildKBArticle",
				_fetchFirstChildKBArticleParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, parentResourcePrimKey, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
			fetchKBArticleByUrlTitle(
				HttpPrincipal httpPrincipal, long groupId, long kbFolderId,
				String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "fetchKBArticleByUrlTitle",
				_fetchKBArticleByUrlTitleParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, kbFolderId, urlTitle);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
			fetchLatestKBArticle(
				HttpPrincipal httpPrincipal, long resourcePrimKey, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "fetchLatestKBArticle",
				_fetchLatestKBArticleParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, status);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
			fetchLatestKBArticleByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long groupId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class,
				"fetchLatestKBArticleByExternalReferenceCode",
				_fetchLatestKBArticleByExternalReferenceCodeParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, externalReferenceCode);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
			fetchLatestKBArticleByUrlTitle(
				HttpPrincipal httpPrincipal, long groupId, long kbFolderId,
				String urlTitle, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "fetchLatestKBArticleByUrlTitle",
				_fetchLatestKBArticleByUrlTitleParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, kbFolderId, urlTitle, status);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.lock.Lock forceLockKBArticle(
			HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "forceLockKBArticle",
				_forceLockKBArticleParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey);

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

			return (com.liferay.portal.kernel.lock.Lock)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
			getAllDescendantKBArticles(
				HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey,
				int status,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.knowledge.base.model.KBArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getAllDescendantKBArticles",
				_getAllDescendantKBArticlesParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey, status, orderByComparator);

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

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getGroupKBArticles(
			HttpPrincipal httpPrincipal, long groupId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.knowledge.base.model.KBArticle>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getGroupKBArticles",
				_getGroupKBArticlesParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupKBArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getGroupKBArticlesCount",
				_getGroupKBArticlesCountParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status);

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

	public static String getGroupKBArticlesRSS(
			HttpPrincipal httpPrincipal, int status, int max, String type,
			double version, String displayStyle,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getGroupKBArticlesRSS",
				_getGroupKBArticlesRSSParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, status, max, type, version, displayStyle,
				themeDisplay);

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

			return (String)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle getKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticle",
				_getKBArticleParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, version);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
			getKBArticleAndAllDescendantKBArticles(
				HttpPrincipal httpPrincipal, long resourcePrimKey, int status,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.knowledge.base.model.KBArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class,
				"getKBArticleAndAllDescendantKBArticles",
				_getKBArticleAndAllDescendantKBArticlesParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, status, orderByComparator);

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

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			getKBArticleAttachment(
				HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticleAttachment",
				_getKBArticleAttachmentParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId);

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

			return (com.liferay.portal.kernel.repository.model.FileEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String getKBArticleRSS(
			HttpPrincipal httpPrincipal, long resourcePrimKey, int status,
			int max, String type, double version, String displayStyle,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticleRSS",
				_getKBArticleRSSParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, status, max, type, version,
				displayStyle, themeDisplay);

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

			return (String)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getKBArticles(
			HttpPrincipal httpPrincipal, long groupId,
			long parentResourcePrimKey, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.knowledge.base.model.KBArticle>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticles",
				_getKBArticlesParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, parentResourcePrimKey, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getKBArticles(
			HttpPrincipal httpPrincipal, long groupId, long[] resourcePrimKeys,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.knowledge.base.model.KBArticle>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticles",
				_getKBArticlesParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKeys, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getKBArticles(
			HttpPrincipal httpPrincipal, long groupId, long[] resourcePrimKeys,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.knowledge.base.model.KBArticle>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticles",
				_getKBArticlesParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKeys, status,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getKBArticlesByKeywords(
			HttpPrincipal httpPrincipal, long groupId, String keywords,
			int status, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticlesByKeywords",
				_getKBArticlesByKeywordsParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, keywords, status, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getKBArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, long parentResourcePrimKey,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticlesCount",
				_getKBArticlesCountParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, parentResourcePrimKey, status);

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

	public static int getKBArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, long[] resourcePrimKeys,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticlesCount",
				_getKBArticlesCountParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKeys, status);

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

	public static com.liferay.knowledge.base.model.KBArticleSearchDisplay
			getKBArticleSearchDisplay(
				HttpPrincipal httpPrincipal, long groupId, String title,
				String content, int status, java.util.Date startDate,
				java.util.Date endDate, boolean andOperator,
				int[] curStartValues, int cur, int delta,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.knowledge.base.model.KBArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticleSearchDisplay",
				_getKBArticleSearchDisplayParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, title, content, status, startDate, endDate,
				andOperator, curStartValues, cur, delta, orderByComparator);

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

			return (com.liferay.knowledge.base.model.KBArticleSearchDisplay)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getKBArticleVersions(
			HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.knowledge.base.model.KBArticle>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticleVersions",
				_getKBArticleVersionsParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getKBArticleVersionsCount(
		HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getKBArticleVersionsCount",
				_getKBArticleVersionsCountParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey, status);

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

	public static com.liferay.knowledge.base.model.KBArticle getLatestKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getLatestKBArticle",
				_getLatestKBArticleParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle getLatestKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getLatestKBArticle",
				_getLatestKBArticleParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, status);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle
			getLatestKBArticleByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long groupId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class,
				"getLatestKBArticleByExternalReferenceCode",
				_getLatestKBArticleByExternalReferenceCodeParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, externalReferenceCode);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle[]
			getPreviousAndNextKBArticles(
				HttpPrincipal httpPrincipal, long kbArticleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getPreviousAndNextKBArticles",
				_getPreviousAndNextKBArticlesParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, kbArticleId);

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

			return (com.liferay.knowledge.base.model.KBArticle[])returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.knowledge.base.model.KBArticle>
		getSectionsKBArticles(
			HttpPrincipal httpPrincipal, long groupId, String[] sections,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.knowledge.base.model.KBArticle>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getSectionsKBArticles",
				_getSectionsKBArticlesParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, sections, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.knowledge.base.model.KBArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getSectionsKBArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, String[] sections,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getSectionsKBArticlesCount",
				_getSectionsKBArticlesCountParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, sections, status);

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

	public static String[] getTempAttachmentNames(
			HttpPrincipal httpPrincipal, long groupId, String tempFolderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "getTempAttachmentNames",
				_getTempAttachmentNamesParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, tempFolderName);

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

			return (String[])returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.lock.Lock lockKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "lockKBArticle",
				_lockKBArticleParameterTypes40);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey);

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

			return (com.liferay.portal.kernel.lock.Lock)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void moveKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey,
			long parentResourceClassNameId, long parentResourcePrimKey,
			double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "moveKBArticle",
				_moveKBArticleParameterTypes41);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, parentResourceClassNameId,
				parentResourcePrimKey, priority);

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

	public static com.liferay.knowledge.base.model.KBArticle
			moveKBArticleToTrash(
				HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "moveKBArticleToTrash",
				_moveKBArticleToTrashParameterTypes42);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle revertKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "revertKBArticle",
				_revertKBArticleParameterTypes43);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, version, serviceContext);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void subscribeGroupKBArticles(
			HttpPrincipal httpPrincipal, long groupId, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "subscribeGroupKBArticles",
				_subscribeGroupKBArticlesParameterTypes44);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, portletId);

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

	public static void subscribeKBArticle(
			HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "subscribeKBArticle",
				_subscribeKBArticleParameterTypes45);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey);

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

	public static void unlockKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "unlockKBArticle",
				_unlockKBArticleParameterTypes46);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey);

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

	public static void unsubscribeGroupKBArticles(
			HttpPrincipal httpPrincipal, long groupId, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "unsubscribeGroupKBArticles",
				_unsubscribeGroupKBArticlesParameterTypes47);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, portletId);

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

	public static void unsubscribeKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "unsubscribeKBArticle",
				_unsubscribeKBArticleParameterTypes48);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey);

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

	public static com.liferay.knowledge.base.model.KBArticle
			updateAndUnlockKBArticle(
				HttpPrincipal httpPrincipal, long resourcePrimKey, String title,
				String content, String description, String[] sections,
				String sourceURL, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				String[] selectedFileNames, long[] removeFileEntryIds,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "updateAndUnlockKBArticle",
				_updateAndUnlockKBArticleParameterTypes49);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, title, content, description,
				sections, sourceURL, displayDate, expirationDate, reviewDate,
				selectedFileNames, removeFileEntryIds, serviceContext);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.knowledge.base.model.KBArticle updateKBArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey, String title,
			String content, String description, String[] sections,
			String sourceURL, java.util.Date displayDate,
			java.util.Date expirationDate, java.util.Date reviewDate,
			String[] selectedFileNames, long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "updateKBArticle",
				_updateKBArticleParameterTypes50);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, resourcePrimKey, title, content, description,
				sections, sourceURL, displayDate, expirationDate, reviewDate,
				selectedFileNames, removeFileEntryIds, serviceContext);

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

			return (com.liferay.knowledge.base.model.KBArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void updateKBArticlesPriorities(
			HttpPrincipal httpPrincipal, long groupId,
			java.util.Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KBArticleServiceUtil.class, "updateKBArticlesPriorities",
				_updateKBArticlesPrioritiesParameterTypes51);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKeyToPriorityMap);

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

	private static Log _log = LogFactoryUtil.getLog(KBArticleServiceHttp.class);

	private static final Class<?>[] _addKBArticleParameterTypes0 = new Class[] {
		String.class, String.class, long.class, long.class, String.class,
		String.class, String.class, String.class, String[].class, String.class,
		java.util.Date.class, java.util.Date.class, java.util.Date.class,
		String[].class, com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addKBArticlesMarkdownParameterTypes1 =
		new Class[] {
			long.class, long.class, String.class, boolean.class,
			java.io.InputStream.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addTempAttachmentParameterTypes2 =
		new Class[] {
			long.class, long.class, String.class, String.class,
			java.io.InputStream.class, String.class
		};
	private static final Class<?>[] _countKBArticlesByKeywordsParameterTypes3 =
		new Class[] {long.class, String.class, int.class};
	private static final Class<?>[] _deleteKBArticleParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteKBArticleAttachmentParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteKBArticlesParameterTypes6 =
		new Class[] {long.class, long[].class};
	private static final Class<?>[] _deleteTempAttachmentParameterTypes7 =
		new Class[] {long.class, long.class, String.class, String.class};
	private static final Class<?>[] _expireKBArticleParameterTypes8 =
		new Class[] {
			long.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _fetchFirstChildKBArticleParameterTypes9 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _fetchFirstChildKBArticleParameterTypes10 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _fetchKBArticleByUrlTitleParameterTypes11 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _fetchLatestKBArticleParameterTypes12 =
		new Class[] {long.class, int.class};
	private static final Class<?>[]
		_fetchLatestKBArticleByExternalReferenceCodeParameterTypes13 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_fetchLatestKBArticleByUrlTitleParameterTypes14 = new Class[] {
			long.class, long.class, String.class, int.class
		};
	private static final Class<?>[] _forceLockKBArticleParameterTypes15 =
		new Class[] {long.class, long.class};
	private static final Class<?>[]
		_getAllDescendantKBArticlesParameterTypes16 = new Class[] {
			long.class, long.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupKBArticlesParameterTypes17 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupKBArticlesCountParameterTypes18 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getGroupKBArticlesRSSParameterTypes19 =
		new Class[] {
			int.class, int.class, String.class, double.class, String.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getKBArticleParameterTypes20 =
		new Class[] {long.class, int.class};
	private static final Class<?>[]
		_getKBArticleAndAllDescendantKBArticlesParameterTypes21 = new Class[] {
			long.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getKBArticleAttachmentParameterTypes22 =
		new Class[] {long.class};
	private static final Class<?>[] _getKBArticleRSSParameterTypes23 =
		new Class[] {
			long.class, int.class, int.class, String.class, double.class,
			String.class, com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getKBArticlesParameterTypes24 =
		new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getKBArticlesParameterTypes25 =
		new Class[] {
			long.class, long[].class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getKBArticlesParameterTypes26 =
		new Class[] {
			long.class, long[].class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getKBArticlesByKeywordsParameterTypes27 =
		new Class[] {long.class, String.class, int.class, int.class, int.class};
	private static final Class<?>[] _getKBArticlesCountParameterTypes28 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _getKBArticlesCountParameterTypes29 =
		new Class[] {long.class, long[].class, int.class};
	private static final Class<?>[] _getKBArticleSearchDisplayParameterTypes30 =
		new Class[] {
			long.class, String.class, String.class, int.class,
			java.util.Date.class, java.util.Date.class, boolean.class,
			int[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getKBArticleVersionsParameterTypes31 =
		new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getKBArticleVersionsCountParameterTypes32 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _getLatestKBArticleParameterTypes33 =
		new Class[] {long.class};
	private static final Class<?>[] _getLatestKBArticleParameterTypes34 =
		new Class[] {long.class, int.class};
	private static final Class<?>[]
		_getLatestKBArticleByExternalReferenceCodeParameterTypes35 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getPreviousAndNextKBArticlesParameterTypes36 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getSectionsKBArticlesParameterTypes37 =
		new Class[] {
			long.class, String[].class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getSectionsKBArticlesCountParameterTypes38 = new Class[] {
			long.class, String[].class, int.class
		};
	private static final Class<?>[] _getTempAttachmentNamesParameterTypes39 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _lockKBArticleParameterTypes40 =
		new Class[] {long.class};
	private static final Class<?>[] _moveKBArticleParameterTypes41 =
		new Class[] {long.class, long.class, long.class, double.class};
	private static final Class<?>[] _moveKBArticleToTrashParameterTypes42 =
		new Class[] {long.class};
	private static final Class<?>[] _revertKBArticleParameterTypes43 =
		new Class[] {
			long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _subscribeGroupKBArticlesParameterTypes44 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _subscribeKBArticleParameterTypes45 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _unlockKBArticleParameterTypes46 =
		new Class[] {long.class};
	private static final Class<?>[]
		_unsubscribeGroupKBArticlesParameterTypes47 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _unsubscribeKBArticleParameterTypes48 =
		new Class[] {long.class};
	private static final Class<?>[] _updateAndUnlockKBArticleParameterTypes49 =
		new Class[] {
			long.class, String.class, String.class, String.class,
			String[].class, String.class, java.util.Date.class,
			java.util.Date.class, java.util.Date.class, String[].class,
			long[].class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateKBArticleParameterTypes50 =
		new Class[] {
			long.class, String.class, String.class, String.class,
			String[].class, String.class, java.util.Date.class,
			java.util.Date.class, java.util.Date.class, String[].class,
			long[].class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateKBArticlesPrioritiesParameterTypes51 = new Class[] {
			long.class, java.util.Map.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:1958682574