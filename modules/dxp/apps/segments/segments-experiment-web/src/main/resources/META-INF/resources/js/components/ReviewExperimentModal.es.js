/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

import SegmentsExperimentContext from '../context.es';
import {StateContext} from '../state/context.es';
import {SegmentsVariantType} from '../types.es';
import {SUCCESS_ANIMATION_FILE_NAME} from '../util/contants.es';
import {useDebounceCallback} from '../util/hooks.es';
import {
	INITIAL_CONFIDENCE_LEVEL,
	MAX_CONFIDENCE_LEVEL,
	MIN_CONFIDENCE_LEVEL,
	percentageNumberToIndex,
} from '../util/percentages.es';
import LoadingButton from './LoadingButton/LoadingButton.es';
import {SliderWithLabel} from './SliderWithLabel.es';
import {SplitPicker} from './SplitPicker/SplitPicker.es';

const TIME_ESTIMATION_THROTTLE_TIME_MS = 1000;

function ReviewExperimentModal({modalObserver, onModalClose, onRun, variants}) {
	const [loading, setLoading] = useState(false);
	const [success, setSuccess] = useState(false);
	const [estimation, setEstimation] = useState({
		days: null,
		loading: true,
	});
	const [confidenceLevel, setConfidenceLevel] = useState(
		INITIAL_CONFIDENCE_LEVEL
	);
	const [draftVariants, setDraftVariants] = useState(
		variants.map((variant, index) => {
			const remainingSplit = 100 % variants.length;
			const splitValue = parseInt(100 / variants.length, 10);

			let split;

			if (index === 0 && remainingSplit > 0) {
				split = splitValue + remainingSplit;
			}
			else {
				split = splitValue;
			}

			return {...variant, split};
		})
	);
	const {APIService, imagesPath} = useContext(SegmentsExperimentContext);
	const {experiment} = useContext(StateContext);

	const mountedRef = useRef();

	useEffect(() => {
		mountedRef.current = true;

		return () => {
			mountedRef.current = false;
		};
	});

	const successAnimationPath = `${imagesPath}${SUCCESS_ANIMATION_FILE_NAME}`;

	const [getEstimation] = useDebounceCallback((body) => {
		APIService.getEstimatedTime(body)
			.then(({segmentsExperimentEstimatedDaysDuration}) => {
				if (mountedRef.current) {
					setEstimation({
						days: segmentsExperimentEstimatedDaysDuration,
						loading: false,
					});
				}
			})
			.catch((_error) => {
				if (mountedRef.current) {
					setEstimation({
						error: true,
					});
				}
			});
	}, TIME_ESTIMATION_THROTTLE_TIME_MS);

	useEffect(() => {
		setEstimation({loading: true});

		getEstimation({
			confidenceLevel,
			segmentsExperimentId: experiment.segmentsExperimentId,
			segmentsExperimentRels: JSON.stringify(
				_variantsToSplitVariantsMap(draftVariants)
			),
		});
	}, [
		draftVariants,
		confidenceLevel,
		getEstimation,
		experiment.segmentsExperimentId,
	]);

	const [height, setHeight] = useState(0);

	const measureHeight = useCallback(
		(node) => {
			if (node !== null && !success) {
				setHeight(node.getBoundingClientRect().height);
			}
		},
		[setHeight, success]
	);

	const [selectedTestType, setSelectedTestType] = useState('AB');

	return (
		<ClayModal observer={modalObserver} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{success
					? Liferay.Language.get('test-started-successfully')
					: Liferay.Language.get('review-and-run-test')}
			</ClayModal.Header>

			<ClayModal.Body>
				{success ? (
					<div
						className="text-center"
						style={{maxHeight: height + 'px'}}
					>
						<img
							alt=""
							className="mb-4 mt-3"
							src={successAnimationPath}
							width="250px"
						/>

						<h3>{Liferay.Language.get('test-running-message')}</h3>
					</div>
				) : (
					<div ref={measureHeight}>
						{!Liferay.FeatureFlags['LRAC-15017'] && (
							<h3 className="border-bottom-0 sheet-subtitle text-secondary">
								{Liferay.Language.get('traffic-split')}
							</h3>
						)}

						{Liferay.FeatureFlags['LRAC-15017'] && (
							<>
								<h3 className="sheet-subtitle text-secondary">
									{Liferay.Language.get('test-type')}
								</h3>

								<p className="small">
									{Liferay.Language.get(
										'choose-how-your-experiment-is-going-to-be-measured'
									)}
								</p>

								<label>
									{Liferay.Language.get('standard')}
								</label>

								<p className="small">
									{Liferay.Language.get(
										'use-the-ab-test-methodology-to-determine-which-variant-performs-better-based-on-statistical-significance'
									)}
								</p>

								<ClayRadioGroup
									inline
									onChange={setSelectedTestType}
									value={selectedTestType}
								>
									<ClayRadio
										label={Liferay.Language.get(
											'enable-standard-test-type'
										)}
										value="AB"
									/>
								</ClayRadioGroup>

								<h3 className="border-bottom-0 mt-3 sheet-subtitle text-secondary">
									{Liferay.Language.get('traffic-split')}
								</h3>
							</>
						)}

						<SplitPicker
							disabled={selectedTestType === 'MAB'}
							onChange={(variants) => {
								setDraftVariants(variants);
							}}
							selectedTestType={selectedTestType}
							variants={draftVariants}
						/>

						<h3 className="border-bottom-0 sheet-subtitle text-secondary">
							{Liferay.Language.get('confidence-level')}
						</h3>

						<SliderWithLabel
							disabled={selectedTestType === 'MAB'}
							label={Liferay.Language.get(
								'confidence-level-required'
							)}
							max={MAX_CONFIDENCE_LEVEL}
							min={MIN_CONFIDENCE_LEVEL}
							onValueChange={setConfidenceLevel}
							value={confidenceLevel}
						/>

						{Liferay.FeatureFlags['LRAC-15017'] && (
							<>
								<label>{Liferay.Language.get('Dynamic')}</label>

								<p className="small">
									{Liferay.Language.get(
										'use-the-multiarmed-bandit-method-to-determine-which-variant-performs-better-by-adapting-dynamically'
									)}
								</p>

								<ClayRadioGroup
									inline
									onChange={setSelectedTestType}
									value={selectedTestType}
								>
									<ClayRadio
										label={Liferay.Language.get(
											'enable-dynamic-test-type'
										)}
										value="MAB"
									/>
								</ClayRadioGroup>
							</>
						)}

						<hr />

						<div className="d-flex">
							<div className="w-100">
								<label>
									{Liferay.Language.get(
										'estimated-time-to-declare-winner'
									)}
								</label>

								<p className="small text-secondary">
									{Liferay.Language.get(
										'time-depends-on-confidence-level-and-traffic-to-the-variants'
									)}
								</p>
							</div>

							<p className="mb-0 text-nowrap">
								{estimation.loading && (
									<ClayLoadingIndicator
										className="my-0"
										small
									/>
								)}

								{!estimation.loading &&
								(estimation.days === undefined ||
									estimation.error) ? (
									<span className="small text-secondary">
										{Liferay.Language.get('not-available')}
									</span>
								) : (
									estimation.days &&
									_getDaysMessage(estimation.days)
								)}
							</p>
						</div>
					</div>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					success ? (
						<ClayButton.Group>
							<ClayButton
								displayType="primary"
								onClick={onModalClose}
							>
								{Liferay.Language.get('ok')}
							</ClayButton>
						</ClayButton.Group>
					) : (
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onModalClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<LoadingButton
								disabled={loading}
								loading={loading}
								onClick={_handleRun}
							>
								{Liferay.Language.get('run')}
							</LoadingButton>
						</ClayButton.Group>
					)
				}
			/>
		</ClayModal>
	);

	/**
	 * Creates a splitVariantsMap `{ [segmentsExperimentRelId]: splitValue, ... }`
	 * and bubbles up `onRun` with `confidenceLevel` and `splitVariantsMap`
	 */
	function _handleRun() {
		const splitVariantsMap = _variantsToSplitVariantsMap(draftVariants);

		setLoading(true);

		onRun({
			confidenceLevel: percentageNumberToIndex(confidenceLevel),
			segmentsExperimentType: selectedTestType,
			splitVariantsMap,
		}).then(() => {
			if (mountedRef.current) {
				setLoading(false);
				setSuccess(true);
			}
		});
	}
}

function _variantsToSplitVariantsMap(variants) {
	return variants.reduce((acc, v) => {
		return {
			...acc,
			[v.segmentsExperimentRelId]: percentageNumberToIndex(v.split),
		};
	}, {});
}

function _getDaysMessage(days) {
	if (days === 1) {
		return sub(Liferay.Language.get('x-day'), days);
	}
	else {
		return sub(Liferay.Language.get('x-days'), days);
	}
}

ReviewExperimentModal.propTypes = {
	modalObserver: PropTypes.object.isRequired,
	onModalClose: PropTypes.func.isRequired,
	onRun: PropTypes.func.isRequired,
	variants: PropTypes.arrayOf(SegmentsVariantType),
};

export {ReviewExperimentModal};
