/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {
	EConfigInURLBehavior,
	FrontendDataSet,
	IView,
} from '@liferay/frontend-data-set-web';
import React, {useState} from 'react';

import {Group, GroupType} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';

type Field = {expand?: boolean; fieldName: string; label: string};

type GroupProcess = Exclude<ExportImportProcess, 'publish'>;

const SORTS = [
	{
		active: true,
		default: true,
		direction: 'asc' as const,
		key: 'descriptiveName',
		label: Liferay.Language.get('title'),
	},
];

const CHILD_SITES_FIELD: Field = {
	fieldName: 'childSitesCount',
	label: Liferay.Language.get('child-sites'),
};

const EXISTS_IN_INSTANCE_FIELD: Field = {
	fieldName: 'existsInInstance',
	label: Liferay.Language.get('exists-in-instance'),
};

const PATH_FIELD: Field = {
	fieldName: 'path',
	label: Liferay.Language.get('path'),
};

const TITLE_FIELD: Field = {
	expand: true,
	fieldName: 'descriptiveName',
	label: Liferay.Language.get('title'),
};

const FIELDS: Record<GroupType, Record<GroupProcess, Field[]>> = {
	site: {
		export: [TITLE_FIELD, PATH_FIELD, CHILD_SITES_FIELD],
		import: [TITLE_FIELD, PATH_FIELD, EXISTS_IN_INSTANCE_FIELD],
	},
	space: {
		export: [TITLE_FIELD],
		import: [TITLE_FIELD, EXISTS_IN_INSTANCE_FIELD],
	},
};

function getView(process: GroupProcess, type: GroupType): IView {
	return {
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: FIELDS[type][process],
		},
	};
}

function sortByDescriptiveName(groups: Group[]) {
	return [...groups].sort((group1, group2) =>
		(group1.descriptiveName ?? '').localeCompare(
			group2.descriptiveName ?? ''
		)
	);
}

export default function GroupSelectorModal({
	apiURL,
	groups,
	onClose,
	onSubmit,
	process,
	selectedGroups,
	title,
	type,
}: {
	apiURL?: string;
	groups?: Group[];
	onClose: () => void;
	onSubmit: (selectedGroups: Group[]) => void;
	process: GroupProcess;
	selectedGroups: Group[];
	title: string;
	type: GroupType;
}) {
	const {observer, onClose: closeModal} = useModal({onClose});

	const [selectedItems, setSelectedItems] = useState(selectedGroups);

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<FrontendDataSet
					apiURL={apiURL}
					configInURLBehavior={EConfigInURLBehavior.OFF}
					id={`exportImportGroupSelector_${type}_${process}`}
					onItemsPropSearch={(item, query) =>
						(item.descriptiveName ?? '')
							.toLowerCase()
							.includes(query.toLowerCase())
					}
					onSelectedItemsChange={setSelectedItems}
					pagination={{initialDelta: 20}}
					selectedItems={selectedItems}
					selectedItemsKey="externalReferenceCode"
					selectionType="multiple"
					style="fluid"
					views={[getView(process, type)]}
					{...(groups
						? {items: sortByDescriptiveName(groups)}
						: {sorts: SORTS})}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							onClick={() => {
								onSubmit(selectedItems);

								closeModal();
							}}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
