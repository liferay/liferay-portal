/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet;

/**
 * @author Lino Alves
 */
public interface SearchResultsPortletPreferences {

	public static final String PREFERENCE_KEY_ACCURATE_COUNT_LIMIT =
		"accurateCountLimit";

	public static final String PREFERENCE_KEY_DISPLAY_IN_DOCUMENT_FORM =
		"displayInDocumentForm";

	public static final String PREFERENCE_KEY_FEDERATED_SEARCH_KEY =
		"federatedSearchKey";

	public static final String PREFERENCE_KEY_FIELDS_TO_DISPLAY =
		"fieldsToDisplay";

	public static final String PREFERENCE_KEY_HIGHLIGHT_ENABLED =
		"highlightEnabled";

	public static final String PREFERENCE_KEY_PAGINATION_DELTA =
		"paginationDelta";

	public static final String PREFERENCE_KEY_PAGINATION_DELTA_PARAMETER_NAME =
		"paginationDeltaParameterName";

	public static final String PREFERENCE_KEY_PAGINATION_START_PARAMETER_NAME =
		"paginationStartParameterName";

	public static final String PREFERENCE_KEY_SHOW_EMPTY_RESULT_MESSAGE =
		"showEmptyResultMessage";

	public static final String PREFERENCE_KEY_SHOW_PAGINATION =
		"showPagination";

	public static final String PREFERENCE_KEY_VIEW_IN_CONTEXT = "viewInContext";

	public int getAccurateCountLimit();

	public String getFederatedSearchKey();

	public String getFieldsToDisplay();

	public int getPaginationDelta();

	public String getPaginationDeltaParameterName();

	public String getPaginationStartParameterName();

	public boolean isDisplayInDocumentForm();

	public boolean isHighlightEnabled();

	public boolean isShowEmptyResultMessage();

	public boolean isShowPagination();

	public boolean isViewInContext();

}