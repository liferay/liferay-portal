/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.store.db;

import com.liferay.document.library.kernel.exception.DuplicateFileException;
import com.liferay.document.library.kernel.exception.NoSuchContentException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.model.DLContent;
import com.liferay.document.library.kernel.model.DLContentDataBlobModel;
import com.liferay.document.library.kernel.service.DLContentLocalService;
import com.liferay.document.library.kernel.store.BaseStore;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.AutoDeleteFileInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionConfig.Factory;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.nio.channels.FileChannel;

import java.sql.Blob;

import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 * @author Tina Tian
 */
@Component(
	immediate = true,
	property = "store.type=com.liferay.portal.store.db.DBStore",
	service = Store.class
)
public class DBStore extends BaseStore {

	@Override
	public void addDirectory(
		long companyId, long repositoryId, String dirName) {
	}

	@Override
	public void addFile(
			long companyId, long repositoryId, String fileName, byte[] bytes)
		throws DuplicateFileException {

		updateFile(
			companyId, repositoryId, fileName, Store.VERSION_DEFAULT, bytes);
	}

	@Override
	public void addFile(
			long companyId, long repositoryId, String fileName, File file)
		throws DuplicateFileException {

		updateFile(
			companyId, repositoryId, fileName, Store.VERSION_DEFAULT, file);
	}

	@Override
	public void addFile(
			long companyId, long repositoryId, String fileName,
			InputStream inputStream)
		throws DuplicateFileException {

		updateFile(
			companyId, repositoryId, fileName, Store.VERSION_DEFAULT,
			inputStream);
	}

	@Override
	public void checkRoot(long companyId) {
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		_dlContentLocalService.deleteContentsByDirectory(
			companyId, repositoryId, dirName);
	}

