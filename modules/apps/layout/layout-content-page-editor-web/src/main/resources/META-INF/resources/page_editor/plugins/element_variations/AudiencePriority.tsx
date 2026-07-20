/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React, {useState} from 'react';

import AudiencesPriorityModal from './AudiencesPriorityModal';
import ElementVariationService from './ElementVariationService';

interface Audience {
	label: string;
	value: string;
}

interface Props {
	audiences: Audience[];
	segmentsExperienceERC: string;
	updateAudiencesPriorityURL: string;
}

export default function AudiencePriority({
	audiences: initialAudiences,
	segmentsExperienceERC,
	updateAudiencesPriorityURL,
}: Props) {
	const [audiences, setAudiences] = useState(initialAudiences);
	const [openModal, setOpenModal] = useState(false);

	return (
		<div className="mb-3 px-3">
			<div className="align-items-center d-flex">
				<span className="font-weight-bold text-3">
					{Liferay.Language.get('audiences-priority')}
				</span>

				{audiences.length ? (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('edit')}
						borderless
						className="ml-1"
						displayType="secondary"
						onClick={() => setOpenModal(true)}
						size="sm"
						symbol="pencil"
					/>
				) : null}
			</div>

			{audiences.length ? (
				<div className="align-items-center d-flex flex-wrap text-secondary">
					{audiences.map((audience, index) => (
						<React.Fragment key={audience.value}>
							{index > 0 ? (
								<ClayIcon
									className="mt-0 mx-1 small"
									symbol="angle-right"
								/>
							) : null}

							<span className="text-3">{audience.label}</span>
						</React.Fragment>
					))}
				</div>
			) : (
				<p className="mb-0 text-3 text-secondary">
					{Liferay.Language.get('no-audiences-available')}
				</p>
			)}

			{openModal ? (
				<AudiencesPriorityModal
					audiences={audiences}
					onClose={() => setOpenModal(false)}
					onSave={(orderedAudiences) =>
						ElementVariationService.updateAudiencesPriority({
							audienceEntryERCs: orderedAudiences.map(
								({value}) => value
							),
							segmentsExperienceERC,
							updateAudiencesPriorityURL,
						}).then(() => setAudiences(orderedAudiences))
					}
				/>
			) : null}
		</div>
	);
}
