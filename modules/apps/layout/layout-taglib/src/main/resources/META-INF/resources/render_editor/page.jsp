<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/render_editor/init.jsp" %>

<%
String content = (String)request.getAttribute("liferay-layout:render-editor:content");
String label = (String)request.getAttribute("liferay-layout:render-editor:label");
String layoutMode = (String)request.getAttribute("liferay-layout:render-editor:layoutMode");
String name = (String)request.getAttribute("liferay-layout:render-editor:name");
boolean required = GetterUtil.getBoolean(request.getAttribute("liferay-layout:render-editor:required"));
%>

<liferay-util:buffer
	var="editorLabel"
>
	<label>
		<%= label %>

		<c:if test="<%= required %>">
			<clay:icon
				cssClass="reference-mark"
				symbol="asterisk"
			/>
		</c:if>
	</label>
</liferay-util:buffer>

<c:choose>
	<c:when test='<%= FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
		<%= editorLabel %>

		<liferay-editor:editor
			contents="<%= content %>"
			disabled='<%= Objects.equals(layoutMode, "edit") %>'
			editorName="ckeditor5_classic"
			name="<%= name %>"
			placeholder="<%= label %>"
			required="<%= required %>"
		/>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test='<%= Objects.equals(layoutMode, "edit") %>'>
				<liferay-util:html-top>
					<aui:link href="/o/frontend-editor-ckeditor-web/ckeditor/skins/moono-lexicon/editor_gecko.css" rel="stylesheet" />
				</liferay-util:html-top>

				<div role="presentation">
					<%= editorLabel %>

					<div class="cke_chrome cke_reset cke_ltr rich-text-input--disabled">
						<div class="cke_inner">
							<div class="cke_top cke_reset_all">
								<div>
									<div class="cke_toolgroup mb-0">
										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__undo_icon"></div>
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__redo_icon" />
										</a>
									</div>
								</div>

								<div>
									<a class="cke_button cke_button_disabled">
										<div class="cke_button_label cke_button__source_label">
											Styles
										</div>
									</a>

									<div class="cke_toolgroup mb-0">
										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__bold_icon"></div>
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__italic_icon" />
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__underline_icon" />
										</a>
									</div>
								</div>

								<div>
									<div class="cke_toolgroup mb-0">
										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__bulletedlist_icon" />
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__numberedlist_icon" />
										</a>
									</div>
								</div>

								<div>
									<div class="cke_toolgroup mb-0">
										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__link_icon" />
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__unlink_icon" />
										</a>
									</div>
								</div>

								<div>
									<div class="cke_toolgroup mb-0">
										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__table_icon" />
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__image_icon" />
										</a>

										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__videoselector_icon" />
										</a>
									</div>
								</div>

								<div>
									<div class="cke_toolgroup mb-0">
										<a class="cke_button cke_button_disabled">
											<div class="cke_button_icon cke_button__source_icon" />

											<div class="cke_button_label cke_button__source_label">
												Source
											</div>
										</a>
									</div>
								</div>
							</div>

							<div class="cke_contents cke_editable cke-height"></div>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<liferay-editor:editor
					contents="<%= content %>"
					editorName="ckeditor"
					name="<%= name %>"
					placeholder="<%= label %>"
					required="<%= required %>"
				/>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>