<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalArticle article = journalDisplayContext.getArticle();

JournalEditArticleDisplayContext journalEditArticleDisplayContext = (JournalEditArticleDisplayContext)request.getAttribute(JournalEditArticleDisplayContext.class.getName());

DDMStructure ddmStructure = journalEditArticleDisplayContext.getDDMStructure();
%>

<aui:input name="ddmStructureId" type="hidden" value="<%= ddmStructure.getStructureId() %>" />

<c:if test="<%= journalWebConfiguration.changeableDefaultLanguage() %>">
	<div id="<%= liferayPortletResponse.getNamespace() %>-change-default-language">
		<react:component
			module="{ChangeDefaultLanguage} from journal-web"
			props="<%= journalEditArticleDisplayContext.getChangeDefaultLanguageData() %>"
			servletContext="<%= application %>"
		/>
	</div>
</c:if>

<c:if test="<%= journalEditArticleDisplayContext.isShowSelectFolder() %>">
	<liferay-frontend:resource-selector
		inputLabel='<%= LanguageUtil.get(request, "folder") %>'
		inputName="newFolderId"
		modalTitle='<%= LanguageUtil.get(request, "select-folder") %>'
		resourceName="<%= journalEditArticleDisplayContext.getFolderName() %>"
		resourceValue="<%= String.valueOf(journalEditArticleDisplayContext.getFolderId()) %>"
		selectEventName="selectFolder"
		selectResourceURL='<%=
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCPath(
				"/select_folder.jsp"
			).setParameter(
				"folderId", journalEditArticleDisplayContext.getFolderId()
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		%>'
		showRemoveButton="<%= false %>"
	/>
</c:if>

<p class="article-structure">
	<b><liferay-ui:message key="structure" /></b>: <%= HtmlUtil.escape(ddmStructure.getName(locale)) %>
</p>

<p class="article-version-status <%= (article != null) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />articleVersionStatusWrapper">
	<b><liferay-ui:message key="version" /></b>: <span id="<portlet:namespace />displayedVersion"><%= (article != null) ? article.getVersion() : "" %></span>

	<c:if test="<%= article != null %>">
		<clay:label
			cssClass="ml-2 text-uppercase"
			displayType="<%= WorkflowConstants.getStatusStyle(article.getStatus()) %>"
			id='<%= liferayPortletResponse.getNamespace() + "statusLabel" %>'
			label="<%= WorkflowConstants.getStatusLabel(article.getStatus()) %>"
		/>
	</c:if>

	<clay:label
		cssClass="hide ml-2 text-uppercase"
		displayType="secondary"
		id='<%= liferayPortletResponse.getNamespace() + "statusDraftLabel" %>'
		label="<%= WorkflowConstants.LABEL_DRAFT %>"
	/>
</p>

<c:choose>
	<c:when test="<%= !journalWebConfiguration.journalArticleForceAutogenerateId() && (journalEditArticleDisplayContext.getClassNameId() == JournalArticleConstants.CLASS_NAME_ID_DEFAULT) %>">
		<div class="article-id">
			<label for="<portlet:namespace />newArticleId"><liferay-ui:message key="id" /></label>

			<aui:input label="" name="newArticleId" type="text" value="<%= (article != null) ? article.getArticleId() : StringPool.BLANK %>" wrapperCssClass="mb-1" />

			<%
			String taglibOnChange = "Liferay.Util.toggleDisabled('#" + liferayPortletResponse.getNamespace() + "newArticleId', event.target.checked);";
			%>

			<aui:input checked="<%= false %>" label="autogenerate-id" name="autoArticleId" onChange="<%= taglibOnChange %>" type="checkbox" value="<%= false %>" wrapperCssClass="mb-3" />
		</div>

		<aui:script>
			var autoArticleInput = document.getElementById(
				'<portlet:namespace />autoArticleId'
			);
			var newArticleInput = document.getElementById(
				'<portlet:namespace />newArticleId'
			);

			if (autoArticleInput && newArticleInput) {
				newArticleInput.disabled = autoArticleInput.checked;

				autoArticleInput.addEventListener('click', () => {
					Liferay.Util.toggleDisabled(newArticleInput, !newArticleInput.disabled);
				});
			}
		</aui:script>
	</c:when>
	<c:otherwise>
		<aui:input name="newArticleId" type="hidden" />
		<aui:input name="autoArticleId" type="hidden" value="<%= true %>" />

		<p class="article-id <%= (article != null) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />articleIdWrapper">
			<b><liferay-ui:message key="id" /></b>: <span id="<portlet:namespace />displayedArticleId"><%= (article != null) ? article.getArticleId() : "" %></span>
		</p>
	</c:otherwise>
</c:choose>