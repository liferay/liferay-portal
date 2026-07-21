/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.http;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>DLAppServiceUtil</code> service
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
public class DLAppServiceHttp {

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				String sourceFileName, String mimeType, String title,
				String description, String changeLog, java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addFileEntry",
				_addFileEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, sourceFileName, mimeType,
				title, description, changeLog, file, serviceContext);

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
			addFileEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String urlTitle,
				String description, String changeLog, byte[] bytes,
				java.util.Date displayDate, java.util.Date expirationDate,
				java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addFileEntry",
				_addFileEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, repositoryId, folderId,
				sourceFileName, mimeType, title, urlTitle, description,
				changeLog, bytes, displayDate, expirationDate, reviewDate,
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
			addFileEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String urlTitle,
				String description, String changeLog, java.io.File file,
				java.util.Date displayDate, java.util.Date expirationDate,
				java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addFileEntry",
				_addFileEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, repositoryId, folderId,
				sourceFileName, mimeType, title, urlTitle, description,
				changeLog, file, displayDate, expirationDate, reviewDate,
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
			addFileEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String urlTitle,
				String description, String changeLog,
				java.io.InputStream inputStream, long size,
				java.util.Date displayDate, java.util.Date expirationDate,
				java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addFileEntry",
				_addFileEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, repositoryId, folderId,
				sourceFileName, mimeType, title, urlTitle, description,
				changeLog, inputStream, size, displayDate, expirationDate,
				reviewDate, serviceContext);

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

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			addFileShortcut(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long repositoryId, long folderId, long toFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addFileShortcut",
				_addFileShortcutParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, repositoryId, folderId,
				toFileEntryId, serviceContext);

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

			return (com.liferay.portal.kernel.repository.model.FileShortcut)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.Folder addFolder(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long repositoryId, long parentFolderId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addFolder", _addFolderParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, repositoryId, parentFolderId,
				name, description, serviceContext);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				HttpPrincipal httpPrincipal, long groupId, long folderId,
				String folderName, String fileName, java.io.File file,
				String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addTempFileEntry",
				_addTempFileEntryParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, folderName, fileName, file,
				mimeType);

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
			addTempFileEntry(
				HttpPrincipal httpPrincipal, long groupId, long folderId,
				String folderName, String fileName,
				java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "addTempFileEntry",
				_addTempFileEntryParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, folderName, fileName, inputStream,
				mimeType);

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

	public static void cancelCheckOut(
			HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "cancelCheckOut",
				_cancelCheckOutParameterTypes8);

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

	public static void checkInFileEntry(
			HttpPrincipal httpPrincipal, long fileEntryId,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			String changeLog,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "checkInFileEntry",
				_checkInFileEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, dlVersionNumberIncrease, changeLog,
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

	public static void checkInFileEntry(
			HttpPrincipal httpPrincipal, long fileEntryId, String lockUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "checkInFileEntry",
				_checkInFileEntryParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, lockUuid, serviceContext);

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

	public static void checkOutFileEntry(
			HttpPrincipal httpPrincipal, long fileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "checkOutFileEntry",
				_checkOutFileEntryParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, serviceContext);

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
			checkOutFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId, String owner,
				long expirationTime,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "checkOutFileEntry",
				_checkOutFileEntryParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, owner, expirationTime, serviceContext);

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
			copyFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId,
				long destinationFolderId, long destinationRepositoryId,
				long fileEntryTypeId, long[] groupIds,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "copyFileEntry",
				_copyFileEntryParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, destinationFolderId,
				destinationRepositoryId, fileEntryTypeId, groupIds,
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

			return (com.liferay.portal.kernel.repository.model.FileEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			copyFileShortcut(
				HttpPrincipal httpPrincipal, long fileShortcutId,
				long destinationFolderId, long destinationRepositoryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "copyFileShortcut",
				_copyFileShortcutParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileShortcutId, destinationFolderId,
				destinationRepositoryId, serviceContext);

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

			return (com.liferay.portal.kernel.repository.model.FileShortcut)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.Folder copyFolder(
			HttpPrincipal httpPrincipal, long sourceRepositoryId,
			long sourceFolderId, long destinationRepositoryId,
			long destinationParentFolderId,
			java.util.Map<Long, Long> fileEntryTypeIds, long[] groupIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "copyFolder",
				_copyFolderParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, sourceRepositoryId, sourceFolderId,
				destinationRepositoryId, destinationParentFolderId,
				fileEntryTypeIds, groupIds, serviceContext);

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

	public static com.liferay.portal.kernel.repository.model.Folder copyFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long sourceFolderId,
			long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "copyFolder",
				_copyFolderParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, sourceFolderId, parentFolderId, name,
				description, serviceContext);

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

	public static void deleteFileEntry(
			HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFileEntry",
				_deleteFileEntryParameterTypes17);

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

	public static void deleteFileEntryByExternalReferenceCode(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"deleteFileEntryByExternalReferenceCode",
				_deleteFileEntryByExternalReferenceCodeParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

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

	public static void deleteFileEntryByTitle(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFileEntryByTitle",
				_deleteFileEntryByTitleParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, title);

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

	public static void deleteFileShortcut(
			HttpPrincipal httpPrincipal, long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFileShortcut",
				_deleteFileShortcutParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileShortcutId);

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

	public static void deleteFileShortcutByExternalReferenceCode(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"deleteFileShortcutByExternalReferenceCode",
				_deleteFileShortcutByExternalReferenceCodeParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

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

	public static void deleteFileVersion(
			HttpPrincipal httpPrincipal, long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFileVersion",
				_deleteFileVersionParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileVersionId);

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

	public static void deleteFileVersion(
			HttpPrincipal httpPrincipal, long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFileVersion",
				_deleteFileVersionParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, version);

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

	public static void deleteFolder(HttpPrincipal httpPrincipal, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFolder",
				_deleteFolderParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, folderId);

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

	public static void deleteFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteFolder",
				_deleteFolderParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, name);

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

	public static void deleteTempFileEntry(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			String folderName, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "deleteTempFileEntry",
				_deleteTempFileEntryParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, folderName, fileName);

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
			fetchFileEntry(HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "fetchFileEntry",
				_fetchFileEntryParameterTypes27);

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
			fetchFileEntry(
				HttpPrincipal httpPrincipal, long groupId, long folderId,
				String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "fetchFileEntry",
				_fetchFileEntryParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, title);

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
			fetchFileEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "fetchFileEntryByExternalReferenceCode",
				_fetchFileEntryByExternalReferenceCodeParameterTypes29);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			fetchFileEntryByFileName(
				HttpPrincipal httpPrincipal, long groupId, long folderId,
				String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "fetchFileEntryByFileName",
				_fetchFileEntryByFileNameParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, fileName);

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
			fetchFileEntryByUuidAndGroupId(
				HttpPrincipal httpPrincipal, String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "fetchFileEntryByUuidAndGroupId",
				_fetchFileEntryByUuidAndGroupIdParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId);

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

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, start, end);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.FileEntry>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, start, end,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				long fileEntryTypeId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, fileEntryTypeId);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				long fileEntryTypeId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, fileEntryTypeId, start, end);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				long fileEntryTypeId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.FileEntry>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, fileEntryTypeId, start, end,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				String[] mimeTypes)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, mimeTypes);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				String[] mimeTypes, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.FileEntry>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntries",
				_getFileEntriesParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, mimeTypes, start, end,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object> getFileEntriesAndFileShortcuts(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntriesAndFileShortcuts",
				_getFileEntriesAndFileShortcutsParameterTypes40);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, start, end);

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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFileEntriesAndFileShortcutsCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntriesAndFileShortcutsCount",
				_getFileEntriesAndFileShortcutsCountParameterTypes41);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status);

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

	public static int getFileEntriesAndFileShortcutsCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			int status, String[] mimeTypes)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntriesAndFileShortcutsCount",
				_getFileEntriesAndFileShortcutsCountParameterTypes42);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, mimeTypes);

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

	public static int getFileEntriesCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntriesCount",
				_getFileEntriesCountParameterTypes43);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId);

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

	public static int getFileEntriesCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntriesCount",
				_getFileEntriesCountParameterTypes44);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, fileEntryTypeId);

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

	public static int getFileEntriesCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			String[] mimeTypes)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntriesCount",
				_getFileEntriesCountParameterTypes45);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, mimeTypes);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntry(HttpPrincipal httpPrincipal, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntry",
				_getFileEntryParameterTypes46);

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
			getFileEntry(
				HttpPrincipal httpPrincipal, long groupId, long folderId,
				String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntry",
				_getFileEntryParameterTypes47);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, title);

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
			getFileEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntryByExternalReferenceCode",
				_getFileEntryByExternalReferenceCodeParameterTypes48);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntryByFileName(
				HttpPrincipal httpPrincipal, long groupId, long folderId,
				String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntryByFileName",
				_getFileEntryByFileNameParameterTypes49);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, fileName);

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
			getFileEntryByUuidAndGroupId(
				HttpPrincipal httpPrincipal, String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileEntryByUuidAndGroupId",
				_getFileEntryByUuidAndGroupIdParameterTypes50);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId);

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

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			getFileShortcut(HttpPrincipal httpPrincipal, long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileShortcut",
				_getFileShortcutParameterTypes51);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileShortcutId);

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

			return (com.liferay.portal.kernel.repository.model.FileShortcut)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			getFileShortcutByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFileShortcutByExternalReferenceCode",
				_getFileShortcutByExternalReferenceCodeParameterTypes52);

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

			return (com.liferay.portal.kernel.repository.model.FileShortcut)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileVersion
			getFileVersion(HttpPrincipal httpPrincipal, long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFileVersion",
				_getFileVersionParameterTypes53);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileVersionId);

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

			return (com.liferay.portal.kernel.repository.model.FileVersion)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.Folder getFolder(
			HttpPrincipal httpPrincipal, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolder",
				_getFolderParameterTypes54);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, folderId);

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

	public static com.liferay.portal.kernel.repository.model.Folder getFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolder",
				_getFolderParameterTypes55);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, name);

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

	public static com.liferay.portal.kernel.repository.model.Folder
			getFolderByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolderByExternalReferenceCode",
				_getFolderByExternalReferenceCodeParameterTypes56);

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

			return (com.liferay.portal.kernel.repository.model.Folder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes57);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, boolean includeMountFolders)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes58);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, includeMountFolders);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, boolean includeMountFolders, int start,
				int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes59);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, includeMountFolders,
				start, end);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, boolean includeMountFolders, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes60);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, includeMountFolders,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, int status, boolean includeMountFolders,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes61);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, status,
				includeMountFolders, start, end, orderByComparator);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes62);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, start, end);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFolders",
				_getFoldersParameterTypes63);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, start, end,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				int status, boolean includeMountFolders, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcuts",
				_getFoldersAndFileEntriesAndFileShortcutsParameterTypes64);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, includeMountFolders,
				start, end);

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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				int status, boolean includeMountFolders, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcuts",
				_getFoldersAndFileEntriesAndFileShortcutsParameterTypes65);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, includeMountFolders,
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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				int status, String[] mimeTypes, boolean includeMountFolders,
				boolean includeOwner, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcuts",
				_getFoldersAndFileEntriesAndFileShortcutsParameterTypes66);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, mimeTypes,
				includeMountFolders, includeOwner, start, end,
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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				HttpPrincipal httpPrincipal, long repositoryId, long folderId,
				int status, String[] mimeTypes, boolean includeMountFolders,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcuts",
				_getFoldersAndFileEntriesAndFileShortcutsParameterTypes67);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, mimeTypes,
				includeMountFolders, start, end, orderByComparator);

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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			int status, boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcutsCount",
				_getFoldersAndFileEntriesAndFileShortcutsCountParameterTypes68);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, includeMountFolders);

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

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			int status, String[] mimeTypes, boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcutsCount",
				_getFoldersAndFileEntriesAndFileShortcutsCountParameterTypes69);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, mimeTypes,
				includeMountFolders);

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

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			int status, String[] mimeTypes, boolean includeMountFolders,
			boolean includeOwner)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class,
				"getFoldersAndFileEntriesAndFileShortcutsCount",
				_getFoldersAndFileEntriesAndFileShortcutsCountParameterTypes70);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, status, mimeTypes,
				includeMountFolders, includeOwner);

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

	public static int getFoldersCount(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFoldersCount",
				_getFoldersCountParameterTypes71);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId);

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

	public static int getFoldersCount(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId,
			boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFoldersCount",
				_getFoldersCountParameterTypes72);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, includeMountFolders);

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

	public static int getFoldersCount(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId,
			int status, boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFoldersCount",
				_getFoldersCountParameterTypes73);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, status,
				includeMountFolders);

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

	public static int getFoldersFileEntriesCount(
			HttpPrincipal httpPrincipal, long repositoryId,
			java.util.List<Long> folderIds, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getFoldersFileEntriesCount",
				_getFoldersFileEntriesCountParameterTypes74);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderIds, status);

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

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					HttpPrincipal httpPrincipal, long groupId, long userId,
					int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntries",
				_getGroupFileEntriesParameterTypes75);

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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					HttpPrincipal httpPrincipal, long groupId, long userId,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.kernel.repository.model.FileEntry>
							orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntries",
				_getGroupFileEntriesParameterTypes76);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, start, end, orderByComparator);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					HttpPrincipal httpPrincipal, long groupId, long userId,
					long rootFolderId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntries",
				_getGroupFileEntriesParameterTypes77);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, start, end);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					HttpPrincipal httpPrincipal, long groupId, long userId,
					long rootFolderId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.kernel.repository.model.FileEntry>
							orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntries",
				_getGroupFileEntriesParameterTypes78);

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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					HttpPrincipal httpPrincipal, long groupId, long userId,
					long rootFolderId, String[] mimeTypes, int status,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.kernel.repository.model.FileEntry>
							orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntries",
				_getGroupFileEntriesParameterTypes79);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, mimeTypes, status,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getGroupFileEntriesCount(
			HttpPrincipal httpPrincipal, long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntriesCount",
				_getGroupFileEntriesCountParameterTypes80);

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

	public static int getGroupFileEntriesCount(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long rootFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntriesCount",
				_getGroupFileEntriesCountParameterTypes81);

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

	public static int getGroupFileEntriesCount(
			HttpPrincipal httpPrincipal, long groupId, long userId,
			long rootFolderId, String[] mimeTypes, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileEntriesCount",
				_getGroupFileEntriesCountParameterTypes82);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, userId, rootFolderId, mimeTypes, status);

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

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileShortcut>
				getGroupFileShortcuts(HttpPrincipal httpPrincipal, long groupId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getGroupFileShortcuts",
				_getGroupFileShortcutsParameterTypes83);

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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.FileShortcut>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getMountFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getMountFolders",
				_getMountFoldersParameterTypes84);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getMountFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getMountFolders",
				_getMountFoldersParameterTypes85);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, start, end);

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
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getMountFolders(
				HttpPrincipal httpPrincipal, long repositoryId,
				long parentFolderId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getMountFolders",
				_getMountFoldersParameterTypes86);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, start, end,
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

			return (java.util.List
				<com.liferay.portal.kernel.repository.model.Folder>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getMountFoldersCount(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getMountFoldersCount",
				_getMountFoldersCountParameterTypes87);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId);

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

	public static void getSubfolderIds(
			HttpPrincipal httpPrincipal, long repositoryId,
			java.util.List<Long> folderIds, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getSubfolderIds",
				_getSubfolderIdsParameterTypes88);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderIds, folderId);

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

	public static java.util.List<Long> getSubfolderIds(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getSubfolderIds",
				_getSubfolderIdsParameterTypes89);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId);

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

			return (java.util.List<Long>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Long> getSubfolderIds(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			boolean recurse)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getSubfolderIds",
				_getSubfolderIdsParameterTypes90);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, recurse);

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

			return (java.util.List<Long>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String[] getTempFileNames(
			HttpPrincipal httpPrincipal, long groupId, long folderId,
			String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "getTempFileNames",
				_getTempFileNamesParameterTypes91);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId, folderName);

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

	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "lockFolder",
				_lockFolderParameterTypes92);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId);

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

	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			String owner, boolean inheritable, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "lockFolder",
				_lockFolderParameterTypes93);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, owner, inheritable,
				expirationTime);

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

	public static com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId, long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "moveFileEntry",
				_moveFileEntryParameterTypes94);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, newFolderId, serviceContext);

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

	public static com.liferay.portal.kernel.repository.model.Folder moveFolder(
			HttpPrincipal httpPrincipal, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "moveFolder",
				_moveFolderParameterTypes95);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, folderId, parentFolderId, serviceContext);

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

	public static com.liferay.portal.kernel.lock.Lock refreshFileEntryLock(
			HttpPrincipal httpPrincipal, String lockUuid, long companyId,
			long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "refreshFileEntryLock",
				_refreshFileEntryLockParameterTypes96);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, lockUuid, companyId, expirationTime);

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

	public static com.liferay.portal.kernel.lock.Lock refreshFolderLock(
			HttpPrincipal httpPrincipal, String lockUuid, long companyId,
			long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "refreshFolderLock",
				_refreshFolderLockParameterTypes97);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, lockUuid, companyId, expirationTime);

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

	public static void revertFileEntry(
			HttpPrincipal httpPrincipal, long fileEntryId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "revertFileEntry",
				_revertFileEntryParameterTypes98);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, version, serviceContext);

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

	public static com.liferay.portal.kernel.search.Hits search(
			HttpPrincipal httpPrincipal, long repositoryId, long creatorUserId,
			int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "search", _searchParameterTypes99);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, creatorUserId, status, start, end);

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

			return (com.liferay.portal.kernel.search.Hits)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.Hits search(
			HttpPrincipal httpPrincipal, long repositoryId, long creatorUserId,
			long folderId, String[] mimeTypes, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "search", _searchParameterTypes100);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, creatorUserId, folderId, mimeTypes,
				status, start, end);

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

			return (com.liferay.portal.kernel.search.Hits)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.Hits search(
			HttpPrincipal httpPrincipal, long repositoryId,
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.search.SearchException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "search", _searchParameterTypes101);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, searchContext);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.search.SearchException) {

					throw (com.liferay.portal.kernel.search.SearchException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.search.Hits)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.Hits search(
			HttpPrincipal httpPrincipal, long repositoryId,
			com.liferay.portal.kernel.search.SearchContext searchContext,
			com.liferay.portal.kernel.search.Query query)
		throws com.liferay.portal.kernel.search.SearchException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "search", _searchParameterTypes102);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, searchContext, query);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.search.SearchException) {

					throw (com.liferay.portal.kernel.search.SearchException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.search.Hits)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void subscribeFileEntry(
			HttpPrincipal httpPrincipal, long groupId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "subscribeFileEntry",
				_subscribeFileEntryParameterTypes103);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fileEntryId);

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

	public static void subscribeFileEntryType(
			HttpPrincipal httpPrincipal, long groupId, long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "subscribeFileEntryType",
				_subscribeFileEntryTypeParameterTypes104);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fileEntryTypeId);

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

	public static void subscribeFolder(
			HttpPrincipal httpPrincipal, long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "subscribeFolder",
				_subscribeFolderParameterTypes105);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId);

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

	public static void unlockFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "unlockFolder",
				_unlockFolderParameterTypes106);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, lockUuid);

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

	public static void unlockFolder(
			HttpPrincipal httpPrincipal, long repositoryId, long parentFolderId,
			String name, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "unlockFolder",
				_unlockFolderParameterTypes107);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, parentFolderId, name, lockUuid);

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

	public static void unsubscribeFileEntry(
			HttpPrincipal httpPrincipal, long groupId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "unsubscribeFileEntry",
				_unsubscribeFileEntryParameterTypes108);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fileEntryId);

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

	public static void unsubscribeFileEntryType(
			HttpPrincipal httpPrincipal, long groupId, long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "unsubscribeFileEntryType",
				_unsubscribeFileEntryTypeParameterTypes109);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fileEntryTypeId);

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

	public static void unsubscribeFolder(
			HttpPrincipal httpPrincipal, long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "unsubscribeFolder",
				_unsubscribeFolderParameterTypes110);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderId);

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
			updateFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId,
				String sourceFileName, String mimeType, String title,
				String urlTitle, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				byte[] bytes, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFileEntry",
				_updateFileEntryParameterTypes111);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease,
				bytes, displayDate, expirationDate, reviewDate, serviceContext);

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
			updateFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId,
				String sourceFileName, String mimeType, String title,
				String urlTitle, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.File file, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFileEntry",
				_updateFileEntryParameterTypes112);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease, file,
				displayDate, expirationDate, reviewDate, serviceContext);

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
			updateFileEntry(
				HttpPrincipal httpPrincipal, long fileEntryId,
				String sourceFileName, String mimeType, String title,
				String urlTitle, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.InputStream inputStream, long size,
				java.util.Date displayDate, java.util.Date expirationDate,
				java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFileEntry",
				_updateFileEntryParameterTypes113);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease,
				inputStream, size, displayDate, expirationDate, reviewDate,
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
			updateFileEntryAndCheckIn(
				HttpPrincipal httpPrincipal, long fileEntryId,
				String sourceFileName, String mimeType, String title,
				String urlTitle, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.File file, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFileEntryAndCheckIn",
				_updateFileEntryAndCheckInParameterTypes114);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease, file,
				displayDate, expirationDate, reviewDate, serviceContext);

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
			updateFileEntryAndCheckIn(
				HttpPrincipal httpPrincipal, long fileEntryId,
				String sourceFileName, String mimeType, String title,
				String urlTitle, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.InputStream inputStream, long size,
				java.util.Date displayDate, java.util.Date expirationDate,
				java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFileEntryAndCheckIn",
				_updateFileEntryAndCheckInParameterTypes115);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease,
				inputStream, size, displayDate, expirationDate, reviewDate,
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

			return (com.liferay.portal.kernel.repository.model.FileEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			updateFileShortcut(
				HttpPrincipal httpPrincipal, long fileShortcutId, long folderId,
				long toFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFileShortcut",
				_updateFileShortcutParameterTypes116);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fileShortcutId, folderId, toFileEntryId,
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

			return (com.liferay.portal.kernel.repository.model.FileShortcut)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.Folder
			updateFolder(
				HttpPrincipal httpPrincipal, long folderId, String name,
				String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "updateFolder",
				_updateFolderParameterTypes117);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, folderId, name, description, serviceContext);

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

	public static boolean verifyFileEntryCheckOut(
			HttpPrincipal httpPrincipal, long repositoryId, long fileEntryId,
			String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "verifyFileEntryCheckOut",
				_verifyFileEntryCheckOutParameterTypes118);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, fileEntryId, lockUuid);

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

	public static boolean verifyFileEntryLock(
			HttpPrincipal httpPrincipal, long repositoryId, long fileEntryId,
			String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "verifyFileEntryLock",
				_verifyFileEntryLockParameterTypes119);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, fileEntryId, lockUuid);

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

	public static boolean verifyInheritableLock(
			HttpPrincipal httpPrincipal, long repositoryId, long folderId,
			String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DLAppServiceUtil.class, "verifyInheritableLock",
				_verifyInheritableLockParameterTypes120);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, repositoryId, folderId, lockUuid);

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

	private static Log _log = LogFactoryUtil.getLog(DLAppServiceHttp.class);

	private static final Class<?>[] _addFileEntryParameterTypes0 = new Class[] {
		long.class, long.class, String.class, String.class, String.class,
		String.class, String.class, java.io.File.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addFileEntryParameterTypes1 = new Class[] {
		String.class, long.class, long.class, String.class, String.class,
		String.class, String.class, String.class, String.class, byte[].class,
		java.util.Date.class, java.util.Date.class, java.util.Date.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addFileEntryParameterTypes2 = new Class[] {
		String.class, long.class, long.class, String.class, String.class,
		String.class, String.class, String.class, String.class,
		java.io.File.class, java.util.Date.class, java.util.Date.class,
		java.util.Date.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addFileEntryParameterTypes3 = new Class[] {
		String.class, long.class, long.class, String.class, String.class,
		String.class, String.class, String.class, String.class,
		java.io.InputStream.class, long.class, java.util.Date.class,
		java.util.Date.class, java.util.Date.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addFileShortcutParameterTypes4 =
		new Class[] {
			String.class, long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addFolderParameterTypes5 = new Class[] {
		String.class, long.class, long.class, String.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addTempFileEntryParameterTypes6 =
		new Class[] {
			long.class, long.class, String.class, String.class,
			java.io.File.class, String.class
		};
	private static final Class<?>[] _addTempFileEntryParameterTypes7 =
		new Class[] {
			long.class, long.class, String.class, String.class,
			java.io.InputStream.class, String.class
		};
	private static final Class<?>[] _cancelCheckOutParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[] _checkInFileEntryParameterTypes9 =
		new Class[] {
			long.class,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease.
				class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _checkInFileEntryParameterTypes10 =
		new Class[] {
			long.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _checkOutFileEntryParameterTypes11 =
		new Class[] {
			long.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _checkOutFileEntryParameterTypes12 =
		new Class[] {
			long.class, String.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyFileEntryParameterTypes13 =
		new Class[] {
			long.class, long.class, long.class, long.class, long[].class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyFileShortcutParameterTypes14 =
		new Class[] {
			long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyFolderParameterTypes15 = new Class[] {
		long.class, long.class, long.class, long.class, java.util.Map.class,
		long[].class, com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _copyFolderParameterTypes16 = new Class[] {
		long.class, long.class, long.class, String.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteFileEntryParameterTypes17 =
		new Class[] {long.class};
	private static final Class<?>[]
		_deleteFileEntryByExternalReferenceCodeParameterTypes18 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _deleteFileEntryByTitleParameterTypes19 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _deleteFileShortcutParameterTypes20 =
		new Class[] {long.class};
	private static final Class<?>[]
		_deleteFileShortcutByExternalReferenceCodeParameterTypes21 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _deleteFileVersionParameterTypes22 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteFileVersionParameterTypes23 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _deleteFolderParameterTypes24 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteFolderParameterTypes25 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _deleteTempFileEntryParameterTypes26 =
		new Class[] {long.class, long.class, String.class, String.class};
	private static final Class<?>[] _fetchFileEntryParameterTypes27 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchFileEntryParameterTypes28 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[]
		_fetchFileEntryByExternalReferenceCodeParameterTypes29 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _fetchFileEntryByFileNameParameterTypes30 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[]
		_fetchFileEntryByUuidAndGroupIdParameterTypes31 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getFileEntriesParameterTypes32 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getFileEntriesParameterTypes33 =
		new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getFileEntriesParameterTypes34 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFileEntriesParameterTypes35 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getFileEntriesParameterTypes36 =
		new Class[] {long.class, long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getFileEntriesParameterTypes37 =
		new Class[] {
			long.class, long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFileEntriesParameterTypes38 =
		new Class[] {long.class, long.class, String[].class};
	private static final Class<?>[] _getFileEntriesParameterTypes39 =
		new Class[] {
			long.class, long.class, String[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getFileEntriesAndFileShortcutsParameterTypes40 = new Class[] {
			long.class, long.class, int.class, int.class, int.class
		};
	private static final Class<?>[]
		_getFileEntriesAndFileShortcutsCountParameterTypes41 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getFileEntriesAndFileShortcutsCountParameterTypes42 = new Class[] {
			long.class, long.class, int.class, String[].class
		};
	private static final Class<?>[] _getFileEntriesCountParameterTypes43 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getFileEntriesCountParameterTypes44 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getFileEntriesCountParameterTypes45 =
		new Class[] {long.class, long.class, String[].class};
	private static final Class<?>[] _getFileEntryParameterTypes46 =
		new Class[] {long.class};
	private static final Class<?>[] _getFileEntryParameterTypes47 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[]
		_getFileEntryByExternalReferenceCodeParameterTypes48 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getFileEntryByFileNameParameterTypes49 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[]
		_getFileEntryByUuidAndGroupIdParameterTypes50 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getFileShortcutParameterTypes51 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getFileShortcutByExternalReferenceCodeParameterTypes52 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getFileVersionParameterTypes53 =
		new Class[] {long.class};
	private static final Class<?>[] _getFolderParameterTypes54 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getFolderParameterTypes55 = new Class[] {
		long.class, long.class, String.class
	};
	private static final Class<?>[]
		_getFolderByExternalReferenceCodeParameterTypes56 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getFoldersParameterTypes57 = new Class[] {
		long.class, long.class
	};
	private static final Class<?>[] _getFoldersParameterTypes58 = new Class[] {
		long.class, long.class, boolean.class
	};
	private static final Class<?>[] _getFoldersParameterTypes59 = new Class[] {
		long.class, long.class, boolean.class, int.class, int.class
	};
	private static final Class<?>[] _getFoldersParameterTypes60 = new Class[] {
		long.class, long.class, boolean.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getFoldersParameterTypes61 = new Class[] {
		long.class, long.class, int.class, boolean.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getFoldersParameterTypes62 = new Class[] {
		long.class, long.class, int.class, int.class
	};
	private static final Class<?>[] _getFoldersParameterTypes63 = new Class[] {
		long.class, long.class, int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsParameterTypes64 =
			new Class[] {
				long.class, long.class, int.class, boolean.class, int.class,
				int.class
			};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsParameterTypes65 =
			new Class[] {
				long.class, long.class, int.class, boolean.class, int.class,
				int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsParameterTypes66 =
			new Class[] {
				long.class, long.class, int.class, String[].class,
				boolean.class, boolean.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsParameterTypes67 =
			new Class[] {
				long.class, long.class, int.class, String[].class,
				boolean.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsCountParameterTypes68 =
			new Class[] {long.class, long.class, int.class, boolean.class};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsCountParameterTypes69 =
			new Class[] {
				long.class, long.class, int.class, String[].class, boolean.class
			};
	private static final Class<?>[]
		_getFoldersAndFileEntriesAndFileShortcutsCountParameterTypes70 =
			new Class[] {
				long.class, long.class, int.class, String[].class,
				boolean.class, boolean.class
			};
	private static final Class<?>[] _getFoldersCountParameterTypes71 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getFoldersCountParameterTypes72 =
		new Class[] {long.class, long.class, boolean.class};
	private static final Class<?>[] _getFoldersCountParameterTypes73 =
		new Class[] {long.class, long.class, int.class, boolean.class};
	private static final Class<?>[]
		_getFoldersFileEntriesCountParameterTypes74 = new Class[] {
			long.class, java.util.List.class, int.class
		};
	private static final Class<?>[] _getGroupFileEntriesParameterTypes75 =
		new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getGroupFileEntriesParameterTypes76 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupFileEntriesParameterTypes77 =
		new Class[] {long.class, long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getGroupFileEntriesParameterTypes78 =
		new Class[] {
			long.class, long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupFileEntriesParameterTypes79 =
		new Class[] {
			long.class, long.class, long.class, String[].class, int.class,
			int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getGroupFileEntriesCountParameterTypes80 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getGroupFileEntriesCountParameterTypes81 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getGroupFileEntriesCountParameterTypes82 =
		new Class[] {
			long.class, long.class, long.class, String[].class, int.class
		};
	private static final Class<?>[] _getGroupFileShortcutsParameterTypes83 =
		new Class[] {long.class};
	private static final Class<?>[] _getMountFoldersParameterTypes84 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getMountFoldersParameterTypes85 =
		new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getMountFoldersParameterTypes86 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getMountFoldersCountParameterTypes87 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getSubfolderIdsParameterTypes88 =
		new Class[] {long.class, java.util.List.class, long.class};
	private static final Class<?>[] _getSubfolderIdsParameterTypes89 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getSubfolderIdsParameterTypes90 =
		new Class[] {long.class, long.class, boolean.class};
	private static final Class<?>[] _getTempFileNamesParameterTypes91 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _lockFolderParameterTypes92 = new Class[] {
		long.class, long.class
	};
	private static final Class<?>[] _lockFolderParameterTypes93 = new Class[] {
		long.class, long.class, String.class, boolean.class, long.class
	};
	private static final Class<?>[] _moveFileEntryParameterTypes94 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _moveFolderParameterTypes95 = new Class[] {
		long.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _refreshFileEntryLockParameterTypes96 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _refreshFolderLockParameterTypes97 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _revertFileEntryParameterTypes98 =
		new Class[] {
			long.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _searchParameterTypes99 = new Class[] {
		long.class, long.class, int.class, int.class, int.class
	};
	private static final Class<?>[] _searchParameterTypes100 = new Class[] {
		long.class, long.class, long.class, String[].class, int.class,
		int.class, int.class
	};
	private static final Class<?>[] _searchParameterTypes101 = new Class[] {
		long.class, com.liferay.portal.kernel.search.SearchContext.class
	};
	private static final Class<?>[] _searchParameterTypes102 = new Class[] {
		long.class, com.liferay.portal.kernel.search.SearchContext.class,
		com.liferay.portal.kernel.search.Query.class
	};
	private static final Class<?>[] _subscribeFileEntryParameterTypes103 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _subscribeFileEntryTypeParameterTypes104 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _subscribeFolderParameterTypes105 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _unlockFolderParameterTypes106 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _unlockFolderParameterTypes107 =
		new Class[] {long.class, long.class, String.class, String.class};
	private static final Class<?>[] _unsubscribeFileEntryParameterTypes108 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _unsubscribeFileEntryTypeParameterTypes109 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _unsubscribeFolderParameterTypes110 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _updateFileEntryParameterTypes111 =
		new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			String.class, String.class,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease.
				class,
			byte[].class, java.util.Date.class, java.util.Date.class,
			java.util.Date.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateFileEntryParameterTypes112 =
		new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			String.class, String.class,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease.
				class,
			java.io.File.class, java.util.Date.class, java.util.Date.class,
			java.util.Date.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateFileEntryParameterTypes113 =
		new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			String.class, String.class,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease.
				class,
			java.io.InputStream.class, long.class, java.util.Date.class,
			java.util.Date.class, java.util.Date.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateFileEntryAndCheckInParameterTypes114 = new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			String.class, String.class,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease.
				class,
			java.io.File.class, java.util.Date.class, java.util.Date.class,
			java.util.Date.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateFileEntryAndCheckInParameterTypes115 = new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			String.class, String.class,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease.
				class,
			java.io.InputStream.class, long.class, java.util.Date.class,
			java.util.Date.class, java.util.Date.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateFileShortcutParameterTypes116 =
		new Class[] {
			long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateFolderParameterTypes117 =
		new Class[] {
			long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _verifyFileEntryCheckOutParameterTypes118 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _verifyFileEntryLockParameterTypes119 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _verifyInheritableLockParameterTypes120 =
		new Class[] {long.class, long.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:1417818601