	@Override
	public void deleteFile(long companyId, long repositoryId, String fileName) {
		_dlContentLocalService.deleteContents(
			companyId, repositoryId, fileName);
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			_dlContentLocalService.deleteContent(
				companyId, repositoryId, fileName, versionLabel);
		}
		catch (PortalException pe) {
			logFailedDeletion(
				companyId, repositoryId, fileName, versionLabel, pe);
		}
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName)
		throws NoSuchFileException {

		try {
			DLContent dlContent = _dlContentLocalService.getContent(
				companyId, repositoryId, fileName);

			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				new GetBlobDataCallable(dlContent.getContentId()));
		}
		catch (Throwable t) {
			throw new NoSuchFileException(companyId, repositoryId, fileName, t);
		}
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws NoSuchFileException {

		try {
			DLContent dlContent = _dlContentLocalService.getContent(
				companyId, repositoryId, fileName, versionLabel);

			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				new GetBlobDataCallable(dlContent.getContentId()));
		}
		catch (Throwable t) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, versionLabel, t);
		}
	}

	@Override
	public String[] getFileNames(long companyId, long repositoryId) {
		List<DLContent> dlContents = _dlContentLocalService.getContents(
			companyId, repositoryId);

		String[] fileNames = new String[dlContents.size()];

		for (int i = 0; i < dlContents.size(); i++) {
			DLContent dlContent = dlContents.get(i);

			fileNames[i] = dlContent.getPath();
		}

		return fileNames;
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		List<DLContent> dlContents =
			_dlContentLocalService.getContentsByDirectory(
				companyId, repositoryId, dirName);

		String[] fileNames = new String[dlContents.size()];

		for (int i = 0; i < dlContents.size(); i++) {
			DLContent dlContent = dlContents.get(i);

			fileNames[i] = dlContent.getPath();
		}

		return fileNames;
	}

	@Override
	public long getFileSize(long companyId, long repositoryId, String fileName)
		throws NoSuchFileException {

		DLContent dlContent = null;

		try {
			dlContent = _dlContentLocalService.getContent(
				companyId, repositoryId, fileName);
		}
		catch (NoSuchContentException nsce) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, nsce);
		}

		return dlContent.getSize();
	}

	@Override
	public boolean hasDirectory(
		long companyId, long repositoryId, String dirName) {

		return true;
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		return _dlContentLocalService.hasContent(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, long newRepositoryId,
			String fileName)
		throws DuplicateFileException, NoSuchFileException {

		if (repositoryId == newRepositoryId) {
			throw new DuplicateFileException(
				companyId, newRepositoryId, fileName);
		}

		if (!hasFile(companyId, repositoryId, fileName)) {
			throw new NoSuchFileException(companyId, repositoryId, fileName);
		}

		if (hasFile(companyId, newRepositoryId, fileName)) {
			throw new DuplicateFileException(
				companyId, newRepositoryId, fileName);
		}

		_dlContentLocalService.updateDLContent(
			companyId, repositoryId, newRepositoryId, fileName, fileName);
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, String fileName,
			String newFileName)
		throws DuplicateFileException, NoSuchFileException {

		if (fileName.equals(newFileName)) {
			throw new DuplicateFileException(
				companyId, repositoryId, newFileName);
		}

		if (!hasFile(companyId, repositoryId, fileName)) {
			throw new NoSuchFileException(companyId, repositoryId, fileName);
		}

		if (hasFile(companyId, repositoryId, newFileName)) {
			throw new DuplicateFileException(
				companyId, repositoryId, newFileName);
		}

		_dlContentLocalService.updateDLContent(
			companyId, repositoryId, repositoryId, fileName, newFileName);
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel, byte[] bytes)
		throws DuplicateFileException {

		if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
			throw new DuplicateFileException(
				companyId, repositoryId, fileName, versionLabel);
		}

		_dlContentLocalService.addContent(
			companyId, repositoryId, fileName, versionLabel, bytes);
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel, File file)
		throws DuplicateFileException {

		if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
			throw new DuplicateFileException(
				companyId, repositoryId, fileName, versionLabel);
		}

		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
		}
		catch (FileNotFoundException fnfe) {
			throw new SystemException(fnfe);
		}

		_dlContentLocalService.addContent(
			companyId, repositoryId, fileName, versionLabel, inputStream,
			file.length());
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel, InputStream inputStream)
		throws DuplicateFileException {

		if (_dlContentLocalService.hasContent(
				companyId, repositoryId, fileName, versionLabel)) {

			throw new DuplicateFileException(
				companyId, repositoryId, fileName, versionLabel);
		}

		long length = -1;

		if (inputStream instanceof ByteArrayInputStream) {
			ByteArrayInputStream byteArrayInputStream =
				(ByteArrayInputStream)inputStream;

			length = byteArrayInputStream.available();
		}
		else if (inputStream instanceof FileInputStream) {
			FileInputStream fileInputStream = (FileInputStream)inputStream;

			FileChannel fileChannel = fileInputStream.getChannel();

			try {
				length = fileChannel.size();
			}
			catch (IOException ioe) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to detect file size from file channel", ioe);
				}
			}
		}
		else if (inputStream instanceof UnsyncByteArrayInputStream) {
			UnsyncByteArrayInputStream unsyncByteArrayInputStream =
				(UnsyncByteArrayInputStream)inputStream;

			length = unsyncByteArrayInputStream.available();
		}

		if (length >= 0) {
			_dlContentLocalService.addContent(
				companyId, repositoryId, fileName, versionLabel, inputStream,
				length);
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to detect length from input stream. Reading " +
						"entire input stream into memory as a last resort.");
			}

			byte[] bytes = null;

			try {
				bytes = FileUtil.getBytes(inputStream);
			}
			catch (IOException ioe) {
				throw new SystemException(ioe);
			}

			_dlContentLocalService.addContent(
				companyId, repositoryId, fileName, versionLabel, bytes);
		}
	}

	@Activate
	protected void activate() {
		DB db = DBManagerUtil.getDB();

		if ((db.getDBType() == DBType.DB2) ||
			(db.getDBType() == DBType.MYSQL) ||
			(db.getDBType() == DBType.MARIADB) ||
			(db.getDBType() == DBType.ORACLE) ||
			(db.getDBType() == DBType.SYBASE)) {

			_transactionConfig = Factory.create(
				Propagation.SUPPORTS,
				new Class<?>[] {PortalException.class, SystemException.class});
		}
		else {
			_transactionConfig = Factory.create(
				Propagation.REQUIRED,
				new Class<?>[] {PortalException.class, SystemException.class});
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(DBStore.class);

	@Reference
	private DLContentLocalService _dlContentLocalService;

	private TransactionConfig _transactionConfig;

	private class GetBlobDataCallable implements Callable<InputStream> {

		@Override
		public InputStream call() throws Exception {
			DLContentDataBlobModel dlContentDataBlobModel =
				_dlContentLocalService.getDataBlobModel(_contentId);

			Blob blob = dlContentDataBlobModel.getDataBlob();

			InputStream inputStream = blob.getBinaryStream();

			if (_transactionConfig.getPropagation() == Propagation.REQUIRED) {
				inputStream = new AutoDeleteFileInputStream(
					FileUtil.createTempFile(inputStream));
			}

			return inputStream;
		}

		private GetBlobDataCallable(long contentId) {
			_contentId = contentId;
		}

		private final long _contentId;

	}

}