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
	<liferay-asset:asset-tags-error />

	<liferay-ui:error exception="<%= DuplicateQueryRuleException.class %>">

		<%
		DuplicateQueryRuleException dqre = (DuplicateQueryRuleException)errorException;

		String name = "categories";

		if (Objects.equals(dqre.getName(), "assetTags")) {
			name = "tags";
		}
		else if (Objects.equals(dqre.getName(), "keywords")) {
			name = "keywords";
		}
		%>

		<liferay-util:buffer
			var="messageArgument"
		>
			<em>(<liferay-ui:message key='<%= dqre.isContains() ? "contains" : "does-not-contain" %>' /> - <liferay-ui:message key='<%= dqre.isAndOperator() ? "all" : "any" %>' /> - <liferay-ui:message key="<%= name %>" />)</em>
		</liferay-util:buffer>

		<liferay-ui:message arguments="<%= messageArgument %>" key="only-one-rule-with-the-combination-x-is-supported" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<p><liferay-ui:message key="displayed-items-must-match-these-rules" /></p>

	<div id="<portlet:namespace />ConditionForm"></div>

	<c:choose>
		<c:when test='<%= FeatureFlagManagerUtil.isEnabled("LPD-74731") %>'>
			<div id="<portlet:namespace />collectionFilterBuilderWrapper">
				<react:component
					module="{CollectionFilterBuilder} from asset-list-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"categorySelectorURL", editAssetListDisplayContext.getCategorySelectorURL()
						).put(
							"groupIds", ListUtil.fromArray(editAssetListDisplayContext.getReferencedModelsGroupIds())
						).put(
							"initialConditions", editAssetListDisplayContext.getFilters()
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

			<fieldset id="<portlet:namespace />assetFilterBuilderWrapper">
				<react:component
					module="{AssetFilterBuilder} from asset-list-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"categorySelectorURL", editAssetListDisplayContext.getCategorySelectorURL()
						).put(
							"disabled", editAssetListDisplayContext.isLiveGroup()
						).put(
							"groupIds", ListUtil.fromArray(editAssetListDisplayContext.getReferencedModelsGroupIds())
						).put(
							"namespace", liferayPortletResponse.getNamespace()
						).put(
							"rules", editAssetListDisplayContext.getAutoFieldRulesJSONArray()
						).put(
							"tagSelectorURL", editAssetListDisplayContext.getTagSelectorURL()
						).put(
							"vocabularyIds", editAssetListDisplayContext.getVocabularyIds()
						).build()
					%>'
				/>
			</fieldset>

			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"namespace", liferayPortletResponse.getNamespace()
					).build()
				%>'
				module="{FilterVisibility} from asset-list-web"
			/>
		</c:when>
		<c:otherwise>
			<div>
				<react:component
					module="{AssetFilterBuilder} from asset-list-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"categorySelectorURL", editAssetListDisplayContext.getCategorySelectorURL()
						).put(
							"disabled", editAssetListDisplayContext.isLiveGroup()
						).put(
							"groupIds", ListUtil.fromArray(editAssetListDisplayContext.getReferencedModelsGroupIds())
						).put(
							"namespace", liferayPortletResponse.getNamespace()
						).put(
							"rules", editAssetListDisplayContext.getAutoFieldRulesJSONArray()
						).put(
							"tagSelectorURL", editAssetListDisplayContext.getTagSelectorURL()
						).put(
							"vocabularyIds", editAssetListDisplayContext.getVocabularyIds()
						).build()
					%>'
				/>
			</div>
		</c:otherwise>
	</c:choose>
</liferay-frontend:fieldset>