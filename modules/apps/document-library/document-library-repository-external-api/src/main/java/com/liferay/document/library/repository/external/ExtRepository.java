/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.external;

import com.liferay.document.library.repository.external.search.ExtRepositoryQueryMapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.InputStream;

import java.util.List;

/**
 * Represents the external repository for Liferay's Document Library, providing
 * utility methods for adding, deleting, retrieving, searching, and updating
 * external repository file entries and folders.
 *
 * <p>
 * Most data transferred in this class is in the external repository's domain
 * format. For example, external repository data such as object keys and user
 * names are strings expressed in the native external repository format.
 * </p>
 *
 * <p>
 * One exception is the {@link #search(SearchContext, Query,
 * ExtRepositoryQueryMapper)} method where the {@link Query} contains Liferay
 * identifiers and user names. To help with this, the {@link
 * ExtRepositoryQueryMapper} instance can be used to translate such IDs to the
 * native external repository format.
 * </p>
 *
 * @author Iván Zaera
 * @author Sergio González
 */
public interface ExtRepository {

	/**
	 * Adds an external repository file entry and associated metadata based on
	 * the {@link InputStream} object.
	 *
	 * @param  extRepositoryParentFolderKey the primary key of the repository
	 *         file entry's parent folder
	 * @param  fileName the repository file entry's file name
	 * @param  mimeType the repository file entry's MIME type
	 * @param  title the repository file entry's title
	 * @param  description the repository file entry's description
	 * @param  changeLog the repository file entry's version change log
	 * @param  inputStream the repository file entry's data (optionally
	 *         <code>null</code>)
	 * @return the repository file entry
	 * @throws PortalException if the repository parent folder could not be
	 *         found or if the repository file entry's information was invalid
	 */
	public ExtRepositoryFileEntry addExtRepositoryFileEntry(
			String extRepositoryParentFolderKey, String fileName,
			String mimeType, String title, String description, String changeLog,
			InputStream inputStream)
		throws PortalException;

	/**
	 * Adds an external repository folder.
	 *
	 * @param  extRepositoryParentFolderKey the primary key of the repository
	 *         folder's parent folder
	 * @param  name the repository folder's name
	 * @param  description the repository folder's description
	 * @return the repository folder
	 * @throws PortalException if the repository parent folder could not be
	 *         found or if the repository folder's information was invalid
	 */
	public ExtRepositoryFolder addExtRepositoryFolder(
			String extRepositoryParentFolderKey, String name,
			String description)
		throws PortalException;

	/**
	 * Cancels the check out of the external repository file. If a user has not
	 * checked out the external repository file entry, invoking this method
	 * results in no changes.
	 *
	 * @param  extRepositoryFileEntryKey the primary key of the repository file
	 *         entry
	 * @return the discarded repository file version, or <code>null</code> if no
	 *         version was available
	 * @throws PortalException if the repository file entry's information was
	 *         invalid
	 */
	public ExtRepositoryFileVersion cancelCheckOut(
			String extRepositoryFileEntryKey)
		throws PortalException;

	/**
	 * Checks in the external repository file entry. If a user has not checked
	 * out the external repository file entry, invoking this method results in
	 * no changes.
	 *
	 * @param  extRepositoryFileEntryKey the primary key of the repository file
	 *         entry
	 * @param  createMajorVersion whether to increase the major or minor version
	 *         number
	 * @param  changeLog the description of the changes being checked in
	 * @throws PortalException if the repository file entry's information was
	 *         invalid
	 */
	public void checkInExtRepositoryFileEntry(
			String extRepositoryFileEntryKey, boolean createMajorVersion,
			String changeLog)
		throws PortalException;

	/**
	 * Checks out the external repository file entry.
	 *
	 * @param  extRepositoryFileEntryKey the primary key of the repository file
	 *         entry
	 * @return the checked out repository file entry
	 * @throws PortalException if the repository file entry's information was
	 *         invalid
	 */
	public ExtRepositoryFileEntry checkOutExtRepositoryFileEntry(
			String extRepositoryFileEntryKey)
		throws PortalException;

