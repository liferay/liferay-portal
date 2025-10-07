/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.http;

import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>JournalArticleServiceUtil</code> service
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
public class JournalArticleServiceHttp {

	public static com.liferay.journal.model.JournalArticle addArticle(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long folderId, long classNameId, long classPK,
			String articleId, boolean autoArticleId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			String layoutUuid, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, int reviewDateMonth,
			int reviewDateDay, int reviewDateYear, int reviewDateHour,
			int reviewDateMinute, boolean neverReview, boolean indexable,
			boolean smallImage, long smallImageId, int smallImageSource,
			String smallImageURL, java.io.File smallFile,
			java.util.Map<String, byte[]> images, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "addArticle",
				_addArticleParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, folderId,
				classNameId, classPK, articleId, autoArticleId, titleMap,
				descriptionMap, friendlyURLMap, content, ddmStructureId,
				ddmTemplateKey, layoutUuid, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, neverExpire,
				reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
				reviewDateMinute, neverReview, indexable, smallImage,
				smallImageId, smallImageSource, smallImageURL, smallFile,
				images, articleURL, serviceContext);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle addArticle(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long folderId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "addArticle",
				_addArticleParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, folderId, titleMap,
				descriptionMap, content, ddmStructureId, ddmTemplateKey,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle
			addArticleDefaultValues(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classPK, java.util.Map<java.util.Locale, String> titleMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String content, long ddmStructureId, String ddmTemplateKey,
				String layoutUuid, int displayDateMonth, int displayDateDay,
				int displayDateYear, int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				int reviewDateMonth, int reviewDateDay, int reviewDateYear,
				int reviewDateHour, int reviewDateMinute, boolean neverReview,
				boolean indexable, boolean smallImage, long smallImageId,
				int smallImageSource, String smallImageURL,
				java.io.File smallImageFile,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "addArticleDefaultValues",
				_addArticleDefaultValuesParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classPK, titleMap,
				descriptionMap, content, ddmStructureId, ddmTemplateKey,
				layoutUuid, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, reviewDateMonth,
				reviewDateDay, reviewDateYear, reviewDateHour, reviewDateMinute,
				neverReview, indexable, smallImage, smallImageId,
				smallImageSource, smallImageURL, smallImageFile,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle copyArticle(
			HttpPrincipal httpPrincipal, long groupId, String sourceArticleId,
			String targetArticleId, boolean autoArticleId, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "copyArticle",
				_copyArticleParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, sourceArticleId, targetArticleId,
				autoArticleId, version);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "deleteArticle",
				_deleteArticleParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version, articleURL,
				serviceContext);

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

	public static void deleteArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "deleteArticle",
				_deleteArticleParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, articleURL, serviceContext);

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

	public static void deleteArticleDefaultValues(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			long ddmStructureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "deleteArticleDefaultValues",
				_deleteArticleDefaultValuesParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, ddmStructureId);

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

