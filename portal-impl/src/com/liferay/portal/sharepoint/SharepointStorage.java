/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.portal.kernel.xml.Element;

import java.io.InputStream;

import java.util.List;

/**
 * @author Bruno Farache
 */
public interface SharepointStorage {

	public void addDocumentElements(
			SharepointRequest sharepointRequest, Element element)
		throws Exception;

	public void createFolder(SharepointRequest sharepointRequest)
		throws Exception;

	public InputStream getDocumentInputStream(
			SharepointRequest sharepointRequest)
		throws Exception;

	public Tree getDocumentTree(SharepointRequest sharepointRequest)
		throws Exception;

	public Tree getDocumentsTree(SharepointRequest sharepointRequest)
		throws Exception;

	public Tree getFolderTree(SharepointRequest sharepointRequest)
		throws Exception;

	public Tree getFoldersTree(SharepointRequest sharepointRequest)
		throws Exception;

	public void getParentFolderIds(
			long groupId, String path, List<Long> folderIds)
		throws Exception;

	public Tree[] moveDocument(SharepointRequest sharepointRequest)
		throws Exception;

	public void putDocument(SharepointRequest sharepointRequest)
		throws Exception;

	public Tree[] removeDocument(SharepointRequest sharepointRequest)
		throws Exception;

}