/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import setDataRecord from '../../../src/main/resources/META-INF/resources/js/utils/setDataRecord.es';

const field = {
	localizable: true,
	name: 'ddm$$Text123$uid$0$$en_US',
	visible: false,
};

describe('setDataRecord', () => {
	it('blanks the localized value when the field is hidden by a visibility rule', () => {
		const dataRecordValues = {};

		setDataRecord(
			{
				...field,
				localizedValue: {en_US: 'value', es_ES: 'valor'},
				localizedValueEdited: {en_US: true, es_ES: true},
				value: 'valor',
			},
			dataRecordValues,
			'es_ES',
			false
		);

		expect(dataRecordValues['Text123$uid$0']).toEqual({
			en_US: 'value',
			es_ES: '',
		});
	});

	it('keeps the localized value when the field is hidden by the translation filter', () => {
		const dataRecordValues = {};

		setDataRecord(
			{
				...field,
				hiddenByTranslationFilter: true,
				localizedValue: {en_US: 'value', es_ES: 'valor'},
				localizedValueEdited: {en_US: true, es_ES: true},
				value: 'valor',
			},
			dataRecordValues,
			'es_ES',
			false
		);

		expect(dataRecordValues['Text123$uid$0']).toEqual({
			en_US: 'value',
			es_ES: 'valor',
		});
	});
});
