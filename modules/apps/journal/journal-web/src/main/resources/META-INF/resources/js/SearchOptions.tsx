/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {addParams, navigate} from 'frontend-js-web';
import React, {Key} from 'react';

const Trigger = React.forwardRef<HTMLButtonElement, any>(
	(
		{
			'aria-label': ariaLabel,
			children,
			'className': _className,
			onClick,
			triggerIcon,
			...otherProps
		},
		ref
	) => (
		<ClayButton
			aria-label={ariaLabel}
			className="form-control-select"
			displayType="secondary"
			onClick={onClick}
			ref={ref}
			size="sm"
			{...otherProps}
		>
			{triggerIcon && <ClayIcon className="mr-2" symbol={triggerIcon} />}

			{children}
		</ClayButton>
	)
);

type Option = {
	label: string;
	value: string;
};

type Props = {
	portletNamespace: string;
	searchIn: Key;
	searchInCommentsURL: string;
	searchInOptions: Option[];
	searchLocation: Key;
	searchLocationOptions: Option[];
	searchResults: Key;
	searchURL: string;
};

const SearchOptions = ({
	portletNamespace: namespace,
	searchIn: initialSearchIn,
	searchInCommentsURL,
	searchInOptions,
	searchLocation: initialLocation,
	searchLocationOptions,
	searchResults: initialResults,
	searchURL,
}: Props) => {
	const onChange = ({
		location,
		results,
		searchIn,
	}: {
		location?: Key;
		results?: Key;
		searchIn?: Key;
	}) => {
		const baseURL =
			searchIn === 'comments' ? searchInCommentsURL : searchURL;
		const parsedBaseURL = new URL(baseURL);
		const searchParams = parsedBaseURL.searchParams;
		searchParams.delete(`${namespace}tab`);

		const url = addParams(
			{
				[`${namespace}searchIn`]: searchIn || initialSearchIn,
				[`${namespace}searchLocation`]: location || initialLocation,
				[`${namespace}tab`]: results || initialResults,
			},
			parsedBaseURL.toString()
		);

		navigate(url);
	};

	return (
		<ClayLayout.Row className="cadmin">
			<ClayLayout.Col>
				{searchLocationOptions ? (
					<ClayForm.Group className="c-mr-2 d-inline-flex">
						<Picker
							aria-label={Liferay.Language.get('location')}
							as={Trigger}
							id={`${namespace}searchLocation`}
							messages={{
								itemDescribedby: Liferay.Language.get(
									'you-are-currently-on-a-text-element,-inside-of-a-list-box'
								),
								itemSelected:
									Liferay.Language.get('x-selected'),
								scrollToBottomAriaLabel:
									Liferay.Language.get('scroll-to-bottom'),
								scrollToTopAriaLabel:
									Liferay.Language.get('scroll-to-top'),
							}}
							onSelectionChange={(key: Key) =>
								onChange({location: key})
							}
							selectedKey={initialLocation}
							triggerIcon="folder"
						>
							<DropDown.Group
								header={Liferay.Language.get('location')}
								items={searchLocationOptions}
							>
								{(item) => (
									<Option key={item.value}>
										{item.label}
									</Option>
								)}
							</DropDown.Group>
						</Picker>
					</ClayForm.Group>
				) : null}

				{searchInOptions ? (
					<ClayForm.Group className="d-inline-flex">
						<Picker
							aria-label={Liferay.Language.get('search-in')}
							as={Trigger}
							id={`${namespace}searchIn`}
							onSelectionChange={(key: Key) =>
								onChange({searchIn: key})
							}
							selectedKey={initialSearchIn}
						>
							<DropDown.Group
								header={Liferay.Language.get('search-in')}
								items={searchInOptions}
							>
								{(item) => (
									<Option key={item.value}>
										{item.label}
									</Option>
								)}
							</DropDown.Group>
						</Picker>
					</ClayForm.Group>
				) : null}
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

export default SearchOptions;
