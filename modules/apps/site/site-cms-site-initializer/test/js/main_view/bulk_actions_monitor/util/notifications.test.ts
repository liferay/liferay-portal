/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getBulkActionTaskFailureMessage} from '../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util/notifications';

describe('getBulkActionTaskFailureMessage', () => {
	it('returns the structure message for a copy blocked by structure scope', () => {
		expect(
			getBulkActionTaskFailureMessage(
				'CopyObjectBulkSelectionAction',
				JSON.stringify([
					{
						id: 'structureNotInDestinationSpace',
						type: 'simpleError',
					},
				])
			)
		).toBe(
			'some-items-could-not-be-copied.-please-ensure-their-structures-exist-in-the-destination-space'
		);
	});

	it('returns null for an unmapped action', () => {
		expect(
			getBulkActionTaskFailureMessage(
				'DeleteObjectBulkSelectionAction',
				JSON.stringify([
					{
						id: 'structureNotInDestinationSpace',
						type: 'simpleError',
					},
				])
			)
		).toBeNull();
	});

	it('returns null when there is no task result', () => {
		expect(
			getBulkActionTaskFailureMessage('MoveObjectBulkSelectionAction', '')
		).toBeNull();
	});

	it('reads the error id from the task result JSON', () => {
		expect(
			getBulkActionTaskFailureMessage(
				'MoveObjectBulkSelectionAction',
				JSON.stringify([
					{
						id: 'structureNotInDestinationSpace',
						type: 'simpleError',
					},
				])
			)
		).toBe(
			'some-items-could-not-be-moved.-please-ensure-their-structures-exist-in-the-destination-space'
		);
	});

	it('returns the first mapped error when several failures are listed', () => {
		expect(
			getBulkActionTaskFailureMessage(
				'MoveObjectBulkSelectionAction',
				JSON.stringify([
					{id: 'someOtherFailure', type: 'simpleError'},
					{
						id: 'structureNotInDestinationSpace',
						type: 'simpleError',
					},
				])
			)
		).toBe(
			'some-items-could-not-be-moved.-please-ensure-their-structures-exist-in-the-destination-space'
		);
	});

	it('returns null when no listed error is mapped', () => {
		expect(
			getBulkActionTaskFailureMessage(
				'MoveObjectBulkSelectionAction',
				JSON.stringify([{id: 'someOtherFailure', type: 'simpleError'}])
			)
		).toBeNull();
	});
});
