/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import toLocalDateTimeFormatted from '../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/util/toLocalDateTimeFormatted';

it('Format date using America Sao Paulo time zone and Brazilian Portuguese format', () => {
	const dateUTC = 'Mon Jul 22 14:36:44 GMT 2024';

	const localDate = toLocalDateTimeFormatted(
		dateUTC,
		'pt-br',
		'America/Sao_Paulo'
	);

	expect(localDate).toStrictEqual('22 de jul. de 2024, 11:36');
});

it('Format date using America Phoenix time zone and American English format', () => {
	const dateUTC = 'Mon Jul 22 14:36:44 GMT 2024';

	const localDate = toLocalDateTimeFormatted(
		dateUTC,
		'en-us',
		'America/Phoenix'
	);

	expect(localDate).toStrictEqual('Jul 22, 2024, 07:36 AM');
});

it('Format date using Central European summer time zone and Spain Spanish format', () => {
	const dateUTC = 'Mon Jul 22 14:36:44 GMT 2024';

	const localDate = toLocalDateTimeFormatted(
		dateUTC,
		'es-ES',
		'Europe/Paris'
	);

	expect(localDate).toStrictEqual('22 jul 2024, 16:36');
});

it('Format date using Asia Shanghai time zone and Chinese Simplified format', () => {
	const dateUTC = 'Mon Jul 22 14:36:44 GMT 2024';

	const localDate = toLocalDateTimeFormatted(
		dateUTC,
		'zh-Hans-CN',
		'Asia/Shanghai'
	);

	expect(localDate).toStrictEqual('2024年7月22日 22:36');
});
