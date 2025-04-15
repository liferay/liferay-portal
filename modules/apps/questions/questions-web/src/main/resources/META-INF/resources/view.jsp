<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:renderURL var="basePortletURL" />

<div id="<%= liferayPortletResponse.getNamespace() %>-questions-root">

	<%
	QuestionsConfiguration questionsConfiguration = ConfigurationProviderUtil.getPortletInstanceConfiguration(QuestionsConfiguration.class, themeDisplay);

	long categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;

	if (!Validator.isBlank(questionsConfiguration.rootTopicExternalReferenceCode())) {
		MBCategory mbCategory = MBCategoryLocalServiceUtil.getMBCategoryByExternalReferenceCode(questionsConfiguration.rootTopicExternalReferenceCode(), themeDisplay.getScopeGroupId());

		categoryId = mbCategory.getCategoryId();
	}
	%>

	<react:component
		module="{Main} from questions-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"askQuestionButtonText", LocalizationUtil.getLocalization(questionsConfiguration.askQuestionButtonTextAsLocalizedXML(), themeDisplay.getLanguageId())
			).put(
				"companyName", renderRequest.getAttribute(QuestionsWebKeys.COMPANY_NAME)
			).put(
				"defaultRank", renderRequest.getAttribute(QuestionsWebKeys.DEFAULT_RANK)
			).put(
				"editQuestionPageTitle", LocalizationUtil.getLocalization(questionsConfiguration.editQuestionPageTitleAsLocalizedXML(), themeDisplay.getLanguageId())
			).put(
				"flagsProperties", renderRequest.getAttribute(QuestionsWebKeys.FLAGS_PROPERTIES)
			).put(
				"historyRouterBasePath", questionsConfiguration.historyRouterBasePath()
			).put(
				"i18nPath", renderRequest.getAttribute(WebKeys.I18N_PATH)
			).put(
				"imageBrowseURL", renderRequest.getAttribute(QuestionsWebKeys.IMAGE_BROWSE_URL)
			).put(
				"includeContextPath", renderRequest.getAttribute("jakarta.servlet.include.context_path")
			).put(
				"isContentReviewer", permissionChecker.isContentReviewer(themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())
			).put(
				"isOmniadmin", permissionChecker.isOmniadmin()
			).put(
				"newQuestionPageTitle", LocalizationUtil.getLocalization(questionsConfiguration.newQuestionPageTitleAsLocalizedXML(), themeDisplay.getLanguageId())
			).put(
				"npmResolvedPackageName", npmResolvedPackageName
			).put(
				"postYourQuestionButtonText", LocalizationUtil.getLocalization(questionsConfiguration.postYourQuestionButtonTextAsLocalizedXML(), themeDisplay.getLanguageId())
			).put(
				"redirectToLogin", questionsConfiguration.enableRedirectToLogin()
			).put(
				"rootTopicId", categoryId
			).put(
				"showCardsForTopicNavigation", questionsConfiguration.showCardsForTopicNavigation()
			).put(
				"siteKey", String.valueOf(themeDisplay.getScopeGroupId())
			).put(
				"tagSelectorURL", renderRequest.getAttribute(QuestionsWebKeys.TAG_SELECTOR_URL)
			).put(
				"trustedUser", renderRequest.getAttribute(QuestionsWebKeys.TRUSTED_USER)
			).put(
				"updateYourQuestionButtonText", LocalizationUtil.getLocalization(questionsConfiguration.updateYourQuestionButtonTextAsLocalizedXML(), themeDisplay.getLanguageId())
			).put(
				"userId", String.valueOf(themeDisplay.getUserId())
			).put(
				"useTopicNamesInURL", questionsConfiguration.useTopicNamesInURL()
			).build()
		%>'
	/>
</div>