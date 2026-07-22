/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {formatExpirationDate} from '../../common/utils/expirationStatus';
import {createScheduleDateFDSPropsTransformer} from './utils/createScheduleDateFDSPropsTransformer';

export default createScheduleDateFDSPropsTransformer({
	actionId: 'update-expiration-date',
	bulkActionType: 'UpdateExpirationDateObjectBulkSelectionAction',
	getItemDate: (itemData) => itemData.embedded?.expirationDate,
	keyValuesKey: 'expirationDate',
	modalFieldLabel: Liferay.Language.get('expiration-date'),
	modalFieldName: 'expirationDate',
	modalNeverLabel: Liferay.Language.get('never-expire'),
	modalSaveRequirementLabel: Liferay.Language.get(
		'enter-an-expiration-date-or-select-never-expire-to-enable-the-save-button'
	),
	modalTitle: Liferay.Language.get('update-expiration-date'),
	renderItemDate: (itemData) => (
		<span className="text-warning">
			{formatExpirationDate(itemData.embedded?.expirationDate) ?? '--'}
		</span>
	),
	sortKey: 'dateExpiration',
	sortLabel: Liferay.Language.get('expiration-date'),
	titleRendererName: 'expiredAssetTitle',
});
