/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayManagementToolbar from '@clayui/management-toolbar';
import ClayModal, {useModal} from '@clayui/modal';
import {useMemo, useState} from 'react';

import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import useListTypeDefinition from '../../../../../hooks/useListTypeDefinition';
import i18n from '../../../../../i18n';
import {getRandomID} from '../../../../../utils/string';
import {LIFERAY_VERSION_PICKLIST} from '../constants';

type NewAppPackageVersionModal = {
	currentVersions: string[];
	handleClose: () => void;
};

export function NewAppPackageVersionModal({
	currentVersions,
	handleClose,
}: NewAppPackageVersionModal) {
	const [
		{
			build: {liferayPackages},
		},
		dispatch,
	] = useNewAppContext();
	const {observer, onClose} = useModal({
		onClose: handleClose,
	});

	const [checkboxVersions, setCheckboxVersions] =
		useState<string[]>(currentVersions);

	const [selectedVersion, setSelectedVersion] = useState('');

	const {data} = useListTypeDefinition(LIFERAY_VERSION_PICKLIST);

	const newVersions = useMemo(() => {
		return (
			data?.listTypeEntries?.map((entry) => entry.name).reverse() || []
		);
	}, [data?.listTypeEntries]);

	return (
		<ClayModal
			center
			className="package-version-modal-container"
			observer={observer}
		>
			<ClayModal.Header>
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
										setSelectedVersion(event.target.value)
									}
									type="text"
									value={selectedVersion}
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
						{newVersions
							?.filter((version) =>
								version
									.toLowerCase()
									.includes(selectedVersion.toLowerCase())
							)
							.map((version, index) => {
								const isChecked =
									checkboxVersions.includes(version);

								const handleCheckboxChange = () => {
									setCheckboxVersions((prevVersions) =>
										isChecked
											? prevVersions.filter(
													(currentVersion) =>
														currentVersion !==
														version
												)
											: [...prevVersions, version]
									);
								};

								return (
									<ClayCheckbox
										checked={isChecked}
										key={index}
										label={version}
										name={`version-${index}`}
										onChange={handleCheckboxChange}
										value={version}
									/>
								);
							})}
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
								dispatch({
									payload: {
										liferayPackages: [
											...liferayPackages,
											{
												file: null,
												id: getRandomID(),
												versions: checkboxVersions,
											},
										],
									},
									type: NewAppTypes.SET_BUILD,
								});

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
