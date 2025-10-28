/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import {SearchForm} from '@liferay/layout-js-components-web';
import React from 'react';

const Trigger = React.forwardRef(
	({className, selectedItem, ...otherProps}, ref) => (
		<ClayButton
			{...otherProps}
			aria-label={Liferay.Language.get('filter-by-content-type')}
			className={`${className} c-mb-2 form-control-sm`}
			displayType="unstyled"
			ref={ref}
		>
			<span>{selectedItem}</span>
		</ClayButton>
	)
);

export default function ContentFilter({
	contentTypes,
	onChangeInput,
	onChangeSelect,
	selectedType,
}) {
	return (
		<div className="flex-shrink-0 page-editor__page-contents__content-filter px-3">
			<p className="mb-4 page-editor__page-contents__content-filter__help">
				{Liferay.Language.get('content-filtering-help')}
			</p>

			<Picker
				UNSAFE_menuClassName="cadmin"
				as={Trigger}
				items={contentTypes}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onSelectionChange={onChangeSelect}
				selectedItem={selectedType}
				selectedKey={selectedType}
			>
				{(type) => <Option key={type}>{type}</Option>}
			</Picker>

			<SearchForm
				className="mb-3"
				label={Liferay.Language.get('search-content')}
				onChange={onChangeInput}
			/>
		</div>
	);
}
