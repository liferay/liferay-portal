/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useContext, useMemo} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import {
	formatVariablesForTextarea,
	parseVariablesInput,
} from '../../../../util/parseVariables';
import SidebarPanel from '../../SidebarPanel';

const RAGSummary = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const rag = useMemo(
		() => formatVariablesForTextarea(selectedItem?.data?.rag),
		[selectedItem]
	);

	const onRAGChanges =
		() =>
		({target}) => {
			if (!selectedItem) {
				return;
			}

			const text = target.value;
			const parsed = parseVariablesInput(text);

			const updatedItem = {
				...selectedItem,
				data: {
					...selectedItem.data,
					['rag']: parsed,
				},
			};

			setSelectedItem(updatedItem);
		};

	return (
		<SidebarPanel
			panelTitle={Liferay.Language.get('retrieval-augmented-generation')}
		>
			<ClayForm.Group>
				<ClayInput
					component="textarea"
					onChange={onRAGChanges()}
					placeholder='{"contentRetriever": {"key": "liferay", "blueprintExternalReferenceCode": "BLUEPRINT_EXTERNAL_REFERENCE_CODE"}}'
					type="text"
					value={rag}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

RAGSummary.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default RAGSummary;
