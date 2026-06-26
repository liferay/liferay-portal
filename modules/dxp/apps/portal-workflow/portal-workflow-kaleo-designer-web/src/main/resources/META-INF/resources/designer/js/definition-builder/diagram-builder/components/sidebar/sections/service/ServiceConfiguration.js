/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import React, {useContext} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import {getUpdatedDataItem} from '../utils';

const ServiceConfiguration = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('service')}>
			<ClayForm.Group>
				<label htmlFor="javaDelegate">
					{Liferay.Language.get('java-delegate')}
				</label>

				<ClayInput
					component="textarea"
					id="javaDelegate"
					onChange={({target}) =>
						setSelectedItem(
							getUpdatedDataItem(
								'javaDelegate',
								selectedItem,
								target
							)
						)
					}
					placeholder="com.example.Converter#convert"
					required={true}
					type="text"
					value={selectedItem?.data.javaDelegate ?? ''}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

export default ServiceConfiguration;
