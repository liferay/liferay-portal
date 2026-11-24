import {getCriterionLabel} from '../getCriterionLabel';
import {
	PropertyTypes,
	RelationalOperators,
	SUPPORTED_OPERATORS_MAP,
} from '../constants';
import {Property} from 'shared/util/records';

const {EQ, GT, NE} = RelationalOperators;

const getOperatorLabel = (type, key) =>
	SUPPORTED_OPERATORS_MAP[type].find((operator) => operator.key === key)
		.label;

const makeProperty = (props) =>
	new Property({
		entityName: 'Individual',
		label: 'Country',
		name: 'country',
		propertyKey: 'individual',
		type: PropertyTypes.Text,
		...props,
	});

describe('getCriterionLabel', () => {
	it('should join the entity, the attribute, the operator and the value', () => {
		expect(
			getCriterionLabel(
				{operatorName: EQ, propertyName: 'country', value: 'Brazil'},
				makeProperty()
			)
		).toBe(
			`Individual Country ${getOperatorLabel(
				PropertyTypes.Text,
				EQ
			)} "Brazil"`
		);
	});

	it.each`
		meaning         | operatorName
		${'is unknown'} | ${EQ}
		${'is known'}   | ${NE}
	`(
		'should leave the value out when the operator is "$meaning"',
		({operatorName}) => {
			const label = getCriterionLabel(
				{operatorName, propertyName: 'country', value: null},
				makeProperty()
			);

			expect(label).toContain('Individual Country');
			expect(label).not.toContain('null');
			expect(label).not.toContain('"');
		}
	);

	it.each`
		case                      | type                     | name        | propertyLabel | operatorName | value     | expectedValue
		${'a number as is'}       | ${PropertyTypes.Number}  | ${'age'}    | ${'Age'}      | ${GT}        | ${18}     | ${'18'}
		${'a boolean uppercased'} | ${PropertyTypes.Boolean} | ${'active'} | ${'Active'}   | ${EQ}        | ${'true'} | ${'TRUE'}
	`(
		'should append $case',
		({expectedValue, name, operatorName, propertyLabel, type, value}) => {
			expect(
				getCriterionLabel(
					{operatorName, propertyName: name, value},
					makeProperty({label: propertyLabel, name, type})
				)
			).toBe(
				`Individual ${propertyLabel} ${getOperatorLabel(
					type,
					operatorName
				)} ${expectedValue}`
			);
		}
	);

	it.each`
		type                  | name         | propertyLabel
		${PropertyTypes.Text} | ${'country'} | ${'Country'}
		${PropertyTypes.Date} | ${'birth'}   | ${'Birth'}
	`(
		'should leave the value out of a new $type row that has none yet',
		({name, propertyLabel, type}) => {
			expect(
				getCriterionLabel(
					{operatorName: EQ, propertyName: name, value: ''},
					makeProperty({label: propertyLabel, name, type})
				)
			).toBe(`Individual ${propertyLabel} ${getOperatorLabel(type, EQ)}`);
		}
	);

	it('should stop at the operator for a select text property, whose value is an id', () => {
		expect(
			getCriterionLabel(
				{operatorName: EQ, propertyName: 'accountId', value: '12345'},
				makeProperty({
					label: 'Account',
					name: 'accountId',
					type: PropertyTypes.SelectText,
				})
			)
		).not.toContain('12345');
	});

	it('should stop at the operator when the value is not a plain value', () => {
		expect(
			getCriterionLabel(
				{
					operatorName: EQ,
					propertyName: 'country',
					value: {nested: 'group'},
				},
				makeProperty()
			)
		).toBe(
			`Individual Country ${getOperatorLabel(PropertyTypes.Text, EQ)}`
		);
	});

	it('should fall back to the property name when the property does not exist', () => {
		expect(
			getCriterionLabel(
				{
					operatorName: EQ,
					propertyName: 'removedAttribute',
					value: 'x',
				},
				undefined
			)
		).toBe('removedAttribute');
	});

	it('should return an empty label when there is neither a property nor a property name', () => {
		expect(getCriterionLabel({operatorName: EQ, value: 'x'})).toBe('');
	});
});
