<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	redirect = currentURL;
}

SegmentsCompanyConfigurationDisplayContext segmentsCompanyConfigurationDisplayContext = (SegmentsCompanyConfigurationDisplayContext)request.getAttribute(SegmentsCompanyConfigurationDisplayContext.class.getName());
%>

<liferay-util:html-top
	outputKey="com.liferay.segments.web#/segments_configuration.jsp"
>
	<aui:link hashedFile="<%= true %>" href="segments-web/css/configuration.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<clay:sheet
	cssClass="segments-configuration"
	size="full"
>
	<h2>
		<liferay-ui:message key="segments-service-company-configuration-name" />

		<c:if test="<%= segmentsCompanyConfigurationDisplayContext.isSegmentsCompanyConfigurationDefined() %>">

				<%
				SegmentsCompanyConfigurationActionDropdownItemsProvider segmentsCompanyConfigurationActionDropdownItemsProvider = new SegmentsCompanyConfigurationActionDropdownItemsProvider(request, segmentsCompanyConfigurationDisplayContext);
				%>

				<div class="float-right">
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= segmentsCompanyConfigurationActionDropdownItemsProvider.getActionDropdownItems() %>"
					/>
				</div>

		</c:if>
	</h2>

	<aui:form action="<%= segmentsCompanyConfigurationDisplayContext.getBindConfigurationActionURL() %>" cssClass="mt-3" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

		<c:if test="<%= !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled() %>">
			<clay:alert
				cssClass="c-my-4"
				defaultTitleDisabled="<%= true %>"
				displayType="warning"
			>
				<strong><liferay-ui:message key="segmentation-is-disabled-in-system-settings" /></strong>

				<%
				String segmentsConfigurationURL = segmentsCompanyConfigurationDisplayContext.getSegmentsCompanyConfigurationURL();
				%>

				<c:choose>
					<c:when test="<%= segmentsConfigurationURL != null %>">
						<clay:link
							href="<%= segmentsConfigurationURL %>"
							label="to-enable,-go-to-system-settings"
						/>
					</c:when>
					<c:otherwise>
						<span><liferay-ui:message key="contact-your-system-administrator-to-enable-it" /></span>
					</c:otherwise>
				</c:choose>
			</clay:alert>
		</c:if>

		<c:if test="<%= !segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() %>">
			<clay:alert
				cssClass="c-my-4"
				defaultTitleDisabled="<%= true %>"
				displayType="warning"
			>
				<strong><liferay-ui:message key="assign-roles-by-segment-is-disabled-in-system-settings" /></strong>

				<%
				String segmentsConfigurationURL = segmentsCompanyConfigurationDisplayContext.getSegmentsCompanyConfigurationURL();
				%>

				<c:choose>
					<c:when test="<%= segmentsConfigurationURL != null %>">
						<clay:link
							href="<%= segmentsConfigurationURL %>"
							label="to-enable,-go-to-system-settings"
						/>
					</c:when>
					<c:otherwise>
				<span><%=
				LanguageUtil.get(
					request, "contact-your-system-administrator-to-enable-it") %></span>
					</c:otherwise>
				</c:choose>
			</clay:alert>
		</c:if>

		<c:if test="<%= !segmentsCompanyConfigurationDisplayContext.isSegmentsCompanyConfigurationDefined() %>">
			<clay:alert
				cssClass="c-mb-4"
				displayType="info"
				id="errorAlert"
				message="this-configuration-is-not-saved-yet.-the-values-shown-are-the-default"
			/>
		</c:if>

		<div class="row <%= (!segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() || !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled()) ? "c-mt-5" : "" %>">
			<div class="col-sm-12 form-group">
				<div class="form-group__inner">
					<c:choose>
						<c:when test="<%= segmentsCompanyConfigurationDisplayContext.isSegmentationChecked() || !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled() %>">
							<input disabled name='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>' type="hidden" value='false' />
						</c:when>
						<c:otherwise>
							<input name="<portlet:namespace />segmentationEnabled" type="hidden" value="false" />
						</c:otherwise>
					</c:choose>

					<clay:checkbox
						checked="<%= segmentsCompanyConfigurationDisplayContext.isSegmentationChecked() %>"
						disabled="<%= !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled() %>"
						id='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
						label="segmentation-enabled-name"
						name='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
					/>

					<div aria-hidden="true" class="form-feedback-group">
						<div class="form-text text-weight-normal"><liferay-ui:message key="segmentation-enabled-description" /></div>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-sm-12 form-group mb-0">
				<div class="form-group__inner">
					<c:choose>
						<c:when test="<%= segmentsCompanyConfigurationDisplayContext.isRoleSegmentationChecked() || !segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() %>">
							<input disabled name='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>' type="hidden" value='false' />
						</c:when>
						<c:otherwise>
							<input name="<portlet:namespace />roleSegmentationEnabled" type="hidden" value="false" />
						</c:otherwise>
					</c:choose>

					<clay:checkbox
						checked="<%= segmentsCompanyConfigurationDisplayContext.isRoleSegmentationChecked() %>"
						disabled="<%= !segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() %>"
						id='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
						label="role-segmentation-enabled-name"
						name='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
					/>

					<div aria-hidden="true" class="form-feedback-group">
						<div class="form-text text-weight-normal">
							<liferay-ui:message key="role-segmentation-enabled-description" />
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="sheet-footer">
			<div class="btn-group-item">
				<c:choose>
					<c:when test="<%= segmentsCompanyConfigurationDisplayContext.isSegmentsCompanyConfigurationDefined() %>">
						<clay:button
							cssClass="submit-btn"
							displayType="primary"
							id='<%= liferayPortletResponse.getNamespace() + "update" %>'
							label='<%= LanguageUtil.get(request, "update") %>'
							name='<%= liferayPortletResponse.getNamespace() + "update" %>'
							type="submit"
						/>
					</c:when>
					<c:otherwise>
						<clay:button
							cssClass="submit-btn"
							displayType="primary"
							id='<%= liferayPortletResponse.getNamespace() + "save" %>'
							label='<%= LanguageUtil.get(request, "save") %>'
							name='<%= liferayPortletResponse.getNamespace() + "save" %>'
							type="submit"
						/>
					</c:otherwise>
				</c:choose>

				<clay:link
					displayType="secondary"
					href="<%= redirect %>"
					id='<%= liferayPortletResponse.getNamespace() + "cancel" %>'
					label="cancel"
					type="button"
				/>
			</div>
		</div>
	</aui:form>
</clay:sheet>

<liferay-frontend:component
	module="{ConfigurationFormEventHandler} from segments-web"
/>