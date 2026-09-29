/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {LanguagePicker, Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {useId} from 'frontend-js-components-web';
import React, {useState} from 'react';

import ElementVariationsSimulationModal from './ElementVariationsSimulationModal';

type Props = React.ComponentProps<typeof ElementVariationsSimulationModal>;

function getDefaultAudienceEntryERC(
	audiences: Props['audiences'],
	experience: Props['experiences'][number] | undefined
) {
	return experience?.audienceEntryERCs[0] ?? audiences[0]?.value ?? '';
}

export default function ElementVariationsSimulationNewTabModal({
	audiences,
	defaultLanguageId,
	experiences,
	languageId: initialLanguageId,
	locales,
	onClose: onCloseModal,
	previewURL,
	segmentsExperienceERC: initialSegmentsExperienceERC,
}: Props) {
	const {observer, onClose} = useModal({onClose: onCloseModal});

	const audienceId = useId();
	const experienceId = useId();
	const languagePickerId = useId();

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

	const openSimulateTab = () => {
		const simulationURL = new URL(previewURL, window.location.origin);

		simulationURL.searchParams.set('audienceEntryERCs', audienceEntryERC);
		simulationURL.searchParams.set('languageId', languageId);

		if (experience) {
			simulationURL.searchParams.set(
				'segmentsExperienceId',
				String(experience.segmentsExperienceId)
			);
		}

		window.open(simulationURL.toString(), '_blank');

		onClose();
	};

	return (
		<ClayModal center observer={observer}>
			<ClayModal.Header>
				{Liferay.Language.get('page-simulation')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div className="form-group">
					<label htmlFor={experienceId}>
						{Liferay.Language.get('experience')}
					</label>

					<Picker
						id={experienceId}
						items={experiences}
						onSelectionChange={(selection) => {
							const nextSegmentsExperienceERC = String(selection);

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
				</div>

				<div className="form-group">
					<label htmlFor={audienceId}>
						{Liferay.Language.get('audience')}
					</label>

					<Picker
						id={audienceId}
						items={audiences}
						onSelectionChange={(selection) =>
							setAudienceEntryERC(String(selection))
						}
						selectedKey={audienceEntryERC}
					>
						{(item) => (
							<Option key={item.value} textValue={item.label}>
								{item.label}
							</Option>
						)}
					</Picker>
				</div>

				<div className="form-group mb-0">
					<label htmlFor={languagePickerId}>
						{Liferay.Language.get('language')}
					</label>

					<div>
						<LanguagePicker
							defaultLocaleId={defaultLanguageId}
							id={languagePickerId}
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
							onSelectedLocaleChange={(id) =>
								setLanguageId(String(id))
							}
							selectedLocaleId={languageId}
						/>
					</div>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={openSimulateTab}>
							{Liferay.Language.get('simulate-in-a-new-tab')}

							<ClayIcon className="ml-2" symbol="shortcut" />
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
