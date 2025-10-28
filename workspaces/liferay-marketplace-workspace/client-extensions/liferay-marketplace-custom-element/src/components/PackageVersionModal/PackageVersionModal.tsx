/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayManagementToolbar from '@clayui/management-toolbar';
import ClayModal, {useModal} from '@clayui/modal';
import {useEffect, useMemo, useState} from 'react';

import './PackageVersionModal.scss';
import useListTypeDefinition from '../../hooks/useListTypeDefinition';
import i18n from '../../i18n';
import {useAppContext} from '../../pages/PublisherDashboard/pages/Apps/AppCreationFlow/AppContext/AppManageState';
import {ActionTypes} from '../../pages/PublisherDashboard/pages/Apps/AppCreationFlow/AppContext/actionTypes';
interface PackageVersionModal {
	appProductId: number;
	currentVersions: string[];
	handleClose: () => void;
}

export function PackageVersionModal({
	appProductId,
	currentVersions,
	handleClose,
}: PackageVersionModal) {
	const [, dispatch] = useAppContext();
	const {observer, onClose} = useModal({
		onClose: handleClose,
	});

	const [checkboxVersions, setCheckboxVersions] =
		useState<string[]>(currentVersions);

	const [versionSelected, setVersionSelected] = useState('');

	const {data} = useListTypeDefinition('LIFERAY-VERSIONS');

	const newVersions = useMemo(() => {
		return data?.listTypeEntries
			.sort((a, b) => {
				const aKey = a.key;
				const bKey = b.key;

				const isAQuarterly = /^\d{4}Q\d$/.test(aKey);
				const isBQuarterly = /^\d{4}Q\d$/.test(bKey);

				if (isAQuarterly && isBQuarterly) {
					return bKey.localeCompare(aKey);
				}

				if (!isAQuarterly && !isBQuarterly) {
					const aVersion = parseFloat(a.name);
					const bVersion = parseFloat(b.name);

					return bVersion - aVersion;
				}

				return isAQuarterly ? -1 : 1;
			})
			.map((version) => version.name);
	}, [data?.listTypeEntries]);

	const [versions, setVersions] = useState<string[]>([]);

	const handleConfirmation = (selectedVersion: string) => {
		dispatch({
			payload: {
				versionName: selectedVersion,
			},
			type: ActionTypes.UPLOAD_BUILD_PACKAGE_FILES,
		});
	};

	useEffect(() => {
		const getProductVersions = async () => {
			return setVersions(newVersions as string[]);
		};

		getProductVersions();
	}, [appProductId, newVersions]);

	return (
		<ClayModal
			center
			className="package-version-modal-container"
			observer={observer}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{i18n.translate('select-compatible-versions')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					{i18n.translate(
						'select-the-versions-of-liferay-that-your-app-is-compatible-with'
					)}
				</p>

				<ClayManagementToolbar>
					<ClayManagementToolbar.Search onlySearch>
						<ClayInput.Group>
							<ClayInput.GroupItem>
								<ClayInput
									aria-label="Search"
									className="form-control input-group-inset input-group-inset-after"
									onChange={(event) =>
										setVersionSelected(event.target.value)
									}
									type="text"
									value={versionSelected}
								/>
								<ClayInput.GroupInsetItem after tag="span">
									<ClayButtonWithIcon
										aria-labelledby="search icon"
										displayType="unstyled"
										symbol="search"
										type="submit"
									/>
								</ClayInput.GroupInsetItem>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayManagementToolbar.Search>
				</ClayManagementToolbar>

				<ClayForm className="modal-form">
					<ClayForm.Group>
						{versions
							?.filter((version: string) =>
								version
									?.toLowerCase()
									.match(versionSelected.toLowerCase())
							)
							.map((version, index) => (
								<ClayCheckbox
									checked={checkboxVersions.includes(version)}
									key={index}
									label={version}
									name={`version-${index}`}
									onChange={(event) =>
										setCheckboxVersions(
											(prevCheckboxVersion) => {
												if (
													prevCheckboxVersion.includes(
														version
													)
												) {
													return prevCheckboxVersion.filter(
														(prevVersion) =>
															prevVersion !==
															version
													);
												}

												return [
													...prevCheckboxVersion,
													event.target.value,
												];
											}
										)
									}
									value={version}
								/>
							))}
					</ClayForm.Group>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onClose()}
						>
							{i18n.translate('cancel')}
						</ClayButton>

						<ClayButton
							onClick={() => {
								checkboxVersions.forEach((version) =>
									handleConfirmation(version)
								);

								onClose();
							}}
						>
							{i18n.translate('confirm')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
