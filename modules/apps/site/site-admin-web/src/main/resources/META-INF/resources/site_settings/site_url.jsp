<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
Group liveGroup = (Group)request.getAttribute("site.liveGroup");
long liveGroupId = (long)request.getAttribute("site.liveGroupId");
Group stagingGroup = (Group)request.getAttribute("site.stagingGroup");
long stagingGroupId = (long)request.getAttribute("site.stagingGroupId");

LayoutSet publicLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(liveGroupId, false);

Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(liveGroupId);

NavigableMap<String, String> publicVirtualHostnames = publicLayoutSet.getVirtualHostnames();

if (publicVirtualHostnames.isEmpty()) {
	publicVirtualHostnames = TreeMapBuilder.put(
		StringPool.BLANK, StringPool.BLANK
	).build();
}
%>

<aui:model-context bean="<%= liveGroup %>" model="<%= Group.class %>" />

<liferay-ui:error exception="<%= AvailableLocaleException.class %>" message="please-select-a-valid-language-for-each-virtual-host" />

<liferay-ui:error exception="<%= GroupFriendlyURLException.class %>">

	<%
	GroupFriendlyURLException gfurle = (GroupFriendlyURLException)errorException;
	%>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.ADJACENT_SLASHES %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-does-not-have-adjacent-slashes" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.DOES_NOT_START_WITH_SLASH %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-begins-with-a-slash" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.DUPLICATE %>">

		<%
		long duplicateClassPK = gfurle.getDuplicateClassPK();
		String duplicateClassName = gfurle.getDuplicateClassName();

		String name = StringPool.BLANK;

		if (duplicateClassName.equals(Group.class.getName())) {
			Group duplicateGroup = GroupLocalServiceUtil.getGroup(duplicateClassPK);

			name = duplicateGroup.getDescriptiveName(locale);
		}
		else if (duplicateClassName.equals(Layout.class.getName())) {
			Layout duplicateLayout = LayoutLocalServiceUtil.getLayout(duplicateClassPK);

			name = duplicateLayout.getName(locale);
		}
		%>

		<liferay-ui:message arguments="<%= new Object[] {ResourceActionsUtil.getModelResource(locale, duplicateClassName), name} %>" key="please-enter-a-unique-friendly-url.-x-and-x-has-the-same-friendly-url" translateArguments="<%= false %>" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.ENDS_WITH_DASH %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-does-not-end-with-a-dash" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.ENDS_WITH_SLASH %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-does-not-end-with-a-slash" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.INVALID_CHARACTERS %>">
		<liferay-ui:message key="please-enter-a-friendly-url-with-valid-characters" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.KEYWORD_CONFLICT %>">
		<liferay-ui:message arguments="<%= gfurle.getKeywordConflict() %>" key="please-enter-a-friendly-url-that-does-not-conflict-with-the-keyword-x" translateArguments="<%= false %>" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.POSSIBLE_DUPLICATE %>">
		<liferay-ui:message key="the-friendly-url-may-conflict-with-another-page" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.TOO_DEEP %>">
		<liferay-ui:message key="the-friendly-url-has-too-many-slashes" />
	</c:if>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.TOO_SHORT %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-is-at-least-two-characters-long" />
	</c:if>
</liferay-ui:error>

<liferay-ui:error exception="<%= LayoutSetVirtualHostException.class %>">
	<liferay-ui:message key="please-enter-a-unique-virtual-host" />

	<liferay-ui:message key="virtual-hosts-must-be-valid-domain-names" />
</liferay-ui:error>

