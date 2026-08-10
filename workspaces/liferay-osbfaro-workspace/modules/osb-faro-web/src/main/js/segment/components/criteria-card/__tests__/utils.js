import * as data from 'test/data';
import * as utils from '../utils';
import {
	CustomFunctionOperators,
	isKnown,
	isUnknown,
	PropertyTypes,
	RelationalOperators,
} from 'segment/segment-editor/dynamic/utils/constants';

const {ActivitiesFilterByCount} = CustomFunctionOperators;

const {EQ, LT, NE} = RelationalOperators;

describe('utils', () => {
	describe('getOperatorLabel', () => {
		it.each`
			operatorKey                | type                                | retVal
			${EQ}                      | ${PropertyTypes.AccountText}        | ${Liferay.Language.get('is').toLowerCase()}
			${ActivitiesFilterByCount} | ${PropertyTypes.Behavior}           | ${Liferay.Language.get('has').toLowerCase()}
			${EQ}                      | ${PropertyTypes.SessionGeolocation} | ${Liferay.Language.get('was').toLowerCase()}
			${EQ}                      | ${PropertyTypes.SessionText}        | ${Liferay.Language.get('is').toLowerCase()}
			${EQ}                      | ${PropertyTypes.Text}               | ${Liferay.Language.get('is').toLowerCase()}
			${EQ}                      | ${PropertyTypes.Boolean}            | ${Liferay.Language.get('is').toLowerCase()}
			${LT}                      | ${PropertyTypes.Date}               | ${Liferay.Language.get('is-before').toLowerCase()}
			${LT}                      | ${PropertyTypes.DateTime}           | ${Liferay.Language.get('is-before').toLowerCase()}
			${LT}                      | ${PropertyTypes.SessionDateTime}    | ${Liferay.Language.get('is-before').toLowerCase()}
			${LT}                      | ${PropertyTypes.AccountNumber}      | ${Liferay.Language.get('less-than').toLowerCase()}
			${LT}                      | ${PropertyTypes.Duration}           | ${Liferay.Language.get('less-than').toLowerCase()}
			${LT}                      | ${PropertyTypes.Number}             | ${Liferay.Language.get('less-than').toLowerCase()}
			${LT}                      | ${PropertyTypes.SessionNumber}      | ${Liferay.Language.get('less-than').toLowerCase()}
		`(
			'get $retVal for $type from $operatorKey',
			({operatorKey, retVal, type}) => {
				expect(utils.getOperatorLabel(operatorKey, type)).toBe(retVal);
			}
		);
	});
	describe('maybeFormatToKnownType', () => {
		it.each`
			operatorName | value   | retVal
			${NE}        | ${null} | ${isKnown}
			${EQ}        | ${null} | ${isUnknown}
			${LT}        | ${123}  | ${LT}
		`('formats $value to $retVal', ({operatorName, retVal, value}) => {
			expect(utils.maybeFormatToKnownType(operatorName, value)).toBe(
				retVal
			);
		});
	});

	describe('maybeFormatValue', () => {
		it.each`
			value                  | type                                | retVal
			${'Test'}              | ${PropertyTypes.AccountText}        | ${'"Test"'}
			${'Test'}              | ${PropertyTypes.Behavior}           | ${'"Test"'}
			${'Test'}              | ${PropertyTypes.Interest}           | ${'"Test"'}
			${'Test'}              | ${PropertyTypes.SessionGeolocation} | ${'"Test"'}
			${'Test'}              | ${PropertyTypes.SessionText}        | ${'"Test"'}
			${'Test'}              | ${PropertyTypes.Text}               | ${'"Test"'}
			${'true'}              | ${PropertyTypes.Boolean}            | ${'TRUE'}
			${data.getTimestamp()} | ${PropertyTypes.Date}               | ${'2018-07-10'}
			${data.getTimestamp()} | ${PropertyTypes.DateTime}           | ${'Jul 10, 2018, 11:01 PM'}
			${data.getTimestamp()} | ${PropertyTypes.SessionDateTime}    | ${'Jul 10, 2018, 11:01 PM'}
			${123}                 | ${PropertyTypes.AccountNumber}      | ${123}
			${1000}                | ${PropertyTypes.Duration}           | ${'00:00:01'}
			${123}                 | ${PropertyTypes.Number}             | ${123}
			${123}                 | ${PropertyTypes.SessionNumber}      | ${123}
		`('formats $value to $retVal if $type', ({retVal, type, value}) => {
			expect(utils.maybeFormatValue(value, type)).toBe(retVal);
		});
	});
});
