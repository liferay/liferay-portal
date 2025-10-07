/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Form from '../../../../../components/MarketplaceForm';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import i18n from '../../../../../i18n';

const CloudResourceRequirements = () => {
	const [
		{
			build: {resourceRequirements},
		},
		dispatch,
	] = useNewAppContext();

	return (
		<>
			<Form.FormControl>
				<Form.Label className="mt-3" htmlFor="numberOfCPUs" required>
					{i18n.translate('number-of-cpus')}
				</Form.Label>

				<Form.Input
					name="numberOfCPUs"
					onChange={({target: {value}}) => {
						if (!isNaN(value as unknown as number)) {
							dispatch({
								payload: {
									resourceRequirements: {
										...resourceRequirements,
										cpu: value,
									},
								},
								type: NewAppTypes.SET_BUILD,
							});
						}
					}}
					placeholder={i18n.translate('enter-the-number-of-cpus')}
					type="text"
					value={resourceRequirements.cpu ?? ''}
				/>

				<Form.HelpMessage>
					{i18n.translate('enter-the-required-cpus-0-is-valid')}
				</Form.HelpMessage>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label className="mt-3" htmlFor="ram" required>
					{i18n.translate('ram-in-gbs')}
				</Form.Label>

				<Form.Input
					name="ram"
					onChange={({target: {value}}) => {
						if (!isNaN(value as unknown as number)) {
							dispatch({
								payload: {
									resourceRequirements: {
										...resourceRequirements,
										ram: value,
									},
								},
								type: NewAppTypes.SET_BUILD,
							});
						}
					}}
					placeholder={i18n.translate('enter-the-required-ram')}
					type="text"
					value={resourceRequirements.ram ?? ''}
				/>

				<Form.HelpMessage>
					{i18n.translate('enter-the-required-ram-0-is-valid')}
				</Form.HelpMessage>
			</Form.FormControl>
		</>
	);
};

export default CloudResourceRequirements;
