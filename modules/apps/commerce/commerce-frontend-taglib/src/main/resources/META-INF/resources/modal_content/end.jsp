<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/modal_content/init.jsp" %>

	</div>

	<c:if test="<%= Validator.isNotNull(submitButtonLabel) || showCancelButton || showSubmitButton %>">
		<div class="bg-white modal-footer modal-iframe-footer position-fixed w-100">
			<div class="modal-item-last">
				<div class="btn-group-spaced" role="group">
					<c:if test="<%= showCancelButton %>">
						<button class="btn btn-secondary modal-closer" type="button">
							<liferay-ui:message key="cancel" />
						</button>
					</c:if>

					<c:if test="<%= showSubmitButton || Validator.isNotNull(submitButtonLabel) %>">
						<button class="btn btn-primary form-submitter" type="submit">
							<%= Validator.isNotNull(submitButtonLabel) ? HtmlUtil.escape(submitButtonLabel) : LanguageUtil.get(request, "submit") %>
						</button>
					</c:if>
				</div>
			</div>
		</div>
	</c:if>
</div>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"redirectURL", redirect
		).put(
			"requestProcessed", SessionMessages.contains(renderRequest, "requestProcessed")
		).put(
			"useNativeSubmit", useNativeSubmit
		).build()
	%>'
	module="{ModalContentHandler} from commerce-frontend-taglib"
/>