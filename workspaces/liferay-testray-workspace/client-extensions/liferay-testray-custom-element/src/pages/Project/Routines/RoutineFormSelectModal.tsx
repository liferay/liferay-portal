/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';
import {useParams} from 'react-router-dom';
import Form from '~/components/Form';
import Modal from '~/components/Modal';
import {FormModalOptions} from '~/hooks/useFormModal';
import i18n from '~/i18n';

import RoutineListView from './RoutineListView';

type RoutineFormSelectModalProps = {
	currentRoutineId: number;
	modal: FormModalOptions;
	selectedRoutineIds?: number[];
};

const RoutineFormSelectModal: React.FC<RoutineFormSelectModalProps> = ({
	currentRoutineId,
	modal: {observer, onClose, onSave, visible},
	selectedRoutineIds,
}) => {
	const {projectId} = useParams();

	const [state, setState] = useState<number[]>(selectedRoutineIds || []);

	return (
		<Modal
			last={
				<Form.Footer
					onClose={onClose}
					onSubmit={() => onSave(state)}
					primaryButtonProps={{
						title: i18n.translate('select-routines'),
					}}
				/>
			}
			observer={observer}
			size="full-screen"
			title={i18n.translate('select-parent-routines')}
			visible={visible}
		>
			<RoutineListView
				listViewProps={{
					initialContext: {selectedRows: selectedRoutineIds},
					managementToolbarProps: {
						applyFilters: false,
						title: i18n.translate('routines'),
					},
					onContextChange: ({selectedRows}) => setState(selectedRows),
				}}
				resource="/routines"
				tableProps={{
					columns: [{key: 'name', value: i18n.translate('name')}],
					rowSelectable: true,
				}}
				variables={{
					filter: `projectId eq '${projectId}' and id ne '${currentRoutineId}'`,
					pageSize: 50,
				}}
			/>
		</Modal>
	);
};

export default RoutineFormSelectModal;
