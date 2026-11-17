/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import ParameterModal from './ParameterModal';

const ParametersSummary = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const [editedIndex, setEditedIndex] = useState(null);
	const [showModal, setShowModal] = useState(false);

	const parameters = selectedItem?.data.parameters ?? [];

	const closeModal = () => {
		setEditedIndex(null);
		setShowModal(false);
	};

	const setParameters = (newParameters) =>
		setSelectedItem({
			...selectedItem,
			data: {
				...selectedItem.data,
				parameters: newParameters,
			},
		});

	const saveParameter = (parameter) => {
		if (editedIndex === null) {
			setParameters([...parameters, parameter]);
		}
		else {
			setParameters(
				parameters.map((currentParameter, index) =>
					index === editedIndex ? parameter : currentParameter
				)
			);
		}

		closeModal();
	};

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('parameters')}>
			{!!parameters.length && (
				<table className="table table-autofit table-list">
					<thead>
						<tr>
							<th className="table-cell-expand">
								{Liferay.Language.get('parameter')}
							</th>

							<th className="table-cell-expand">
								{Liferay.Language.get('value')}
							</th>

							<th />
						</tr>
					</thead>

					<tbody>
						{parameters.map(({name, value}, index) => (
							<tr key={name}>
								<td className="table-cell-expand text-break">
									{name}
								</td>

								<td className="table-cell-expand text-break">
									{value}
								</td>

								<td>
									<ClayDropDownWithItems
										items={[
											{
												label: Liferay.Language.get(
													'edit'
												),
												onClick: () => {
													setEditedIndex(index);
													setShowModal(true);
												},
											},
											{
												label: Liferay.Language.get(
													'remove'
												),
												onClick: () =>
													setParameters(
														parameters.filter(
															(
																parameter,
																parameterIndex
															) =>
																parameterIndex !==
																index
														)
													),
											},
										]}
										trigger={
											<ClayButtonWithIcon
												aria-label={Liferay.Language.get(
													'actions'
												)}
												displayType="unstyled"
												symbol="ellipsis-v"
												title={Liferay.Language.get(
													'actions'
												)}
											/>
										}
									/>
								</td>
							</tr>
						))}
					</tbody>
				</table>
			)}

			<ClayButton
				displayType="secondary"
				onClick={() => setShowModal(true)}
				size="sm"
			>
				{Liferay.Language.get('add-parameter')}
			</ClayButton>

			{showModal && (
				<ParameterModal
					onClose={closeModal}
					onSave={saveParameter}
					parameter={
						editedIndex === null ? null : parameters[editedIndex]
					}
					parameterNames={parameters.map(({name}) => name)}
				/>
			)}
		</SidebarPanel>
	);
};

ParametersSummary.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default ParametersSummary;
