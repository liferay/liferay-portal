/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Element;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Bruno Farache
 */
public abstract class BaseSharepointStorageImpl implements SharepointStorage {

	@Override
	public void addDocumentElements(
			SharepointRequest sharepointRequest, Element element)
		throws Exception {
	}

	@Override
	public void createFolder(SharepointRequest sharepointRequest)
		throws Exception {
	}

	@Override
	public InputStream getDocumentInputStream(
			SharepointRequest sharepointRequest)
		throws Exception {

		return null;
	}

	@Override
	public Tree getDocumentTree(SharepointRequest sharepointRequest)
		throws Exception {

		return new Tree();
	}

	@Override
	public Tree getDocumentsTree(SharepointRequest sharepointRequest)
		throws Exception {

		return new Tree();
	}

	@Override
	public Tree getFolderTree(SharepointRequest sharepointRequest)
		throws Exception {

		return new Tree();
	}

	@Override
	public Tree getFoldersTree(SharepointRequest sharepointRequest)
		throws Exception {

		return new Tree();
	}

	@Override
	public void getParentFolderIds(
			long groupId, String path, List<Long> folderIds)
		throws Exception {
	}

	@Override
	public Tree[] moveDocument(SharepointRequest sharepointRequest)
		throws Exception {

		return null;
	}

	@Override
	public void putDocument(SharepointRequest sharepointRequest)
		throws Exception {
	}

	@Override
	public Tree[] removeDocument(SharepointRequest sharepointRequest)
		throws Exception {

		return null;
	}

	protected void addDocumentElement(
			Element element, String documentName, Date createDate,
			Date modifiedDate, String userName)
		throws Exception {

		element.addNamespace("z", "#RowsetSchema");

		Element rowEl = element.addElement("z:row");

		rowEl.addAttribute("ows_FileRef", documentName);
		rowEl.addAttribute("ows_FSObjType", "0");
		rowEl.addAttribute("ows_Created", getDate(createDate, true));
		rowEl.addAttribute("ows_Author", userName);
		rowEl.addAttribute("ows_Modified", getDate(modifiedDate, true));
		rowEl.addAttribute("ows_Editor", userName);
	}

	protected String getDate(Date date, boolean xml) {
		if (date == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(2);

		if (xml) {
			sb.append(
				DateUtil.getDate(date, "yyyy-mm-dd HH:mm:ss Z", LocaleUtil.US));
		}
		else {
			sb.append("TR|");
			sb.append(
				DateUtil.getDate(
					date, "dd MMM yyyy HH:mm:ss Z", LocaleUtil.US));
		}

		return sb.toString();
	}

	protected Tree getDocumentTree(
		String documentName, Date createDate, Date modifiedDate, long size,
		String userName, String version) {

		Tree documentTree = new Tree();

		documentName = SharepointUtil.replaceBackSlashes(documentName);

		documentTree.addChild(new Leaf("document_name", documentName, true));

		String createDateString = getDate(createDate, false);
		String modifiedDateString = getDate(modifiedDate, false);

		Tree metaInfoTree = new Tree();

		metaInfoTree.addChild(
			new Leaf("vti_timecreated", createDateString, false));
		metaInfoTree.addChild(
			new Leaf("vti_timelastmodified", modifiedDateString, false));
		metaInfoTree.addChild(
			new Leaf("vti_timelastwritten", modifiedDateString, false));
		metaInfoTree.addChild(new Leaf("vti_filesize", "IR|" + size, false));
		metaInfoTree.addChild(
			new Leaf("vti_sourcecontrolcheckedoutby", "SR|" + userName, false));
		metaInfoTree.addChild(
			new Leaf(
				"vti_sourcecontroltimecheckedout", createDateString, false));
		metaInfoTree.addChild(
			new Leaf("vti_sourcecontrolversion", "SR|V" + version, false));
		metaInfoTree.addChild(
			new Leaf("vti_sourcecontrollockexpires", createDateString, false));

		documentTree.addChild(new Leaf("meta_info", metaInfoTree));

		return documentTree;
	}

	protected Tree getFolderTree(String name) {
		Date date = new Date();

		return getFolderTree(name, date, date, date);
	}

	protected Tree getFolderTree(
		String name, Date createDate, Date modifiedDate, Date lastPostDate) {

		Tree folderTree = new Tree();

		Tree metaInfoTree = new Tree();

		name = SharepointUtil.replaceBackSlashes(name);

		metaInfoTree.addChild(
			new Leaf("vti_timecreated", getDate(createDate, false), false));
		metaInfoTree.addChild(
			new Leaf(
				"vti_timelastmodified", getDate(modifiedDate, false), false));
		metaInfoTree.addChild(
			new Leaf(
				"vti_timelastwritten", getDate(lastPostDate, false), false));
		metaInfoTree.addChild(new Leaf("vti_hassubdirs", "BR|true", false));
		metaInfoTree.addChild(new Leaf("vti_isbrowsable", "BR|true", false));
		metaInfoTree.addChild(new Leaf("vti_isexecutable", "BR|false", false));
		metaInfoTree.addChild(new Leaf("vti_isscriptable", "BR|false", false));

		folderTree.addChild(new Leaf("url", name, true));
		folderTree.addChild(new Leaf("meta_info", metaInfoTree));

		return folderTree;
	}

	protected long getLastFolderId(
			long groupId, String path, long defaultParentFolderId)
		throws Exception {

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(defaultParentFolderId);

		String[] pathArray = SharepointUtil.getPathArray(path);

		if (pathArray.length > 2) {
			path = removeFoldersFromPath(path, 2);

			getParentFolderIds(groupId, path, folderIds);
		}

		return folderIds.get(folderIds.size() - 1);
	}

	protected String getParentFolderPath(String path) {
		int pos = path.lastIndexOf(CharPool.FORWARD_SLASH);

		return path.substring(0, pos);
	}

	protected String getResourceName(String path) {
		int pos = path.lastIndexOf(CharPool.FORWARD_SLASH);

		return path.substring(pos + 1);
	}

	protected String removeFoldersFromPath(String path, int index) {
		for (int i = 0; i < index; i++) {
			int pos = path.indexOf(CharPool.SLASH);

			path = path.substring(pos + 1);
		}

		return path;
	}

}