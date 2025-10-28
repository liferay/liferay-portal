/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useRef, useState} from 'react';

function isValid(element) {
	return Array.from(element.querySelectorAll('*')).every(
		(child) => !child.reportValidity || child.reportValidity()
	);
}

export function MultiStepFormModal({
	children,
	className,
	observer,
	onClose,
	onFormError,
	size,
	submitLabel = Liferay.Language.get('submit'),
	submitURL,
	title,
}) {
	const [currentStepIndex, setCurrentStepIndex] = useState(0);
	const [currentStepElement, setCurrentStepElement] = useState(null);

	const maxSteps = React.Children.count(children);

	const isPreviousButtonEnabled = currentStepIndex > 0;
	const isNextButtonEnabled = currentStepIndex < maxSteps - 1;

	const formRef = useRef(null);

	const handlePreviousStepButtonClick = () => {
		setCurrentStepIndex((previousIndex) => previousIndex - 1);
	};

	const handleNextStepButtonClick = () => {
		if (isValid(currentStepElement)) {
			setCurrentStepIndex((previousIndex) => previousIndex + 1);
		}
	};

	const handleFormSubmit = (event) => {
		event.preventDefault();

		if (!isValid(currentStepElement)) {
			return;
		}

		const formData = new FormData(formRef.current);

		fetch(submitURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then(({error, redirectURL}) => {
				if (error) {
					onFormError(error);
				}
				if (redirectURL) {
					navigate(redirectURL);
				}
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const mapChild = (child, index) => {
		const isActive = index === currentStepIndex;

		return React.cloneElement(child, {
			isActive,
			ref: isActive ? setCurrentStepElement : () => {},
		});
	};

	return (
		<ClayModal
			className={className}
			observer={observer}
			onSubmit={handleFormSubmit}
			size={size}
		>
			{title && (
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{title}
				</ClayModal.Header>
			)}

			<ClayModal.Body>
				<ClayForm ref={formRef}>
					{React.Children.map(children, mapChild)}
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={
								isPreviousButtonEnabled
									? handlePreviousStepButtonClick
									: onClose
							}
						>
							{isPreviousButtonEnabled
								? Liferay.Language.get('previous')
								: Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={
								isNextButtonEnabled
									? handleNextStepButtonClick
									: handleFormSubmit
							}
						>
							{isNextButtonEnabled
								? Liferay.Language.get('next')
								: submitLabel}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

MultiStepFormModal.propTypes = {
	className: PropTypes.string,
	observer: PropTypes.object.isRequired,
	onClose: PropTypes.func.isRequired,
	size: PropTypes.string,
	submitLabel: PropTypes.string,
	submitURL: PropTypes.string.isRequired,
	title: PropTypes.string,
};

export const MultiStepFormModalStep = React.forwardRef(
	({children, isActive}, ref) => {
		return (
			<div
				aria-hidden={!isActive}
				ref={ref}
				style={{display: isActive ? '' : 'none'}}
			>
				{children}
			</div>
		);
	}
);

MultiStepFormModalStep.propTypes = {
	isActive: PropTypes.bool,
};
