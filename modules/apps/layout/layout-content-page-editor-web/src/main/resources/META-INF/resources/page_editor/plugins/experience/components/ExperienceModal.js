/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {config} from '../../../app/config/index';
import Button from '../../../common/components/Button';

const ExperienceModal = ({
	canUpdateSegments,
	errorMessage,
	experienceId,
	initialName = '',
	observer,
	onClose,
	onErrorDismiss,
	onNewSegmentClick,
	onSubmit,
	segmentId,
	segments = [],
}) => {
	const [selectedSegmentId, setSelectedSegmentId] = useState(
		segmentId !== undefined
			? segmentId
			: segments[0] && segments[0].segmentsEntryId
	);

	const isMounted = useIsMounted();

	const [name, setName] = useState(initialName);
	const [requiredNameError, setRequiredNameError] = useState(false);
	const [loading, setLoading] = useState(false);

	const handleFormSubmit = (event) => {
		event.preventDefault();

		const validName = _getValidValue(name);

		if (!validName) {
			setRequiredNameError(true);
		}
		else {
			setLoading(true);

			onSubmit({
				name,
				segmentsEntryId: selectedSegmentId,
				segmentsExperienceId: experienceId,
			}).finally(() => {
				if (isMounted()) {
					setLoading(false);
				}
			});
		}
	};
	const handleNameChange = (event) => {
		const {value} = event.target;

		if (!_getValidValue(value)) {
			setRequiredNameError(true);
		}
		else {
			setRequiredNameError(false);
		}

		setName(value);
	};
	const handleSegmentChange = (event) => {
		setSelectedSegmentId(event.target.value);
	};
	const handleNewSegmentClick = (event) => {
		event.preventDefault();

		onNewSegmentClick({
			experienceId,
			experienceName: name,
			segmentId: selectedSegmentId,
		});
	};

	const nameInputId = `${config.portletNamespace}segmentsExperienceName`;
	const segmentSelectId = `${config.portletNamespace}segmentsExperienceSegment`;

	const nameGroupClassName = classNames('my-2', {
		'has-error': requiredNameError,
	});

	const modalTitle = experienceId
		? Liferay.Language.get('edit-experience')
		: Liferay.Language.get('new-experience');

	return (
		<ClayModal
			containerProps={{className: 'cadmin'}}
			observer={observer}
			size="md"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{modalTitle}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm
					autoComplete="off"
					className="mb-3"
					noValidate
					onSubmit={handleFormSubmit}
				>
					{errorMessage && (
						<ClayAlert
							displayType="danger"
							onClose={onErrorDismiss}
							title={errorMessage}
						/>
					)}

					<ClayForm.Group className={nameGroupClassName}>
						<label htmlFor={nameInputId}>
							{Liferay.Language.get('name')}

							<ClayIcon
								className="ml-1 reference-mark"
								focusable="false"
								role="presentation"
								symbol="asterisk"
							/>
						</label>

						<ClayInput
							autoFocus
							id={nameInputId}
							onChange={handleNameChange}
							placeholder={Liferay.Language.get(
								'experience-name'
							)}
							required
							type="text"
							value={name}
						/>

						{requiredNameError && (
							<ClayForm.FeedbackItem>
								<ClayForm.FeedbackIndicator
									className="mr-1"
									symbol="exclamation-full"
								/>

								{Liferay.Language.get(
									'an-experience-name-is-required'
								)}
							</ClayForm.FeedbackItem>
						)}
					</ClayForm.Group>

					<ClayForm.Group className="my-2">
						<label htmlFor={segmentSelectId}>
							{Liferay.Language.get('audience')}

							<ClayIcon
								className="ml-1 reference-mark"
								focusable="false"
								role="presentation"
								symbol="asterisk"
							/>
						</label>

						<div className="d-flex">
							<ClaySelect
								disabled={!segments.length}
								id={segmentSelectId}
								onChange={handleSegmentChange}
								value={selectedSegmentId}
							>
								{segments.length ? (
									segments.map((segment) => {
										return (
											<ClaySelect.Option
												key={segment.segmentsEntryId}
												label={segment.name}
												value={segment.segmentsEntryId}
											/>
										);
									})
								) : (
									<ClaySelect.Option
										label={Liferay.Language.get(
											'no-segments'
										)}
										value=""
									/>
								)}
							</ClaySelect>

							{canUpdateSegments === true && (
								<Button
									className="flex-shrink-0 ml-2"
									disabled={loading}
									displayType="secondary"
									onClick={handleNewSegmentClick}
									type="button"
								>
									{Liferay.Language.get('new-segment')}
								</Button>
							)}
						</div>
					</ClayForm.Group>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={loading}
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<Button
							disabled={loading}
							displayType="primary"
							loading={loading}
							onClick={handleFormSubmit}
						>
							{Liferay.Language.get('save')}
						</Button>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</ClayModal>
	);
};

ExperienceModal.propTypes = {
	canUpdateSegments: PropTypes.bool.isRequired,
	errorMessage: PropTypes.string,
	experienceId: PropTypes.string,
	initialName: PropTypes.string,
	observer: PropTypes.object.isRequired,
	onClose: PropTypes.func.isRequired,
	onErrorDismiss: PropTypes.func.isRequired,
	onNewSegmentClick: PropTypes.func.isRequired,
	onSubmit: PropTypes.func.isRequired,
	segmentId: PropTypes.string,
	segments: PropTypes.array.isRequired,
};

function _getValidValue(value) {
	return value && value.replace(/ /g, '');
}

export default ExperienceModal;
