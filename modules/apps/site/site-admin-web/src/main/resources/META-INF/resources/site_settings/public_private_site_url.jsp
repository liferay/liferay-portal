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
LayoutSet privateLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(liveGroupId, true);

Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(liveGroupId);

NavigableMap<String, String> publicVirtualHostnames = publicLayoutSet.getVirtualHostnames();

if (publicVirtualHostnames.isEmpty()) {
	publicVirtualHostnames = TreeMapBuilder.put(
		StringPool.BLANK, StringPool.BLANK
	).build();
}

NavigableMap<String, String> privateVirtualHostnames = privateLayoutSet.getVirtualHostnames();

if (privateVirtualHostnames.isEmpty()) {
	privateVirtualHostnames = TreeMapBuilder.put(
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
		<liferay-ui:message key="enter-the-friendly-url-that-is-used-by-both-public-and-private-pages" />

		<liferay-ui:message arguments="<%= new Object[] {themeDisplay.getPortalURL() + themeDisplay.getPathFriendlyURLPublic(), themeDisplay.getPortalURL() + themeDisplay.getPathFriendlyURLPrivateGroup()} %>" key="the-friendly-url-is-appended-to-x-for-public-pages-and-x-for-private-pages" translateArguments="<%= false %>" />
	</p>

	<aui:input label="friendly-url" name="groupFriendlyURL" type="text" value="<%= HttpComponentsUtil.decodeURL(liveGroup.getFriendlyURL()) %>" />

	<c:if test="<%= liveGroup.hasStagingGroup() %>">
		<aui:input label="staging-friendly-url" name="stagingFriendlyURL" type="text" value="<%= HttpComponentsUtil.decodeURL(stagingGroup.getFriendlyURL()) %>" />
	</c:if>

	<p class="small text-secondary">
		<liferay-ui:message key="enter-the-public-and-private-virtual-host-that-map-to-the-public-and-private-friendly-url" />

		<liferay-ui:message arguments="<%= new Object[] {HttpComponentsUtil.getProtocol(request), themeDisplay.getPortalURL() + themeDisplay.getPathFriendlyURLPublic()} %>" key="for-example,-if-the-public-virtual-host-is-www.helloworld.com-and-the-friendly-url-is-/helloworld" translateArguments="<%= false %>" />
	</p>

	<div class="mb-5" id="<portlet:namespace />publicVirtualHostFields">
		<div class="sheet-subtitle"><liferay-ui:message key="public-pages" /></div>

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
					<aui:input inlineField="<%= true %>" label="public-pages-virtual-host" maxlength="200" name="publicVirtualHostname[]" placeholder="virtual-host" type="text" value="<%= virtualHostname %>" wrapperCssClass="col-sm-6" />

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

	<div id="<portlet:namespace />privateVirtualHostFields">
		<div class="sheet-subtitle"><liferay-ui:message key="private-pages" /></div>

		<%
		for (Map.Entry<String, String> entry : privateVirtualHostnames.entrySet()) {
			String virtualHostname = entry.getKey();

			String virtualHostLanguageId = Validator.isNotNull(entry.getValue()) ? entry.getValue() : StringPool.BLANK;
		%>

			<clay:container-fluid
				cssClass="lfr-form-row"
				fullWidth="<%= true %>"
			>
				<clay:row>
					<aui:input inlineField="<%= true %>" label="private-pages-virtual-host" maxlength="200" name="privateVirtualHostname[]" placeholder="virtual-host" type="text" value="<%= virtualHostname %>" wrapperCssClass="col-sm-6" />

					<aui:select inlineField="<%= true %>" label="language" name="privateVirtualHostLanguageId[]" wrapperCssClass="col-sm-6">
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
						<aui:input inlineField="<%= true %>" label="staging-public-pages" maxlength="200" name="stagingPublicVirtualHostname[]" placeholder="virtual-host" type="text" value="<%= virtualHostname %>" wrapperCssClass="col-sm-6" />

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

		<%
		LayoutSet stagingPrivateLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(stagingGroupId, true);

		NavigableMap<String, String> stagingPrivateVirtualHostnames = stagingPrivateLayoutSet.getVirtualHostnames();

		if (stagingPrivateVirtualHostnames.isEmpty()) {
			stagingPrivateVirtualHostnames = TreeMapBuilder.put(
				StringPool.BLANK, StringPool.BLANK
			).build();
		}
		%>

		<div id="<portlet:namespace />stagingPrivateVirtualHostFields">

			<%
			for (Map.Entry<String, String> entry : stagingPrivateVirtualHostnames.entrySet()) {
				String virtualHostname = entry.getKey();

				String virtualHostLanguageId = Validator.isNotNull(entry.getValue()) ? entry.getValue() : StringPool.BLANK;
			%>

				<clay:container-fluid
					cssClass="lfr-form-row"
					fullWidth="<%= true %>"
				>
					<clay:row>
						<aui:input inlineField="<%= true %>" label="staging-private-pages" maxlength="200" name="stagingPrivateVirtualHostname[]" placeholder="virtual-host" type="text" value="<%= virtualHostname %>" wrapperCssClass="col-sm-6" />

						<aui:select inlineField="<%= true %>" label="language" name="stagingPrivateVirtualHostLanguageId[]" wrapperCssClass="col-sm-6">
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

	new Liferay.AutoFields({
		contentBox: '#<portlet:namespace />privateVirtualHostFields',
		namespace: '<portlet:namespace />',
	}).render();

	<c:if test="<%= liveGroup.hasStagingGroup() %>">
		new Liferay.AutoFields({
			contentBox: '#<portlet:namespace />stagingPublicVirtualHostFields',
			namespace: '<portlet:namespace />',
		}).render();

		new Liferay.AutoFields({
			contentBox: '#<portlet:namespace />stagingPrivateVirtualHostFields',
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