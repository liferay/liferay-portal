<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<label class="control-label <%= fragmentCollectionFilterDateDisplayContext.isShowLabel() ? "" : "sr-only" %>" for="<portlet:namespace />dateInput">
	<%= HtmlUtil.escape(fragmentCollectionFilterDateDisplayContext.getLabel()) %>
</label>

<div className="date-picker">
	<div class="input-group">
		<div class="input-group-item">
			<input name="datePicker" type="hidden" value="" />

			<input class="form-control input-group-inset input-group-inset-after" id="<portlet:namespace />dateInput" placeholder="YYYY-MM-DD" type="text" value="" />

			<div class="input-group-inset-item input-group-inset-item-after">
				<clay:button
					displayType="unstyled"
					icon="date"
				/>
			</div>
		</div>
	</div>

	<react:component
		module="{FragmentCollectionFilterDate} from fragment-collection-filter-date"
		props="<%= fragmentCollectionFilterDateDisplayContext.getProps() %>"
	/>
</div>