	/**
	 * Copies the external repository object to a different parent folder.
	 *
	 * @param  extRepositoryObjectType the repository object's type (file or
	 *         folder)
	 * @param  extRepositoryFileEntryKey the primary key of the repository
	 *         object
	 * @param  newExtRepositoryFolderKey the primary key of the repository
	 *         destination folder
	 * @param  newTitle the new name of the repository object in the destination
	 *         folder
	 * @return the repository object
	 * @throws PortalException if a portal exception occurred
	 */
	public <T extends ExtRepositoryObject> T copyExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryFileEntryKey, String newExtRepositoryFolderKey,
			String newTitle)
		throws PortalException;

	/**
	 * Deletes the external repository object.
	 *
	 * @param  extRepositoryObjectType the repository object's type (file or
	 *         folder)
	 * @param  extRepositoryObjectKey the primary key of the repository object
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteExtRepositoryObject(
			ExtRepositoryObjectType<? extends ExtRepositoryObject>
				extRepositoryObjectType,
			String extRepositoryObjectKey)
		throws PortalException;

	public String getAuthType();

	/**
	 * Returns the content stream of the external repository file entry.
	 *
	 * @param  extRepositoryFileEntry the primary key of the repository file
	 *         entry
	 * @return the content stream of the repository file entry
	 * @throws PortalException if the repository file entry's information was
	 *         invalid
	 */
	public InputStream getContentStream(
			ExtRepositoryFileEntry extRepositoryFileEntry)
		throws PortalException;

	/**
	 * Returns the content stream of the external repository file version.
	 *
	 * @param  extRepositoryFileVersion the primary key of the repository file
	 *         version
	 * @return the content stream of the repository file version
	 * @throws PortalException if the repository file version's information was
	 *         invalid
	 */
	public InputStream getContentStream(
			ExtRepositoryFileVersion extRepositoryFileVersion)
		throws PortalException;

	/**
	 * Returns the external repository file version of the file entry,
	 * identified by the version name.
	 *
	 * @param  extRepositoryFileEntry the primary key of the repository file
	 *         entry
	 * @param  version the repository version name (e.g. <code>1.0</code>)
	 * @return the repository file version of the file entry
	 * @throws PortalException if the repository file entry or version
	 *         information was invalid
	 */
	public ExtRepositoryFileVersion getExtRepositoryFileVersion(
			ExtRepositoryFileEntry extRepositoryFileEntry, String version)
		throws PortalException;

	/**
	 * Returns the {@link ExtRepositoryFileVersionDescriptor} translated from
	 * the repository file version key. The descriptor describes the external
	 * repository file entry key and version name.
	 *
	 * @param  extRepositoryFileVersionKey the repository file version's key
	 * @return the {@link ExtRepositoryFileVersionDescriptor} translated from
	 *         the repository file version key
	 */
	public ExtRepositoryFileVersionDescriptor
		getExtRepositoryFileVersionDescriptor(
			String extRepositoryFileVersionKey);

	/**
	 * Returns the external repository file versions of the external repository
	 * file entry. The versions are ordered newest to oldest.
	 *
	 * @param  extRepositoryFileEntry the primary key of the repository file
	 *         entry
	 * @return the repository file versions of the repository file entry
	 * @throws PortalException if the repository file entry was invalid
	 */
	public List<ExtRepositoryFileVersion> getExtRepositoryFileVersions(
			ExtRepositoryFileEntry extRepositoryFileEntry)
		throws PortalException;

	/**
	 * Returns the external repository object matching the type and key.
	 *
	 * @param  extRepositoryObjectType the repository object's type. Use {@link
	 *         ExtRepositoryObjectType#FILE},  {@link
	 *         ExtRepositoryObjectType#FOLDER}, or  {@link
	 *         ExtRepositoryObjectType#OBJECT} to specify file, folder, or both,
	 *         respectively.
	 * @param  extRepositoryObjectKey the primary key of the repository object
	 * @return the repository object matching the type and key
	 * @throws PortalException if a portal exception occurred
	 */
	public <T extends ExtRepositoryObject> T getExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryObjectKey)
		throws PortalException;

	/**
	 * Returns the external repository object matching the type and title, in
	 * the parent folder.
	 *
	 * @param  extRepositoryObjectType the repository object's type. Use {@link
	 *         ExtRepositoryObjectType#FILE},  {@link
	 *         ExtRepositoryObjectType#FOLDER}, or  {@link
	 *         ExtRepositoryObjectType#OBJECT} to specify file, folder, or both,
	 *         respectively.
	 * @param  extRepositoryFolderKey the primary key of the repository object's
	 *         parent folder
	 * @param  title the repository object's name
	 * @return the repository object matching the type and title, in the parent
	 *         folder
	 * @throws PortalException if a portal exception occurred
	 */
	public <T extends ExtRepositoryObject> T getExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryFolderKey, String title)
		throws PortalException;

	/**
	 * Returns the external repository objects matching the type, in the parent
	 * folder. Depending on the repository object type requested, this method
	 * may return only repository files, only repository folders, or both
	 * repository files and folders.
	 *
	 * @param  extRepositoryObjectType the type of repository objects to return.
	 *         Use {@link ExtRepositoryObjectType#FILE},  {@link
	 *         ExtRepositoryObjectType#FOLDER}, or  {@link
	 *         ExtRepositoryObjectType#OBJECT} to specify file, folder, or both,
	 *         respectively.
	 * @param  extRepositoryFolderKey the primary key of the repository folder
	 *         to search
	 * @return the repository objects matching the type, in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public <T extends ExtRepositoryObject> List<T> getExtRepositoryObjects(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryFolderKey)
		throws PortalException;

	/**
	 * Returns the number of elements in the external repository folder matching
	 * the object type. Depending on the repository object type requested, this
	 * method may only return the number of repository files, the number of
	 * repository folders, or the combined number of repository files and
	 * folders.
	 *
	 * @param  extRepositoryObjectType the repository object type to count. Use
	 *         {@link ExtRepositoryObjectType#FILE},  {@link
	 *         ExtRepositoryObjectType#FOLDER}, or  {@link
	 *         ExtRepositoryObjectType#OBJECT} to specify file, folder, or both,
	 *         respectively.
	 * @param  extRepositoryFolderKey the primary key of the repository folder
	 *         to search
	 * @return the number of elements in the repository folder matching the
	 *         object type
	 * @throws PortalException if a portal exception occurred
	 */
	public int getExtRepositoryObjectsCount(
			ExtRepositoryObjectType<? extends ExtRepositoryObject>
				extRepositoryObjectType,
			String extRepositoryFolderKey)
		throws PortalException;

	/**
	 * Returns the external repository parent folder of the external repository
	 * object.
	 *
	 * @param  extRepositoryObject the repository object (file or folder)
	 * @return the repository parent folder of the repository object
	 * @throws PortalException if the repository object's information was
	 *         invalid
	 */
	public ExtRepositoryFolder getExtRepositoryParentFolder(
			ExtRepositoryObject extRepositoryObject)
		throws PortalException;

	public default ExtRepositoryFileVersion getLatestExtRepositoryFileVersion(
			ExtRepositoryFileEntry extRepositoryFileEntry)
		throws PortalException {

		List<ExtRepositoryFileVersion> extRepositoryFileVersions =
			getExtRepositoryFileVersions(extRepositoryFileEntry);

		return extRepositoryFileVersions.get(0);
	}

	/**
	 * Returns the Liferay login value, which is mapped from the user's external
	 * repository login value. A Liferay login value can be a user ID, screen
	 * name, email, etc, depending on the value returned by the {@link
	 * #getAuthType()} method. The format of the repository login is specific
	 * for each repository implementation.
	 *
	 * @param  extRepositoryLogin the user's repository login value
	 * @return the Liferay login value, which is mapped from the user's
	 *         repository login value
	 */
	public String getLiferayLogin(String extRepositoryLogin);

	/**
	 * Returns the primary key of the external repository root folder.
	 *
	 * @return the primary key of the repository root folder
	 * @throws PortalException if the repository root folder could not be
	 *         accessed
	 */
	public String getRootFolderKey() throws PortalException;

	/**
	 * Returns the keys of the external repository subfolders stored inside the
	 * external repository folder.
	 *
	 * @param  extRepositoryFolderKey the primary key of the repository folder
	 * @param  recurse whether to recurse through each repository subfolder
	 * @return the keys of the repository subfolders stored inside the
	 *         repository folder
	 * @throws PortalException if the repository folder's information was
	 *         invalid
	 */
	public List<String> getSubfolderKeys(
			String extRepositoryFolderKey, boolean recurse)
		throws PortalException;

	/**
	 * Returns the supported configurations for the external repository. Each
	 * configuration may have a different list of supported parameters.
	 *
	 * @return the supported configurations for the repository
	 * @see    #getSupportedParameters()
	 */
	public String[] getSupportedConfigurations();

	/**
	 * Returns the supported external repository configuration parameters
	 * indexed by configuration type. These parameters are stored in the
	 * database when the repository is configured and made available through a
	 * {@link UnicodeProperties} object passed to the {@link
	 * #initRepository(UnicodeProperties, CredentialsProvider)} method.
	 *
	 * @return the supported repository configuration parameters indexed by
	 *         configuration type
	 * @see    #getSupportedConfigurations()
	 */
	public String[][] getSupportedParameters();

	/**
	 * Initializes the external repository and checks the connectivity between
	 * the external repository and Liferay Portal.
	 *
	 * <p>
	 * This method uses the credentials provided by the {@link
	 * CredentialsProvider} object to authenticate to the repository. In
	 * addition, the method acquires the repository-specific configuration
	 * parameters from the type settings properties parameter. The parameters
	 * contained in the type settings properties parameter object are indexed by
	 * the key names returned by the {@link #getSupportedParameters()} method.
	 * This method is called only once, the first time the repository is used.
	 * </p>
	 *
	 * @param  typeSettingsUnicodeProperties the type settings properties
	 * @param  credentialsProvider the Liferay Portal username and password
	 * @throws PortalException if a portal exception occurred
	 */
	public void initRepository(
			UnicodeProperties typeSettingsUnicodeProperties,
			CredentialsProvider credentialsProvider)
		throws PortalException;

	/**
	 * Moves the external repository object to a different location. This method
	 * can also be used to rename repository objects.
	 *
	 * @param  extRepositoryObjectType the repository object's type (file or
	 *         folder)
	 * @param  extRepositoryObjectKey the primary key of the repository object
	 * @param  newExtRepositoryFolderKey the primary key of the destination
	 *         repository folder
	 * @param  newTitle the new name of the repository object (may not change)
	 * @return the repository object
	 * @throws PortalException if the repository object could not be moved or if
	 *         a portal exception occurred
	 */
	public <T extends ExtRepositoryObject> T moveExtRepositoryObject(
			ExtRepositoryObjectType<T> extRepositoryObjectType,
			String extRepositoryObjectKey, String newExtRepositoryFolderKey,
			String newTitle)
		throws PortalException;

	/**
	 * Returns the external repository objects fulfilling the query. There may
	 * be some limitations due to back-end repository constraints.
	 *
	 * <p>
	 * The limitations are repository specific, which means there is a specific
	 * set of constraints for each type of external repository (Documentum,
	 * SharePoint, etc). This method is given a {@link Query} object, which is a
	 * logic expression matching Lucene's capabilities. Implementors of external
	 * repositories must map that query to a native repository query. Some
	 * external repositories may be missing some of Lucene's capabilities, so
	 * the query may not be fully translated.
	 * </p>
	 *
	 * @param  searchContext the search context to be applied. The folder ID,
	 *         start bound, and end bound attributes must be set in this search
	 *         context.
	 * @param  query the logical expression describing the query to be
	 *         performed. This may need to be translated to an equivalent native
	 *         repository query.
	 * @param  extRepositoryQueryMapper the repository query mapper to translate
	 *         Liferay IDs to the native repository format
	 * @return the repository objects fulfilling the query
	 * @throws PortalException if a portal exception occurred
	 */
	public List<ExtRepositorySearchResult<?>> search(
			SearchContext searchContext, Query query,
			ExtRepositoryQueryMapper extRepositoryQueryMapper)
		throws PortalException;

	/**
	 * Updates the external repository file entry's content.
	 *
	 * @param  extRepositoryFileEntryKey the primary key of the repository file
	 *         entry
	 * @param  mimeType the repository file entry's MIME type
	 * @param  inputStream the new repository file entry's content
	 * @return the updated file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public ExtRepositoryFileEntry updateExtRepositoryFileEntry(
			String extRepositoryFileEntryKey, String mimeType,
			InputStream inputStream)
		throws PortalException;

}