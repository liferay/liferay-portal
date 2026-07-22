/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ReviewDateRenderer from './cell_renderers/ReviewDateRenderer';
import {createScheduleDateFDSPropsTransformer} from './utils/createScheduleDateFDSPropsTransformer';

export default createScheduleDateFDSPropsTransformer({
	actionId: 'update-review-date',
	bulkActionType: 'UpdateReviewDateObjectBulkSelectionAction',
	getItemDate: (itemData) => itemData.dateReview,
	keyValuesKey: 'reviewDate',
	modalFieldLabel: Liferay.Language.get('review-date'),
	modalFieldName: 'reviewDate',
	modalNeverLabel: Liferay.Language.get('never-review'),
	modalSaveRequirementLabel: Liferay.Language.get(
		'enter-a-review-date-or-select-never-review-to-enable-the-save-button'
	),
	modalTitle: Liferay.Language.get('update-review-date'),
	renderItemDate: (itemData) => <ReviewDateRenderer itemData={itemData} />,
	sortKey: 'dateReview',
	sortLabel: Liferay.Language.get('review-date'),
	titleRendererName: 'overdueReviewTitle',
});
