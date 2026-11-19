/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.http;

import com.liferay.message.boards.service.MBMessageServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>MBMessageServiceUtil</code> service
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
public class MBMessageServiceHttp {

	public static com.liferay.message.boards.model.MBMessage
			addDiscussionMessage(
				HttpPrincipal httpPrincipal, long groupId, String className,
				long classPK, long threadId, long parentMessageId,
				String subject, String body,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addDiscussionMessage",
				_addDiscussionMessageParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, className, classPK, threadId,
				parentMessageId, subject, body, serviceContext);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage addMessage(
			HttpPrincipal httpPrincipal, long groupId, long categoryId,
			String subject, String body, String format,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addMessage",
				_addMessageParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, subject, body, format,
				inputStreamOVPs, anonymous, priority, allowPingbacks,
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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage addMessage(
			HttpPrincipal httpPrincipal, long groupId, long categoryId,
			String subject, String body, String format, String fileName,
			java.io.File file, boolean anonymous, double priority,
			boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			   java.io.FileNotFoundException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addMessage",
				_addMessageParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, subject, body, format, fileName,
				file, anonymous, priority, allowPingbacks, serviceContext);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof java.io.FileNotFoundException) {
					throw (java.io.FileNotFoundException)exception;
				}

				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage addMessage(
			HttpPrincipal httpPrincipal, long categoryId, String subject,
			String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addMessage",
				_addMessageParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, categoryId, subject, body, serviceContext);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage addMessage(
			HttpPrincipal httpPrincipal, long parentMessageId, String subject,
			String body, String format,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addMessage",
				_addMessageParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, parentMessageId, subject, body, format,
				inputStreamOVPs, anonymous, priority, allowPingbacks,
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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage addMessage(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long parentMessageId, String subject, String body, String format,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addMessage",
				_addMessageParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, parentMessageId, subject,
				body, format, inputStreamOVPs, anonymous, priority,
				allowPingbacks, serviceContext);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void addMessageAttachment(
			HttpPrincipal httpPrincipal, long messageId, String fileName,
			java.io.File file, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addMessageAttachment",
				_addMessageAttachmentParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, fileName, file, mimeType);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempAttachment(
				HttpPrincipal httpPrincipal, long groupId, long categoryId,
				String folderName, String fileName,
				java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "addTempAttachment",
				_addTempAttachmentParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, folderName, fileName,
				inputStream, mimeType);

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

	public static void deleteDiscussionMessage(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "deleteDiscussionMessage",
				_deleteDiscussionMessageParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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

	public static void deleteMessage(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "deleteMessage",
				_deleteMessageParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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

	public static void deleteMessageAttachment(
			HttpPrincipal httpPrincipal, long messageId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "deleteMessageAttachment",
				_deleteMessageAttachmentParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, fileName);

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

	public static void deleteMessageAttachments(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "deleteMessageAttachments",
				_deleteMessageAttachmentsParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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
			HttpPrincipal httpPrincipal, long groupId, long categoryId,
			String folderName, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "deleteTempAttachment",
				_deleteTempAttachmentParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, folderName, fileName);

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

	public static void emptyMessageAttachments(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "emptyMessageAttachments",
				_emptyMessageAttachmentsParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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

	public static com.liferay.message.boards.model.MBMessage
			fetchMBMessageByUrlSubject(
				HttpPrincipal httpPrincipal, long groupId, String urlSubject)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "fetchMBMessageByUrlSubject",
				_fetchMBMessageByUrlSubjectParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, urlSubject);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.message.boards.model.MBMessage>
			getCategoryMessages(
				HttpPrincipal httpPrincipal, long groupId, long categoryId,
				int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getCategoryMessages",
				_getCategoryMessagesParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, status, start, end);

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

			return (java.util.List<com.liferay.message.boards.model.MBMessage>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCategoryMessagesCount(
		HttpPrincipal httpPrincipal, long groupId, long categoryId,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getCategoryMessagesCount",
				_getCategoryMessagesCountParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, status);

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

	public static String getCategoryMessagesRSS(
			HttpPrincipal httpPrincipal, long groupId, long categoryId,
			int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getCategoryMessagesRSS",
				_getCategoryMessagesRSSParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, status, max, type, version,
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

	public static java.util.List<com.liferay.message.boards.model.MBMessage>
			getChildMessages(
				HttpPrincipal httpPrincipal, long parentMessageId,
				boolean flatten,
				com.liferay.portal.kernel.dao.orm.QueryDefinition
					<com.liferay.message.boards.model.MBMessage>
						queryDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getChildMessages",
				_getChildMessagesParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, parentMessageId, flatten, queryDefinition);

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

			return (java.util.List<com.liferay.message.boards.model.MBMessage>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getChildMessagesCount(
			HttpPrincipal httpPrincipal, long parentMessageId, boolean flatten,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBMessage> queryDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getChildMessagesCount",
				_getChildMessagesCountParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, parentMessageId, flatten, queryDefinition);

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

	public static String getCompanyMessagesRSS(
			HttpPrincipal httpPrincipal, long companyId, int status, int max,
			String type, double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getCompanyMessagesRSS",
				_getCompanyMessagesRSSParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, status, max, type, version, displayStyle,
				feedURL, entryURL, themeDisplay);

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

	public static int getGroupMessagesCount(
		HttpPrincipal httpPrincipal, long groupId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getGroupMessagesCount",
				_getGroupMessagesCountParameterTypes21);

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

	public static String getGroupMessagesRSS(
			HttpPrincipal httpPrincipal, long groupId, int status, int max,
			String type, double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getGroupMessagesRSS",
				_getGroupMessagesRSSParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status, max, type, version, displayStyle,
				feedURL, entryURL, themeDisplay);

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

	public static String getGroupMessagesRSS(
			HttpPrincipal httpPrincipal, long groupId, long userId, int status,
			int max, String type, double version, String displayStyle,
			String feedURL, String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getGroupMessagesRSS",
				_getGroupMessagesRSSParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, status, max, type, version,
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

	public static java.util.List<com.liferay.message.boards.model.MBMessage>
			getGroupUserMessageBoardMessagesActivity(
				HttpPrincipal httpPrincipal, long groupId, long userId,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class,
				"getGroupUserMessageBoardMessagesActivity",
				_getGroupUserMessageBoardMessagesActivityParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, start, end);

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

			return (java.util.List<com.liferay.message.boards.model.MBMessage>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupUserMessageBoardMessagesActivityCount(
			HttpPrincipal httpPrincipal, long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class,
				"getGroupUserMessageBoardMessagesActivityCount",
				_getGroupUserMessageBoardMessagesActivityCountParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId);

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

	public static com.liferay.message.boards.model.MBMessage
			getMBMessageByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class,
				"getMBMessageByExternalReferenceCode",
				_getMBMessageByExternalReferenceCodeParameterTypes26);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage getMessage(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getMessage",
				_getMessageParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getMessageDisplay(
				HttpPrincipal httpPrincipal, long messageId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getMessageDisplay",
				_getMessageDisplayParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, status);

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

			return (com.liferay.message.boards.model.MBMessageDisplay)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String[] getTempAttachmentNames(
			HttpPrincipal httpPrincipal, long groupId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getTempAttachmentNames",
				_getTempAttachmentNamesParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderName);

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

	public static int getThreadAnswersCount(
		HttpPrincipal httpPrincipal, long groupId, long categoryId,
		long threadId) {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getThreadAnswersCount",
				_getThreadAnswersCountParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, threadId);

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

	public static java.util.List<com.liferay.message.boards.model.MBMessage>
			getThreadMessages(
				HttpPrincipal httpPrincipal, long groupId, long categoryId,
				long threadId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getThreadMessages",
				_getThreadMessagesParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, threadId, status, start, end);

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

			return (java.util.List<com.liferay.message.boards.model.MBMessage>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getThreadMessagesCount(
			HttpPrincipal httpPrincipal, long groupId, long categoryId,
			long threadId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getThreadMessagesCount",
				_getThreadMessagesCountParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, categoryId, threadId, status);

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

	public static String getThreadMessagesRSS(
			HttpPrincipal httpPrincipal, long threadId, int status, int max,
			String type, double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "getThreadMessagesRSS",
				_getThreadMessagesRSSParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, threadId, status, max, type, version, displayStyle,
				feedURL, entryURL, themeDisplay);

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

	public static void moveMessageAttachmentToTrash(
			HttpPrincipal httpPrincipal, long messageId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "moveMessageAttachmentToTrash",
				_moveMessageAttachmentToTrashParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, fileName);

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

	public static void restoreMessageAttachmentFromTrash(
			HttpPrincipal httpPrincipal, long messageId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "restoreMessageAttachmentFromTrash",
				_restoreMessageAttachmentFromTrashParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, fileName);

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

	public static void subscribeMessage(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "subscribeMessage",
				_subscribeMessageParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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

	public static void unsubscribeMessage(
			HttpPrincipal httpPrincipal, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "unsubscribeMessage",
				_unsubscribeMessageParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId);

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

	public static com.liferay.message.boards.model.MBMessage updateAnswer(
			HttpPrincipal httpPrincipal, long messageId, boolean answer,
			boolean cascade)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "updateAnswer",
				_updateAnswerParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, answer, cascade);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage
			updateDiscussionMessage(
				HttpPrincipal httpPrincipal, String className, long classPK,
				long messageId, String subject, String body,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "updateDiscussionMessage",
				_updateDiscussionMessageParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, messageId, subject, body,
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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.message.boards.model.MBMessage updateMessage(
			HttpPrincipal httpPrincipal, long messageId, String subject,
			String body,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				MBMessageServiceUtil.class, "updateMessage",
				_updateMessageParameterTypes40);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, messageId, subject, body, inputStreamOVPs, priority,
				allowPingbacks, serviceContext);

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

			return (com.liferay.message.boards.model.MBMessage)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(MBMessageServiceHttp.class);

	private static final Class<?>[] _addDiscussionMessageParameterTypes0 =
		new Class[] {
			long.class, String.class, long.class, long.class, long.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addMessageParameterTypes1 = new Class[] {
		long.class, long.class, String.class, String.class, String.class,
		java.util.List.class, boolean.class, double.class, boolean.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addMessageParameterTypes2 = new Class[] {
		long.class, long.class, String.class, String.class, String.class,
		String.class, java.io.File.class, boolean.class, double.class,
		boolean.class, com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addMessageParameterTypes3 = new Class[] {
		long.class, String.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addMessageParameterTypes4 = new Class[] {
		long.class, String.class, String.class, String.class,
		java.util.List.class, boolean.class, double.class, boolean.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addMessageParameterTypes5 = new Class[] {
		String.class, long.class, String.class, String.class, String.class,
		java.util.List.class, boolean.class, double.class, boolean.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addMessageAttachmentParameterTypes6 =
		new Class[] {
			long.class, String.class, java.io.File.class, String.class
		};
	private static final Class<?>[] _addTempAttachmentParameterTypes7 =
		new Class[] {
			long.class, long.class, String.class, String.class,
			java.io.InputStream.class, String.class
		};
	private static final Class<?>[] _deleteDiscussionMessageParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteMessageParameterTypes9 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteMessageAttachmentParameterTypes10 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _deleteMessageAttachmentsParameterTypes11 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteTempAttachmentParameterTypes12 =
		new Class[] {long.class, long.class, String.class, String.class};
	private static final Class<?>[] _emptyMessageAttachmentsParameterTypes13 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchMBMessageByUrlSubjectParameterTypes14 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getCategoryMessagesParameterTypes15 =
		new Class[] {long.class, long.class, int.class, int.class, int.class};
	private static final Class<?>[] _getCategoryMessagesCountParameterTypes16 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _getCategoryMessagesRSSParameterTypes17 =
		new Class[] {
			long.class, long.class, int.class, int.class, String.class,
			double.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getChildMessagesParameterTypes18 =
		new Class[] {
			long.class, boolean.class,
			com.liferay.portal.kernel.dao.orm.QueryDefinition.class
		};
	private static final Class<?>[] _getChildMessagesCountParameterTypes19 =
		new Class[] {
			long.class, boolean.class,
			com.liferay.portal.kernel.dao.orm.QueryDefinition.class
		};
	private static final Class<?>[] _getCompanyMessagesRSSParameterTypes20 =
		new Class[] {
			long.class, int.class, int.class, String.class, double.class,
			String.class, String.class, String.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getGroupMessagesCountParameterTypes21 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getGroupMessagesRSSParameterTypes22 =
		new Class[] {
			long.class, int.class, int.class, String.class, double.class,
			String.class, String.class, String.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[] _getGroupMessagesRSSParameterTypes23 =
		new Class[] {
			long.class, long.class, int.class, int.class, String.class,
			double.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[]
		_getGroupUserMessageBoardMessagesActivityParameterTypes24 =
			new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[]
		_getGroupUserMessageBoardMessagesActivityCountParameterTypes25 =
			new Class[] {long.class, long.class};
	private static final Class<?>[]
		_getMBMessageByExternalReferenceCodeParameterTypes26 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getMessageParameterTypes27 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getMessageDisplayParameterTypes28 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getTempAttachmentNamesParameterTypes29 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getThreadAnswersCountParameterTypes30 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getThreadMessagesParameterTypes31 =
		new Class[] {
			long.class, long.class, long.class, int.class, int.class, int.class
		};
	private static final Class<?>[] _getThreadMessagesCountParameterTypes32 =
		new Class[] {long.class, long.class, long.class, int.class};
	private static final Class<?>[] _getThreadMessagesRSSParameterTypes33 =
		new Class[] {
			long.class, int.class, int.class, String.class, double.class,
			String.class, String.class, String.class,
			com.liferay.portal.kernel.theme.ThemeDisplay.class
		};
	private static final Class<?>[]
		_moveMessageAttachmentToTrashParameterTypes34 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_restoreMessageAttachmentFromTrashParameterTypes35 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _subscribeMessageParameterTypes36 =
		new Class[] {long.class};
	private static final Class<?>[] _unsubscribeMessageParameterTypes37 =
		new Class[] {long.class};
	private static final Class<?>[] _updateAnswerParameterTypes38 =
		new Class[] {long.class, boolean.class, boolean.class};
	private static final Class<?>[] _updateDiscussionMessageParameterTypes39 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateMessageParameterTypes40 =
		new Class[] {
			long.class, String.class, String.class, java.util.List.class,
			double.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:344526856