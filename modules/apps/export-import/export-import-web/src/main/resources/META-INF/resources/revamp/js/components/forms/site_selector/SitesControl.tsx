/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {useId, useState} from 'react';

import {GroupType, Site} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import SectionTags from '../content_selector/SectionTags';
import SiteSelectorModal from './SiteSelectorModal';

const LABELS = {
	assetLibrary: {
		empty: Liferay.Language.get('no-spaces-are-selected'),
		select: Liferay.Language.get('select-spaces'),
		title: Liferay.Language.get('spaces'),
	},
	site: {
		empty: Liferay.Language.get('no-sites-are-selected'),
		select: Liferay.Language.get('select-sites'),
		title: Liferay.Language.get('sites'),
	},
};

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
	type = 'site',
}: {
	apiURL?: string;
	onChange: (externalReferenceCodes: string[]) => void;
	process?: ExportImportProcess;
	selectedExternalReferenceCodes: string[];
	sites?: Site[];
	totalCount?: number;
	type?: GroupType;
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
		: LABELS[type].empty;

	return (
		<>
			<ClayLayout.ContentRow verticalAlign="center">
				<Text size={6} weight="bold">
					{LABELS[type].title}
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
				{LABELS[type].select}
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
					title={LABELS[type].select}
					type={type}
				/>
			)}
		</>
	);
}
