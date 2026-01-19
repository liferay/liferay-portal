/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {SearcheableSpaceMembersList} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SearcheableSpaceMembersList';
import {SelectOptions} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersSelectOptions';

describe('SearcheableSpaceMembersList', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('renders correctly', () => {
		const {container} = render(
			<SearcheableSpaceMembersList
				onSearch={jest.fn()}
				onSelectChange={jest.fn()}
				selectValue={SelectOptions.USERS}
			/>
		);

		expect(container).toMatchSnapshot();
		expect(screen.getByRole('combobox')).toBeInTheDocument();
		expect(
			screen.getByRole('textbox', {name: /search-for-name-or-email/i})
		).toBeInTheDocument();
	});

	it('renders with a custom className', () => {
		const customClass = 'my-custom-class';

		const {container} = render(
			<SearcheableSpaceMembersList
				className={customClass}
				onSearch={jest.fn()}
				onSelectChange={jest.fn()}
				selectValue={SelectOptions.USERS}
			/>
		);

		expect(container.getElementsByClassName(customClass)).toHaveLength(1);
	});

	it('calls onSearch after debounce when typing in the search input', async () => {
		const onSearch = jest.fn();

		render(
			<SearcheableSpaceMembersList
				onSearch={onSearch}
				onSelectChange={jest.fn()}
				selectValue={SelectOptions.USERS}
			/>
		);

		const searchInput = screen.getByRole('textbox', {
			name: /search-for-name-or-email/i,
		});
		const searchText = 'test search';

		await userEvent.type(searchInput, searchText, {delay: null});

		expect(searchInput).toHaveValue(searchText);
		expect(onSearch).not.toHaveBeenCalled();

		jest.advanceTimersByTime(400);

		expect(onSearch).toHaveBeenCalledWith(searchText);
		expect(onSearch).toHaveBeenCalledTimes(1);
	});

	it('calls onSelectChange when the select value is changed', async () => {
		const onSelectChange = jest.fn();

		render(
			<SearcheableSpaceMembersList
				onSearch={jest.fn()}
				onSelectChange={onSelectChange}
				selectValue={SelectOptions.USERS}
			/>
		);

		const select = screen.getByRole('combobox');

		await userEvent.selectOptions(select, SelectOptions.GROUPS, {
			delay: null,
		});

		expect(onSelectChange).toHaveBeenCalledWith(SelectOptions.GROUPS);
		expect(onSelectChange).toHaveBeenCalledTimes(1);
	});
});
