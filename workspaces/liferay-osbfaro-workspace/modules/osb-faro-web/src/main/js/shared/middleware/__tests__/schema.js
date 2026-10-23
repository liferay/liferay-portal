import {
	dataSource,
	getDistributionSchema,
	individual,
	individuals,
	segment,
	segments,
} from 'shared/middleware/schema';
import {mockIndividual, mockLiferayDataSource, mockSegment} from 'test/data';
import {normalize} from 'normalizr';

describe('Schema', () => {
	it('should normalize an individual', () => {
		const action = mockIndividual('foo');

		const result = normalize(action, individual);

		expect(result).toMatchObject({
			entities: {
				individuals: {
					foo: {
						data: expect.objectContaining({
							id: 'foo',
							name: 'Foo Bar',
						}),
					},
				},
			},
			result: 'foo',
		});
	});

	it('should normalize an array of individuals', () => {
		const action = [mockIndividual('foo'), mockIndividual('bar')];

		const result = normalize(action, individuals);

		expect(result).toMatchObject({
			entities: {
				individuals: {
					bar: {
						data: expect.objectContaining({id: 'bar'}),
					},
					foo: {
						data: expect.objectContaining({id: 'foo'}),
					},
				},
			},
			result: ['foo', 'bar'],
		});
	});

	it('should normalize a segment', () => {
		const action = mockSegment('foo');

		const result = normalize(action, segment);

		expect(result).toMatchObject({
			entities: {
				segments: {
					foo: {
						data: expect.objectContaining({
							id: 'foo',
						}),
					},
				},
			},
			result: 'foo',
		});
	});

	it('should normalize an array of segments', () => {
		const action = [mockSegment('foo'), mockSegment('bar')];

		const result = normalize(action, segments);

		expect(result).toMatchObject({
			entities: {
				segments: {
					bar: {
						data: expect.objectContaining({id: 'bar'}),
					},
					foo: {
						data: expect.objectContaining({id: 'foo'}),
					},
				},
			},
			result: ['foo', 'bar'],
		});
	});

	it('should normalize a liferay data-source', () => {
		const payload = mockLiferayDataSource(2);

		expect(normalize(payload, dataSource)).toMatchObject({
			entities: {
				dataSources: {
					2: {
						data: payload,
					},
				},
			},
		});
	});

	describe('getDistributionSchema', () => {
		it('should normalize distribution data', () => {
			const payload = [{count: 2, values: ['foo', 'bar']}];

			expect(normalize(payload, getDistributionSchema(3))).toMatchObject({
				entities: {
					distributions: {
						3: {
							data: payload,
						},
					},
				},
			});
		});
	});
});
