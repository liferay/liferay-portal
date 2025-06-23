/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayManagementToolbar from '@clayui/management-toolbar';
import {useCallback, useContext, useEffect, useRef, useState} from 'react';

import i18n from '../../../i18n';
import {ListViewContext, ListViewTypes} from '../hooks/ListViewContext';

const ManagementToolbarSearch = () => {
	const [{keywords}, dispatch] = useContext(ListViewContext);
	const [search, setSearch] = useState(keywords || '');

	const inputRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		const timeout = setTimeout(() => {
			inputRef?.current?.focus();
		}, 100);

		return () => clearTimeout(timeout);
	}, [inputRef]);

	const onApply = useCallback(() => {
		dispatch({
			payload: search,
			type: ListViewTypes.SET_SEARCH,
		});
	}, [dispatch, search]);

	const onClear = useCallback(() => {
		dispatch({
			payload: '',
			type: ListViewTypes.SET_SEARCH,
		});
	}, [dispatch]);

	return (
		<div className="w-100">
			<ClayManagementToolbar.Search
				onSubmit={(event: React.FormEvent<HTMLFormElement>) => {
					event.preventDefault();
					onApply();
				}}
			>
				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							aria-label="Search"
							className="bg-white form-control input-group-inset input-group-inset-after"
							onChange={({target: {value}}) => setSearch(value)}
							placeholder={i18n.translate('search')}
							ref={inputRef}
							type="text"
							value={search}
						/>
						<ClayInput.GroupInsetItem
							after
							className="bg-white"
							tag="span"
						>
							<ClayButtonWithIcon
								aria-label="Search"
								displayType="unstyled"
								onClick={onApply}
								symbol="search"
							/>
							{keywords && (
								<ClayButtonWithIcon
									aria-label="Search"
									displayType="unstyled"
									onClick={onClear}
									symbol="times"
								/>
							)}
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayManagementToolbar.Search>
		</div>
	);
};

export default ManagementToolbarSearch;
