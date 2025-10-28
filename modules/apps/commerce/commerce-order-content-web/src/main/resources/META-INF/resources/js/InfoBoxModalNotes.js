/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import React from 'react';

import InfoBoxModalNotesInput from './info_box/modal/InfoBoxModalNotesInput';

const InfoBoxModalNotes = ({
	handleDelete,
	handleSubmit,
	handleToggle,
	id,
	isRestricted,
	isValid,
	label,
	notes,
	observer,
	onOpenChange,
	open,
	setInputValue,
	spritemap,
}) => {
	const InputRenderer = InfoBoxModalNotesInput;

	return (
		<>
			{open && (
				<ClayModal
					id={id}
					observer={observer}
					size="lg"
					spritemap={spritemap}
				>
					<ClayForm onSubmit={handleSubmit}>
						<ClayModal.Header
							closeButtonAriaLabel={Liferay.Language.get('close')}
						>
							{label}
						</ClayModal.Header>

						<ClayModal.Body>
							<ClayForm.Group>
								<InputRenderer
									handleDelete={handleDelete}
									handleSubmit={handleSubmit}
									handleToggle={handleToggle}
									isRestricted={isRestricted}
									notes={notes}
									setInputValue={setInputValue}
									spritemap={spritemap}
								/>
							</ClayForm.Group>
						</ClayModal.Body>

						<ClayModal.Footer
							last={
								<ClayButton.Group spaced>
									<ClayButton
										displayType="secondary"
										onClick={() => onOpenChange(false)}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayButton
										disabled={!isValid}
										type="submit"
									>
										{Liferay.Language.get('submit')}
									</ClayButton>
								</ClayButton.Group>
							}
						/>
					</ClayForm>
				</ClayModal>
			)}
		</>
	);
};

export default InfoBoxModalNotes;
