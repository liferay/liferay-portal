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
	HandlerSelection,
	isAllLayoutsSelected,
} from '../../../utils/contentSelection';
import SectionTags from './SectionTags';

interface Props {
	additionCount?: number;
	deletionCount?: number;
	label: string;
	onChange: (value: HandlerSelection | undefined) => void;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	value: HandlerSelection | undefined;
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
	onSelectPages,
	onSetMode,
	privateLayout,
}: {
	label: string;
	onSelectPages: () => void;
	onSetMode: (mode: boolean) => void;
	privateLayout: boolean;
}) {
	return (
		<div aria-label={label} className="pl-4" role="radiogroup">
			<div className="align-items-center d-flex">
				<ClayRadio
					checked={!privateLayout}
					containerProps={{className: 'my-1'}}
					label={Liferay.Language.get('public-pages')}
					name="layoutSetPrivateLayout"
					onChange={() => onSetMode(false)}
					value="false"
				/>

				{!privateLayout && (
					<SelectPagesButton
						onClick={onSelectPages}
						privateLayout={false}
					/>
				)}
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

				{privateLayout && (
					<SelectPagesButton onClick={onSelectPages} privateLayout />
				)}
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
	value,
}: Props) {
	const {privateLayoutsEnabled, ...modalConfiguration} =
		pageTreeModalConfiguration;

	const checkboxId = useId();

	const [showModal, setShowModal] = useState(false);

	const {layoutIds = [], privateLayout = false} = (
		typeof value === 'object' ? value : {}
	) as {layoutIds?: number[]; privateLayout?: boolean};

	const isAll = isAllLayoutsSelected(value);

	const selected = typeof value === 'object';

	const openModal = () => setShowModal(true);

	return (
		<div className="p-3">
			<ClayLayout.ContentRow className="align-items-center mb-2">
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

						{!privateLayoutsEnabled && (
							<SelectPagesButton onClick={openModal} />
						)}
					</div>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{privateLayoutsEnabled && selected && (
				<LayoutVisibilitySelector
					label={label}
					onSelectPages={openModal}
					onSetMode={(next) => onChange({privateLayout: next})}
					privateLayout={privateLayout}
				/>
			)}

			{showModal && (
				<PageTreeModal
					{...modalConfiguration}
					initialAll={isAll}
					initialSelectedIds={layoutIds.map(String)}
					onClose={() => setShowModal(false)}
					onSubmit={(result) => {
						setShowModal(false);

						onChange(result ?? undefined);
					}}
					privateLayout={privateLayout}
				/>
			)}
		</div>
	);
}
