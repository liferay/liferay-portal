/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {saveViewSettings} from '../../utils/saveViewSettings';
import ViewsContext from '../../views/ViewsContext';

const ActiveViewSelectorTrigger = React.forwardRef(
	({symbol, ...otherProps}, ref) => (
		<ClayButtonWithIcon
			{...otherProps}
			aria-label={Liferay.Language.get('show-view-options')}
			className="nav-link nav-link-monospaced"
			displayType="unstyled"
			ref={ref}
			symbol={symbol}
			title={Liferay.Language.get('show-view-options')}
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
			onSelectionChange={handleSelectionChange}
			selectedKey={activeView.name}
			symbol={activeView.thumbnail}
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
