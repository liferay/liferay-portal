/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

import StepBar from '../step-bar/StepBar.es';

const ModalWithSteps = ({
	error,
	modalSize = 'lg',
	observer,
	step,
	stepsCount = 2,
	visible,
}) => {
	return (
		<>
			{visible && (
				<ClayModal observer={observer} size={modalSize}>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{step.title}
					</ClayModal.Header>

					{error && (
						<ClayAlert
							className="mb-0"
							displayType="danger"
							title={Liferay.Language.get('error')}
						>
							{error}
						</ClayAlert>
					)}

					<StepBar
						current={step.order}
						title={step.subtitle}
						total={stepsCount}
					/>

					<step.component {...step.props} />

					<ClayModal.Footer
						first={
							step.cancelBtn && (
								<ClayButton
									disabled={step.cancelBtn.disabled}
									displayType="secondary"
									onClick={step.cancelBtn.handle}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>
							)
						}
						last={
							<>
								{step.previousBtn && (
									<ClayButton
										className="mr-3"
										disabled={step.previousBtn.disabled}
										displayType="secondary"
										onClick={step.previousBtn.handle}
									>
										{Liferay.Language.get('previous')}
									</ClayButton>
								)}

								<ClayButton
									disabled={step.nextBtn.disabled}
									onClick={step.nextBtn.handle}
								>
									{step.nextBtn.text}
								</ClayButton>
							</>
						}
					/>
				</ClayModal>
			)}
		</>
	);
};

export default ModalWithSteps;
