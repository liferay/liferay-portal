/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import React, {useContext} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import {getUpdatedDataItem} from '../utils';

const HTTP_METHODS = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'];

const HTTPEndpoint = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('http-endpoint')}>
			<ClayForm.Group>
				<label htmlFor="httpMethod">
					{Liferay.Language.get('http-method')}
				</label>

				<ClaySelect
					aria-label={Liferay.Language.get('http-method')}
					id="httpMethod"
					onChange={({target}) =>
						setSelectedItem(
							getUpdatedDataItem(
								'httpMethod',
								selectedItem,
								target
							)
						)
					}
					value={selectedItem?.data.httpMethod ?? 'GET'}
				>
					{HTTP_METHODS.map((httpMethod) => (
						<ClaySelect.Option
							key={httpMethod}
							label={httpMethod}
							value={httpMethod}
						/>
					))}
				</ClaySelect>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="url">{Liferay.Language.get('url')}</label>

				<ClayInput
					id="url"
					onChange={({target}) =>
						setSelectedItem(
							getUpdatedDataItem('url', selectedItem, target)
						)
					}
					placeholder="https://ai-sandbox.liferay.net/o/ai-hub/v1.0/..."
					required={true}
					type="text"
					value={selectedItem?.data.url ?? ''}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

export default HTTPEndpoint;
