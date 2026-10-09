/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.soap.repository.connector.operation;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.sharepoint.soap.repository.connector.SharepointException;
import com.liferay.sharepoint.soap.repository.connector.SharepointObject;
import com.liferay.sharepoint.soap.repository.connector.internal.util.RemoteExceptionSharepointExceptionMapper;
import com.liferay.sharepoint.soap.repository.connector.operation.constants.SharepointConstants;
import com.liferay.sharepoint.soap.repository.connector.schema.XMLUtil;
import com.liferay.sharepoint.soap.repository.connector.schema.query.Query;
import com.liferay.sharepoint.soap.repository.connector.schema.query.QueryField;
import com.liferay.sharepoint.soap.repository.connector.schema.query.QueryOptionsList;
import com.liferay.sharepoint.soap.repository.connector.schema.query.option.ExpandUserFieldQueryOption;

import com.microsoft.schemas.sharepoint.soap.GetListItemsDocument;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument;
import com.microsoft.schemas.sharepoint.soap.impl.GetListItemsDocumentImpl;

import java.rmi.RemoteException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Iván Zaera
 */
public final class GetSharepointObjectsByQueryOperation extends BaseOperation {

	@Override
	public void afterPropertiesSet() {
		String libraryPath = sharepointConnectionInfo.getLibraryPath();
		String sitePath = sharepointConnectionInfo.getSitePath();

		_pathPrefixToRemoveLength = libraryPath.length() + sitePath.length();
	}

	public List<SharepointObject> execute(
			Query query, QueryOptionsList queryOptionsList,
			String... queryFieldNames)
		throws SharepointException {

		try {
			GetListItemsResponseDocument getListItemsResponseDocument =
				listsSoap12Stub.getListItems(
					getGetListItemsDocument(
						query, _getQueryOptionsList(queryOptionsList),
						queryFieldNames));

			log(query, queryOptionsList, getListItemsResponseDocument);

			return getSharepointObjects(getListItemsResponseDocument);
		}
		catch (RemoteException remoteException) {
			throw RemoteExceptionSharepointExceptionMapper.map(
				remoteException, sharepointConnectionInfo);
		}
	}

	protected GetListItemsDocument getGetListItemsDocument(
		Query query, QueryOptionsList queryOptionsList,
		String... queryFieldNames) {

		GetListItemsDocument getListItemsDocument =
			GetListItemsDocument.Factory.newInstance();

		GetListItemsDocument.GetListItems getListItems =
			getListItemsDocument.addNewGetListItems();

		getListItems.setListName(sharepointConnectionInfo.getLibraryName());
		getListItems.setQuery(getQuery(query));
		getListItems.setQueryOptions(getQueryOptions(queryOptionsList));
		getListItems.setViewFields(getViewFields(queryFieldNames));
		getListItems.setViewName(SharepointConstants.VIEW_DEFAULT);
		getListItems.setRowLimit(SharepointConstants.ROW_LIMIT_DEFAULT);

		return getListItemsDocument;
	}

	protected String getNodeValue(Node node, int index) {
		if (node == null) {
			return null;
		}

		String nodeValue = node.getNodeValue();

		String[] parts = nodeValue.split(
			SharepointConstants.PATTERN_MULTI_VALUE_SEPARATOR);

		if (index < parts.length) {
			return parts[index];
		}

		return null;
	}

	protected Set<SharepointObject.Permission> getPermissions(
		String permissionsHexMask) {

		Set<SharepointObject.Permission> permissions = EnumSet.noneOf(
			SharepointObject.Permission.class);

		long permisssionsMask = Long.valueOf(
			permissionsHexMask.substring(2), 16);

		for (SharepointObject.Permission permission :
				SharepointObject.Permission.values()) {

			long permissionBit = permisssionsMask & permission.getMask();

			if (permissionBit == 0) {
				continue;
			}

			permissions.add(permission);
		}

		return permissions;
	}

	protected GetListItemsDocument.GetListItems.Query getQuery(Query query) {
		GetListItemsDocument.GetListItems.Query getListItemsQuery =
			GetListItemsDocumentImpl.GetListItems.Query.Factory.newInstance();

		Node node = getListItemsQuery.getDomNode();

		node.appendChild(XMLUtil.toNode(node.getOwnerDocument(), query));

		return getListItemsQuery;
	}

	protected GetListItemsDocument.GetListItems.QueryOptions getQueryOptions(
		QueryOptionsList queryOptionsList) {

		GetListItemsDocument.GetListItems.QueryOptions queryOptions =
			GetListItemsDocumentImpl.GetListItems.QueryOptions.Factory.
				newInstance();

		Node node = queryOptions.getDomNode();

		node.appendChild(
			XMLUtil.toNode(node.getOwnerDocument(), queryOptionsList));

		return queryOptions;
	}

