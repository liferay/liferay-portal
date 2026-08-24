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
	<c:choose>
		<c:when test='<%= FeatureFlagManagerUtil.isEnabled("LPD-74731") %>'>
			<div>
				<react:component
					module="{CollectionOrdering} from asset-list-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"initialOrderByColumn1", editAssetListDisplayContext.getOrderByColumn1()
						).put(
							"initialOrderByColumn2", editAssetListDisplayContext.getOrderByColumn2()
						).put(
							"initialOrderByType1", editAssetListDisplayContext.getOrderByType1()
						).put(
							"initialOrderByType2", editAssetListDisplayContext.getOrderByType2()
						).put(
							"namespace", liferayPortletResponse.getNamespace()
						).put(
							"properties", editAssetListDisplayContext.getTypePropertiesJSONArray()
						).build()
					%>'
				/>
			</div>
		</c:when>
		<c:otherwise>
			<clay:row
				id='<%= liferayPortletResponse.getNamespace() + "ordering" %>'
			>
				<clay:col
					md="6"
				>

					<%
					String orderByColumn1 = editAssetListDisplayContext.getOrderByColumn1();
					%>

					<div class="h5"><liferay-ui:message key="order-by" /></div>

					<aui:select aria-label='<%= LanguageUtil.get(request, "order-by") %>' label="" name="TypeSettingsProperties--orderByColumn1--" wrapperCssClass="d-inline-flex">
						<aui:option label="title" selected='<%= Objects.equals(orderByColumn1, "title") %>' value="title" />

						<aui:option label="create-date" selected='<%= Objects.equals(orderByColumn1, "createDate") %>' value="createDate" />

						<aui:option label="modified-date" selected='<%= Objects.equals(orderByColumn1, "modified") %>' value="modified" />

						<aui:option label="publish-date" selected='<%= Objects.equals(orderByColumn1, "publishDate") %>' value="publishDate" />

						<aui:option label="expiration-date" selected='<%= Objects.equals(orderByColumn1, "expirationDate") %>' value="expirationDate" />

						<aui:option label="priority" selected='<%= Objects.equals(orderByColumn1, "priority") %>' value="priority" />
					</aui:select>

					<%
					String orderByType1 = editAssetListDisplayContext.getOrderByType1();
					%>

					<div class="d-inline-flex order-by-type-container">
						<clay:button
							borderless="<%= true %>"
							cssClass='<%= StringUtil.equalsIgnoreCase(orderByType1, "DESC") ? "hide icon" : "icon" %>'
							displayType="secondary"
							icon="order-list-up"
							monospaced="<%= true %>"
							title="descending"
						/>

						<clay:button
							borderless="<%= true %>"
							cssClass='<%= StringUtil.equalsIgnoreCase(orderByType1, "ASC") ? "hide icon" : "icon" %>'
							displayType="secondary"
							icon="order-list-down"
							monospaced="<%= true %>"
							title="ascending"
						/>

						<aui:input cssClass="order-by-type-field" name="TypeSettingsProperties--orderByType1--" type="hidden" value="<%= orderByType1 %>" />
					</div>
				</clay:col>

				<clay:col
					md="6"
				>

					<%
					String orderByColumn2 = editAssetListDisplayContext.getOrderByColumn2();
					%>

					<div class="h5">
						<liferay-ui:message key="and-then-by" />
					</div>

					<aui:select aria-label='<%= LanguageUtil.get(request, "and-then-by") %>' label="" name="TypeSettingsProperties--orderByColumn2--" wrapperCssClass="d-inline-flex">
						<aui:option label="title" selected='<%= Objects.equals(orderByColumn2, "title") %>' value="title" />

						<aui:option label="create-date" selected='<%= Objects.equals(orderByColumn2, "createDate") %>' value="createDate" />

						<aui:option label="modified-date" selected='<%= Objects.equals(orderByColumn2, "modified") %>' value="modified" />

						<aui:option label="publish-date" selected='<%= Objects.equals(orderByColumn2, "publishDate") %>' value="publishDate" />

						<aui:option label="expiration-date" selected='<%= Objects.equals(orderByColumn2, "expirationDate") %>' value="expirationDate" />

						<aui:option label="priority" selected='<%= Objects.equals(orderByColumn2, "priority") %>' value="priority" />
					</aui:select>

					<%
					String orderByType2 = editAssetListDisplayContext.getOrderByType2();
					%>

					<div class="d-inline-flex order-by-type-container">
						<clay:button
							borderless="<%= true %>"
							cssClass='<%= StringUtil.equalsIgnoreCase(orderByType2, "DESC") ? "hide icon" : "icon" %>'
							displayType="secondary"
							icon="order-list-up"
							monospaced="<%= true %>"
							title="descending"
						/>

						<clay:button
							borderless="<%= true %>"
							cssClass='<%= StringUtil.equalsIgnoreCase(orderByType2, "ASC") ? "hide icon" : "icon" %>'
							displayType="secondary"
							icon="order-list-down"
							monospaced="<%= true %>"
							title="ascending"
						/>

						<aui:input cssClass="order-by-type-field" name="TypeSettingsProperties--orderByType2--" type="hidden" value="<%= orderByType2 %>" />
					</div>
				</clay:col>
			</clay:row>

			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"iconCssClass", ".icon"
					).put(
						"orderingContainerId", liferayPortletResponse.getNamespace() + "ordering"
					).build()
				%>'
				module="{Ordering} from asset-list-web"
			/>
		</c:otherwise>
	</c:choose>
</liferay-frontend:fieldset>