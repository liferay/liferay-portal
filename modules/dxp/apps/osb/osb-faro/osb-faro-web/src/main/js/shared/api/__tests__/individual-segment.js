jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {create, update} from '../individual-segment';

const createArgs = {
	channelId: '123',
	criteriaString: "(name eq 'test test')",
	groupId: '30555',
	individualIds: [1, 2, 3],
	name: 'segment name'
};

const updateArgs = {
	channelId: '123',
	criteriaString: "(name eq 'test test')",
	groupId: '30555',
	id: '23',
	name: 'segment name'
};

const newRequestParams = {
	data: createArgs,
	method: 'POST',
	path: 'contacts/30555/individual_segment'
};

const updateRequestParams = {
	data: updateArgs,
	method: 'PUT',
	path: 'contacts/30555/individual_segment/23'
};

describe('Individual Segment API', () => {
	describe('Create Segment', () => {
		it('should not pass individualIds in the data object to sendRequest if the segmentType is BATCH', () => {
			const segmentType = 'BATCH';

			create({...createArgs, segmentType});

			expect(sendRequest).toHaveBeenCalledWith({
				...newRequestParams,
				data: {
					channelId: '123',
					filter: "(name eq 'test test')",
					includeAnonymousUsers: false,
					name: createArgs.name,
					segmentType
				}
			});
		});
	});

	describe('Update Segment', () => {
		it('should pass filter in data object to sendRequest if the segmentType is BATCH', () => {
			const segmentType = 'BATCH';

			const data = {
				channelId: '123',
				filter: "(name eq 'test test')",
				includeAnonymousUsers: false,
				name: updateArgs.name
			};

			update({...updateArgs, segmentType});

			expect(sendRequest).toHaveBeenCalledWith({
				...updateRequestParams,
				data: {...data, segmentType}
			});
		});
	});
});
