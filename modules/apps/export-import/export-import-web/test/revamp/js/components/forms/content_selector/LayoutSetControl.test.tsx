/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import LayoutSetControl from '../../../../../../src/main/resources/META-INF/resources/revamp/js/components/forms/content_selector/LayoutSetControl';
import {
	LayoutSetVisibility,
	PortletDataHandlerSelection,
} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';

jest.mock('@liferay/layout-js-components-web', () => ({
	PageTreePickerModal: () => null,
}));

function renderLayoutSetControl(
	{
		availableLayoutSetVisibilities = ['public'],
		onChange = jest.fn(),
		portletDataHandlerSelection,
	}: {
		availableLayoutSetVisibilities?: LayoutSetVisibility[];
		onChange?: jest.Mock;
		portletDataHandlerSelection?: PortletDataHandlerSelection;
	} = {portletDataHandlerSelection: {all: true, privateLayout: false}}
) {
	return render(
		<LayoutSetControl
			availableLayoutSetVisibilities={availableLayoutSetVisibilities}
			label="Pages"
			onChange={onChange}
			pageTreeModalConfiguration={{
				groupId: 20121,
				pageSize: 20,
				siteExternalReferenceCode: 'site',
			}}
			portletDataHandlerSelection={portletDataHandlerSelection}
		/>
	);
}

describe('LayoutSetControl', () => {
	it('shows the pages as partially selected when some pages are excluded', () => {
		renderLayoutSetControl({
			portletDataHandlerSelection: {
				all: true,
				excludedItems: ['excluded'],
				privateLayout: false,
			},
		});

		const checkbox = screen.getByRole('checkbox', {name: 'Pages'});

		expect(checkbox).not.toBeChecked();
		expect(checkbox).toBePartiallyChecked();
	});

	it('selects all pages when a partially selected checkbox is clicked', async () => {
		const user = userEvent.setup();

		const portletDataHandlerSelections: PortletDataHandlerSelection[] = [
			{all: true, excludedItems: ['excluded'], privateLayout: false},
			{items: ['item'], privateLayout: false},
		];

		for (const portletDataHandlerSelection of portletDataHandlerSelections) {
			const onChange = jest.fn();

			const {unmount} = renderLayoutSetControl({
				onChange,
				portletDataHandlerSelection,
			});

			await user.click(screen.getByRole('checkbox', {name: 'Pages'}));

			expect(onChange).toHaveBeenCalledWith({
				all: true,
				privateLayout: false,
			});

			unmount();
		}
	});

	it('selects the private pages again when they are the default', async () => {
		const onChange = jest.fn();

		renderLayoutSetControl({
			availableLayoutSetVisibilities: ['private'],
			onChange,
		});

		await userEvent.click(screen.getByRole('checkbox', {name: 'Pages'}));

		expect(onChange).toHaveBeenCalledWith({all: true, privateLayout: true});
	});

	it('shows the visibility radios only when both visibilities are available', () => {
		const {unmount} = renderLayoutSetControl({
			availableLayoutSetVisibilities: ['private'],
			portletDataHandlerSelection: {all: true, privateLayout: true},
		});

		expect(screen.queryAllByRole('radio')).toHaveLength(0);

		unmount();

		renderLayoutSetControl({
			availableLayoutSetVisibilities: ['public', 'private'],
			portletDataHandlerSelection: {all: true, privateLayout: false},
		});

		expect(screen.getAllByRole('radio')).toHaveLength(2);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderLayoutSetControl();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
