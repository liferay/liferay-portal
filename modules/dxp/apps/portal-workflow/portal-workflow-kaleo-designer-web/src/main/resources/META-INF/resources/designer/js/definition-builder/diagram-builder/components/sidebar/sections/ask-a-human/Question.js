/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import React, {useContext, useMemo} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import {
	formatVariablesForTextarea,
	parseVariablesInput,
} from '../../../../util/parseVariables';
import SidebarPanel from '../../SidebarPanel';

const Question = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const question = useMemo(
		() => formatVariablesForTextarea(selectedItem?.data?.question),
		[selectedItem]
	);

	const onQuestionChange = ({target}) => {
		if (!selectedItem) {
			return;
		}

		setSelectedItem({
			...selectedItem,
			data: {
				...selectedItem.data,
				question: parseVariablesInput(target.value),
			},
		});
	};

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('question')}>
			<ClayForm.Group>
				<label htmlFor="question">
					{Liferay.Language.get('question')}
				</label>

				<ClayInput
					component="textarea"
					id="question"
					onChange={onQuestionChange}
					placeholder='{"options": [{"label_i18n": {"en_US": "Yes"}, "output": {"answer": "yes"}}, {"label_i18n": {"en_US": "No"}, "output": {"answer": "no"}}], "title_i18n": {"en_US": "Do you approve?"}, "type": "select"}'
					type="text"
					value={question}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

export default Question;
