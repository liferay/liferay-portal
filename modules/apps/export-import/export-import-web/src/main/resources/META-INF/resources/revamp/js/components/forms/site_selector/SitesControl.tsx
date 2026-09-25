/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {useId, useState} from 'react';

import {PreviewSite} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import SectionTags from '../content_selector/SectionTags';
import SiteSelectorModal from './SiteSelectorModal';

function toPreviewSitesByExternalReferenceCode(previewSites: PreviewSite[]) {
	const previewSitesByExternalReferenceCode = new Map<string, PreviewSite>();

	for (const previewSite of previewSites) {
		const knownPreviewSite = previewSitesByExternalReferenceCode.get(
			previewSite.externalReferenceCode
		);

		if (previewSite.descriptiveName || !knownPreviewSite) {
			previewSitesByExternalReferenceCode.set(
				previewSite.externalReferenceCode,
				previewSite
			);
		}
	}

	return previewSitesByExternalReferenceCode;
}

function getDescription(selectedCount: number, selectedNames: string[]) {
	if (!selectedCount) {
		return Liferay.Language.get('no-sites-are-selected');
	}

	if (selectedNames.length === selectedCount) {
		return sub(
			Liferay.Language.get('selected-x'),
			selectedNames.join(', ')
		);
	}

	return sub(
		Liferay.Language.get('x-sites-are-selected'),
		String(selectedCount)
	);
}

export default function SitesControl({
	apiURL,
	onChange,
	previewSites,
	process = 'export',
	selectedExternalReferenceCodes,
	totalCount,
}: {
	apiURL?: string;
	onChange: (externalReferenceCodes: string[]) => void;
	previewSites?: PreviewSite[];
	process?: ExportImportProcess;
	selectedExternalReferenceCodes: string[];
	totalCount?: number;
}) {
	const descriptionId = useId();

	const [showModal, setShowModal] = useState(false);

	const [
		knownSitesByExternalReferenceCode,
		setKnownSitesByExternalReferenceCode,
	] = useState(() =>
		toPreviewSitesByExternalReferenceCode(previewSites ?? [])
	);

	const selectedCount = selectedExternalReferenceCodes.length;

	const selectedNames = selectedExternalReferenceCodes
		.map((externalReferenceCode) =>
			knownSitesByExternalReferenceCode.get(externalReferenceCode)
		)
		.filter((previewSite): previewSite is PreviewSite => !!previewSite)
		.map(
			(previewSite) =>
				previewSite.descriptiveName || previewSite.externalReferenceCode
		);

	const description = getDescription(selectedCount, selectedNames);

	return (
		<>
			<ClayLayout.ContentRow className="align-items-center">
				<ClayLayout.ContentCol expand>
					<span className="align-items-center d-inline-flex">
						<span className="font-weight-semi-bold text-dark">
							{Liferay.Language.get('sites')}
						</span>

						<SectionTags additionCount={totalCount} />
					</span>

					<span
						className="d-block small text-secondary text-truncate"
						id={descriptionId}
						title={description}
					>
						{description}
					</span>
				</ClayLayout.ContentCol>

				<ClayLayout.ContentCol expand={false}>
					<ClayButton
						aria-describedby={descriptionId}
						className="font-weight-semi-bold"
						displayType="link"
						onClick={() => setShowModal(true)}
						size="sm"
					>
						{Liferay.Language.get('select-sites')}
					</ClayButton>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{showModal && (
				<SiteSelectorModal
					apiURL={apiURL}
					onClose={() => setShowModal(false)}
					onSubmit={(nextPickedSites) => {
						setKnownSitesByExternalReferenceCode(
							(previousKnownSitesByExternalReferenceCode) =>
								toPreviewSitesByExternalReferenceCode([
									...previousKnownSitesByExternalReferenceCode.values(),
									...nextPickedSites,
								])
						);

						onChange(
							nextPickedSites.map(
								(previewSite) =>
									previewSite.externalReferenceCode
							)
						);
					}}
					previewSites={previewSites}
					process={process}
					selectedExternalReferenceCodes={
						selectedExternalReferenceCodes
					}
				/>
			)}
		</>
	);
}
