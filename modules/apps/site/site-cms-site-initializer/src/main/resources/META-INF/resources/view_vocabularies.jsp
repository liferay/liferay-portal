<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewVocabulariesDisplayContext viewVocabulariesDisplayContext = (ViewVocabulariesDisplayContext)request.getAttribute(ViewVocabulariesDisplayContext.class.getName());
%>

<div class="cms-section">
	<div>
		<react:component
			module="{CategorizationToolbar} from site-cms-site-initializer"
			props="<%= viewVocabulariesDisplayContext.getReactData() %>"
		/>
	</div>

	<frontend-data-set:headless-display
		apiURL="<%= viewVocabulariesDisplayContext.getAPIURL() %>"
		creationMenu="<%= viewVocabulariesDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewVocabulariesDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewVocabulariesDisplayContext.getFDSActionDropdownItems() %>"
		id="<%= CMSSiteInitializerFDSNames.VOCABULARIES %>"
		propsTransformer="{VocabularyFDSPropsTransformer} from site-cms-site-initializer"
	/>
</div>