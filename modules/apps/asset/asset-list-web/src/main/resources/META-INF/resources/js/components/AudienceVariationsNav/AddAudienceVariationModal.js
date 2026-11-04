/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

const AddAudienceVariationModal = ({
	addVariationURL,
	audiences,
	observer,
	onClose,
	portletNamespace,
}) => {
	const [audienceEntryERC, setAudienceEntryERC] = useState(null);

	const pickerId = useId();

	return (
		<ClayModal center observer={observer} size="md">
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('new-x'),
					Liferay.Language.get('personalized-variation')
				)}
			</ClayModal.Header>

			<form action={addVariationURL} method="post">
				<ClayModal.Body>
					<p>
						{Liferay.Language.get(
							'select-an-audience-to-create-your-personalized-variation'
						)}
					</p>

					<ClayForm.Group className="mb-0">
						<label htmlFor={pickerId}>
							{Liferay.Language.get('audience')}
						</label>

						<Picker
							id={pickerId}
							items={audiences}
							onSelectionChange={setAudienceEntryERC}
							placeholder={Liferay.Language.get(
								'select-an-option'
							)}
							selectedKey={audienceEntryERC}
						>
							{(audience) => (
								<Option
									key={audience.value}
									textValue={audience.label}
								>
									{audience.label}
								</Option>
							)}
						</Picker>
					</ClayForm.Group>

					<input
						name={`${portletNamespace}audienceEntryERC`}
						type="hidden"
						value={audienceEntryERC ?? ''}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={!audienceEntryERC}
								type="submit"
							>
								{Liferay.Language.get('create')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</form>
		</ClayModal>
	);
};

AddAudienceVariationModal.propTypes = {
	addVariationURL: PropTypes.string.isRequired,
	audiences: PropTypes.arrayOf(
		PropTypes.shape({
			label: PropTypes.string.isRequired,
			value: PropTypes.string.isRequired,
		})
	).isRequired,
	observer: PropTypes.object.isRequired,
	onClose: PropTypes.func.isRequired,
	portletNamespace: PropTypes.string.isRequired,
};

export default AddAudienceVariationModal;