<aui:fieldset>
	<p class="small text-secondary">
		<liferay-ui:message key="enter-the-friendly-url-that-is-used-by-pages" />

		<liferay-ui:message arguments='<%= new Object[] {"<strong>" + themeDisplay.getPortalURL() + themeDisplay.getPathFriendlyURLPublic() + "</strong>"} %>' key="the-friendly-url-is-appended-to-x-for-pages" translateArguments="<%= false %>" />
	</p>

	<aui:input label="friendly-url" name="groupFriendlyURL" type="text" value="<%= HttpComponentsUtil.decodeURL(liveGroup.getFriendlyURL()) %>" />

	<c:if test="<%= liveGroup.hasStagingGroup() %>">
		<aui:input label="staging-friendly-url" name="stagingFriendlyURL" type="text" value="<%= HttpComponentsUtil.decodeURL(stagingGroup.getFriendlyURL()) %>" />
	</c:if>

	<p class="small text-secondary">
		<liferay-ui:message key="enter-the-virtual-host-that-map-to-the-friendly-url" />

		<liferay-ui:message arguments='<%= new Object[] {"<strong>www.helloworld.com</strong>", "<strong>/helloworld</strong>", "<strong>" + HttpComponentsUtil.getProtocol(request) + "://www.helloworld.com</strong>", "<strong>" + themeDisplay.getPortalURL() + themeDisplay.getPathFriendlyURLPublic() + "/helloworld</strong>"} %>' key="for-example,-if-the-virtual-host-is-x-and-the-friendly-url-is-x,-then-x-is-mapped-to-x" translateArguments="<%= false %>" />
	</p>

	<div class="mb-5" id="<portlet:namespace />publicVirtualHostFields">
		<div class="sheet-subtitle"><liferay-ui:message key="pages" /></div>

		<%
		for (Map.Entry<String, String> entry : publicVirtualHostnames.entrySet()) {
			String virtualHostname = entry.getKey();

			String virtualHostLanguageId = Validator.isNotNull(entry.getValue()) ? entry.getValue() : StringPool.BLANK;
		%>

			<clay:container-fluid
				cssClass="lfr-form-row"
				fullWidth="<%= true %>"
			>
				<clay:row>
					<aui:input inlineField="<%= true %>" label="virtual-host" maxlength="200" name="publicVirtualHostname[]" placeholder="virtual-host" type="text" value="<%= virtualHostname %>" wrapperCssClass="col-sm-6" />

					<aui:select inlineField="<%= true %>" label="language" name="publicVirtualHostLanguageId[]" wrapperCssClass="col-sm-6">
						<aui:option label="default-language" value="" />

						<%
						for (Locale localeEntry : availableLocales) {
							String languageId = LocaleUtil.toLanguageId(localeEntry);
						%>

							<aui:option label="<%= localeEntry.getDisplayName(themeDisplay.getLocale()) %>" selected="<%= languageId.equals(virtualHostLanguageId) %>" value="<%= languageId %>" />

						<%
						}
						%>

					</aui:select>
				</clay:row>
			</clay:container-fluid>

		<%
		}
		%>

	</div>

	<c:if test="<%= liveGroup.hasStagingGroup() %>">

		<%
		LayoutSet stagingPublicLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(stagingGroupId, false);

		NavigableMap<String, String> stagingPublicVirtualHostnames = stagingPublicLayoutSet.getVirtualHostnames();

		if (stagingPublicVirtualHostnames.isEmpty()) {
			stagingPublicVirtualHostnames = TreeMapBuilder.put(
				StringPool.BLANK, StringPool.BLANK
			).build();
		}
		%>

		<div id="<portlet:namespace />stagingPublicVirtualHostFields">

			<%
			for (Map.Entry<String, String> entry : stagingPublicVirtualHostnames.entrySet()) {
				String virtualHostname = entry.getKey();

				String virtualHostLanguageId = Validator.isNotNull(entry.getValue()) ? entry.getValue() : StringPool.BLANK;
			%>

				<clay:container-fluid
					cssClass="lfr-form-row"
					fullWidth="<%= true %>"
				>
					<clay:row>
						<aui:input inlineField="<%= true %>" label="staging-pages" maxlength="200" name="stagingPublicVirtualHostname[]" placeholder="virtual-host" type="text" value="<%= virtualHostname %>" wrapperCssClass="col-sm-6" />

						<aui:select inlineField="<%= true %>" label="language" name="stagingPublicVirtualHostLanguageId[]" wrapperCssClass="col-sm-6">
							<aui:option label="default-language" value="" />

							<%
							for (Locale localeEntry : availableLocales) {
								String languageId = LocaleUtil.toLanguageId(localeEntry);
							%>

								<aui:option label="<%= localeEntry.getDisplayName(themeDisplay.getLocale()) %>" selected="<%= languageId.equals(virtualHostLanguageId) %>" value="<%= languageId %>" />

							<%
							}
							%>

						</aui:select>
					</clay:row>
				</clay:container-fluid>

			<%
			}
			%>

		</div>
	</c:if>
</aui:fieldset>

<aui:script use="liferay-auto-fields">
	new Liferay.AutoFields({
		contentBox: '#<portlet:namespace />publicVirtualHostFields',
		namespace: '<portlet:namespace />',
	}).render();

	<c:if test="<%= liveGroup.hasStagingGroup() %>">
		new Liferay.AutoFields({
			contentBox: '#<portlet:namespace />stagingPublicVirtualHostFields',
			namespace: '<portlet:namespace />',
		}).render();
	</c:if>
</aui:script>

<aui:script>
	var friendlyURL = document.getElementById(
		'<portlet:namespace />groupFriendlyURL'
	);

	if (friendlyURL) {
		friendlyURL.addEventListener('change', (event) => {
			var value = friendlyURL.value.trim();

			if (value == '/') {
				value = '';
			}
			else {
				value = value.replace(/^[^\/]|\/$/g, (match, index) => {
					var str = '';

					if (index == 0) {
						str = '/' + match;
					}

					return str;
				});
			}

			friendlyURL.value = value;
		});
	}
</aui:script>