/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.soap.repository;

import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.repository.external.CredentialsProvider;
import com.liferay.document.library.repository.external.ExtRepository;
import com.liferay.document.library.repository.external.ExtRepositoryAdapter;
import com.liferay.document.library.repository.external.ExtRepositoryFileEntry;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersion;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersionDescriptor;
import com.liferay.document.library.repository.external.ExtRepositoryFolder;
import com.liferay.document.library.repository.external.ExtRepositoryObject;
import com.liferay.document.library.repository.external.ExtRepositoryObjectType;
import com.liferay.document.library.repository.external.ExtRepositorySearchResult;
import com.liferay.document.library.repository.external.cache.ConnectionBuilder;
import com.liferay.document.library.repository.external.cache.ConnectionCache;
import com.liferay.document.library.repository.external.search.ExtRepositoryQueryMapper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sharepoint.soap.repository.connector.SharepointConnection;
import com.liferay.sharepoint.soap.repository.connector.SharepointException;
import com.liferay.sharepoint.soap.repository.connector.SharepointObject;
import com.liferay.sharepoint.soap.repository.connector.SharepointRuntimeException;
import com.liferay.sharepoint.soap.repository.connector.SharepointVersion;
import com.liferay.sharepoint.soap.repository.connector.operation.PathUtil;
import com.liferay.sharepoint.soap.repository.connector.operation.URLUtil;
import com.liferay.sharepoint.soap.repository.model.SharepointWSFileEntry;
import com.liferay.sharepoint.soap.repository.model.SharepointWSFileVersion;
import com.liferay.sharepoint.soap.repository.model.SharepointWSFolder;
import com.liferay.sharepoint.soap.repository.model.SharepointWSObject;
import com.liferay.sharepoint.soap.repository.search.SharepointQueryBuilder;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class SharepointWSRepository
	extends ExtRepositoryAdapter
	implements ConnectionBuilder<SharepointConnection>, ExtRepository {

	public SharepointWSRepository() {
		super(null);
	}

	@Override
	public ExtRepositoryFileEntry addExtRepositoryFileEntry(
			String extRepositoryParentFolderKey, String fileName,
			String mimeType, String title, String description, String changeLog,
			InputStream inputStream)
		throws PortalException {

		String filePath = null;

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject parentFolderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryParentFolderKey));

			String parentFolderPath = parentFolderSharepointObject.getPath();

			filePath = PathUtil.buildPath(parentFolderPath, title);

			sharepointConnection.addFile(
				parentFolderPath, title, changeLog, inputStream);

			SharepointObject fileSharepointObject =
				sharepointConnection.getSharepointObject(filePath);

			return new SharepointWSFileEntry(fileSharepointObject);
		}
		catch (SharepointException sharepointException) {
			_processSharepointObjectException(false, filePath, title);

			throw new SystemException(sharepointException);
		}
		catch (SharepointRuntimeException sharepointRuntimeException) {
			throw new SystemException(sharepointRuntimeException);
		}
	}

	@Override
	public ExtRepositoryFolder addExtRepositoryFolder(
			String extRepositoryParentFolderKey, String name,
			String description)
		throws PortalException {

		String folderPath = null;

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject parentFolderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryParentFolderKey));

			String parentFolderPath = parentFolderSharepointObject.getPath();

			folderPath = PathUtil.buildPath(parentFolderPath, name);

			sharepointConnection.addFolder(parentFolderPath, name);

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(folderPath);

			return new SharepointWSFolder(folderSharepointObject);
		}
		catch (SharepointException sharepointException) {
			_processSharepointObjectException(true, folderPath, name);

			throw new SystemException(sharepointException);
		}
		catch (SharepointRuntimeException sharepointRuntimeException) {
			throw new SystemException(sharepointRuntimeException);
		}
	}

	@Override
	public SharepointConnection buildConnection() throws RepositoryException {
		try {
			return new SharepointConnection(
				_serverVersion, _protocol, _host, _port, _sitePath,
				_libraryName, _libraryPath, _credentialsProvider.getLogin(),
				_credentialsProvider.getPassword());
		}
		catch (SharepointRuntimeException sharepointRuntimeException) {
			throw new RepositoryException(
				"Unable to communicate with the Sharepoint server",
				sharepointRuntimeException);
		}
	}

	@Override
	public ExtRepositoryFileVersion cancelCheckOut(
		String extRepositoryFileEntryKey) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject fileSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFileEntryKey));

			String filePath = fileSharepointObject.getPath();

			sharepointConnection.cancelCheckOutFile(filePath);

			return null;
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public void checkInExtRepositoryFileEntry(
		String extRepositoryFileEntryKey, boolean createMajorVersion,
		String changeLog) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject fileSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFileEntryKey));

			String filePath = fileSharepointObject.getPath();

			SharepointConnection.CheckInType checkInType = null;

			if (createMajorVersion) {
				checkInType = SharepointConnection.CheckInType.MAJOR;
			}
			else {
				checkInType = SharepointConnection.CheckInType.MINOR;
			}

			sharepointConnection.checkInFile(filePath, changeLog, checkInType);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public ExtRepositoryFileEntry checkOutExtRepositoryFileEntry(
		String extRepositoryFileEntryKey) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject fileSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFileEntryKey));

			String filePath = fileSharepointObject.getPath();

			sharepointConnection.checkOutFile(filePath);

			return new SharepointWSFileEntry(fileSharepointObject);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public <T extends ExtRepositoryObject> T copyExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryFileEntryKey, String newExtRepositoryFolderKey,
			String newTitle)
		throws PortalException {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject fileSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFileEntryKey));

			String filePath = fileSharepointObject.getPath();

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(newExtRepositoryFolderKey));

			String folderPath = folderSharepointObject.getPath();

			String newFilePath = PathUtil.buildPath(folderPath, newTitle);

			sharepointConnection.copySharepointObject(filePath, newFilePath);

			SharepointObject newSharepointObject =
				sharepointConnection.getSharepointObject(newFilePath);

			return _toExtRepositoryObject(
				extRepositoryObjectType, newSharepointObject);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public void deleteExtRepositoryObject(
		ExtRepositoryObjectType<? extends ExtRepositoryObject>
			extRepositoryObjectType,
		String extRepositoryObjectKey) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject sharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryObjectKey));

			sharepointConnection.deleteSharepointObject(
				sharepointObject.getPath());
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public String getAuthType() {
		return CompanyConstants.AUTH_TYPE_SN;
	}

	@Override
	public InputStream getContentStream(
		ExtRepositoryFileEntry extRepositoryFileEntry) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointWSFileEntry sharepointWSFileEntry =
				(SharepointWSFileEntry)extRepositoryFileEntry;

			SharepointObject fileSharepointObject =
				sharepointWSFileEntry.getSharepointObject();

			return sharepointConnection.getInputStream(fileSharepointObject);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public InputStream getContentStream(
		ExtRepositoryFileVersion extRepositoryFileVersion) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointWSFileVersion sharepointWSFileVersion =
				(SharepointWSFileVersion)extRepositoryFileVersion;

			return sharepointConnection.getInputStream(
				sharepointWSFileVersion.getSharepointVersion());
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public ExtRepositoryFileVersion getExtRepositoryFileVersion(
		ExtRepositoryFileEntry extRepositoryFileEntry, String version) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointWSFileEntry sharepointWSFileEntry =
				(SharepointWSFileEntry)extRepositoryFileEntry;

			SharepointObject fileSharepointObject =
				sharepointWSFileEntry.getSharepointObject();

			String filePath = fileSharepointObject.getPath();

			List<SharepointVersion> sharepointVersions =
				sharepointConnection.getSharepointVersions(filePath);

			for (SharepointVersion sharepointVersion : sharepointVersions) {
				if (version.equals(sharepointVersion.getVersion())) {
					return new SharepointWSFileVersion(sharepointVersion);
				}
			}

			return null;
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public ExtRepositoryFileVersionDescriptor
		getExtRepositoryFileVersionDescriptor(
			String extRepositoryFileVersionKey) {

		String[] extRepositoryFileVersionKeyParts = StringUtil.split(
			extRepositoryFileVersionKey, StringPool.AT);

		String extRepositoryFileEntryKey = extRepositoryFileVersionKeyParts[0];
		String version = extRepositoryFileVersionKeyParts[1];

		return new ExtRepositoryFileVersionDescriptor(
			extRepositoryFileEntryKey, version);
	}

	@Override
	public List<ExtRepositoryFileVersion> getExtRepositoryFileVersions(
		ExtRepositoryFileEntry extRepositoryFileEntry) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointWSFileEntry sharepointWSFileEntry =
				(SharepointWSFileEntry)extRepositoryFileEntry;

			SharepointObject fileSharepointObject =
				sharepointWSFileEntry.getSharepointObject();

			String filePath = fileSharepointObject.getPath();

			return TransformUtil.transform(
				sharepointConnection.getSharepointVersions(filePath),
				sharepointVersion -> new SharepointWSFileVersion(
					sharepointVersion));
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public <T extends ExtRepositoryObject> T getExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryObjectKey)
		throws PortalException {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject sharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryObjectKey));

			if (sharepointObject == null) {
				if (extRepositoryObjectType == ExtRepositoryObjectType.FOLDER) {
					throw new NoSuchFolderException(extRepositoryObjectKey);
				}

				throw new NoSuchFileEntryException(extRepositoryObjectKey);
			}

			return _toExtRepositoryObject(
				extRepositoryObjectType, sharepointObject);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public <T extends ExtRepositoryObject> T getExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryFolderKey, String title)
		throws PortalException {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFolderKey));

			String folderPath = folderSharepointObject.getPath();

			SharepointConnection.ObjectTypeFilter objectTypeFilter =
				_toObjectTypeFilter(extRepositoryObjectType);

			List<SharepointObject> sharepointObjects =
				sharepointConnection.getSharepointObjects(
					folderPath, objectTypeFilter);

			for (SharepointObject sharepointObject : sharepointObjects) {
				if (title.equals(sharepointObject.getName())) {
					return _toExtRepositoryObject(
						extRepositoryObjectType, sharepointObject);
				}
			}

			if (extRepositoryObjectType == ExtRepositoryObjectType.FOLDER) {
				throw new NoSuchFolderException(title);
			}

			throw new NoSuchFileEntryException(title);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public <T extends ExtRepositoryObject> List<T> getExtRepositoryObjects(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryFolderKey)
		throws PortalException {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFolderKey));

			String folderPath = folderSharepointObject.getPath();

			SharepointConnection.ObjectTypeFilter objectTypeFilter =
				_toObjectTypeFilter(extRepositoryObjectType);

			return TransformUtil.transform(
				sharepointConnection.getSharepointObjects(
					folderPath, objectTypeFilter),
				sharepointObject -> _toExtRepositoryObject(
					extRepositoryObjectType, sharepointObject));
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public int getExtRepositoryObjectsCount(
		ExtRepositoryObjectType<? extends ExtRepositoryObject>
			extRepositoryObjectType,
		String extRepositoryFolderKey) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFolderKey));

			String folderPath = folderSharepointObject.getPath();

			SharepointConnection.ObjectTypeFilter objectTypeFilter =
				_toObjectTypeFilter(extRepositoryObjectType);

			return sharepointConnection.getSharepointObjectsCount(
				folderPath, objectTypeFilter);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public ExtRepositoryFolder getExtRepositoryParentFolder(
		ExtRepositoryObject extRepositoryObject) {

		try {
			SharepointWSObject sharepointWSObject =
				(SharepointWSObject)extRepositoryObject;

			SharepointObject sharepointObject =
				sharepointWSObject.getSharepointObject();

			String parentFolderPath = sharepointObject.getFolderPath();

			if (parentFolderPath == null) {
				return null;
			}

			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject parentFolderSharepointObject =
				sharepointConnection.getSharepointObject(parentFolderPath);

			return new SharepointWSFolder(parentFolderSharepointObject);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public String getLiferayLogin(String extRepositoryLogin) {
		int index = extRepositoryLogin.lastIndexOf(StringPool.BACK_SLASH);

		String login = extRepositoryLogin.substring(index + 1);

		index = login.indexOf(",#");

		if (index == -1) {
			return login;
		}

		return login.substring(0, index);
	}

	@Override
	public String getRootFolderKey() {
		return _rootFolderKey;
	}

	public SharepointConnection getSharepointConnection()
		throws RepositoryException {

		return _connectionCache.getConnection();
	}

	public String getSharepointLogin(String liferayLogin) {
		return _host + StringPool.BACK_SLASH + liferayLogin;
	}

	@Override
	public List<String> getSubfolderKeys(
		String extRepositoryFolderKey, boolean recurse) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFolderKey));

			String folderPath = folderSharepointObject.getPath();

			List<String> extRepositoryFolderKeys = new ArrayList<>();

			_getSubfolderKeys(folderPath, extRepositoryFolderKeys);

			return extRepositoryFolderKeys;
		}
		catch (SharepointException sharepointException) {
			throw new SystemException(sharepointException);
		}
	}

	@Override
	public String[] getSupportedConfigurations() {
		return _SUPPORTED_CONFIGURATIONS;
	}

	@Override
	public String[][] getSupportedParameters() {
		return _SUPPORTED_PARAMETERS;
	}

	@Override
	public void initRepository(
		UnicodeProperties typeSettingsUnicodeProperties,
		CredentialsProvider credentialsProvider) {

		try {
			_credentialsProvider = credentialsProvider;

			_libraryName = typeSettingsUnicodeProperties.getProperty(
				_LIBRARY_NAME);

			_libraryPath = typeSettingsUnicodeProperties.getProperty(
				_LIBRARY_PATH);

			if (Validator.isNull(_libraryPath)) {
				_libraryPath = _libraryName;
			}

			String serverVersion = typeSettingsUnicodeProperties.getProperty(
				_SERVER_VERSION, StringPool.BLANK);

			if (serverVersion.equals(_SHAREPOINT_2013_VALUE)) {
				_serverVersion =
					SharepointConnection.ServerVersion.SHAREPOINT_2013;
			}
			else {
				_serverVersion =
					SharepointConnection.ServerVersion.SHAREPOINT_2010;
			}

			String siteURL = typeSettingsUnicodeProperties.getProperty(
				_SITE_URL);

			URL url = URLUtil.toURL(siteURL);

			_host = url.getHost();
			_protocol = url.getProtocol();
			_port = _getPort(url);
			_sitePath = url.getPath();

			_connectionCache = new ConnectionCache<>(
				SharepointConnection.class, getRepositoryId(), this);

			SharepointConnection sharepointConnection =
				getSharepointConnection();

			_pingSharepointConnection(sharepointConnection);

			SharepointObject rootFolderSharepointObject =
				sharepointConnection.getSharepointObject(StringPool.SLASH);

			_rootFolderKey = String.valueOf(
				rootFolderSharepointObject.getSharepointObjectId());
		}
		catch (SharepointException sharepointException) {
			throw new SystemException(sharepointException);
		}
	}

	@Override
	public <T extends ExtRepositoryObject> T moveExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryObjectKey, String newExtRepositoryFolderKey,
			String newTitle)
		throws PortalException {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject sharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryObjectKey));

			String path = sharepointObject.getPath();

			SharepointObject folderSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(newExtRepositoryFolderKey));

			String folderPath = folderSharepointObject.getPath();

			String newPath = PathUtil.buildPath(folderPath, newTitle);

			_validateExtension(path, newPath);

			if (path.equals(newPath)) {
				return _toExtRepositoryObject(
					extRepositoryObjectType, sharepointObject);
			}

			sharepointConnection.moveSharepointObject(path, newPath);

			return _toExtRepositoryObject(
				extRepositoryObjectType,
				sharepointConnection.getSharepointObject(newPath));
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public List<ExtRepositorySearchResult<?>> search(
			SearchContext searchContext, Query query,
			ExtRepositoryQueryMapper extRepositoryQueryMapper)
		throws PortalException {

		List<SharepointObject> sharepointObjects = _search(
			searchContext, query, extRepositoryQueryMapper);

		sharepointObjects = ListUtil.subList(
			sharepointObjects, searchContext.getStart(),
			searchContext.getEnd());

		return TransformUtil.transform(
			sharepointObjects,
			sharepointObject -> {
				ExtRepositoryObject extRepositoryObject =
					_toExtRepositoryObject(
						ExtRepositoryObjectType.OBJECT, sharepointObject);

				return new ExtRepositorySearchResult<>(
					extRepositoryObject, 1, StringPool.BLANK);
			});
	}

	@Override
	public ExtRepositoryFileEntry updateExtRepositoryFileEntry(
		String extRepositoryFileEntryKey, String mimeType,
		InputStream inputStream) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			SharepointObject fileSharepointObject =
				sharepointConnection.getSharepointObject(
					_toSharepointObjectId(extRepositoryFileEntryKey));

			String filePath = fileSharepointObject.getPath();

			sharepointConnection.updateFile(filePath, inputStream);

			return new SharepointWSFileEntry(fileSharepointObject);
		}
		catch (SharepointException | SharepointRuntimeException exception) {
			throw new SystemException(exception);
		}
	}

	private int _getPort(URL url) {
		int port = url.getPort();

		if (port != -1) {
			return port;
		}

		String protocol = url.getProtocol();

		if (protocol.equals("https")) {
			return 443;
		}

		return 80;
	}

	private void _getSubfolderKeys(
		String path, List<String> extRepositoryFolderKeys) {

		try {
			SharepointConnection sharepointConnection =
				getSharepointConnection();

			List<SharepointObject> folderSharepointObjects =
				sharepointConnection.getSharepointObjects(
					path, SharepointConnection.ObjectTypeFilter.FOLDERS);

			for (SharepointObject folderSharepointObject :
					folderSharepointObjects) {

				String extRepositoryFolderKey = String.valueOf(
					folderSharepointObject.getSharepointObjectId());

				extRepositoryFolderKeys.add(extRepositoryFolderKey);

				_getSubfolderKeys(
					folderSharepointObject.getPath(), extRepositoryFolderKeys);
			}
		}
		catch (SharepointException sharepointException) {
			throw new SystemException(sharepointException);
		}
	}

	private void _pingSharepointConnection(
			SharepointConnection sharepointConnection)
		throws SharepointException {

		sharepointConnection.getSharepointObjectsCount(
			StringPool.SLASH, SharepointConnection.ObjectTypeFilter.FILES);
	}

	private void _processSharepointObjectException(
			boolean folder, String path, String name)
		throws PortalException {

		if (path == null) {
			return;
		}

		SharepointConnection sharepointConnection = getSharepointConnection();

		try {
			SharepointObject sharepointObject =
				sharepointConnection.getSharepointObject(path);

			if (sharepointObject == null) {
				return;
			}

			if (folder) {
				throw new DuplicateFolderNameException(name);
			}

			throw new DuplicateFileEntryException(name);
		}
		catch (SharepointException sharepointException) {
			if (_log.isDebugEnabled()) {
				_log.debug(sharepointException);
			}

			// The Sharepoint object does not exist

		}
	}

	private List<SharepointObject> _search(
			SearchContext searchContext, Query query,
			ExtRepositoryQueryMapper extRepositoryQueryMapper)
		throws PortalException {

		try {
			SharepointQueryBuilder sharepointQueryBuilder =
				new SharepointQueryBuilder(
					this, searchContext, query, extRepositoryQueryMapper);

			SharepointConnection sharepointConnection =
				getSharepointConnection();

			return sharepointConnection.getSharepointObjects(
				sharepointQueryBuilder.getQuery(),
				sharepointQueryBuilder.getQueryOptionsList());
		}
		catch (SharepointException sharepointException) {
			throw new SystemException(sharepointException);
		}
	}

	private <T extends ExtRepositoryObject> T _toExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			SharepointObject sharepointObject)
		throws PortalException {

		if (sharepointObject.isFile()) {
			if (extRepositoryObjectType == ExtRepositoryObjectType.FOLDER) {
				throw new NoSuchFolderException(
					StringBundler.concat(
						"Invalid external repository object type ",
						extRepositoryObjectType, " for Sharepoint object ",
						sharepointObject));
			}

			return (T)new SharepointWSFileEntry(sharepointObject);
		}

		if (extRepositoryObjectType == ExtRepositoryObjectType.FILE) {
			throw new NoSuchFileEntryException(
				StringBundler.concat(
					"Invalid external repository object type ",
					extRepositoryObjectType, " for Sharepoint object ",
					sharepointObject));
		}

		return (T)new SharepointWSFolder(sharepointObject);
	}

	private SharepointConnection.ObjectTypeFilter _toObjectTypeFilter(
		ExtRepositoryObjectType<? extends ExtRepositoryObject>
			extRepositoryObjectType) {

		SharepointConnection.ObjectTypeFilter objectTypeFilter =
			_objectTypeFilters.get(extRepositoryObjectType);

		if (objectTypeFilter == null) {
			throw new IllegalArgumentException(
				"Invalid external repository object type " +
					extRepositoryObjectType);
		}

		return objectTypeFilter;
	}

	private long _toSharepointObjectId(String key) {
		return GetterUtil.getLong(key);
	}

	private void _validateExtension(String oldPath, String newPath)
		throws PortalException {

		String oldExtension = PathUtil.getExtension(oldPath);

		String newExtension = PathUtil.getExtension(newPath);

		if (!newExtension.equals(oldExtension)) {
			throw new SourceFileNameException(
				"Sharepoint connector does not support changing the file " +
					"extension");
		}
	}

	private static final String _CONFIGURATION_WS = "SHAREPOINT_WS";

	private static final String _LIBRARY_NAME = "LIBRARY_NAME";

	private static final String _LIBRARY_PATH = "LIBRARY_PATH";

	private static final String _SERVER_VERSION = "SERVER_VERSION";

	private static final String _SHAREPOINT_2013_VALUE =
		SharepointConnection.ServerVersion.SHAREPOINT_2013.getValue();

	private static final String _SITE_URL = "SITE_URL";

	private static final String[] _SUPPORTED_CONFIGURATIONS = {
		_CONFIGURATION_WS
	};

	private static final String[][] _SUPPORTED_PARAMETERS = {
		{_LIBRARY_NAME, _LIBRARY_PATH, _SERVER_VERSION, _SITE_URL}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		SharepointWSRepository.class);

	private static final Map
		<ExtRepositoryObjectType<?>, SharepointConnection.ObjectTypeFilter>
			_objectTypeFilters =
				HashMapBuilder.
					<ExtRepositoryObjectType<?>,
					 SharepointConnection.ObjectTypeFilter>put(
						ExtRepositoryObjectType.FILE,
						SharepointConnection.ObjectTypeFilter.FILES
					).put(
						ExtRepositoryObjectType.FOLDER,
						SharepointConnection.ObjectTypeFilter.FOLDERS
					).put(
						ExtRepositoryObjectType.OBJECT,
						SharepointConnection.ObjectTypeFilter.ALL
					).build();

	private ConnectionCache<SharepointConnection> _connectionCache;
	private CredentialsProvider _credentialsProvider;
	private String _host;
	private String _libraryName;
	private String _libraryPath;
	private int _port;
	private String _protocol;
	private String _rootFolderKey;
	private SharepointConnection.ServerVersion _serverVersion;
	private String _sitePath;

}