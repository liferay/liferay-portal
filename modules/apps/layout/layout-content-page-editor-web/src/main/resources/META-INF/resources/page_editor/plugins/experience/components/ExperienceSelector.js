/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import {useModal} from '@clayui/modal';
import {
	ReactPortal,
	useEventListener,
	useIsMounted,
} from '@liferay/frontend-js-react-web';
import {openToast, useId, useSessionState} from 'frontend-js-components-web';
import {COOKIE_TYPES, navigate} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {config} from '../../../app/config/index';
import {useDispatch, useSelector} from '../../../app/contexts/StoreContext';
import selectCanUpdateExperiences from '../../../app/selectors/selectCanUpdateExperiences';
import selectCanUpdateSegments from '../../../app/selectors/selectCanUpdateSegments';
import getKeyboardFocusableElements from '../../../app/utils/getKeyboardFocusableElements';
import createExperience from '../thunks/createExperience';
import duplicateExperience from '../thunks/duplicateExperience';
import removeExperience from '../thunks/removeExperience';
import updateExperience from '../thunks/updateExperience';
import updateExperiencePriority from '../thunks/updateExperiencePriority';
import {useDebounceCallback} from '../utils';
import ExperienceModal from './ExperienceModal';
import ExperiencesList from './ExperiencesList';

const useOnClickOutside = (ref, handler) => {
	useEffect(() => {
		const listener = (event) => {
			if (!ref.current || ref.current.contains(event.target)) {
				return;
			}
			handler(event);
		};
		document.addEventListener('mousedown', listener);
		document.addEventListener('touchstart', listener);

		return () => {
			document.removeEventListener('mousedown', listener);
			document.removeEventListener('touchstart', listener);
		};
	}, [ref, handler]);
};

/**
 * It produces an object with a target and subtarget keys indicating what experiences
 * should change to change the priority of target priority.
 */
function getUpdateExperiencePriorityTargets(
	orderedExperiences,
	targetExperienceId,
	direction
) {
	const targetIndex = orderedExperiences.findIndex(
		(experience) => experience.segmentsExperienceId === targetExperienceId
	);

	let subtargetIndex;

	if (direction === 'up') {
		subtargetIndex = targetIndex - 1;
	}
	else {
		subtargetIndex = targetIndex + 1;
	}

	const subtargetExperience = orderedExperiences[subtargetIndex];
	const targetExperience = orderedExperiences[targetIndex];

	return {
		priority: subtargetExperience.priority,
		segmentsExperienceId: targetExperience.segmentsExperienceId,
	};
}

