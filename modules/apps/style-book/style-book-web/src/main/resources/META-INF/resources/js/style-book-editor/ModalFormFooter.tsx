/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {useFormikContext} from 'formik';
import React from 'react';

interface ModalFormFooterProps {
	closeModal: () => void;
	formId: string;
	submitLabel: string;
}

const ModalFormFooter = ({
	closeModal,
	formId,
	submitLabel,
}: ModalFormFooterProps) => {
	const {isSubmitting, isValid} = useFormikContext<Record<string, unknown>>();

	return (
		<ClayModal.Footer
			last={
				<ClayButton.Group spaced>
					<ClayButton displayType="secondary" onClick={closeModal}>
						{Liferay.Language.get('cancel')}
					</ClayButton>

					<ClayButton
						aria-busy={isSubmitting}
						disabled={!isValid || isSubmitting}
						displayType="primary"
						form={formId}
						type="submit"
					>
						{isSubmitting && (
							<span className="inline-item inline-item-before">
								<span
									aria-hidden="true"
									className="loading-animation"
								></span>
							</span>
						)}

						{submitLabel}
					</ClayButton>
				</ClayButton.Group>
			}
		/>
	);
};

export default ModalFormFooter;
