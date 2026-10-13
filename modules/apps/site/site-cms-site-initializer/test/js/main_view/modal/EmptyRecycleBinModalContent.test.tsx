/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import EmptyRecycleBinModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/EmptyRecycleBinModalContent';
import {triggerAssetBulkAction} from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetBulkAction';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetBulkAction',
	() => ({
		triggerAssetBulkAction: jest.fn(),
	})
);

describe('EmptyRecycleBinModalContent', () => {
	it('deletes every item matching the given filter and closes the modal', () => {
		const closeModal = jest.fn();
		const filterString =
			"cmsRoot eq true and (cmsSection eq 'contents' or cmsSection eq 'files' or cmsSection eq 'products') and rootDescendantNode eq false and status eq 8";

		render(
			<EmptyRecycleBinModalContent
				closeModal={closeModal}
				filterString={filterString}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'empty-bin'}));

		expect(triggerAssetBulkAction).toHaveBeenCalledWith({
			apiURL: `/o/bulk/v1.0/bulk-action?filter=${encodeURIComponent(filterString)}&nestedFields=embedded`,
			selectedData: {selectAll: true},
			type: 'DeleteObjectBulkSelectionAction',
		});
		expect(closeModal).toHaveBeenCalled();
	});
});
