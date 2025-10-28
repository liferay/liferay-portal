/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {saveViewSettings} from '../../utils/saveViewSettings';
import ViewsContext from '../../views/ViewsContext';

const ActiveViewSelectorTrigger = React.forwardRef(
	({symbol, title, ...otherProps}, ref) => (
		<ClayButtonWithIcon
			{...otherProps}
			aria-label={title}
			className="nav-link nav-link-monospaced"
			displayType="unstyled"
			ref={ref}
			symbol={symbol}
			title={title}
		/>
	)
);

function ActiveViewSelector({views}) {
	const {appURL, id, portletId, updateView} = useContext(
		FrontendDataSetContext
	);
	const [{activeView}, viewsDispatch] = useContext(ViewsContext);

	const handleSelectionChange = (value) => {
		viewsDispatch(updateView(value));

		saveViewSettings({
			appURL,
			id,
			portletId,
			settings: {name: value},
		});
	};

	return (
		<Picker
			as={ActiveViewSelectorTrigger}
			items={views}
			messages={{
				itemDescribedby: Liferay.Language.get(
					'you-are-currently-on-a-text-element,-inside-of-a-list-box'
				),
				itemSelected: Liferay.Language.get('x-selected'),
				scrollToBottomAriaLabel:
					Liferay.Language.get('scroll-to-bottom'),
				scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
			}}
			onSelectionChange={handleSelectionChange}
			selectedKey={activeView.name}
			symbol={activeView.thumbnail}
			title={sub(
				Liferay.Language.get('x-view-selected'),
				activeView.label
			)}
		>
			{({label, name, thumbnail}) => (
				<Option key={name} textValue={name}>
					<ClayIcon className="mr-3" symbol={thumbnail} />

					{label}
				</Option>
			)}
		</Picker>
	);
}

export default ActiveViewSelector;
