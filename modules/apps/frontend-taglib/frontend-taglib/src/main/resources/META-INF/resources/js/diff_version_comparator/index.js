/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import {ClayInput} from '@clayui/form';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import Diff from './components/Diff';
import Filter from './components/Filter';
import List from './components/List';
import LocaleSelector from './components/LocaleSelector';
import Selector from './components/Selector';
import sub from './utils/sub';

function Comparator({
	availableLocales,
	diffHtmlResults,
	diffVersions,
	languageId,
	nextVersion,
	portletNamespace,
	portletURL,
	previousVersion,
	resourceURL,
	sourceVersion,
	targetVersion,
}) {
	const isMounted = useIsMounted();

	const availableVersions = useMemo(
		() =>
			diffVersions.filter((diffVersion) => {
				const diffVersionNumber = parseInt(diffVersion.version, 10);

				return (
					sourceVersion !== diffVersionNumber &&
					targetVersion !== diffVersionNumber
				);
			}),
		[diffVersions, sourceVersion, targetVersion]
	);

	const selectableVersions = useMemo(
		() => availableVersions.filter((version) => version.inRange),
		[availableVersions]
	);

	const getDiffURL = useCallback(
		(filterTargetVersion) => {
			const diffURL = new URL(resourceURL);

			diffURL.searchParams.append(
				`${portletNamespace}filterSourceVersion`,
				sourceVersion
			);

			diffURL.searchParams.append(
				`${portletNamespace}filterTargetVersion`,
				filterTargetVersion
			);

			return diffURL.toString();
		},
		[portletNamespace, resourceURL, sourceVersion]
	);

	const [diff, setDiff] = useState(diffHtmlResults);
	const [diffURL, setDiffURL] = useState(getDiffURL(targetVersion));
	const [filterQuery, setFilterQuery] = useState('');
	const [selectedLanguageId, setSelectedLanguageId] = useState(languageId);
	const [selectedVersion, setSelectedVersion] = useState(null);
	const [visibleVersions, setVisibleVersions] = useState(selectableVersions);

	const handleFilterChange = useCallback(
		(event) => {
			const query = event.target.value.toLowerCase();

			const visibleVersions = selectableVersions.filter(
				({label, userName}) => {
					return (
						label.toLowerCase().includes(query) ||
						userName.toLowerCase().includes(query)
					);
				}
			);

			setFilterQuery(query);
			setVisibleVersions(visibleVersions);
		},
		[selectableVersions]
	);

	const diffCacheRef = useRef({[diffURL]: diff});
	const formRef = useRef();

	const handleTargetChange = useCallback(
		(event) => {
			const target = event.target.closest('[data-version]');

			const currentTargetVersion = target ? target.dataset.version : null;

			const selectedVersion = selectableVersions.find((version) => {
				return version.version === currentTargetVersion;
			});

			setDiffURL(getDiffURL(currentTargetVersion || targetVersion));
			setSelectedVersion(selectedVersion);
		},
		[getDiffURL, selectableVersions, targetVersion]
	);

	useEffect(() => {
		const cached = diffCacheRef.current[diffURL];

		if (cached) {
			setDiff(cached);
		}
		else {
			fetch(diffURL)
				.then((res) => res.text())
				.then((text) => {
					diffCacheRef.current[diffURL] = text;
				})
				.catch(() => {
					diffCacheRef.current[diffURL] = Liferay.Language.get(
						'an-error-occurred-while-processing-the-requested-resource'
					);
				})
				.finally(() => {
					if (isMounted()) {
						setDiff(diffCacheRef.current[diffURL]);
					}
				});
		}
	}, [diffURL, isMounted]);

	return (
		<form
			action={portletURL}
			className="container-fluid container-fluid-max-xl container-view diff-version-comparator"
			method="post"
			name={`${portletNamespace}diffVersionFm`}
			ref={formRef}
		>
			<ClayInput
				name={`${portletNamespace}sourceVersion`}
				type="hidden"
				value={sourceVersion}
			/>

			<ClayInput
				name={`${portletNamespace}targetVersion`}
				type="hidden"
				value={targetVersion}
			/>

			<ClayCard horizontal>
				<ClayCard.Body>
					<ClayCard.Description displayType="title">
						{Liferay.Language.get(
							'you-are-comparing-these-versions'
						)}
					</ClayCard.Description>

					<ClayCard.Row>
						<div className="col-md-4">
							<div className="float-right">
								<Selector
									label={sub(
										Liferay.Language.get('version-x'),
										[sourceVersion]
									)}
									selectedVersion={previousVersion}
									uniqueVersionLabel={Liferay.Language.get(
										'first-version'
									)}
									urlSelector="sourceURL"
									versions={availableVersions}
								/>
							</div>
						</div>

						<div className="col-md-8 diff-target-selector">
							<Selector
								label={sub(Liferay.Language.get('version-x'), [
									targetVersion,
								])}
								selectedVersion={nextVersion}
								uniqueVersionLabel={Liferay.Language.get(
									'last-version'
								)}
								urlSelector="targetURL"
								versions={availableVersions}
							/>
						</div>
					</ClayCard.Row>

					<div className="divider row"></div>

					<ClayCard.Row>
						<div className="col-md-4">
							{selectableVersions.length >= 5 && (
								<Filter
									onChange={handleFilterChange}
									query={filterQuery}
								/>
							)}

							{availableLocales &&
								availableLocales.length > 1 && (
									<LocaleSelector
										locales={availableLocales}
										onChange={(event) => {
											setSelectedLanguageId(
												event.target.value
											);

											submitForm(formRef.current);
										}}
										portletNamespace={portletNamespace}
										selectedLanguageId={selectedLanguageId}
									/>
								)}

							<List
								onChange={handleTargetChange}
								selected={selectedVersion}
								versions={visibleVersions}
							/>
						</div>

						<div className="col-md-8">
							<Diff
								diff={diff}
								onClose={handleTargetChange}
								version={selectedVersion}
							/>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		</form>
	);
}

export default Comparator;
