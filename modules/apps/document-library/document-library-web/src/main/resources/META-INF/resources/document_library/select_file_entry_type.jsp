<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
long fileEntryTypeId = ParamUtil.getLong(request, "fileEntryTypeId");
String eventName = ParamUtil.getString(request, "eventName", liferayPortletResponse.getNamespace() + "selectFileEntryType");

long[] groupIds = PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId);

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/document_library/select_file_entry_type.jsp"
).setParameter(
	"eventName", eventName
).buildPortletURL();
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(true);
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "document-types"));
					});
			}
		}
	%>'
/>

<aui:form action="<%= portletURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="selectFileEntryTypeFm">
	<liferay-ui:search-container
		iteratorURL="<%= portletURL %>"
	>
		<liferay-ui:search-container-results
			results="<%= DLFileEntryTypeServiceUtil.getFileEntryTypes(groupIds) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.document.library.kernel.model.DLFileEntryType"
			cssClass="select-action"
			modelVar="fileEntryType"
		>

			<%
			if (fileEntryTypeId == fileEntryType.getFileEntryTypeId()) {
				row.setCssClass("select-action active");
			}

			row.setData(
				HashMapBuilder.<String, Object>put(
					"fileEntryTypeId", fileEntryType.getFileEntryTypeId()
				).build());
			%>

			<liferay-ui:search-container-column-icon
				icon="edit-layout"
			/>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
			>
				<div class="h5">
					<aui:a href="#">
						<%= HtmlUtil.escape(fileEntryType.getName(locale)) %>
					</aui:a>
				</div>

				<div class="h6 text-default">
					<span><%= HtmlUtil.escape(fileEntryType.getDescription(locale)) %></span>
				</div>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="descriptive"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<aui:script use="aui-base">
	var form = A.one('#<portlet:namespace />selectFileEntryTypeFm');

	form.delegate(
		'click',
		(event) => {
			event.preventDefault();

			var currentTarget = event.currentTarget;

			A.all('.select-action').removeClass('active');

			currentTarget.addClass('active');

			Liferay.Util.getOpener().Liferay.fire(
				'<%= HtmlUtil.escapeJS(eventName) %>',
				{
					data: {
						value: currentTarget.attr('data-fileEntryTypeId'),
					},
				}
			);
		},
		'.select-action'
	);
</aui:script>