<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/image_selector/init.jsp" %>

<%
boolean draggable = GetterUtil.getBoolean(request.getAttribute("liferay-ui:image-selector:draggable"));
long fileEntryId = GetterUtil.getLong(request.getAttribute("liferay-ui:image-selector:fileEntryId"));
String imageCropDirection = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:imageCropDirection"), "none");
String imageCropRegion = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:imageCropRegion"));
String imageURL = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:imageURL"));
String itemSelectorEventName = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:itemSelectorEventName"));
String itemSelectorURL = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:itemSelectorURL"));
long maxFileSize = GetterUtil.getLong(request.getAttribute("liferay-ui:image-selector:maxFileSize"));
String paramName = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:paramName"));
String uploadURL = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:uploadURL"));
String validExtensions = GetterUtil.getString((String)request.getAttribute("liferay-ui:image-selector:validExtensions"));

String cssClass = "drop-zone taglib-image-selector";

if (fileEntryId == 0) {
	cssClass += " drop-enabled";
}

if (draggable) {
	cssClass += " draggable-image " + imageCropDirection;
}
%>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="item-selector-taglib/css/image_selector.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div>
	<react:component
		module="{ImageSelector} from item-selector-taglib"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"fileEntryId", fileEntryId
			).put(
				"imageCropDirection", imageCropDirection
			).put(
				"imageCropRegion", imageCropRegion
			).put(
				"imageURL", imageURL
			).put(
				"isDraggable", draggable
			).put(
				"itemSelectorEventName", itemSelectorEventName
			).put(
				"itemSelectorURL", itemSelectorURL
			).put(
				"maxFileSize", maxFileSize
			).put(
				"paramName", paramName
			).put(
				"uploadURL", uploadURL
			).put(
				"validExtensions", validExtensions
			).build()
		%>'
	/>

	<div class="<%= cssClass %>">
		<aui:input name='<%= paramName + "Id" %>' type="hidden" value="<%= fileEntryId %>" />
		<aui:input name='<%= paramName + "CropRegion" %>' type="hidden" value="<%= imageCropRegion %>" />

		<c:if test="<%= Validator.isNotNull(imageURL) %>">
			<div class="image-wrapper">
				<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="current-image" />" class="current-image <%= Validator.isNull(imageURL) ? "hide" : StringPool.BLANK %>" src="<%= HtmlUtil.escape(Validator.isNotNull(imageURL) ? imageURL : StringPool.BLANK) %>" />
			</div>
		</c:if>

		<c:if test="<%= fileEntryId == 0 %>">
			<div class="browse-image-controls <%= (fileEntryId != 0) ? "hide" : StringPool.BLANK %>">
				<div class="drag-drop-label">
					<c:choose>
						<c:when test="<%= Validator.isNotNull(itemSelectorEventName) && Validator.isNotNull(itemSelectorURL) %>">
							<liferay-util:buffer
								var="selectFileButton"
							>
								<button class="btn btn-secondary" disabled><liferay-ui:message key="select-file" /></button>
							</liferay-util:buffer>

							<c:choose>
								<c:when test="<%= BrowserSnifferUtil.isMobile(request) %>">
									<%= selectFileButton %>
								</c:when>
								<c:otherwise>
									<span class="pr-1">
										<liferay-ui:message arguments="<%= selectFileButton %>" key="drag-and-drop-to-upload-or-x" />
									</span>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="drag-and-drop-to-upload" />
						</c:otherwise>
					</c:choose>
				</div>

				<div class="file-validation-info">
					<c:if test="<%= Validator.isNotNull(validExtensions) %>">
						<strong><%= HtmlUtil.escape(validExtensions) %></strong>
					</c:if>

					<c:if test="<%= maxFileSize != 0 %>">
						<span class="pl-1">
							<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(maxFileSize, locale) %>" key="maximum-size-x" />
						</span>
					</c:if>
				</div>
			</div>
		</c:if>

		<span class="selection-status">
			<clay:icon
				symbol="check"
			/>
		</span>

		<c:if test="<%= fileEntryId != 0 %>">
			<div class="change-image-controls">
				<clay:button
					cssClass="browse-image"
					displayType="secondary"
					icon="picture"
					title="change-image"
				/>

				<clay:button
					cssClass="ml-1"
					displayType="secondary"
					icon="trash"
					monospaced="<%= true %>"
					title="remove-image"
				/>
			</div>
		</c:if>
	</div>
</div>