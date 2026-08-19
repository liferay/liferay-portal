<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test="<%= RoleLocalServiceUtil.hasUserRole(user.getUserId(), company.getCompanyId(), RoleConstants.ADMINISTRATOR, true) %>">

		<%
		String className = (String)request.getAttribute("websites.className");
		long classPK = (Long)request.getAttribute("websites.classPK");

		List<Website> websites = Collections.emptyList();

		int[] websitesIndexes = null;

		String websitesIndexesParam = ParamUtil.getString(request, "websitesIndexes");

		if (Validator.isNotNull(websitesIndexesParam)) {
			websites = new ArrayList<Website>();

			websitesIndexes = StringUtil.split(websitesIndexesParam, 0);

			for (int websitesIndex : websitesIndexes) {
				websites.add(new WebsiteImpl());
			}
		}
		else {
			if (classPK > 0) {
				websites = WebsiteServiceUtil.getWebsites(className, classPK);

				websitesIndexes = new int[websites.size()];

				for (int i = 0; i < websites.size(); i++) {
					websitesIndexes[i] = i;
				}
			}

			if (websites.isEmpty()) {
				websites = new ArrayList<Website>();

				websites.add(new WebsiteImpl());

				websitesIndexes = new int[] {0};
			}

			if (websitesIndexes == null) {
				websitesIndexes = new int[0];
			}
		}
		%>

		<liferay-ui:error-marker
			key="<%= WebKeys.ERROR_SECTION %>"
			value="websites"
		/>

		<div class="alert alert-info">
			<liferay-ui:message key="url-and-type-are-required-fields.-websites-must-start-with-http-or-https" />
		</div>

		<liferay-ui:error key="<%= NoSuchListTypeException.class.getName() + className + ListTypeConstants.WEBSITE %>" message="please-select-a-type" />
		<liferay-ui:error exception="<%= WebsiteURLException.class %>" message="please-enter-a-valid-url" />

		<aui:fieldset id='<%= liferayPortletResponse.getNamespace() + "websites" %>'>

			<%
			for (int i = 0; i < websitesIndexes.length; i++) {
				int websitesIndex = websitesIndexes[i];

				Website website = websites.get(i);
			%>

				<aui:model-context bean="<%= website %>" model="<%= Website.class %>" />

				<div class="form-group-autofit lfr-form-row">
					<aui:input name='<%= "websiteId" + websitesIndex %>' type="hidden" value="<%= website.getWebsiteId() %>" />

					<div class="form-group-item">
						<aui:input cssClass="url-field" fieldParam='<%= "websiteUrl" + websitesIndex %>' id='<%= "websiteUrl" + websitesIndex %>' inlineField="<%= true %>" name="url" />
					</div>

					<div class="form-group-item">
						<aui:select inlineField="<%= true %>" label="type" listType="<%= className + ListTypeConstants.WEBSITE %>" listTypeFieldName="listTypeId" name='<%= "websiteListTypeId" + websitesIndex %>' />
					</div>

					<div class="form-group-item form-group-item-label-spacer">
						<aui:input checked="<%= website.isPrimary() %>" cssClass="primary-ctrl" id='<%= "websitePrimary" + websitesIndex %>' inlineField="<%= true %>" label="primary" name="websitePrimary" type="radio" value="<%= websitesIndex %>" />
					</div>
				</div>

			<%
			}
			%>

			<aui:input name="websitesIndexes" type="hidden" value="<%= StringUtil.merge(websitesIndexes) %>" />
		</aui:fieldset>

		<aui:script type="module">
			import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

			autoFields({
				contentBox: '#<portlet:namespace />websites',
				fieldIndexes: '<portlet:namespace />websitesIndexes',
				namespace: '<portlet:namespace />',
			});
		</aui:script>
	</c:when>
	<c:otherwise>
		<div class="alert alert-info">
			<liferay-ui:message key="you-do-not-have-the-required-permissions-to-access-this-content" />
		</div>
	</c:otherwise>
</c:choose>