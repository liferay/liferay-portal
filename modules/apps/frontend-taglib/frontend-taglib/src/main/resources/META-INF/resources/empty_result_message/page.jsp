<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/empty_result_message/init.jsp" %>

<div class="c-empty-state c-empty-state-animation taglib-empty-result-message">
	<div class="c-empty-state-image <%= animationTypeCssClass %>">
		<c:if test="<%= Validator.isNotNull(animationTypeSymbol) %>">
			<div class="c-empty-state-illustration">
				<svg aria-hidden="true" class="c-empty-state-icon c-empty-state-icon-<%= animationTypeSymbol %>" focusable="false" viewBox="0 0 256 256">
					<use xlink:href="<%= themeDisplay.getPathThemeEmptyStatesSpritemap() %>#<%= animationTypeSymbol %>"></use>
				</svg>
			</div>
		</c:if>
	</div>

	<div class="c-empty-state-title">
		<c:choose>
			<c:when test="<%= Validator.isNull(title) %>">
				<liferay-ui:message arguments="<%= elementType %>" key="no-x-yet" translateArguments="<%= false %>" />
			</c:when>
			<c:otherwise>
				<%= title %>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="<%= Validator.isNotNull(description) %>">
		<div class="c-empty-state-text">
			<%= description %>
		</div>
	</c:if>

	<c:if test="<%= ListUtil.isNotEmpty(actionDropdownItems) %>">
		<div class="c-empty-state-footer">
			<c:choose>
				<c:when test="<%= actionDropdownItems.size() > 1 %>">
					<clay:dropdown-menu
						additionalProps="<%= additionalProps %>"
						displayType="<%= buttonCssClass %>"
						dropdownItems="<%= actionDropdownItems %>"
						label="new"
						propsTransformer="<%= propsTransformer %>"
						propsTransformerServletContext="<%= propsTransformerServletContext %>"
					/>
				</c:when>
				<c:otherwise>

					<%
					DropdownItem actionDropdownItem = actionDropdownItems.get(0);
					%>

					<c:choose>
						<c:when test='<%= Validator.isNotNull(actionDropdownItem.get("href")) %>'>
							<clay:link
								displayType="<%= buttonCssClass %>"
								href='<%= String.valueOf(actionDropdownItem.get("href")) %>'
								label='<%= String.valueOf(actionDropdownItem.get("label")) %>'
								type="button"
							/>
						</c:when>
						<c:otherwise>
							<clay:button
								additionalProps='<%= (HashMap)actionDropdownItem.get("data") %>'
								displayType="<%= buttonCssClass %>"
								label='<%= String.valueOf(actionDropdownItem.get("label")) %>'
								propsTransformer="<%= buttonPropsTransformer %>"
								propsTransformerServletContext="<%= propsTransformerServletContext %>"
							/>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
</div>