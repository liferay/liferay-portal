/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {LanguagePicker, Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {preventIframeNavigation} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import React, {useState} from 'react';

import ViewportSizeSelector from '../../app/components/ViewportSizeSelector';
import {
	VIEWPORT_SIZES,
	ViewportSize,
} from '../../app/config/constants/viewportSizes';
import {config} from '../../app/config/index';

import './ElementVariationsSimulationModal.scss';

interface Audience {
	label: string;
	value: string;
}

interface Experience {
	audienceEntryERCs: Array<string>;
	label: string;
	segmentsExperienceERC: string;
	segmentsExperienceId: number;
}

interface Props {
	audiences: Array<Audience>;
	defaultLanguageId: string;
	experiences: Array<Experience>;
	languageId: string;
	locales: Array<{id: string; label: string; symbol: string}>;
	onClose: () => void;
	previewURL: string;
	segmentsExperienceERC: string;
}

function getDefaultAudienceEntryERC(
	audiences: Array<Audience>,
	experience: Experience | undefined
) {
	return experience?.audienceEntryERCs[0] ?? audiences[0]?.value ?? '';
}

export default function ElementVariationsSimulationModal({
	audiences,
	defaultLanguageId,
	experiences,
	languageId: initialLanguageId,
	locales,
	onClose: onCloseModal,
	previewURL,
	segmentsExperienceERC: initialSegmentsExperienceERC,
}: Props) {
	const {observer} = useModal({onClose: onCloseModal});

	const audienceId = useId();
	const experienceId = useId();

	const [segmentsExperienceERC, setSegmentsExperienceERC] = useState(
		initialSegmentsExperienceERC
	);

	const experience = experiences.find(
		(currentExperience) =>
			currentExperience.segmentsExperienceERC === segmentsExperienceERC
	);

	const [audienceEntryERC, setAudienceEntryERC] = useState(() =>
		getDefaultAudienceEntryERC(audiences, experience)
	);

	const [languageId, setLanguageId] = useState(initialLanguageId);

	const [viewportSize, setViewportSize] = useState<ViewportSize>(
		VIEWPORT_SIZES.desktop
	);

	const simulationURL = new URL(previewURL, window.location.origin);

	simulationURL.searchParams.set('audienceEntryERCs', audienceEntryERC);
	simulationURL.searchParams.set('languageId', languageId);

	if (experience) {
		simulationURL.searchParams.set(
			'segmentsExperienceId',
			String(experience.segmentsExperienceId)
		);
	}

	const [loading, setLoading] = useState(true);

	const onIframeLoad = (event: React.SyntheticEvent<HTMLIFrameElement>) => {
		preventIframeNavigation(event);

		const iframe = event.currentTarget;

		const iframeDocument = iframe.contentDocument;

		const setReady = () => setLoading(false);

		if (
			!iframeDocument?.querySelector(
				'script[data-element-variations-preview]'
			) ||
			iframeDocument.documentElement.dataset.elementVariationsPreviewReady
		) {
			setReady();

			return;
		}

		iframe.contentWindow?.addEventListener(
			'elementVariationsPreviewReady',
			setReady,
			{once: true}
		);
	};

	const {maxWidth} = config.availableViewportSizes[viewportSize];

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header>
				{Liferay.Language.get('page-simulation')}
			</ClayModal.Header>

			<ClayModal.Body className="d-flex flex-column p-0">
				<div className="align-items-center bg-white d-flex my-1 px-4 py-2">
					<label className="mb-0 mr-2" htmlFor={experienceId}>
						{Liferay.Language.get('experience')}
					</label>

					<Picker
						className="form-control-sm mr-3 w-auto"
						id={experienceId}
						items={experiences}
						onSelectionChange={(selection) => {
							const nextSegmentsExperienceERC = String(selection);

							if (
								nextSegmentsExperienceERC ===
								segmentsExperienceERC
							) {
								return;
							}

							setLoading(true);
							setSegmentsExperienceERC(nextSegmentsExperienceERC);
							setAudienceEntryERC(
								getDefaultAudienceEntryERC(
									audiences,
									experiences.find(
										(currentExperience) =>
											currentExperience.segmentsExperienceERC ===
											nextSegmentsExperienceERC
									)
								)
							);
						}}
						selectedKey={segmentsExperienceERC}
					>
						{(item) => (
							<Option
								key={item.segmentsExperienceERC}
								textValue={item.label}
							>
								{item.label}
							</Option>
						)}
					</Picker>

					<label className="mb-0 mr-2" htmlFor={audienceId}>
						{Liferay.Language.get('audience')}
					</label>

					<Picker
						className="form-control-sm mr-3 w-auto"
						id={audienceId}
						items={audiences}
						onSelectionChange={(selection) => {
							const nextAudienceEntryERC = String(selection);

							if (nextAudienceEntryERC === audienceEntryERC) {
								return;
							}

							setLoading(true);
							setAudienceEntryERC(nextAudienceEntryERC);
						}}
						selectedKey={audienceEntryERC}
					>
						{(item) => (
							<Option key={item.value} textValue={item.label}>
								{item.label}
							</Option>
						)}
					</Picker>

					<div className="mr-3">
						<LanguagePicker
							defaultLocaleId={defaultLanguageId}
							hideTriggerText
							locales={locales}
							messages={{
								default: Liferay.Language.get('default'),
								option: Liferay.Language.get('x-language-x'),
								translated: Liferay.Language.get('translated'),
								translating:
									Liferay.Language.get('translating-x-x'),
								trigger: Liferay.Language.get(
									'select-a-language.-current-language-x'
								),
								untranslated:
									Liferay.Language.get('not-translated'),
							}}
							onSelectedLocaleChange={(id) => {
								const nextLanguageId = String(id);

								if (nextLanguageId === languageId) {
									return;
								}

								setLoading(true);
								setLanguageId(nextLanguageId);
							}}
							selectedLocaleId={languageId}
							small
						/>
					</div>

					<ClayButton
						aria-label={Liferay.Language.get(
							'simulate-in-a-new-tab'
						)}
						className="text-primary"
						displayType="unstyled"
						monospaced
						onClick={() =>
							window.open(simulationURL.toString(), '_blank')
						}
						size="sm"
						title={Liferay.Language.get('simulate-in-a-new-tab')}
					>
						<ClayIcon symbol="shortcut" />
					</ClayButton>

					<div className="ml-auto">
						<ViewportSizeSelector
							onSizeSelected={setViewportSize}
							selectedSize={viewportSize}
						/>
					</div>
				</div>

				<div
					aria-busy={loading}
					className="bg-light border-top d-flex flex-grow-1 justify-content-center position-relative"
				>
					{loading ? (
						<div className="align-items-center d-flex h-100 justify-content-center position-absolute w-100">
							<ClayLoadingIndicator />
						</div>
					) : null}

					<iframe
						className={classNames(
							'border-0 element-variations__simulation-modal-iframe h-100 w-100',
							{
								'element-variations__simulation-modal-iframe--loading':
									loading,
							}
						)}
						onLoad={onIframeLoad}
						src={simulationURL.toString()}
						style={
							viewportSize === VIEWPORT_SIZES.desktop
								? undefined
								: {maxWidth}
						}
						title={Liferay.Language.get('page-simulation')}
					/>
				</div>
			</ClayModal.Body>
		</ClayModal>
	);
}
