/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox, ClayRadio} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {useId, useState} from 'react';

import PageTreeModal, {
	PageTreeModalConfiguration,
} from '../../../pages/export/components/PageTreeModal';
import {
	PortletDataHandlerSelection,
	isAllLayoutsSelected,
} from '../../../utils/contentSelection';
import SectionTags from './SectionTags';

interface Props {
	additionCount?: number;
	deletionCount?: number;
	label: string;
	onChange: (value: PortletDataHandlerSelection | undefined) => void;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined;
}

function SelectPagesButton({
	onClick,
	privateLayout,
}: {
	onClick: () => void;
	privateLayout?: boolean;
}) {
	return (
		<ClayButton
			aria-label={
				privateLayout === undefined
					? undefined
					: sub(
							Liferay.Language.get('select-x'),
							privateLayout
								? Liferay.Language.get('private-pages')
								: Liferay.Language.get('public-pages')
						)
			}
			className="font-weight-semi-bold"
			displayType="link"
			onClick={onClick}
			size="sm"
		>
			{Liferay.Language.get('select-layouts')}
		</ClayButton>
	);
}

function LayoutVisibilitySelector({
	label,
	onSetMode,
	privateLayout,
}: {
	label: string;
	onSetMode: (mode: boolean) => void;
	privateLayout: boolean;
}) {
	return (
		<div aria-label={label} className="mt-2 pl-4" role="radiogroup">
			<div className="align-items-center d-flex">
				<ClayRadio
					checked={!privateLayout}
					containerProps={{className: 'my-1'}}
					label={Liferay.Language.get('public-pages')}
					name="layoutSetPrivateLayout"
					onChange={() => onSetMode(false)}
					value="false"
				/>
			</div>

			<div className="align-items-center d-flex mb-1">
				<ClayRadio
					checked={privateLayout}
					containerProps={{className: 'my-1'}}
					label={Liferay.Language.get('private-pages')}
					name="layoutSetPrivateLayout"
					onChange={() => onSetMode(true)}
					value="true"
				/>
			</div>
		</div>
	);
}

export default function LayoutSetControl({
	additionCount,
	deletionCount,
	label,
	onChange,
	pageTreeModalConfiguration,
	portletDataHandlerSelection,
}: Props) {
	const {privateLayoutsEnabled, ...modalConfiguration} =
		pageTreeModalConfiguration;

	const checkboxId = useId();

	const [showModal, setShowModal] = useState(false);

	const {layoutIds = [], privateLayout = false} = (
		typeof portletDataHandlerSelection === 'object'
			? portletDataHandlerSelection
			: {}
	) as {layoutIds?: number[]; privateLayout?: boolean};

	const isAll = isAllLayoutsSelected(portletDataHandlerSelection);

	const selected = typeof portletDataHandlerSelection === 'object';

	const openModal = () => setShowModal(true);

	return (
		<div className="p-3">
			<ClayLayout.ContentRow className="align-items-center">
				<ClayLayout.ContentCol className="pr-2" expand={false}>
					<ClayCheckbox
						checked={isAll}
						id={checkboxId}
						indeterminate={!isAll && !!layoutIds.length}
						onChange={() =>
							onChange(isAll ? undefined : {privateLayout})
						}
					/>
				</ClayLayout.ContentCol>

				<ClayLayout.ContentCol expand>
					<div className="align-items-center d-flex justify-content-between">
						<div className="align-items-center d-flex">
							<label
								className="cursor-pointer font-weight-semi-bold mb-0 small"
								htmlFor={checkboxId}
							>
								{label}
							</label>

							<SectionTags
								additionCount={additionCount}
								deletionCount={deletionCount}
							/>
						</div>

						<SelectPagesButton
							onClick={openModal}
							privateLayout={privateLayout}
						/>
					</div>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{privateLayoutsEnabled && selected && (
				<LayoutVisibilitySelector
					label={label}
					onSetMode={(nextPrivateLayout) =>
						onChange({privateLayout: nextPrivateLayout})
					}
					privateLayout={privateLayout}
				/>
			)}

			{showModal && (
				<PageTreeModal
					{...modalConfiguration}
					initialAll={isAll}
					initialSelectedIds={layoutIds.map(String)}
					onClose={() => setShowModal(false)}
					onSubmit={(selectedPortletDataHandlerSelection) => {
						setShowModal(false);

						onChange(
							selectedPortletDataHandlerSelection ?? undefined
						);
					}}
					privateLayout={privateLayout}
				/>
			)}
		</div>
	);
}
