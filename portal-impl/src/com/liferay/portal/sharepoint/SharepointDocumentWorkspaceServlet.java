/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.sharepoint.dws.MemberResponseElement;
import com.liferay.portal.sharepoint.dws.ResponseElement;
import com.liferay.portal.sharepoint.dws.RoleResponseElement;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * @author Bruno Farache
 */
public class SharepointDocumentWorkspaceServlet extends HttpServlet {

	@Override
	protected void doPost(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					httpServletRequest.getHeader(HttpHeaders.USER_AGENT), " ",
					httpServletRequest.getMethod(), " ",
					httpServletRequest.getRequestURI()));
		}

		try {
			getDwsMetaDataResponse(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	protected void getDwsMetaDataResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		StringBundler sb = new StringBundler(12);

		sb.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"");
		sb.append("http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<SOAP-ENV:Header/>");
		sb.append("<SOAP-ENV:Body>");
		sb.append("<GetDwsMetaDataResponse xmlns=\"");
		sb.append("http://schemas.microsoft.com/sharepoint/soap/dws/\">");
		sb.append("<GetDwsMetaDataResult>");

		String results = getResults(httpServletRequest);

		int pos = results.indexOf("\n");

		if (pos != -1) {
			results = results.substring(pos + 1);
		}

		results = StringUtil.replace(results, '<', "&lt;");
		results = StringUtil.replace(results, '>', "&gt;");

		sb.append(results);

		sb.append("</GetDwsMetaDataResult>");
		sb.append("</GetDwsMetaDataResponse>");
		sb.append("</SOAP-ENV:Body>");
		sb.append("</SOAP-ENV:Envelope>");

		httpServletResponse.setContentType(ContentTypes.TEXT_XML_UTF8);

		ServletResponseUtil.write(httpServletResponse, sb.toString());
	}

	protected String getResults(HttpServletRequest httpServletRequest)
		throws Exception {

		String xml = StringUtil.read(httpServletRequest.getInputStream());

		String documentName = null;

		int beginPos = xml.lastIndexOf("<document>");
		int endPos = xml.lastIndexOf("</document>");

		if (beginPos != -1) {
			documentName = xml.substring(beginPos + 10, endPos);

			documentName = HttpComponentsUtil.decodeURL(documentName);
		}

		String path = documentName;

		if (_log.isInfoEnabled()) {
			_log.info("Original path " + path);
		}

		path = SharepointUtil.stripService(path, true);

		if (_log.isInfoEnabled()) {
			_log.info("Modified path " + path);
		}

		Group group = GroupServiceUtil.getGroup(
			SharepointUtil.getGroupId(path));

		boolean minimal = false;

		beginPos = xml.lastIndexOf("<minimal>");

		if (beginPos != -1) {
			endPos = xml.lastIndexOf("</minimal>");

			minimal = GetterUtil.getBoolean(
				xml.substring(beginPos + 9, endPos));
		}

		Document doc = SAXReaderUtil.createDocument();

		Element root = doc.addElement("Results");

		String url = StringBundler.concat(
			"http://", httpServletRequest.getLocalAddr(), ":",
			httpServletRequest.getServerPort(), "/sharepoint");

		Element subscribeUrlEl = root.addElement("SubscribeUrl");

		subscribeUrlEl.setText(url);

		root.addElement("MtgInstance");

		Element settingUrlEl = root.addElement("SettingUrl");

		settingUrlEl.setText(url);

		Element permsUrlEl = root.addElement("PermsUrl");

		permsUrlEl.setText(url);

		Element userInfoUrlEl = root.addElement("UserInfoUrl");

		userInfoUrlEl.setText(url);

		Element rolesEl = root.addElement("Roles");

		List<Role> roles = RoleLocalServiceUtil.getRoles(
			PortalUtil.getCompanyId(httpServletRequest));

		for (Role role : roles) {
			ResponseElement responseElement = new RoleResponseElement(role);

			responseElement.addElement(rolesEl);
		}

		if (!minimal) {
			Element schemaEl = root.addElement("Schema");

			schemaEl.addAttribute("Name", "Documents");
			schemaEl.addAttribute("Url", group.getDescriptiveName());

			Element fieldEl = schemaEl.addElement("Field");

			fieldEl.addAttribute("Name", "FileLeafRef");
			fieldEl.addAttribute("Required", "true");
			fieldEl.addAttribute("Type", "Invalid");

			fieldEl.addElement("Choices");

			fieldEl = schemaEl.addElement("Field");

			fieldEl.addAttribute("Name", "_SourceUrl");
			fieldEl.addAttribute("Required", "false");
			fieldEl.addAttribute("Type", "Text");

			fieldEl.addElement("Choices");

			fieldEl = schemaEl.addElement("Field");

			fieldEl.addAttribute("Name", "_SharedFileIndex");
			fieldEl.addAttribute("Required", "false");
			fieldEl.addAttribute("Type", "Text");

			fieldEl.addElement("Choices");

			fieldEl = schemaEl.addElement("Field");

			fieldEl.addAttribute("Name", "Order");
			fieldEl.addAttribute("Required", "false");
			fieldEl.addAttribute("Type", "Number");

			fieldEl.addElement("Choices");

			fieldEl = schemaEl.addElement("Field");

			fieldEl.addAttribute("Name", "Title");
			fieldEl.addAttribute("Required", "false");
			fieldEl.addAttribute("Type", "Text");

			fieldEl.addElement("Choices");

			Element listInfoEl = root.addElement("ListInfo");

			listInfoEl.addAttribute("Name", "Links");

			Element moderatedEl = listInfoEl.addElement("Moderated");

			moderatedEl.setText(Boolean.FALSE.toString());

			Element listPermissionsEl = listInfoEl.addElement(
				"ListPermissions");

			listPermissionsEl.addElement("DeleteListItems");
			listPermissionsEl.addElement("EditListItems");
			listPermissionsEl.addElement("InsertListItems");
			listPermissionsEl.addElement("ManageRoles");
			listPermissionsEl.addElement("ManageWeb");
		}

		Element permissionsEl = root.addElement("Permissions");

		if (!minimal) {
			permissionsEl.addElement("DeleteListItems");
			permissionsEl.addElement("EditListItems");
			permissionsEl.addElement("InsertListItems");
			permissionsEl.addElement("ManageRoles");
			permissionsEl.addElement("ManageWeb");
		}

		Element hasUniquePermEl = root.addElement("HasUniquePerm");

		hasUniquePermEl.setText(Boolean.TRUE.toString());

		Element workspaceTypeEl = root.addElement("WorkspaceType");

		workspaceTypeEl.setText("DWS");

		Element isADModeEl = root.addElement("IsADMode");

		isADModeEl.setText(Boolean.FALSE.toString());

		Element docUrlEl = root.addElement("DocUrl");

		docUrlEl.setText(documentName);

		Element minimalEl = root.addElement("Minimal");

		minimalEl.setText(Boolean.TRUE.toString());

		Element resultsEl = root.addElement("Results");

		Element titleElement = resultsEl.addElement("Title");

		titleElement.setText(group.getDescriptiveName());

		resultsEl.addElement("LastUpdate");

		HttpSession httpSession = httpServletRequest.getSession();

		User user = (User)httpSession.getAttribute(WebKeys.USER);

		ResponseElement responseElement = new MemberResponseElement(
			user, false);

		responseElement.addElement(resultsEl);

		Element membersEl = resultsEl.addElement("Members");

		List<User> users = UserLocalServiceUtil.getGroupUsers(
			group.getGroupId());

		for (User member : users) {
			responseElement = new MemberResponseElement(member, true);

			responseElement.addElement(membersEl);
		}

		if (minimal) {
			return doc.asXML();
		}

		Element assigneesEl = resultsEl.addElement("Assignees");

		for (User member : users) {
			responseElement = new MemberResponseElement(member, true);

			responseElement.addElement(assigneesEl);
		}

		Element listEl = resultsEl.addElement("List");

		listEl.addAttribute("Name", "Documents");

		listEl.addElement("ID");

		String parentFolderPath = path;

		int pos = parentFolderPath.lastIndexOf("/");

		if (pos != -1) {
			parentFolderPath = parentFolderPath.substring(0, pos);
		}

		SharepointStorage storage = SharepointUtil.getStorage(parentFolderPath);

		SharepointRequest sharepointRequest = new SharepointRequest(
			parentFolderPath);

		storage.addDocumentElements(sharepointRequest, listEl);

		return doc.asXML();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharepointDocumentWorkspaceServlet.class);

}