	protected List<SharepointObject> getSharepointObjects(
		GetListItemsResponseDocument getListItemsResponseDocument) {

		GetListItemsResponseDocument.GetListItemsResponse getListItemsResponse =
			getListItemsResponseDocument.getGetListItemsResponse();

		GetListItemsResponseDocument.GetListItemsResponse.GetListItemsResult
			getListItemsResult = getListItemsResponse.getGetListItemsResult();

		Node getListItemsResultNode = getListItemsResult.getDomNode();

		Node dataElement = XMLUtil.getNode(
			"Data", getListItemsResultNode.getFirstChild());

		if (dataElement == null) {
			return Collections.emptyList();
		}

		NodeList nodeList = dataElement.getChildNodes();

		if (nodeList == null) {
			return Collections.emptyList();
		}

		List<SharepointObject> sharepointObjects = new ArrayList<>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			String localName = node.getLocalName();

			if ((localName == null) ||
				!StringUtil.equalsIgnoreCase(localName, "row")) {

				continue;
			}

			NamedNodeMap namedNodeMap = node.getAttributes();

			Node owsFileRefNode = namedNodeMap.getNamedItem("ows_FileRef");

			String path = getNodeValue(owsFileRefNode, 1);

			path = path.substring(_pathPrefixToRemoveLength);

			Node owsAuthorNode = namedNodeMap.getNamedItem("ows_Author");

			Node owsCheckedOutUserIdNode = namedNodeMap.getNamedItem(
				"ows_CheckoutUser");

			if (owsCheckedOutUserIdNode == null) {
				owsCheckedOutUserIdNode = namedNodeMap.getNamedItem(
					"ows_CheckedOutTitle");
			}

			Node owsCreatedX0020DateNode = namedNodeMap.getNamedItem(
				"ows_Created_x0020_Date");
			Node owsFSObjTypeNode = namedNodeMap.getNamedItem("ows_FSObjType");
			Node owsLastX0020ModifiedNode = namedNodeMap.getNamedItem(
				"ows_Last_x0020_Modified");
			Node owsPermMaskNode = namedNodeMap.getNamedItem("ows_PermMask");
			Node owsFileX0020SizeNode = namedNodeMap.getNamedItem(
				"ows_File_x0020_Size");

			SharepointObject sharepointObject = new SharepointObject(
				getNodeValue(owsAuthorNode, 1),
				getNodeValue(owsCheckedOutUserIdNode, 1),
				parseDate(getNodeValue(owsCreatedX0020DateNode, 1)),
				Objects.equals(
					getNodeValue(owsFSObjTypeNode, 1),
					SharepointConstants.FS_OBJ_TYPE_FOLDER),
				parseDate(getNodeValue(owsLastX0020ModifiedNode, 1)), path,
				getPermissions(owsPermMaskNode.getNodeValue()),
				GetterUtil.getLong(getNodeValue(owsFileRefNode, 0)),
				GetterUtil.getLong(getNodeValue(owsFileX0020SizeNode, 1)),
				toURL(path));

			sharepointObjects.add(sharepointObject);
		}

		return sharepointObjects;
	}

	protected GetListItemsDocument.GetListItems.ViewFields getViewFields(
		String... queryFieldNames) {

		GetListItemsDocument.GetListItems.ViewFields viewFields =
			GetListItemsDocumentImpl.GetListItems.ViewFields.Factory.
				newInstance();

		Node node = viewFields.getDomNode();

		for (Node childNode :
				XMLUtil.toNodes(
					node.getOwnerDocument(), toQueryFields(queryFieldNames))) {

			node.appendChild(childNode);
		}

		return viewFields;
	}

	protected void log(
		Query query, QueryOptionsList queryOptionsList,
		GetListItemsResponseDocument getListItemsResponseDocument) {

		if (!_log.isDebugEnabled()) {
			return;
		}

		_log.debug(
			StringBundler.concat(
				"Query: ", query, "\nQuery options: ", queryOptionsList,
				"\nResult: ", getListItemsResponseDocument.xmlText()));
	}

	protected Date parseDate(String dateString) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(
				SharepointConstants.SHAREPOINT_OBJECT_DATE_FORMAT_PATTERN);

			dateFormat.setTimeZone(
				SharepointConstants.SHAREPOINT_OBJECT_TIME_ZONE);

			return dateFormat.parse(dateString);
		}
		catch (ParseException parseException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse " + dateString +
						" to a Sharepoint object date",
					parseException);
			}

			return new Date(0);
		}
	}

	protected QueryField[] toQueryFields(String[] queryFieldNames) {
		QueryField[] queryFields = new QueryField[queryFieldNames.length];

		for (int i = 0; i < queryFieldNames.length; i++) {
			queryFields[i] = new QueryField(queryFieldNames[i]);
		}

		return queryFields;
	}

	private QueryOptionsList _getQueryOptionsList(
		QueryOptionsList queryOptionsList) {

		if (!queryOptionsList.contains(ExpandUserFieldQueryOption.class)) {
			return queryOptionsList.append(
				new ExpandUserFieldQueryOption(true));
		}

		return queryOptionsList;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetSharepointObjectsByQueryOperation.class);

	private int _pathPrefixToRemoveLength;

}