const ExperienceSelector = ({experiences, segments, selectedExperience}) => {
	const dispatch = useDispatch();

	const canUpdateExperiences = useSelector(selectCanUpdateExperiences);
	const canUpdateSegments = useSelector(selectCanUpdateSegments);

	const buttonRef = useRef();
	const selectorRef = useRef();
	const [buttonBoundingClientRect, setButtonBoundingClientRect] = useState({
		bottom: 0,
		left: 0,
	});

	const isMounted = useIsMounted();
	const [open, setOpen] = useState(false);
	const [openModal, setOpenModal] = useState(false);
	const [editingExperience, setEditingExperience] = useState({});

	const [modalExperienceState, setModalExperienceState] = useSessionState(
		'modalExperienceState',
		undefined,
		COOKIE_TYPES.NECESSARY
	);
	const modalExperienceStateRef = useRef(modalExperienceState);
	modalExperienceStateRef.current = modalExperienceState;

	const isSelectedExperienceActive = experiences.find(
		({segmentsExperienceId}) =>
			segmentsExperienceId === selectedExperience.segmentsExperienceId
	)?.active;

	const {observer: modalObserver, onClose: onModalClose} = useModal({
		onClose: () => {
			setOpenModal(false);
			setEditingExperience({});
			debouncedSetOpen(true);
		},
	});

	const [debouncedSetOpen] = useDebounceCallback((value) => {
		if (isMounted() && buttonRef.current) {
			setButtonBoundingClientRect(
				buttonRef.current.getBoundingClientRect()
			);

			setOpen(value);
		}
	}, 100);

	const experienceSelectorContentId = useId();

	const memoizedDebouncedSetOpen = useCallback(
		() => debouncedSetOpen(false),
		[debouncedSetOpen]
	);
	useOnClickOutside(selectorRef, memoizedDebouncedSetOpen);

	const onNewSegmentClick = ({experienceId, experienceName, segmentId}) => {
		setModalExperienceState({
			experienceId,
			experienceName,
			plid: config.plid,
			segmentId,
		});

		navigate(config.editSegmentsEntryURL);
	};

	useEffect(() => {
		if (config.plid) {
			const modalExperienceState = modalExperienceStateRef.current;
			setModalExperienceState(null);

			if (
				modalExperienceState &&
				config.plid === modalExperienceState.plid
			) {
				setOpenModal(true);
				setEditingExperience({
					name: modalExperienceState.experienceName,
					segmentsEntryId:
						config.selectedSegmentsEntryId ||
						modalExperienceState.segmentId,
					segmentsExperienceId: modalExperienceState.experienceId,
				});
			}
		}
	}, [setModalExperienceState]);

	useEffect(() => {
		if (open) {
			const element = document.querySelector(
				'.dropdown-menu__experience--active'
			);
			const focusableElements = getKeyboardFocusableElements(
				selectorRef.current
			);

			element?.scrollIntoView?.({
				behavior: 'smooth',
				block: 'nearest',
				inline: 'nearest',
			});

			focusableElements[0]?.focus();
		}
	}, [open]);

	useEffect(() => {
		const element = document.querySelector(
			'.dropdown-menu__experience--active'
		);

		element?.scrollIntoView?.({
			behavior: 'smooth',
			block: 'nearest',
			inline: 'nearest',
		});
	}, [

		// LPS-127205

		experiences.length,
	]);

	useEventListener(
		'keydown',
		(event) => {
			if (event.key === 'Escape' && open) {
				debouncedSetOpen(false);
			}
		},
		true,
		window
	);

	useEventListener(
		'keyup',
		() => {
			if (open && !selectorRef.current.contains(document.activeElement)) {
				debouncedSetOpen(false);
			}
		},
		true,
		window
	);

	const onExperienceCreation = ({
		name,
		segmentsEntryERC,
		segmentsEntryId,
		segmentsEntryScopeERC,
		segmentsExperienceId,
	}) => {
		if (segmentsExperienceId) {
			return dispatch(
				updateExperience({
					name,
					segmentsEntryERC,
					segmentsEntryId,
					segmentsEntryScopeERC,
					segmentsExperienceId,
				})
			)
				.then(() => {
					if (isMounted()) {
						onModalClose();
					}
					openToast({
						message: Liferay.Language.get(
							'the-experience-was-updated-successfully'
						),
						type: 'success',
					});
				})
				.catch(() => {
					if (isMounted()) {
						setEditingExperience({
							error: Liferay.Language.get(
								'an-unexpected-error-occurred-while-updating-the-experience'
							),
							name,
							segmentsEntryId,
							segmentsExperienceId,
						});
					}
				});
		}
		else {
			return dispatch(
				createExperience({
					name,
					segmentsEntryERC,
					segmentsEntryScopeERC,
				})
			)
				.then(() => {
					if (isMounted()) {
						onModalClose();
					}
					openToast({
						message: Liferay.Language.get(
							'the-experience-was-created-successfully'
						),
						type: 'success',
					});
				})
				.catch(() => {
					if (isMounted()) {
						setEditingExperience({
							error: Liferay.Language.get(
								'an-unexpected-error-occurred-while-creating-the-experience'
							),
							name,
							segmentsEntryId,
							segmentsExperienceId,
						});
					}
				});
		}
	};

	const onNewExperience = () => {
		setOpenModal(true);
		debouncedSetOpen(false);
	};

	const onCreateVariations = () => {
		const url = new URL(
			config.editElementVariationsURL,
			window.location.origin
		);

		url.searchParams.set(
			`${config.portletNamespace}segmentsExperienceId`,
			selectedExperience.segmentsExperienceId
		);

		navigate(url.href);
	};

	const onEditExperience = (experienceData) => {
		const {
			name,
			segmentsEntryERC,
			segmentsEntryId,
			segmentsEntryScopeERC,
			segmentsExperienceId,
		} = experienceData;

		setOpenModal(true);
		debouncedSetOpen(false);

		setEditingExperience({
			name,
			segmentsEntryERC,
			segmentsEntryId,
			segmentsEntryScopeERC,
			segmentsExperienceId,
		});
	};

	const onDropdownKeyDown = (event) => {
		if (event.key === 'Escape') {
			buttonRef.current?.focus();
		}
		else if (event.key === 'Tab') {
			const focusableElements = getKeyboardFocusableElements(
				selectorRef.current
			);

			if (event.shiftKey) {
				if (focusableElements.indexOf(event.target) === 0) {
					event.preventDefault();
					buttonRef.current?.focus();
				}
			}
			else if (
				focusableElements.indexOf(event.target) ===
				focusableElements.length - 1
			) {
				event.preventDefault();

				const allFocusableElements =
					getKeyboardFocusableElements(document);

				const index = allFocusableElements.indexOf(buttonRef.current);

				allFocusableElements[index + 1]?.focus();
			}
		}
	};

	const onDeleteExperience = (id) => {
		dispatch(
			removeExperience({
				segmentsExperienceId: id,
				selectedExperienceId: selectedExperience.segmentsExperienceId,
			})
		)
			.then(() => {
				openToast({
					message: Liferay.Language.get(
						'the-experience-was-deleted-successfully'
					),
					type: 'success',
				});
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

	const onDuplicateExperience = (id) => {
		dispatch(
			duplicateExperience({
				segmentsExperienceId: id,
			})
		)
			.then(() => {
				openToast({
					message: Liferay.Language.get(
						'the-experience-was-duplicated-successfully'
					),
					type: 'success',
				});
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

	const onPriorityDecrease = (id) => {
		const target = getUpdateExperiencePriorityTargets(
			experiences,
			id,
			'down'
		);

		dispatch(updateExperiencePriority(target));
	};

	const onPriorityIncrease = (id) => {
		const target = getUpdateExperiencePriorityTargets(
			experiences,
			id,
			'up'
		);

		dispatch(updateExperiencePriority(target));
	};

	return (
		<>
			<ClayButton.Group className="page-editor__experience-selector-group">
				<ClayButton
					aria-controls={experienceSelectorContentId}
					aria-expanded={open}
					aria-haspopup="true"
					aria-label={`${Liferay.Language.get('experience')}: ${
						selectedExperience.name
					}`}
					className="form-control-select page-editor__experience-selector pr-4 text-left text-truncate"
					disabled={!canUpdateExperiences}
					displayType="secondary"
					onClick={() => debouncedSetOpen(!open)}
					ref={buttonRef}
					size="sm"
					type="button"
				>
					<ClayLayout.ContentRow verticalAlign="center">
						<ClayLayout.ContentCol expand>
							<span className="text-truncate">
								{selectedExperience.name}
							</span>
						</ClayLayout.ContentCol>

						{experiences.length > 1 && (
							<ClayLayout.ContentCol>
								{isSelectedExperienceActive ? (
									<ClayLabel displayType="success">
										{Liferay.Language.get('active')}
									</ClayLabel>
								) : (
									<ClayLabel displayType="secondary">
										{Liferay.Language.get('inactive')}
									</ClayLabel>
								)}
							</ClayLayout.ContentCol>
						)}

						<ClayLayout.ContentCol>
							{selectedExperience.hasLockedSegmentsExperiment && (
								<ClayIcon symbol="lock" />
							)}
						</ClayLayout.ContentCol>
					</ClayLayout.ContentRow>
				</ClayButton>

				{Liferay.FeatureFlags['LPD-85746'] && (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('create-variations')}
						className="align-self-stretch h-auto"
						disabled={!canUpdateExperiences}
						displayType="secondary"
						onClick={onCreateVariations}
						size="sm"
						symbol="nodes"
						title={Liferay.Language.get('create-variations')}
					/>
				)}
			</ClayButton.Group>

			{open && (
				<ReactPortal className="cadmin">
					<div
						className="dropdown-menu p-4 page-editor__toolbar-experience__dropdown-menu toggled"
						id={experienceSelectorContentId}
						onKeyDown={onDropdownKeyDown}
						ref={selectorRef}
						style={{
							left: buttonBoundingClientRect.left,
							top: buttonBoundingClientRect.bottom,
						}}
						tabIndex="-1"
					>
						<ExperiencesSelectorHeader
							canCreateExperiences={canUpdateExperiences}
							onNewExperience={onNewExperience}
						/>

						{experiences.length > 1 && (
							<ExperiencesList
								activeExperienceId={
									selectedExperience.segmentsExperienceId
								}
								canUpdateExperiences={canUpdateExperiences}
								defaultExperienceId={
									config.defaultSegmentsExperienceId
								}
								experiences={experiences}
								onDeleteExperience={onDeleteExperience}
								onDuplicateExperience={onDuplicateExperience}
								onEditExperience={onEditExperience}
								onPriorityDecrease={onPriorityDecrease}
								onPriorityIncrease={onPriorityIncrease}
							/>
						)}
					</div>
				</ReactPortal>
			)}

			{openModal && (
				<ExperienceModal
					canUpdateSegments={canUpdateSegments}
					errorMessage={editingExperience.error}
					experienceId={editingExperience.segmentsExperienceId}
					initialName={editingExperience.name}
					observer={modalObserver}
					onClose={onModalClose}
					onErrorDismiss={() => setEditingExperience({error: null})}
					onNewSegmentClick={onNewSegmentClick}
					onSubmit={onExperienceCreation}
					segmentId={editingExperience.segmentsEntryId}
					segments={segments}
					segmentsEntryERC={editingExperience.segmentsEntryERC}
					segmentsEntryScopeERC={
						editingExperience.segmentsEntryScopeERC
					}
				/>
			)}
		</>
	);
};

const ExperiencesSelectorHeader = ({canCreateExperiences, onNewExperience}) => {
	const [dismissAlert, setDismissAlert] = useState(false);

	return (
		<>
			<ClayLayout.ContentRow className="mb-3" verticalAlign="center">
				<ClayLayout.ContentCol expand>
					<h3 className="mb-0">
						{Liferay.Language.get('select-experience')}
					</h3>
				</ClayLayout.ContentCol>

				<ClayLayout.ContentCol>
					{canCreateExperiences === true && (
						<ClayButton
							aria-label={Liferay.Language.get('new-experience')}
							displayType="secondary"
							onClick={onNewExperience}
							size="sm"
						>
							{Liferay.Language.get('new-experience')}
						</ClayButton>
					)}
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{canCreateExperiences && (
				<>
					<p className="text-secondary">
						{Liferay.Language.get('experience-help-message')}
					</p>
					<p className="text-secondary">
						{`${Liferay.Language.get(
							'experience-help-message-more-info-see'
						)} `}

						<a
							className="text-decoration-underline"
							href={config.contentPagePersonalizationLearnURL}
							target="_blank"
						>
							{Liferay.Language.get(
								'content-page-personalization'
							)}
							.
						</a>
					</p>
				</>
			)}

			{!config.isSegmentationEnabled && !dismissAlert && (
				<ClayAlert
					className="mx-0 segmentation-disabled-alert"
					displayType="warning"
					onClose={() => setDismissAlert(true)}
				>
					<strong className="d-block lead">
						{Liferay.Language.get(
							'experiences-cannot-be-displayed-because-segmentation-is-disabled'
						)}
					</strong>

					{config.segmentsConfigurationURL ? (
						<ClayLink href={config.segmentsConfigurationURL}>
							{Liferay.Language.get(
								'to-enable,-go-to-instance-settings'
							)}
						</ClayLink>
					) : (
						Liferay.Language.get(
							'contact-your-system-administrator-to-enable-it'
						)
					)}
				</ClayAlert>
			)}
		</>
	);
};

export default ExperienceSelector;
