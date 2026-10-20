/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {useId, useState} from 'react';

import {Site} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import SectionTags from '../content_selector/SectionTags';
import SiteSelectorModal from './SiteSelectorModal';

function toEntries(sites: Site[]) {
	return sites.map(
		(site) => [site.externalReferenceCode, site] as [string, Site]
	);
}

export default function SitesControl({
	apiURL,
	onChange,
	process = 'export',
	selectedExternalReferenceCodes,
	sites,
	totalCount,
}: {
	apiURL?: string;
	onChange: (externalReferenceCodes: string[]) => void;
	process?: ExportImportProcess;
	selectedExternalReferenceCodes: string[];
	sites?: Site[];
	totalCount?: number;
}) {
	const descriptionId = useId();

	const [showModal, setShowModal] = useState(false);

	const [
		knownSitesByExternalReferenceCode,
		setKnownSitesByExternalReferenceCode,
	] = useState(() => new Map(toEntries(sites ?? [])));

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
			<ClayLayout.ContentRow verticalAlign="center">
				<Text size={6} weight="bold">
					{Liferay.Language.get('sites')}
				</Text>

				<SectionTags additionCount={totalCount} />
			</ClayLayout.ContentRow>

			<Text as="p" color="secondary" id={descriptionId} size={3} truncate>
				{description}
			</Text>

			<ClayButton
				aria-describedby={descriptionId}
				displayType="primary"
				onClick={() => setShowModal(true)}
				outline
				size="sm"
			>
				{Liferay.Language.get('select-sites')}
			</ClayButton>

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
					process={process}
					selectedSites={selectedSites}
					sites={sites}
				/>
			)}
		</>
	);
}
