import React from 'react';
import {
	ActionTypes,
	AttributesProvider,
	attributesReducer,
	isAttributeInUse,
	useAttributes
} from '../attributes';
import {isBoolean} from 'lodash';
import {render} from '@testing-library/react';

describe('attributes', () => {
	const initialAttributes = {
		attributes: {
			1: {
				dataType: 'BOOLEAN',
				id: '1',
				name: 'booleanName'
			}
		},
		breakdownOrder: ['123'],
		breakdowns: {
			123: {
				attributeId: '1',
				dataType: 'BOOLEAN',
				id: '123',
				type: 'event'
			}
		},
		filterOrder: ['111'],
		filters: {
			111: {
				attributeId: '1',
				dataType: 'BOOLEAN',
				id: '111',
				operator: 'eq',
				value: ['true']
			}
		}
	};

	describe('isAttributeInUse', () => {
		it('should return false if no attributes are in use', () => {
			expect(
				isAttributeInUse(
					'2',
					initialAttributes.breakdowns,
					initialAttributes.filters
				)
			).toBeFalse();
		});

		it('should return true if at least 1 attribute is in use', () => {
			expect(
				isAttributeInUse(
					'1',
					initialAttributes.breakdowns,
					initialAttributes.filters
				)
			).toBeTrue();
		});
	});

	describe('attributesReducer', () => {
		const attribute = {
			dataType: 'STRING',
			id: '0',
			name: 'testName'
		};
		const breakdown = {
			attributeId: '0',
			dataType: 'STRING',
			type: 'event'
		};
		const filter = {
			attributeId: '0',
			dataType: 'STRING',
			operator: 'eq',
			value: ['test']
		};

		it('should AddBreakdown', () => {
			const initialState = {
				attributes: {},
				breakdownOrder: [],
				breakdowns: {},
				filterOrder: [],
				filters: {}
			};
			const attributes = attributesReducer(initialState, {
				payload: {
					attribute,
					breakdown
				},
				type: ActionTypes.AddBreakdown
			});

			const newBreakdown =
				attributes.breakdowns[attributes.breakdownOrder[0]];

			expect(initialState).not.toEqual(attributes);
			expect(attributes.attributes['0']).toEqual(attribute);
			expect(newBreakdown).toHaveProperty(
				'attributeId',
				breakdown.attributeId
			);
			expect(newBreakdown).toHaveProperty('dataType', breakdown.dataType);
			expect(newBreakdown).toHaveProperty('id');
			expect(newBreakdown).toHaveProperty('type', breakdown.type);
		});

		it('should AddFilter', () => {
			const initialState = {
				attributes: {},
				breakdownOrder: [],
				breakdowns: {},
				filterOrder: [],
				filters: {}
			};
			const attributes = attributesReducer(initialState, {
				payload: {
					attribute,
					filter
				},
				type: ActionTypes.AddFilter
			});

			const newFilter = attributes.filters[attributes.filterOrder[0]];

			expect(initialState).not.toEqual(attributes);
			expect(attributes.attributes['0']).toEqual(attribute);
			expect(newFilter).toHaveProperty(
				'attributeId',
				breakdown.attributeId
			);
			expect(newFilter).toHaveProperty('id');
			expect(newFilter).toHaveProperty('operator', filter.operator);
			expect(newFilter).toHaveProperty('value', filter.value);
		});

		it('should EditBreakdown and not delete attribute if attribute is being used by filter', () => {
			const attributes = attributesReducer(initialAttributes, {
				payload: {
					attribute,
					breakdown,
					id: '123'
				},
				type: ActionTypes.EditBreakdown
			});

			const newBreakdown =
				attributes.breakdowns[attributes.breakdownOrder[0]];

			expect(initialAttributes).not.toEqual(attributes);
			expect(attributes.attributes['0']).toEqual(attribute);
			expect(newBreakdown).toHaveProperty(
				'attributeId',
				breakdown.attributeId
			);
			expect(newBreakdown).toHaveProperty('dataType', breakdown.dataType);
			expect(newBreakdown).toHaveProperty('id');
			expect(newBreakdown).toHaveProperty('type', breakdown.type);
			expect(attributes.breakdownOrder.length).toBe(1);
			expect(attributes.attributes['1']).toBeTruthy();
		});

		it('should EditBreakdown', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['123', '234'],
				breakdowns: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						type: 'event'
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						type: 'event'
					}
				},
				filterOrder: ['222'],
				filters: {
					222: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '222',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {
					attribute,
					breakdown,
					id: '123'
				},
				type: ActionTypes.EditBreakdown
			});

			const newBreakdown =
				attributes.breakdowns[attributes.breakdownOrder[0]];

			expect(initialState).not.toEqual(attributes);
			expect(attributes.attributes['0']).toEqual(attribute);
			expect(newBreakdown).toHaveProperty(
				'attributeId',
				breakdown.attributeId
			);
			expect(newBreakdown).toHaveProperty('dataType', breakdown.dataType);
			expect(newBreakdown).toHaveProperty('id');
			expect(newBreakdown).toHaveProperty('type', breakdown.type);
			expect(attributes.breakdownOrder[0]).toEqual('123');
			expect(attributes.breakdownOrder.length).toBe(2);
			expect(attributes.attributes['1']).toBeUndefined();
		});

		it('should EditFilter and not delete attribute if attribute is being used by breakdown', () => {
			const attributes = attributesReducer(initialAttributes, {
				payload: {
					attribute,
					filter,
					id: '111'
				},
				type: ActionTypes.EditFilter
			});

			const newFilter = attributes.filters[attributes.filterOrder[0]];

			expect(initialAttributes).not.toEqual(attributes);
			expect(attributes.attributes['0']).toEqual(attribute);
			expect(newFilter).toHaveProperty(
				'attributeId',
				breakdown.attributeId
			);
			expect(newFilter).toHaveProperty('id');
			expect(newFilter).toHaveProperty('operator', filter.operator);
			expect(newFilter).toHaveProperty('value', filter.value);
			expect(attributes.filterOrder.length).toBe(1);
			expect(attributes.attributes['1']).toBeTruthy();
		});

		it('should EditFilter', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['234'],
				breakdowns: {
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						type: 'event'
					}
				},
				filterOrder: ['123', '234'],
				filters: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						operator: 'eq',
						value: ['true']
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {
					attribute,
					filter,
					id: '123'
				},
				type: ActionTypes.EditFilter
			});

			const newFilter = attributes.filters[attributes.filterOrder[0]];

			expect(initialState).not.toEqual(attributes);
			expect(attributes.attributes['0']).toEqual(attribute);
			expect(newFilter).toHaveProperty(
				'attributeId',
				breakdown.attributeId
			);
			expect(newFilter).toHaveProperty('id');
			expect(newFilter).toHaveProperty('operator', filter.operator);
			expect(newFilter).toHaveProperty('value', filter.value);
			expect(attributes.filterOrder.length).toBe(2);
			expect(attributes.attributes['1']).toBeUndefined();
		});

		it('should DeleteBreakdown', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['123', '234'],
				breakdowns: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						type: 'event'
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						type: 'event'
					}
				},
				filterOrder: ['234'],
				filters: {
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {
					id: '123'
				},
				type: ActionTypes.DeleteBreakdown
			});

			expect(initialState).not.toEqual(attributes);
			expect(attributes.breakdownOrder.length).toBe(1);
			expect(attributes.filterOrder.length).toBe(1);
			expect(attributes.attributes['2']).toBeTruthy();
			expect(attributes.attributes['1']).toBeUndefined();
			expect(attributes.breakdowns['123']).toBeUndefined();
			expect(attributes.breakdowns['234']).toBeTruthy();
			expect(attributes.filters['234']).toBeTruthy();
		});

		it('should DeleteBreakdown', () => {
			const attributes = attributesReducer(initialAttributes, {
				payload: {
					id: '123'
				},
				type: ActionTypes.DeleteBreakdown
			});

			expect(initialAttributes).not.toEqual(attributes);
			expect(attributes.breakdownOrder.length).toBe(0);
			expect(attributes.filterOrder.length).toBe(1);
			expect(attributes.attributes['1']).toBeTruthy();
			expect(attributes.breakdowns['123']).toBeUndefined();
			expect(attributes.filters['111']).toBeTruthy();
		});

		it('should DeleteFilter', () => {
			const attributes = attributesReducer(initialAttributes, {
				payload: {
					id: '111'
				},
				type: ActionTypes.DeleteFilter
			});

			expect(initialAttributes).not.toEqual(attributes);
			expect(attributes.breakdownOrder.length).toBe(1);
			expect(attributes.filterOrder.length).toBe(0);
			expect(attributes.attributes['1']).toBeTruthy();
			expect(attributes.breakdowns['123']).toBeTruthy();
			expect(attributes.filters['111']).toBeUndefined();
		});

		it('should DeleteFilter', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['223'],
				breakdowns: {
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						type: 'event'
					}
				},
				filterOrder: ['123', '234'],
				filters: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						operator: 'eq',
						value: ['true']
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {
					id: '123'
				},
				type: ActionTypes.DeleteFilter
			});

			expect(initialState).not.toEqual(attributes);
			expect(attributes.breakdownOrder.length).toBe(1);
			expect(attributes.filterOrder.length).toBe(1);
			expect(attributes.attributes['2']).toBeTruthy();
			expect(attributes.attributes['1']).toBeUndefined();
			expect(attributes.breakdowns['234']).toBeTruthy();
			expect(attributes.filters['123']).toBeUndefined();
			expect(attributes.filters['234']).toBeTruthy();
		});

		it('should MoveBreakdown', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['123', '234'],
				breakdowns: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						type: 'event'
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						type: 'event'
					}
				},
				filterOrder: ['123', '234'],
				filters: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						operator: 'eq',
						value: ['true']
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {
					from: 1,
					to: 0
				},
				type: ActionTypes.MoveBreakdown
			});

			expect(initialState).not.toEqual(attributes);
			expect(attributes.breakdownOrder[0]).toEqual('234');
			expect(attributes.breakdownOrder[1]).toEqual('123');
			expect(attributes.breakdownOrder.length).toBe(2);
			expect(attributes.filterOrder[0]).toEqual('123');
			expect(attributes.filterOrder[1]).toEqual('234');
			expect(attributes.filterOrder.length).toBe(2);
		});

		it('should MoveFilter', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['123', '234'],
				breakdowns: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						type: 'event'
					},
					234: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						type: 'event'
					}
				},
				filterOrder: ['123', '234'],
				filters: {
					123: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						id: '123',
						operator: 'eq',
						value: ['true']
					},
					223: {
						attributeId: '2',
						dataType: 'DURATION',
						id: '234',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {
					from: 1,
					to: 0
				},
				type: ActionTypes.MoveFilter
			});

			expect(initialState).not.toEqual(attributes);
			expect(attributes.breakdownOrder[0]).toEqual('123');
			expect(attributes.breakdownOrder[1]).toEqual('234');
			expect(attributes.breakdownOrder.length).toBe(2);
			expect(attributes.filterOrder[0]).toEqual('234');
			expect(attributes.filterOrder[1]).toEqual('123');
			expect(attributes.filterOrder.length).toBe(2);
		});

		it('should DeleteAllAttributes', () => {
			const initialState = {
				attributes: {
					1: {
						dataType: 'BOOLEAN',
						id: '1',
						name: 'booleanName'
					},
					2: {
						dataType: 'DURATION',
						id: '2',
						name: 'durationName'
					}
				},
				breakdownOrder: ['1', '2'],
				breakdowns: {
					1: {
						attributeId: '1',
						dataType: 'BOOLEAN',
						type: 'event'
					},
					2: {
						attributeId: '2',
						dataType: 'DURATION',
						type: 'event'
					}
				},
				filterOrder: ['1', '2'],
				filters: {
					1: {
						attributeId: '1',
						operator: 'eq',
						value: ['true']
					},
					2: {
						attributeId: '2',
						operator: 'gt',
						value: [60000]
					}
				}
			};

			const attributes = attributesReducer(initialState, {
				payload: {},
				type: ActionTypes.DeleteAllAttributes
			});

			expect(initialState).not.toEqual(attributes);
			expect(attributes.attributes).toBeEmpty();
			expect(attributes.breakdownOrder).toBeEmpty();
			expect(attributes.breakdowns).toBeEmpty();
			expect(attributes.filterOrder).toBeEmpty();
			expect(attributes.filters).toBeEmpty();
		});
	});

	describe('useAttributes', () => {
		it('should return the provided state and actions', () => {
			const ChildComponent = () => {
				const {
					addBreakdown,
					addFilter,
					attributes,
					breakdownOrder,
					breakdowns,
					changed,
					deleteAllAttributes,
					deleteBreakdown,
					deleteFilter,
					editBreakdown,
					editFilter,
					filterOrder,
					filters,
					moveBreakdown,
					moveFilter
				} = useAttributes();

				if (
					addBreakdown &&
					addFilter &&
					attributes &&
					breakdowns &&
					isBoolean(changed) &&
					deleteAllAttributes &&
					deleteBreakdown &&
					deleteFilter &&
					editBreakdown &&
					editFilter &&
					filters &&
					moveBreakdown &&
					moveFilter &&
					filterOrder &&
					breakdownOrder
				) {
					return <div>{'contains all'}</div>;
				}

				return <div>{'missing some'}</div>;
			};

			const {container} = render(
				<AttributesProvider>
					<ChildComponent />
				</AttributesProvider>
			);

			expect(container).toHaveTextContent('contains all');
		});
	});
});
