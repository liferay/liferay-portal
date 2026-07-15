/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EVENT_TYPES} from '../../../../../src/main/resources/META-INF/resources/js/custom/form/eventTypes';
import repeatableDNDReducer from '../../../../../src/main/resources/META-INF/resources/js/custom/form/reducers/repeatableDNDReducer.es';

const PORTLET_NAMESPACE = '_com_liferay_journal_web_portlet_JournalPortlet_';

const QUESTION_PREFIX = `${PORTLET_NAMESPACE}ddm$$Question$question0$0`;

const QUESTION_NAME = `${QUESTION_PREFIX}$$en_US`;

const createAnswerField = (instanceId, repeatedIndex, value) => ({
	fieldName: 'Answer',
	name: `${QUESTION_PREFIX}#Answer$${instanceId}$${repeatedIndex}$$en_US`,
	nestedFields: [
		{
			fieldName: 'AnswerText',
			name: `${QUESTION_PREFIX}#Answer$${instanceId}$${repeatedIndex}#AnswerText$text_${instanceId}$0$$en_US`,
			value,
		},
	],
	repeatable: true,
});

const createNestedTextField = (fieldName, instanceId) => ({
	fieldName,
	name: `${QUESTION_PREFIX}#${fieldName}$${instanceId}$0$$en_US`,
	repeatable: false,
	value: fieldName,
});

const createPages = (fields) => [{rows: [{columns: [{fields}]}]}];

describe('repeatableDNDReducer', () => {
	describe('REPEATABLE_FIELD.CHANGE_ORDER', () => {
		it('renumbers only the reordered instances when the nested repeatable field has sibling fields (LPP-64814)', () => {
			const state = {
				pages: createPages([
					{
						fieldName: 'Question',
						name: QUESTION_NAME,
						nestedFields: [
							createNestedTextField('Title', 'title0'),
							createAnswerField('answer0', 0, 'answer-one'),
							createAnswerField('answer1', 1, 'answer-two'),
							createAnswerField('answer2', 2, 'answer-three'),
							createNestedTextField('Note', 'note0'),
						],
						repeatable: false,
					},
				]),
			};

			const result = repeatableDNDReducer(state, {
				payload: {
					draggedIndex: 1,
					sourceFieldName: `${QUESTION_PREFIX}#Answer$answer1$1$$en_US`,
					sourceNestedFieldIndex: 2,
					targetIndex: 0,
					targetNestedFieldIndex: 1,
				},
				type: EVENT_TYPES.FORM_VIEW.REPEATABLE_FIELD.CHANGE_ORDER,
			});

			const [question] = result.pages[0].rows[0].columns[0].fields;

			expect(question.name).toBe(QUESTION_NAME);

			expect(question.nestedFields.map(({name}) => name)).toEqual([
				`${QUESTION_PREFIX}#Title$title0$0$$en_US`,
				`${QUESTION_PREFIX}#Answer$answer1$0$$en_US`,
				`${QUESTION_PREFIX}#Answer$answer0$1$$en_US`,
				`${QUESTION_PREFIX}#Answer$answer2$2$$en_US`,
				`${QUESTION_PREFIX}#Note$note0$0$$en_US`,
			]);

			const answers = question.nestedFields.filter(
				({fieldName}) => fieldName === 'Answer'
			);

			expect(
				answers.map(({nestedFields}) => nestedFields[0].name)
			).toEqual([
				`${QUESTION_PREFIX}#Answer$answer1$0#AnswerText$text_answer1$0$$en_US`,
				`${QUESTION_PREFIX}#Answer$answer0$1#AnswerText$text_answer0$0$$en_US`,
				`${QUESTION_PREFIX}#Answer$answer2$2#AnswerText$text_answer2$0$$en_US`,
			]);

			expect(
				answers.map(({nestedFields}) => nestedFields[0].value)
			).toEqual(['answer-two', 'answer-one', 'answer-three']);
		});

		it('renumbers top-level repeatable field instances by their new order', () => {
			const createTopLevelField = (instanceId, repeatedIndex, value) => ({
				fieldName: 'Field',
				name: `${PORTLET_NAMESPACE}ddm$$Field$${instanceId}$${repeatedIndex}$$en_US`,
				repeatable: true,
				value,
			});

			const state = {
				pages: createPages([
					createTopLevelField('instance0', 0, 'one'),
					createTopLevelField('instance1', 1, 'two'),
					createTopLevelField('instance2', 2, 'three'),
				]),
			};

			const result = repeatableDNDReducer(state, {
				payload: {
					draggedIndex: 2,
					sourceFieldName: `${PORTLET_NAMESPACE}ddm$$Field$instance2$2$$en_US`,
					targetIndex: 0,
				},
				type: EVENT_TYPES.FORM_VIEW.REPEATABLE_FIELD.CHANGE_ORDER,
			});

			const {fields} = result.pages[0].rows[0].columns[0];

			expect(fields.map(({name}) => name)).toEqual([
				`${PORTLET_NAMESPACE}ddm$$Field$instance2$0$$en_US`,
				`${PORTLET_NAMESPACE}ddm$$Field$instance0$1$$en_US`,
				`${PORTLET_NAMESPACE}ddm$$Field$instance1$2$$en_US`,
			]);

			expect(fields.map(({value}) => value)).toEqual([
				'three',
				'one',
				'two',
			]);
		});
	});
});
