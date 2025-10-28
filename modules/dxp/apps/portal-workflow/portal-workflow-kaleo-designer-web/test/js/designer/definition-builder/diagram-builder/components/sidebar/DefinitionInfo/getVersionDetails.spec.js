/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {getVersionDetails} from '../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/DefinitionInfo/getVersionDetails';

const versionCreationDateMock = 'Aug 12, 2024, 10:20AM';

describe('getVersionDetails function', () => {
	it("only returns the version creation date when the creator's name is not passed as a parameter", () => {
		const versionDetails = getVersionDetails('', versionCreationDateMock);

		expect(versionDetails).toBe(versionCreationDateMock);
		expect(versionDetails).not.toBe(`${versionCreationDateMock} by`);
	});

	it('returns the version creation date and creator name when both are passed as a parameter', () => {
		const creatorName = 'John Doe';
		const versionDetails = getVersionDetails(
			creatorName,
			versionCreationDateMock
		);

		expect(versionDetails).toBe(
			`${versionCreationDateMock} by ${creatorName}`
		);
		expect(versionDetails).not.toBe(`${versionCreationDateMock} by`);
		expect(versionDetails).not.toBe(`${versionCreationDateMock}`);
	});
});