	public static com.liferay.journal.model.JournalArticle expireArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "expireArticle",
				_expireArticleParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version, articleURL,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void expireArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "expireArticle",
				_expireArticleParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, articleURL, serviceContext);

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

	public static com.liferay.journal.model.JournalArticle fetchArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "fetchArticle",
				_fetchArticleParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle
			fetchLatestArticleByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long groupId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class,
				"fetchLatestArticleByExternalReferenceCode",
				_fetchLatestArticleByExternalReferenceCodeParameterTypes10);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getArticle(
			HttpPrincipal httpPrincipal, long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticle",
				_getArticleParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(methodKey, id);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticle",
				_getArticleParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticle",
				_getArticleParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getArticle(
			HttpPrincipal httpPrincipal, long groupId, String className,
			long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticle",
				_getArticleParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, className, classPK);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getArticleByUrlTitle(
			HttpPrincipal httpPrincipal, long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticleByUrlTitle",
				_getArticleByUrlTitleParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, urlTitle);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String getArticleContent(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version, String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticleContent",
				_getArticleContentParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version, languageId,
				portletRequestModel, themeDisplay);

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

	public static String getArticleContent(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticleContent",
				_getArticleContentParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, languageId, portletRequestModel,
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

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticles(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			java.util.Locale locale) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticles",
				_getArticlesParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, locale);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticles(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			java.util.Locale locale, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticles",
				_getArticlesParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, locale, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByArticleId(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByArticleId",
				_getArticlesByArticleIdParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByArticleId(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByArticleId",
				_getArticlesByArticleIdParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByLayoutUuid(
			HttpPrincipal httpPrincipal, long groupId, String layoutUuid) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByLayoutUuid",
				_getArticlesByLayoutUuidParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutUuid);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByLayoutUuid(
			HttpPrincipal httpPrincipal, long groupId, String layoutUuid,
			int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByLayoutUuid",
				_getArticlesByLayoutUuidParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutUuid, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getArticlesByLayoutUuidCount(
		HttpPrincipal httpPrincipal, long groupId, String layoutUuid) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByLayoutUuidCount",
				_getArticlesByLayoutUuidCountParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutUuid);

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

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByStructureId(
			HttpPrincipal httpPrincipal, long groupId, long ddmStructureId,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByStructureId",
				_getArticlesByStructureIdParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, ddmStructureId, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByStructureId(
			HttpPrincipal httpPrincipal, long groupId, long ddmStructureId,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByStructureId",
				_getArticlesByStructureIdParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, ddmStructureId, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByStructureId(
			HttpPrincipal httpPrincipal, long groupId, long ddmStructureId,
			java.util.Locale locale, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByStructureId",
				_getArticlesByStructureIdParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, ddmStructureId, locale, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByStructureId(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			long ddmStructureId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByStructureId",
				_getArticlesByStructureIdParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, ddmStructureId, status, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByStructureId(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			long ddmStructureId, java.util.Locale locale, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByStructureId",
				_getArticlesByStructureIdParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, ddmStructureId, locale, status,
				start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getArticlesByStructureId(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			long classNameId, long ddmStructureId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesByStructureId",
				_getArticlesByStructureIdParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, classNameId, ddmStructureId,
				status, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, long folderId) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesCount",
				_getArticlesCountParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId);

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

	public static int getArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, long folderId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesCount",
				_getArticlesCountParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, status);

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

	public static int getArticlesCountByArticleId(
		HttpPrincipal httpPrincipal, long groupId, String articleId) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesCountByArticleId",
				_getArticlesCountByArticleIdParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

	public static int getArticlesCountByArticleId(
		HttpPrincipal httpPrincipal, long groupId, String articleId,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getArticlesCountByArticleId",
				_getArticlesCountByArticleIdParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, status);

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

	public static int getArticlesCountByStructureId(
		HttpPrincipal httpPrincipal, long groupId, long ddmStructureId) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class,
				"getArticlesCountByStructureId",
				_getArticlesCountByStructureIdParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, ddmStructureId);

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

	public static int getArticlesCountByStructureId(
		HttpPrincipal httpPrincipal, long groupId, long ddmStructureId,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class,
				"getArticlesCountByStructureId",
				_getArticlesCountByStructureIdParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, ddmStructureId, status);

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

	public static int getArticlesCountByStructureId(
		HttpPrincipal httpPrincipal, long groupId, long classNameId,
		long ddmStructureId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class,
				"getArticlesCountByStructureId",
				_getArticlesCountByStructureIdParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, ddmStructureId, status);

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

	public static int getArticlesCountByStructureId(
		HttpPrincipal httpPrincipal, long groupId, long folderId,
		long classNameId, long ddmStructureId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class,
				"getArticlesCountByStructureId",
				_getArticlesCountByStructureIdParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, classNameId, ddmStructureId,
				status);

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

	public static com.liferay.journal.model.JournalArticle
			getDisplayArticleByUrlTitle(
				HttpPrincipal httpPrincipal, long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getDisplayArticleByUrlTitle",
				_getDisplayArticleByUrlTitleParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, urlTitle);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFoldersAndArticlesCount(
		HttpPrincipal httpPrincipal, long groupId,
		java.util.List<Long> folderIds) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getFoldersAndArticlesCount",
				_getFoldersAndArticlesCountParameterTypes40);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderIds);

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

	public static java.util.List<com.liferay.journal.model.JournalArticle>
			getGroupArticles(
				HttpPrincipal httpPrincipal, long groupId, long userId,
				long rootFolderId, int status, boolean includeOwner, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.journal.model.JournalArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticles",
				_getGroupArticlesParameterTypes41);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, status, includeOwner,
				start, end, orderByComparator);

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

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
			getGroupArticles(
				HttpPrincipal httpPrincipal, long groupId, long userId,
				long rootFolderId, int status, boolean includeOwner,
				java.util.Locale locale, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.journal.model.JournalArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticles",
				_getGroupArticlesParameterTypes42);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, status, includeOwner,
				locale, start, end, orderByComparator);

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

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
			getGroupArticles(
				HttpPrincipal httpPrincipal, long groupId, long userId,
				long rootFolderId, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.journal.model.JournalArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticles",
				_getGroupArticlesParameterTypes43);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, status, start, end,
				orderByComparator);

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

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
			getGroupArticles(
				HttpPrincipal httpPrincipal, long groupId, long userId,
				long rootFolderId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.journal.model.JournalArticle>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticles",
				_getGroupArticlesParameterTypes44);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, start, end,
				orderByComparator);

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

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupArticlesCount(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long rootFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticlesCount",
				_getGroupArticlesCountParameterTypes45);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId);

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

	public static int getGroupArticlesCount(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long rootFolderId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticlesCount",
				_getGroupArticlesCountParameterTypes46);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, status);

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

	public static int getGroupArticlesCount(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long rootFolderId, int status, boolean includeOwner)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getGroupArticlesCount",
				_getGroupArticlesCountParameterTypes47);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, status, includeOwner);

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

	public static com.liferay.journal.model.JournalArticle getLatestArticle(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLatestArticle",
				_getLatestArticleParameterTypes48);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getLatestArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLatestArticle",
				_getLatestArticleParameterTypes49);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, status);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle getLatestArticle(
			HttpPrincipal httpPrincipal, long groupId, String className,
			long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLatestArticle",
				_getLatestArticleParameterTypes50);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, className, classPK);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle
			getLatestArticleByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long groupId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class,
				"getLatestArticleByExternalReferenceCode",
				_getLatestArticleByExternalReferenceCodeParameterTypes51);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getLatestArticles(
			HttpPrincipal httpPrincipal, long groupId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.journal.model.JournalArticle> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLatestArticles",
				_getLatestArticlesParameterTypes52);

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

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getLatestArticlesCount(
		HttpPrincipal httpPrincipal, long groupId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLatestArticlesCount",
				_getLatestArticlesCountParameterTypes53);

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

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getLayoutArticles(HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLayoutArticles",
				_getLayoutArticlesParameterTypes54);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.journal.model.JournalArticle>
		getLayoutArticles(
			HttpPrincipal httpPrincipal, long groupId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLayoutArticles",
				_getLayoutArticlesParameterTypes55);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.journal.model.JournalArticle>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getLayoutArticlesCount(
		HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "getLayoutArticlesCount",
				_getLayoutArticlesCountParameterTypes56);

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

	public static void moveArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "moveArticle",
				_moveArticleParameterTypes57);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, newFolderId, serviceContext);

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

	public static com.liferay.journal.model.JournalArticle moveArticleFromTrash(
			HttpPrincipal httpPrincipal, long groupId, long resourcePrimKey,
			long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "moveArticleFromTrash",
				_moveArticleFromTrashParameterTypes58);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, resourcePrimKey, newFolderId,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle moveArticleFromTrash(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "moveArticleFromTrash",
				_moveArticleFromTrashParameterTypes59);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, newFolderId, serviceContext);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle moveArticleToTrash(
			HttpPrincipal httpPrincipal, long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "moveArticleToTrash",
				_moveArticleToTrashParameterTypes60);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void removeArticleLocale(
			HttpPrincipal httpPrincipal, long companyId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "removeArticleLocale",
				_removeArticleLocaleParameterTypes61);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, languageId);

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

	public static com.liferay.journal.model.JournalArticle removeArticleLocale(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "removeArticleLocale",
				_removeArticleLocaleParameterTypes62);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version, languageId);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void restoreArticleFromTrash(
			HttpPrincipal httpPrincipal, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "restoreArticleFromTrash",
				_restoreArticleFromTrashParameterTypes63);

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

	public static void restoreArticleFromTrash(
			HttpPrincipal httpPrincipal, long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "restoreArticleFromTrash",
				_restoreArticleFromTrashParameterTypes64);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

	public static com.liferay.journal.model.JournalArticle revertArticle(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "revertArticle",
				_revertArticleParameterTypes65);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void subscribe(
			HttpPrincipal httpPrincipal, long groupId, long articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "subscribe",
				_subscribeParameterTypes66);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

	public static void subscribeStructure(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long ddmStructureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "subscribeStructure",
				_subscribeStructureParameterTypes67);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, ddmStructureId);

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

	public static void unsubscribe(
			HttpPrincipal httpPrincipal, long groupId, long articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "unsubscribe",
				_unsubscribeParameterTypes68);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId);

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

	public static void unsubscribeStructure(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long ddmStructureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "unsubscribeStructure",
				_unsubscribeStructureParameterTypes69);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, ddmStructureId);

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

	public static com.liferay.journal.model.JournalArticle updateArticle(
			HttpPrincipal httpPrincipal, long userId, long groupId,
			long folderId, String articleId, double version,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String content, String layoutUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "updateArticle",
				_updateArticleParameterTypes70);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, groupId, folderId, articleId, version,
				titleMap, descriptionMap, content, layoutUuid, serviceContext);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle updateArticle(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			String articleId, double version,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			String content, String ddmTemplateKey, String layoutUuid,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			java.io.File smallFile, java.util.Map<String, byte[]> images,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "updateArticle",
				_updateArticleParameterTypes71);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, articleId, version, titleMap,
				descriptionMap, friendlyURLMap, content, ddmTemplateKey,
				layoutUuid, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, reviewDateMonth,
				reviewDateDay, reviewDateYear, reviewDateHour, reviewDateMinute,
				neverReview, indexable, smallImage, smallImageId,
				smallImageSource, smallImageURL, smallFile, images, articleURL,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle updateArticle(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			String articleId, double version, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "updateArticle",
				_updateArticleParameterTypes72);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, articleId, version, content,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle
			updateArticleDefaultValues(
				HttpPrincipal httpPrincipal, long groupId, String articleId,
				java.util.Map<java.util.Locale, String> titleMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String content, String ddmTemplateKey, String layoutUuid,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				int reviewDateMonth, int reviewDateDay, int reviewDateYear,
				int reviewDateHour, int reviewDateMinute, boolean neverReview,
				boolean indexable, boolean smallImage, long smallImageId,
				int smallImageSource, String smallImageURL,
				java.io.File smallImageFile,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "updateArticleDefaultValues",
				_updateArticleDefaultValuesParameterTypes73);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, titleMap, descriptionMap,
				content, ddmTemplateKey, layoutUuid, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
				reviewDateHour, reviewDateMinute, neverReview, indexable,
				smallImage, smallImageId, smallImageSource, smallImageURL,
				smallImageFile, serviceContext);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle
			updateArticleTranslation(
				HttpPrincipal httpPrincipal, long groupId, String articleId,
				double version, java.util.Locale locale, String title,
				String description, String content,
				java.util.Map<String, byte[]> images,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "updateArticleTranslation",
				_updateArticleTranslationParameterTypes74);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version, locale, title,
				description, content, images, serviceContext);

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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.journal.model.JournalArticle updateStatus(
			HttpPrincipal httpPrincipal, long groupId, String articleId,
			double version, int status, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				JournalArticleServiceUtil.class, "updateStatus",
				_updateStatusParameterTypes75);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, articleId, version, status, articleURL,
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

			return (com.liferay.journal.model.JournalArticle)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		JournalArticleServiceHttp.class);

	private static final Class<?>[] _addArticleParameterTypes0 = new Class[] {
		String.class, long.class, long.class, long.class, long.class,
		String.class, boolean.class, java.util.Map.class, java.util.Map.class,
		java.util.Map.class, String.class, long.class, String.class,
		String.class, int.class, int.class, int.class, int.class, int.class,
		int.class, int.class, int.class, int.class, int.class, boolean.class,
		int.class, int.class, int.class, int.class, int.class, boolean.class,
		boolean.class, boolean.class, long.class, int.class, String.class,
		java.io.File.class, java.util.Map.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addArticleParameterTypes1 = new Class[] {
		String.class, long.class, long.class, java.util.Map.class,
		java.util.Map.class, String.class, long.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addArticleDefaultValuesParameterTypes2 =
		new Class[] {
			long.class, long.class, long.class, java.util.Map.class,
			java.util.Map.class, String.class, long.class, String.class,
			String.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class,
			boolean.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class, boolean.class, boolean.class, long.class,
			int.class, String.class, java.io.File.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyArticleParameterTypes3 = new Class[] {
		long.class, String.class, String.class, boolean.class, double.class
	};
	private static final Class<?>[] _deleteArticleParameterTypes4 =
		new Class[] {
			long.class, String.class, double.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteArticleParameterTypes5 =
		new Class[] {
			long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteArticleDefaultValuesParameterTypes6 =
		new Class[] {long.class, String.class, long.class};
	private static final Class<?>[] _expireArticleParameterTypes7 =
		new Class[] {
			long.class, String.class, double.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _expireArticleParameterTypes8 =
		new Class[] {
			long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _fetchArticleParameterTypes9 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[]
		_fetchLatestArticleByExternalReferenceCodeParameterTypes10 =
			new Class[] {long.class, String.class};
	private static final Class<?>[] _getArticleParameterTypes11 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getArticleParameterTypes12 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[] _getArticleParameterTypes13 = new Class[] {
		long.class, String.class, double.class
	};
	private static final Class<?>[] _getArticleParameterTypes14 = new Class[] {
		long.class, String.class, long.class
	};
	private static final Class<?>[] _getArticleByUrlTitleParameterTypes15 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getArticleContentParameterTypes16 =
		new Class[] {
			long.class, String.class, double.class, String.class,
			com.liferay.portal.kernel.portlet.PortletRequestModel.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getArticleContentParameterTypes17 =
		new Class[] {
			long.class, String.class, String.class,
			com.liferay.portal.kernel.portlet.PortletRequestModel.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getArticlesParameterTypes18 = new Class[] {
		long.class, long.class, java.util.Locale.class
	};
	private static final Class<?>[] _getArticlesParameterTypes19 = new Class[] {
		long.class, long.class, java.util.Locale.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getArticlesByArticleIdParameterTypes20 =
		new Class[] {
			long.class, String.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByArticleIdParameterTypes21 =
		new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByLayoutUuidParameterTypes22 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getArticlesByLayoutUuidParameterTypes23 =
		new Class[] {long.class, String.class, int.class, int.class};
	private static final Class<?>[]
		_getArticlesByLayoutUuidCountParameterTypes24 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getArticlesByStructureIdParameterTypes25 =
		new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByStructureIdParameterTypes26 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByStructureIdParameterTypes27 =
		new Class[] {
			long.class, long.class, java.util.Locale.class, int.class,
			int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByStructureIdParameterTypes28 =
		new Class[] {
			long.class, long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByStructureIdParameterTypes29 =
		new Class[] {
			long.class, long.class, long.class, java.util.Locale.class,
			int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesByStructureIdParameterTypes30 =
		new Class[] {
			long.class, long.class, long.class, long.class, int.class,
			int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getArticlesCountParameterTypes31 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getArticlesCountParameterTypes32 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[]
		_getArticlesCountByArticleIdParameterTypes33 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getArticlesCountByArticleIdParameterTypes34 = new Class[] {
			long.class, String.class, int.class
		};
	private static final Class<?>[]
		_getArticlesCountByStructureIdParameterTypes35 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getArticlesCountByStructureIdParameterTypes36 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getArticlesCountByStructureIdParameterTypes37 = new Class[] {
			long.class, long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getArticlesCountByStructureIdParameterTypes38 = new Class[] {
			long.class, long.class, long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getDisplayArticleByUrlTitleParameterTypes39 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getFoldersAndArticlesCountParameterTypes40 = new Class[] {
			long.class, java.util.List.class
		};
	private static final Class<?>[] _getGroupArticlesParameterTypes41 =
		new Class[] {
			long.class, long.class, long.class, int.class, boolean.class,
			int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupArticlesParameterTypes42 =
		new Class[] {
			long.class, long.class, long.class, int.class, boolean.class,
			java.util.Locale.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupArticlesParameterTypes43 =
		new Class[] {
			long.class, long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupArticlesParameterTypes44 =
		new Class[] {
			long.class, long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupArticlesCountParameterTypes45 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getGroupArticlesCountParameterTypes46 =
		new Class[] {long.class, long.class, long.class, int.class};
	private static final Class<?>[] _getGroupArticlesCountParameterTypes47 =
		new Class[] {
			long.class, long.class, long.class, int.class, boolean.class
		};
	private static final Class<?>[] _getLatestArticleParameterTypes48 =
		new Class[] {long.class};
	private static final Class<?>[] _getLatestArticleParameterTypes49 =
		new Class[] {long.class, String.class, int.class};
	private static final Class<?>[] _getLatestArticleParameterTypes50 =
		new Class[] {long.class, String.class, long.class};
	private static final Class<?>[]
		_getLatestArticleByExternalReferenceCodeParameterTypes51 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getLatestArticlesParameterTypes52 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getLatestArticlesCountParameterTypes53 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getLayoutArticlesParameterTypes54 =
		new Class[] {long.class};
	private static final Class<?>[] _getLayoutArticlesParameterTypes55 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getLayoutArticlesCountParameterTypes56 =
		new Class[] {long.class};
	private static final Class<?>[] _moveArticleParameterTypes57 = new Class[] {
		long.class, String.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _moveArticleFromTrashParameterTypes58 =
		new Class[] {
			long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _moveArticleFromTrashParameterTypes59 =
		new Class[] {
			long.class, String.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _moveArticleToTrashParameterTypes60 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _removeArticleLocaleParameterTypes61 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _removeArticleLocaleParameterTypes62 =
		new Class[] {long.class, String.class, double.class, String.class};
	private static final Class<?>[] _restoreArticleFromTrashParameterTypes63 =
		new Class[] {long.class};
	private static final Class<?>[] _restoreArticleFromTrashParameterTypes64 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _revertArticleParameterTypes65 =
		new Class[] {long.class, String.class, double.class};
	private static final Class<?>[] _subscribeParameterTypes66 = new Class[] {
		long.class, long.class
	};
	private static final Class<?>[] _subscribeStructureParameterTypes67 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _unsubscribeParameterTypes68 = new Class[] {
		long.class, long.class
	};
	private static final Class<?>[] _unsubscribeStructureParameterTypes69 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _updateArticleParameterTypes70 =
		new Class[] {
			long.class, long.class, long.class, String.class, double.class,
			java.util.Map.class, java.util.Map.class, String.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateArticleParameterTypes71 =
		new Class[] {
			long.class, long.class, String.class, double.class,
			java.util.Map.class, java.util.Map.class, java.util.Map.class,
			String.class, String.class, String.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, boolean.class, int.class, int.class,
			int.class, int.class, int.class, boolean.class, boolean.class,
			boolean.class, long.class, int.class, String.class,
			java.io.File.class, java.util.Map.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateArticleParameterTypes72 =
		new Class[] {
			long.class, long.class, String.class, double.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateArticleDefaultValuesParameterTypes73 = new Class[] {
			long.class, String.class, java.util.Map.class, java.util.Map.class,
			String.class, String.class, String.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, boolean.class, int.class, int.class,
			int.class, int.class, int.class, boolean.class, boolean.class,
			boolean.class, long.class, int.class, String.class,
			java.io.File.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateArticleTranslationParameterTypes74 =
		new Class[] {
			long.class, String.class, double.class, java.util.Locale.class,
			String.class, String.class, String.class, java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateStatusParameterTypes75 =
		new Class[] {
			long.class, String.class, double.class, int.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}