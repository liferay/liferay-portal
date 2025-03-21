/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.soap.repository.connector.operation;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.sharepoint.soap.repository.connector.SharepointException;
import com.liferay.sharepoint.soap.repository.connector.SharepointObject;
import com.liferay.sharepoint.soap.repository.connector.SharepointVersion;
import com.liferay.sharepoint.soap.repository.connector.internal.util.RemoteExceptionSharepointExceptionMapper;

import com.microsoft.schemas.sharepoint.soap.GetVersionsDocument;
import com.microsoft.schemas.sharepoint.soap.GetVersionsResponseDocument;

import jakarta.xml.bind.DatatypeConverter;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Iván Zaera
 */
public final class GetSharepointVersionsOperation extends BaseOperation {

	@Override
	public void afterPropertiesSet() {
		_getSharepointObjectByPathOperation = getOperation(
			GetSharepointObjectByPathOperation.class);
	}

	public List<SharepointVersion> execute(String filePath)
		throws SharepointException {

		try {
			SharepointObject sharepointObject =
				_getSharepointObjectByPathOperation.execute(filePath);

			if (sharepointObject == null) {
				throw new SharepointException(
					"Unable to find Sharepoint object at " + filePath);
			}

			GetVersionsResponseDocument getVersionsResponseDocument =
				versionsSoap12Stub.getVersions(
					getGetVersionsDocument(filePath));

			return getSharepointVersions(
				sharepointObject, getVersionsResponseDocument);
		}
		catch (RemoteException remoteException) {
			throw RemoteExceptionSharepointExceptionMapper.map(
				remoteException, sharepointConnectionInfo);
		}
	}

	protected Date getDate(String dateString) {
		Calendar calendar = DatatypeConverter.parseDateTime(dateString);

		return calendar.getTime();
	}

	protected GetVersionsDocument getGetVersionsDocument(String filePath) {
		GetVersionsDocument getVersionsDocument =
			GetVersionsDocument.Factory.newInstance();

		GetVersionsDocument.GetVersions getVersions =
			getVersionsDocument.addNewGetVersions();

		getVersions.setFileName(toFullPath(filePath));

		return getVersionsDocument;
	}

	protected String getSharepointVersionId(
		long sharepointObjectId, String version) {

		return sharepointObjectId + StringPool.AT + version;
	}

	protected List<SharepointVersion> getSharepointVersions(
		SharepointObject sharepointObject,
		GetVersionsResponseDocument getVersionsResponseDocument) {

		List<SharepointVersion> sharepointVersions = new ArrayList<>();

		GetVersionsResponseDocument.GetVersionsResponse getVersionsResponse =
			getVersionsResponseDocument.getGetVersionsResponse();

		GetVersionsResponseDocument.GetVersionsResponse.GetVersionsResult
			getVersionsResult = getVersionsResponse.getGetVersionsResult();

		Node getVersionsResultNode = getVersionsResult.getDomNode();

		Node resultNode = getVersionsResultNode.getFirstChild();

		NodeList nodeList = resultNode.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			String localName = node.getLocalName();

			if ((localName == null) ||
				!StringUtil.equalsIgnoreCase(localName, "result")) {

				continue;
			}

			NamedNodeMap namedNodeMap = node.getAttributes();

			Node commentsNode = namedNodeMap.getNamedItem("comments");
			Node createdByNode = namedNodeMap.getNamedItem("createdBy");
			Node createdRawNode = namedNodeMap.getNamedItem("createdRaw");
			Node versionNode = namedNodeMap.getNamedItem("version");
			Node urlNode = namedNodeMap.getNamedItem("url");
			Node sizeNode = namedNodeMap.getNamedItem("size");

			SharepointVersion sharepointVersion = new SharepointVersion(
				commentsNode.getNodeValue(), createdByNode.getNodeValue(),
				getDate(createdRawNode.getNodeValue()),
				getSharepointVersionId(
					sharepointObject.getSharepointObjectId(),
					versionNode.getNodeValue()),
				GetterUtil.getLong(sizeNode.getNodeValue()),
				URLUtil.toURL(urlNode.getNodeValue()),
				getVersion(versionNode.getNodeValue()));

			sharepointVersions.add(sharepointVersion);
		}

		sharepointVersions.sort(_comparator);

		return sharepointVersions;
	}

	protected String getVersion(String version) {
		if (version.startsWith(StringPool.AT)) {
			return version.substring(1);
		}

		return version;
	}

	private static final Comparator<SharepointVersion> _comparator =
		new SharepointVersionComparator();

	private GetSharepointObjectByPathOperation
		_getSharepointObjectByPathOperation;

}