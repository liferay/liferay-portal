/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox, ClayRadio} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import React, {useId} from 'react';

import {
	LayoutSetSelection,
	PortletDataHandlerSelection,
} from '../../../utils/contentSelection';
import {PageTreeModalConfiguration} from '../../PageTreeModal';
import SectionTags from './SectionTags';

interface Props {
	additionCount?: number;
	deletionCount?: number;
	label: string;
	onChange: (value: PortletDataHandlerSelection | undefined) => void;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined;
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
	const {privateLayoutsAvailable} = pageTreeModalConfiguration;

	const checkboxId = useId();

	const {privateLayout = false} = (
		typeof portletDataHandlerSelection === 'object'
			? portletDataHandlerSelection
			: {}
	) as LayoutSetSelection;

	const selected = typeof portletDataHandlerSelection === 'object';

	return (
		<div className="p-3">
			<ClayLayout.ContentRow className="align-items-center">
				<ClayLayout.ContentCol className="pr-2" expand={false}>
					<ClayCheckbox
						checked={selected}
						id={checkboxId}
						onChange={() =>
							onChange(selected ? undefined : {privateLayout})
						}
					/>
				</ClayLayout.ContentCol>

				<ClayLayout.ContentCol expand>
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
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{privateLayoutsAvailable && selected && (
				<LayoutVisibilitySelector
					label={label}
					onSetMode={(nextPrivateLayout) =>
						onChange({privateLayout: nextPrivateLayout})
					}
					privateLayout={privateLayout}
				/>
			)}
		</div>
	);
}
