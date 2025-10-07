/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import FormInput from '../../../../../../../components/Input/formInput';
import i18n from '../../../../../../../i18n';
import {useAppContext} from '../../AppContext/AppManageState';
import {ActionTypes} from '../../AppContext/actionTypes';

import './index.scss';

const ResourceRequirements = () => {
	const [{resourceRequirements}, dispatch] = useAppContext();

	return (
		<div className="d-flex justify-content-between resource-requirements-content">
			<FormInput
				boldLabel
				className="custom-input resource-requirements-content-input"
				helpMessage={i18n.translate(
					'enter-the-required-cpus-0-is-valid'
				)}
				label={i18n.translate('number-of-cpus')}
				maxLength={2}
				name="numberOfCPUs"
				onChange={({target: {value}}) => {
					if (!isNaN(value as unknown as number)) {
						dispatch({
							payload: {key: 'cpu', value},
							type: ActionTypes.UPDATE_RESOURCE_REQUIREMENTS,
						});
					}
				}}
				placeholder={i18n.translate('enter-the-number-of-cpus')}
				value={resourceRequirements.cpu ?? ''}
			/>

			<FormInput
				boldLabel
				className="custom-input resource-requirements-content-input"
				helpMessage={i18n.translate(
					'enter-the-required-ram-0-is-valid'
				)}
				label={i18n.translate('ram-in-gbs')}
				maxLength={2}
				name="ram"
				onChange={({target: {value}}) => {
					if (!isNaN(value as unknown as number)) {
						dispatch({
							payload: {key: 'ram', value},
							type: ActionTypes.UPDATE_RESOURCE_REQUIREMENTS,
						});
					}
				}}
				placeholder={i18n.translate('enter-the-required-ram')}
				value={resourceRequirements.ram ?? ''}
			/>
		</div>
	);
};

export default ResourceRequirements;
