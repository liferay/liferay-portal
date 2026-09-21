/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLabel from '@clayui/label';
import React, {useId, useState} from 'react';

import {PreviewSite} from '../../../types/exportImportPreview';
import SiteSelectorModal from './SiteSelectorModal';

export default function SitesControl({
	apiURL,
	onChange,
	previewSites,
	selectedExternalReferenceCodes,
	showExistsInInstance = false,
	totalCount,
}: {
	apiURL?: string;
	onChange: (externalReferenceCodes: string[]) => void;
	previewSites?: PreviewSite[];
	selectedExternalReferenceCodes: string[];
	showExistsInInstance?: boolean;
	totalCount?: number;
}) {
	const descriptionId = useId();

	const [showModal, setShowModal] = useState(false);

	const [pickedSites, setPickedSites] = useState<PreviewSite[]>(
		previewSites ?? []
	);

	const selectedCount = selectedExternalReferenceCodes.length;

	const selectedNames = [...(previewSites ?? []), ...pickedSites]
		.filter(
			(previewSite, index, sites) =>
				selectedExternalReferenceCodes.includes(
					previewSite.externalReferenceCode
				) &&
				sites.findIndex(
					({externalReferenceCode}) =>
						externalReferenceCode ===
						previewSite.externalReferenceCode
				) === index
		)
		.map(
			(previewSite) =>
				previewSite.descriptiveName || previewSite.externalReferenceCode
		);

	let description = Liferay.Language.get('no-sites-are-selected');

	if (selectedCount) {
		description = selectedNames.length
			? Liferay.Util.sub(
					Liferay.Language.get('selected-x'),
					selectedNames.join(', ')
				)
			: Liferay.Util.sub(
					Liferay.Language.get('x-sites-are-selected'),
					String(selectedCount)
				);
	}

	return (
		<>
			<div className="align-items-center d-flex">
				<span className="font-weight-bold text-6">
					{Liferay.Language.get('sites')}
				</span>

				{totalCount !== undefined && (
					<ClayLabel className="ml-2" displayType="secondary">
						{Liferay.Util.sub(
							Liferay.Language.get('x-items'),
							String(totalCount)
						)}
					</ClayLabel>
				)}
			</div>

			<span className="d-block small text-secondary" id={descriptionId}>
				{description}
			</span>

			<ClayButton
				aria-describedby={descriptionId}
				className="font-weight-semi-bold mt-2 pl-0"
				displayType="link"
				onClick={() => setShowModal(true)}
				size="sm"
			>
				{Liferay.Language.get('select-sites')}
			</ClayButton>

			{showModal && (
				<SiteSelectorModal
					apiURL={apiURL}
					onClose={() => setShowModal(false)}
					onSubmit={(nextPickedSites) => {
						setPickedSites(nextPickedSites);

						onChange(
							nextPickedSites.map(
								(previewSite) =>
									previewSite.externalReferenceCode
							)
						);
					}}
					previewSites={previewSites}
					selectedExternalReferenceCodes={
						selectedExternalReferenceCodes
					}
					showExistsInInstance={showExistsInInstance}
				/>
			)}
		</>
	);
}
