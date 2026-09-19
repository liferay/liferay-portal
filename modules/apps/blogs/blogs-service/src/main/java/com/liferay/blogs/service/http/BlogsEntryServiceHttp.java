/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.http;

import com.liferay.blogs.service.BlogsEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>BlogsEntryServiceUtil</code> service
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
public class BlogsEntryServiceHttp {

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addAttachmentFileEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, String fileName, String mimeType,
				java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "addAttachmentFileEntry",
				_addAttachmentFileEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, fileName, mimeType,
				inputStream);

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

	public static com.liferay.portal.kernel.repository.model.Folder
			addAttachmentsFolder(HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "addAttachmentsFolder",
				_addAttachmentsFolderParameterTypes1);

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

			return (com.liferay.portal.kernel.repository.model.Folder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.blogs.model.BlogsEntry addEntry(
			HttpPrincipal httpPrincipal, String title, String subtitle,
			String description, String content, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "addEntry",
				_addEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, title, subtitle, description, content,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, allowPingbacks,
				allowTrackbacks, trackbacks, coverImageCaption,
				coverImageImageSelector, smallImageImageSelector,
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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.blogs.model.BlogsEntry addEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			String title, String subtitle, String urlTitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "addEntry",
				_addEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, title, subtitle, urlTitle,
				description, content, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				allowPingbacks, allowTrackbacks, trackbacks, coverImageCaption,
				coverImageImageSelector, smallImageImageSelector,
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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteAttachmentFileEntry(
			HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "deleteAttachmentFileEntry",
				_deleteAttachmentFileEntryParameterTypes4);

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

	public static void deleteEntry(HttpPrincipal httpPrincipal, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "deleteEntry",
				_deleteEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(methodKey, entryId);

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

	public static com.liferay.blogs.model.BlogsEntry
			fetchBlogsEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long groupId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class,
				"fetchBlogsEntryByExternalReferenceCode",
				_fetchBlogsEntryByExternalReferenceCodeParameterTypes6);

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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			getAttachmentFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getAttachmentFileEntry",
				_getAttachmentFileEntryParameterTypes7);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			getAttachmentFileEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class,
				"getAttachmentFileEntryByExternalReferenceCode",
				_getAttachmentFileEntryByExternalReferenceCodeParameterTypes8);

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

			return (com.liferay.portal.kernel.repository.model.FileEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.blogs.model.BlogsEntry
			getBlogsEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long groupId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class,
				"getBlogsEntryByExternalReferenceCode",
				_getBlogsEntryByExternalReferenceCodeParameterTypes9);

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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
			getCompanyEntries(
				HttpPrincipal httpPrincipal, long companyId,
				java.util.Date displayDate, int status, int max)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getCompanyEntries",
				_getCompanyEntriesParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, displayDate, status, max);

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

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String getCompanyEntriesRSS(
			HttpPrincipal httpPrincipal, long companyId,
			java.util.Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getCompanyEntriesRSS",
				_getCompanyEntriesRSSParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, displayDate, status, max, type, version,
				displayStyle, feedURL, entryURL, themeDisplay);

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

	public static com.liferay.blogs.model.BlogsEntry[] getEntriesPrevAndNext(
			HttpPrincipal httpPrincipal, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getEntriesPrevAndNext",
				_getEntriesPrevAndNextParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(methodKey, entryId);

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

			return (com.liferay.blogs.model.BlogsEntry[])returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.blogs.model.BlogsEntry getEntry(
			HttpPrincipal httpPrincipal, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getEntry",
				_getEntryParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(methodKey, entryId);

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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.blogs.model.BlogsEntry getEntry(
			HttpPrincipal httpPrincipal, long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getEntry",
				_getEntryParameterTypes14);

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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupEntries(
			HttpPrincipal httpPrincipal, long groupId,
			java.util.Date displayDate, int status, int max) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntries",
				_getGroupEntriesParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, displayDate, status, max);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupEntries(
			HttpPrincipal httpPrincipal, long groupId,
			java.util.Date displayDate, int status, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntries",
				_getGroupEntriesParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, displayDate, status, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupEntries(
			HttpPrincipal httpPrincipal, long groupId, int status, int max) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntries",
				_getGroupEntriesParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status, max);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupEntries(
			HttpPrincipal httpPrincipal, long groupId, int status, int start,
			int end) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntries",
				_getGroupEntriesParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupEntries(
			HttpPrincipal httpPrincipal, long groupId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.blogs.model.BlogsEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntries",
				_getGroupEntriesParameterTypes19);

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

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, java.util.Date displayDate,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntriesCount",
				_getGroupEntriesCountParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, displayDate, status);

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

	public static int getGroupEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntriesCount",
				_getGroupEntriesCountParameterTypes21);

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

	public static String getGroupEntriesRSS(
			HttpPrincipal httpPrincipal, long groupId,
			java.util.Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupEntriesRSS",
				_getGroupEntriesRSSParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, displayDate, status, max, type, version,
				displayStyle, feedURL, entryURL, themeDisplay);

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

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupUserEntries(
			HttpPrincipal httpPrincipal, long groupId, long userId, int status,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.blogs.model.BlogsEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupUserEntries",
				_getGroupUserEntriesParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, status, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
		getGroupUserEntries(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			int[] statuses, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.blogs.model.BlogsEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupUserEntries",
				_getGroupUserEntriesParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, statuses, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupUserEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long userId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupUserEntriesCount",
				_getGroupUserEntriesCountParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, status);

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

	public static int getGroupUserEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long userId,
		int[] statuses) {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupUserEntriesCount",
				_getGroupUserEntriesCountParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, statuses);

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

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
			getGroupsEntries(
				HttpPrincipal httpPrincipal, long companyId, long groupId,
				java.util.Date displayDate, int status, int max)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getGroupsEntries",
				_getGroupsEntriesParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, groupId, displayDate, status, max);

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

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.blogs.model.BlogsEntry>
			getOrganizationEntries(
				HttpPrincipal httpPrincipal, long organizationId,
				java.util.Date displayDate, int status, int max)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getOrganizationEntries",
				_getOrganizationEntriesParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId, displayDate, status, max);

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

			return (java.util.List<com.liferay.blogs.model.BlogsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String getOrganizationEntriesRSS(
			HttpPrincipal httpPrincipal, long organizationId,
			java.util.Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "getOrganizationEntriesRSS",
				_getOrganizationEntriesRSSParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, organizationId, displayDate, status, max, type,
				version, displayStyle, feedURL, entryURL, themeDisplay);

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

	public static com.liferay.blogs.model.BlogsEntry moveEntryToTrash(
			HttpPrincipal httpPrincipal, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "moveEntryToTrash",
				_moveEntryToTrashParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(methodKey, entryId);

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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void restoreEntryFromTrash(
			HttpPrincipal httpPrincipal, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "restoreEntryFromTrash",
				_restoreEntryFromTrashParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(methodKey, entryId);

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

	public static void subscribe(HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "subscribe",
				_subscribeParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

	public static void unsubscribe(HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "unsubscribe",
				_unsubscribeParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

	public static com.liferay.blogs.model.BlogsEntry updateEntry(
			HttpPrincipal httpPrincipal, long entryId, String title,
			String subtitle, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "updateEntry",
				_updateEntryParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, entryId, title, subtitle, description, content,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, allowPingbacks,
				allowTrackbacks, trackbacks, coverImageCaption,
				coverImageImageSelector, smallImageImageSelector,
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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.blogs.model.BlogsEntry updateEntry(
			HttpPrincipal httpPrincipal, long entryId, String title,
			String subtitle, String urlTitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				BlogsEntryServiceUtil.class, "updateEntry",
				_updateEntryParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, entryId, title, subtitle, urlTitle, description,
				content, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, allowPingbacks,
				allowTrackbacks, trackbacks, coverImageCaption,
				coverImageImageSelector, smallImageImageSelector,
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

			return (com.liferay.blogs.model.BlogsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		BlogsEntryServiceHttp.class);

	private static final Class<?>[] _addAttachmentFileEntryParameterTypes0 =
		new Class[] {
			String.class, long.class, String.class, String.class,
			java.io.InputStream.class
		};
	private static final Class<?>[] _addAttachmentsFolderParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _addEntryParameterTypes2 = new Class[] {
		String.class, String.class, String.class, String.class, int.class,
		int.class, int.class, int.class, int.class, boolean.class,
		boolean.class, String[].class, String.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addEntryParameterTypes3 = new Class[] {
		String.class, String.class, String.class, String.class, String.class,
		String.class, int.class, int.class, int.class, int.class, int.class,
		boolean.class, boolean.class, String[].class, String.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteAttachmentFileEntryParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteEntryParameterTypes5 = new Class[] {
		long.class
	};
	private static final Class<?>[]
		_fetchBlogsEntryByExternalReferenceCodeParameterTypes6 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getAttachmentFileEntryParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getAttachmentFileEntryByExternalReferenceCodeParameterTypes8 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getBlogsEntryByExternalReferenceCodeParameterTypes9 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getCompanyEntriesParameterTypes10 =
		new Class[] {long.class, java.util.Date.class, int.class, int.class};
	private static final Class<?>[] _getCompanyEntriesRSSParameterTypes11 =
		new Class[] {
			long.class, java.util.Date.class, int.class, int.class,
			String.class, double.class, String.class, String.class,
			String.class, com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getEntriesPrevAndNextParameterTypes12 =
		new Class[] {long.class};
	private static final Class<?>[] _getEntryParameterTypes13 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getEntryParameterTypes14 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[] _getGroupEntriesParameterTypes15 =
		new Class[] {long.class, java.util.Date.class, int.class, int.class};
	private static final Class<?>[] _getGroupEntriesParameterTypes16 =
		new Class[] {
			long.class, java.util.Date.class, int.class, int.class, int.class
		};
	private static final Class<?>[] _getGroupEntriesParameterTypes17 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getGroupEntriesParameterTypes18 =
		new Class[] {long.class, int.class, int.class, int.class};
	private static final Class<?>[] _getGroupEntriesParameterTypes19 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupEntriesCountParameterTypes20 =
		new Class[] {long.class, java.util.Date.class, int.class};
	private static final Class<?>[] _getGroupEntriesCountParameterTypes21 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getGroupEntriesRSSParameterTypes22 =
		new Class[] {
			long.class, java.util.Date.class, int.class, int.class,
			String.class, double.class, String.class, String.class,
			String.class, com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getGroupUserEntriesParameterTypes23 =
		new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupUserEntriesParameterTypes24 =
		new Class[] {
			long.class, long.class, int[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupUserEntriesCountParameterTypes25 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _getGroupUserEntriesCountParameterTypes26 =
		new Class[] {long.class, long.class, int[].class};
	private static final Class<?>[] _getGroupsEntriesParameterTypes27 =
		new Class[] {
			long.class, long.class, java.util.Date.class, int.class, int.class
		};
	private static final Class<?>[] _getOrganizationEntriesParameterTypes28 =
		new Class[] {long.class, java.util.Date.class, int.class, int.class};
	private static final Class<?>[] _getOrganizationEntriesRSSParameterTypes29 =
		new Class[] {
			long.class, java.util.Date.class, int.class, int.class,
			String.class, double.class, String.class, String.class,
			String.class, com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _moveEntryToTrashParameterTypes30 =
		new Class[] {long.class};
	private static final Class<?>[] _restoreEntryFromTrashParameterTypes31 =
		new Class[] {long.class};
	private static final Class<?>[] _subscribeParameterTypes32 = new Class[] {
		long.class
	};
	private static final Class<?>[] _unsubscribeParameterTypes33 = new Class[] {
		long.class
	};
	private static final Class<?>[] _updateEntryParameterTypes34 = new Class[] {
		long.class, String.class, String.class, String.class, String.class,
		int.class, int.class, int.class, int.class, int.class, boolean.class,
		boolean.class, String[].class, String.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _updateEntryParameterTypes35 = new Class[] {
		long.class, String.class, String.class, String.class, String.class,
		String.class, int.class, int.class, int.class, int.class, int.class,
		boolean.class, boolean.class, String[].class, String.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};

}
// LIFERAY-SERVICE-BUILDER-HASH:-500313778