<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotEntry depotEntry = (DepotEntry)request.getAttribute(DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY);

ViewDesignLibraryResourcesDisplayContext viewDesignLibraryResourcesDisplayContext = new ViewDesignLibraryResourcesDisplayContext(depotEntry, request, liferayPortletResponse);
%>

<div>
	<div>
		<react:component
			module="{DesignLibraryBreadcrumb} from design-library-web"
			props="<%= viewDesignLibraryResourcesDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<c:choose>
		<c:when test="<%= viewDesignLibraryResourcesDisplayContext.hasContentAccess() %>">
			<div class="design-library-fds-wrapper design-library-fds-wrapper--resources">
				<frontend-data-set:headless-display
					additionalProps="<%= viewDesignLibraryResourcesDisplayContext.getFDSAdditionalProps() %>"
					apiURL="<%= viewDesignLibraryResourcesDisplayContext.getAPIURL() %>"
					emptyState="<%= viewDesignLibraryResourcesDisplayContext.getEmptyState() %>"
					fdsActionDropdownItems="<%= viewDesignLibraryResourcesDisplayContext.getFDSActionDropdownItems() %>"
					formName="fm"
					id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARY_RESOURCES %>"
					propsTransformer="{DesignLibraryResourcesFDSPropsTransformer} from design-library-web"
					selectedItemsKey="embedded.id"
					selectionType="multiple"
				/>
			</div>
		</c:when>
		<c:otherwise>
			<clay:alert
				displayType="info"
				message="you-do-not-have-access-to-any-content-in-this-design-library"
			/>
		</c:otherwise>
	</c:choose>
</div>