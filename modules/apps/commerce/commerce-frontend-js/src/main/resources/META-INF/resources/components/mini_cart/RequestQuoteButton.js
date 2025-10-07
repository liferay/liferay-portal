/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React, {useContext} from 'react';

import {liferayNavigate} from '../../utilities/index';
import MiniCartContext from './MiniCartContext';

function RequestQuoteButton({disabled = false}) {
	const {actionURLs} = useContext(MiniCartContext);

	const {orderDetailURL} = actionURLs;

	return (
		<div className="request-quote-wrapper">
			<ClayButton
				block={true}
				className="btn-md request-quote"
				disabled={disabled}
				displayType="secondary"
				onClick={() => {
					return liferayNavigate(orderDetailURL);
				}}
			>
				<span className="text-truncate-inline">
					<span className="text-truncate">
						{Liferay.Language.get('request-a-quote')}
					</span>
				</span>
			</ClayButton>
		</div>
	);
}

export default RequestQuoteButton;
