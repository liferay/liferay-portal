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

function toEntries(sites: PreviewSite[]) {
	return sites.map(
		(site) => [site.externalReferenceCode, site] as [string, PreviewSite]
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
	] = useState(() => new Map(toEntries(previewSites ?? [])));

	const selectedSites = selectedExternalReferenceCodes.map(
		(externalReferenceCode) =>
			knownSitesByExternalReferenceCode.get(externalReferenceCode) ?? {
				externalReferenceCode,
			}
	);

	const description = selectedSites.length
		? sub(
				Liferay.Language.get('selected-x'),
				selectedSites
					.map(
						({descriptiveName, externalReferenceCode}) =>
							descriptiveName || externalReferenceCode
					)
					.join(', ')
			)
		: Liferay.Language.get('no-sites-are-selected');

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
					onSubmit={(nextSelectedSites) => {
						setKnownSitesByExternalReferenceCode(
							(previousKnownSitesByExternalReferenceCode) =>
								new Map([
									...previousKnownSitesByExternalReferenceCode,
									...toEntries(nextSelectedSites),
								])
						);

						onChange(
							nextSelectedSites.map(
								({externalReferenceCode}) =>
									externalReferenceCode
							)
						);
					}}
					previewSites={previewSites}
					process={process}
					selectedSites={selectedSites}
				/>
			)}
		</>
	);
}
