<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<aui:fieldset>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

	<liferay-ui:error exception="<%= LocaleException.class %>">

		<%
		LocaleException le = (LocaleException)errorException;
		%>

		<c:if test="<%= le.getType() == LocaleException.TYPE_DISPLAY_SETTINGS %>">
			<liferay-ui:message key="please-enter-a-valid-locale" />
		</c:if>
	</liferay-ui:error>

	<liferay-ui:error exception="<%= RequiredLocaleException.class %>">

		<%
		RequiredLocaleException rle = (RequiredLocaleException)errorException;
		%>

		<liferay-ui:message arguments="<%= rle.getMessageArguments() %>" key="<%= rle.getMessageKey() %>" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<aui:select label="default-language" name="languageId">

		<%
		User guestUser = company.getGuestUser();

		String languageId = ParamUtil.getString(request, "languageId", guestUser.getLanguageId());

		Locale companyLocale = LocaleUtil.fromLanguageId(languageId);

		for (Locale availableLocale : LanguageUtil.getAvailableLocales()) {
		%>

			<aui:option label="<%= availableLocale.getDisplayName(locale) %>" lang="<%= LocaleUtil.toW3cLanguageId(availableLocale) %>" selected="<%= Objects.equals(companyLocale.getLanguage(), availableLocale.getLanguage()) && Objects.equals(companyLocale.getCountry(), availableLocale.getCountry()) %>" value="<%= LocaleUtil.toLanguageId(availableLocale) %>" />

		<%
		}
		%>

	</aui:select>

	<div id="<portlet:namespace />languageWarning"></div>

	<aui:fieldset cssClass="available-languages" label="available-languages">

		<%
		String[] availableLanguageIds = LocaleUtil.toLanguageIds(LanguageUtil.getAvailableLocales());
		%>

		<aui:input name='<%= "settings--" + PropsKeys.LOCALES + "--" %>' type="hidden" value="<%= StringUtil.merge(availableLanguageIds) %>" />

		<%

		// Left list

		List<KeyValuePair> leftList = new ArrayList<>();

		for (String propsValuesLanguageId : SetUtil.fromArray(PropsValues.LOCALES)) {
			if (!ArrayUtil.contains(availableLanguageIds, propsValuesLanguageId)) {
				Locale propsValuesLocale = LocaleUtil.fromLanguageId(propsValuesLanguageId, false);

				if (propsValuesLocale != null) {
					leftList.add(new KeyValuePair(propsValuesLanguageId, propsValuesLocale.getDisplayName(locale)));
				}
			}
		}

		leftList = ListUtil.sort(leftList, new KeyValuePairComparator(false, true));

		// Right list

		List<KeyValuePair> rightList = new ArrayList<>();

		String[] currentLanguageIds = ArrayUtil.unique(PrefsPropsUtil.getStringArray(company.getCompanyId(), PropsKeys.LOCALES, StringPool.COMMA, PropsValues.LOCALES_ENABLED));

		for (String currentLanguageId : currentLanguageIds) {
			Locale currentLocale = LocaleUtil.fromLanguageId(currentLanguageId);

			rightList.add(new KeyValuePair(currentLanguageId, currentLocale.getDisplayName(locale)));
		}
		%>

		<liferay-ui:input-move-boxes
			leftBoxName="availableLanguageIds"
			leftList="<%= leftList %>"
			leftTitle="available"
			rightBoxName="currentLanguageIds"
			rightList="<%= rightList %>"
			rightTitle="current"
		/>
	</aui:fieldset>
</aui:fieldset>

<aui:script use="aui-alert,aui-base">
	const languageSelectInput = document.getElementById(
		'<portlet:namespace />languageId'
	);

	if (languageSelectInput) {
		languageSelectInput.addEventListener('change', () => {
			new A.Alert({
				bodyContent:
					'<liferay-ui:message key="this-change-will-only-affect-the-newly-created-localized-content" />',
				boundingBox: '#<portlet:namespace />languageWarning',
				closeable: true,
				cssClass: 'alert-warning',
				destroyOnHide: false,
				render: true,
			});
		});
	}

	function <portlet:namespace />saveLocales() {

		// Wrapping in a timeout to deal with React's async rendering

		setTimeout(() => {
			var form = document.<portlet:namespace />fm;

			var currentLanguageIdsElement = Liferay.Util.getFormElement(
				form,
				'currentLanguageIds'
			);

			if (currentLanguageIdsElement) {
				Liferay.Util.setFormValues(form, {
					<%= PropsKeys.LOCALES %>: Liferay.Util.getSelectedOptionValues(
						currentLanguageIdsElement
					),
				});
			}
		});
	}

	Liferay.after(
		['form:registered', 'inputmoveboxes:moveItem', 'inputmoveboxes:orderItem'],
		<portlet:namespace />saveLocales
	);
</aui:script>