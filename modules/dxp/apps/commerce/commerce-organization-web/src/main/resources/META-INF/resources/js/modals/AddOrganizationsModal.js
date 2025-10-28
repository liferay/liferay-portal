/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useState} from 'react';

import ChartContext from '../ChartContext';
import {createOrganizations} from '../data/organizations';
import {MODEL_TYPE_MAP} from '../utils/constants';

export default function AddOrganizationModal({
	closeModal,
	observer,
	parentData,
}) {
	const [query, setQuery] = useState('');
	const [items, setItems] = useState([]);
	const [errors, setErrors] = useState([]);
	const {chartInstanceRef} = useContext(ChartContext);

	function handleSave() {
		const newOrganizations = query
			? items.concat({label: query, value: query})
			: [...items];

		const organizationNames = newOrganizations.map((item) => item.label);

		if (!newOrganizations.length) {
			setErrors([Liferay.Language.get('a-name-is-required')]);

			return;
		}

		createOrganizations(organizationNames, parentData.id).then(
			(results) => {
				const newOrganizationsDetails = [];
				const newErrors = new Set();
				const failedOrganizations = [];

				results.forEach((result, fetchNumber) => {
					if (result.status === 'rejected') {
						failedOrganizations.push(newOrganizations[fetchNumber]);
						newErrors.add(result.reason.title);
					}
					else {
						newOrganizationsDetails.push(result.value);
					}
				});

				setItems(failedOrganizations);
				setErrors(Array.from(newErrors));
				setQuery('');

				if (newOrganizationsDetails.length) {
					const message =
						newOrganizationsDetails.length === 1
							? sub(
									Liferay.Language.get(
										'1-organization-was-added-to-x'
									),
									parentData.name
								)
							: sub(
									Liferay.Language.get(
										'x-organizations-were-added-to-x'
									),
									newOrganizationsDetails.length,
									parentData.name
								);

					openToast({
						message,
						type: 'success',
					});

					chartInstanceRef.current.addNodes(
						newOrganizationsDetails,
						MODEL_TYPE_MAP.organization,
						parentData
					);

					chartInstanceRef.current.updateNodeContent({
						...parentData,
						numberOfOrganizations:
							parentData.numberOfOrganizations +
							newOrganizationsDetails.length,
					});
				}

				if (!failedOrganizations.length) {
					closeModal();
				}
			}
		);
	}

	return (
		<ClayModal center observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-organizations')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group
					className={classNames(!!errors.length && 'has-error')}
				>
					<label htmlFor="addNewOrganization">
						{Liferay.Language.get('name') + ' '}

						<ClayIcon
							className="ml-1 reference-mark"
							symbol="asterisk"
						/>
					</label>

					<ClayInput.Group>
						<ClayInput.GroupItem>
							<ClayMultiSelect
								id="addNewOrganization"
								items={items}
								onChange={setQuery}
								onItemsChange={setItems}
								placeholder={Liferay.Language.get(
									'organization-name'
								)}
								value={query}
							/>

							{!!errors.length && (
								<ClayForm.FeedbackGroup>
									{errors.map((error, i) => (
										<ClayForm.FeedbackItem key={i}>
											<ClayForm.FeedbackIndicator symbol="info-circle" />

											{error}
										</ClayForm.FeedbackItem>
									))}
								</ClayForm.FeedbackGroup>
							)}
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" onClick={handleSave}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
