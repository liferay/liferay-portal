/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	SelectOptions,
	SpaceMembersSelectOptions,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersSelectOptions';

const label = 'Test Label';

describe('SpaceMembersSelectOptions', () => {
	it('renders the component with a label and children', () => {
		render(
			<SpaceMembersSelectOptions
				label={label}
				selectValue={SelectOptions.USERS}
			>
				<div>Child Element</div>
			</SpaceMembersSelectOptions>
		);

		expect(screen.getByText('Child Element')).toBeInTheDocument();
		expect(screen.getByRole('combobox', {name: label}));
	});

	it('renders with a custom className', () => {
		const customClass = 'custom-class';

		const {container} = render(
			<SpaceMembersSelectOptions
				className={customClass}
				selectValue={SelectOptions.USERS}
			>
				<div>child</div>
			</SpaceMembersSelectOptions>
		);

		expect(container.getElementsByClassName(customClass)).toHaveLength(1);
	});

	it('calls onSelectChange when the select value is changed', async () => {
		const onSelectChange = jest.fn();

		render(
			<SpaceMembersSelectOptions
				label={label}
				onSelectChange={onSelectChange}
				selectValue={SelectOptions.USERS}
			>
				<div>child</div>
			</SpaceMembersSelectOptions>
		);

		expect(onSelectChange).not.toHaveBeenCalled();

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: label}),
			SelectOptions.GROUPS
		);

		expect(onSelectChange).toHaveBeenCalledTimes(1);
		expect(onSelectChange).toHaveBeenCalledWith(SelectOptions.GROUPS);
	});
});
