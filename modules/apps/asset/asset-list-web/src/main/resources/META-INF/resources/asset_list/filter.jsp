<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-frontend:fieldset
	disabled="<%= editAssetListDisplayContext.isLiveGroup() %>"
>
	<p><liferay-ui:message key="displayed-items-must-match-these-rules" /></p>

	<div>
		<react:component
			module="{CollectionFilterBuilder} from asset-list-web"
			props='<%=
				HashMapBuilder.<String, Object>put(
					"groupIds", ListUtil.fromArray(editAssetListDisplayContext.getReferencedModelsGroupIds())
				).put(
					"initialConditions", editAssetListDisplayContext.getFilterMaps()
				).put(
					"namespace", liferayPortletResponse.getNamespace()
				).put(
					"properties", editAssetListDisplayContext.getTypePropertiesJSONArray()
				).put(
					"tagSelectorURL", editAssetListDisplayContext.getTagSelectorURL()
				).put(
					"vocabularyIds", editAssetListDisplayContext.getVocabularyIds()
				).build()
			%>'
		/>
	</div>
</liferay-frontend:fieldset>